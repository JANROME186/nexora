package com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItemRepository;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.StockSummary;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InvalidInventoryCommandException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryConflictException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryEntityNotFoundException;
import com.nexora.hop.platformfoundation.inventoryquality.shared.InventoryErrorCodes;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Compiles the generatable outputs of bcm-inv-001-product-catalog/generation-plan.yaml
 * (createInventoryItem, listInventoryItems, getInventoryItem, updateInventoryItem) plus the
 * BCM-INV-001 custom-implementation points that COM-MOD-010-BE-001 covers:
 *
 * <ul>
 *   <li>CUS-CAT-001-01 itemType/classification consistency (RN-002).
 *   <li>CUS-CAT-001-02 field-level delegation boundary — this service is the single write path
 *       for InventoryItem's core identity fields; sibling capabilities mutate only their
 *       delegated field set through their own commands.
 *   <li>CUS-CAT-001-03 discontinuation gate (RN-004) — the {@link InventoryItem#isDiscontinued()}
 *       method is consulted by BCM-INV-002/004/005/007 services before accepting new commands.
 * </ul>
 */
@Service
public class InventoryItemService {

  private static final Set<String> ITEM_TYPES =
      Set.of(
          InventoryItem.ITEM_TYPE_CONSUMABLE,
          InventoryItem.ITEM_TYPE_REAGENT,
          InventoryItem.ITEM_TYPE_SUPPLY,
          InventoryItem.ITEM_TYPE_EQUIPMENT);

  private static final Set<String> CLASSIFICATIONS =
      Set.of(
          InventoryItem.CLASSIFICATION_DIAGNOSTIC_REAGENT,
          InventoryItem.CLASSIFICATION_LAB_SUPPLY,
          InventoryItem.CLASSIFICATION_PPE,
          InventoryItem.CLASSIFICATION_CALIBRATOR_CONTROL_MATERIAL,
          InventoryItem.CLASSIFICATION_CAPITAL_EQUIPMENT,
          InventoryItem.CLASSIFICATION_OTHER);

  /**
   * RN-002 consistency map. capital_equipment classification requires itemType equipment; the
   * calibrator_control_material classification requires itemType reagent; diagnostic_reagent
   * requires itemType reagent; lab_supply and ppe require consumable or supply; other is
   * permissive.
   */
  private static final Map<String, Set<String>> CLASSIFICATION_TO_ITEM_TYPES =
      Map.of(
          InventoryItem.CLASSIFICATION_DIAGNOSTIC_REAGENT, Set.of(InventoryItem.ITEM_TYPE_REAGENT),
          InventoryItem.CLASSIFICATION_CALIBRATOR_CONTROL_MATERIAL,
              Set.of(InventoryItem.ITEM_TYPE_REAGENT),
          InventoryItem.CLASSIFICATION_LAB_SUPPLY,
              Set.of(InventoryItem.ITEM_TYPE_CONSUMABLE, InventoryItem.ITEM_TYPE_SUPPLY),
          InventoryItem.CLASSIFICATION_PPE,
              Set.of(InventoryItem.ITEM_TYPE_CONSUMABLE, InventoryItem.ITEM_TYPE_SUPPLY),
          InventoryItem.CLASSIFICATION_CAPITAL_EQUIPMENT,
              Set.of(InventoryItem.ITEM_TYPE_EQUIPMENT),
          InventoryItem.CLASSIFICATION_OTHER,
              Set.of(
                  InventoryItem.ITEM_TYPE_CONSUMABLE,
                  InventoryItem.ITEM_TYPE_REAGENT,
                  InventoryItem.ITEM_TYPE_SUPPLY,
                  InventoryItem.ITEM_TYPE_EQUIPMENT));

  private final InventoryItemRepository repository;
  private final TenantDirectory tenantDirectory;
  private final AuditRecorder auditRecorder;
  private final Clock clock;

  @Autowired
  public InventoryItemService(
      InventoryItemRepository repository,
      TenantDirectory tenantDirectory,
      AuditRecorder auditRecorder) {
    this(repository, tenantDirectory, auditRecorder, Clock.systemUTC());
  }

  InventoryItemService(
      InventoryItemRepository repository,
      TenantDirectory tenantDirectory,
      AuditRecorder auditRecorder,
      Clock clock) {
    this.repository = repository;
    this.tenantDirectory = tenantDirectory;
    this.auditRecorder = auditRecorder;
    this.clock = clock;
  }

  public InventoryItem registerItem(RegisterInventoryItemCommand command) {
    String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
    if (!tenantDirectory.tenantExists(tenantId)) {
      throw new InventoryEntityNotFoundException(
          "Tenant was not found.", InventoryErrorCodes.TENANT_NOT_FOUND);
    }
    String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
    String branchId = requiredText(command.branchId(), "Branch id is required.");
    String itemCode = requiredText(command.itemCode(), "Item code is required.");
    String itemName = requiredText(command.itemName(), "Item name is required.");
    String itemType = requiredEnum(command.itemType(), ITEM_TYPES, "Item type is invalid.");
    String classification =
        requiredEnum(command.classification(), CLASSIFICATIONS, "Classification is invalid.");
    validateTypeClassificationConsistency(itemType, classification);
    String unitOfMeasure = requiredText(command.unitOfMeasure(), "Unit of measure is required.");
    String actor = requiredText(command.actorId(), "Actor id is required.");

    repository
        .findByScopeAndCode(tenantId, laboratoryId, branchId, itemCode)
        .ifPresent(
            existing -> {
              throw new InventoryConflictException(
                  "Item code " + itemCode + " already exists in this scope.",
                  InventoryErrorCodes.INVENTORY_ITEM_CODE_NOT_UNIQUE);
            });

    LocalDateTime now = LocalDateTime.now(clock);
    InventoryItem item =
        new InventoryItem(
            newId(),
            tenantId,
            laboratoryId,
            branchId,
            itemCode,
            itemName,
            itemType,
            classification,
            unitOfMeasure,
            InventoryItem.STATUS_ACTIVE,
            StockSummary.empty(),
            null,
            null,
            new AuditMetadata(actor, now, actor, now));
    InventoryItem saved = repository.save(item);
    auditRecorder.recordSystemEvent(
        tenantId,
        "InventoryItemRegistered",
        "InventoryItem",
        saved.inventoryItemId(),
        "{\"itemCode\":\"%s\",\"itemType\":\"%s\",\"classification\":\"%s\"}"
            .formatted(itemCode, itemType, classification));
    return saved;
  }

  public InventoryItem updateItem(String inventoryItemId, UpdateInventoryItemCommand command) {
    InventoryItem current = requireItem(inventoryItemId);
    if (current.isDiscontinued()) {
      throw new InventoryConflictException(
          "Discontinued items cannot be edited.",
          InventoryErrorCodes.INVENTORY_ITEM_DISCONTINUED);
    }
    String itemName = requiredText(command.itemName(), "Item name is required.");
    String itemType = requiredEnum(command.itemType(), ITEM_TYPES, "Item type is invalid.");
    String classification =
        requiredEnum(command.classification(), CLASSIFICATIONS, "Classification is invalid.");
    validateTypeClassificationConsistency(itemType, classification);
    String unitOfMeasure = requiredText(command.unitOfMeasure(), "Unit of measure is required.");
    String status = requiredEnum(
        command.status(),
        Set.of(InventoryItem.STATUS_ACTIVE, InventoryItem.STATUS_INACTIVE),
        "Status is invalid.");
    String actor = requiredText(command.actorId(), "Actor id is required.");
    LocalDateTime now = LocalDateTime.now(clock);
    InventoryItem updated =
        current.withCoreIdentity(
            itemName,
            itemType,
            classification,
            unitOfMeasure,
            status,
            new AuditMetadata(current.audit().createdBy(), current.audit().createdAt(), actor, now));
    InventoryItem saved = repository.save(updated);
    auditRecorder.recordSystemEvent(
        current.tenantId(),
        "InventoryItemUpdated",
        "InventoryItem",
        saved.inventoryItemId(),
        "{\"itemType\":\"%s\",\"classification\":\"%s\"}".formatted(itemType, classification));
    return saved;
  }

  public InventoryItem discontinueItem(String inventoryItemId, String actorId) {
    InventoryItem current = requireItem(inventoryItemId);
    String actor = requiredText(actorId, "Actor id is required.");
    if (current.isDiscontinued()) {
      return current;
    }
    LocalDateTime now = LocalDateTime.now(clock);
    InventoryItem updated =
        current.withStatus(
            InventoryItem.STATUS_DISCONTINUED,
            new AuditMetadata(current.audit().createdBy(), current.audit().createdAt(), actor, now));
    InventoryItem saved = repository.save(updated);
    auditRecorder.recordSystemEvent(
        current.tenantId(),
        "InventoryItemDiscontinued",
        "InventoryItem",
        saved.inventoryItemId(),
        "{}");
    return saved;
  }

  public List<InventoryItem> listItems(String tenantId, String laboratoryId, String branchId) {
    return repository.findByScope(
        requiredText(tenantId, "Tenant id is required."),
        requiredText(laboratoryId, "Laboratory id is required."),
        requiredText(branchId, "Branch id is required."));
  }

  public InventoryItem getItem(String inventoryItemId) {
    return requireItem(inventoryItemId);
  }

  /** Cross-service read for sibling BCM-INV-* services to consult before writing. */
  public InventoryItem requireActiveItem(String inventoryItemId, String discontinuedErrorCode) {
    InventoryItem item = requireItem(inventoryItemId);
    if (item.isDiscontinued()) {
      throw new InventoryConflictException(
          "Inventory item " + inventoryItemId + " is discontinued and cannot accept this command.",
          discontinuedErrorCode);
    }
    return item;
  }

  public InventoryItem requireItem(String inventoryItemId) {
    return repository
        .findById(requiredText(inventoryItemId, "Inventory item id is required."))
        .orElseThrow(
            () ->
                new InventoryEntityNotFoundException(
                    "Inventory item was not found.", InventoryErrorCodes.INVENTORY_ITEM_NOT_FOUND));
  }

  /** Delegated write path used by sibling capabilities' Apply* commands. */
  public InventoryItem save(InventoryItem item) {
    return repository.save(item);
  }

  private static void validateTypeClassificationConsistency(
      String itemType, String classification) {
    Set<String> allowed = CLASSIFICATION_TO_ITEM_TYPES.getOrDefault(classification, Set.of());
    if (!allowed.contains(itemType)) {
      throw new InvalidInventoryCommandException(
          "itemType "
              + itemType
              + " is not consistent with classification "
              + classification
              + ".",
          InventoryErrorCodes.INVENTORY_ITEM_TYPE_CLASSIFICATION_MISMATCH);
    }
  }

  private static String requiredText(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new InvalidInventoryCommandException(
          message, InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    return value;
  }

  private static String requiredEnum(String value, Set<String> allowed, String message) {
    if (value == null || !allowed.contains(value)) {
      throw new InvalidInventoryCommandException(
          message + " Allowed: " + allowed,
          InventoryErrorCodes.INVENTORY_COMMAND_INVALID);
    }
    return value;
  }

  private static String newId() {
    return UUID.randomUUID().toString();
  }

  Clock clock() {
    return clock;
  }

  public record RegisterInventoryItemCommand(
      String tenantId,
      String laboratoryId,
      String branchId,
      String itemCode,
      String itemName,
      String itemType,
      String classification,
      String unitOfMeasure,
      String actorId) {}

  public record UpdateInventoryItemCommand(
      String itemName,
      String itemType,
      String classification,
      String unitOfMeasure,
      String status,
      String actorId) {}
}
