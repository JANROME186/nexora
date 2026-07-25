package com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.domain;

import java.time.Instant;

public record ImagingAppointmentSlot(
        String slotId,
        String tenantId,
        String patientId,
        String branchId,
        String modality,
        String procedureCode,
        String procedureRoomId,
        Instant startTime,
        Instant endTime,
        int durationMinutes,
        String slotStatus,
        String notes,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
) {
    public ImagingAppointmentSlot {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration minutes must be positive");
        }
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }
}
