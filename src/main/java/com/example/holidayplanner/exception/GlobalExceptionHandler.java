package com.example.holidayplanner.exception;

import jakarta.validation.ConstraintViolationException;

import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.holidayplanner.generated.model.ErrorResponse;

/**
 * Global exception handler to manage and respond to various exceptions across the application.
 * <p>
 * This class provides centralized handling for common exceptions, returning structured error responses
 * for invalid parameters, resource not found, network issues, and uncaught exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * URL for fetching available countries from Nager Date API.
     */
    @Value("${nagar.available.countries.api.url:https://date.nager.at/api/v3/AvailableCountries}")
    private String availableCountriesApi;

    /**
     * Handle invalid parameter and method argument mismatch exceptions and return a structured error response.
     * <p>
     * Catches validation, parameter, and argument type mismatch exceptions and responds with HTTP 400 Bad Request.
     *
     * @param ex Exception thrown
     * @param request WebRequest context
     * @return ResponseEntity containing an ErrorResponse with details of the error
     */
    @ExceptionHandler({ConstraintViolationException.class, InvalidParameterException.class,MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleInvalidParameterException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle no resource found exceptions and return a structured error response.
     * <p>
     * Catches missing resource exceptions and responds with HTTP 404 Not Found.
     *
     * @param ex NoResourceFoundException thrown
     * @param request WebRequest context
     * @return ResponseEntity containing an ErrorResponse with details of the error
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle resource access exceptions, particularly network-related issues with Nager API communication,
     * and return a structured error response.
     * <p>
     * Catches network errors and responds with HTTP 500 Internal Server Error, including possible troubleshooting info.
     *
     * @param ex ResourceAccessException thrown
     * @param request WebRequest context
     * @return ResponseEntity containing an ErrorResponse with details of the error
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException ex, WebRequest request) {
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Resource Access Error";
        if (errorMessage.contains("I/O error")) {
            errorMessage += " - possible network issue or north bound nagar date service might be down, please check this URL from web browser " + availableCountriesApi;
        }
        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                errorMessage,
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle all other uncaught exceptions and return a structured error response.
     * <p>
     * Catches any unhandled exceptions and responds with HTTP 500 Internal Server Error.
     *
     * @param ex Exception thrown
     * @param request WebRequest context
     * @return ResponseEntity containing an ErrorResponse with details of the error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}