package com.nexora.hop.platformfoundation.externalqualitycompliance.application;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditFinding;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditSchedule;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditScheduleRepository;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityComplianceException;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.events.AuditEvents;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AuditManagementService {

    private final AuditScheduleRepository repository;
    private final CapaManagementService capaService;
    private final ApplicationEventPublisher eventPublisher;

    public AuditManagementService(
            AuditScheduleRepository repository,
            CapaManagementService capaService,
            ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.capaService = capaService;
        this.eventPublisher = eventPublisher;
    }

    public AuditSchedule createAuditSchedule(
            TenantId tenantId,
            String title,
            String category,
            String standardReference,
            UUID leadAuditorId,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate,
            AuditMetadata audit) {

        AuditSchedule schedule = new AuditSchedule(
                UUID.randomUUID(),
                null,
                tenantId != null ? tenantId : new TenantId(UUID.randomUUID().toString()),
                title,
                category,
                standardReference,
                leadAuditorId,
                plannedStartDate,
                plannedEndDate,
                audit
        );

        AuditSchedule saved = repository.save(schedule);

        if (eventPublisher != null) {
            eventPublisher.publishEvent(new AuditEvents.AuditScheduleCreatedEvent(
                    saved.getAuditId(),
                    saved.getAuditCode(),
                    saved.getTitle(),
                    saved.getCategory(),
                    saved.getLeadAuditorId(),
                    Instant.now()
            ));
        }

        return saved;
    }

    public AuditSchedule getAuditSchedule(UUID auditId) {
        return repository.findById(auditId)
                .orElseThrow(() -> new ExternalQualityComplianceException("AUDIT_NOT_FOUND", "quality.error.audit_not_found", "Audit schedule not found with ID: " + auditId));
    }

    public List<AuditSchedule> listAuditSchedules(String category, String status) {
        return repository.findAll(category, status);
    }

    public AuditSchedule recordAuditFinding(
            UUID auditId,
            String clauseReference,
            String severityStr,
            String observation,
            String evidenceReference,
            AuditMetadata audit) {

        AuditSchedule schedule = getAuditSchedule(auditId);
        AuditFinding.Severity severity = AuditFinding.Severity.fromString(severityStr);

        AuditFinding finding = schedule.addFinding(clauseReference, severity, observation, evidenceReference, audit);

        UUID capaId = null;
        if (severity == AuditFinding.Severity.CRITICAL || severity == AuditFinding.Severity.MAJOR) {
            if (capaService != null) {
                try {
                    CapaInvestigation capa = capaService.createCapa(
                            schedule.getTenantId(),
                            "Audit Non-Conformity (" + severity + "): " + schedule.getAuditCode(),
                            "AUDIT_FINDING",
                            finding.getFindingId().toString(),
                            schedule.getLeadAuditorId(),
                            LocalDate.now().plusDays(21),
                            audit
                    );
                    capaId = capa.getCapaId();
                    finding.linkCapa(capaId);
                } catch (Exception ignored) {
                }
            }
        }

        AuditSchedule saved = repository.save(schedule);

        if (eventPublisher != null) {
            eventPublisher.publishEvent(new AuditEvents.AuditFindingRecordedEvent(
                    saved.getAuditId(),
                    saved.getAuditCode(),
                    finding.getFindingId(),
                    finding.getSeverity().name(),
                    capaId,
                    Instant.now()
            ));
        }

        return saved;
    }

    public AuditSchedule closeAuditSchedule(UUID auditId, AuditMetadata audit) {
        AuditSchedule schedule = getAuditSchedule(auditId);
        schedule.closeAudit(audit);

        AuditSchedule saved = repository.save(schedule);

        if (eventPublisher != null) {
            eventPublisher.publishEvent(new AuditEvents.AuditScheduleClosedEvent(
                    saved.getAuditId(),
                    saved.getAuditCode(),
                    Instant.now()
            ));
        }

        return saved;
    }
}
