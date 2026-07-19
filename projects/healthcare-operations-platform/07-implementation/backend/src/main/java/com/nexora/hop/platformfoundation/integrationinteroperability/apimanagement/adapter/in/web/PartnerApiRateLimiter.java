package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.in.web;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * In-memory fixed-window (one-minute) request counter enforcing {@code RateLimitPolicy}
 * (RN-004). One window counter per partner API key id; the window resets whenever the current
 * epoch minute changes. Local-deterministic in scope for the same reason as
 * {@code LocalDeterministicPassthroughIntegrationAdapter}/{@code LocalDeterministicFiscalAdapter}:
 * a distributed rate limiter (e.g. Redis-backed) would be a provider-specific choice for a later
 * production-hardening backlog item.
 */
@Component
public class PartnerApiRateLimiter {

    private record WindowState(long windowEpochMinute, AtomicInteger requestCount) {
    }

    private final ConcurrentHashMap<String, WindowState> windowsByPartnerKeyId = new ConcurrentHashMap<>();

    /** @return {@code true} if the request is within the policy's requests-per-minute budget */
    public boolean tryConsume(String partnerKeyId, int requestsPerMinute, Instant now) {
        long currentWindow = now.getEpochSecond() / 60;
        WindowState state = windowsByPartnerKeyId.compute(partnerKeyId, (key, existing) -> {
            if (existing == null || existing.windowEpochMinute() != currentWindow) {
                return new WindowState(currentWindow, new AtomicInteger(0));
            }
            return existing;
        });
        return state.requestCount().incrementAndGet() <= requestsPerMinute;
    }
}
