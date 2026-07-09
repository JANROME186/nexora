package com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain;

import java.util.List;
import java.util.Optional;

public interface AnalyteDefinitionRepository {

    AnalyteDefinition save(AnalyteDefinition analyte);

    Optional<AnalyteDefinition> findById(String analyteId);

    List<AnalyteDefinition> findByLaboratoryId(String laboratoryId);

    boolean existsByCode(String laboratoryId, String code, String excludeAnalyteId);

    void saveConstraint(AnalyteResultConstraint constraint);

    Optional<AnalyteResultConstraint> findConstraint(String analyteId);

    void replaceCodedValues(String analyteId, List<AnalyteCodedValue> codedValues);

    List<AnalyteCodedValue> findCodedValues(String analyteId);
}
