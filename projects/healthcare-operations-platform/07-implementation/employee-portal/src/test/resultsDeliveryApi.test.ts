import { describe, expect, it, vi, beforeEach } from "vitest";
import * as http from "../api/httpClient";
import {
  listReleasedResults,
  getResultById,
  listResultReports,
  regenerateReport,
  authorizeDelivery,
  getDeliveryTicket,
  listOpenEscalations,
  acknowledgeCriticalEscalation,
  escalateCriticalEscalation,
  closeCriticalEscalation,
  listResultNotifications,
} from "../api/resultsDeliveryApi";
import type {
  GeneratedResultReport,
  ResultDeliveryTicket,
  CriticalResultEscalation,
  ResultNotificationRequest,
} from "../api/types";

const mockBackendResult = {
  resultId: "res-1",
  tenantId: "t-1",
  laboratoryId: "lab-1",
  sampleId: "samp-1",
  analyteSnapshot: { testDefinitionId: "td-1", analyteId: "an-1", name: "Glucose", unit: "mg/dL" },
  referenceRangeSnapshot: { referenceRangeId: "rr-1", lowValue: "70", highValue: "110" },
  resultValue: { rawValue: "95", capturedAt: "2026-07-17T10:00:00Z", capturedBy: "tech-1" },
  processingIncidents: [],
  amendments: [],
  status: "released",
};

const mockReport: GeneratedResultReport = {
  reportId: "rpt-1",
  resultId: "res-1",
  tenantId: "t-1",
  status: "generated",
  generatedAt: "2026-07-17T10:00:00Z",
  generatedBy: "system",
};

const mockTicket: ResultDeliveryTicket = {
  ticketId: "tick-1",
  resultId: "res-1",
  tenantId: "t-1",
  patientId: "pat-1",
  accessCode: "ABC123",
  status: "AUTHORIZED",
  expiresAt: "2026-07-18T10:00:00Z",
};

const mockEscalation: CriticalResultEscalation = {
  escalationId: "esc-1",
  tenantId: "t-1",
  laboratoryId: "lab-1",
  resultId: "res-1",
  criticalReason: "Critical high value",
  escalationTier: 1,
  acknowledgementDeadline: "2026-07-17T12:00:00Z",
  status: "OPEN",
};

const mockNotification: ResultNotificationRequest = {
  notificationRequestId: "notif-1",
  resultId: "res-1",
  tenantId: "t-1",
  recipientType: "patient",
  recipientId: "pat-1",
  channel: "sms",
  status: "delivered",
  dispatchedAt: "2026-07-17T10:01:00Z",
  deliveredAt: "2026-07-17T10:02:00Z",
};

