package com.example.holidayplanner.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("Mocked request description");
    }

    @Test
    @DisplayName("Test handleInvalidParameterException returns BAD_REQUEST")
    void handleInvalidParameterException_ShouldReturnBadRequest() {
        Exception exception = new MissingServletRequestParameterException("param", "String");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInvalidParameterException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String errorMessage = response.getBody() != null ? response.getBody().error() : "wrong message";
        assertEquals("Bad Request", errorMessage);
    }

    @Test
    @DisplayName("Test handleNoResourceFoundException returns NOT_FOUND")
    void handleNoResourceFoundException_ShouldReturnNotFound() {
        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, "/invalid/path");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNoResourceFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        String errorMessage = response.getBody() != null ? response.getBody().error() : "wrong message";
        assertEquals("Not Found", errorMessage);
    }

    @Test
    @DisplayName("Test handleResourceAccessException returns INTERNAL_SERVER_ERROR")
    void handleResourceAccessException_ShouldReturnInternalServerError() {
        ResourceAccessException exception = new ResourceAccessException("I/O error occurred");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleResourceAccessException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        String errorMessage = response.getBody() != null ? response.getBody().error() : "wrong message";
        assertEquals("Internal Server Error", errorMessage);
        Assertions.assertTrue(response.getBody().message().startsWith("I/O error occurred - possible network issue or north bound nagar date service might be down, please check this URL from web browser"));
    }

    @Test
    @DisplayName("Test handleGlobalException returns INTERNAL_SERVER_ERROR")
    void handleGlobalException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        String errorMessage = response.getBody() != null ? response.getBody().error() : "wrong message";
        assertEquals("Internal Server Error", errorMessage);
    }
}