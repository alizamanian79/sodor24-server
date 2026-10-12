package com.app.server.service.impliment.OtpFactory;

import com.app.server.model.NotificationType;
import com.app.server.model.OtpType;
import com.app.server.service.NotificationService;
import com.app.server.service.OtpService;
import com.app.server.service.impliment.UserOtpServiceImpl;
import org.springframework.stereotype.Component;


@Component
public class OtpFactory {

    private UserOtpServiceImpl userOtpService;
  

    public OtpFactory(UserOtpServiceImpl userOtpService) {
        this.userOtpService = userOtpService;

    }

    public OtpService getService(OtpType type) {

        return switch (type) {
            case USER -> userOtpService;
            case SIGNATURE -> null;
        };
    }
}
