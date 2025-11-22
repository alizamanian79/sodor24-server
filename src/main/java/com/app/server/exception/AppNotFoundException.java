package com.app.server.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppNotFoundException extends RuntimeException {
  private String details;
    public AppNotFoundException(String message) {
        super(message);
    }

  public AppNotFoundException(String message,String details) {
    super(message);
    this.details = details;
  }

}
