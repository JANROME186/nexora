package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QualityEventIntakeRepository {
    QualityEventIntake save(QualityEventIntake event);
    Optional<QualityEventIntake> findById(UUID id);
    List<QualityEventIntake> findAll(String sourceSystem, String severity);
}
