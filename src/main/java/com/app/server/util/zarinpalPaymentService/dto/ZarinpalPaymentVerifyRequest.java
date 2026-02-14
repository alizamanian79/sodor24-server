package com.app.server.util.zarinpalPaymentService.dto;

public class ZarinpalPaymentVerifyRequest {
    private Long amount;
    private String authority;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
