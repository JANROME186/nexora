package com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain;

import java.math.BigDecimal;
import java.util.List;

/** Value constraints applied to captured analyte results (ENT-ANL-002), one-to-one with the analyte. */
public record AnalyteResultConstraint(
        String constraintId,
        String analyteId,
        BigDecimal minValue,
        BigDecimal maxValue,
        List<String> allowedCodedValues) {
}
