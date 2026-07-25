package com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain;

import java.time.Instant;

public record RadiologyReport(
        String reportId,
        String tenantId,
        String studyId,
        String radiologistId,
        String findingsText,
        String impressionText,
        String reportStatus,
        Instant signedAt,
        String digitalSignatureHash,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
) {}
