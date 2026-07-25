package com.nexora.hop.platformfoundation.inventoryquality.reagentmanagement.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.ReagentProfile;
import com.nexora.hop.platformfoundation.inventoryquality.reagentmanagement.application.ReagentManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rendered controller for bcm-inv-002-reagent-management/openapi-source.md. */
@RestController
@RequestMapping("/api/inventory/reagents")
class ReagentProfileController {

  private final ReagentManagementService service;

  ReagentProfileController(ReagentManagementService service) {
    this.service = service;
  }

  @PostMapping("/items/{inventoryItemId}/reagent-profile")
  ResponseEntity<ReagentProfileResponse> assignReagentProfile(
      @PathVariable String inventoryItemId, @Valid @RequestBody AssignReagentProfileRequest request) {
    var updated =
        service.assignReagentProfile(
            inventoryItemId,
            new ReagentManagementService.AssignReagentProfileCommand(
                request.linkedTestDefinitionId(),
                request.reagentCategory(),
                request.consumptionUnitRatio(),
                request.actorId()));
    return ResponseEntity.ok(ReagentProfileResponse.from(inventoryItemId, updated.reagentProfile()));
  }

  @GetMapping("/items/{inventoryItemId}/reagent-profile")
  ResponseEntity<ReagentProfileResponse> getReagentProfile(@PathVariable String inventoryItemId) {
    return ResponseEntity.ok(
        ReagentProfileResponse.from(inventoryItemId, service.getReagentProfile(inventoryItemId)));
  }

  record AssignReagentProfileRequest(
      String linkedTestDefinitionId,
      @NotBlank String reagentCategory,
      BigDecimal consumptionUnitRatio,
      @NotBlank String actorId) {}

  record ReagentProfileResponse(
      String inventoryItemId,
      String linkedTestDefinitionId,
      String reagentCategory,
      BigDecimal consumptionUnitRatio) {

    static ReagentProfileResponse from(String inventoryItemId, ReagentProfile profile) {
      if (profile == null) {
        return new ReagentProfileResponse(inventoryItemId, null, null, null);
      }
      return new ReagentProfileResponse(
          inventoryItemId,
          profile.linkedTestDefinitionId(),
          profile.reagentCategory(),
          profile.consumptionUnitRatio());
    }
  }
}
