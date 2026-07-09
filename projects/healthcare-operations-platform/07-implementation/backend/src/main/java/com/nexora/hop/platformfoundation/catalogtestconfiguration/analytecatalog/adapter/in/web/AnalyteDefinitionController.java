package com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.adapter.in.web;

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

import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.application.AnalyteCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.application.CodedValueInput;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.application.CreateAnalyteDefinitionCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.application.UpdateAnalyteDefinitionCommand;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteCodedValue;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain.AnalyteResultConstraint;

/** Rendered controller for bcm-svc-004-analyte-catalog/openapi-source.yaml (base path /api/catalog/analytes). */
@RestController
@RequestMapping("/api/catalog/analytes")
class AnalyteDefinitionController {

    private final AnalyteCatalogService service;

    AnalyteDefinitionController(AnalyteCatalogService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<AnalyteDefinitionResponse>> listAnalytes(@RequestParam String laboratoryId) {
        return ResponseEntity.ok(service.list(laboratoryId).stream().map(this::toResponse).toList());
    }

    @GetMapping("/{analyteId}")
    ResponseEntity<AnalyteDefinitionResponse> getAnalyte(@PathVariable String analyteId) {
        return ResponseEntity.ok(toResponse(service.get(analyteId)));
    }

    @PostMapping
    ResponseEntity<AnalyteDefinitionResponse> createAnalyte(@Valid @RequestBody CreateAnalyteDefinitionRequest request) {
        AnalyteDefinition created = service.create(new CreateAnalyteDefinitionCommand(
                request.tenantId(), request.laboratoryId(), request.code(), request.nameEn(), request.nameEs(),
                request.loincCode(), request.resultDataType(), request.measurementUnit(), request.decimalPrecision(),
                request.minValue(), request.maxValue(), toInputs(request.codedValues())));
        return ResponseEntity.created(URI.create("/api/catalog/analytes/" + created.analyteId()))
                .body(toResponse(created));
    }

    @PutMapping("/{analyteId}")
    ResponseEntity<AnalyteDefinitionResponse> updateAnalyte(
            @PathVariable String analyteId, @Valid @RequestBody UpdateAnalyteDefinitionRequest request) {
        AnalyteDefinition updated = service.update(analyteId, new UpdateAnalyteDefinitionCommand(
                request.code(), request.nameEn(), request.nameEs(), request.loincCode(), request.resultDataType(),
                request.measurementUnit(), request.decimalPrecision(), request.minValue(), request.maxValue(),
                toInputs(request.codedValues())));
        return ResponseEntity.ok(toResponse(updated));
    }

    @PostMapping("/{analyteId}/publish")
    ResponseEntity<AnalyteDefinitionResponse> publishAnalyte(@PathVariable String analyteId) {
        return ResponseEntity.ok(toResponse(service.publish(analyteId)));
    }

    @PostMapping("/{analyteId}/deprecate")
    ResponseEntity<AnalyteDefinitionResponse> deprecateAnalyte(@PathVariable String analyteId) {
        return ResponseEntity.ok(toResponse(service.deprecate(analyteId)));
    }

    @GetMapping("/{analyteId}/published-snapshot")
    ResponseEntity<AnalyteDefinitionResponse> getPublishedAnalyteSnapshot(@PathVariable String analyteId) {
        return ResponseEntity.ok(toResponse(service.getPublishedSnapshot(analyteId)));
    }

    private AnalyteDefinitionResponse toResponse(AnalyteDefinition entity) {
        AnalyteResultConstraint constraint = service.getConstraint(entity.analyteId());
        List<AnalyteCodedValue> codedValues = service.getCodedValues(entity.analyteId());
        return AnalyteDefinitionResponse.from(entity, constraint, codedValues);
    }

    private static List<CodedValueInput> toInputs(List<CodedValueRequest> codedValues) {
        if (codedValues == null) {
            return List.of();
        }
        return codedValues.stream()
                .map(value -> new CodedValueInput(value.code(), value.displayEn(), value.displayEs()))
                .toList();
    }

    record CodedValueRequest(@NotBlank String code, @NotBlank String displayEn, @NotBlank String displayEs) {
    }

    record CreateAnalyteDefinitionRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String code,
            @NotBlank String nameEn,
            @NotBlank String nameEs,
            String loincCode,
            @NotBlank String resultDataType,
            String measurementUnit,
            Integer decimalPrecision,
            BigDecimal minValue,
            BigDecimal maxValue,
            List<CodedValueRequest> codedValues) {
    }

    record UpdateAnalyteDefinitionRequest(
            @NotBlank String code,
            @NotBlank String nameEn,
            @NotBlank String nameEs,
            String loincCode,
            @NotBlank String resultDataType,
            String measurementUnit,
            Integer decimalPrecision,
            BigDecimal minValue,
            BigDecimal maxValue,
            List<CodedValueRequest> codedValues) {
    }

    record CodedValueResponse(String codedValueId, String code, String displayEn, String displayEs) {
        static CodedValueResponse from(AnalyteCodedValue value) {
            return new CodedValueResponse(value.codedValueId(), value.code(), value.display().en(), value.display().es());
        }
    }

    record AnalyteDefinitionResponse(
            String analyteId,
            String tenantId,
            String laboratoryId,
            String code,
            String nameEn,
            String nameEs,
            String loincCode,
            String resultDataType,
            String measurementUnit,
            Integer decimalPrecision,
            BigDecimal minValue,
            BigDecimal maxValue,
            List<CodedValueResponse> codedValues,
            String status,
            int version,
            Instant createdAt,
            Instant updatedAt) {
        static AnalyteDefinitionResponse from(
                AnalyteDefinition entity, AnalyteResultConstraint constraint, List<AnalyteCodedValue> codedValues) {
            return new AnalyteDefinitionResponse(
                    entity.analyteId(),
                    entity.tenantId(),
                    entity.laboratoryId(),
                    entity.code(),
                    entity.name().en(),
                    entity.name().es(),
                    entity.loincCode(),
                    entity.resultDataType(),
                    entity.measurementUnit(),
                    entity.decimalPrecision(),
                    constraint == null ? null : constraint.minValue(),
                    constraint == null ? null : constraint.maxValue(),
                    codedValues.stream().map(CodedValueResponse::from).toList(),
                    entity.status(),
                    entity.version(),
                    entity.createdAt(),
                    entity.updatedAt());
        }
    }
}
