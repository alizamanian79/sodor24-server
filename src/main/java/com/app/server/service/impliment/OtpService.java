package com.app.server.service.impliment;

import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Otp;
import com.app.server.model.User;
import com.app.server.repository.OtpRepository;
import com.app.server.repository.UserRepository;
import com.app.server.service.UserService;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.ActivityRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final int    OTP_LENGTH  = 5;
    private static final String OTP_CHARS   = "0123456789";

    private final OtpRepository  otpRepository;
    private final UserRepository userRepository;

    private final WalletRMQProducer walletRMQProducer;


    @Transactional
    public String generateAndSend(String phoneNumber) {
        User user = userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("کاربر با این ایمیل یافت نشد."));


        otpRepository.deleteAllByPhoneNumber(phoneNumber);

        String code = generateCode();
        Otp    otp  = new Otp(code, user);
        otpRepository.save(otp);


        return code;

    }



    @Transactional
    public Object verify(String phoneNumber, String code) {
        Otp otp = otpRepository
                .findActiveOtpByPhoneNumber(phoneNumber, LocalDateTime.now())
                .orElseThrow(() -> new AppNotFoundException("کد OTP معتبر یا فعالی وجود ندارد."));

        if (!otp.getCode().equals(code)) {
            throw new AppNotFoundException("کد OTP اشتباه است.");
        }

        otp.setUsed(true);
        otpRepository.save(otp);


        // main part
        // تأیید کاربر
        User user = otp.getUser();

        ActivityRequestDto activeWallet =ActivityRequestDto.builder()
                .sub(user.getWalletId())
                .value(true)
                .build();
        walletRMQProducer.setActive(activeWallet);

        return true;
    }



    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void cleanExpiredOtps() {
        otpRepository.deleteExpiredOtps(LocalDateTime.now());
    }



    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb    = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(OTP_CHARS.charAt(random.nextInt(OTP_CHARS.length())));
        }
        return sb.toString();
    }

}