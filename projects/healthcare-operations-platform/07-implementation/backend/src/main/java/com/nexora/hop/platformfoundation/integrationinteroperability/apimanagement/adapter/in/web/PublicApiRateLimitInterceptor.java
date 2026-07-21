package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.in.web;

import java.io.IOException;
import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.ApiSurfaceRegistration;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicy;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicyRepository;

/**
 * Enforces BCM-PLT-005 RN-007 for anonymous public-classified requests, closing the RN-004 gap
 * TD-BE-015 originally flagged. Applies only to {@code /api/public/**} (never any internal or
 * partner endpoint) and reuses the existing {@link PartnerApiRateLimiter} fixed-window counter
 * under a {@code public::} namespace prefix so a public consumer cannot exhaust a partner key's
 * budget or vice-versa.
 *
 * <p>The consumer identity used for rate-limit accounting is derived from the active
 * {@link RateLimitPolicy#consumerIdentificationMethod()} on the {@code public}-classification
 * policy:
 *
 * <ul>
 *   <li>{@link RateLimitPolicy#METHOD_IP_ADDRESS}: uses {@code X-Forwarded-For}'s first entry
 *       when present, else {@link HttpServletRequest#getRemoteAddr()}.</li>
 *   <li>{@link RateLimitPolicy#METHOD_SESSION_TOKEN}: uses the {@code X-Public-Session-Token}
 *       header, falling back to the servlet's session id when the header is absent.</li>
 * </ul>
 *
 * <p>When no {@code public}-classification policy has been configured the interceptor allows the
 * request through unbounded, consistent with rate limiting being an explicitly opt-in governance
 * control per classification tier (identical policy to
 * {@link PartnerApiKeyRateLimitInterceptor}). This mirrors the local-deterministic scope decision
 * documented in {@link PartnerApiRateLimiter}: a distributed rate limiter (e.g. Redis-backed) is a
 * provider-specific choice for a later production-hardening backlog item.
 */
@Component
public class PublicApiRateLimitInterceptor implements HandlerInterceptor {

    /** Public API base path prefix. Kept private here to avoid coupling other modules. */
    public static final String PUBLIC_API_BASE_PATH = "/api/public";
    /** Session-token header consulted when {@code METHOD_SESSION_TOKEN} is active. */
    public static final String SESSION_TOKEN_HEADER = "X-Public-Session-Token";
    /** Structured error code emitted when the public window is exhausted. */
    public static final String PUBLIC_RATE_LIMIT_EXCEEDED_CODE = "PUBLIC_RATE_LIMIT_EXCEEDED";

    private static final String CONSUMER_BUCKET_NAMESPACE = "public::";

    private final RateLimitPolicyRepository rateLimitPolicyRepository;
    private final PartnerApiRateLimiter rateLimiter;

    public PublicApiRateLimitInterceptor(
            RateLimitPolicyRepository rateLimitPolicyRepository, PartnerApiRateLimiter rateLimiter) {
        this.rateLimitPolicyRepository = rateLimitPolicyRepository;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (request.getRequestURI() == null || !request.getRequestURI().startsWith(PUBLIC_API_BASE_PATH)) {
            return true;
        }
        RateLimitPolicy policy = rateLimitPolicyRepository
                .findByClassification(ApiSurfaceRegistration.CLASSIFICATION_PUBLIC).orElse(null);
        if (policy == null) {
            return true;
        }
        String consumerBucket = CONSUMER_BUCKET_NAMESPACE + resolveConsumerIdentity(request, policy);
        if (!rateLimiter.tryConsume(consumerBucket, policy.requestsPerMinute(), Instant.now())) {
            writeError(response, 429, PUBLIC_RATE_LIMIT_EXCEEDED_CODE,
                    "Rate limit of " + policy.requestsPerMinute() + " requests per minute exceeded.");
            return false;
        }
        return true;
    }

    static String resolveConsumerIdentity(HttpServletRequest request, RateLimitPolicy policy) {
        if (RateLimitPolicy.METHOD_SESSION_TOKEN.equals(policy.consumerIdentificationMethod())) {
            String header = request.getHeader(SESSION_TOKEN_HEADER);
            if (header != null && !header.isBlank()) {
                return header;
            }
            String sessionId = request.getRequestedSessionId();
            if (sessionId != null && !sessionId.isBlank()) {
                return sessionId;
            }
            return "anonymous";
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            int comma = forwardedFor.indexOf(',');
            return comma == -1 ? forwardedFor.trim() : forwardedFor.substring(0, comma).trim();
        }
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr == null ? "unknown" : remoteAddr;
    }

    private static void writeError(HttpServletResponse response, int status, String code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String messageKey = "public.error." + code.toLowerCase(java.util.Locale.ROOT);
        response.getWriter().write(
                "{\"status\":%d,\"code\":\"%s\",\"messageKey\":\"%s\",\"message\":\"%s\",\"occurredAt\":\"%s\"}"
                        .formatted(status, code, messageKey, message, Instant.now()));
    }
}
