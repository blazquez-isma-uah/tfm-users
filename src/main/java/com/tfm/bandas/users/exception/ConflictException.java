package com.tfm.bandas.users.exception;

public class ConflictException extends RuntimeException {

    private final String errorCode; // puede ser null si no aplica un código concreto

    public ConflictException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}