package com.example.holidayplanner.exception;

/**
 * Custom exception class to handle invalid parameters in API requests.
 * <p>
 * This exception is thrown when an API request contains invalid parameters,
 * allowing for clear error handling and messaging throughout the application.
 */
public class InvalidParameterException extends RuntimeException {
    /**
     * Constructs a new InvalidParameterException with the specified detail message.
     * <p>
     * Used to provide a descriptive error message when invalid parameters are encountered in API requests.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidParameterException(String message) {
        super(message);
    }
}