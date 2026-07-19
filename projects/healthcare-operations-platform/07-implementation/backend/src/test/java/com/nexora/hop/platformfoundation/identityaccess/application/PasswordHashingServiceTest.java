package com.nexora.hop.platformfoundation.identityaccess.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PasswordHashingServiceTest {

    private final PasswordHashingService service = new PasswordHashingService();

    @Test
    void hashesAndMatchesPassword() {
        String raw = "securePassword123";
        String hash = service.hash(raw);

        assertThat(hash).isNotBlank();
        assertThat(hash).isNotEqualTo(raw);
        assertThat(service.matches(raw, hash)).isTrue();
    }

    @Test
    void rejectsMismatchingPassword() {
        String raw = "securePassword123";
        String hash = service.hash(raw);

        assertThat(service.matches("wrongPassword", hash)).isFalse();
    }

    @Test
    void handlesNullInputs() {
        assertThat(service.hash(null)).isEmpty();
        assertThat(service.matches(null, "hash")).isFalse();
        assertThat(service.matches("password", null)).isFalse();
    }
}
