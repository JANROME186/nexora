import { afterEach, describe, expect, it, vi } from "vitest";
import * as api from "../api/cashSalesApi";

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

describe("cashSalesApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("builds cash session requests", async () => {
    const fetchMock = mockFetch({});

    await api.listCashSessions("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/cashier/sessions?tenantId=tenant-1");

    await api.getCashSession("session/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/cashier/sessions/session%2F1");

    await api.openCashSession({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      openedBy: "cashier-1",
      openingAmount: 100,
      currency: "USD",
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/cashier/sessions");

    await api.closeCashSession("session/1", { countedAmount: 100 });
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/revenue/cashier/sessions/session%2F1/close",
      expect.objectContaining({ method: "POST", body: JSON.stringify({ countedAmount: 100 }) }),
    ]);
  });

  it("builds sale and payment requests", async () => {
    const fetchMock = mockFetch({});

    await api.listSales("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/cashier/sales?tenantId=tenant-1");

    await api.getSale("sale/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/cashier/sales/sale%2F1");

    await api.listSaleLines("sale/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/cashier/sales/sale%2F1/lines");

    await api.listSalePayments("sale/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/cashier/sales/sale%2F1/payments");

    await api.createSale({
      tenantId: "tenant-1",
      sourceType: "diagnostic_order",
      sourceReferenceId: "order-1",
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/cashier/sales");

    await api.registerPayment("sale/1", {
      amount: 50,
      method: "cash",
      registeredBy: "cashier-1",
    });
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/revenue/cashier/sales/sale%2F1/payments",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ amount: 50, method: "cash", registeredBy: "cashier-1" }),
      }),
    ]);

    await api.cancelSale("sale/1", { reasonCode: "patient_request" });
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/revenue/cashier/sales/sale%2F1/cancel",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ reasonCode: "patient_request" }),
      }),
    ]);
  });

  it("builds billing request requests", async () => {
    const fetchMock = mockFetch({});

    await api.listBillingRequests("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/billing-requests?tenantId=tenant-1");

    await api.getBillingRequest("invoice/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/billing-requests/invoice%2F1");

    await api.listTaxLines("invoice/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/billing-requests/invoice%2F1/tax-lines");

    await api.createBillingRequest({
      saleId: "sale-1",
      legalName: "Acme Diagnostics",
      taxIdentifier: "TAX-1",
      fiscalAddress: "123 Main St",
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/revenue/billing-requests");

    await api.submitBillingRequest("invoice/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/revenue/billing-requests/invoice%2F1/submit",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);

    await api.retryBillingRequest("invoice/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/revenue/billing-requests/invoice%2F1/retry",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);

    await api.cancelBillingRequest("invoice/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/revenue/billing-requests/invoice%2F1/cancel",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);
  });
});
