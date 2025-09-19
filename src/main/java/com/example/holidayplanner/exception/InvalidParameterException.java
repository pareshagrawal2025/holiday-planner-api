package com.example.holidayplanner.exception;

// Custom exception class to handle invalid parameters in API requests
public class InvalidParameterException extends RuntimeException {
    public InvalidParameterException(String message) {
        super(message);
    }
}