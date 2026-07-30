package com.app.server.exception;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppForbiddenException extends RuntimeException {

    private String details;
    private String redirect;

    public AppForbiddenException(String message,String details,String redirect) {
        super(message);
        this.details = details;
        this.redirect=redirect;
    }

    public AppForbiddenException(String message) {
        super(message);
    }


}


