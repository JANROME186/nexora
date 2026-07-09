package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.adapter.in.web;

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

import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.application.CreateSampleTypeCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.application.SampleCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.application.UpdateSampleTypeCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain.SampleType;

/**
 * Rendered controller for the SampleType resource of
 * bcm-svc-007-sample-catalog/openapi-source.yaml (base path /api/catalog/samples/types).
 */
@RestController
@RequestMapping("/api/catalog/samples/types")
class SampleTypeController {

    private final SampleCatalogService service;

    SampleTypeController(SampleCatalogService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<SampleTypeResponse>> listSampleTypes(@RequestParam String laboratoryId) {
        return ResponseEntity.ok(service.listSampleTypes(laboratoryId).stream().map(SampleTypeResponse::from).toList());
    }

    @PostMapping
    ResponseEntity<SampleTypeResponse> createSampleType(@Valid @RequestBody CreateSampleTypeRequest request) {
        SampleType created = service.createSampleType(new CreateSampleTypeCommand(
                request.tenantId(), request.laboratoryId(), request.code(), request.nameEn(), request.nameEs(),
                request.matrix()));
        return ResponseEntity.created(URI.create("/api/catalog/samples/types/" + created.sampleTypeId()))
                .body(SampleTypeResponse.from(created));
    }

    @PutMapping("/{sampleTypeId}")
    ResponseEntity<SampleTypeResponse> updateSampleType(
            @PathVariable String sampleTypeId, @Valid @RequestBody UpdateSampleTypeRequest request) {
        SampleType updated = service.updateSampleType(sampleTypeId, new UpdateSampleTypeCommand(
                request.code(), request.nameEn(), request.nameEs(), request.matrix()));
        return ResponseEntity.ok(SampleTypeResponse.from(updated));
    }

    @PostMapping("/{sampleTypeId}/publish")
    ResponseEntity<SampleTypeResponse> publishSampleType(@PathVariable String sampleTypeId) {
        return ResponseEntity.ok(SampleTypeResponse.from(service.publishSampleType(sampleTypeId)));
    }

    record CreateSampleTypeRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String code,
            @NotBlank String nameEn,
            @NotBlank String nameEs,
            @NotBlank String matrix) {
    }

    record UpdateSampleTypeRequest(
            @NotBlank String code, @NotBlank String nameEn, @NotBlank String nameEs, @NotBlank String matrix) {
    }

    record SampleTypeResponse(
            String sampleTypeId,
            String tenantId,
            String laboratoryId,
            String code,
            String nameEn,
            String nameEs,
            String matrix,
            String status,
            int version,
            Instant createdAt,
            Instant updatedAt) {
        static SampleTypeResponse from(SampleType entity) {
            return new SampleTypeResponse(
                    entity.sampleTypeId(),
                    entity.tenantId(),
                    entity.laboratoryId(),
                    entity.code(),
                    entity.name().en(),
                    entity.name().es(),
                    entity.matrix(),
                    entity.status(),
                    entity.version(),
                    entity.createdAt(),
                    entity.updatedAt());
        }
    }
}
