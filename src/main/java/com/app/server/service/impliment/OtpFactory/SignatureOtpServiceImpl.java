package com.app.server.service.impliment.OtpFactory;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignatureOtpServiceImpl implements OtpService {

    @Override
    public String generateOtp(String receiver) {
        return "";
    }

    @Override
    public Object verifyOtp(String receiver, String code, Object data) {
        return null;
    }
}
