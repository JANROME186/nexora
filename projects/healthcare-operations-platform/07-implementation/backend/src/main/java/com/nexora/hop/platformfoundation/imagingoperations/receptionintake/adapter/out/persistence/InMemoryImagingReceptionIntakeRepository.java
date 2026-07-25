package com.nexora.hop.platformfoundation.imagingoperations.receptionintake.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.domain.ImagingReceptionIntake;
import com.nexora.hop.platformfoundation.imagingoperations.receptionintake.domain.ImagingReceptionIntakeRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
public class InMemoryImagingReceptionIntakeRepository implements ImagingReceptionIntakeRepository {

    private final Map<String, ImagingReceptionIntake> store = new ConcurrentHashMap<>();

    @Override
    public ImagingReceptionIntake save(ImagingReceptionIntake intake) {
        store.put(intake.tenantId() + ":" + intake.intakeId(), intake);
        return intake;
    }

    @Override
    public Optional<ImagingReceptionIntake> findById(String tenantId, String intakeId) {
        return Optional.ofNullable(store.get(tenantId + ":" + intakeId));
    }

    @Override
    public Optional<ImagingReceptionIntake> findByAppointmentSlotId(String tenantId, String appointmentSlotId) {
        return store.values().stream()
                .filter(i -> i.tenantId().equals(tenantId) && i.appointmentSlotId().equals(appointmentSlotId))
                .findFirst();
    }
}
