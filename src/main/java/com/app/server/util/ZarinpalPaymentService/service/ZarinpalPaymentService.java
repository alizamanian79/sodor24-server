package com.app.server.util.ZarinpalPaymentService.service;


import com.app.server.util.ZarinpalPaymentService.dto.ZarinpalPaymentResponse;

public interface ZarinpalPaymentService {

   ZarinpalPaymentResponse payment(Long amount , String callback ,
                                   String description , String mobile , String email);
   boolean verifyPayment(String authority, Long amount);
}
