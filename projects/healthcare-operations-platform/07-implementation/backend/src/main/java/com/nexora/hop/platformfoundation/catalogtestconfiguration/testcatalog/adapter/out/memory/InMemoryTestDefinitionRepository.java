package com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.adapter.out.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestAnalyteLink;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinitionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestSampleRequirementLink;

@Repository
@Profile("!local")
class InMemoryTestDefinitionRepository implements TestDefinitionRepository {

    private final Map<String, TestDefinition> tests = new ConcurrentHashMap<>();
    private final Map<String, List<TestAnalyteLink>> analyteLinks = new ConcurrentHashMap<>();
    private final Map<String, List<TestSampleRequirementLink>> sampleLinks = new ConcurrentHashMap<>();

    @Override
    public TestDefinition save(TestDefinition test) {
        tests.put(test.testDefinitionId(), test);
        return test;
    }

    @Override
    public Optional<TestDefinition> findById(String testDefinitionId) {
        return Optional.ofNullable(tests.get(testDefinitionId));
    }

    @Override
    public List<TestDefinition> findByLaboratoryId(String laboratoryId) {
        return tests.values().stream().filter(test -> test.laboratoryId().equals(laboratoryId)).toList();
    }

    @Override
    public boolean existsByCode(String laboratoryId, String code, String excludeTestDefinitionId) {
        return tests.values().stream()
                .anyMatch(test -> test.laboratoryId().equals(laboratoryId)
                        && test.code().equals(code)
                        && !test.testDefinitionId().equals(excludeTestDefinitionId));
    }

    @Override
    public void replaceAnalyteLinks(String testDefinitionId, List<TestAnalyteLink> links) {
        analyteLinks.put(testDefinitionId, new ArrayList<>(links));
    }

    @Override
    public List<TestAnalyteLink> findAnalyteLinks(String testDefinitionId) {
        return List.copyOf(analyteLinks.getOrDefault(testDefinitionId, List.of()));
    }

    @Override
    public void replaceSampleRequirementLinks(String testDefinitionId, List<TestSampleRequirementLink> links) {
        sampleLinks.put(testDefinitionId, new ArrayList<>(links));
    }

    @Override
    public List<TestSampleRequirementLink> findSampleRequirementLinks(String testDefinitionId) {
        return List.copyOf(sampleLinks.getOrDefault(testDefinitionId, List.of()));
    }
}
