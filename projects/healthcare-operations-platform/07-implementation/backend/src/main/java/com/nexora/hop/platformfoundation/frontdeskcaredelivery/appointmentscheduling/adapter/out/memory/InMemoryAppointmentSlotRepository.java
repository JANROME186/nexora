package com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.AppointmentSlot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.AppointmentSlotRepository;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain.RequestedCatalogItem;

@Repository
@Profile("!local")
class InMemoryAppointmentSlotRepository implements AppointmentSlotRepository {

    private final Map<String, AppointmentSlot> appointments = new ConcurrentHashMap<>();
    private final Map<String, RequestedCatalogItem> requestedItems = new ConcurrentHashMap<>();

    @Override
    public AppointmentSlot save(AppointmentSlot appointment) {
        appointments.put(appointment.appointmentId(), appointment);
        return appointment;
    }

    @Override
    public Optional<AppointmentSlot> findById(String appointmentId) {
        return Optional.ofNullable(appointments.get(appointmentId));
    }

    @Override
    public List<AppointmentSlot> findByTenantId(String tenantId) {
        return appointments.values().stream().filter(a -> a.tenantId().equals(tenantId)).toList();
    }

    @Override
    public List<AppointmentSlot> findByPatientAndBranch(String patientId, String branchId) {
        return appointments.values().stream()
                .filter(a -> a.patientId().equals(patientId) && a.branchId().equals(branchId))
                .toList();
    }

    @Override
    public RequestedCatalogItem saveRequestedItem(RequestedCatalogItem item) {
        requestedItems.put(item.itemId(), item);
        return item;
    }

    @Override
    public List<RequestedCatalogItem> findRequestedItems(String appointmentId) {
        return requestedItems.values().stream().filter(i -> i.appointmentId().equals(appointmentId)).toList();
    }
}
