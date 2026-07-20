package com.nexora.hop.platformfoundation.inventoryquality.reagentmanagement.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.ReagentProfile;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Compiles bcm-inv-002-reagent-management/openapi-source.yaml. Delegated single-field mutation of
 * {@link InventoryItem#reagentProfile()} — never mutates any other InventoryItem field. Enforces
 * BCM-INV-002 RN-001 (itemType must be reagent), RN-002 (consumption ratio must be positive) and
 * RN-004 (discontinued items reject new assignments).
 */
@Service
public class ReagentManagementService {

  private static final Set<String> VALID_CATEGORIES =
      Set.of(
          ReagentProfile.CATEGORY_CALIBRATOR,
          ReagentProfile.CATEGORY_CONTROL,
          ReagentProfile.CATEGORY_WORKING_REAGENT,
          ReagentProfile.CATEGORY_BUFFER,
          ReagentProfile.CATEGORY_DILUENT,
          ReagentProfile.CATEGORY_OTHER);

  private final InventoryItemService inventoryItemService;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public ReagentManagementService(
      InventoryItemService inventoryItemService, AuditRecorder auditRecorder) {
    this(inventoryItemService, auditRecorder, Clock.systemUTC());
  }

  ReagentManagementService(
      InventoryItemService inventoryItemService, AuditRecorder auditRecorder, Clock clock) {
    this.inventoryItemService = inventoryItemService;
    this.auditRecorder = auditRecorder;
    this.clock = clock;
  }

  public InventoryItem assignReagentProfile(String inventoryItemId, AssignReagentProfileCommand command) {
    InventoryItem current =
        inventoryItemService.requireActiveItem(
            inventoryItemId, InventoryErrorCodes.INVENTORY_ITEM_DISCONTINUED);
    if (!InventoryItem.ITEM_TYPE_REAGENT.equals(current.itemType())) {
      throw new InventoryConflictException(
          "Only items with itemType=reagent may receive a reagent profile.",
          InventoryErrorCodes.REAGENT_ITEM_TYPE_NOT_ELIGIBLE);
    }
    String category = command.reagentCategory();
    if (category == null || !VALID_CATEGORIES.contains(category)) {
      throw new InvalidInventoryCommandException(
          "Reagent category is invalid. Allowed: " + VALID_CATEGORIES,
          InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    BigDecimal ratio = command.consumptionUnitRatio();
    if (ratio == null || ratio.signum() <= 0) {
      throw new InvalidInventoryCommandException(
          "Consumption unit ratio must be a positive number.",
          InventoryErrorCodes.REAGENT_CONSUMPTION_RATIO_INVALID);
    }
    String actor = requireText(command.actorId());
    LocalDateTime now = LocalDateTime.now(clock);
    InventoryItem updated =
        current.withReagentProfile(
            new ReagentProfile(command.linkedTestDefinitionId(), category, ratio),
            new AuditMetadata(
                current.audit().createdBy(), current.audit().createdAt(), actor, now));
    InventoryItem saved = inventoryItemService.save(updated);
    auditRecorder.recordSystemEvent(
        current.tenantId(),
        "ReagentProfileAssigned",
        "InventoryItem",
        saved.inventoryItemId(),
        "{\"reagentCategory\":\"%s\"}".formatted(category));
    return saved;
  }

  public ReagentProfile getReagentProfile(String inventoryItemId) {
    return inventoryItemService.requireItem(inventoryItemId).reagentProfile();
  }

  private static String requireText(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidInventoryCommandException(
          "Actor id is required.", InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    return value;
  }

  public record AssignReagentProfileCommand(
      String linkedTestDefinitionId,
      String reagentCategory,
      BigDecimal consumptionUnitRatio,
      String actorId) {}
}
