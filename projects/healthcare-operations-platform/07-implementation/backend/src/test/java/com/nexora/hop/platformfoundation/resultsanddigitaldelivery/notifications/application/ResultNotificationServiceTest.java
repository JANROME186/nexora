package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.application;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.application.FrontDeskSaleSourcePort;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DoctorSnapshot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.PatientSnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultFlaggedCriticalEvent;
import com.nexora.hop.platformfoundation.notificationmanagement.application.NotificationManagementService;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequest;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryAuthorizedEvent;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryTicket;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryTicketRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.adapter.out.memory.InMemoryResultNotificationRequestRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain.ResultNotificationRequest;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResultNotificationServiceTest {

    private InMemoryResultNotificationRequestRepository repository;
    private NotificationManagementService notificationManagementService;
    private LaboratoryResultsRepository laboratoryResultsRepository;
    private ResultDeliveryTicketRepository resultDeliveryTicketRepository;
    private FrontDeskSaleSourcePort saleSourcePort;
    private ResultNotificationService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryResultNotificationRequestRepository();
        notificationManagementService = mock(NotificationManagementService.class);
        laboratoryResultsRepository = mock(LaboratoryResultsRepository.class);
        resultDeliveryTicketRepository = mock(ResultDeliveryTicketRepository.class);
        saleSourcePort = mock(FrontDeskSaleSourcePort.class);
        service = new ResultNotificationService(repository, notificationManagementService, laboratoryResultsRepository, resultDeliveryTicketRepository, saleSourcePort);
    }

    @Test
    void shouldComposeDeliveredNotificationOnDeliveryAuthorized() {
        // RN-001 (TST-RNT-007-01) / RN-003 (TST-RNT-007-03)
        UUID ticketId = UUID.randomUUID();
        ResultId resultId = new ResultId("r1");
        TenantId tenantId = new TenantId("t1");

        ResultDeliveryTicket ticket = mock(ResultDeliveryTicket.class);
        when(ticket.getTicketId()).thenReturn(ticketId);
        when(ticket.getResultId()).thenReturn(resultId);
        when(ticket.getTenantId()).thenReturn(tenantId);
        when(ticket.getPatientId()).thenReturn(new PatientId("p1"));
        when(ticket.getRecipientId()).thenReturn("p1");
        when(ticket.getRecipientType()).thenReturn("patient");
        when(ticket.getAccessCode()).thenReturn("code123");
        when(resultDeliveryTicketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        LaboratoryResult result = mock(LaboratoryResult.class);
        when(result.laboratoryId()).thenReturn("lab-1");
        when(laboratoryResultsRepository.findById("r1", "t1")).thenReturn(Optional.of(result));

        NotificationRequest dummyDispatch = mock(NotificationRequest.class);
        when(dummyDispatch.getNotificationId()).thenReturn(UUID.randomUUID());
        when(notificationManagementService.submitNotificationRequest(
                any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(dummyDispatch);

        ResultDeliveryAuthorizedEvent event = new ResultDeliveryAuthorizedEvent(ticketId, resultId, tenantId, "patient", "patient_portal");
        service.onResultDeliveryAuthorized(event);

        // Verify notification composed and recorded
        List<ResultNotificationRequest> composedList = repository.findAll();
        assertEquals(1, composedList.size());
        ResultNotificationRequest request = composedList.get(0);
        assertEquals("result_delivered", request.getTriggerReason());
        assertEquals("submitted", request.getDispatchStatus());
        assertEquals(dummyDispatch.getNotificationId(), request.getDispatchReference());

        // Verify dispatch routed through BCM-PLT-003
        verify(notificationManagementService, times(1)).submitNotificationRequest(
                eq(tenantId),
                eq(new LaboratoryId("lab-1")),
                eq("p1"),
                eq("patient"),
                eq("sms"),
                eq("tpl_result_delivered"),
                anyMap(),
                any()
        );
    }

    @Test
    void shouldComposeCriticalNotificationOnCriticalFlag() {
        // RN-002 (TST-RNT-007-02) / RN-003 (TST-RNT-007-03)
        LaboratoryResult result = mock(LaboratoryResult.class);
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

        NotificationRequest dummyDispatch = mock(NotificationRequest.class);
        when(dummyDispatch.getNotificationId()).thenReturn(UUID.randomUUID());
        when(notificationManagementService.submitNotificationRequest(
                any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(dummyDispatch);

        ResultFlaggedCriticalEvent event = new ResultFlaggedCriticalEvent("r1", "t1", "lab-1", "Value critical");
        service.onResultFlaggedCritical(event);

        // Both patient and doctor notifications composed
        List<ResultNotificationRequest> composedList = repository.findAll();
        assertEquals(2, composedList.size());

        // Verify notifications submitted to BCM-PLT-003
        verify(notificationManagementService, times(2)).submitNotificationRequest(
                any(TenantId.class),
                eq(new LaboratoryId("lab-1")),
                anyString(),
                anyString(),
                anyString(),
                eq("tpl_result_critical"),
                anyMap(),
                any()
        );
    }
}
