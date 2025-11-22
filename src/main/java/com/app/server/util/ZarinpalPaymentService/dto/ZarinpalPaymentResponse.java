package com.app.server.Utils.ZarinpalPaymentService.dto;

public class ZarinpalPaymentResponse {
    private String message;
    private String code;
    private String authority;
    private String gateway;


    public ZarinpalPaymentResponse() {
    }

    public ZarinpalPaymentResponse(String message, String code, String authority, String gateway) {
        this.message = message;
        this.code = code;
        this.authority = authority;
        this.gateway = gateway;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

}
