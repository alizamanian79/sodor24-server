package com.app.server.service.impliment;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.NotificationType;
import com.app.server.model.Otp;
import com.app.server.model.User;
import com.app.server.repository.OtpRepository;
import com.app.server.repository.UserRepository;
import com.app.server.service.NotificationService;
import com.app.server.service.OtpService;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.ActivityRequestDto;
import com.app.server.util.wallet_service_producer.dto.request.CreateWalletRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserOtpServiceImpl implements OtpService {

    @Value("${application.wallet-service.currency}")
    private String currency;

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final NotificationFactory notificationFactory;
    private final WalletRMQProducer walletRMQProducer;
    private final RandomCodeGenerator randomCodeGenerator;

    private static final int OTP_EXPIRY_MINUTES = 2;
    private static final int OTP_LENGTH = 5;

    private static final String SMS_TEMPLATE =
            "به صدور24 خوش آمدید.\n" +
                    "ثبت نام شما با موفقیت انجام شد.\n" +
                    "کد تأیید شما: %s\n" +
                    "از همراهی شما سپاسگزاریم.";


    // =========================================================
    // GENERATE OTP
    // =========================================================

    @Override
    @Transactional
    public String generateOtp(String phoneNumber) {

        validatePhoneNumber(phoneNumber);

        User user = findValidatableUser(phoneNumber);

        handleExistingOtp(phoneNumber);

        String code = generateRandomCode();

        saveOtp(phoneNumber, code);

        user.setOtp(code);
        user.setValid(false);
        userRepository.save(user);

        sendSmsNotification(phoneNumber, code);

        return code;
    }


    // =========================================================
    // VERIFY OTP
    // =========================================================

    @Override
    @Transactional
    public Object verifyOtp(String receiver, String code, Object data) {

        validatePhoneNumber(receiver);

        if (code == null || code.trim().isEmpty()) {
            throw new AppBadRequestException("کد تأیید نمی‌تواند خالی باشد");
        }

        User user = findUserByPhoneNumber(receiver);

        if (user.isValid()) {
            throw new AppConflicException(
                    "احراز هویت شما قبلاً تأیید شده است"
            );
        }

        Otp otp = findOtp(receiver, code);

        validateOtpExpiration(otp);

        /*
         * ابتدا Wallet ساخته می‌شود.
         * اگر ساخت Wallet شکست بخورد، کاربر verify نمی‌شود.
         */
        String walletId = createWallet(user);

        /*
         * در صورت نیاز اینجا Keycloak نیز فعال/verify می‌شود.
         */
        verifyUserInKeycloak(user);

        completeVerification(user, walletId);

        deleteOtp(otp);

        return true;
    }


    // =========================================================
    // SCHEDULED OTP CLEANUP
    // =========================================================

    @Scheduled(fixedRate = 120000)
    @Transactional
    public void removeExpiredOtps() {

        LocalDateTime now = LocalDateTime.now();

        long deletedCount =
                otpRepository.deleteByExpiresAtBefore(now);

        if (deletedCount > 0) {
            log.info(
                    "Deleted {} expired OTPs",
                    deletedCount
            );
        }
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    private void validatePhoneNumber(String phoneNumber) {

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new AppBadRequestException(
                    "شماره تماس نمی‌تواند خالی باشد"
            );
        }

        /*
         * اگر شماره موبایل ایران است:
         *
         * 09123456789
         *
         * می‌توانی validation دقیق‌تری هم اضافه کنی.
         */
    }


    // =========================================================
    // USER
    // =========================================================

    private User findValidatableUser(String phoneNumber) {

        User user = userRepository
                .findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new AppNotFoundException(
                                "کاربر با این شماره تماس پیدا نشد"
                        )
                );

        if (user.isValid()) {
            throw new AppConflicException(
                    "احراز هویت شما قبلاً تأیید شده است"
            );
        }

        return user;
    }


    private User findUserByPhoneNumber(String phoneNumber) {

        return userRepository
                .findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new AppNotFoundException(
                                "کاربر با این شماره تماس پیدا نشد"
                        )
                );
    }


    // =========================================================
    // EXISTING OTP
    // =========================================================

    private void handleExistingOtp(String phoneNumber) {

        otpRepository
                .findOtpByReceiver(phoneNumber)
                .ifPresent(otp -> {

                    if (isOtpExpired(otp)) {

                        deleteOtp(otp);

                        log.info(
                                "Expired OTP deleted for {}",
                                phoneNumber
                        );

                        return;
                    }

                    String remainingTime =
                            getRemainingTime(otp.getExpiresAt());

                    throw new AppBadRequestException(
                            "کد تأیید قبلاً ارسال شده است. " +
                                    "لطفاً بعد از " +
                                    remainingTime +
                                    " مجدداً اقدام نمایید"
                    );
                });
    }


    // =========================================================
    // OTP CREATION
    // =========================================================

    private String generateRandomCode() {

        return randomCodeGenerator.generate(OTP_LENGTH);
    }


    private void saveOtp(
            String phoneNumber,
            String code
    ) {

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusMinutes(OTP_EXPIRY_MINUTES);

        Otp otp = Otp.builder()
                .code(code)
                .receiver(phoneNumber)
                .expiresAt(expiresAt)
                .build();

        otpRepository.save(otp);
    }


    // =========================================================
    // FIND OTP
    // =========================================================

    private Otp findOtp(
            String receiver,
            String code
    ) {

        return otpRepository
                .findOtpByReceiverAndCode(receiver, code)
                .orElseThrow(() ->
                        new AppBadRequestException(
                                "کد تأیید اشتباه می‌باشد"
                        )
                );
    }


    // =========================================================
    // OTP EXPIRATION
    // =========================================================

    private boolean isOtpExpired(Otp otp) {

        return otp.getExpiresAt() == null ||
                !otp.getExpiresAt().isAfter(LocalDateTime.now());
    }


    private void validateOtpExpiration(Otp otp) {

        if (isOtpExpired(otp)) {

            deleteOtp(otp);

            throw new AppBadRequestException(
                    "کد تأیید منقضی شده است. لطفاً کد جدید دریافت کنید"
            );
        }
    }


    // =========================================================
    // SMS
    // =========================================================

    private void sendSmsNotification(
            String phoneNumber,
            String code
    ) {

        NotificationService smsService =
                notificationFactory.getService(
                        NotificationType.SMS
                );

        String message =
                String.format(
                        SMS_TEMPLATE,
                        code
                );

        try {

            smsService.sendNotification(
                    phoneNumber,
                    message
            );

        } catch (Exception e) {

            log.error(
                    "Failed to send SMS to {}",
                    phoneNumber,
                    e
            );

            /*
             * مهم:
             *
             * اگر SMS ارسال نشد، بهتر است OTP معتبر
             * باقی نماند.
             *
             * چون متد @Transactional است،
             * exception باعث rollback می‌شود.
             */
            throw new AppBadRequestException(
                    "ارسال کد تأیید با خطا مواجه شد"
            );
        }
    }


    // =========================================================
    // KEYCLOAK
    // =========================================================

    private void verifyUserInKeycloak(User user) {

        /*
         * این قسمت را بر اساس معماری Keycloak پروژه‌ات
         * پیاده‌سازی کن.
         *
         * مثلاً:
         *
         * keycloakService.verifyUser(user.getSub());
         *
         * یا:
         *
         * keycloakService.enableUser(user.getSub());
         */

        log.info(
                "Keycloak verification requested for user: {}",
                user.getId()
        );
    }


    // =========================================================
    // WALLET
    // =========================================================

    private String createWallet(User user) {

        /*
         * sub باید شناسه واقعی کاربر باشد.
         *
         * اگر User دارای sub از Keycloak است:
         *
         * user.getSub()
         *
         * استفاده کن.
         */

        String sub = user.getSub();

        if (sub == null || sub.isBlank()) {
            throw new AppBadRequestException(
                    "شناسه کاربر برای ساخت کیف پول وجود ندارد"
            );
        }

        CreateWalletRequestDto request =
                CreateWalletRequestDto.builder()
                        .sub(sub)
                        .balance(BigDecimal.ZERO)
                        .currency(currency)
                        .build();

        WalletResponseDto response =
                walletRMQProducer.createWallet(request);

        if (response == null || response.getData() == null) {

            throw new AppBadRequestException(
                    "ایجاد کیف پول با خطا مواجه شد"
            );
        }

        Map<String, Object> data =
                (Map<String, Object>) response.getData();

        Object walletSub = data.get("sub");

        if (walletSub == null) {

            throw new AppBadRequestException(
                    "شناسه کیف پول از سرویس Wallet دریافت نشد"
            );
        }

        String walletId = walletSub.toString();

        ActivityRequestDto activityRequest =
                ActivityRequestDto.builder()
                        .sub(walletId)
                        .value(true)
                        .build();

        walletRMQProducer.setActive(
                activityRequest
        );

        return walletId;
    }


    // =========================================================
    // COMPLETE VERIFICATION
    // =========================================================

    private void completeVerification(
            User user,
            String walletId
    ) {

        user.setValid(true);
        user.setOtp(null);
        user.setWalletId(walletId);

        userRepository.save(user);
    }


    // =========================================================
    // DELETE OTP
    // =========================================================

    private void deleteOtp(Otp otp) {

        otpRepository.delete(otp);
    }


    // =========================================================
    // REMAINING TIME
    // =========================================================

    private String getRemainingTime(
            LocalDateTime expiresAt
    ) {

        Duration remaining =
                Duration.between(
                        LocalDateTime.now(),
                        expiresAt
                );

        if (remaining.isNegative() ||
                remaining.isZero()) {

            return "منقضی شده";
        }

        long minutes =
                remaining.toMinutes();

        long seconds =
                remaining
                        .minusMinutes(minutes)
                        .getSeconds();

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }
}