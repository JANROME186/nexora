import { afterEach, describe, expect, it, vi } from "vitest";
import * as api from "../api/laboratoryOperationsApi";
import type { BackendLaboratoryResult } from "../api/laboratoryResultMapper";

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

const backendResult: BackendLaboratoryResult = {
  resultId: "result-1",
  tenantId: "tenant-1",
  laboratoryId: "lab-1",
  sampleId: "sample-1",
  status: "captured",
};

describe("laboratoryOperationsApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("builds sample worklist and lifecycle requests", async () => {
    const fetchMock = mockFetch([]);

    await api.listCollectionWorklist("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/clinical-operations/samples?tenantId=tenant-1&worklist=collection",
    );

    await api.listReceptionWorklist("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/clinical-operations/samples?tenantId=tenant-1&worklist=reception",
    );

    mockFetch({});
    const sampleFetch = mockFetch({});
    await api.getSample("sample-1");
    expect(lastFetchCall(sampleFetch)[0]).toBe("/api/clinical-operations/samples/sample-1");

    await api.collectSample("sample-1", { collectedBy: "tech-1" });
    expect(lastFetchCall(sampleFetch)).toEqual([
      "/api/clinical-operations/samples/sample-1/collect",
      expect.objectContaining({ method: "POST", body: JSON.stringify({ collectedBy: "tech-1" }) }),
    ]);

    await api.receiveSample("sample-1", { conditionCriteriaMet: true });
    expect(lastFetchCall(sampleFetch)[0]).toBe(
      "/api/clinical-operations/samples/sample-1/reception",
    );

    await api.rejectSample("sample-1", { reasonCode: "hemolyzed" });
    expect(lastFetchCall(sampleFetch)[0]).toBe("/api/clinical-operations/samples/sample-1/reject");

    await api.disposeSample("sample-1", { reasonCode: "processed" });
    expect(lastFetchCall(sampleFetch)[0]).toBe("/api/clinical-operations/samples/sample-1/dispose");

    await api.getLabelPrintJob("sample-1");
    expect(lastFetchCall(sampleFetch)[0]).toBe("/api/clinical-operations/samples/sample-1/label");
  });

  it("builds laboratory result worklist and lifecycle requests, mapping the backend wire shape", async () => {
    const listFetch = mockFetch([backendResult]);
    const processing = await api.listProcessingWorklist("tenant-1");
    expect(lastFetchCall(listFetch)[0]).toBe(
      "/api/clinical-operations/laboratory-results?tenantId=tenant-1&worklist=processing",
    );
    expect(processing).toEqual([
      expect.objectContaining({ resultId: "result-1", status: "captured" }),
    ]);

    await api.listTechnicalValidationWorklist("tenant-1");
    expect(lastFetchCall(listFetch)[0]).toBe(
      "/api/clinical-operations/laboratory-results/technical-validation-worklist?tenantId=tenant-1",
    );

    await api.listMedicalValidationWorklist("tenant-1");
    expect(lastFetchCall(listFetch)[0]).toBe(
      "/api/clinical-operations/laboratory-results/medical-validation-worklist?tenantId=tenant-1",
    );

    await api.listReleaseWorklist("tenant-1");
    expect(lastFetchCall(listFetch)[0]).toBe(
      "/api/clinical-operations/laboratory-results/release-worklist?tenantId=tenant-1",
    );

    const singleFetch = mockFetch(backendResult);
    const result = await api.getLaboratoryResult("result-1");
    expect(lastFetchCall(singleFetch)[0]).toBe(
      "/api/clinical-operations/laboratory-results/result-1",
    );
    expect(result.resultId).toBe("result-1");

    const resultValue = {
      rawValue: "5.0",
      capturedAt: "2026-07-18T00:00:00Z",
      capturedBy: "tech-1",
    };
    await api.captureResult("result-1", { values: [resultValue] });
    expect(lastFetchCall(singleFetch)).toEqual([
      "/api/clinical-operations/laboratory-results/result-1/capture",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ values: [resultValue] }),
      }),
    ]);

    await api.recordIncident("result-1", {
      incidentType: "sample_hemolyzed",
      description: "notes",
    });
    expect(lastFetchCall(singleFetch)[0]).toBe(
      "/api/clinical-operations/laboratory-results/result-1/incident",
    );

    await api.submitForValidation("result-1");
    expect(lastFetchCall(singleFetch)).toEqual([
      "/api/clinical-operations/laboratory-results/result-1/submit",
      expect.objectContaining({ method: "POST", body: "{}" }),
    ]);

    await api.validateTechnically("result-1", { notes: "ok" });
    expect(lastFetchCall(singleFetch)[0]).toBe(
      "/api/clinical-operations/laboratory-results/result-1/technical-validation",
    );

    await api.validateMedically("result-1", { notes: "ok" });
    expect(lastFetchCall(singleFetch)[0]).toBe(
      "/api/clinical-operations/laboratory-results/result-1/medical-validation",
    );

    await api.releaseResult("result-1", { notes: "ok" });
    expect(lastFetchCall(singleFetch)[0]).toBe(
      "/api/clinical-operations/laboratory-results/result-1/release",
    );

    await api.amendResult("result-1", {
      reason: "correction",
      newValues: [{ rawValue: "5.2", capturedAt: "2026-07-18T00:00:00Z", capturedBy: "tech-1" }],
    });
    expect(lastFetchCall(singleFetch)[0]).toBe(
      "/api/clinical-operations/laboratory-results/result-1/amend",
    );
  });
});
