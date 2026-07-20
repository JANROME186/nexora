package com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.application.EquipmentManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain.EquipmentAvailabilityChange;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.EquipmentProfile;
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

/** Rendered controller for bcm-qlt-004-equipment-management/openapi-source.yaml. */
@RestController
@RequestMapping("/api/quality/equipment")
class EquipmentController {

  private final EquipmentManagementService service;

  EquipmentController(EquipmentManagementService service) {
    this.service = service;
  }

  @PostMapping("/items/{inventoryItemId}/equipment-profile")
  ResponseEntity<EquipmentProfileResponse> setEquipmentProfile(
      @PathVariable String inventoryItemId, @Valid @RequestBody SetEquipmentProfileRequest request) {
    return ResponseEntity.ok(
        EquipmentProfileResponse.from(
            service
                .setEquipmentProfile(
                    inventoryItemId,
                    new EquipmentManagementService.SetEquipmentProfileCommand(
                        request.assetTag(),
                        request.serialNumber(),
                        request.manufacturer(),
                        request.model(),
                        request.installedAt(),
                        request.location(),
                        request.availabilityStatus(),
                        request.actorId()))
                .equipmentProfile()));
  }

  @PostMapping("/items/{inventoryItemId}/availability")
  ResponseEntity<EquipmentProfileResponse> changeAvailability(
      @PathVariable String inventoryItemId, @Valid @RequestBody ChangeAvailabilityRequest request) {
    return ResponseEntity.ok(
        EquipmentProfileResponse.from(
            service
                .changeAvailability(
                    inventoryItemId,
                    new EquipmentManagementService.ChangeEquipmentAvailabilityCommand(
                        request.newStatus(), request.reasonCode(), request.actorId()))
                .equipmentProfile()));
  }

  @GetMapping("/items/{inventoryItemId}/equipment-profile")
  ResponseEntity<EquipmentProfileResponse> getEquipmentProfile(@PathVariable String inventoryItemId) {
    return ResponseEntity.ok(EquipmentProfileResponse.from(service.getEquipmentProfile(inventoryItemId)));
  }

  @GetMapping("/items/{inventoryItemId}/availability")
  ResponseEntity<List<AvailabilityChangeResponse>> listAvailabilityChanges(
      @PathVariable String inventoryItemId) {
    return ResponseEntity.ok(
        service.listAvailabilityChanges(inventoryItemId).stream()
            .map(AvailabilityChangeResponse::from)
            .toList());
  }

  record SetEquipmentProfileRequest(
      @NotBlank String assetTag,
      String serialNumber,
      String manufacturer,
      String model,
      LocalDateTime installedAt,
      String location,
      @NotBlank String availabilityStatus,
      @NotBlank String actorId) {}

  record ChangeAvailabilityRequest(
      @NotBlank String newStatus, @NotBlank String reasonCode, @NotBlank String actorId) {}

  record EquipmentProfileResponse(
      String assetTag,
      String serialNumber,
      String manufacturer,
      String model,
      Instant installedAt,
      String location,
      String availabilityStatus) {
    static EquipmentProfileResponse from(EquipmentProfile profile) {
      return new EquipmentProfileResponse(
          profile.assetTag(),
          profile.serialNumber(),
          profile.manufacturer(),
          profile.model(),
          profile.installedAt() == null
              ? null
              : profile.installedAt().atZone(ZoneOffset.UTC).toInstant(),
          profile.location(),
          profile.availabilityStatus());
    }
  }

  record AvailabilityChangeResponse(
      String changeId,
      String inventoryItemId,
      String previousStatus,
      String newStatus,
      String reasonCode,
      String changedBy,
      Instant changedAt) {
    static AvailabilityChangeResponse from(EquipmentAvailabilityChange change) {
      return new AvailabilityChangeResponse(
          change.changeId(),
          change.inventoryItemId(),
          change.previousStatus(),
          change.newStatus(),
          change.reasonCode(),
          change.changedBy(),
          change.changedAt().atZone(ZoneOffset.UTC).toInstant());
    }
  }
}
