package com.nexora.hop.platformfoundation.externalqualitycompliance.application;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigationRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityDomainException;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.events.CapaEvents;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CapaManagementService {

    private final CapaInvestigationRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public CapaManagementService(
            CapaInvestigationRepository repository,
            ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public CapaInvestigation createCapa(
            TenantId tenantId,
            String title,
            String sourceCategory,
            String sourceReferenceId,
            UUID assignedInvestigatorId,
            LocalDate targetCompletionDate,
            AuditMetadata audit) {

        CapaInvestigation capa = new CapaInvestigation(
                UUID.randomUUID(),
                null,
                tenantId != null ? tenantId : new TenantId(UUID.randomUUID().toString()),
                title,
                sourceCategory,
                sourceReferenceId,
                assignedInvestigatorId,
                targetCompletionDate,
                audit
        );

        CapaInvestigation saved = repository.save(capa);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new CapaEvents.CapaInvestigationInitiatedEvent(
                    saved.getCapaId(),
                    saved.getCapaNumber(),
                    saved.getTitle(),
                    saved.getSourceCategory(),
                    saved.getSourceReferenceId(),
                    saved.getAssignedInvestigatorId(),
                    Instant.now()
            ));
        }
        return saved;
    }

    public CapaInvestigation getCapa(UUID capaId) {
        return repository.findById(capaId)
                .orElseThrow(() -> new ExternalQualityDomainException("CAPA_NOT_FOUND", "quality.error.capa_not_found", "CAPA investigation not found with ID: " + capaId));
    }

    public List<CapaInvestigation> listCapas(String status, String sourceCategory) {
        return repository.findAll(status, sourceCategory);
    }

    public CapaInvestigation recordRootCauseAnalysis(UUID capaId, String rootCauseMethodology, String rootCauseSummary, AuditMetadata audit) {
        CapaInvestigation capa = getCapa(capaId);
        capa.recordRca(rootCauseMethodology, rootCauseSummary, audit);
        return repository.save(capa);
    }

    public CapaInvestigation approveActionPlan(UUID capaId, AuditMetadata audit) {
        CapaInvestigation capa = getCapa(capaId);
        capa.approveActionPlan(audit);
        CapaInvestigation saved = repository.save(capa);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new CapaEvents.CapaInvestigationApprovedEvent(
                    saved.getCapaId(),
                    saved.getCapaNumber(),
                    Instant.now()
            ));
        }
        return saved;
    }

    public CapaInvestigation verifyEffectiveness(UUID capaId, String effectivenessRatingStr, String closureNotes, AuditMetadata audit) {
        CapaInvestigation capa = getCapa(capaId);
        CapaInvestigation.EffectivenessRating rating = CapaInvestigation.EffectivenessRating.fromString(effectivenessRatingStr);
        capa.verifyEffectiveness(rating, closureNotes, audit);
        CapaInvestigation saved = repository.save(capa);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new CapaEvents.CapaInvestigationVerifiedEvent(
                    saved.getCapaId(),
                    saved.getCapaNumber(),
                    saved.getEffectivenessRating().name(),
                    saved.getStatus().name(),
                    Instant.now()
            ));
        }
        return saved;
    }
}
