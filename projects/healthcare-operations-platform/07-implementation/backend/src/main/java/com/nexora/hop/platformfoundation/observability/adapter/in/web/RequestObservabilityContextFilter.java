package com.nexora.hop.platformfoundation.observability.adapter.in.web;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Populates the SLF4J {@link MDC} with {@code tenantId}, {@code userId} and {@code traceId} for
 * every request so every log line carries operational context (BCM-PLT-006, closes the
 * metrics-logs-traces-validation-runbook.md and tenant-impact-triage-runbook.md known gap on
 * MDC context, COM-MOD-012-BE-001). Runs ahead of {@code HopAuthorizationInterceptor} so context
 * is available even for rejected (401/403) and unmapped (health/actuator) requests.
 *
 * <p>Header values are attacker-controlled (SpotBugs/FindSecBugs {@code SERVLET_HEADER}, CWE-807):
 * {@link #sanitizeForLogging} strips control characters (including CR/LF) before a tenant/user
 * header value ever reaches a log line, preventing log injection/forging, and {@link
 * #resolveTraceId} only reuses the inbound {@code traceparent} trace-id segment when it matches
 * the strict W3C Trace Context format, so a malformed or hostile header can never be reflected
 * back in the {@code X-Trace-Id} response header.
 */
@Component
class RequestObservabilityContextFilter extends OncePerRequestFilter {

    static final String TENANT_ID_HEADER = "X-HOP-TENANT-ID";
    static final String USER_ID_HEADER = "X-HOP-USER-ID";
    static final String TRACE_PARENT_HEADER = "traceparent";
    static final String TRACE_ID_RESPONSE_HEADER = "X-Trace-Id";

    static final String MDC_TENANT_ID = "tenantId";
    static final String MDC_USER_ID = "userId";
    static final String MDC_TRACE_ID = "traceId";

    private static final String UNKNOWN = "unknown";
    private static final int MAX_CONTEXT_VALUE_LENGTH = 120;
    private static final Pattern CONTROL_CHARACTERS = Pattern.compile("[\\p{Cntrl}]");
    private static final Pattern W3C_TRACE_ID = Pattern.compile("^[0-9a-f]{32}$");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = resolveTraceId(request);
        try {
            MDC.put(MDC_TENANT_ID, sanitizeForLogging(request.getHeader(TENANT_ID_HEADER)));
            MDC.put(MDC_USER_ID, sanitizeForLogging(request.getHeader(USER_ID_HEADER)));
            MDC.put(MDC_TRACE_ID, traceId);
            response.setHeader(TRACE_ID_RESPONSE_HEADER, traceId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_TENANT_ID);
            MDC.remove(MDC_USER_ID);
            MDC.remove(MDC_TRACE_ID);
        }
    }

    /**
     * Strips control characters (CR/LF and friends) and bounds the length before a client-supplied
     * header value is allowed into a log line, so a hostile header cannot forge extra log lines or
     * exhaust log storage.
     */
    private static String sanitizeForLogging(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            return UNKNOWN;
        }
        String cleaned = CONTROL_CHARACTERS.matcher(headerValue.trim()).replaceAll("");
        if (cleaned.isBlank()) {
            return UNKNOWN;
        }
        return cleaned.length() > MAX_CONTEXT_VALUE_LENGTH ? cleaned.substring(0, MAX_CONTEXT_VALUE_LENGTH) : cleaned;
    }

    /**
     * Reuses the trace id segment of an inbound W3C Trace Context {@code traceparent} header
     * ({@code version-traceid-parentid-flags}) only when it matches the strict 32-lowercase-hex-
     * digit W3C format, so requests are correlatable across a future OpenTelemetry Collector
     * without ever reflecting an unvalidated client-supplied value back in a response header;
     * otherwise mints a new id so every request is still traceable end-to-end within this
     * service's own logs.
     */
    private static String resolveTraceId(HttpServletRequest request) {
        String traceparent = request.getHeader(TRACE_PARENT_HEADER);
        if (traceparent != null) {
            String[] parts = traceparent.split("-");
            if (parts.length >= 2 && W3C_TRACE_ID.matcher(parts[1]).matches()) {
                return parts[1];
            }
        }
        return UUID.randomUUID().toString().replace("-", "");
    }
}
