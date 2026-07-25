package com.nexora.hop.platformfoundation.imagingoperations.receptionintake.domain;

import java.time.Instant;

public record ImagingReceptionIntake(
        String intakeId,
        String tenantId,
        String appointmentSlotId,
        String patientId,
        Instant intakeTime,
        String checkInStatus,
        boolean preparationVerified,
        String intakeNotes,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
) {}
