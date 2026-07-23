package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityEvaluation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityEvaluationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("local | test")
public class InMemoryExternalQualityEvaluationRepository implements ExternalQualityEvaluationRepository {

    private final Map<UUID, ExternalQualityEvaluation> store = new ConcurrentHashMap<>();

    @Override
    public ExternalQualityEvaluation save(ExternalQualityEvaluation evaluation) {
        store.put(evaluation.getEvaluationId(), evaluation);
        return evaluation;
    }

    @Override
    public Optional<ExternalQualityEvaluation> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ExternalQualityEvaluation> findAll(String programCode, String rating) {
        return store.values().stream()
                .filter(e -> programCode == null || programCode.isBlank() || e.getProgramCode().equalsIgnoreCase(programCode.trim()))
                .filter(e -> rating == null || rating.isBlank() || e.getPerformanceRating().name().equalsIgnoreCase(rating.trim()))
                .toList();
    }
}
