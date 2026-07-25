package com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.application.MaintenanceManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.domain.MaintenanceEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import org.springframework.web.bind.annotation.RestController;

/** Rendered controller for bcm-qlt-005-maintenance-management/openapi-source.md. */
@RestController
@RequestMapping("/api/quality/maintenance")
class MaintenanceController {

  private final MaintenanceManagementService service;

  MaintenanceController(MaintenanceManagementService service) {
    this.service = service;
  }

  @PostMapping("/items/{inventoryItemId}/maintenance")
  ResponseEntity<MaintenanceResponse> recordMaintenance(
      @PathVariable String inventoryItemId, @Valid @RequestBody RecordMaintenanceRequest request) {
    return ResponseEntity.ok(
        MaintenanceResponse.from(
            service.recordMaintenance(
                inventoryItemId,
                new MaintenanceManagementService.RecordMaintenanceCommand(
                    request.maintenanceType(),
                    request.performedBy(),
                    request.externalTechnicianRef(),
                    request.description(),
                    request.startedAt(),
                    request.completedAt(),
                    request.downtimeMinutes(),
                    request.nextScheduledAt()))));
  }

  @PostMapping("/maintenance/{maintenanceEventId}/complete")
  ResponseEntity<MaintenanceResponse> completeMaintenance(
      @PathVariable String maintenanceEventId, @Valid @RequestBody CompleteMaintenanceRequest request) {
    return ResponseEntity.ok(
        MaintenanceResponse.from(
            service.completeMaintenance(
                maintenanceEventId,
                new MaintenanceManagementService.CompleteMaintenanceCommand(
                    request.actorId(),
                    request.completedAt(),
                    request.downtimeMinutes(),
                    request.nextScheduledAt()))));
  }

  @GetMapping("/items/{inventoryItemId}/maintenance")
  ResponseEntity<List<MaintenanceResponse>> listMaintenance(@PathVariable String inventoryItemId) {
    return ResponseEntity.ok(
        service.listMaintenance(inventoryItemId).stream().map(MaintenanceResponse::from).toList());
  }

  record RecordMaintenanceRequest(
      @NotBlank String maintenanceType,
      String performedBy,
      String externalTechnicianRef,
      @NotBlank String description,
      LocalDateTime startedAt,
      LocalDateTime completedAt,
      Integer downtimeMinutes,
      LocalDateTime nextScheduledAt) {}

  record CompleteMaintenanceRequest(
      @NotBlank String actorId,
      LocalDateTime completedAt,
      Integer downtimeMinutes,
      LocalDateTime nextScheduledAt) {}

  record MaintenanceResponse(
      String maintenanceEventId,
      String inventoryItemId,
      String tenantId,
      String branchId,
      String maintenanceType,
      String performedBy,
      String externalTechnicianRef,
      String description,
      Instant startedAt,
      Instant completedAt,
      Integer downtimeMinutes,
      Instant nextScheduledAt) {
    static MaintenanceResponse from(MaintenanceEvent event) {
      return new MaintenanceResponse(
          event.maintenanceEventId(),
          event.inventoryItemId(),
          event.tenantId(),
          event.branchId(),
          event.maintenanceType(),
          event.performedBy(),
          event.externalTechnicianRef(),
          event.description(),
          event.startedAt().atZone(ZoneOffset.UTC).toInstant(),
          event.completedAt() == null ? null : event.completedAt().atZone(ZoneOffset.UTC).toInstant(),
          event.downtimeMinutes(),
          event.nextScheduledAt() == null
              ? null
              : event.nextScheduledAt().atZone(ZoneOffset.UTC).toInstant());
    }
  }
}
