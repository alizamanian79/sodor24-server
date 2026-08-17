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
import com.app.server.service.impliment.NotificationFactory;
import com.app.server.service.impliment.RandomCodeGenerator;
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
import java.util.List;
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
    private static final String SMS_TEMPLATE = "به صدور24 خوش آمدید.\n" +
            "ثبت نام شما با موفقیت انجام شد.\n" +
            "کد تأیید شما: %s\n" +
            "از همراهی شما سپاسگزاریم.";

    @Override
    @Transactional
    public String generateOtp(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        checkExistingOtp(phoneNumber);

        User user = findValidatableUser(phoneNumber);
        String code = generateRandomCode();

        saveOtpAndUpdateUser(phoneNumber, code, user);
        sendSmsNotification(user.getPhoneNumber(), code);

        return code;
    }

    @Override
    @Transactional
    public Object verifyOtp(String receiver, String code, Object data) {
        User user = findUserByPhoneNumber(receiver);
        Otp otp = findOtpByCode(code);

        validateOtpOwner(otp, user);

        String walletId = createWallet();
        completeVerification(user, walletId);
        deleteOtp(otp);

        return true;
    }

    @Scheduled(fixedRate = 120000)
    @Transactional
    public void removeExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        long deletedCount = otpRepository.deleteByExpiresAtBefore(now);
        if (deletedCount > 0) {
            log.info("Deleted {} expired OTPs", deletedCount);
        }
    }

    // Private helper methods
    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new AppBadRequestException("شماره تماس نمی‌تواند خالی باشد");
        }
    }

    private void checkExistingOtp(String phoneNumber) {
        otpRepository.findOtpByReceiver(phoneNumber).ifPresent(otp -> {
            String remainingTime = getRemainingTime(otp.getExpiresAt());
            throw new AppBadRequestException(
                    String.format("کد تایید قبلاً ارسال شده است. لطفاً بعد از %s دقیقه مجدداً اقدام نمایید", remainingTime)
            );
        });
    }

    private User findValidatableUser(String phoneNumber) {
        User user = userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppNotFoundException("کاربر با این شماره تماس پیدا نشد"));

        if (user.isValid()) {
            throw new AppConflicException("احراز هویت شما قبلاً تایید شده است");
        }
        return user;
    }

    private User findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppNotFoundException("کاربر با این شماره تماس پیدا نشد"));
    }

    private Otp findOtpByCode(String code) {
        return otpRepository.findOtpByCode(code)
                .orElseThrow(() -> new AppNotFoundException("کد شما اشتباه می‌باشد"));
    }

    private String generateRandomCode() {
        return randomCodeGenerator.generate(OTP_LENGTH);
    }

    private void saveOtpAndUpdateUser(String phoneNumber, String code, User user) {
        Otp otp = Otp.builder()
                .code(code)
                .receiver(phoneNumber)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();
        otpRepository.save(otp);

        user.setOtp(code);
        user.setValid(false);
        userRepository.save(user);
    }

    private void sendSmsNotification(String phoneNumber, String code) {
        try {
            NotificationService smsService = notificationFactory.getService(NotificationType.SMS);
            String message = String.format(SMS_TEMPLATE, code);
            smsService.sendNotification(phoneNumber, message);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}", phoneNumber, e);
        }
    }

    private void verifyUserinKeycloak(){

    }


    private void validateOtpOwner(Otp otp, User user) {
        if (!otp.getReceiver().equals(user.getPhoneNumber())) {
            deleteOtp(otp);
            throw new AppBadRequestException("کد تأیید برای این کاربر معتبر نمی‌باشد");
        }
    }

    private String createWallet() {
        CreateWalletRequestDto request = CreateWalletRequestDto.builder()
                .sub("")
                .balance(BigDecimal.ZERO)
                .currency(currency)
                .build();

        WalletResponseDto response = walletRMQProducer.createWallet(request);
        Map<String, Object> data = (Map<String, Object>) response.getData();
        String walletId = data.get("sub").toString();

        ActivityRequestDto activityRequest = ActivityRequestDto.builder()
                .sub(walletId)
                .value(true)
                .build();
        walletRMQProducer.setActive(activityRequest);

        return walletId;
    }

    private void completeVerification(User user, String walletId) {
        user.setValid(true);
        user.setOtp(null);
        user.setWalletId(walletId);
        userRepository.save(user);
    }

    private void deleteOtp(Otp otp) {
        otpRepository.delete(otp);
    }

    private String getRemainingTime(LocalDateTime expiredTime) {
        Duration remaining = Duration.between(LocalDateTime.now(), expiredTime);
        if (remaining.isNegative()) {
            return "منقضی شده";
        }
        long minutes = remaining.toMinutes();
        long seconds = remaining.minusMinutes(minutes).getSeconds();
        return String.format("%02d:%02d", minutes, seconds);
    }
}