package com.example.holidayplanner.exception;

import java.time.LocalDateTime;

// Record to represent the structure of error responses sent to clients in case of exceptions
public record ErrorResponse(LocalDateTime timestamp,
                            int status,
                            String error,
                            String message,
                            String path) {
}