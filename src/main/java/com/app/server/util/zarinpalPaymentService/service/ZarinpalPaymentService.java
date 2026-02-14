package com.app.server.util.zarinpalPaymentService.service;

import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentRequest;
import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentResponse;

public interface ZarinpalPaymentService {

   ZarinpalPaymentResponse payment(ZarinpalPaymentRequest request);
   boolean verifyPayment(String authority, Long amount);
}
