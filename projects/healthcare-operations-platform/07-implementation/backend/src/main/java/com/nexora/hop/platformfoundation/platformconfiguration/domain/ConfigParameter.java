package com.nexora.hop.platformfoundation.platformconfiguration.domain;

/** BCM-PLT-002 {@code ConfigKeyRoot} aggregate root (business-model.md). */
public record ConfigParameter(
        String key, String valueType, String rawValue, boolean tenantOverrideAllowed, boolean isEncrypted) {
}
