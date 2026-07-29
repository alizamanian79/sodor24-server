package com.app.server.service.impliment.OtpFactory;

import com.app.server.model.NotificationType;
import com.app.server.model.OtpType;
import com.app.server.service.NotificationService;
import com.app.server.service.OtpService;
import org.springframework.stereotype.Component;


@Component
public class OtpFactory {

    private UserOtpServiceImpl userOtpService;
    private SignatureOtpServiceImpl signatureOtpService;

    public OtpFactory(UserOtpServiceImpl userOtpService, SignatureOtpServiceImpl signatureOtpService) {
        this.userOtpService = userOtpService;
        this.signatureOtpService = signatureOtpService;
    }

    public OtpService getService(OtpType type) {

        return switch (type) {
            case USER -> userOtpService;
            case SIGNATURE -> signatureOtpService;
        };
    }
}
