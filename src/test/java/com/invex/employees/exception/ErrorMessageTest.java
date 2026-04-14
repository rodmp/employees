package com.invex.employees.exception;


// Created 2026-04-13
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ErrorMessageTest {

    @Test
    void accessorsAndNoArgsConstructor() {
        ErrorMessage message = new ErrorMessage();
        assertNull(message.getMessage());

        Date now = new Date();
        message.setStatusCode(400);
        message.setTimestamp(now);
        message.setMessage("err");
        message.setDescription("desc");

        assertEquals(400, message.getStatusCode());
        assertEquals(now, message.getTimestamp());
        assertEquals("err", message.getMessage());
        assertEquals("desc", message.getDescription());
    }

    @Test
    void allArgsConstructor() {
        Date now = new Date();
        ErrorMessage message = new ErrorMessage(404, now, "nf", "uri=/x");

        assertEquals(404, message.getStatusCode());
        assertEquals(now, message.getTimestamp());
        assertEquals("nf", message.getMessage());
        assertEquals("uri=/x", message.getDescription());
    }
}
