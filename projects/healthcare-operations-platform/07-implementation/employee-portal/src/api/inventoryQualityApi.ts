/**
 * Inventory and Internal Quality API client (COM-MOD-010-FE-001).
 *
 * Covers employee-portal administration endpoints generated from:
 *   BCM-INV-001 Product Catalog, BCM-INV-002 Reagent Management, BCM-INV-003 Lot Management,
 *   BCM-INV-004 Procurement Management, BCM-INV-005 Stock Entries, BCM-INV-006 Stock Exits,
 *   BCM-INV-007 Consumption Tracking, BCM-INV-008 Inventory Adjustments, BCM-INV-009 Waste
 *   Management, BCM-QLT-001 Internal Quality Controls, BCM-QLT-003 Calibration Management,
 *   BCM-QLT-004 Equipment Management, BCM-QLT-005 Maintenance Management.
 *
 * Mirrors the `integrationMigrationApi.ts` shape (TD-STACK-003): a thin, typed operation facade
 * over the hand-written HTTP adapter, one function per backend endpoint, that can be replaced by
 * OpenAPI Generator output later without changing screen code. Backend logic (stock invariants,
 * QC rule evaluation, calibration/maintenance validation) is never reimplemented here.
 */
import { get, post, put } from "./httpClient";
import type {
  AdjustmentRecord,
  ApplyAdjustmentRequest,
  ApplyConsumptionRequest,
  ApplyStockExitRequest,
  ApplyStockReceiptRequest,
  ApplyWasteRequest,
  ApprovalRequest,
  AssignReagentProfileRequest,
  AvailabilityChangeRecord,
  CalibrationEvent,
  CancelRequest,
  ChangeAvailabilityRequest,
  CompleteMaintenanceRequest,
  ConsumptionRecord,
  CreatePurchaseOrderRequest,
  EquipmentProfileRecord,
  InventoryItem,
  MaintenanceEvent,
  OverrideAcceptanceDecisionRequest,
  PurchaseOrder,
  QualityControlRun,
  ReagentProfileRecord,
  ReceiveLineRequest,
  RecordCalibrationRequest,
  RecordMaintenanceRequest,
  RecordQualityControlRunRequest,
  RegisterInventoryItemRequest,
  RegisterStockLotRequest,
  SetEquipmentProfileRequest,
  StockEntry,
  StockExit,
  StockLot,
  UpdateInventoryItemRequest,
  WasteRecord,
} from "./types";

const CATALOG_BASE = "/api/inventory/catalog";
const REAGENTS_BASE = "/api/inventory/reagents";
const LOTS_BASE = "/api/inventory/lots";
const PURCHASE_ORDERS_BASE = "/api/inventory/purchase-orders";
const STOCK_ENTRIES_BASE = "/api/inventory/stock-entries";
const STOCK_EXITS_BASE = "/api/inventory/stock-exits";
const CONSUMPTION_BASE = "/api/inventory/consumption";
const ADJUSTMENTS_BASE = "/api/inventory/adjustments";
const WASTE_BASE = "/api/inventory/waste";
const INTERNAL_QUALITY_CONTROLS_BASE = "/api/quality/internal-controls";
const CALIBRATIONS_BASE = "/api/quality/calibrations";
const EQUIPMENT_BASE = "/api/quality/equipment";
const MAINTENANCE_BASE = "/api/quality/maintenance";

function encode(value: string): string {
  return encodeURIComponent(value);
}

function scopeQuery(tenantId: string, laboratoryId: string, branchId: string): string {
  return `tenantId=${encode(tenantId)}&laboratoryId=${encode(laboratoryId)}&branchId=${encode(branchId)}`;
}

// -- BCM-INV-001 Product Catalog --------------------------------------------------------------

export function listInventoryItems(
  tenantId: string,
  laboratoryId: string,
  branchId: string,
): Promise<InventoryItem[]> {
  return get(`${CATALOG_BASE}/items?${scopeQuery(tenantId, laboratoryId, branchId)}`);
}

export function registerInventoryItem(
  request: RegisterInventoryItemRequest,
): Promise<InventoryItem> {
  return post(`${CATALOG_BASE}/items`, request);
}

export function updateInventoryItem(
  inventoryItemId: string,
  request: UpdateInventoryItemRequest,
): Promise<InventoryItem> {
  return put(`${CATALOG_BASE}/items/${encode(inventoryItemId)}`, request);
}

