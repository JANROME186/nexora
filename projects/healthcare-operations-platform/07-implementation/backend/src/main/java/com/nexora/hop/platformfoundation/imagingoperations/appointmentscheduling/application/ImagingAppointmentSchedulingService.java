package com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.application;

import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.domain.ImagingAppointmentSlot;
import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.domain.ImagingAppointmentSlotRepository;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingDomainException;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingErrorCode;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ImagingAppointmentSchedulingService {

    private final ImagingAppointmentSlotRepository repository;

    public ImagingAppointmentSchedulingService(ImagingAppointmentSlotRepository repository) {
        this.repository = repository;
    }

    public ImagingAppointmentSlot scheduleSlot(
            String tenantId,
            String patientId,
            String branchId,
            String modality,
            String procedureCode,
            String procedureRoomId,
            Instant startTime,
            int durationMinutes,
            String notes,
            String actorId) {
        Instant endTime = startTime.plusSeconds(durationMinutes * 60L);
        List<ImagingAppointmentSlot> overlapping = repository.findOverlappingRoomSlots(tenantId, procedureRoomId, startTime, endTime);
        if (!overlapping.isEmpty()) {
            throw new ImagingDomainException(ImagingErrorCode.ROOM_NOT_AVAILABLE, "Procedure room " + procedureRoomId + " is already occupied during requested slot time");
        }

        String slotId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        ImagingAppointmentSlot slot = new ImagingAppointmentSlot(
                slotId, tenantId, patientId, branchId, modality, procedureCode, procedureRoomId,
                startTime, endTime, durationMinutes, "SCHEDULED", notes, actorId, now, actorId, now
        );
        return repository.save(slot);
    }

    public ImagingAppointmentSlot getSlot(String tenantId, String slotId) {
        return repository.findById(tenantId, slotId)
                .orElseThrow(() -> new ImagingNotFoundException(ImagingErrorCode.APPOINTMENT_NOT_FOUND, "Imaging appointment slot " + slotId + " not found"));
    }

    public List<ImagingAppointmentSlot> listSlotsForPatient(String tenantId, String patientId) {
        return repository.findByTenantAndPatient(tenantId, patientId);
    }

    public ImagingAppointmentSlot updateSlotStatus(String tenantId, String slotId, String newStatus, String actorId) {
        ImagingAppointmentSlot existing = getSlot(tenantId, slotId);
        ImagingAppointmentSlot updated = new ImagingAppointmentSlot(
                existing.slotId(), existing.tenantId(), existing.patientId(), existing.branchId(),
                existing.modality(), existing.procedureCode(), existing.procedureRoomId(),
                existing.startTime(), existing.endTime(), existing.durationMinutes(),
                newStatus, existing.notes(), existing.createdBy(), existing.createdAt(), actorId, Instant.now()
        );
        return repository.save(updated);
    }
}
