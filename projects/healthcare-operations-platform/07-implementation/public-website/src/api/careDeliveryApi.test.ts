import { beforeEach, describe, expect, it, vi } from "vitest";
import { submitAppointmentRequest, submitQuotationRequest } from "./careDeliveryApi";

const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("careDeliveryApi", () => {
  beforeEach(() => {
    mockFetch.mockReset();
  });

  it("submits an appointment request to the public intake endpoint", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 201,
      json: async () => ({
        appointmentId: "apt-1",
        laboratoryId: "lab-local",
        branchId: "branch-local",
        scheduledStart: "2026-08-01T10:00:00Z",
        scheduledEnd: "2026-08-01T10:30:00Z",
        status: "requested",
        channel: "public_website",
      }),
    });

    const body = {
      tenantId: "tenant-local",
      laboratoryId: "lab-local",
      branchId: "branch-local",
      prospectivePhone: "555-0100",
      scheduledStart: "2026-08-01T10:00:00Z",
      scheduledEnd: "2026-08-01T10:30:00Z",
      requestedItems: [{ testDefinitionId: "seed-test-glucose", catalogItemKind: "test" as const }],
    };

    const result = await submitAppointmentRequest(body);
    expect(result.status).toBe("requested");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/care-delivery/appointment-requests",
      expect.objectContaining({ method: "POST", body: JSON.stringify(body) }),
    );
  });

  it("submits a quotation request to the public intake endpoint", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 201,
      json: async () => ({
        quotationId: "quo-1",
        laboratoryId: "lab-local",
        branchId: "branch-local",
        status: "draft",
      }),
    });

    const body = {
      tenantId: "tenant-local",
      laboratoryId: "lab-local",
      branchId: "branch-local",
      prospectiveEmail: "someone@example.com",
      lines: [
        { testDefinitionId: "seed-test-glucose", catalogItemKind: "test" as const, quantity: 1 },
      ],
    };

    const result = await submitQuotationRequest(body);
    expect(result.status).toBe("draft");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/care-delivery/quotation-requests",
      expect.objectContaining({ method: "POST", body: JSON.stringify(body) }),
    );
  });
});
