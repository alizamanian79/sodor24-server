package com.app.server.service.impliment;

import com.app.server.exception.AppBadRequestException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Otp;
import com.app.server.repository.OtpRepository;
import com.app.server.thread.GenerateOtpCodeThread;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.shaded.com.google.protobuf.OptionOrBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final GenerateOtpCodeThread otpCodeThread;

    public String generate(int number) {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        try {
            otpCodeThread.setCharacterNumber(number==0?6:number);
            Future<String> res = ex.submit(otpCodeThread);
            Otp otp = Otp.builder()
                    .code(res.get())
                    .expiresAt(LocalDateTime.now().plusMinutes(2))
                    .build();
            otpRepository.save(otp);
            return res.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public boolean verifyOtpByCode(String code) {
       Otp otp =otpRepository.findOtpByCode(code).orElseThrow(()->new AppNotFoundException("کد تایید اشتباه میباشد"));
       return true;
    }


    @Scheduled(fixedRate = 120000)
    public void removeExpiredOtps() {
        otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }


}