package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.application.IntegrationManagementService;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationEndpoint;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationMessageRecord;

/** Rendered controller for bcm-plt-004-integration-management/openapi-source.yaml (IntegrationEndpoint resource). */
@RestController
@RequestMapping("/api/platform/integration/endpoints")
class IntegrationEndpointController {

    private final IntegrationManagementService service;

    IntegrationEndpointController(IntegrationManagementService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<EndpointResponse> registerIntegrationEndpoint(@Valid @RequestBody RegisterEndpointRequest request) {
        IntegrationEndpoint created = service.registerEndpoint(
                request.tenantId(), request.laboratoryId(), request.endpointName(), request.protocol(),
                request.direction(), request.actorId());
        return ResponseEntity.created(URI.create("/api/platform/integration/endpoints/" + created.endpointId()))
                .body(EndpointResponse.from(created));
    }

    @GetMapping
    ResponseEntity<List<EndpointResponse>> listIntegrationEndpoints(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.listEndpoints(tenantId).stream().map(EndpointResponse::from).toList());
    }

    @GetMapping("/{endpointId}")
    ResponseEntity<EndpointResponse> getIntegrationEndpoint(@PathVariable String endpointId) {
        return ResponseEntity.ok(EndpointResponse.from(service.getEndpoint(endpointId)));
    }

    @PostMapping("/{endpointId}/retire")
    ResponseEntity<EndpointResponse> retireIntegrationEndpoint(
            @PathVariable String endpointId, @RequestBody(required = false) ActorRequest request) {
        String actorId = request == null ? "system" : request.actorId();
        return ResponseEntity.ok(EndpointResponse.from(service.retireEndpoint(endpointId, actorId)));
    }

    @PostMapping("/{endpointId}/messages")
    ResponseEntity<MessageResponse> receiveMessage(
            @PathVariable String endpointId, @Valid @RequestBody ReceiveMessageRequest request) {
        IntegrationMessageRecord record = service.receiveMessage(
                endpointId, request.externalMessageId(), request.rawPayload(), request.actorId());
        return ResponseEntity.status(record.normalizationStatus().equals(IntegrationMessageRecord.STATUS_ACKNOWLEDGED)
                        ? org.springframework.http.HttpStatus.CREATED
                        : org.springframework.http.HttpStatus.OK)
                .body(MessageResponse.from(record));
    }

    record RegisterEndpointRequest(
            @NotBlank String tenantId, @NotBlank String laboratoryId, @NotBlank String endpointName,
            @NotBlank String protocol, @NotBlank String direction, @NotBlank String actorId) {
    }

    record ActorRequest(String actorId) {
    }

    record ReceiveMessageRequest(@NotBlank String externalMessageId, String rawPayload, @NotBlank String actorId) {
    }

    record EndpointResponse(
            String endpointId, String tenantId, String laboratoryId, String endpointName, String protocol,
            String direction, String status, Instant createdAt, Instant updatedAt) {
        static EndpointResponse from(IntegrationEndpoint entity) {
            return new EndpointResponse(
                    entity.endpointId(), entity.tenantId(), entity.laboratoryId(), entity.endpointName(),
                    entity.protocol(), entity.direction(), entity.status(),
                    entity.audit().createdAt().atZone(java.time.ZoneOffset.UTC).toInstant(),
                    entity.audit().updatedAt().atZone(java.time.ZoneOffset.UTC).toInstant());
        }
    }

    record MessageResponse(
            String messageId, String endpointId, String externalMessageId, String correlationId,
            String normalizationStatus, String canonicalErrorCode, int retryCount) {
        static MessageResponse from(IntegrationMessageRecord entity) {
            return new MessageResponse(
                    entity.messageId(), entity.endpointId(), entity.externalMessageId(), entity.correlationId(),
                    entity.normalizationStatus(), entity.canonicalErrorCode(), entity.retryCount());
        }
    }
}