export function discontinueInventoryItem(
  inventoryItemId: string,
  actorId: string,
): Promise<InventoryItem> {
  return post(`${CATALOG_BASE}/items/${encode(inventoryItemId)}/discontinue`, { actorId });
}

// -- BCM-INV-002 Reagent Management -----------------------------------------------------------

export function assignReagentProfile(
  inventoryItemId: string,
  request: AssignReagentProfileRequest,
): Promise<ReagentProfileRecord> {
  return post(`${REAGENTS_BASE}/items/${encode(inventoryItemId)}/reagent-profile`, request);
}

export function getReagentProfile(inventoryItemId: string): Promise<ReagentProfileRecord> {
  return get(`${REAGENTS_BASE}/items/${encode(inventoryItemId)}/reagent-profile`);
}

// -- BCM-INV-003 Lot Management ----------------------------------------------------------------

export function registerStockLot(
  inventoryItemId: string,
  request: RegisterStockLotRequest,
): Promise<StockLot> {
  return post(`${LOTS_BASE}/items/${encode(inventoryItemId)}/lots`, request);
}

export function listStockLots(inventoryItemId: string): Promise<StockLot[]> {
  return get(`${LOTS_BASE}/items/${encode(inventoryItemId)}/lots`);
}

export function quarantineStockLot(stockLotId: string, actorId: string): Promise<StockLot> {
  return post(`${LOTS_BASE}/lots/${encode(stockLotId)}/quarantine`, { actorId });
}

export function expireStockLot(stockLotId: string, actorId: string): Promise<StockLot> {
  return post(`${LOTS_BASE}/lots/${encode(stockLotId)}/expire`, { actorId });
}

// -- BCM-INV-004 Procurement Management --------------------------------------------------------

export function createPurchaseOrder(request: CreatePurchaseOrderRequest): Promise<PurchaseOrder> {
  return post(PURCHASE_ORDERS_BASE, request);
}

export function listPurchaseOrders(
  tenantId: string,
  laboratoryId: string,
  branchId: string,
): Promise<PurchaseOrder[]> {
  return get(`${PURCHASE_ORDERS_BASE}?${scopeQuery(tenantId, laboratoryId, branchId)}`);
}

export function submitPurchaseOrder(
  purchaseOrderId: string,
  actorId: string,
): Promise<PurchaseOrder> {
  return post(`${PURCHASE_ORDERS_BASE}/${encode(purchaseOrderId)}/submit`, { actorId });
}

export function approvePurchaseOrder(
  purchaseOrderId: string,
  request: ApprovalRequest,
): Promise<PurchaseOrder> {
  return post(`${PURCHASE_ORDERS_BASE}/${encode(purchaseOrderId)}/approve`, request);
}

export function cancelPurchaseOrder(
  purchaseOrderId: string,
  request: CancelRequest,
): Promise<PurchaseOrder> {
  return post(`${PURCHASE_ORDERS_BASE}/${encode(purchaseOrderId)}/cancel`, request);
}

export function receivePurchaseOrderLine(
  purchaseOrderId: string,
  lineId: string,
  request: ReceiveLineRequest,
): Promise<PurchaseOrder> {
  return post(
    `${PURCHASE_ORDERS_BASE}/${encode(purchaseOrderId)}/lines/${encode(lineId)}/receive`,
    request,
  );
}

// -- BCM-INV-005 Stock Entries -------------------------------------------------------------------

export function applyStockReceipt(request: ApplyStockReceiptRequest): Promise<StockEntry> {
  return post(STOCK_ENTRIES_BASE, request);
}

export function listStockEntries(
  tenantId: string,
  laboratoryId: string,
  branchId: string,
): Promise<StockEntry[]> {
  return get(`${STOCK_ENTRIES_BASE}?${scopeQuery(tenantId, laboratoryId, branchId)}`);
}

// -- BCM-INV-006 Stock Exits ----------------------------------------------------------------------

export function applyStockExit(request: ApplyStockExitRequest): Promise<StockExit> {
  return post(STOCK_EXITS_BASE, request);
}

export function listStockExits(
  tenantId: string,
  laboratoryId: string,
  branchId: string,
): Promise<StockExit[]> {
  return get(`${STOCK_EXITS_BASE}?${scopeQuery(tenantId, laboratoryId, branchId)}`);
}

// -- BCM-INV-007 Consumption Tracking --------------------------------------------------------------

export function applyConsumption(request: ApplyConsumptionRequest): Promise<ConsumptionRecord> {
  return post(CONSUMPTION_BASE, request);
}

