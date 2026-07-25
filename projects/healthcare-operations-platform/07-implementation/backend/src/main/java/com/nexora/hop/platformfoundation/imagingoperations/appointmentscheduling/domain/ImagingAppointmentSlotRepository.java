package com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ImagingAppointmentSlotRepository {
    ImagingAppointmentSlot save(ImagingAppointmentSlot slot);
    Optional<ImagingAppointmentSlot> findById(String tenantId, String slotId);
    List<ImagingAppointmentSlot> findByTenantAndPatient(String tenantId, String patientId);
    List<ImagingAppointmentSlot> findOverlappingRoomSlots(String tenantId, String procedureRoomId, Instant startTime, Instant endTime);
}
