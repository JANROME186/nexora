package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Captured analytical value with unit, method and normalization metadata (BCM-LAB-006, VO-LPR-003).
 */
public record ResultValue(
        String rawValue,
        BigDecimal numericValue,
        String unit,
        String method,
        Instant capturedAt,
        String capturedBy,
        String deviceReference) {
}
