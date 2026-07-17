package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.application;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.application.FrontDeskSaleSourcePort;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultStatus;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.application.PatientManagementService;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientRepresentative;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.*;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.shared.ResultsDeliveryErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResultDeliveryService {

    private final ResultDeliveryTicketRepository repository;
    private final LaboratoryResultsRepository laboratoryResultsRepository;
    private final PatientManagementService patientManagementService;
    private final FrontDeskSaleSourcePort saleSourcePort;
    private final ApplicationEventPublisher eventPublisher;

    public ResultDeliveryService(
            ResultDeliveryTicketRepository repository,
            LaboratoryResultsRepository laboratoryResultsRepository,
            PatientManagementService patientManagementService,
            FrontDeskSaleSourcePort saleSourcePort,
            ApplicationEventPublisher eventPublisher) {
        
        this.repository = repository;
        this.laboratoryResultsRepository = laboratoryResultsRepository;
        this.patientManagementService = patientManagementService;
        this.saleSourcePort = saleSourcePort;
        this.eventPublisher = eventPublisher;
    }

    public List<ResultDeliveryTicket> authorizeResultDelivery(String resultId, String tenantId, AuditMetadata audit) {
        // RN-001: A result can be delivered only when its LaboratoryResult status is released
        LaboratoryResult result = laboratoryResultsRepository.findById(resultId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found."));

        if (result.status() != ResultStatus.released && result.status() != ResultStatus.amended) {
            throw new IllegalStateException(ResultsDeliveryErrorCodes.DELIVERY_RESULT_NOT_RELEASED);
        }

        DiagnosticOrder order = saleSourcePort.findOrderById(result.orderId());
        String patientIdStr = order.patientSnapshot().patientId();
        PatientId patientId = new PatientId(patientIdStr);
        TenantId tenantIdObj = new TenantId(tenantId);
        ResultId resultIdObj = new ResultId(resultId);

        List<ResultDeliveryTicket> createdTickets = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(30); // Default 30-day ticket validity

        // 1. Authorize Patient
        // RN-002: A patient recipient may be authorized only for their own results
        if (patientManagementService.patientExists(patientIdStr)) {
            DeliveryAuthorizationCheck authCheck = new DeliveryAuthorizationCheck(true, true, false, now);
            ResultDeliveryTicket patientTicket = new ResultDeliveryTicket(
                    UUID.randomUUID(),
                    resultIdObj,
                    tenantIdObj,
                    patientId,
                    UUID.randomUUID().toString().substring(0, 8),
                    expiresAt,
                    "patient",
                    patientIdStr,
                    "patient_portal",
                    authCheck,
                    audit
            );
            repository.save(patientTicket);
            createdTickets.add(patientTicket);
            eventPublisher.publishEvent(new ResultDeliveryAuthorizedEvent(
                    patientTicket.getTicketId(), resultIdObj, tenantIdObj, "patient", "patient_portal"));
        } else {
            throw new IllegalStateException(ResultsDeliveryErrorCodes.DELIVERY_PATIENT_OWNERSHIP_MISMATCH);
        }

        // 2. Authorize Representatives
        // RN-003: Patient representative relationship check
        List<PatientRepresentative> representatives = patientManagementService.listRepresentatives(patientIdStr);
        LocalDate today = LocalDate.now();
        for (PatientRepresentative rep : representatives) {
            boolean active = PatientRepresentative.STATUS_ACTIVE.equals(rep.status());
            boolean windowValid = !today.isBefore(rep.authorizationFrom()) && 
                    (rep.authorizationTo() == null || !today.isAfter(rep.authorizationTo()));

            if (active && windowValid) {
                DeliveryAuthorizationCheck authCheck = new DeliveryAuthorizationCheck(true, true, true, now);
                ResultDeliveryTicket repTicket = new ResultDeliveryTicket(
                        UUID.randomUUID(),
                        resultIdObj,
                        tenantIdObj,
                        patientId,
                        UUID.randomUUID().toString().substring(0, 8),
                        expiresAt,
                        "patient_representative",
                        rep.representativeId(),
                        "patient_portal",
                        authCheck,
                        audit
                );
                repository.save(repTicket);
                createdTickets.add(repTicket);
                eventPublisher.publishEvent(new ResultDeliveryAuthorizedEvent(
                        repTicket.getTicketId(), resultIdObj, tenantIdObj, "patient_representative", "patient_portal"));
            }
        }

        // 3. Authorize Referring Doctor
        // RN-004: Referring doctor check
        if (order.doctorSnapshot() != null) {
            String doctorId = order.doctorSnapshot().doctorId();
            DeliveryAuthorizationCheck authCheck = new DeliveryAuthorizationCheck(true, true, false, now);
            ResultDeliveryTicket docTicket = new ResultDeliveryTicket(
                    UUID.randomUUID(),
                    resultIdObj,
                    tenantIdObj,
                    patientId,
                    UUID.randomUUID().toString().substring(0, 8),
                    expiresAt,
                    "referring_doctor",
                    doctorId,
                    "doctor_portal",
                    authCheck,
                    audit
            );
            repository.save(docTicket);
            createdTickets.add(docTicket);
            eventPublisher.publishEvent(new ResultDeliveryAuthorizedEvent(
                    docTicket.getTicketId(), resultIdObj, tenantIdObj, "referring_doctor", "doctor_portal"));
        }

        return createdTickets;
    }

    public ResultDeliveryTicket getDeliveredResult(UUID ticketId, String tenantId, String callerId, AuditMetadata audit) {
        ResultDeliveryTicket ticket = repository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found."));

        if (!ticket.getTenantId().value().equals(tenantId)) {
            throw new IllegalStateException(ResultsDeliveryErrorCodes.DELIVERY_SCOPE_MISMATCH);
        }

        // RN-008: Recipient validation scope
        if (!ticket.getRecipientId().equals(callerId)) {
            throw new IllegalStateException(ResultsDeliveryErrorCodes.DELIVERY_PATIENT_OWNERSHIP_MISMATCH);
        }

        if (ticket.getStatus() == ResultDeliveryTicket.Status.WITHHELD) {
            throw new IllegalStateException(ResultsDeliveryErrorCodes.DELIVERY_WITHHELD_PENDING_REAUTHORIZATION);
        }

        // Transition status to VIEWED and record accessed state
        // RN-007: Opening a delivered result by an authorized recipient must record ResultViewed
        ticket.markViewed(LocalDateTime.now(), audit);
        repository.save(ticket);

        eventPublisher.publishEvent(new ResultViewedEvent(
                ticket.getTicketId(), ticket.getResultId(), ticket.getTenantId(), ticket.getRecipientId(), ticket.getViewedAt()));

        return ticket;
    }

    public void withholdResultDelivery(String resultId, String tenantId, AuditMetadata audit) {
        // RN-005: An amendment to a delivered result marks its existing delivery tickets withheld
        ResultId resIdObj = new ResultId(resultId);
        List<ResultDeliveryTicket> tickets = repository.findByResultId(resIdObj);
        for (ResultDeliveryTicket ticket : tickets) {
            if (ticket.getStatus() != ResultDeliveryTicket.Status.WITHHELD) {
                ticket.withhold(audit);
                repository.save(ticket);
                eventPublisher.publishEvent(new ResultDeliveryWithheldEvent(ticket.getTicketId(), resIdObj, ticket.getTenantId()));
            }
        }
        // Trigger re-authorization workflow
        authorizeResultDelivery(resultId, tenantId, audit);
    }
}
