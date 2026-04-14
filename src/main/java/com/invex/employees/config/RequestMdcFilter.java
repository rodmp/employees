package com.invex.employees.config;

// Created 2026-04-13
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.invex.employees.constant.ApiPaths.TRANSACTION_ID_HEADER;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestMdcFilter extends OncePerRequestFilter {

    private static final String MDC_REQUEST_ID = "http.request_id";
    private static final String MDC_USER_AGENT = "http.user_agent";
    private static final String MDC_TRANSACTION_ID = "http.transaction_id";
    private static final String MDC_METHOD = "http.method";
    private static final String MDC_PATH = "http.path";

    /** Solo estas cabeceras van al log en DEBUG (evita cookies, Authorization, etc.). */
    private static final List<String> HEADERS_OK_TO_LOG = List.of(
            TRANSACTION_ID_HEADER,
            "X-Request-Id",
            "Accept",
            "Content-Type",
            "Accept-Language"
    );

    private static final int MAX_PATH = 512;
    private static final int MAX_HEADER_IN_LOG = 128;
    private static final int MAX_MDC_VALUE = 256;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String txId = readOrCreateTransactionId(request);
        response.setHeader(TRANSACTION_ID_HEADER, txId);

        String method = request.getMethod() != null ? request.getMethod() : "";
        String path = clip(request.getRequestURI() != null ? request.getRequestURI() : "", MAX_PATH);

        try {
            MDC.put(MDC_TRANSACTION_ID, txId);
            MDC.put(MDC_METHOD, method);
            MDC.put(MDC_PATH, path);
            copyHeaderToMdc(request, "X-Request-Id", MDC_REQUEST_ID);
            copyHeaderToMdc(request, "User-Agent", MDC_USER_AGENT);

            if (log.isDebugEnabled()) {
                log.debug("{} {} | {}", method, path, debugHeadersLine(request));
            }

            chain.doFilter(request, response);

            if (log.isDebugEnabled()) {
                log.debug("{} {} -> {}", method, path, response.getStatus());
            }
        } finally {
            MDC.remove(MDC_TRANSACTION_ID);
            MDC.remove(MDC_METHOD);
            MDC.remove(MDC_PATH);
            MDC.remove(MDC_REQUEST_ID);
            MDC.remove(MDC_USER_AGENT);
        }
    }

    private static String readOrCreateTransactionId(HttpServletRequest request) {
        String raw = request.getHeader(TRANSACTION_ID_HEADER);
        if (raw == null) {
            return UUID.randomUUID().toString();
        }
        raw = raw.trim();
        if (raw.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        return raw.length() > MAX_MDC_VALUE ? raw.substring(0, MAX_MDC_VALUE) : raw;
    }

    private static void copyHeaderToMdc(HttpServletRequest request, String headerName, String mdcKey) {
        String v = request.getHeader(headerName);
        if (v == null || v.isBlank()) {
            return;
        }
        if (v.length() > MAX_MDC_VALUE) {
            v = v.substring(0, MAX_MDC_VALUE);
        }
        MDC.put(mdcKey, v);
    }

    private static String debugHeadersLine(HttpServletRequest request) {
        StringBuilder out = new StringBuilder();
        for (String name : HEADERS_OK_TO_LOG) {
            String v = request.getHeader(name);
            if (v == null || v.isBlank()) {
                continue;
            }
            if (out.length() > 0) {
                out.append(", ");
            }
            v = clip(v.replace('\r', ' ').replace('\n', ' '), MAX_HEADER_IN_LOG);
            out.append(name).append('=').append(v);
        }
        return out.length() == 0 ? "-" : out.toString();
    }

    private static String clip(String s, int max) {
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "...";
    }
}
