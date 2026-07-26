package com.app.server.service.impliment;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements NotificationService {

    @Override
    public void sendNotification(String receiver, String message) {

    }
}