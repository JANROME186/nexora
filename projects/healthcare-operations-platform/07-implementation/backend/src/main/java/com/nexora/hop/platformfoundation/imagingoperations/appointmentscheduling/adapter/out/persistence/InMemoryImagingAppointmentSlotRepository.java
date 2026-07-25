package com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.domain.ImagingAppointmentSlot;
import com.nexora.hop.platformfoundation.imagingoperations.appointmentscheduling.domain.ImagingAppointmentSlotRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
public class InMemoryImagingAppointmentSlotRepository implements ImagingAppointmentSlotRepository {

    private final Map<String, ImagingAppointmentSlot> store = new ConcurrentHashMap<>();

    @Override
    public ImagingAppointmentSlot save(ImagingAppointmentSlot slot) {
        store.put(slot.tenantId() + ":" + slot.slotId(), slot);
        return slot;
    }

    @Override
    public Optional<ImagingAppointmentSlot> findById(String tenantId, String slotId) {
        return Optional.ofNullable(store.get(tenantId + ":" + slotId));
    }

    @Override
    public List<ImagingAppointmentSlot> findByTenantAndPatient(String tenantId, String patientId) {
        return store.values().stream()
                .filter(s -> s.tenantId().equals(tenantId) && s.patientId().equals(patientId))
                .toList();
    }

    @Override
    public List<ImagingAppointmentSlot> findOverlappingRoomSlots(String tenantId, String procedureRoomId, Instant startTime, Instant endTime) {
        return store.values().stream()
                .filter(s -> s.tenantId().equals(tenantId)
                        && s.procedureRoomId().equals(procedureRoomId)
                        && !"CANCELLED".equalsIgnoreCase(s.slotStatus())
                        && s.startTime().isBefore(endTime)
                        && s.endTime().isAfter(startTime))
                .toList();
    }
}
