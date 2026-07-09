package com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain;

import java.util.List;
import java.util.Optional;

public interface TestDefinitionRepository {

    TestDefinition save(TestDefinition test);

    Optional<TestDefinition> findById(String testDefinitionId);

    List<TestDefinition> findByLaboratoryId(String laboratoryId);

    boolean existsByCode(String laboratoryId, String code, String excludeTestDefinitionId);

    void replaceAnalyteLinks(String testDefinitionId, List<TestAnalyteLink> links);

    List<TestAnalyteLink> findAnalyteLinks(String testDefinitionId);

    void replaceSampleRequirementLinks(String testDefinitionId, List<TestSampleRequirementLink> links);

    List<TestSampleRequirementLink> findSampleRequirementLinks(String testDefinitionId);
}
