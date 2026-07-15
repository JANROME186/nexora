package com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionCatalogSelection;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionRequest;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionRequestRepository;

@Repository
@Profile("!local")
class InMemoryAdmissionRequestRepository implements AdmissionRequestRepository {

    private final Map<String, AdmissionRequest> admissions = new ConcurrentHashMap<>();
    private final Map<String, AdmissionCatalogSelection> selections = new ConcurrentHashMap<>();

    @Override
    public AdmissionRequest save(AdmissionRequest admission) {
        admissions.put(admission.admissionId(), admission);
        return admission;
    }

    @Override
    public Optional<AdmissionRequest> findById(String admissionId) {
        return Optional.ofNullable(admissions.get(admissionId));
    }

    @Override
    public List<AdmissionRequest> findByTenantId(String tenantId) {
        return admissions.values().stream().filter(a -> a.tenantId().equals(tenantId)).toList();
    }

    @Override
    public AdmissionCatalogSelection saveSelection(AdmissionCatalogSelection selection) {
        selections.put(selection.selectionId(), selection);
        return selection;
    }

    @Override
    public List<AdmissionCatalogSelection> findSelections(String admissionId) {
        return selections.values().stream().filter(s -> s.admissionId().equals(admissionId)).toList();
    }
}
