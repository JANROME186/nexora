package com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.application.CalibrationManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.domain.CalibrationEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rendered controller for bcm-qlt-003-calibration-management/openapi-source.yaml. */
@RestController
@RequestMapping("/api/quality/calibrations")
class CalibrationController {

  private final CalibrationManagementService service;

  CalibrationController(CalibrationManagementService service) {
    this.service = service;
  }

  @PostMapping("/items/{inventoryItemId}/calibrations")
  ResponseEntity<CalibrationResponse> recordCalibration(
      @PathVariable String inventoryItemId, @Valid @RequestBody RecordCalibrationRequest request) {
    return ResponseEntity.ok(
        CalibrationResponse.from(
            service.recordCalibration(
                inventoryItemId,
                new CalibrationManagementService.RecordCalibrationCommand(
                    request.calibrationStandardRef(),
                    request.performedBy(),
                    request.performedAt(),
                    request.result(),
                    request.nextDueDate(),
                    request.certificateReference()))));
  }

  @GetMapping("/items/{inventoryItemId}/calibrations")
  ResponseEntity<List<CalibrationResponse>> listCalibrations(@PathVariable String inventoryItemId) {
    return ResponseEntity.ok(
        service.listCalibrations(inventoryItemId).stream()
            .map(CalibrationResponse::from)
            .toList());
  }

  record RecordCalibrationRequest(
      @NotBlank String calibrationStandardRef,
      @NotBlank String performedBy,
      LocalDateTime performedAt,
      @NotBlank String result,
      LocalDate nextDueDate,
      String certificateReference) {}

  record CalibrationResponse(
      String calibrationEventId,
      String inventoryItemId,
      String tenantId,
      String branchId,
      String calibrationStandardRef,
      String performedBy,
      Instant performedAt,
      String result,
      LocalDate nextDueDate,
      String certificateReference) {
    static CalibrationResponse from(CalibrationEvent event) {
      return new CalibrationResponse(
          event.calibrationEventId(),
          event.inventoryItemId(),
          event.tenantId(),
          event.branchId(),
          event.calibrationStandardRef(),
          event.performedBy(),
          event.performedAt().atZone(ZoneOffset.UTC).toInstant(),
          event.result(),
          event.nextDueDate(),
          event.certificateReference());
    }
  }
}
