package com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.application.InternalQualityControlService;
import com.nexora.hop.platformfoundation.inventoryquality.internalqualitycontrols.domain.QualityControlRun;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Rendered controller for bcm-qlt-001-internal-quality-controls/openapi-source.yaml. */
@RestController
@RequestMapping("/api/quality/internal-controls")
class InternalQualityControlController {

  private final InternalQualityControlService service;

  InternalQualityControlController(InternalQualityControlService service) {
    this.service = service;
  }

  @PostMapping
  ResponseEntity<QualityControlRunResponse> recordQualityControlRun(
      @Valid @RequestBody RecordQualityControlRunRequest request) {
    return ResponseEntity.ok(
        QualityControlRunResponse.from(
            service.recordQualityControlRun(
                new InternalQualityControlService.RecordQualityControlRunCommand(
                    request.tenantId(),
                    request.laboratoryId(),
                    request.branchId(),
                    request.testDefinitionId(),
                    request.controlMaterialStockLotId(),
                    request.measuredValue(),
                    request.expectedMin(),
                    request.expectedMax(),
                    request.linkedLaboratoryResultIds(),
                    request.performedBy(),
                    request.performedAt(),
                    request.evidenceReference()))));
  }

  @PostMapping("/{qcRunId}/override")
  ResponseEntity<QualityControlRunResponse> overrideAcceptanceDecision(
      @PathVariable String qcRunId, @Valid @RequestBody OverrideAcceptanceDecisionRequest request) {
    return ResponseEntity.ok(
        QualityControlRunResponse.from(
            service.overrideAcceptanceDecision(
                qcRunId,
                new InternalQualityControlService.OverrideAcceptanceDecisionCommand(
                    request.acceptanceDecision(),
                    request.overrideReason(),
                    request.supervisorId(),
                    request.supervisorScoped()))));
  }

  @GetMapping("/{qcRunId}")
  ResponseEntity<QualityControlRunResponse> getQualityControlRun(@PathVariable String qcRunId) {
    return ResponseEntity.ok(QualityControlRunResponse.from(service.getQualityControlRun(qcRunId)));
  }

  @GetMapping
  ResponseEntity<List<QualityControlRunResponse>> listQualityControlRuns(
      @RequestParam String tenantId,
      @RequestParam String laboratoryId,
      @RequestParam String branchId) {
    return ResponseEntity.ok(
        service.listQualityControlRuns(tenantId, laboratoryId, branchId).stream()
            .map(QualityControlRunResponse::from)
            .toList());
  }

  record RecordQualityControlRunRequest(
      @NotBlank String tenantId,
      @NotBlank String laboratoryId,
      @NotBlank String branchId,
      @NotBlank String testDefinitionId,
      @NotBlank String controlMaterialStockLotId,
      @NotNull BigDecimal measuredValue,
      @NotNull BigDecimal expectedMin,
      @NotNull BigDecimal expectedMax,
      List<String> linkedLaboratoryResultIds,
      @NotBlank String performedBy,
      LocalDateTime performedAt,
      String evidenceReference) {}

  record OverrideAcceptanceDecisionRequest(
      @NotBlank String acceptanceDecision,
      @NotBlank String overrideReason,
      @NotBlank String supervisorId,
      boolean supervisorScoped) {}

  record QualityControlRunResponse(
      String qcRunId,
      String tenantId,
      String laboratoryId,
      String branchId,
      String testDefinitionId,
      String controlMaterialStockLotId,
      BigDecimal measuredValue,
      BigDecimal expectedMin,
      BigDecimal expectedMax,
      String ruleEvaluation,
      String acceptanceDecision,
      List<String> linkedLaboratoryResultIds,
      String performedBy,
      Instant performedAt,
      String evidenceReference,
      String overrideReason,
      String overrideBy) {
    static QualityControlRunResponse from(QualityControlRun run) {
      return new QualityControlRunResponse(
          run.qcRunId(),
          run.tenantId(),
          run.laboratoryId(),
          run.branchId(),
          run.testDefinitionId(),
          run.controlMaterialStockLotId(),
          run.measuredValue(),
          run.expectedRange().min(),
          run.expectedRange().max(),
          run.ruleEvaluation(),
          run.acceptanceDecision(),
          run.linkedLaboratoryResultIds(),
          run.performedBy(),
          run.performedAt().atZone(ZoneOffset.UTC).toInstant(),
          run.evidenceReference(),
          run.overrideReason(),
          run.overrideBy());
    }
  }
}
