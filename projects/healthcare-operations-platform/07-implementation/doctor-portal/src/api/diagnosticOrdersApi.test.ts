import { describe, it, expect, vi, beforeEach } from "vitest";
import { listReferredOrders } from "./diagnosticOrdersApi";

const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("listReferredOrders", () => {
  beforeEach(() => {
    mockFetch.mockReset();
    localStorage.clear();
  });

  it("requests the diagnostic-orders endpoint scoped to tenant and doctor", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => [{ orderId: "ord-1" }],
    });

    const orders = await listReferredOrders("tenant-a", "Doctor-01");

    expect(orders).toEqual([{ orderId: "ord-1" }]);
    const [calledUrl] = mockFetch.mock.calls[0];
    expect(calledUrl).toContain("/api/clinical-operations/diagnostic-orders?");
    expect(calledUrl).toContain("tenantId=tenant-a");
    expect(calledUrl).toContain("doctorId=Doctor-01");
  });
});
