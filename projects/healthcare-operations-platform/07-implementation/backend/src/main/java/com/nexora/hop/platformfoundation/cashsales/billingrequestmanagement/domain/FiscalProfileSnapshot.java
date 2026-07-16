package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain;

import java.time.Instant;

public record FiscalProfileSnapshot(
        String legalName,
        String taxIdentifier,
        String fiscalAddress,
        String fiscalRegime,
        Instant capturedAt) {
}
