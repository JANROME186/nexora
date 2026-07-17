package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain;

public record ResultFlaggedCriticalEvent(
        String resultId,
        String tenantId,
        String laboratoryId,
        String criticalReason) {
}
