import { afterEach, describe, expect, it, vi } from "vitest";
import {
  cancelAppointment,
  cancelQuotation,
  confirmAppointment,
  issueQuotation,
  listAppointments,
  listQuotations,
} from "../api/publicRequestsApi";

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

describe("publicRequestsApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    window.localStorage.clear();
  });

  it("lists appointments for a tenant", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [] });

    await listAppointments("tenant-1");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/care-delivery/appointments?tenantId=tenant-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("confirms an appointment", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { appointmentId: "apt-1", status: "confirmed" } });

    await confirmAppointment("apt-1");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/care-delivery/appointments/apt-1/confirm",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("cancels an appointment with a reason code", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { appointmentId: "apt-1", status: "cancelled" } });

    await cancelAppointment("apt-1", { reasonCode: "duplicate" });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/care-delivery/appointments/apt-1/cancel",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ reasonCode: "duplicate" }),
      }),
    );
  });

  it("lists quotations for a tenant", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [] });

    await listQuotations("tenant-1");

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/care-delivery/quotations?tenantId=tenant-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("issues a quotation", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { quotationId: "quote-1", status: "issued" } });

    await issueQuotation("quote-1", {});

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/care-delivery/quotations/quote-1/issue",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("cancels a quotation with a reason code", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { quotationId: "quote-1", status: "cancelled" } });

    await cancelQuotation("quote-1", { reasonCode: "not reachable" });

    expect(fetchMock).toHaveBeenCalledWith(
      "/api/care-delivery/quotations/quote-1/cancel",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ reasonCode: "not reachable" }),
      }),
    );
  });
});
