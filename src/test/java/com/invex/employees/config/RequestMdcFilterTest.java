package com.invex.employees.config;


// Created 2026-04-13
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static com.invex.employees.constant.ApiPaths.TRANSACTION_ID_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestMdcFilterTest {

    private final RequestMdcFilter filter = new RequestMdcFilter();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @BeforeEach
    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void putsRequestIdAndUserAgentWhenPresent() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/employees");
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn("tx-from-client");
        when(request.getHeader("X-Request-Id")).thenReturn("req-123");
        when(request.getHeader("User-Agent")).thenReturn("JUnit");
        doAnswer(invocation -> {
            assertEquals("tx-from-client", MDC.get("http.transaction_id"));
            assertEquals("GET", MDC.get("http.method"));
            assertEquals("/employees", MDC.get("http.path"));
            assertEquals("req-123", MDC.get("http.request_id"));
            assertEquals("JUnit", MDC.get("http.user_agent"));
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(TRANSACTION_ID_HEADER, "tx-from-client");
        verify(chain).doFilter(request, response);
        assertNull(MDC.get("http.request_id"));
        assertNull(MDC.get("http.transaction_id"));
        assertNull(MDC.get("http.method"));
        assertNull(MDC.get("http.path"));
    }

    @Test
    void skipsBlankHeaders() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/employees");
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn("   ");
        when(request.getHeader("X-Request-Id")).thenReturn("   ");
        when(request.getHeader("User-Agent")).thenReturn(null);
        doAnswer(invocation -> {
            assertNotNull(MDC.get("http.transaction_id"));
            assertNull(MDC.get("http.request_id"));
            assertNull(MDC.get("http.user_agent"));
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(eq(TRANSACTION_ID_HEADER), any());
    }

    @Test
    void truncatesVeryLongUserAgent() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/employees");
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(null);
        when(request.getHeader("X-Request-Id")).thenReturn(null);
        String longUa = "x".repeat(300);
        when(request.getHeader("User-Agent")).thenReturn(longUa);
        doAnswer(invocation -> {
            assertEquals(256, MDC.get("http.user_agent").length());
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilterInternal(request, response, chain);
    }

    @Test
    void generatesTransactionIdWhenAbsent() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/employees/1");
        when(request.getHeader(TRANSACTION_ID_HEADER)).thenReturn(null);
        when(request.getHeader("X-Request-Id")).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn(null);
        doAnswer(invocation -> {
            assertNotNull(MDC.get("http.transaction_id"));
            return null;
        }).when(chain).doFilter(any(), any());

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(eq(TRANSACTION_ID_HEADER), any());
    }
}
