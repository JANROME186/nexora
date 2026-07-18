package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.adapter.in.web;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.application.ResultNotificationService;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain.ResultNotificationRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for Result Notification history (BCM-RES-007).
 */
@RestController
@RequestMapping("/api/clinical-operations/laboratory-results/{resultId}/notifications")
class ResultNotificationController {

    private final ResultNotificationService service;

    ResultNotificationController(ResultNotificationService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<ResultNotificationView>> listNotifications(
            @PathVariable String resultId,
            @RequestParam @NotBlank String tenantId) {
        List<ResultNotificationView> views = service.listNotificationsForResult(resultId, tenantId).stream()
                .map(ResultNotificationView::from)
                .toList();
        return ResponseEntity.ok(views);
    }

    /** Employee-portal-facing view of a notification request (BCM-RES-007 wire contract). */
    record ResultNotificationView(
            UUID notificationRequestId,
            String resultId,
            String tenantId,
            String recipientType,
            String recipientId,
            String channel,
            String status,
            LocalDateTime dispatchedAt,
            LocalDateTime deliveredAt,
            String failureReason,
            LocalDateTime createdAt) {

        static ResultNotificationView from(ResultNotificationRequest request) {
            return new ResultNotificationView(
                    request.getResultNotificationId(),
                    request.getResultId().value(),
                    request.getTenantId().value(),
                    request.getRecipientType(),
                    request.getRecipientId(),
                    request.getChannel(),
                    request.getDispatchStatus(),
                    request.getDispatchedAt(),
                    request.getDeliveredAt(),
                    request.getFailureReason(),
                    request.getAudit().createdAt());
        }
    }
}
