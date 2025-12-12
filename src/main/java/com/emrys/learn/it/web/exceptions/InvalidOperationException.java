package com.emrys.learn.it.web.exceptions;
public class InvalidOperationException extends RuntimeException {
    private final String errorCode;

    public InvalidOperationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
