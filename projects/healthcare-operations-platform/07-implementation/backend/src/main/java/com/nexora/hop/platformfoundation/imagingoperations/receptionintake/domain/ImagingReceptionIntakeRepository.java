package com.nexora.hop.platformfoundation.imagingoperations.receptionintake.domain;

import java.util.Optional;

public interface ImagingReceptionIntakeRepository {
    ImagingReceptionIntake save(ImagingReceptionIntake intake);
    Optional<ImagingReceptionIntake> findById(String tenantId, String intakeId);
    Optional<ImagingReceptionIntake> findByAppointmentSlotId(String tenantId, String appointmentSlotId);
}
