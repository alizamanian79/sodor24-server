package com.app.server.util.zarinpalPaymentService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZarinpalPaymentRequest {
    private String email;
    private String mobile;
    private Long amount;
    private String description;
    private String callback_url;


    public ZarinpalPaymentRequest(String email, String mobile, Long amount, String description, String callback_url) {
        this.email = email;
        this.mobile = mobile;
        this.amount = amount;
        this.description = description;
        this.callback_url = callback_url;
    }

    public ZarinpalPaymentRequest() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCallback_url() {
        return callback_url;
    }

    public void setCallback_url(String callback_url) {
        this.callback_url = callback_url;
    }
}
