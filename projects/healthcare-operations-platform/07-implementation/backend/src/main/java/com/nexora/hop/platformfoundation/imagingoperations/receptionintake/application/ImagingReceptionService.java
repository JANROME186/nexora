package com.nexora.hop.platformfoundation.imagingoperations.receptionintake.application;

import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.domain.ImagingReceptionIntake;
import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.domain.ImagingReceptionIntakeRepository;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingErrorCode;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingNotFoundException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ImagingReceptionService {

    private final ImagingReceptionIntakeRepository repository;

    public ImagingReceptionService(ImagingReceptionIntakeRepository repository) {
        this.repository = repository;
    }

    public ImagingReceptionIntake checkIn(
            String tenantId,
            String appointmentSlotId,
            String patientId,
            boolean preparationVerified,
            String intakeNotes,
            String actorId) {
        String intakeId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        ImagingReceptionIntake intake = new ImagingReceptionIntake(
                intakeId, tenantId, appointmentSlotId, patientId, now,
                "CHECKED_IN", preparationVerified, intakeNotes, actorId, now, actorId, now
        );
        return repository.save(intake);
    }

    public ImagingReceptionIntake getIntake(String tenantId, String intakeId) {
        return repository.findById(tenantId, intakeId)
                .orElseThrow(() -> new ImagingNotFoundException(ImagingErrorCode.RECEPTION_NOT_FOUND, "Imaging reception intake " + intakeId + " not found"));
    }

    public ImagingReceptionIntake getIntakeBySlot(String tenantId, String appointmentSlotId) {
        return repository.findByAppointmentSlotId(tenantId, appointmentSlotId)
                .orElseThrow(() -> new ImagingNotFoundException(ImagingErrorCode.RECEPTION_NOT_FOUND, "Imaging reception intake for slot " + appointmentSlotId + " not found"));
    }
}
