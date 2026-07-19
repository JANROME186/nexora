package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

/** Unit coverage for the RN-004 fixed-window rate-limit counter. */
class PartnerApiRateLimiterTest {

    private final PartnerApiRateLimiter limiter = new PartnerApiRateLimiter();

    @Test
    void allowsRequestsUpToThePolicyLimitWithinTheSameWindow() {
        Instant now = Instant.parse("2026-01-01T00:00:10Z");
        assertThat(limiter.tryConsume("key-1", 2, now)).isTrue();
        assertThat(limiter.tryConsume("key-1", 2, now.plusSeconds(1))).isTrue();
        assertThat(limiter.tryConsume("key-1", 2, now.plusSeconds(2))).isFalse();
    }

    @Test
    void resetsTheCounterOnceTheEpochMinuteChanges() {
        Instant firstWindow = Instant.parse("2026-01-01T00:00:59Z");
        Instant nextWindow = Instant.parse("2026-01-01T00:01:00Z");
        assertThat(limiter.tryConsume("key-2", 1, firstWindow)).isTrue();
        assertThat(limiter.tryConsume("key-2", 1, firstWindow)).isFalse();
        assertThat(limiter.tryConsume("key-2", 1, nextWindow)).isTrue();
    }

    @Test
    void tracksEachPartnerKeyIndependently() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");
        assertThat(limiter.tryConsume("key-a", 1, now)).isTrue();
        assertThat(limiter.tryConsume("key-b", 1, now)).isTrue();
        assertThat(limiter.tryConsume("key-a", 1, now)).isFalse();
    }
}
