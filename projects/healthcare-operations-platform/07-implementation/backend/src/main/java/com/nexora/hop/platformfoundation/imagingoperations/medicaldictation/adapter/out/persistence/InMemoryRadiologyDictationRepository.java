package com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.domain.RadiologyDictation;
import com.nexora.hop.platformfoundation.imagingoperations.medicaldictation.domain.RadiologyDictationRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
public class InMemoryRadiologyDictationRepository implements RadiologyDictationRepository {

    private final Map<String, RadiologyDictation> store = new ConcurrentHashMap<>();

    @Override
    public RadiologyDictation save(RadiologyDictation dictation) {
        store.put(dictation.tenantId() + ":" + dictation.dictationId(), dictation);
        return dictation;
    }

    @Override
    public Optional<RadiologyDictation> findById(String tenantId, String dictationId) {
        return Optional.ofNullable(store.get(tenantId + ":" + dictationId));
    }

    @Override
    public List<RadiologyDictation> findByStudyId(String tenantId, String studyId) {
        return store.values().stream()
                .filter(d -> d.tenantId().equals(tenantId) && d.studyId().equals(studyId))
                .toList();
    }
}
