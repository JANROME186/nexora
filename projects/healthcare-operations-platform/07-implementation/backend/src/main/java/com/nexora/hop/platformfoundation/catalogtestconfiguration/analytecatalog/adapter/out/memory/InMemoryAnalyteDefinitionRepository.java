package com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.adapter.out.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteCodedValue;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteDefinitionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteResultConstraint;

@Repository
@Profile("!local")
class InMemoryAnalyteDefinitionRepository implements AnalyteDefinitionRepository {

    private final Map<String, AnalyteDefinition> analytes = new ConcurrentHashMap<>();
    private final Map<String, AnalyteResultConstraint> constraints = new ConcurrentHashMap<>();
    private final Map<String, List<AnalyteCodedValue>> codedValues = new ConcurrentHashMap<>();

    @Override
    public AnalyteDefinition save(AnalyteDefinition analyte) {
        analytes.put(analyte.analyteId(), analyte);
        return analyte;
    }

    @Override
    public Optional<AnalyteDefinition> findById(String analyteId) {
        return Optional.ofNullable(analytes.get(analyteId));
    }

    @Override
    public List<AnalyteDefinition> findByLaboratoryId(String laboratoryId) {
        return analytes.values().stream().filter(analyte -> analyte.laboratoryId().equals(laboratoryId)).toList();
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludeAnalyteId) {
        return analytes.values().stream()
                .anyMatch(analyte -> analyte.laboratoryId().equals(laboratoryId)
                        && analyte.code().equals(code)
                        && !analyte.analyteId().equals(excludeAnalyteId));
    }

    @Override
    public void saveConstraint(AnalyteResultConstraint constraint) {
        constraints.put(constraint.analyteId(), constraint);
    }

    @Override
    public Optional<AnalyteResultConstraint> findConstraint(String analyteId) {
        return Optional.ofNullable(constraints.get(analyteId));
    }

    @Override
    public void replaceCodedValues(String analyteId, List<AnalyteCodedValue> values) {
        codedValues.put(analyteId, new ArrayList<>(values));
    }

    @Override
    public List<AnalyteCodedValue> findCodedValues(String analyteId) {
        return List.copyOf(codedValues.getOrDefault(analyteId, List.of()));
    }
}
