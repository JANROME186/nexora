package com.nexora.hop.platformfoundation.imagingoperations.studydelivery.domain;

import java.util.List;
import java.util.Optional;

public interface ImagingDeliveryPackageRepository {
    ImagingDeliveryPackage save(ImagingDeliveryPackage deliveryPackage);
    Optional<ImagingDeliveryPackage> findById(String tenantId, String packageId);
    List<ImagingDeliveryPackage> findByStudyId(String tenantId, String studyId);
    List<ImagingDeliveryPackage> findByTenantAndPatient(String tenantId, String patientId);
}
