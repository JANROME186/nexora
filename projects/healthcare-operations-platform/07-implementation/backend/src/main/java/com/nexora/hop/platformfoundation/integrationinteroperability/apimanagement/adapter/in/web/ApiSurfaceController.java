package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.in.web;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.application.ApiManagementService;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.ApiSurfaceRegistration;

/** Rendered controller for bcm-plt-005-api-management/openapi-source.yaml (ApiSurfaceRegistration resource). */
@RestController
@RequestMapping("/api/platform/api-management/operations")
class ApiSurfaceController {

    private final ApiManagementService service;

    ApiSurfaceController(ApiManagementService service) {
        this.service = service;
    }

    @PostMapping("/{operationId}/classification")
    ResponseEntity<RegistrationResponse> classifyApiOperation(
            @PathVariable String operationId, @Valid @RequestBody ClassifyRequest request) {
        ApiSurfaceRegistration saved = service.classifyOperation(
                operationId, request.ownerCapability(), request.classification(), request.apiVersion(),
                request.tenantId(), request.actorId());
        return ResponseEntity.ok(RegistrationResponse.from(saved));
    }

    @GetMapping
    ResponseEntity<List<RegistrationResponse>> listApiOperations() {
        return ResponseEntity.ok(service.listOperations().stream().map(RegistrationResponse::from).toList());
    }

    @PostMapping("/{operationId}/deprecation")
    ResponseEntity<RegistrationResponse> scheduleApiDeprecation(
            @PathVariable String operationId, @Valid @RequestBody ScheduleDeprecationRequest request) {
        ApiSurfaceRegistration saved = service.scheduleDeprecation(
                operationId, request.deprecationWindowFrom(), request.deprecationWindowTo(),
                request.migrationNote(), request.actorId());
        return ResponseEntity.ok(RegistrationResponse.from(saved));
    }

    @PostMapping("/{operationId}/retirement")
    ResponseEntity<RegistrationResponse> retireApiOperation(
            @PathVariable String operationId, @RequestBody(required = false) ActorRequest request) {
        String actorId = request == null ? "system" : request.actorId();
        return ResponseEntity.ok(RegistrationResponse.from(service.retireDeprecatedOperation(operationId, actorId)));
    }

    record ActorRequest(String actorId) {
    }

    record ClassifyRequest(
            @NotBlank String ownerCapability, @NotBlank String classification, @NotBlank String apiVersion,
            String tenantId, @NotBlank String actorId) {
    }

    record ScheduleDeprecationRequest(
            LocalDateTime deprecationWindowFrom, LocalDateTime deprecationWindowTo, String migrationNote,
            @NotBlank String actorId) {
    }

    record RegistrationResponse(
            String registrationId, String tenantId, String ownerCapability, String operationId,
            String classification, String apiVersion, String deprecationStatus,
            LocalDateTime deprecationWindowFrom, LocalDateTime deprecationWindowTo, String migrationNote) {
        static RegistrationResponse from(ApiSurfaceRegistration entity) {
            return new RegistrationResponse(
                    entity.registrationId(), entity.tenantId(), entity.ownerCapability(), entity.operationId(),
                    entity.classification(), entity.apiVersion(), entity.deprecationStatus(),
                    entity.deprecationWindowFrom(), entity.deprecationWindowTo(), entity.migrationNote());
        }
    }
}
