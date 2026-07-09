package com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.application;

import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationAssignment;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationInstruction;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationInstructionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogCustomRuleNotImplementedException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles generatable outputs from bcm-svc-005-patient-preparation-management/generation-plan.yaml.
 * Custom points CUS-SVC-005-01..03 (assignment target validation, versioning, patient-facing
 * snapshot) are hooks deferred to MVP-MOD-002-BE-002.
 */
@Service
public class PatientPreparationManagementService {

    private static final List<String> CATEGORIES = List.of(
            PreparationInstruction.CATEGORY_FASTING, PreparationInstruction.CATEGORY_MEDICATION,
            PreparationInstruction.CATEGORY_ACTIVITY, PreparationInstruction.CATEGORY_TIMING,
            PreparationInstruction.CATEGORY_HYDRATION, PreparationInstruction.CATEGORY_OTHER);

    private final PreparationInstructionRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public PatientPreparationManagementService(
            PreparationInstructionRepository repository, TenantDirectory tenantDirectory, AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    PatientPreparationManagementService(
            PreparationInstructionRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public PreparationInstruction create(CreatePreparationInstructionCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String code = requiredText(command.code(), "Preparation code is required.");
        String titleEn = requiredText(command.titleEn(), "English title is required.");
        String titleEs = requiredText(command.titleEs(), "Spanish title is required.");
        String textEn = requiredText(command.instructionTextEn(), "English instruction text is required.");
        String textEs = requiredText(command.instructionTextEs(), "Spanish instruction text is required.");
        String category = requiredOneOf(command.category(), "Preparation category is invalid.",
                CATEGORIES.toArray(String[]::new));

        if (PreparationInstruction.CATEGORY_FASTING.equals(category) && command.durationHours() == null) {
            throw new InvalidCatalogCommandException("A fasting preparation must declare a duration in hours.");
        }
        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }
        if (repository.existsByCode(laboratoryId, code, null)) {
            throw new InvalidCatalogCommandException("Preparation code already exists in this laboratory.");
        }

        Instant now = Instant.now(clock);
        PreparationInstruction preparation = new PreparationInstruction(
                newId(), tenantId, laboratoryId, code, new LocalizedText(titleEn, titleEs),
                new LocalizedText(textEn, textEs), category, command.durationHours(),
                PreparationInstruction.STATUS_DRAFT, 1, now, now);
        PreparationInstruction saved = repository.save(preparation);

        auditRecorder.recordSystemEvent(tenantId, "PreparationCreated", "PreparationInstruction",
                saved.preparationId(), "{\"code\":\"%s\"}".formatted(jsonText(saved.code())));
        return saved;
    }

    public PreparationInstruction update(String preparationId, UpdatePreparationInstructionCommand command) {
        PreparationInstruction current = require(preparationId);
        if (!PreparationInstruction.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogCustomRuleNotImplementedException(
                    "RN-005",
                    "A published preparation is immutable; editing it requires the versioning and "
                            + "snapshot-freeze behavior reserved for MVP-MOD-002-BE-002.");
        }

        String code = requiredText(command.code(), "Preparation code is required.");
        String titleEn = requiredText(command.titleEn(), "English title is required.");
        String titleEs = requiredText(command.titleEs(), "Spanish title is required.");
        String textEn = requiredText(command.instructionTextEn(), "English instruction text is required.");
        String textEs = requiredText(command.instructionTextEs(), "Spanish instruction text is required.");
        String category = requiredOneOf(command.category(), "Preparation category is invalid.",
                CATEGORIES.toArray(String[]::new));

        if (PreparationInstruction.CATEGORY_FASTING.equals(category) && command.durationHours() == null) {
            throw new InvalidCatalogCommandException("A fasting preparation must declare a duration in hours.");
        }
        if (repository.existsByCode(current.laboratoryId(), code, current.preparationId())) {
            throw new InvalidCatalogCommandException("Preparation code already exists in this laboratory.");
        }

        PreparationInstruction updated = new PreparationInstruction(
                current.preparationId(), current.tenantId(), current.laboratoryId(), code,
                new LocalizedText(titleEn, titleEs), new LocalizedText(textEn, textEs), category,
                command.durationHours(), current.status(), current.version(), current.createdAt(), Instant.now(clock));
        return repository.save(updated);
    }

    public PreparationInstruction deprecate(String preparationId) {
        PreparationInstruction current = require(preparationId);
        if (PreparationInstruction.STATUS_RETIRED.equals(current.status())) {
            throw new InvalidCatalogCommandException("A retired preparation cannot be deprecated.");
        }
        PreparationInstruction deprecated = new PreparationInstruction(
                current.preparationId(), current.tenantId(), current.laboratoryId(), current.code(),
                current.title(), current.instructionText(), current.category(), current.durationHours(),
                PreparationInstruction.STATUS_DEPRECATED, current.version(), current.createdAt(), Instant.now(clock));
        return repository.save(deprecated);
    }

    public PreparationInstruction publish(String preparationId) {
        require(preparationId);
        throw new CatalogCustomRuleNotImplementedException(
                "RN-002",
                "Publishing a preparation requires localization completeness validation and snapshot "
                        + "freeze reserved for MVP-MOD-002-BE-002.");
    }

    public PreparationAssignment assign(String preparationId, AssignPreparationCommand command) {
        require(preparationId);
        throw new CatalogCustomRuleNotImplementedException(
                "RN-004",
                "Assigning a preparation requires cross-aggregate target publication validation "
                        + "reserved for MVP-MOD-002-BE-002.");
    }

    public PreparationInstruction get(String preparationId) {
        return require(preparationId);
    }

    public List<PreparationAssignment> getAssignments(String preparationId) {
        require(preparationId);
        return repository.findAssignments(preparationId);
    }

    public List<PreparationInstruction> list(String laboratoryId) {
        return repository.findByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    private PreparationInstruction require(String preparationId) {
        return repository.findById(requiredText(preparationId, "Preparation id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Preparation was not found."));
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
