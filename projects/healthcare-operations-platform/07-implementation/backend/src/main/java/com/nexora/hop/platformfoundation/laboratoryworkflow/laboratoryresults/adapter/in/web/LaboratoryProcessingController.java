package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.adapter.in.web;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.CaptureResultCommand;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.LaboratoryResultsService;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application.RecordIncidentCommand;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;

/**
 * REST controller for Laboratory Processing (BCM-LAB-006).
 *
 * <p>Generatable endpoints: listProcessingWorklist (GET), getResult (GET), recordIncident (POST).
 * <p>Custom-rule stub: captureResult (POST) — full snapshot capture, received-sample guard
 *   and device-message boundary deferred to MVP-MOD-006-BE-002 (CUS-LPR-006-01/02/03).
 */
@RestController
@RequestMapping("/api/clinical-operations/laboratory-results")
class LaboratoryProcessingController {

    private final LaboratoryResultsService service;

    LaboratoryProcessingController(LaboratoryResultsService service) {
        this.service = service;
    }

    // Generatable: listProcessingWorklist
    @GetMapping("/processing-worklist")
    ResponseEntity<List<LaboratoryResult>> listProcessingWorklist(
            @RequestParam @NotBlank String tenantId,
            @RequestParam @NotBlank String laboratoryId) {
        return ResponseEntity.ok(service.listProcessingWorklist(tenantId, laboratoryId));
    }

    // Generatable: listByStatus (BCM-RES-001 result search/worklist facade)
    @GetMapping
    ResponseEntity<List<LaboratoryResult>> listByStatus(
            @RequestParam @NotBlank String tenantId,
            @RequestParam @NotBlank String status) {
        return ResponseEntity.ok(service.listByStatus(tenantId, status));
    }

    // Generatable: getResult
    @GetMapping("/{resultId}")
    ResponseEntity<LaboratoryResult> getResult(
            @PathVariable String resultId,
            @RequestParam @NotBlank String tenantId) {
        return ResponseEntity.ok(service.getResult(resultId, tenantId));
    }

    // Custom-rule stub: captureResult (CUS-LPR-006-01/02/03 deferred to BE-002)
    @PostMapping
    ResponseEntity<LaboratoryResult> captureResult(@Valid @RequestBody CaptureResultRequest request) {
        LaboratoryResult created = service.captureResult(new CaptureResultCommand(
                request.tenantId(), request.laboratoryId(), request.branchId(),
                request.orderId(), request.sampleId(),
                request.rawValue(), request.numericValue(), request.unit(),
                request.captureSource(), request.capturedBy(), request.deviceReference(),
                request.testDefinitionId(), request.analyteId(), request.analyteName(),
                request.referenceRangeId()));
        return ResponseEntity
                .created(URI.create("/api/clinical-operations/laboratory-results/" + created.resultId()))
                .body(created);
    }

    // Generatable: recordIncident
    @PostMapping("/{resultId}/incidents")
    ResponseEntity<LaboratoryResult> recordIncident(
            @PathVariable String resultId,
            @Valid @RequestBody RecordIncidentRequest request) {
        return ResponseEntity.ok(service.recordIncident(new RecordIncidentCommand(
                resultId, request.tenantId(), request.incidentType(),
                request.notes(), request.recordedBy())));
    }

    // -------------------------------------------------------------------------
    // Request records
    // -------------------------------------------------------------------------

    record CaptureResultRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            @NotBlank String orderId,
            @NotBlank String sampleId,
            @NotBlank String rawValue,
            BigDecimal numericValue,
            @NotBlank String unit,
            @NotBlank String captureSource,
            String capturedBy,
            String deviceReference,
            String testDefinitionId,
            String analyteId,
            String analyteName,
            String referenceRangeId) {
    }

    record RecordIncidentRequest(
            @NotBlank String tenantId,
            @NotBlank String incidentType,
            String notes,
            @NotBlank String recordedBy) {
    }
}
