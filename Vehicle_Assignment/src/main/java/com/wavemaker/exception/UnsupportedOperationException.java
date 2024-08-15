package com.wavemaker.exception;

public class UnsupportedOperationException extends RuntimeException{
    private String errorMessage;
    private int errorCode;
    public UnsupportedOperationException(String errorMessage, int errorCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
