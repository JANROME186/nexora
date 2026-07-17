package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.application;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.application.FrontDeskSaleSourcePort;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DoctorSnapshot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.PatientSnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultStatus;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.PatientManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepresentative;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.adapter.out.memory.InMemoryResultDeliveryTicketRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.*;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultDeliveryServiceTest {

    private InMemoryResultDeliveryTicketRepository repository;
    private LaboratoryResultsRepository laboratoryResultsRepository;
    private PatientManagementService patientManagementService;
    private FrontDeskSaleSourcePort saleSourcePort;
    private ApplicationEventPublisher eventPublisher;
    private ResultDeliveryService service;

    private AuditMetadata audit;

    @BeforeEach
    void setUp() {
        repository = new InMemoryResultDeliveryTicketRepository();
        laboratoryResultsRepository = mock(LaboratoryResultsRepository.class);
        patientManagementService = mock(PatientManagementService.class);
        saleSourcePort = mock(FrontDeskSaleSourcePort.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new ResultDeliveryService(repository, laboratoryResultsRepository, patientManagementService, saleSourcePort, eventPublisher);

        audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
    }

    @Test
    void shouldRefuseAuthorizationForNonReleasedResult() {
        // RN-001 (TST-DLV-004-01)
        LaboratoryResult unreleasedResult = mock(LaboratoryResult.class);
        when(unreleasedResult.status()).thenReturn(ResultStatus.captured);
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(unreleasedResult));

        assertThrows(IllegalStateException.class, () -> service.authorizeResultDelivery("r1", "t1", audit));
    }

    @Test
    void shouldAuthorizeReleasedResultAndMapRecipients() {
        // Arrange
        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.status()).thenReturn(ResultStatus.released);
        when(result.orderId()).thenReturn("ord-123");
        when(result.laboratoryId()).thenReturn("lab-1");
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        PatientSnapshot patientSnap = mock(PatientSnapshot.class);
        when(patientSnap.patientId()).thenReturn("p1");
        when(patientSnap.fullName()).thenReturn("John Doe");
        DoctorSnapshot doctorSnap = new DoctorSnapshot("d1", 1, "Dr. House", "license123", Instant.now());
        DiagnosticOrder order = mock(DiagnosticOrder.class);
        when(order.patientSnapshot()).thenReturn(patientSnap);
        when(order.doctorSnapshot()).thenReturn(doctorSnap);
        when(saleSourcePort.findOrderById("ord-123")).thenReturn(order);

        when(patientManagementService.patientExists("p1")).thenReturn(true);

        PatientRepresentative activeRep = mock(PatientRepresentative.class);
        when(activeRep.representativeId()).thenReturn("rep-1");
        when(activeRep.status()).thenReturn(PatientRepresentative.STATUS_ACTIVE);
        when(activeRep.authorizationFrom()).thenReturn(LocalDate.now().minusDays(1));
        when(activeRep.authorizationTo()).thenReturn(LocalDate.now().plusDays(1));
        when(patientManagementService.listRepresentatives("p1")).thenReturn(List.of(activeRep));

        // Act
        List<ResultDeliveryTicket> tickets = service.authorizeResultDelivery("r1", "t1", audit);

        // Assert
        assertEquals(3, tickets.size()); // patient, representative, doctor
        verify(eventPublisher, times(3)).publishEvent(any(ResultDeliveryAuthorizedEvent.class));
    }

    @Test
    void shouldRefuseAccessIfCallerIdMismatch() {
        // RN-002 / RN-008
        ResultDeliveryTicket ticket = new ResultDeliveryTicket(
                UUID.randomUUID(),
                new com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId("r1"),
                new com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId("t1"),
                new com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId("p1"),
                "access",
                LocalDateTime.now().plusDays(1),
                "patient",
                "p1",
                "patient_portal",
                new DeliveryAuthorizationCheck(true, true, false, LocalDateTime.now()),
                audit
        );
        repository.save(ticket);

        assertThrows(IllegalStateException.class, () -> service.getDeliveredResult(ticket.getTicketId(), "t1", "p2", audit));
    }

    @Test
    void shouldRefuseRepresentativeAuthorizationIfExpired() {
        // RN-003 (TST-DLV-004-03)
        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.status()).thenReturn(ResultStatus.released);
        when(result.orderId()).thenReturn("ord-123");
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        PatientSnapshot patientSnap = mock(PatientSnapshot.class);
        when(patientSnap.patientId()).thenReturn("p1");
        when(patientSnap.fullName()).thenReturn("John Doe");
        DiagnosticOrder order = mock(DiagnosticOrder.class);
        when(order.patientSnapshot()).thenReturn(patientSnap);
        when(order.doctorSnapshot()).thenReturn(null); // No doctor
        when(saleSourcePort.findOrderById("ord-123")).thenReturn(order);

        when(patientManagementService.patientExists("p1")).thenReturn(true);

        PatientRepresentative expiredRep = mock(PatientRepresentative.class);
        when(expiredRep.representativeId()).thenReturn("rep-1");
        when(expiredRep.status()).thenReturn(PatientRepresentative.STATUS_ACTIVE);
        when(expiredRep.authorizationFrom()).thenReturn(LocalDate.now().minusDays(10));
        when(expiredRep.authorizationTo()).thenReturn(LocalDate.now().minusDays(1)); // Expired yesterday
        when(patientManagementService.listRepresentatives("p1")).thenReturn(List.of(expiredRep));

        List<ResultDeliveryTicket> tickets = service.authorizeResultDelivery("r1", "t1", audit);

        // Only patient ticket is created, representative is skipped
        assertEquals(1, tickets.size());
        assertEquals("patient", tickets.get(0).getRecipientType());
    }

    @Test
    void shouldWithholdTicketsOnAmendment() {
        // RN-005 (TST-DLV-004-05)
        ResultDeliveryTicket ticket = new ResultDeliveryTicket(
                UUID.randomUUID(),
                new com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId("r1"),
                new com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId("t1"),
                new com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId("p1"),
                "access",
                LocalDateTime.now().plusDays(1),
                audit
        );
        repository.save(ticket);

        // Stub authorizeResultDelivery components for the re-authorization call triggered by withholdResultDelivery
        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.status()).thenReturn(ResultStatus.released);
        when(result.orderId()).thenReturn("ord-123");
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));
        DiagnosticOrder order = mock(DiagnosticOrder.class);
        PatientSnapshot patientSnap = mock(PatientSnapshot.class);
        when(patientSnap.patientId()).thenReturn("p1");
        when(patientSnap.fullName()).thenReturn("John Doe");
        when(order.patientSnapshot()).thenReturn(patientSnap);
        when(saleSourcePort.findOrderById("ord-123")).thenReturn(order);
        when(patientManagementService.patientExists("p1")).thenReturn(true);

        service.withholdResultDelivery("r1", "t1", audit);

        assertEquals(ResultDeliveryTicket.Status.WITHHELD, ticket.getStatus());
        verify(eventPublisher, times(1)).publishEvent(any(ResultDeliveryWithheldEvent.class));
    }

    @Test
    void shouldTransitionStatusToViewedOnRead() {
        // RN-007 (TST-DLV-004-07)
        ResultDeliveryTicket ticket = new ResultDeliveryTicket(
                UUID.randomUUID(),
                new com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId("r1"),
                new com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId("t1"),
                new com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId("p1"),
                "access",
                LocalDateTime.now().plusDays(1),
                "patient",
                "p1",
                "patient_portal",
                new DeliveryAuthorizationCheck(true, true, false, LocalDateTime.now()),
                audit
        );
        repository.save(ticket);

        ResultDeliveryTicket readTicket = service.getDeliveredResult(ticket.getTicketId(), "t1", "p1", audit);

        assertEquals(ResultDeliveryTicket.Status.VIEWED, readTicket.getStatus());
        assertNotNull(readTicket.getViewedAt());
        verify(eventPublisher, times(1)).publishEvent(any(ResultViewedEvent.class));
    }
}
