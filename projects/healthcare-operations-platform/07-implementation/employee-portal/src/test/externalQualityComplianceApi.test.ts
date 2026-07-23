import { afterEach, describe, expect, it, vi } from "vitest";
import {
  approveExternalQualityControl,
  assignCapa,
  closeCapa,
  closeQualityAudit,
  createExternalQualityControl,
  exportComplianceEvidence,
  linkQualityEvent,
  listCapaRecords,
  listExternalQualityControls,
  listQualityAudits,
  listQualityEvents,
  openCapa,
  openQualityAudit,
  planQualityAudit,
  recordAuditFinding,
  recordQualityEvent,
  rejectExternalQualityControl,
  searchComplianceEvidence,
  searchDocuments,
  verifyCapa,
} from "../api/externalQualityComplianceApi";

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

describe("externalQualityComplianceApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
    window.localStorage.clear();
  });

  // BCM-QLT-002 External Quality Controls
  it("lists external QC scoped by tenant and laboratory", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ externalQCId: "eqc-1" }] });
    const result = await listExternalQualityControls("tenant-1", "lab-1");
    expect(result).toEqual([{ externalQCId: "eqc-1" }]);
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/external-controls?tenantId=tenant-1&laboratoryId=lab-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("creates an external QC via POST", async () => {
    const fetchMock = mockFetchOnce({
      status: 201,
      jsonBody: { externalQCId: "eqc-2" },
    });
    await createExternalQualityControl("t-1", "l-1", {
      controlType: "ISO17025",
      providerName: "Acumed",
      referenceCode: "REF-001",
      description: "Check",
    });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/external-controls?tenantId=t-1&laboratoryId=l-1",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("approves an external QC via PUT", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { externalQCId: "eqc-1", status: "approved" } });
    await approveExternalQualityControl("eqc-1", { reviewedBy: "sup-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/external-controls/eqc-1/approve",
      expect.objectContaining({ method: "PUT" }),
    );
  });

  it("rejects an external QC via PUT", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { externalQCId: "eqc-1", status: "rejected" } });
    await rejectExternalQualityControl("eqc-1", { reviewedBy: "sup-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/external-controls/eqc-1/reject",
      expect.objectContaining({ method: "PUT" }),
    );
  });

  // BCM-QLT-006 CAPA
  it("lists CAPA records scoped by tenant and laboratory", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ capaId: "capa-1" }] });
    await listCapaRecords("t-1", "l-1");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/capa?tenantId=t-1&laboratoryId=l-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("opens a CAPA via POST", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { capaId: "capa-2" } });
    await openCapa("t-1", "l-1", { sourceEventType: "contamination", description: "Incident" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/capa?tenantId=t-1&laboratoryId=l-1",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("assigns CAPA via PUT", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { capaId: "capa-1" } });
    await assignCapa("capa-1", { assignedTo: "user-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/capa/capa-1/assign",
      expect.objectContaining({ method: "PUT" }),
    );
  });

  it("closes CAPA via PUT", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { capaId: "capa-1", status: "closed" } });
    await closeCapa("capa-1", { closedBy: "mgr-1", correctiveAction: "SOP updated" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/capa/capa-1/close",
      expect.objectContaining({ method: "PUT" }),
    );
  });

  it("verifies CAPA via PUT", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { capaId: "capa-1", status: "verified" } });
    await verifyCapa("capa-1", { verifiedBy: "qm-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/capa/capa-1/verify",
      expect.objectContaining({ method: "PUT" }),
    );
  });

  // BCM-QLT-007 Quality Audits
  it("lists quality audits via GET", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ auditId: "a-1" }] });
    await listQualityAudits("t-1", "l-1");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/audits?tenantId=t-1&laboratoryId=l-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("plans a quality audit via POST", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { auditId: "a-2" } });
    await planQualityAudit("t-1", "l-1", { auditType: "internal", auditorName: "Dr. Lopez" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/audits?tenantId=t-1&laboratoryId=l-1",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("opens a quality audit via PUT", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { auditId: "a-1", status: "open" } });
    await openQualityAudit("a-1", {});
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/audits/a-1/open",
      expect.objectContaining({ method: "PUT" }),
    );
  });

  it("records a finding via POST", async () => {
    const fetchMock = mockFetchOnce({ status: 201, jsonBody: { auditId: "a-1" } });
    await recordAuditFinding("a-1", {
      category: "documentation",
      description: "Missing SOP",
      severity: "minor",
    });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/audits/a-1/findings",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("closes a quality audit via PUT", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { auditId: "a-1", status: "closed" } });
    await closeQualityAudit("a-1", { closedBy: "director-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/audits/a-1/close",
      expect.objectContaining({ method: "PUT" }),
    );
  });

  // BCM-PLT-007 Compliance Evidence
  it("searches compliance evidence with query params", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ exportId: "exp-1" }] });
    await searchComplianceEvidence({ tenantId: "t-1", subjectType: "CapaRecord" });
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining("/api/audit/events"),
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("exports compliance evidence via POST", async () => {
    const fetchMock = mockFetchOnce({
      status: 201,
      jsonBody: { exportId: "exp-2", recordCount: 10 },
    });
    await exportComplianceEvidence({ requestedBy: "auditor-1" });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/audit/events/export",
      expect.objectContaining({ method: "POST" }),
    );
  });

  // BCM-PLT-008 Documents
  it("searches documents with query params", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ documentId: "doc-1" }] });
    await searchDocuments({ tenantId: "t-1", ownerType: "CAPA" });
    expect(fetchMock).toHaveBeenCalledWith(
      expect.stringContaining("/api/documents"),
      expect.objectContaining({ method: "GET" }),
    );
  });

  // Quality Event Intake
  it("lists quality events via GET", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: [{ qualityEventId: "qev-1" }] });
    await listQualityEvents("t-1", "l-1");
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/events/intake?tenantId=t-1&laboratoryId=l-1",
      expect.objectContaining({ method: "GET" }),
    );
  });

  it("records a quality event via POST", async () => {
    const fetchMock = mockFetchOnce({
      status: 201,
      jsonBody: { qualityEventId: "qev-2" },
    });
    await recordQualityEvent("t-1", "l-1", {
      eventType: "near_miss",
      description: "Mislabeled",
      reportedBy: "tech-1",
    });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/events/intake?tenantId=t-1&laboratoryId=l-1",
      expect.objectContaining({ method: "POST" }),
    );
  });

  it("links a quality event via PUT", async () => {
    const fetchMock = mockFetchOnce({ jsonBody: { qualityEventId: "qev-1", status: "linked" } });
    await linkQualityEvent("qev-1", {
      linkedInvestigationId: "capa-5",
      linkedInvestigationType: "capa",
    });
    expect(fetchMock).toHaveBeenCalledWith(
      "/api/quality/events/intake/qev-1/link",
      expect.objectContaining({ method: "PUT" }),
    );
  });
});
