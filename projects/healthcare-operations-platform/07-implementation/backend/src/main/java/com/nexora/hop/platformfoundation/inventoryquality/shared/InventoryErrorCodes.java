package com.nexora.hop.platformfoundation.inventoryquality.shared;

/**
 * Canonical structured error codes for the nine BCM-INV capability packages compiled in
 * COM-MOD-010-BE-001, matching each capability's {@code openapi-source.md} {@code
 * error_model.domain_errors} exactly. Continues the first-class {@code code}+{@code messageKey}
 * error-envelope convention established by BCM-PLT-004/005/010 (MVP-MOD-008), further reducing
 * TD-I18N-002.
 */
public final class InventoryErrorCodes {

  private InventoryErrorCodes() {}

  // Cross-cutting.
  public static final String INVENTORY_COMMAND_INVALID = "INVENTORY_COMMAND_INVALID";
  public static final String INVENTORY_ITEM_NOT_FOUND = "INVENTORY_ITEM_NOT_FOUND";
  public static final String TENANT_NOT_FOUND = "TENANT_NOT_FOUND";

  // BCM-INV-001 Product Catalog.
  public static final String INVENTORY_ITEM_CODE_NOT_UNIQUE = "INVENTORY_ITEM_CODE_NOT_UNIQUE";
  public static final String INVENTORY_ITEM_TYPE_CLASSIFICATION_MISMATCH =
      "INVENTORY_ITEM_TYPE_CLASSIFICATION_MISMATCH";
  public static final String INVENTORY_ITEM_DISCONTINUED = "INVENTORY_ITEM_DISCONTINUED";
  public static final String INVENTORY_SCOPE_MISMATCH = "INVENTORY_SCOPE_MISMATCH";

  // BCM-INV-002 Reagent Management.
  public static final String REAGENT_ITEM_TYPE_NOT_ELIGIBLE = "REAGENT_ITEM_TYPE_NOT_ELIGIBLE";
  public static final String REAGENT_CONSUMPTION_RATIO_INVALID =
      "REAGENT_CONSUMPTION_RATIO_INVALID";
  public static final String REAGENT_SCOPE_MISMATCH = "REAGENT_SCOPE_MISMATCH";

  // BCM-INV-003 Lot Management.
  public static final String LOT_QUANTITY_INVARIANT_VIOLATION = "LOT_QUANTITY_INVARIANT_VIOLATION";
  public static final String LOT_DISPOSED_TRANSITION_FORBIDDEN =
      "LOT_DISPOSED_TRANSITION_FORBIDDEN";
  public static final String LOT_SCOPE_MISMATCH = "LOT_SCOPE_MISMATCH";
  public static final String STOCK_LOT_NOT_FOUND = "STOCK_LOT_NOT_FOUND";

  // BCM-INV-004 Procurement Management.
  public static final String PURCHASE_ORDER_LINE_ITEM_INVALID = "PURCHASE_ORDER_LINE_ITEM_INVALID";
  public static final String PURCHASE_ORDER_TERMINAL_STATE = "PURCHASE_ORDER_TERMINAL_STATE";
  public static final String PURCHASE_ORDER_LINE_QUANTITY_OR_COST_INVALID =
      "PURCHASE_ORDER_LINE_QUANTITY_OR_COST_INVALID";
  public static final String PROCUREMENT_SCOPE_MISMATCH = "PROCUREMENT_SCOPE_MISMATCH";
  public static final String PURCHASE_ORDER_NOT_FOUND = "PURCHASE_ORDER_NOT_FOUND";
  public static final String PURCHASE_ORDER_LINE_NOT_FOUND = "PURCHASE_ORDER_LINE_NOT_FOUND";

  // BCM-INV-005 Stock Entries.
  public static final String STOCK_ENTRY_QUANTITY_INVALID = "STOCK_ENTRY_QUANTITY_INVALID";
  public static final String STOCK_ENTRY_PURCHASE_ORDER_LINE_INVALID =
      "STOCK_ENTRY_PURCHASE_ORDER_LINE_INVALID";
  public static final String STOCK_ENTRY_ITEM_DISCONTINUED = "STOCK_ENTRY_ITEM_DISCONTINUED";
  public static final String STOCK_ENTRY_SCOPE_MISMATCH = "STOCK_ENTRY_SCOPE_MISMATCH";