describe("resultsDeliveryApi", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  // BCM-RES-001
  it("listReleasedResults calls correct endpoint and normalizes the response", async () => {
    vi.spyOn(http, "get").mockResolvedValue([mockBackendResult]);
    const result = await listReleasedResults("t-1");
    expect(http.get).toHaveBeenCalledWith(
      "/api/clinical-operations/laboratory-results?tenantId=t-1&status=released",
    );
    expect(result).toEqual([
      {
        resultId: "res-1",
        tenantId: "t-1",
        laboratoryId: "lab-1",
        sampleId: "samp-1",
        testDefinitionId: "td-1",
        status: "released",
        analyteSnapshots: [{ analyteRefId: "an-1", name: "Glucose", unit: "mg/dL" }],
        referenceRangeSnapshots: [
          {
            rangeRefId: "rr-1",
            normalLow: 70,
            normalHigh: 110,
            criticalLow: undefined,
            criticalHigh: undefined,
          },
        ],
        resultValues: [
          {
            rawValue: "95",
            numericValue: undefined,
            unit: undefined,
            method: undefined,
            capturedAt: "2026-07-17T10:00:00Z",
            capturedBy: "tech-1",
            deviceReference: undefined,
          },
        ],
        incidents: [],
        technicalValidation: undefined,
        medicalValidation: undefined,
        releaseRecord: undefined,
        amendments: [],
        version: 1,
      },
    ]);
  });

  it("getResultById calls correct endpoint with tenantId and normalizes the response", async () => {
    vi.spyOn(http, "get").mockResolvedValue(mockBackendResult);
    const result = await getResultById("res-1", "t-1");
    expect(http.get).toHaveBeenCalledWith(
      "/api/clinical-operations/laboratory-results/res-1?tenantId=t-1",
    );
    expect(result.resultId).toBe("res-1");
    expect(result.testDefinitionId).toBe("td-1");
  });

  // BCM-RES-002
  it("listResultReports calls correct endpoint with tenantId", async () => {
    vi.spyOn(http, "get").mockResolvedValue([mockReport]);
    const result = await listResultReports("res-1", "t-1");
    expect(http.get).toHaveBeenCalledWith(
      "/api/clinical-operations/laboratory-results/res-1/reports?tenantId=t-1",
    );
    expect(result).toEqual([mockReport]);
  });

  it("regenerateReport posts to correct endpoint with tenantId and actorId", async () => {
    vi.spyOn(http, "post").mockResolvedValue(mockReport);
    const result = await regenerateReport("res-1", "t-1", "actor-1");
    expect(http.post).toHaveBeenCalledWith(
      "/api/clinical-operations/laboratory-results/res-1/reports/regenerate?tenantId=t-1&actorId=actor-1",
      {},
    );
    expect(result).toEqual(mockReport);
  });

  // BCM-RES-004
  it("authorizeDelivery posts to correct endpoint", async () => {
    vi.spyOn(http, "post").mockResolvedValue([mockTicket]);
    const result = await authorizeDelivery("res-1", "t-1", "actor-1");
    expect(http.post).toHaveBeenCalledWith(
      "/api/results/delivery/authorize?resultId=res-1&tenantId=t-1&actorId=actor-1",
      {},
    );
    expect(result).toEqual([mockTicket]);
  });

  it("getDeliveryTicket calls correct endpoint", async () => {
    vi.spyOn(http, "get").mockResolvedValue(mockTicket);
    const result = await getDeliveryTicket("tick-1", "t-1", "caller-1", "actor-1");
    expect(http.get).toHaveBeenCalledWith(
      "/api/results/delivery/tick-1?tenantId=t-1&callerId=caller-1&actorId=actor-1",
    );
    expect(result).toEqual(mockTicket);
  });

  // BCM-RES-006
  it("listOpenEscalations calls correct endpoint", async () => {
    vi.spyOn(http, "get").mockResolvedValue([mockEscalation]);
    const result = await listOpenEscalations("t-1");
    expect(http.get).toHaveBeenCalledWith("/api/results/critical-escalations/open?tenantId=t-1");
    expect(result).toEqual([mockEscalation]);
  });

  it("acknowledgeCriticalEscalation posts to correct endpoint", async () => {
    vi.spyOn(http, "post").mockResolvedValue({ ...mockEscalation, status: "ACKNOWLEDGED" });
    const result = await acknowledgeCriticalEscalation("esc-1", "user-1", "actor-1");
    expect(http.post).toHaveBeenCalledWith(
      "/api/results/critical-escalations/esc-1/acknowledge?userId=user-1&actorId=actor-1",
      {},
    );
    expect(result.status).toBe("ACKNOWLEDGED");
  });

  it("escalateCriticalEscalation posts to correct endpoint", async () => {
    vi.spyOn(http, "post").mockResolvedValue({ ...mockEscalation, status: "ESCALATED" });
    const result = await escalateCriticalEscalation("esc-1", "actor-1");
    expect(http.post).toHaveBeenCalledWith(
      "/api/results/critical-escalations/esc-1/escalate?actorId=actor-1",
      {},
    );
    expect(result.status).toBe("ESCALATED");
  });

  it("closeCriticalEscalation posts to correct endpoint", async () => {
    vi.spyOn(http, "post").mockResolvedValue({ ...mockEscalation, status: "CLOSED" });
    const result = await closeCriticalEscalation("esc-1", "actor-1");
    expect(http.post).toHaveBeenCalledWith(
      "/api/results/critical-escalations/esc-1/close?actorId=actor-1",
      {},
    );
    expect(result.status).toBe("CLOSED");
  });

  // BCM-RES-007
  it("listResultNotifications calls correct endpoint with tenantId", async () => {
    vi.spyOn(http, "get").mockResolvedValue([mockNotification]);
    const result = await listResultNotifications("res-1", "t-1");
    expect(http.get).toHaveBeenCalledWith(
      "/api/clinical-operations/laboratory-results/res-1/notifications?tenantId=t-1",
    );
    expect(result).toEqual([mockNotification]);
  });
});
