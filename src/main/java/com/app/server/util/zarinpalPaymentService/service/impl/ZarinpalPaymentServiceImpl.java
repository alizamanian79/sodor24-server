package com.app.server.util.zarinpalPaymentService.service.impl;


import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentRequest;
import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentResponse;
import com.app.server.util.zarinpalPaymentService.service.ZarinpalPaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZarinpalPaymentServiceImpl implements ZarinpalPaymentService {
    RestTemplate restTemplate = new RestTemplate();

    // Add this config to application.yml
    // # ==================== Zarinpal config ==================== #
    // zarinpal.server_host=${ZARINPAL_SERVER_HOST:https://sandbox.zarinpal.com}
    // zarinpal.merchant_Id=${ZARINPAL_MERCHANT_ID:7007f58a-3ec5-4ea6-a414-5849591ba0a7}


    // To Generate Link for payment
    @Value("${zarinpal.merchant_Id}")
    private String merchantPaymentId;

    @Value("${zarinpal.server_host}")
    private String paymentServerHost;

    String merchantId = "";
    String requestUrl = "/pg/v4/payment/request.json";
    String gatewayLink = "/pg/StartPay";
    String verifyUrl = "/pg/v4/payment/verify.json";
    String currency = "IRT"; // IRT is Toman and IRR is Rial

    @PostConstruct
    public void init() {
       this.merchantId = merchantPaymentId;
       this.requestUrl=paymentServerHost+requestUrl;
       this.gatewayLink=paymentServerHost+gatewayLink;
       this.verifyUrl=paymentServerHost+verifyUrl;
    }



    @Override
    public ZarinpalPaymentResponse payment(ZarinpalPaymentRequest req) {

        ZarinpalPaymentResponse res = new ZarinpalPaymentResponse();

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("merchant_id", merchantId);
            body.put("amount", req.getAmount());
            body.put("currency",currency);
            body.put("callback_url", req.getCallback_url());
            body.put("description", req.getDescription());

            Map<String, String> metadata = new HashMap<>();
            metadata.put("mobile", req.getMobile());
            metadata.put("email", req.getEmail());
//            metadata.put("order_id", "1235");
            body.put("metadata", metadata);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, entity, String.class);



            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(response.getBody(), Map.class);

            Object dataObj = responseMap.get("data");

            if (dataObj instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) dataObj;

                Object codeObj = dataMap.get("code");
                Object authorityObj = dataMap.get("authority");
                System.out.println();

                if (codeObj != null && authorityObj != null) {
                    res.setMessage("لینک پرداخت با موفقیت ساخته شد");
                    res.setCode(codeObj.toString());
                    res.setAuthority(authorityObj.toString());
                    res.setGateway(gatewayLink + "/" + authorityObj.toString());
                } else {
                    res.setMessage("اطلاعات ناقص از درگاه پرداخت دریافت شد.");
                    res.setCode("500");
                    res.setAuthority(null);
                    res.setGateway(null);
                }

            } else {
                res.setMessage("خطا در ساختار پاسخ دریافتی از زرین‌پال.");
                res.setCode("500");
                res.setAuthority(null);
                res.setGateway(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setMessage("خطا در ارتباط با درگاه پرداخت.");
            res.setCode("500");
            res.setAuthority(null);
            res.setGateway(null);
        }

        return res;
    }


    @Override
    public boolean verifyPayment(String authority, Long amount) {
        try {

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("merchant_id", merchantId);
            requestBody.put("amount", amount);
            requestBody.put("authority", authority);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(verifyUrl, requestEntity, String.class);



            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(response.getBody(), Map.class);

            if (responseMap.get("data") instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");

                Object codeObj = dataMap.get("code");

                if (codeObj != null && codeObj.toString().equals("100") || codeObj.toString().equals("101")) {

                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }





}
