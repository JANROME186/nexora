package com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * AGG-013 InventoryItem — the single shared aggregate for the entire COM-MOD-010 Inventory and
 * Internal Quality module (business-model.yaml ENT-CAT-001). Owned by BCM-INV-001 Product
 * Catalog; sibling capabilities hold delegated field-level mutation authority through their own
 * commands.
 */
public record InventoryItem(
    String inventoryItemId,
    String tenantId,
    String laboratoryId,
    String branchId,
    String itemCode,
    String itemName,
    String itemType,
    String classification,
    String unitOfMeasure,
    String status,
    StockSummary stockSummary,
    ReagentProfile reagentProfile,
    EquipmentProfile equipmentProfile,
    AuditMetadata audit) {

  public static final String ITEM_TYPE_CONSUMABLE = "consumable";
  public static final String ITEM_TYPE_REAGENT = "reagent";
  public static final String ITEM_TYPE_SUPPLY = "supply";
  public static final String ITEM_TYPE_EQUIPMENT = "equipment";

  public static final String CLASSIFICATION_DIAGNOSTIC_REAGENT = "diagnostic_reagent";
  public static final String CLASSIFICATION_LAB_SUPPLY = "lab_supply";
  public static final String CLASSIFICATION_PPE = "ppe";
  public static final String CLASSIFICATION_CALIBRATOR_CONTROL_MATERIAL =
      "calibrator_control_material";
  public static final String CLASSIFICATION_CAPITAL_EQUIPMENT = "capital_equipment";
  public static final String CLASSIFICATION_OTHER = "other";

  public static final String STATUS_ACTIVE = "active";
  public static final String STATUS_INACTIVE = "inactive";
  public static final String STATUS_DISCONTINUED = "discontinued";

  /** RN-004: a discontinued item cannot receive new stock, purchase-order-line or reagent commands. */
  public boolean isDiscontinued() {
    return STATUS_DISCONTINUED.equals(status);
  }

  public InventoryItem withStockSummary(StockSummary summary, AuditMetadata newAudit) {
    return new InventoryItem(
        inventoryItemId,
        tenantId,
        laboratoryId,
        branchId,
        itemCode,
        itemName,
        itemType,
        classification,
        unitOfMeasure,
        status,
        summary,
        reagentProfile,
        equipmentProfile,
        newAudit);
  }

  public InventoryItem withReagentProfile(ReagentProfile profile, AuditMetadata newAudit) {
    return new InventoryItem(
        inventoryItemId,
        tenantId,
        laboratoryId,
        branchId,
        itemCode,
        itemName,
        itemType,
        classification,
        unitOfMeasure,
        status,
        stockSummary,
        profile,
        equipmentProfile,
        newAudit);
  }

  public InventoryItem withEquipmentProfile(EquipmentProfile profile, AuditMetadata newAudit) {
    return new InventoryItem(
        inventoryItemId,
        tenantId,
        laboratoryId,
        branchId,
        itemCode,
        itemName,
        itemType,
        classification,
        unitOfMeasure,
        status,
        stockSummary,
        reagentProfile,
        profile,
        newAudit);
  }

  public InventoryItem withStatus(String newStatus, AuditMetadata newAudit) {
    return new InventoryItem(
        inventoryItemId,
        tenantId,
        laboratoryId,
        branchId,
        itemCode,
        itemName,
        itemType,
        classification,
        unitOfMeasure,
        newStatus,
        stockSummary,
        reagentProfile,
        equipmentProfile,
        newAudit);
  }

  public InventoryItem withCoreIdentity(
      String newItemName,
      String newItemType,
      String newClassification,
      String newUnitOfMeasure,
      String newStatus,
      AuditMetadata newAudit) {
    return new InventoryItem(
        inventoryItemId,
        tenantId,
        laboratoryId,
        branchId,
        itemCode,
        newItemName,
        newItemType,
        newClassification,
        newUnitOfMeasure,
        newStatus,
        stockSummary,
        reagentProfile,
        equipmentProfile,
        newAudit);
  }
}
