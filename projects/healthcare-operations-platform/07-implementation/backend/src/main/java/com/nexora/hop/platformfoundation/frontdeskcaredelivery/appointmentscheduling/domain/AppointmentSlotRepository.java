package com.nexora.hop.platformfoundation.frontdeskcaredelivery.appointmentscheduling.domain;

import java.util.List;
import java.util.Optional;

public interface AppointmentSlotRepository {

    AppointmentSlot save(AppointmentSlot appointment);

    Optional<AppointmentSlot> findById(String appointmentId);

    List<AppointmentSlot> findByTenantId(String tenantId);

    List<AppointmentSlot> findByPatientAndBranch(String patientId, String branchId);

    List<AppointmentSlot> findByBranchId(String branchId);

    RequestedCatalogItem saveRequestedItem(RequestedCatalogItem item);

    List<RequestedCatalogItem> findRequestedItems(String appointmentId);
}
