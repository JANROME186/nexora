import { describe, it, expect, vi, beforeEach } from "vitest";
import { getPatientImagingDeliveryPackagesAsDoctor } from "./imagingDeliveryApi";

const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("getPatientImagingDeliveryPackagesAsDoctor", () => {
  beforeEach(() => {
    mockFetch.mockReset();
    localStorage.clear();
  });

  it("requests the delivery-packages endpoint scoped to patient with the referring-doctor self-access params", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
      status: 200,
      json: async () => [{ packageId: "pkg-1", studyId: "std-1", patientId: "Patient-A" }],
    });

    const packages = await getPatientImagingDeliveryPackagesAsDoctor("Patient-A", "Doctor-01");

    expect(packages).toEqual([{ packageId: "pkg-1", studyId: "std-1", patientId: "Patient-A" }]);
    const [calledUrl] = mockFetch.mock.calls[0];
    expect(calledUrl).toContain("/api/v1/imaging/delivery-packages?");
    expect(calledUrl).toContain("patientId=Patient-A");
    expect(calledUrl).toContain("callerRoleCode=REFERRING_DOCTOR");
    expect(calledUrl).toContain("callerId=Doctor-01");
  });
});
