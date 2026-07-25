package com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.application;

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
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteCodedValue;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteDefinitionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteResultConstraint;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogConflictException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles generatable outputs from bcm-svc-004-analyte-catalog/generation-plan.md and implements
 * the custom rules CUS-SVC-004-01..03 (completeness-gated publication, immutable versioning and the
 * published snapshot) delivered by MVP-MOD-002-BE-002.
 */
@Service
public class AnalyteCatalogService {

    private static final List<String> RESULT_DATA_TYPES = List.of(
            AnalyteDefinition.TYPE_NUMERIC, AnalyteDefinition.TYPE_QUALITATIVE,
            AnalyteDefinition.TYPE_SEMI_QUANTITATIVE, AnalyteDefinition.TYPE_TEXT, AnalyteDefinition.TYPE_CODED);

    private final AnalyteDefinitionRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public AnalyteCatalogService(
            AnalyteDefinitionRepository repository, TenantDirectory tenantDirectory, AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    AnalyteCatalogService(
            AnalyteDefinitionRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public AnalyteDefinition create(CreateAnalyteDefinitionCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String code = requiredText(command.code(), "Analyte code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        String resultDataType = requiredOneOf(command.resultDataType(), "Result data type is invalid.",
                RESULT_DATA_TYPES.toArray(String[]::new));

        validateNumericRequirements(resultDataType, command.measurementUnit(), command.decimalPrecision());
        validateConstraintConsistency(command.minValue(), command.maxValue());

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }
        if (repository.existsByCode(laboratoryId, code, null)) {
            throw new InvalidCatalogCommandException("Analyte code already exists in this laboratory.");
        }

        Instant now = Instant.now(clock);
        AnalyteDefinition analyte = new AnalyteDefinition(
                newId(), tenantId, laboratoryId, code, new LocalizedText(nameEn, nameEs),
                optionalText(command.loincCode()), resultDataType, optionalText(command.measurementUnit()),
                command.decimalPrecision(), AnalyteDefinition.STATUS_DRAFT, 1, now, now);
        AnalyteDefinition saved = repository.save(analyte);
        saveConstraintAndCodedValues(saved.analyteId(), command.minValue(), command.maxValue(), command.codedValues());

        auditRecorder.recordSystemEvent(tenantId, "AnalyteCreated", "AnalyteDefinition", saved.analyteId(),
                "{\"code\":\"%s\"}".formatted(jsonText(saved.code())));
        return saved;
    }

    public AnalyteDefinition update(String analyteId, UpdateAnalyteDefinitionCommand command) {
        AnalyteDefinition current = require(analyteId);
        if (!AnalyteDefinition.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "A published analyte is immutable. Its published snapshot is the source of truth; create a "
                            + "new draft version instead of editing it directly (RN-004).");
        }

        String code = requiredText(command.code(), "Analyte code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        String resultDataType = requiredOneOf(command.resultDataType(), "Result data type is invalid.",
                RESULT_DATA_TYPES.toArray(String[]::new));

        validateNumericRequirements(resultDataType, command.measurementUnit(), command.decimalPrecision());
        validateConstraintConsistency(command.minValue(), command.maxValue());

        if (repository.existsByCode(current.laboratoryId(), code, current.analyteId())) {
            throw new InvalidCatalogCommandException("Analyte code already exists in this laboratory.");
        }

        AnalyteDefinition updated = new AnalyteDefinition(
                current.analyteId(), current.tenantId(), current.laboratoryId(), code, new LocalizedText(nameEn, nameEs),
                optionalText(command.loincCode()), resultDataType, optionalText(command.measurementUnit()),
                command.decimalPrecision(), current.status(), current.version(), current.createdAt(),
                Instant.now(clock));
        AnalyteDefinition saved = repository.save(updated);
        saveConstraintAndCodedValues(saved.analyteId(), command.minValue(), command.maxValue(), command.codedValues());
        return saved;
    }

    public AnalyteDefinition deprecate(String analyteId) {
        AnalyteDefinition current = require(analyteId);
        if (AnalyteDefinition.STATUS_RETIRED.equals(current.status())) {
            throw new InvalidCatalogCommandException("A retired analyte cannot be deprecated.");
        }
        AnalyteDefinition deprecated = new AnalyteDefinition(
                current.analyteId(), current.tenantId(), current.laboratoryId(), current.code(), current.name(),
                current.loincCode(), current.resultDataType(), current.measurementUnit(), current.decimalPrecision(),
                AnalyteDefinition.STATUS_DEPRECATED, current.version(), current.createdAt(), Instant.now(clock));
        return repository.save(deprecated);
    }

