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
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogCustomRuleNotImplementedException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles generatable outputs from bcm-svc-004-analyte-catalog/generation-plan.yaml.
 * Custom points CUS-SVC-004-01..03 (versioning, data type ripple review, published snapshot)
 * are hooks deferred to MVP-MOD-002-BE-002.
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
            throw new CatalogCustomRuleNotImplementedException(
                    "RN-004",
                    "A published analyte is immutable; editing it requires the versioning, snapshot-freeze "
                            + "and dependent ripple review behavior reserved for MVP-MOD-002-BE-002.");
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

    public AnalyteDefinition publish(String analyteId) {
        require(analyteId);
        throw new CatalogCustomRuleNotImplementedException(
                "RN-003",
                "Publishing an analyte requires coded value completeness validation, snapshot freeze and "
                        + "dependent ripple flagging reserved for MVP-MOD-002-BE-002.");
    }

    public AnalyteDefinition getPublishedSnapshot(String analyteId) {
        require(analyteId);
        throw new CatalogCustomRuleNotImplementedException(
                "CUS-SVC-004-03",
                "The published analyte snapshot projection is reserved for MVP-MOD-002-BE-002.");
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
