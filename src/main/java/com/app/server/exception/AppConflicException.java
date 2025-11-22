package com.app.server.exception;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppConflicException extends RuntimeException {

    private String details;

    public AppConflicException(String message,String details) {
        super(message);
        this.details = details;
    }

    public AppConflicException(String message) {
        super(message);
    }



}