    /**
     * RN-003/RN-006 publication rule: only a complete analyte can be published from draft. A
     * numeric analyte must declare a measurement unit and decimal precision; a coded analyte must
     * declare at least one coded value. Publishing freezes the record; the published record is the
     * immutable snapshot returned by {@link #getPublishedSnapshot}.
     */
    public AnalyteDefinition publish(String analyteId) {
        AnalyteDefinition current = require(analyteId);
        if (!AnalyteDefinition.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "Only a draft analyte can be published (current status: " + current.status() + ").");
        }
        validateNumericRequirements(current.resultDataType(), current.measurementUnit(), current.decimalPrecision());
        if (AnalyteDefinition.TYPE_CODED.equals(current.resultDataType())
                && repository.findCodedValues(current.analyteId()).isEmpty()) {
            throw new InvalidCatalogCommandException(
                    "A coded analyte must declare at least one coded value before publication.");
        }

        AnalyteDefinition published = new AnalyteDefinition(
                current.analyteId(), current.tenantId(), current.laboratoryId(), current.code(), current.name(),
                current.loincCode(), current.resultDataType(), current.measurementUnit(), current.decimalPrecision(),
                AnalyteDefinition.STATUS_PUBLISHED, current.version(), current.createdAt(), Instant.now(clock));
        AnalyteDefinition saved = repository.save(published);
        auditRecorder.recordSystemEvent(saved.tenantId(), "AnalytePublished", "AnalyteDefinition",
                saved.analyteId(), "{\"version\":%d}".formatted(saved.version()));
        return saved;
    }

    /** CUS-SVC-004-03: the immutable published snapshot projection of an analyte. */
    public AnalyteDefinition getPublishedSnapshot(String analyteId) {
        AnalyteDefinition current = require(analyteId);
        if (AnalyteDefinition.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogEntityNotFoundException(
                    "This analyte has no published snapshot; it has never been published.");
        }
        return current;
    }

    public AnalyteDefinition get(String analyteId) {
        return require(analyteId);
    }

    public List<AnalyteDefinition> list(String laboratoryId) {
        return repository.findByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    public List<AnalyteCodedValue> getCodedValues(String analyteId) {
        require(analyteId);
        return repository.findCodedValues(analyteId);
    }

    public AnalyteResultConstraint getConstraint(String analyteId) {
        require(analyteId);
        return repository.findConstraint(analyteId).orElse(null);
    }

    private AnalyteDefinition require(String analyteId) {
        return repository.findById(requiredText(analyteId, "Analyte id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Analyte was not found."));
    }

    private static void validateNumericRequirements(String resultDataType, String measurementUnit, Integer decimalPrecision) {
        if (AnalyteDefinition.TYPE_NUMERIC.equals(resultDataType)
                && (!StringUtils.hasText(measurementUnit) || decimalPrecision == null)) {
            throw new InvalidCatalogCommandException(
                    "A numeric analyte must declare a measurement unit and decimal precision.");
        }
    }

    private static void validateConstraintConsistency(java.math.BigDecimal minValue, java.math.BigDecimal maxValue) {
        if (minValue != null && maxValue != null && minValue.compareTo(maxValue) > 0) {
            throw new InvalidCatalogCommandException("Analyte result constraint min value must not exceed max value.");
        }
    }

    private void saveConstraintAndCodedValues(
            String analyteId,
            java.math.BigDecimal minValue,
            java.math.BigDecimal maxValue,
            List<CodedValueInput> codedValues) {
        List<String> codes = codedValues == null
                ? List.of()
                : codedValues.stream().map(CodedValueInput::code).toList();
        repository.saveConstraint(new AnalyteResultConstraint(newId(), analyteId, minValue, maxValue, codes));
        repository.replaceCodedValues(analyteId, toCodedValues(analyteId, codedValues));
    }

    private static List<AnalyteCodedValue> toCodedValues(String analyteId, List<CodedValueInput> inputs) {
        if (inputs == null) {
            return List.of();
        }
        return inputs.stream()
                .map(input -> new AnalyteCodedValue(
                        newId(), analyteId, requiredText(input.code(), "Coded value code is required."),
                        new LocalizedText(
                                requiredText(input.displayEn(), "Coded value English display is required."),
                                requiredText(input.displayEs(), "Coded value Spanish display is required."))))
                .toList();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
