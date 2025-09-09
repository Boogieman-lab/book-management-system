package com.example.bookmanagementsystembo.exception;

public record ErrorResponse(String errorCodeMessage, String message, Object payload) {

    public static ErrorResponse of(String errorCodeMessage, String message, Object payload) {
        return new ErrorResponse(errorCodeMessage, message, payload);
    }

    public static ErrorResponse of(String errorCodeMessage, String message) {
        return new ErrorResponse(errorCodeMessage, message, null);
    }
}
