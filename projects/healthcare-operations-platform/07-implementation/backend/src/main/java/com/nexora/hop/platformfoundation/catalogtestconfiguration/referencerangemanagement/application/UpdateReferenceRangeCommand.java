package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.application;

import java.time.LocalDate;
import java.util.List;

public record UpdateReferenceRangeCommand(
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        List<ReferenceRangeSegmentInput> segments) {
}
