package com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.application;

import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelDefinitionRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelMember;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogCustomRuleNotImplementedException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles generatable outputs from bcm-svc-003-panel-catalog/generation-plan.yaml.
 * Custom points CUS-SVC-003-01..03 (member publication validation, versioning, published
 * snapshot) are hooks deferred to MVP-MOD-002-BE-002.
 */
@Service
public class PanelCatalogService {

    private final PanelDefinitionRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public PanelCatalogService(
            PanelDefinitionRepository repository, TenantDirectory tenantDirectory, AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    PanelCatalogService(
            PanelDefinitionRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public PanelDefinition create(CreatePanelDefinitionCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String code = requiredText(command.code(), "Panel code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }
        if (repository.existsByCode(laboratoryId, code, null)) {
            throw new InvalidCatalogCommandException("Panel code already exists in this laboratory.");
        }

        Instant now = Instant.now(clock);
        PanelDefinition panel = new PanelDefinition(
                newId(), tenantId, laboratoryId, code, new LocalizedText(nameEn, nameEs),
                PanelDefinition.STATUS_DRAFT, 1, now, now);
        PanelDefinition saved = repository.save(panel);
        repository.replaceMembers(saved.panelId(), toMembers(saved.panelId(), command.members()));

        auditRecorder.recordSystemEvent(tenantId, "PanelDefinitionCreated", "PanelDefinition", saved.panelId(),
                "{\"code\":\"%s\"}".formatted(jsonText(saved.code())));
        return saved;
    }

    public PanelDefinition update(String panelId, UpdatePanelDefinitionCommand command) {
        PanelDefinition current = require(panelId);
        if (!PanelDefinition.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogCustomRuleNotImplementedException(
                    "RN-004",
                    "A published panel is immutable; editing it requires the versioning and "
                            + "snapshot-freeze behavior reserved for MVP-MOD-002-BE-002.");
        }

        String code = requiredText(command.code(), "Panel code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        if (repository.existsByCode(current.laboratoryId(), code, current.panelId())) {
            throw new InvalidCatalogCommandException("Panel code already exists in this laboratory.");
        }

        PanelDefinition updated = new PanelDefinition(
                current.panelId(), current.tenantId(), current.laboratoryId(), code, new LocalizedText(nameEn, nameEs),
                current.status(), current.version(), current.createdAt(), Instant.now(clock));
        PanelDefinition saved = repository.save(updated);
        repository.replaceMembers(saved.panelId(), toMembers(saved.panelId(), command.members()));
        return saved;
    }

    public PanelDefinition deprecate(String panelId) {
        PanelDefinition current = require(panelId);
        if (PanelDefinition.STATUS_RETIRED.equals(current.status())) {
            throw new InvalidCatalogCommandException("A retired panel cannot be deprecated.");
        }
        PanelDefinition deprecated = new PanelDefinition(
                current.panelId(), current.tenantId(), current.laboratoryId(), current.code(), current.name(),
                PanelDefinition.STATUS_DEPRECATED, current.version(), current.createdAt(), Instant.now(clock));
        PanelDefinition saved = repository.save(deprecated);
        auditRecorder.recordSystemEvent(saved.tenantId(), "PanelDefinitionDeprecated", "PanelDefinition",
                saved.panelId(), "{}");
        return saved;
    }

    public PanelDefinition publish(String panelId) {
        require(panelId);
        throw new CatalogCustomRuleNotImplementedException(
                "RN-002/RN-003",
                "Publishing a panel requires minimum member and cross-aggregate member publication "
                        + "validation reserved for MVP-MOD-002-BE-002.");
    }

    public PanelDefinition getPublishedSnapshot(String panelId) {
        require(panelId);
        throw new CatalogCustomRuleNotImplementedException(
                "CUS-SVC-003-03",
                "The published panel snapshot projection is reserved for MVP-MOD-002-BE-002.");
    }

    public PanelDefinition get(String panelId) {
        return require(panelId);
    }

    public List<PanelMember> getMembers(String panelId) {
        require(panelId);
        return repository.findMembers(panelId);
    }

    public List<PanelDefinition> list(String laboratoryId) {
        return repository.findByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    private PanelDefinition require(String panelId) {
        return repository.findById(requiredText(panelId, "Panel id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Panel was not found."));
    }

    private static List<PanelMember> toMembers(String panelId, List<PanelMemberInput> inputs) {
        if (inputs == null) {
            return List.of();
        }
        return inputs.stream()
                .map(input -> new PanelMember(
                        newId(), panelId, requiredText(input.testRefId(), "Test reference id is required."),
                        input.displayOrder(), input.mandatory()))
                .toList();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
