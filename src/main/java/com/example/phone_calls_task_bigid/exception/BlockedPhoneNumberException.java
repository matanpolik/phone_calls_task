package com.example.phone_calls_task_bigid.exception;

public class BlockedPhoneNumberException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Blocked number exception.";
    public BlockedPhoneNumberException() {
        super(DEFAULT_MESSAGE);
    }
}