package com.app.server.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppInternalException extends RuntimeException {

    private String details;

    public AppInternalException(String message,String details) {
        super(message);
        this.details = details;
    }

    public AppInternalException(String message) {
        super(message);
    }


}