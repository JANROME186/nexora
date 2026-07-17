package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.history.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;

import java.time.LocalDateTime;
import java.util.List;

public record PatientResultHistoryView(
        PatientId patientId,
        List<ResultHistoryEntry> entries
) {
    public record ResultHistoryEntry(
            ResultId resultId,
            String analyteName,
            String stringValue,
            String referenceRange,
            boolean isAbnormal,
            LocalDateTime releasedAt
    ) {}
}
