package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.adapter.in.web;

import java.math.BigDecimal;
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

import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.application.CreateSampleRequirementCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.application.SampleCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.application.UpdateSampleRequirementCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleRequirement;

/**
 * Rendered controller for the SampleRequirement resource of
 * bcm-svc-007-sample-catalog/openapi-source.yaml (base path /api/catalog/samples/requirements).
 */
@RestController
@RequestMapping("/api/catalog/samples/requirements")
class SampleRequirementController {

    private final SampleCatalogService service;

    SampleRequirementController(SampleCatalogService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<SampleRequirementResponse>> listSampleRequirements(@RequestParam String laboratoryId) {
        return ResponseEntity.ok(
                service.listSampleRequirements(laboratoryId).stream().map(SampleRequirementResponse::from).toList());
    }

    @GetMapping("/{requirementId}")
    ResponseEntity<SampleRequirementResponse> getSampleRequirement(@PathVariable String requirementId) {
        return ResponseEntity.ok(SampleRequirementResponse.from(service.getSampleRequirement(requirementId)));
    }

    @PostMapping
    ResponseEntity<SampleRequirementResponse> createSampleRequirement(
            @Valid @RequestBody CreateSampleRequirementRequest request) {
        SampleRequirement created = service.createSampleRequirement(new CreateSampleRequirementCommand(
                request.tenantId(), request.laboratoryId(), request.sampleTypeRefId(), request.minVolumeMl(),
                request.containerRefId(), request.handlingInstructionsEn(), request.handlingInstructionsEs(),
                request.storageTemperature()));
        return ResponseEntity.created(URI.create("/api/catalog/samples/requirements/" + created.requirementId()))
                .body(SampleRequirementResponse.from(created));
    }

    @PutMapping("/{requirementId}")
    ResponseEntity<SampleRequirementResponse> updateSampleRequirement(
            @PathVariable String requirementId, @Valid @RequestBody UpdateSampleRequirementRequest request) {
        SampleRequirement updated = service.updateSampleRequirement(requirementId, new UpdateSampleRequirementCommand(
                request.sampleTypeRefId(), request.minVolumeMl(), request.containerRefId(),
                request.handlingInstructionsEn(), request.handlingInstructionsEs(), request.storageTemperature()));
        return ResponseEntity.ok(SampleRequirementResponse.from(updated));
    }

    @PostMapping("/{requirementId}/publish")
    ResponseEntity<SampleRequirementResponse> publishSampleRequirement(@PathVariable String requirementId) {
        return ResponseEntity.ok(SampleRequirementResponse.from(service.publishSampleRequirement(requirementId)));
    }

    @GetMapping("/{requirementId}/published-snapshot")
    ResponseEntity<SampleRequirementResponse> getPublishedSampleRequirementSnapshot(
            @PathVariable String requirementId) {
        return ResponseEntity.ok(
                SampleRequirementResponse.from(service.getPublishedSampleRequirementSnapshot(requirementId)));
    }

    record CreateSampleRequirementRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String sampleTypeRefId,
            BigDecimal minVolumeMl,
            String containerRefId,
            String handlingInstructionsEn,
            String handlingInstructionsEs,
            String storageTemperature) {
    }

    record UpdateSampleRequirementRequest(
            @NotBlank String sampleTypeRefId,
            BigDecimal minVolumeMl,
            String containerRefId,
            String handlingInstructionsEn,
            String handlingInstructionsEs,
            String storageTemperature) {
    }

    record SampleRequirementResponse(
            String requirementId,
            String tenantId,
            String laboratoryId,
            String sampleTypeRefId,
            BigDecimal minVolumeMl,
            String containerRefId,
            String handlingInstructionsEn,
            String handlingInstructionsEs,
            String storageTemperature,
            String status,
            int version,
            Instant createdAt,
            Instant updatedAt) {
        static SampleRequirementResponse from(SampleRequirement entity) {
            return new SampleRequirementResponse(
                    entity.requirementId(),
                    entity.tenantId(),
                    entity.laboratoryId(),
                    entity.sampleTypeRefId(),
                    entity.minVolumeMl(),
                    entity.containerRefId(),
                    entity.handlingInstructions() == null ? null : entity.handlingInstructions().en(),
                    entity.handlingInstructions() == null ? null : entity.handlingInstructions().es(),
                    entity.storageTemperature(),
                    entity.status(),
                    entity.version(),
                    entity.createdAt(),
                    entity.updatedAt());
        }
    }
}
