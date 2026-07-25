package com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.domain;

import java.time.Instant;

public record RadiologyDictation(
        String dictationId,
        String tenantId,
        String studyId,
        String radiologistId,
        String dictationText,
        String audioReferenceUrl,
        String dictationStatus,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
) {}
