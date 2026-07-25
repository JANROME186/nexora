package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.application;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.application.FrontDeskSaleSourcePort;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultFlaggedCriticalEvent;
import com.nexora.hop.platformfoundation.notificationmanagement.application.NotificationManagementService;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequest;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryAuthorizedEvent;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryTicket;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain.ResultDeliveryTicketRepository;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain.ResultNotificationRequest;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain.ResultNotificationRequestRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ResultNotificationService {

    private final ResultNotificationRequestRepository repository;
    private final NotificationManagementService notificationManagementService;
    private final LaboratoryResultsRepository laboratoryResultsRepository;
    private final ResultDeliveryTicketRepository resultDeliveryTicketRepository;
    private final FrontDeskSaleSourcePort saleSourcePort;

    public ResultNotificationService(
            ResultNotificationRequestRepository repository,
            NotificationManagementService notificationManagementService,
            LaboratoryResultsRepository laboratoryResultsRepository,
            ResultDeliveryTicketRepository resultDeliveryTicketRepository,
            FrontDeskSaleSourcePort saleSourcePort) {

        this.repository = repository;
        this.notificationManagementService = notificationManagementService;
        this.laboratoryResultsRepository = laboratoryResultsRepository;
        this.resultDeliveryTicketRepository = resultDeliveryTicketRepository;
        this.saleSourcePort = saleSourcePort;
    }

    @EventListener
    public void onResultDeliveryAuthorized(ResultDeliveryAuthorizedEvent event) {
        // RN-001: Compose routine notification only after ResultDeliveryAuthorized
        ResultDeliveryTicket ticket = resultDeliveryTicketRepository.findById(event.deliveryTicketId())
                .orElseThrow(() -> new IllegalArgumentException("Delivery ticket not found"));

        LaboratoryResult result = laboratoryResultsRepository.findById(event.resultId().value(), event.tenantId().value())
                .orElseThrow(() -> new IllegalArgumentException("Laboratory result not found"));

        AuditMetadata systemAudit = new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now());

        String targetChannel = "sms";
        if ("referring_doctor".equals(ticket.getRecipientType())) {
            targetChannel = "email";
        }

        ResultNotificationRequest notificationReq = new ResultNotificationRequest(
                UUID.randomUUID(),
                event.resultId(),
                event.tenantId(),
                ticket.getPatientId(),
                ticket.getRecipientId(),
                ticket.getRecipientType(),
                "result_delivered",
                "tpl_result_delivered",
                targetChannel,
                systemAudit
        );
        repository.save(notificationReq);

        // Compose payload and delegate dispatch to BCM-PLT-003 (RN-003)
        Map<String, String> params = new HashMap<>();
        params.put("resultId", event.resultId().value());
        params.put("recipientId", ticket.getRecipientId());
        params.put("recipientType", ticket.getRecipientType());
        params.put("accessCode", ticket.getAccessCode());

        NotificationRequest dispatchReq = notificationManagementService.submitNotificationRequest(
                event.tenantId(),
                new LaboratoryId(result.laboratoryId()),
                ticket.getRecipientId(),
                ticket.getRecipientType(),
                targetChannel,
                "tpl_result_delivered",
                params,
                systemAudit
        );

        notificationReq.submit(dispatchReq.getNotificationId(), systemAudit);
        repository.save(notificationReq);
    }

    @EventListener
    public void onResultFlaggedCritical(ResultFlaggedCriticalEvent event) {
        // RN-002: Unconditional composition on critical flagging
        LaboratoryResult result = laboratoryResultsRepository.findById(event.resultId(), event.tenantId())
                .orElseThrow(() -> new IllegalArgumentException("Laboratory result not found"));

        DiagnosticOrder order = saleSourcePort.findOrderById(result.orderId());
        AuditMetadata systemAudit = new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now());

        // Notify patient
        String patientIdStr = order.patientSnapshot().patientId();
        ResultNotificationRequest patientNotify = new ResultNotificationRequest(
                UUID.randomUUID(),
                new ResultId(event.resultId()),
                new TenantId(event.tenantId()),
                new PatientId(patientIdStr),
                patientIdStr,
                "patient",
                "result_critical",
                "tpl_result_critical",
                "sms",
                systemAudit
        );
        repository.save(patientNotify);

        Map<String, String> patientParams = Map.of(
                "resultId", event.resultId(),
                "recipientId", patientIdStr,
                "criticalReason", event.criticalReason()
        );

        NotificationRequest patientDispatch = notificationManagementService.submitNotificationRequest(
                new TenantId(event.tenantId()),
                new LaboratoryId(result.laboratoryId()),
                patientIdStr,
                "patient",
                "sms",
                "tpl_result_critical",
                patientParams,
                systemAudit
        );
        patientNotify.submit(patientDispatch.getNotificationId(), systemAudit);
        repository.save(patientNotify);

        // Notify Referring Doctor if present
        if (order.doctorSnapshot() != null) {
            String doctorIdStr = order.doctorSnapshot().doctorId();
            ResultNotificationRequest docNotify = new ResultNotificationRequest(
                    UUID.randomUUID(),
                    new ResultId(event.resultId()),
                    new TenantId(event.tenantId()),
                    new PatientId(patientIdStr),
                    doctorIdStr,
                    "referring_doctor",
                    "result_critical",
                    "tpl_result_critical",
                    "email",
                    systemAudit
            );
            repository.save(docNotify);

            Map<String, String> docParams = Map.of(
                    "resultId", event.resultId(),
                    "recipientId", doctorIdStr,
                    "criticalReason", event.criticalReason()
            );

            NotificationRequest docDispatch = notificationManagementService.submitNotificationRequest(
                    new TenantId(event.tenantId()),
                    new LaboratoryId(result.laboratoryId()),
                    doctorIdStr,
                    "referring_doctor",
                    "email",
                    "tpl_result_critical",
                    docParams,
                    systemAudit
            );
            docNotify.submit(docDispatch.getNotificationId(), systemAudit);
            repository.save(docNotify);
        }
    }

    public List<ResultNotificationRequest> listAllResultNotifications() {
        return repository.findAll();
    }

    /** List notification requests for a result (BCM-RES-007 dispatch history). */
    public List<ResultNotificationRequest> listNotificationsForResult(String resultId, String tenantId) {
        return repository.findByResultId(new ResultId(resultId), new TenantId(tenantId));
    }
}
