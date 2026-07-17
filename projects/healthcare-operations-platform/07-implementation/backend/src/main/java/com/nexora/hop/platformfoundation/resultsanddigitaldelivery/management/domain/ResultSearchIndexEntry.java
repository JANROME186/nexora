package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.management.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.*;

import java.time.LocalDateTime;

public record ResultSearchIndexEntry(
        ResultId resultId,
        TenantId tenantId,
        LaboratoryId laboratoryId,
        BranchId branchId,
        OrderId orderId,
        SampleId sampleId,
        PatientId patientId,
        String analyteName,
        Status status,
        boolean criticalFlagPresent,
        LocalDateTime lastEventAt,
        int projectionVersion
) {
    public enum Status {
        CAPTURED,
        PENDING_TECHNICAL_VALIDATION,
        TECHNICALLY_VALIDATED,
        PENDING_MEDICAL_VALIDATION,
        MEDICALLY_VALIDATED,
        RELEASED,
        AMENDED
    }
}
