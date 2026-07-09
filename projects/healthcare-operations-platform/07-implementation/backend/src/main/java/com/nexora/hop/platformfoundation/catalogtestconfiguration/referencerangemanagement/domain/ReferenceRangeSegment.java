package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain;

import java.math.BigDecimal;

/** A demographic segment with numeric thresholds (ENT-REF-002). */
public record ReferenceRangeSegment(
        String segmentId,
        String rangeId,
        String sex,
        Integer ageMinDays,
        Integer ageMaxDays,
        String condition,
        BigDecimal normalLow,
        BigDecimal normalHigh,
        BigDecimal criticalLow,
        BigDecimal criticalHigh,
        String unit) {

    public static final String SEX_ANY = "any";
    public static final String SEX_MALE = "male";
    public static final String SEX_FEMALE = "female";
    public static final String SEX_OTHER = "other";
}
