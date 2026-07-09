package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.application;

import java.math.BigDecimal;

public record ReferenceRangeSegmentInput(
        String sex,
        Integer ageMinDays,
        Integer ageMaxDays,
        String condition,
        BigDecimal normalLow,
        BigDecimal normalHigh,
        BigDecimal criticalLow,
        BigDecimal criticalHigh,
        String unit) {
}
