package com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.application;

import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.optionalText;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.DiagnosticService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.DiagnosticServiceRepository;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.diagnosticservicecatalog.domain.ServiceComponentLink;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogConflictException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.CatalogEntityNotFoundException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.InvalidCatalogCommandException;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;

/**
 * Compiles the generatable outputs declared in
 * bcm-svc-001-diagnostic-service-catalog/generation-plan.yaml and implements the custom rules
 * CUS-SVC-001-01..04 (publication validation, immutable versioning and the published snapshot
 * projection) delivered by MVP-MOD-002-BE-002.
 */
@Service
public class DiagnosticServiceCatalogService {

    private static final List<String> SERVICE_TYPES = List.of(
            DiagnosticService.TYPE_TEST, DiagnosticService.TYPE_PANEL, DiagnosticService.TYPE_PROFILE, DiagnosticService.TYPE_MIXED);

    private final DiagnosticServiceRepository repository;
    private final TenantDirectory tenantDirectory;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public DiagnosticServiceCatalogService(
            DiagnosticServiceRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder) {
        this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
    }

    DiagnosticServiceCatalogService(
            DiagnosticServiceRepository repository,
            TenantDirectory tenantDirectory,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.tenantDirectory = tenantDirectory;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public DiagnosticService create(CreateDiagnosticServiceCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String code = requiredText(command.code(), "Diagnostic service code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        String serviceType = requiredOneOf(command.serviceType(), "Service type is invalid.",
                SERVICE_TYPES.toArray(String[]::new));

        if (!tenantDirectory.tenantExists(tenantId)) {
            throw new CatalogEntityNotFoundException("Tenant was not found.");
        }
        if (repository.existsByCode(laboratoryId, code, null)) {
            throw new InvalidCatalogCommandException("Diagnostic service code already exists in this laboratory.");
        }

        Instant now = Instant.now(clock);
        DiagnosticService service = new DiagnosticService(
                newId(), tenantId, laboratoryId, code, new LocalizedText(nameEn, nameEs),
                optionalText(command.categoryId()), serviceType, DiagnosticService.STATUS_DRAFT, 1, now, now);
        DiagnosticService saved = repository.save(service);
        repository.replaceComponentLinks(saved.serviceId(), toLinks(saved.serviceId(), command.components()));

        auditRecorder.recordSystemEvent(tenantId, "DiagnosticServiceCreated", "DiagnosticService", saved.serviceId(),
                "{\"code\":\"%s\"}".formatted(jsonText(saved.code())));
        return saved;
    }

    public DiagnosticService update(String serviceId, UpdateDiagnosticServiceCommand command) {
        DiagnosticService current = require(serviceId);
        if (!DiagnosticService.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "A published diagnostic service is immutable. Its published snapshot is the source of "
                            + "truth; create a new draft version instead of editing it directly (RN-003).");
        }

        String code = requiredText(command.code(), "Diagnostic service code is required.");
        String nameEn = requiredText(command.nameEn(), "English name is required.");
        String nameEs = requiredText(command.nameEs(), "Spanish name is required.");
        String serviceType = requiredOneOf(command.serviceType(), "Service type is invalid.",
                SERVICE_TYPES.toArray(String[]::new));

        if (repository.existsByCode(current.laboratoryId(), code, current.serviceId())) {
            throw new InvalidCatalogCommandException("Diagnostic service code already exists in this laboratory.");
        }

        DiagnosticService updated = new DiagnosticService(
                current.serviceId(), current.tenantId(), current.laboratoryId(), code,
                new LocalizedText(nameEn, nameEs), optionalText(command.categoryId()), serviceType,
                current.status(), current.version(), current.createdAt(), Instant.now(clock));
        DiagnosticService saved = repository.save(updated);
        repository.replaceComponentLinks(saved.serviceId(), toLinks(saved.serviceId(), command.components()));
        return saved;
    }

