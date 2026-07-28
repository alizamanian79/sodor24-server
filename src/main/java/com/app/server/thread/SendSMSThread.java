//package com.app.server.thread;
//
//import com.app.server.model.NotificationType;
//import com.app.server.service.NotificationService;
//import com.app.server.service.impliment.NiazpardazSMSService;
//import com.app.server.service.impliment.NotificationFactory;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class SendSMSThread implements Runnable {
//
//    private  NotificationFactory notificationFactory;
//    private final String receiver;
//    private final String message;
//
//    public SendSMSThread(
//            NotificationFactory notificationFactory,
//            String receiver,
//            String message) {
//
//        this.notificationFactory = notificationFactory;
//        this.receiver = receiver;
//        this.message = message;
//    }
//
//    @Override
//    public void run() {
//        NotificationService service =
//                notificationFactory.getService(NotificationType.SMS);
//
//        service.sendNotification(receiver, message);
//    }
//}