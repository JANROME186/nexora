import { afterEach, describe, expect, it, vi } from "vitest";
import {
  applyAdjustment,
  applyStockReceipt,
  approvePurchaseOrder,
  changeEquipmentAvailability,
  discontinueInventoryItem,
  listInventoryItems,
  overrideQualityControlDecision,
  quarantineStockLot,
  recordCalibration,
  registerInventoryItem,
  registerStockLot,
} from "../api/inventoryQualityApi";

function mockFetchOnce(response: Partial<Response> & { jsonBody?: unknown }) {
  const fetchMock = vi.fn().mockResolvedValue({
    ok: response.ok ?? true,
    status: response.status ?? 200,
    statusText: response.statusText ?? "OK",
    json: async () => response.jsonBody,
  } as Response);
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

describe("inventoryQualityApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    window.localStorage.clear();
  });

  it("lists inventory items scoped by tenant, laboratory and branch", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ inventoryItemId: "item-1" }] });

    const items = await listInventoryItems("tenant 1", "lab-1", "branch-1");

    expect(items).toEqual([{ inventoryItemId: "item-1" }]);
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/inventory/catalog/items?tenantId=tenant%201&laboratoryId=lab-1&branchId=branch-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("registers an inventory item through POST", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { inventoryItemId: "item-1" } });

    await registerInventoryItem({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      itemCode: "REAG-001",
      itemName: "Reagent A",
      itemType: "REAGENT",
      classification: "CONSUMABLE",
      unitOfMeasure: "ML",
      actorId: "user-1",
    });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/inventory/catalog/items",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("discontinues an inventory item", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: { inventoryItemId: "item-1", status: "DISCONTINUED" },
    });

    await discontinueInventoryItem("item-1", "user-1");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/inventory/catalog/items/item-1/discontinue",
      expect.objectContaining({ method: "POST", body: JSON.stringify({ actorId: "user-1" }) }),
    );
  });

  it("registers a stock lot for an inventory item", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { stockLotId: "lot-1" } });

    await registerStockLot("item-1", { lotNumber: "LOT-1", actorId: "user-1" });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/inventory/lots/items/item-1/lots",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("quarantines a stock lot", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { stockLotId: "lot-1", status: "QUARANTINED" } });

    await quarantineStockLot("lot-1", "user-1");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/inventory/lots/lots/lot-1/quarantine",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("approves a purchase order", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { purchaseOrderId: "po-1", status: "APPROVED" } });

    await approvePurchaseOrder("po-1", { actorId: "user-1" });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/inventory/purchase-orders/po-1/approve",
      expect.objectContaining({ method: "POST", body: JSON.stringify({ actorId: "user-1" }) }),
    );
  });

  it("applies a stock receipt", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { stockEntryId: "entry-1" } });

    await applyStockReceipt({
      inventoryItemId: "item-1",
      quantity: "10",
      entryType: "PURCHASE_RECEIPT",
      actorId: "user-1",
    });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/inventory/stock-entries",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("applies an inventory adjustment", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { adjustmentId: "adj-1" } });

    await applyAdjustment({
      inventoryItemId: "item-1",
      deltaQuantity: "-2",
      reasonCode: "COUNT_CORRECTION",
      requestedBy: "user-1",
      approverId: "user-2",
      actorId: "user-1",
    });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/inventory/adjustments",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("overrides an internal quality control acceptance decision", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: { qcRunId: "qc-1", acceptanceDecision: "ACCEPTED" },
    });

    await overrideQualityControlDecision("qc-1", {
      acceptanceDecision: "ACCEPTED",
      overrideReason: "Recalibrated instrument",
      supervisorId: "user-2",
      supervisorScoped: false,
    });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/internal-controls/qc-1/override",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("records a calibration event for an equipment item", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { calibrationEventId: "cal-1" } });

    await recordCalibration("item-1", {
      calibrationStandardRef: "STD-1",
      performedBy: "user-1",
      result: "PASS",
    });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/calibrations/items/item-1/calibrations",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("changes equipment availability", async () => {
    const fetchMock = mockFetchOnce({
      jsonBody: { assetTag: "EQ-1", availabilityStatus: "IN_USE" },
    });

    await changeEquipmentAvailability("item-1", {
      newStatus: "IN_USE",
      reasonCode: "SCHEDULED_TEST_RUN",
      actorId: "user-1",
    });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/equipment/items/item-1/availability",
      expect.objectContaining({ method: "POST" }),
    );
  });
});
