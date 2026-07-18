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
    });

    expect(app.getNavigation().currentRoute).toBe("home");
    expect(app.homeScreen().actions.map((action) => action.route)).toContain("audit-summary");
  });

  it("throws when accessing screens without session", () => {
    const app = createMobileApp({ apiBaseUrl: "http://localhost:8080" });

    // We haven't logged in, so currentSession() is null
    expect(() => app.homeScreen()).toThrow(MESSAGES.sessionRequired);
    expect(() => app.resultsScreen()).toThrow(MESSAGES.sessionRequired);
    expect(() => app.resultHistoryScreen()).toThrow(MESSAGES.sessionRequired);

    // detail screen does not require session on creation, it gets it from api instance
    const detail = app.resultDetailScreen("ticket-1");
    expect(detail.getState().ticket).toBeNull();
  });
});
