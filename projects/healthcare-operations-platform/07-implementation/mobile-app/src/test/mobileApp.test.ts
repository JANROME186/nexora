import { describe, expect, it } from "vitest";

import { createMobileApp } from "../mobileApp";
import { MESSAGES } from "../i18n/messages";

describe("mobile app composition", () => {
  it("moves from login to authenticated home and exposes home actions", () => {
    const app = createMobileApp({ apiBaseUrl: "http://localhost:8080" });

    expect(app.getNavigation().currentRoute).toBe("login");

    app.loginScreen.submit({
      tenantId: "tenant-1",
      userId: "user-1",
      displayName: "Mobile Admin",
      email: "mobile.admin@example.test",
      roleCodes: ["ADMIN"],
    });

    expect(app.getNavigation().currentRoute).toBe("home");
    expect(app.homeScreen().actions.map((action) => action.route)).toContain("audit-summary");
    expect(app.resultsScreen().getState().tickets).toEqual([]);
    expect(app.resultHistoryScreen().getState().history).toEqual([]);
  });

  it("omits patient session headers before authentication", async () => {
    const capturedHeaders: Record<string, string | null> = {};
    const app = createMobileApp({
      apiBaseUrl: "http://localhost:8080",
      fetcher: async (_input, init) => {
        const headers = new Headers(init?.headers);
        capturedHeaders.authorization = headers.get("Authorization");
        capturedHeaders.tenant = headers.get("X-Tenant-Id");
        capturedHeaders.user = headers.get("X-User-Id");
        return new Response(JSON.stringify({ patientId: "patient-1", fullName: "Patient One" }));
      },
    });

    await app.patientMobileApi.getProfile("patient-1");

    expect(capturedHeaders.authorization).toBeNull();
    expect(capturedHeaders.tenant).toBeNull();
    expect(capturedHeaders.user).toBeNull();
  });

  it("throws when accessing screens without session", () => {
    const app = createMobileApp({ apiBaseUrl: "http://localhost:8080" });

    // We haven't logged in, so currentSession() is null
    expect(() => app.homeScreen()).toThrow(MESSAGES.sessionRequired);
    expect(() => app.resultsScreen()).toThrow(MESSAGES.sessionRequired);
    expect(() => app.resultHistoryScreen()).toThrow(MESSAGES.sessionRequired);
    expect(() => app.patientMobileWorkflow()).toThrow(MESSAGES.sessionRequired);

    // detail screen does not require session on creation, it gets it from api instance
    const detail = app.resultDetailScreen("ticket-1");
    expect(detail.getState().ticket).toBeNull();
  });

  it("exposes patient mobile workflow for PATIENT role", async () => {
    const capturedHeaders: Record<string, string | null> = {};
    const app = createMobileApp({
      apiBaseUrl: "http://localhost:8080",
      fetcher: async (_input, init) => {
        const headers = new Headers(init?.headers);
        capturedHeaders.authorization = headers.get("Authorization");
        capturedHeaders.tenant = headers.get("X-Tenant-Id");
        capturedHeaders.user = headers.get("X-User-Id");
        return new Response(JSON.stringify({ patientId: "patient-1", fullName: "Patient One" }));
      },
    });

    app.loginScreen.submit({
      tenantId: "tenant-1",
      userId: "patient-1",
      displayName: "Patient One",
      email: "patient.one@example.test",
      roleCodes: ["PATIENT"],
    });

    expect(app.homeScreen().actions.map((action) => action.route)).toEqual([
      "patient-profile",
      "patient-appointments",
      "patient-orders",
      "patient-results",
      "patient-notifications",
    ]);
    expect(app.patientMobileWorkflow().canAccess("patient-profile")).toBe(true);

    await app.patientMobileApi.getProfile("patient-1");
    expect(capturedHeaders.authorization).toBe("Bearer local-session:tenant-1:patient-1");
    expect(capturedHeaders.tenant).toBe("tenant-1");
    expect(capturedHeaders.user).toBe("patient-1");
  });
});
