package com.app.server.service;

import com.app.server.dto.response.Sodor24ResponseDto;

public interface NotificationService {

    void sendNotification(String receiver, String message);

}