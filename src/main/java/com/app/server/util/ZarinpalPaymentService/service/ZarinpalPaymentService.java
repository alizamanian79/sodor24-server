package com.app.server.util.ZarinpalPaymentService.service;

import com.app.server.util.ZarinpalPaymentService.dto.ZarinpalPaymentRequest;
import com.app.server.util.ZarinpalPaymentService.dto.ZarinpalPaymentResponse;

public interface ZarinpalPaymentService {

   ZarinpalPaymentResponse payment(ZarinpalPaymentRequest request);
   boolean verifyPayment(String authority, Long amount);
}
