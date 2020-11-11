    package com.veosaf.service.exception;

public class CustomBusinessException extends RuntimeException {

    public CustomBusinessException(String message) {
        super(message);
    }
    public CustomBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
