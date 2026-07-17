package com.nexora.hop.platformfoundation.documentmanagement.domain;

import java.time.LocalDate;

public record RetentionPolicy(
    LocalDate retainUntil,
    boolean legalHold
) {
    public static RetentionPolicy standard(LocalDate retainUntil) {
        return new RetentionPolicy(retainUntil, false);
    }
}
