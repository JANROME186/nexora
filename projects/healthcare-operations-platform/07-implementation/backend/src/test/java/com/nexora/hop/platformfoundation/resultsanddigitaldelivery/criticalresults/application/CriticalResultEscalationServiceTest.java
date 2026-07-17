package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.application;

import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.adapter.out.memory.InMemoryCriticalResultEscalationRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.domain.*;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CriticalResultEscalationServiceTest {

    private InMemoryCriticalResultEscalationRepository repository;
    private LaboratoryResultsRepository laboratoryResultsRepository;
    private ApplicationEventPublisher eventPublisher;
    private CriticalResultEscalationService service;

    private AuditMetadata audit;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCriticalResultEscalationRepository();
        laboratoryResultsRepository = mock(LaboratoryResultsRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new CriticalResultEscalationService(repository, laboratoryResultsRepository, eventPublisher);

        audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
    }

    @Test
    void shouldCreateEscalationOnCriticalFlag() {
        // RN-001 / TST-CRR-006-01
        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.laboratoryId()).thenReturn("lab-1");
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        CriticalResultEscalation escalation = service.createEscalation("r1", "t1", "lab-1", "Extremely high value", audit);

        assertNotNull(escalation);
        assertEquals(CriticalResultEscalation.Status.OPEN, escalation.getStatus());
        assertEquals("Extremely high value", escalation.getCriticalReason());
        assertEquals(1, escalation.getEscalationTier());

        // Verify stored
        assertTrue(repository.findById(escalation.getEscalationId()).isPresent());
    }

    @Test
    void shouldAdvanceTierOnEscalation() {
        // RN-002 / TST-CRR-006-02
        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.laboratoryId()).thenReturn("lab-1");
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        CriticalResultEscalation escalation = service.createEscalation("r1", "t1", "lab-1", "Extremely high value", audit);
        
        CriticalResultEscalation escalated = service.escalate(escalation.getEscalationId(), audit);

        assertEquals(2, escalated.getEscalationTier());
        assertEquals(CriticalResultEscalation.Status.ESCALATED, escalated.getStatus());
        verify(eventPublisher, times(1)).publishEvent(any(CriticalResultEscalatedEvent.class));
    }

    @Test
    void shouldAcknowledgeSuccessfully() {
        // RN-003
        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.laboratoryId()).thenReturn("lab-1");
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        CriticalResultEscalation escalation = service.createEscalation("r1", "t1", "lab-1", "Extremely high value", audit);

        CriticalResultEscalation acknowledged = service.acknowledge(escalation.getEscalationId(), "handler-123", audit);

        assertEquals(CriticalResultEscalation.Status.ACKNOWLEDGED, acknowledged.getStatus());
        assertEquals("handler-123", acknowledged.getAcknowledgedBy().value());
        assertNotNull(acknowledged.getAcknowledgedAt());
        verify(eventPublisher, times(1)).publishEvent(any(CriticalResultAcknowledgedEvent.class));
    }

    @Test
    void shouldRejectClosureWithoutAcknowledgement() {
        // RN-003 / TST-CRR-006-03
        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.laboratoryId()).thenReturn("lab-1");
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        CriticalResultEscalation escalation = service.createEscalation("r1", "t1", "lab-1", "Extremely high value", audit);

        // Not acknowledged yet, attempt close should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> service.close(escalation.getEscalationId(), audit));
    }

    @Test
    void shouldCloseSuccessfullyIfAcknowledged() {
        // RN-003
        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.laboratoryId()).thenReturn("lab-1");
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        CriticalResultEscalation escalation = service.createEscalation("r1", "t1", "lab-1", "Extremely high value", audit);
        service.acknowledge(escalation.getEscalationId(), "handler-123", audit);

        CriticalResultEscalation closed = service.close(escalation.getEscalationId(), audit);

        assertEquals(CriticalResultEscalation.Status.CLOSED, closed.getStatus());
    }
}
