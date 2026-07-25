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
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogConflictException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestAnalyteLink;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinitionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestSampleRequirementLink;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles generatable outputs from bcm-svc-002-test-catalog/generation-plan.md and implements
 * the custom rules CUS-SVC-002-01..04 (publication cross-validation of analytes and sample
 * requirements, immutable versioning and the published snapshot) delivered by MVP-MOD-002-BE-002.
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

        requireMeasurementUnit(resultType, measurementUnit);
        requireTenantExists(tenantId);
        requireUniqueCode(laboratoryId, code, null);

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
            throw new CatalogConflictException(
                    "A published test definition is immutable. Its published snapshot is the source of truth; "
                            + "create a new draft version instead of editing it directly (RN-004).");
        }

        String code = requiredText(command.code(), "Test code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        String resultType = requiredOneOf(command.resultType(), "Result type is invalid.",
                RESULT_TYPES.toArray(String[]::new));
        String measurementUnit = optionalText(command.measurementUnit());

        requireMeasurementUnit(resultType, measurementUnit);
        requireUniqueCode(current.laboratoryId(), code, current.testDefinitionId());

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

    /**
     * RN-003 publication rule: a test can only be published from draft, and it must declare the
     * analytes it measures and the sample requirements it consumes. Publishing freezes the record;
     * the published record is the immutable snapshot returned by {@link #getPublishedSnapshot}.
     */
    public TestDefinition publish(String testDefinitionId) {
        TestDefinition current = require(testDefinitionId);
        if (!TestDefinition.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "Only a draft test definition can be published (current status: " + current.status() + ").");
        }
        if (repository.findAnalyteLinks(current.testDefinitionId()).isEmpty()) {
            throw new InvalidCatalogCommandException(
                    "A test definition must reference at least one analyte before publication.");
        }
        if (repository.findSampleRequirementLinks(current.testDefinitionId()).isEmpty()) {
            throw new InvalidCatalogCommandException(
                    "A test definition must reference at least one sample requirement before publication.");
        }

        TestDefinition published = new TestDefinition(
                current.testDefinitionId(), current.tenantId(), current.laboratoryId(), current.code(),
                current.name(), current.methodology(), current.measurementUnit(), current.resultType(),
                current.turnaroundTimeHours(), TestDefinition.STATUS_PUBLISHED, current.version(),
                current.createdAt(), Instant.now(clock));
        TestDefinition saved = repository.save(published);
        auditRecorder.recordSystemEvent(saved.tenantId(), "TestDefinitionPublished", "TestDefinition",
                saved.testDefinitionId(), "{\"version\":%d}".formatted(saved.version()));
        return saved;
    }

    /** CUS-SVC-002-04: the immutable published snapshot projection of a test definition. */
    public TestDefinition getPublishedSnapshot(String testDefinitionId) {
        TestDefinition current = require(testDefinitionId);
        if (TestDefinition.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogEntityNotFoundException(
                    "This test definition has no published snapshot; it has never been published.");
        }
        return current;
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

    /**
     * Published-only projection over {@link #list(String)} realizing BCM-SVC-002 {@code
     * listPublishedTests} for the COM-MOD-011 anonymous public catalog. Drafts, deprecated and
     * retired records are never returned; this is a filter, not a separate collection, so the
     * published snapshot remains the single source of truth (RN-004).
     */
    public List<TestDefinition> listPublished(String laboratoryId) {
        return list(laboratoryId).stream()
                .filter(entry -> TestDefinition.STATUS_PUBLISHED.equals(entry.status()))
                .toList();
    }

    private TestDefinition require(String testDefinitionId) {
        return repository.findById(requiredText(testDefinitionId, "Test definition id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Test definition was not found."));
    }

    private void requireTenantExists(String tenantId) {
        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }
    }

    private void requireUniqueCode(String laboratoryId, String code, String testDefinitionIdToExclude) {
        if (repository.existsByCode(laboratoryId, code, testDefinitionIdToExclude)) {
            throw new InvalidCatalogCommandException("Test code already exists in this laboratory.");
        }
    }

    private static void requireMeasurementUnit(String resultType, String measurementUnit) {
        if (TestDefinition.RESULT_NUMERIC.equals(resultType) && !StringUtils.hasText(measurementUnit)) {
            throw new InvalidCatalogCommandException("A numeric result type test must declare a measurement unit.");
        }
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
