package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExternalQualityEvaluationRepository {
    ExternalQualityEvaluation save(ExternalQualityEvaluation evaluation);
    Optional<ExternalQualityEvaluation> findById(UUID id);
    List<ExternalQualityEvaluation> findAll(String programCode, String rating);
}