    public DiagnosticService deprecate(String serviceId) {
        DiagnosticService current = require(serviceId);
        if (DiagnosticService.STATUS_RETIRED.equals(current.status())) {
            throw new InvalidCatalogCommandException("A retired diagnostic service cannot be deprecated.");
        }
        DiagnosticService deprecated = new DiagnosticService(
                current.serviceId(), current.tenantId(), current.laboratoryId(), current.code(), current.name(),
                current.categoryId(), current.serviceType(), DiagnosticService.STATUS_DEPRECATED, current.version(),
                current.createdAt(), Instant.now(clock));
        DiagnosticService saved = repository.save(deprecated);
        auditRecorder.recordSystemEvent(saved.tenantId(), "DiagnosticServiceDeprecated", "DiagnosticService",
                saved.serviceId(), "{}");
        return saved;
    }

    /**
     * RN-002 publication rule: a diagnostic service can only be published from draft, and it must
     * declare at least one orderable component (test or panel). Publishing freezes the record: once
     * published it is immutable ({@link #update} is rejected with {@link CatalogConflictException}),
     * so the published record itself is the immutable published snapshot returned by
     * {@link #getPublishedSnapshot}.
     */
    public DiagnosticService publish(String serviceId) {
        DiagnosticService current = require(serviceId);
        if (!DiagnosticService.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogConflictException(
                    "Only a draft diagnostic service can be published (current status: " + current.status() + ").");
        }
        if (repository.findComponentLinks(current.serviceId()).isEmpty()) {
            throw new InvalidCatalogCommandException(
                    "A diagnostic service must declare at least one component before publication.");
        }

        DiagnosticService published = new DiagnosticService(
                current.serviceId(), current.tenantId(), current.laboratoryId(), current.code(), current.name(),
                current.categoryId(), current.serviceType(), DiagnosticService.STATUS_PUBLISHED, current.version(),
                current.createdAt(), Instant.now(clock));
        DiagnosticService saved = repository.save(published);
        auditRecorder.recordSystemEvent(saved.tenantId(), "DiagnosticServicePublished", "DiagnosticService",
                saved.serviceId(), "{\"version\":%d}".formatted(saved.version()));
        return saved;
    }

    /** CUS-SVC-001-03: the immutable published snapshot projection of a diagnostic service. */
    public DiagnosticService getPublishedSnapshot(String serviceId) {
        DiagnosticService current = require(serviceId);
        if (DiagnosticService.STATUS_DRAFT.equals(current.status())) {
            throw new CatalogEntityNotFoundException(
                    "This diagnostic service has no published snapshot; it has never been published.");
        }
        return current;
    }

    public DiagnosticService get(String serviceId) {
        return require(serviceId);
    }

    public List<ServiceComponentLink> getComponents(String serviceId) {
        require(serviceId);
        return repository.findComponentLinks(serviceId);
    }

    public List<DiagnosticService> list(String laboratoryId) {
        return repository.findByLaboratoryId(requiredText(laboratoryId, "Laboratory id is required."));
    }

    /**
     * Published-only projection over {@link #list(String)} realizing BCM-SVC-001 {@code
     * listPublishedServices} for the COM-MOD-011 anonymous public catalog. Drafts, deprecated and
     * retired records are never returned; this is a filter, not a separate collection, so the
     * published snapshot remains the single source of truth (RN-003).
     */
    public List<DiagnosticService> listPublished(String laboratoryId) {
        return list(laboratoryId).stream()
                .filter(entry -> DiagnosticService.STATUS_PUBLISHED.equals(entry.status()))
                .toList();
    }

    private DiagnosticService require(String serviceId) {
        return repository.findById(requiredText(serviceId, "Diagnostic service id is required."))
                .orElseThrow(() -> new CatalogEntityNotFoundException("Diagnostic service was not found."));
    }

    private static List<ServiceComponentLink> toLinks(String serviceId, List<ServiceComponentLinkInput> inputs) {
        if (inputs == null) {
            return List.of();
        }
        return inputs.stream()
                .map(input -> new ServiceComponentLink(
                        newId(),
                        serviceId,
                        requiredOneOf(input.componentType(), "Component type is invalid.",
                                ServiceComponentLink.COMPONENT_TEST, ServiceComponentLink.COMPONENT_PANEL),
                        requiredText(input.componentRefId(), "Component reference id is required."),
                        input.displayOrder()))
                .toList();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
