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
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogConflictException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles generatable outputs from bcm-svc-005-patient-preparation-management/generation-plan.yaml
 * and implements the custom rules CUS-SVC-005-01..03 (localization-gated publication, published
 * preparation assignment and immutable versioning) delivered by MVP-MOD-002-BE-002.
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
            throw new CatalogConflictException(
                    "A published preparation is immutable. Its published snapshot is the source of truth; create "
                            + "a new draft version instead of editing it directly (RN-005).");
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

    /**
     * RN-002 publication rule: a preparation can only be published from draft and must be fully
     * localized (title and instruction text in every supported language). A fasting preparation
     * must also declare its duration. Publishing freezes the localized patient-facing content.
     */
    public PreparationInstruction publish(String preparationId) {
        PreparationInstruction current = require(preparationId);
        if (!PreparationInstruction.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "Only a draft preparation can be published (current status: " + current.status() + ").");
        }
        requireLocalized(current.title(), "title");
        requireLocalized(current.instructionText(), "instruction text");
        if (PreparationInstruction.CATEGORY_FASTING.equals(current.category()) && current.durationHours() == null) {
            throw new InvalidCatalogCommandException(
                    "A fasting preparation must declare a duration in hours before publication.");
        }

        PreparationInstruction published = new PreparationInstruction(
                current.preparationId(), current.tenantId(), current.laboratoryId(), current.code(),
                current.title(), current.instructionText(), current.category(), current.durationHours(),
                PreparationInstruction.STATUS_PUBLISHED, current.version(), current.createdAt(), Instant.now(clock));
        PreparationInstruction saved = repository.save(published);
        auditRecorder.recordSystemEvent(saved.tenantId(), "PreparationPublished", "PreparationInstruction",
                saved.preparationId(), "{\"version\":%d}".formatted(saved.version()));
        return saved;
    }

    /**
     * RN-004 assignment rule: a preparation may only be assigned to a test or panel once it has been
     * published (a draft preparation is not yet safe to surface to patients). The assignment target
     * type and reference are validated before the assignment is persisted.
     */
    public PreparationAssignment assign(String preparationId, AssignPreparationCommand command) {
        PreparationInstruction current = require(preparationId);
        if (!PreparationInstruction.STATUS_PUBLISHED.equals(current.status())) {
            throw new CatalogConflictException(
                    "Only a published preparation can be assigned to a test or panel (current status: "
                            + current.status() + ").");
        }
        String targetType = requiredOneOf(command.targetType(), "Assignment target type is invalid.",
                PreparationAssignment.TARGET_TEST, PreparationAssignment.TARGET_PANEL);
        String targetRefId = requiredText(command.targetRefId(), "Assignment target reference id is required.");

        PreparationAssignment assignment = new PreparationAssignment(newId(), current.preparationId(), targetType, targetRefId);
        PreparationAssignment saved = repository.saveAssignment(assignment);
        auditRecorder.recordSystemEvent(current.tenantId(), "PreparationAssigned", "PreparationAssignment",
                saved.assignmentId(),
                "{\"preparationId\":\"%s\",\"targetType\":\"%s\"}".formatted(
                        jsonText(current.preparationId()), jsonText(targetType)));
        return saved;
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

    /**
     * Cross-module read used by BCM-ATT-001 (Appointment Scheduling) to surface preparation
     * instructions for a requested test or panel (VO-APT-002 PreparationSummary). Only published
     * preparations are returned; a draft or deprecated assignment is not yet safe to surface to
     * patients.
     */
    public List<PreparationInstruction> findPublishedForTarget(String targetType, String targetRefId) {
        String type = requiredOneOf(targetType, "Preparation target type is invalid.",
                PreparationAssignment.TARGET_TEST, PreparationAssignment.TARGET_PANEL);
        String refId = requiredText(targetRefId, "Preparation target reference id is required.");
        return repository.findAssignmentsByTarget(type, refId).stream()
                .map(assignment -> repository.findById(assignment.preparationId()))
                .flatMap(java.util.Optional::stream)
                .filter(preparation -> PreparationInstruction.STATUS_PUBLISHED.equals(preparation.status()))
                .toList();
    }

    private PreparationInstruction require(String preparationId) {
        return repository.findById(requiredText(preparationId, "Preparation id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Preparation was not found."));
    }

    private static void requireLocalized(LocalizedText text, String field) {
        if (text == null || !org.springframework.util.StringUtils.hasText(text.en())
                || !org.springframework.util.StringUtils.hasText(text.es())) {
            throw new InvalidCatalogCommandException(
                    "A preparation must provide a localized " + field + " in every language before publication.");
        }
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
