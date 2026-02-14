package com.app.server.util.zarinpalPaymentService.controller;

import com.app.server.util.zarinpalPaymentService.service.ZarinpalPaymentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CallbackController {

    @Autowired
    private ZarinpalPaymentService zarinpalPaymentService;

    @GetMapping("/api/v1/callback/zarinpalpayment/{order_id}/{amount}")
    public void verifyCallback(
            @PathVariable String order_id,
            @PathVariable Long amount,
            @RequestParam(name = "Authority") String authority,
            @RequestParam(name = "Status") String status,
            HttpServletResponse response
    ) throws IOException {
        if ("OK".equals(status) && authority != null) {
            Boolean res = zarinpalPaymentService.verifyPayment(authority, amount);
            if (res) {
                // Transaction managed

                response.sendRedirect("http://localhost:3000/payment-success?order_id=" + order_id);
            } else {
                response.sendRedirect("http://localhost:3000/payment-failed?order_id=" + order_id);
            }
        } else {
            response.sendRedirect("http://localhost:3000/payment-failed?order_id=" + order_id);
        }
    }

}
