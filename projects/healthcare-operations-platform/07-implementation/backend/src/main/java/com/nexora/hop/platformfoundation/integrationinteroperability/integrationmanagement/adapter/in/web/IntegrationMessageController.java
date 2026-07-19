package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.adapter.in.web;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.application.IntegrationManagementService;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationMessageRecord;

/** Rendered controller for bcm-plt-004-integration-management/openapi-source.yaml (IntegrationMessageRecord resource). */
@RestController
@RequestMapping("/api/platform/integration/messages")
class IntegrationMessageController {

    private final IntegrationManagementService service;

    IntegrationMessageController(IntegrationManagementService service) {
        this.service = service;
    }

    @GetMapping("/{messageId}")
    ResponseEntity<MessageDetailResponse> getMessage(@PathVariable String messageId) {
        return ResponseEntity.ok(MessageDetailResponse.from(service.getMessage(messageId)));
    }

    @PostMapping("/{messageId}/retry")
    ResponseEntity<MessageDetailResponse> retryMessage(
            @PathVariable String messageId, @RequestBody(required = false) RetryMessageRequest request) {
        String rawPayload = request == null ? null : request.rawPayload();
        String actorId = request == null || request.actorId() == null ? "system" : request.actorId();
        return ResponseEntity.ok(MessageDetailResponse.from(service.retryMessage(messageId, rawPayload, actorId)));
    }

    record RetryMessageRequest(String rawPayload, String actorId) {
    }

    record MessageDetailResponse(
            String messageId, String endpointId, String externalMessageId, String correlationId,
            String sourceProtocol, String rawPayloadReference, String normalizedMessageType,
            Map<String, String> canonicalFields, String targetBoundedContext, String normalizationStatus,
            String canonicalErrorCode, int retryCount, LocalDateTime nextRetryAt, String deadLetterReason) {
        static MessageDetailResponse from(IntegrationMessageRecord entity) {
            return new MessageDetailResponse(
                    entity.messageId(), entity.endpointId(), entity.externalMessageId(), entity.correlationId(),
                    entity.envelope().sourceProtocol(), entity.envelope().rawPayloadReference(),
                    entity.normalizedMessageType(), entity.canonicalFields(), entity.targetBoundedContext(),
                    entity.normalizationStatus(), entity.canonicalErrorCode(), entity.retryCount(),
                    entity.nextRetryAt(), entity.deadLetterReason());
        }
    }
}
