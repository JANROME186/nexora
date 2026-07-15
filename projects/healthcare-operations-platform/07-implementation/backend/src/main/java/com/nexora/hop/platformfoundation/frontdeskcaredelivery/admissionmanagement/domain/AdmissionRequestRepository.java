package com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface AdmissionRequestRepository {

    AdmissionRequest save(AdmissionRequest admission);

    Optional<AdmissionRequest> findById(String admissionId);

    List<AdmissionRequest> findByTenantId(String tenantId);

    AdmissionCatalogSelection saveSelection(AdmissionCatalogSelection selection);

    List<AdmissionCatalogSelection> findSelections(String admissionId);
}
