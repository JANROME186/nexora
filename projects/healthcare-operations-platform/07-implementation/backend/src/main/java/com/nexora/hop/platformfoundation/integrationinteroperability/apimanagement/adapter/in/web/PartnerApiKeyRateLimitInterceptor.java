package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.in.web;

import java.io.IOException;
import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.PartnerApiKey;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.PartnerApiKeyRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicy;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicyRepository;
import com.nexora.hop.platformfoundation.integrationinteroperability.shared.IntegrationErrorCodes;

/**
 * Enforces RN-004 (rate-limit policy) for requests carrying a partner API key. Only requests that
 * present the {@value #PARTNER_KEY_HEADER} header are affected; requests without it (the vast
 * majority of internal/employee-portal traffic) pass through unchanged, so this cannot regress any
 * existing non-partner endpoint. A key with no configured {@link RateLimitPolicy} is allowed
 * through unbounded, consistent with rate limiting being an explicitly opt-in governance control
 * per classification tier rather than a universal default.
 */
@Component
public class PartnerApiKeyRateLimitInterceptor implements HandlerInterceptor {

    public static final String PARTNER_KEY_HEADER = "X-Partner-Api-Key";

    private final PartnerApiKeyRepository partnerApiKeyRepository;
    private final RateLimitPolicyRepository rateLimitPolicyRepository;
    private final PartnerApiRateLimiter rateLimiter;

    public PartnerApiKeyRateLimitInterceptor(
            PartnerApiKeyRepository partnerApiKeyRepository,
            RateLimitPolicyRepository rateLimitPolicyRepository,
            PartnerApiRateLimiter rateLimiter) {
        this.partnerApiKeyRepository = partnerApiKeyRepository;
        this.rateLimitPolicyRepository = rateLimitPolicyRepository;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String partnerKeyId = request.getHeader(PARTNER_KEY_HEADER);
        if (partnerKeyId == null || partnerKeyId.isBlank()) {
            return true;
        }
        PartnerApiKey key = partnerApiKeyRepository.findById(partnerKeyId).orElse(null);
        if (key == null || !key.isUsable()) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    IntegrationErrorCodes.API_PARTNER_KEY_INVALID_OR_SCOPE_MISMATCH,
                    "Partner API key is missing, unknown, or not active.");
            return false;
        }
        if (key.rateLimitPolicyRef() == null) {
            return true;
        }
        RateLimitPolicy policy = rateLimitPolicyRepository.findById(key.rateLimitPolicyRef()).orElse(null);
        if (policy == null) {
            return true;
        }
        if (!rateLimiter.tryConsume(key.keyId(), policy.requestsPerMinute(), Instant.now())) {
            writeError(response, 429, IntegrationErrorCodes.API_RATE_LIMIT_EXCEEDED,
                    "Rate limit of " + policy.requestsPerMinute() + " requests per minute exceeded.");
            return false;
        }
        return true;
    }

    private static void writeError(HttpServletResponse response, int status, String code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"status\":%d,\"code\":\"%s\",\"message\":\"%s\",\"occurredAt\":\"%s\"}"
                        .formatted(status, code, message, Instant.now()));
    }
}
