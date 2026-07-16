import { afterEach, describe, expect, it, vi } from "vitest";
import * as api from "../api/frontDeskApi";

function mockFetch(responseBody: unknown = {}) {
  const fetchMock = vi.fn().mockResolvedValue({
    ok: true,
    status: 200,
    statusText: "OK",
    json: async () => responseBody,
  } as Response);
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

function lastFetchCall(fetchMock: ReturnType<typeof mockFetch>) {
  return fetchMock.mock.calls[fetchMock.mock.calls.length - 1] as [string, RequestInit];
}

describe("frontDeskApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("builds reception visit requests", async () => {
    const fetchMock = mockFetch({});

    await api.listReceptionVisits("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/care-delivery/reception-visits?tenantId=tenant-1",
    );

    await api.getReceptionVisit("visit/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/reception-visits/visit%2F1");

    await api.startReceptionVisit({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      patientId: "patient-1",
      intakeChannel: "walk_in",
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/reception-visits");

    await api.confirmReceptionIdentity("visit/1", { identityConfirmationMethod: "document_check" });
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/reception-visits/visit%2F1/confirm-identity",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ identityConfirmationMethod: "document_check" }),
      }),
    ]);

    await api.advanceReceptionToAdmission("visit/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/reception-visits/visit%2F1/advance-to-admission",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);

    await api.updateReceptionPriority("visit/1", { priority: "urgent" });
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/care-delivery/reception-visits/visit%2F1/priority",
    );

    await api.abandonReceptionVisit("visit/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/reception-visits/visit%2F1/abandon",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);
  });

  it("builds diagnostic order requests", async () => {
    const fetchMock = mockFetch({});

    await api.listDiagnosticOrders("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/clinical-operations/diagnostic-orders?tenantId=tenant-1",
    );

    await api.getDiagnosticOrder("order/1");
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/clinical-operations/diagnostic-orders/order%2F1",
    );

    await api.listDiagnosticOrderLines("order/1");
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/clinical-operations/diagnostic-orders/order%2F1/lines",
    );

    await api.createDiagnosticOrder({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      intakeChannel: "walk_in",
      patientId: "patient-1",
      lines: [{ testDefinitionId: "test-1", catalogItemKind: "test", quantity: 1 }],
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/clinical-operations/diagnostic-orders");

    await api.priceDiagnosticOrder("order/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/clinical-operations/diagnostic-orders/order%2F1/price",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);

    await api.acceptDiagnosticOrder("order/1", { clinicalNotes: "notes" });
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/clinical-operations/diagnostic-orders/order%2F1/accept",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ clinicalNotes: "notes" }),
      }),
    ]);

    await api.cancelDiagnosticOrder("order/1", { reasonCode: "patient_request" });
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/clinical-operations/diagnostic-orders/order%2F1/cancel",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ reasonCode: "patient_request" }),
      }),
    ]);

    await api.completeDiagnosticOrder("order/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/clinical-operations/diagnostic-orders/order%2F1/complete",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);
  });
});
