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

  it("builds appointment scheduling requests", async () => {
    const fetchMock = mockFetch({});

    await api.listAppointments("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/appointments?tenantId=tenant-1");

    await api.getAppointment("apt/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/appointments/apt%2F1");

    await api.listAppointmentRequestedItems("apt/1");
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/care-delivery/appointments/apt%2F1/requested-items",
    );

    await api.getAppointmentPreparationInstructions("apt/1");
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/care-delivery/appointments/apt%2F1/preparation-instructions",
    );

    await api.requestAppointment({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      patientId: "patient-1",
      channel: "employee_portal",
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/appointments");

    await api.confirmAppointment("apt/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/appointments/apt%2F1/confirm",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);

    await api.checkInAppointment("apt/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/appointments/apt%2F1/check-in",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);

    await api.cancelAppointment("apt/1", { reasonCode: "patient_request" });
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/appointments/apt%2F1/cancel",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ reasonCode: "patient_request" }),
      }),
    ]);

    await api.markAppointmentNoShow("apt/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/appointments/apt%2F1/no-show",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);
  });

  it("builds admission management requests", async () => {
    const fetchMock = mockFetch({});

    await api.listAdmissionRequests("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/care-delivery/admission-requests?tenantId=tenant-1",
    );

    await api.getAdmissionRequest("adm/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/admission-requests/adm%2F1");

    await api.listAdmissionCatalogSelections("adm/1");
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/care-delivery/admission-requests/adm%2F1/catalog-selections",
    );

    await api.startAdmissionRequest({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      visitId: "visit-1",
      patientId: "patient-1",
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/admission-requests");

    await api.markAdmissionReady("adm/1", { clinicalNotesDraft: "notes" });
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/admission-requests/adm%2F1/mark-ready",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ clinicalNotesDraft: "notes" }),
      }),
    ]);

    await api.commitAdmissionRequest("adm/1", {
      consentConfirmed: true,
      sampleRequirementsAcknowledged: true,
    });
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/care-delivery/admission-requests/adm%2F1/commit",
    );

    await api.rejectAdmissionRequest("adm/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/admission-requests/adm%2F1/reject",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);
  });

  it("builds quotation management requests", async () => {
    const fetchMock = mockFetch({});

    await api.listQuotations("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/quotations?tenantId=tenant-1");

    await api.getQuotation("quo/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/quotations/quo%2F1");

    await api.listQuotationLines("quo/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/quotations/quo%2F1/lines");

    await api.startQuotation({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      patientId: "patient-1",
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/quotations");

    await api.issueQuotation("quo/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/quotations/quo%2F1/issue",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);

    await api.acceptQuotation("quo/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/quotations/quo%2F1/accept",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);

    await api.convertQuotation("quo/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/quotations/quo%2F1/convert",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);

    await api.cancelQuotation("quo/1", { reasonCode: "duplicate" });
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/quotations/quo%2F1/cancel",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ reasonCode: "duplicate" }),
      }),
    ]);

    await api.expireQuotation("quo/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/quotations/quo%2F1/expire",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);
  });
});
