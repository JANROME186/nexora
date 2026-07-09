package com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.adapter.in.web;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.application.AssignPreparationCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.application.CreatePreparationInstructionCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.application.PatientPreparationManagementService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.application.UpdatePreparationInstructionCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain.PreparationInstruction;

/**
 * Rendered controller for bcm-svc-005-patient-preparation-management/openapi-source.yaml
 * (base path /api/catalog/preparations).
 */
@RestController
@RequestMapping("/api/catalog/preparations")
class PreparationInstructionController {

    private final PatientPreparationManagementService service;

    PreparationInstructionController(PatientPreparationManagementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<PreparationInstructionResponse>> listPreparations(@RequestParam String laboratoryId) {
        return ResponseEntity.ok(service.list(laboratoryId).stream().map(PreparationInstructionResponse::from).toList());
    }

    @GetMapping("/{preparationId}")
    ResponseEntity<PreparationInstructionResponse> getPreparation(@PathVariable String preparationId) {
        return ResponseEntity.ok(PreparationInstructionResponse.from(service.get(preparationId)));
    }

    @PostMapping
    ResponseEntity<PreparationInstructionResponse> createPreparation(
            @Valid @RequestBody CreatePreparationInstructionRequest request) {
        PreparationInstruction created = service.create(new CreatePreparationInstructionCommand(
                request.tenantId(), request.laboratoryId(), request.code(), request.titleEn(), request.titleEs(),
                request.instructionTextEn(), request.instructionTextEs(), request.category(), request.durationHours()));
        return ResponseEntity.created(URI.create("/api/catalog/preparations/" + created.preparationId()))
                .body(PreparationInstructionResponse.from(created));
    }

    @PutMapping("/{preparationId}")
    ResponseEntity<PreparationInstructionResponse> updatePreparation(
            @PathVariable String preparationId, @Valid @RequestBody UpdatePreparationInstructionRequest request) {
        PreparationInstruction updated = service.update(preparationId, new UpdatePreparationInstructionCommand(
                request.code(), request.titleEn(), request.titleEs(), request.instructionTextEn(),
                request.instructionTextEs(), request.category(), request.durationHours()));
        return ResponseEntity.ok(PreparationInstructionResponse.from(updated));
    }

    @PostMapping("/{preparationId}/assignments")
    ResponseEntity<Void> assignPreparation(
            @PathVariable String preparationId, @Valid @RequestBody AssignPreparationRequest request) {
        service.assign(preparationId, new AssignPreparationCommand(request.targetType(), request.targetRefId()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{preparationId}/publish")
    ResponseEntity<PreparationInstructionResponse> publishPreparation(@PathVariable String preparationId) {
        return ResponseEntity.ok(PreparationInstructionResponse.from(service.publish(preparationId)));
    }

    @PostMapping("/{preparationId}/deprecate")
    ResponseEntity<PreparationInstructionResponse> deprecatePreparation(@PathVariable String preparationId) {
        return ResponseEntity.ok(PreparationInstructionResponse.from(service.deprecate(preparationId)));
    }

    record AssignPreparationRequest(@NotBlank String targetType, @NotBlank String targetRefId) {
    }

    record CreatePreparationInstructionRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String code,
            @NotBlank String titleEn,
            @NotBlank String titleEs,
            @NotBlank String instructionTextEn,
            @NotBlank String instructionTextEs,
            @NotBlank String category,
            Integer durationHours) {
    }

    record UpdatePreparationInstructionRequest(
            @NotBlank String code,
            @NotBlank String titleEn,
            @NotBlank String titleEs,
            @NotBlank String instructionTextEn,
            @NotBlank String instructionTextEs,
            @NotBlank String category,
            Integer durationHours) {
    }

    record PreparationInstructionResponse(
            String preparationId,
            String tenantId,
            String laboratoryId,
            String code,
            String titleEn,
            String titleEs,
            String instructionTextEn,
            String instructionTextEs,
            String category,
            Integer durationHours,
            String status,
            int version,
            Instant createdAt,
            Instant updatedAt) {
        static PreparationInstructionResponse from(PreparationInstruction entity) {
            return new PreparationInstructionResponse(
                    entity.preparationId(),
                    entity.tenantId(),
                    entity.laboratoryId(),
                    entity.code(),
                    entity.title().en(),
                    entity.title().es(),
                    entity.instructionText().en(),
                    entity.instructionText().es(),
                    entity.category(),
                    entity.durationHours(),
                    entity.status(),
                    entity.version(),
                    entity.createdAt(),
                    entity.updatedAt());
        }
    }
}
