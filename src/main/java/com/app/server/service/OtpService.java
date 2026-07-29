package com.app.server.service;



public interface OtpService {
     String generateOtp(String receiver);
     Object verifyOtp(String receiver,String code,Object data);
}
