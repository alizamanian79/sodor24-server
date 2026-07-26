package com.app.server.service.impliment;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import com.app.server.service.NotificationService;
import com.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SMSServiceImpl implements NotificationService {

    @Value("${app.env}")
    private String env;

    private final UserRepository userRepository;
    private final NiazpardazSMSService niazpardazSMSService;

    @Override
    public void sendNotification(String receiver, String message) {
       User user = userRepository.findUserByPhoneNumber(receiver).orElseThrow(()->new AppNotFoundException("تماس پیدا نشد"));
        if (!env.equals("dev")){
            niazpardazSMSService.sendSms(user.getPhoneNumber(),message) ;
        }
        System.out.println(message);
    }
}
