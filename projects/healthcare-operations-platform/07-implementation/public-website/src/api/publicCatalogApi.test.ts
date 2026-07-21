import { beforeEach, describe, expect, it, vi } from "vitest";
import {
  getDiagnosticServiceSnapshot,
  getPanelSnapshot,
  getPreparationSnapshot,
  getTestSnapshot,
  listDiagnosticServices,
  listPanels,
  listPreparations,
  listTests,
} from "./publicCatalogApi";

const mockFetch = vi.fn();
window.fetch = mockFetch;

describe("publicCatalogApi", () => {
  beforeEach(() => {
    mockFetch.mockReset();
    mockFetch.mockResolvedValue({ ok: true, status: 200, json: async () => [] });
  });

  it("requests the published diagnostic services list with laboratoryId", async () => {
    await listDiagnosticServices("lab-local");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/catalog/diagnostic-services/published?laboratoryId=lab-local",
      expect.any(Object),
    );
  });

  it("requests a diagnostic service published snapshot by id", async () => {
    await getDiagnosticServiceSnapshot("seed-service-glucose");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/catalog/diagnostic-services/seed-service-glucose/published-snapshot",
      expect.any(Object),
    );
  });

  it("requests the published tests list with laboratoryId", async () => {
    await listTests("lab-local");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/catalog/tests/published?laboratoryId=lab-local",
      expect.any(Object),
    );
  });

  it("requests a test published snapshot by id", async () => {
    await getTestSnapshot("seed-test-glucose");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/catalog/tests/seed-test-glucose/published-snapshot",
      expect.any(Object),
    );
  });

  it("requests the published panels list with laboratoryId", async () => {
    await listPanels("lab-local");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/catalog/panels/published?laboratoryId=lab-local",
      expect.any(Object),
    );
  });

  it("requests a panel published snapshot by id", async () => {
    await getPanelSnapshot("panel-1");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/catalog/panels/panel-1/published-snapshot",
      expect.any(Object),
    );
  });

  it("requests the published preparations list with laboratoryId", async () => {
    await listPreparations("lab-local");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/catalog/preparations/published?laboratoryId=lab-local",
      expect.any(Object),
    );
  });

  it("requests a preparation published snapshot by id", async () => {
    await getPreparationSnapshot("prep-1");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/catalog/preparations/prep-1/published-snapshot",
      expect.any(Object),
    );
  });

  it("URL-encodes ids and laboratory ids", async () => {
    await getTestSnapshot("id with space");
    expect(mockFetch).toHaveBeenCalledWith(
      "/api/public/catalog/tests/id%20with%20space/published-snapshot",
      expect.any(Object),
    );
  });
});
