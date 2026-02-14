package com.app.server.exception;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppBadRequestException extends RuntimeException {

    public String details;


    public AppBadRequestException(String message) {
        super(message);
    }
    public AppBadRequestException(String message,String details) {
        super(message);
        this.details = details;
    }


}
