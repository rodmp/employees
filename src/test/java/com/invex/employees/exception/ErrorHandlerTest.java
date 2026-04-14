package com.invex.employees.exception;


// Created 2026-04-13
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
        when(webRequest.getDescription(false)).thenReturn("uri=/employees");
    }

    @Test
    void employeeNotFoundReturns404() {
        ResponseEntity<ErrorMessage> response = errorHandler.employeeNotFoundHandler(
                new EmployeeNotFoundException("missing"), webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatusCode());
        assertEquals("missing", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void validationReturns400WithFieldDetails() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("employee", "firstName", "must not be blank"),
                new FieldError("employee", "age", "must not be null")));

        ResponseEntity<ErrorMessage> response = errorHandler.validationHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("firstName: must not be blank; age: must not be null", response.getBody().getMessage());
    }

    @Test
    void notReadableReturns400() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMostSpecificCause()).thenReturn(new IllegalArgumentException("Malformed JSON"));

        ResponseEntity<ErrorMessage> response = errorHandler.notReadableHandler(ex, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Malformed JSON", response.getBody().getMessage());
    }

    @Test
    void runtimeReturns400() {
        ResponseEntity<ErrorMessage> response = errorHandler.runtimeExceptionHandler(
                new IllegalStateException("bad state"), webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("bad state", response.getBody().getMessage());
    }

    @Test
    void genericExceptionReturns500() throws Exception {
        ResponseEntity<ErrorMessage> response = errorHandler.requestExceptionHandler(
                new SQLException("db down"), webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatusCode());
        assertEquals("db down", response.getBody().getMessage());
    }
}
