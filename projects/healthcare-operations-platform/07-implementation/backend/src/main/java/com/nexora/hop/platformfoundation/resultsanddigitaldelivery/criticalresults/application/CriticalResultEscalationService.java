package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.application;

import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.domain.*;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.UserId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CriticalResultEscalationService {

    private final CriticalResultEscalationRepository repository;
    private final LaboratoryResultsRepository laboratoryResultsRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CriticalResultEscalationService(
            CriticalResultEscalationRepository repository,
            LaboratoryResultsRepository laboratoryResultsRepository,
            ApplicationEventPublisher eventPublisher) {

        this.repository = repository;
        this.laboratoryResultsRepository = laboratoryResultsRepository;
        this.eventPublisher = eventPublisher;
    }

    public CriticalResultEscalation createEscalation(
            String resultId,
            String tenantId,
            String laboratoryId,
            String criticalReason,
            AuditMetadata audit) {

        // RN-001: Unconditional escalation creation upon critical flagging
        // Read-only access to verify result exists (RN-004 boundary safeguard)
        LaboratoryResult result = laboratoryResultsRepository.findById(resultId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Laboratory result not found"));

        UUID escalationId = UUID.randomUUID();
        LocalDateTime deadline = LocalDateTime.now().plusMinutes(15); // Standard 15-minute deadline tier

        CriticalResultEscalation escalation = new CriticalResultEscalation(
                escalationId,
                new TenantId(tenantId),
                new LaboratoryId(laboratoryId),
                new ResultId(resultId),
                criticalReason,
                deadline,
                audit
        );

        repository.save(escalation);
        return escalation;
    }

    public CriticalResultEscalation escalate(UUID escalationId, AuditMetadata audit) {
        // RN-002: Advance escalation tier and trigger notifications
        CriticalResultEscalation escalation = repository.findById(escalationId)
                .orElseThrow(() -> new IllegalArgumentException("Escalation not found"));

        if (escalation.getStatus() == CriticalResultEscalation.Status.CLOSED || 
            escalation.getStatus() == CriticalResultEscalation.Status.ACKNOWLEDGED) {
            return escalation;
        }

        LocalDateTime nextDeadline = LocalDateTime.now().plusMinutes(15);
        escalation.escalate(nextDeadline, audit);
        repository.save(escalation);

        eventPublisher.publishEvent(new CriticalResultEscalatedEvent(
                escalation.getEscalationId(),
                escalation.getResultId(),
                escalation.getTenantId(),
                escalation.getEscalationTier()
        ));

        return escalation;
    }

    public CriticalResultEscalation acknowledge(UUID escalationId, String userId, AuditMetadata audit) {
        // RN-003: Require both acknowledgedBy and acknowledgedAt
        CriticalResultEscalation escalation = repository.findById(escalationId)
                .orElseThrow(() -> new IllegalArgumentException("Escalation not found"));

        escalation.acknowledge(new UserId(userId), LocalDateTime.now(), audit);
        repository.save(escalation);

        eventPublisher.publishEvent(new CriticalResultAcknowledgedEvent(
                escalation.getEscalationId(),
                escalation.getResultId(),
                escalation.getTenantId(),
                escalation.getAcknowledgedBy(),
                escalation.getAcknowledgedAt()
        ));

        return escalation;
    }

    public CriticalResultEscalation close(UUID escalationId, AuditMetadata audit) {
        // RN-003: A close attempt without prior acknowledgement is rejected
        CriticalResultEscalation escalation = repository.findById(escalationId)
                .orElseThrow(() -> new IllegalArgumentException("Escalation not found"));

        escalation.close(audit);
        repository.save(escalation);

        return escalation;
    }

    public List<CriticalResultEscalation> listOpenEscalations(String tenantId) {
        return repository.findOpenEscalations(tenantId);
    }

    @org.springframework.context.event.EventListener
    public void onResultFlaggedCritical(com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultFlaggedCriticalEvent event) {
        createEscalation(
                event.resultId(),
                event.tenantId(),
                event.laboratoryId(),
                event.criticalReason(),
                new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now())
        );
    }
}
