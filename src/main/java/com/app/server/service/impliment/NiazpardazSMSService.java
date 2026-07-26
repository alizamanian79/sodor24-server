package com.app.server.service.impliment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.niazpardaz.sms.client.*;
import com.niazpardaz.sms.models.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class NiazpardazSMSService {

    @Value("${niazpardaz.sms.api-key}")
    private String apiKey;

    @Value("${niazpardaz.sender.number}")
    private String sender;


    public String sendSms(String phoneNumber , String message){
        NiazpardazSmsClient client = NiazpardazSmsClientBuilder
                .create(apiKey)
                .build();

        SendBatchSmsResult result = client.send(sender, phoneNumber, message);

        if (result.isSuccessful()) {
//            System.out.println("BatchSmsId: " + result.getBatchSmsId());
            System.out.println(message);
            Long res =(Long) result.getBatchSmsId();
            return res.toString();
        } else {
            System.out.println("Error: " + result.getResultCodeEnum().getDescription());
            return result.getResultCodeEnum().getDescription().toString();
        }
    }


}
