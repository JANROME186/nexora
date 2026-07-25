package com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.application;

import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.domain.RadiologyDictation;
import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.domain.RadiologyDictationRepository;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingErrorCode;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MedicalDictationService {

    private final RadiologyDictationRepository repository;

    public MedicalDictationService(RadiologyDictationRepository repository) {
        this.repository = repository;
    }

    public RadiologyDictation createDictation(
            String tenantId,
            String studyId,
            String dictationText,
            String audioReferenceUrl,
            String actorId) {
        String dictationId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        RadiologyDictation dictation = new RadiologyDictation(
                dictationId, tenantId, studyId, actorId, dictationText, audioReferenceUrl,
                "COMPLETED", actorId, now, actorId, now
        );
        return repository.save(dictation);
    }

    public RadiologyDictation getDictation(String tenantId, String dictationId) {
        return repository.findById(tenantId, dictationId)
                .orElseThrow(() -> new ImagingNotFoundException(ImagingErrorCode.DICTATION_NOT_FOUND, "Radiology dictation " + dictationId + " not found"));
    }

    public List<RadiologyDictation> listDictationsForStudy(String tenantId, String studyId) {
        return repository.findByStudyId(tenantId, studyId);
    }
}