  // BCM-INV-006 Stock Exits.
  public static final String STOCK_EXIT_QUANTITY_EXCEEDS_LOT = "STOCK_EXIT_QUANTITY_EXCEEDS_LOT";
  public static final String STOCK_EXIT_DESTINATION_BRANCH_REQUIRED =
      "STOCK_EXIT_DESTINATION_BRANCH_REQUIRED";
  public static final String STOCK_EXIT_LOT_NOT_ELIGIBLE = "STOCK_EXIT_LOT_NOT_ELIGIBLE";
  public static final String STOCK_EXIT_SCOPE_MISMATCH = "STOCK_EXIT_SCOPE_MISMATCH";

  // BCM-INV-007 Consumption Tracking.
  public static final String CONSUMPTION_REAGENT_PROFILE_MISSING =
      "CONSUMPTION_REAGENT_PROFILE_MISSING";
  public static final String CONSUMPTION_LOT_NOT_ELIGIBLE = "CONSUMPTION_LOT_NOT_ELIGIBLE";
  public static final String CONSUMPTION_SCOPE_MISMATCH = "CONSUMPTION_SCOPE_MISMATCH";

  // BCM-INV-008 Inventory Adjustments.
  public static final String ADJUSTMENT_QUANTITY_INVALID = "ADJUSTMENT_QUANTITY_INVALID";
  public static final String ADJUSTMENT_APPROVER_SAME_AS_REQUESTER =
      "ADJUSTMENT_APPROVER_SAME_AS_REQUESTER";
  public static final String ADJUSTMENT_REASON_CODE_REQUIRED = "ADJUSTMENT_REASON_CODE_REQUIRED";
  public static final String ADJUSTMENT_SCOPE_MISMATCH = "ADJUSTMENT_SCOPE_MISMATCH";

  // BCM-INV-009 Waste Management.
  public static final String WASTE_QUANTITY_EXCEEDS_LOT = "WASTE_QUANTITY_EXCEEDS_LOT";
  public static final String WASTE_REASON_CODE_REQUIRED = "WASTE_REASON_CODE_REQUIRED";
  public static final String WASTE_SCOPE_MISMATCH = "WASTE_SCOPE_MISMATCH";

  // BCM-QLT-001 Internal Quality Controls.
  public static final String QUALITY_CONTROL_NOT_FOUND = "QUALITY_CONTROL_NOT_FOUND";
  public static final String QC_CONTROL_MATERIAL_LOT_INELIGIBLE =
      "QC_CONTROL_MATERIAL_LOT_INELIGIBLE";
  public static final String QC_OVERRIDE_NOT_AUTHORIZED = "QC_OVERRIDE_NOT_AUTHORIZED";
  public static final String QC_SCOPE_MISMATCH = "QC_SCOPE_MISMATCH";
  public static final String QC_EXPECTED_RANGE_INVALID = "QC_EXPECTED_RANGE_INVALID";

  // BCM-QLT-003 Calibration Management.
  public static final String CALIBRATION_ITEM_TYPE_NOT_ELIGIBLE =
      "CALIBRATION_ITEM_TYPE_NOT_ELIGIBLE";
  public static final String CALIBRATION_NEXT_DUE_DATE_INVALID =
      "CALIBRATION_NEXT_DUE_DATE_INVALID";
  public static final String CALIBRATION_SCOPE_MISMATCH = "CALIBRATION_SCOPE_MISMATCH";

  // BCM-QLT-004 Equipment Management.
  public static final String EQUIPMENT_ITEM_TYPE_NOT_ELIGIBLE =
      "EQUIPMENT_ITEM_TYPE_NOT_ELIGIBLE";
  public static final String EQUIPMENT_RETIRED_TRANSITION_FORBIDDEN =
      "EQUIPMENT_RETIRED_TRANSITION_FORBIDDEN";
  public static final String EQUIPMENT_SCOPE_MISMATCH = "EQUIPMENT_SCOPE_MISMATCH";
  public static final String EQUIPMENT_PROFILE_MISSING = "EQUIPMENT_PROFILE_MISSING";

  // BCM-QLT-005 Maintenance Management.
  public static final String MAINTENANCE_NOT_FOUND = "MAINTENANCE_NOT_FOUND";
  public static final String MAINTENANCE_ITEM_TYPE_NOT_ELIGIBLE =
      "MAINTENANCE_ITEM_TYPE_NOT_ELIGIBLE";
  public static final String MAINTENANCE_COMPLETED_BEFORE_STARTED =
      "MAINTENANCE_COMPLETED_BEFORE_STARTED";
  public static final String MAINTENANCE_SCOPE_MISMATCH = "MAINTENANCE_SCOPE_MISMATCH";
}
