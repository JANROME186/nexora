package com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.domain;

import java.util.List;
import java.util.Optional;

public interface RadiologyDictationRepository {
    RadiologyDictation save(RadiologyDictation dictation);
    Optional<RadiologyDictation> findById(String tenantId, String dictationId);
    List<RadiologyDictation> findByStudyId(String tenantId, String studyId);
}
