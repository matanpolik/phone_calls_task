package com.example.phone_calls_task_bigid.exception;

public class BlockedPhoneNumberException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Received a call from a blocked number.";
    public BlockedPhoneNumberException() {
        super(DEFAULT_MESSAGE);
    }
}