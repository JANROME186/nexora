package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CapaInvestigationRepository {
    CapaInvestigation save(CapaInvestigation capa);
    Optional<CapaInvestigation> findById(UUID id);
    List<CapaInvestigation> findAll(String status, String sourceCategory);
}
