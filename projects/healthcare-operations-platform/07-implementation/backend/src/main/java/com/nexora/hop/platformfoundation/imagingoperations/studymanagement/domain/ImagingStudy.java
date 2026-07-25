package com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain;

import java.time.Instant;

public record ImagingStudy(
        String studyId,
        String tenantId,
        String accessionNumber,
        String patientId,
        String modality,
        String studyDescription,
        String studyStatus,
        int seriesCount,
        int instanceCount,
        Instant studyDate,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
) {}
