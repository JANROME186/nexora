package com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain;

import java.util.List;
import java.util.Optional;

public interface ImagingStudyRepository {
    ImagingStudy save(ImagingStudy study);
    Optional<ImagingStudy> findById(String tenantId, String studyId);
    Optional<ImagingStudy> findByAccessionNumber(String tenantId, String accessionNumber);
    List<ImagingStudy> findByTenantAndPatient(String tenantId, String patientId);
}
