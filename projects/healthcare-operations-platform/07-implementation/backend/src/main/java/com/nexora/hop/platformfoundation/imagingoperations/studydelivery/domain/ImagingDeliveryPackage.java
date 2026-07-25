package com.nexora.hop.platformfoundation.imagingoperations.studydelivery.domain;

import java.time.Instant;

public record ImagingDeliveryPackage(
        String packageId,
        String tenantId,
        String studyId,
        String patientId,
        String deliveryFormat,
        String deliveryStatus,
        String portalAccessToken,
        Instant expiresAt,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
) {}