export function listConsumptionRecords(
  tenantId: string,
  laboratoryId: string,
  branchId: string,
): Promise<ConsumptionRecord[]> {
  return get(`${CONSUMPTION_BASE}?${scopeQuery(tenantId, laboratoryId, branchId)}`);
}

// -- BCM-INV-008 Inventory Adjustments ---------------------------------------------------------

export function applyAdjustment(request: ApplyAdjustmentRequest): Promise<AdjustmentRecord> {
  return post(ADJUSTMENTS_BASE, request);
}

export function listAdjustments(
  tenantId: string,
  laboratoryId: string,
  branchId: string,
): Promise<AdjustmentRecord[]> {
  return get(`${ADJUSTMENTS_BASE}?${scopeQuery(tenantId, laboratoryId, branchId)}`);
}

// -- BCM-INV-009 Waste Management ----------------------------------------------------------------

export function applyWaste(request: ApplyWasteRequest): Promise<WasteRecord> {
  return post(WASTE_BASE, request);
}

export function listWasteRecords(
  tenantId: string,
  laboratoryId: string,
  branchId: string,
): Promise<WasteRecord[]> {
  return get(`${WASTE_BASE}?${scopeQuery(tenantId, laboratoryId, branchId)}`);
}

// -- BCM-QLT-001 Internal Quality Controls -----------------------------------------------------

export function recordQualityControlRun(
  request: RecordQualityControlRunRequest,
): Promise<QualityControlRun> {
  return post(INTERNAL_QUALITY_CONTROLS_BASE, request);
}

export function overrideQualityControlDecision(
  qcRunId: string,
  request: OverrideAcceptanceDecisionRequest,
): Promise<QualityControlRun> {
  return post(`${INTERNAL_QUALITY_CONTROLS_BASE}/${encode(qcRunId)}/override`, request);
}

export function listQualityControlRuns(
  tenantId: string,
  laboratoryId: string,
  branchId: string,
): Promise<QualityControlRun[]> {
  return get(`${INTERNAL_QUALITY_CONTROLS_BASE}?${scopeQuery(tenantId, laboratoryId, branchId)}`);
}

// -- BCM-QLT-003 Calibration Management ----------------------------------------------------------

export function recordCalibration(
  inventoryItemId: string,
  request: RecordCalibrationRequest,
): Promise<CalibrationEvent> {
  return post(`${CALIBRATIONS_BASE}/items/${encode(inventoryItemId)}/calibrations`, request);
}

export function listCalibrations(inventoryItemId: string): Promise<CalibrationEvent[]> {
  return get(`${CALIBRATIONS_BASE}/items/${encode(inventoryItemId)}/calibrations`);
}

// -- BCM-QLT-004 Equipment Management -------------------------------------------------------------

export function setEquipmentProfile(
  inventoryItemId: string,
  request: SetEquipmentProfileRequest,
): Promise<EquipmentProfileRecord> {
  return post(`${EQUIPMENT_BASE}/items/${encode(inventoryItemId)}/equipment-profile`, request);
}

export function getEquipmentProfile(inventoryItemId: string): Promise<EquipmentProfileRecord> {
  return get(`${EQUIPMENT_BASE}/items/${encode(inventoryItemId)}/equipment-profile`);
}

export function changeEquipmentAvailability(
  inventoryItemId: string,
  request: ChangeAvailabilityRequest,
): Promise<EquipmentProfileRecord> {
  return post(`${EQUIPMENT_BASE}/items/${encode(inventoryItemId)}/availability`, request);
}

export function listEquipmentAvailabilityHistory(
  inventoryItemId: string,
): Promise<AvailabilityChangeRecord[]> {
  return get(`${EQUIPMENT_BASE}/items/${encode(inventoryItemId)}/availability`);
}

// -- BCM-QLT-005 Maintenance Management ------------------------------------------------------------

export function recordMaintenance(
  inventoryItemId: string,
  request: RecordMaintenanceRequest,
): Promise<MaintenanceEvent> {
  return post(`${MAINTENANCE_BASE}/items/${encode(inventoryItemId)}/maintenance`, request);
}

export function completeMaintenance(
  maintenanceEventId: string,
  request: CompleteMaintenanceRequest,
): Promise<MaintenanceEvent> {
  return post(`${MAINTENANCE_BASE}/maintenance/${encode(maintenanceEventId)}/complete`, request);
}

export function listMaintenanceEvents(inventoryItemId: string): Promise<MaintenanceEvent[]> {
  return get(`${MAINTENANCE_BASE}/items/${encode(inventoryItemId)}/maintenance`);
}
