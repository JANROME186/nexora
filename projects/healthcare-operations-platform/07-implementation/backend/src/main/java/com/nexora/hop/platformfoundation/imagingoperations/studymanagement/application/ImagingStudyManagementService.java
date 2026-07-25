package com.nexora.hop.platformfoundation.imagingoperations.studymanagement.application;

import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingDomainException;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingErrorCode;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingNotFoundException;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain.ImagingStudy;
import com.nexora.hop.platformfoundation.imagingoperations.studymanagement.domain.ImagingStudyRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ImagingStudyManagementService {

    private final ImagingStudyRepository repository;

    public ImagingStudyManagementService(ImagingStudyRepository repository) {
        this.repository = repository;
    }

    public ImagingStudy createStudy(
            String tenantId,
            String accessionNumber,
            String patientId,
            String modality,
            String studyDescription,
            String actorId) {
        if (repository.findByAccessionNumber(tenantId, accessionNumber).isPresent()) {
            throw new ImagingDomainException(ImagingErrorCode.ACCESSION_EXISTS, "Accession number " + accessionNumber + " already exists for tenant");
        }

        String studyId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        ImagingStudy study = new ImagingStudy(
                studyId, tenantId, accessionNumber, patientId, modality, studyDescription,
                "ORDERED", 0, 0, now, actorId, now, actorId, now
        );
        return repository.save(study);
    }

    public ImagingStudy getStudy(String tenantId, String studyId) {
        return repository.findById(tenantId, studyId)
                .orElseThrow(() -> new ImagingNotFoundException(ImagingErrorCode.STUDY_NOT_FOUND, "Imaging study " + studyId + " not found"));
    }

    public ImagingStudy getStudyByAccession(String tenantId, String accessionNumber) {
        return repository.findByAccessionNumber(tenantId, accessionNumber)
                .orElseThrow(() -> new ImagingNotFoundException(ImagingErrorCode.STUDY_NOT_FOUND, "Imaging study with accession " + accessionNumber + " not found"));
    }

    public List<ImagingStudy> listStudiesForPatient(String tenantId, String patientId) {
        return repository.findByTenantAndPatient(tenantId, patientId);
    }

    public ImagingStudy updateStudyCountsAndStatus(
            String tenantId,
            String studyId,
            int seriesCount,
            int instanceCount,
            String status,
            String actorId) {
        ImagingStudy existing = getStudy(tenantId, studyId);
        ImagingStudy updated = new ImagingStudy(
                existing.studyId(), existing.tenantId(), existing.accessionNumber(), existing.patientId(),
                existing.modality(), existing.studyDescription(), status, seriesCount, instanceCount,
                existing.studyDate(), existing.createdBy(), existing.createdAt(), actorId, Instant.now()
        );
        return repository.save(updated);
    }
}
