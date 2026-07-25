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
import java.util.UUID;

@Service
public class ResultDeliveryService {
    private static final String RECIPIENT_PATIENT = "patient";
    private static final String RECIPIENT_PATIENT_REPRESENTATIVE = "patient_representative";
    private static final String RECIPIENT_REFERRING_DOCTOR = "referring_doctor";
    private static final String PATIENT_PORTAL = "patient_portal";
    private static final String DOCTOR_PORTAL = "doctor_portal";
    private static final int DELIVERY_TICKET_VALIDITY_DAYS = 30;

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
        LocalDateTime expiresAt = now.plusDays(DELIVERY_TICKET_VALIDITY_DAYS);

        authorizePatient(
                createdTickets, patientIdStr, resultIdObj, tenantIdObj, patientId, expiresAt, now, audit);
        authorizeRepresentatives(
                createdTickets, patientIdStr, resultIdObj, tenantIdObj, patientId, expiresAt, now, audit);
        authorizeReferringDoctor(createdTickets, order, resultIdObj, tenantIdObj, patientId, expiresAt, now, audit);

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
                publishWithheld(ticket, resIdObj);
            }
        }
        // Trigger re-authorization workflow
        authorizeResultDelivery(resultId, tenantId, audit);
    }

    private void authorizePatient(
            List<ResultDeliveryTicket> createdTickets,
            String patientIdStr,
            ResultId resultId,
            TenantId tenantId,
            PatientId patientId,
            LocalDateTime expiresAt,
            LocalDateTime now,
            AuditMetadata audit) {
        if (!patientManagementService.patientExists(patientIdStr)) {
            throw new IllegalStateException(ResultsDeliveryErrorCodes.DELIVERY_PATIENT_OWNERSHIP_MISMATCH);
        }
        ResultDeliveryTicket patientTicket = createTicket(
                resultId, tenantId, patientId, expiresAt, RECIPIENT_PATIENT, patientIdStr, PATIENT_PORTAL, false, now, audit);
        saveAndPublish(createdTickets, patientTicket, resultId, tenantId, RECIPIENT_PATIENT, PATIENT_PORTAL);
    }

    private void authorizeRepresentatives(
            List<ResultDeliveryTicket> createdTickets,
            String patientIdStr,
            ResultId resultId,
            TenantId tenantId,
            PatientId patientId,
            LocalDateTime expiresAt,
            LocalDateTime now,
            AuditMetadata audit) {
        List<PatientRepresentative> representatives = patientManagementService.listRepresentatives(patientIdStr);
        LocalDate today = LocalDate.now();
        for (PatientRepresentative representative : representatives) {
            if (isRepresentativeActive(representative, today)) {
                ResultDeliveryTicket repTicket = createTicket(
                        resultId,
                        tenantId,
                        patientId,
                        expiresAt,
                        RECIPIENT_PATIENT_REPRESENTATIVE,
                        representative.representativeId(),
                        PATIENT_PORTAL,
                        true,
                        now,
                        audit);
                saveAndPublish(
                        createdTickets,
                        repTicket,
                        resultId,
                        tenantId,
                        RECIPIENT_PATIENT_REPRESENTATIVE,
                        PATIENT_PORTAL);
            }
        }
    }

    private void authorizeReferringDoctor(
            List<ResultDeliveryTicket> createdTickets,
            DiagnosticOrder order,
            ResultId resultId,
            TenantId tenantId,
            PatientId patientId,
            LocalDateTime expiresAt,
            LocalDateTime now,
            AuditMetadata audit) {
        if (order.doctorSnapshot() == null) {
            return;
        }
        String doctorId = order.doctorSnapshot().doctorId();
        ResultDeliveryTicket docTicket = createTicket(
                resultId, tenantId, patientId, expiresAt, RECIPIENT_REFERRING_DOCTOR, doctorId, DOCTOR_PORTAL, false, now, audit);
        saveAndPublish(createdTickets, docTicket, resultId, tenantId, RECIPIENT_REFERRING_DOCTOR, DOCTOR_PORTAL);
    }

    private boolean isRepresentativeActive(PatientRepresentative representative, LocalDate today) {
        boolean active = PatientRepresentative.STATUS_ACTIVE.equals(representative.status());
        boolean windowValid = !today.isBefore(representative.authorizationFrom())
                && (representative.authorizationTo() == null || !today.isAfter(representative.authorizationTo()));
        return active && windowValid;
    }

    private void saveAndPublish(
            List<ResultDeliveryTicket> createdTickets,
            ResultDeliveryTicket ticket,
            ResultId resultId,
            TenantId tenantId,
            String recipientType,
            String channel) {
        repository.save(ticket);
        createdTickets.add(ticket);
        publishAuthorized(ticket, resultId, tenantId, recipientType, channel);
    }

    private ResultDeliveryTicket createTicket(
            ResultId resultId,
            TenantId tenantId,
            PatientId patientId,
            LocalDateTime expiresAt,
            String recipientType,
            String recipientId,
            String channel,
            boolean representativeVerified,
            LocalDateTime authorizedAt,
            AuditMetadata audit) {
        DeliveryAuthorizationCheck authCheck =
                new DeliveryAuthorizationCheck(true, true, representativeVerified, authorizedAt);
        return new ResultDeliveryTicket(
                UUID.randomUUID(),
                resultId,
                tenantId,
                patientId,
                UUID.randomUUID().toString().substring(0, 8),
                expiresAt,
                recipientType,
                recipientId,
                channel,
                authCheck,
                audit);
    }

    private void publishAuthorized(
            ResultDeliveryTicket ticket, ResultId resultId, TenantId tenantId, String recipientType, String channel) {
        eventPublisher.publishEvent(
                new ResultDeliveryAuthorizedEvent(ticket.getTicketId(), resultId, tenantId, recipientType, channel));
    }

    private void publishWithheld(ResultDeliveryTicket ticket, ResultId resultId) {
        eventPublisher.publishEvent(new ResultDeliveryWithheldEvent(ticket.getTicketId(), resultId, ticket.getTenantId()));
    }
}
