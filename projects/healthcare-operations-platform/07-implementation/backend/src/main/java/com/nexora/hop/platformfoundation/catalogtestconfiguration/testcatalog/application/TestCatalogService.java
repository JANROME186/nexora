package com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.application;

import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.optionalText;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogCustomRuleNotImplementedException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestAnalyteLink;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinitionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestSampleRequirementLink;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles generatable outputs from bcm-svc-002-test-catalog/generation-plan.yaml.
 * Custom points CUS-SVC-002-01..04 (publication cross-validation, versioning, analyte
 * publication check, published snapshot) are hooks deferred to MVP-MOD-002-BE-002.
 */
@Service
public class TestCatalogService {

    private static final List<String> RESULT_TYPES = List.of(
            TestDefinition.RESULT_NUMERIC, TestDefinition.RESULT_QUALITATIVE,
            TestDefinition.RESULT_SEMI_QUANTITATIVE, TestDefinition.RESULT_TEXT, TestDefinition.RESULT_STRUCTURED);

    private final TestDefinitionRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public TestCatalogService(
            TestDefinitionRepository repository, TenantDirectory tenantDirectory, AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    TestCatalogService(
            TestDefinitionRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public TestDefinition create(CreateTestDefinitionCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String code = requiredText(command.code(), "Test code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        String resultType = requiredOneOf(command.resultType(), "Result type is invalid.",
                RESULT_TYPES.toArray(String[]::new));
        String measurementUnit = optionalText(command.measurementUnit());

        if (TestDefinition.RESULT_NUMERIC.equals(resultType) && !StringUtils.hasText(measurementUnit)) {
            throw new InvalidCatalogCommandException("A numeric result type test must declare a measurement unit.");
        }
        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }
        if (repository.existsByCode(laboratoryId, code, null)) {
            throw new InvalidCatalogCommandException("Test code already exists in this laboratory.");
        }

        Instant now = Instant.now(clock);
        TestDefinition test = new TestDefinition(
                newId(), tenantId, laboratoryId, code, new LocalizedText(nameEn, nameEs),
                optionalText(command.methodology()), measurementUnit, resultType, command.turnaroundTimeHours(),
                TestDefinition.STATUS_DRAFT, 1, now, now);
        TestDefinition saved = repository.save(test);
        repository.replaceAnalyteLinks(saved.testDefinitionId(), toAnalyteLinks(saved.testDefinitionId(), command.analyteRefIds()));
        repository.replaceSampleRequirementLinks(
                saved.testDefinitionId(), toSampleLinks(saved.testDefinitionId(), command.sampleRequirementRefIds()));

        auditRecorder.recordSystemEvent(tenantId, "TestDefinitionCreated", "TestDefinition", saved.testDefinitionId(),
                "{\"code\":\"%s\"}".formatted(jsonText(saved.code())));
        return saved;
    }

    public TestDefinition update(String testDefinitionId, UpdateTestDefinitionCommand command) {
        TestDefinition current = require(testDefinitionId);
        if (!TestDefinition.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogCustomRuleNotImplementedException(
                    "RN-004",
                    "A published test definition is immutable; editing it requires the versioning and "
                            + "snapshot-freeze behavior reserved for MVP-MOD-002-BE-002.");
        }

        String code = requiredText(command.code(), "Test code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        String resultType = requiredOneOf(command.resultType(), "Result type is invalid.",
                RESULT_TYPES.toArray(String[]::new));
        String measurementUnit = optionalText(command.measurementUnit());

        if (TestDefinition.RESULT_NUMERIC.equals(resultType) && !StringUtils.hasText(measurementUnit)) {
            throw new InvalidCatalogCommandException("A numeric result type test must declare a measurement unit.");
        }
        if (repository.existsByCode(current.laboratoryId(), code, current.testDefinitionId())) {
            throw new InvalidCatalogCommandException("Test code already exists in this laboratory.");
        }

        TestDefinition updated = new TestDefinition(
                current.testDefinitionId(), current.tenantId(), current.laboratoryId(), code,
                new LocalizedText(nameEn, nameEs), optionalText(command.methodology()), measurementUnit, resultType,
                command.turnaroundTimeHours(), current.status(), current.version(), current.createdAt(),
                Instant.now(clock));
        TestDefinition saved = repository.save(updated);
        repository.replaceAnalyteLinks(saved.testDefinitionId(), toAnalyteLinks(saved.testDefinitionId(), command.analyteRefIds()));
        repository.replaceSampleRequirementLinks(
                saved.testDefinitionId(), toSampleLinks(saved.testDefinitionId(), command.sampleRequirementRefIds()));
        return saved;
    }

    public TestDefinition deprecate(String testDefinitionId) {
        TestDefinition current = require(testDefinitionId);
        if (TestDefinition.STATUS_RETIRED.equals(current.status())) {
            throw new InvalidCatalogCommandException("A retired test definition cannot be deprecated.");
        }
        TestDefinition deprecated = new TestDefinition(
                current.testDefinitionId(), current.tenantId(), current.laboratoryId(), current.code(),
                current.name(), current.methodology(), current.measurementUnit(), current.resultType(),
                current.turnaroundTimeHours(), TestDefinition.STATUS_DEPRECATED, current.version(),
                current.createdAt(), Instant.now(clock));
        TestDefinition saved = repository.save(deprecated);
        auditRecorder.recordSystemEvent(saved.tenantId(), "TestDefinitionDeprecated", "TestDefinition",
                saved.testDefinitionId(), "{}");
        return saved;
    }

    public TestDefinition publish(String testDefinitionId) {
        require(testDefinitionId);
        throw new CatalogCustomRuleNotImplementedException(
                "RN-003",
                "Publishing a test definition requires cross-aggregate sample requirement and analyte "
                        + "publication validation reserved for MVP-MOD-002-BE-002.");
    }

    public TestDefinition getPublishedSnapshot(String testDefinitionId) {
        require(testDefinitionId);
        throw new CatalogCustomRuleNotImplementedException(
                "CUS-SVC-002-04",
                "The published test snapshot projection is reserved for MVP-MOD-002-BE-002.");
    }

    public TestDefinition get(String testDefinitionId) {
        return require(testDefinitionId);
    }

    public List<TestAnalyteLink> getAnalyteLinks(String testDefinitionId) {
        require(testDefinitionId);
        return repository.findAnalyteLinks(testDefinitionId);
    }

    public List<TestSampleRequirementLink> getSampleRequirementLinks(String testDefinitionId) {
        require(testDefinitionId);
        return repository.findSampleRequirementLinks(testDefinitionId);
    }

    public List<TestDefinition> list(String laboratoryId) {
        return repository.findByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    private TestDefinition require(String testDefinitionId) {
        return repository.findById(requiredText(testDefinitionId, "Test definition id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Test definition was not found."));
    }

    private static List<TestAnalyteLink> toAnalyteLinks(String testDefinitionId, List<String> analyteRefIds) {
        if (analyteRefIds == null) {
            return List.of();
        }
        return analyteRefIds.stream()
                .map(refId -> new TestAnalyteLink(newId(), testDefinitionId,
                        requiredText(refId, "Analyte reference id is required."), null))
                .toList();
    }

    private static List<TestSampleRequirementLink> toSampleLinks(String testDefinitionId, List<String> refIds) {
        if (refIds == null) {
            return List.of();
        }
        return refIds.stream()
                .map(refId -> new TestSampleRequirementLink(newId(), testDefinitionId,
                        requiredText(refId, "Sample requirement reference id is required.")))
                .toList();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
