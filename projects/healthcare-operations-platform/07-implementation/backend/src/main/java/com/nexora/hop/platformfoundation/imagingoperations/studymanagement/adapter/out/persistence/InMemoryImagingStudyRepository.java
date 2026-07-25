package com.nexora.hop.platformfoundation.imagingoperations.studymanagement.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain.ImagingStudy;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain.ImagingStudyRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
public class InMemoryImagingStudyRepository implements ImagingStudyRepository {

    private final Map<String, ImagingStudy> store = new ConcurrentHashMap<>();

    @Override
    public ImagingStudy save(ImagingStudy study) {
        store.put(study.tenantId() + ":" + study.studyId(), study);
        return study;
    }

    @Override
    public Optional<ImagingStudy> findById(String tenantId, String studyId) {
        return Optional.ofNullable(store.get(tenantId + ":" + studyId));
    }

    @Override
    public Optional<ImagingStudy> findByAccessionNumber(String tenantId, String accessionNumber) {
        return store.values().stream()
                .filter(s -> s.tenantId().equals(tenantId) && s.accessionNumber().equalsIgnoreCase(accessionNumber))
                .findFirst();
    }

    @Override
    public List<ImagingStudy> findByTenantAndPatient(String tenantId, String patientId) {
        return store.values().stream()
                .filter(s -> s.tenantId().equals(tenantId) && s.patientId().equals(patientId))
                .toList();
    }
}
