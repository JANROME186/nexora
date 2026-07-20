import { describe, expect, it } from "vitest";

import { createMobileApp } from "../mobileApp";

describe("mobile foundation smoke", () => {
  it("starts at login, authenticates locally and navigates to Platform Foundation summary areas", () => {
    const app = createMobileApp({ apiBaseUrl: "http://localhost:8080" });

    expect(app.getNavigation().currentRoute).toBe("login");

    app.loginScreen.submit({
      tenantId: "tenant-1",
      userId: "user-1",
      displayName: "Mobile Operator",
      email: "mobile.operator@example.test",
    });

    expect(app.getNavigation().currentRoute).toBe("home");
    expect(app.homeScreen().scopeLabel).toBe("Alcance de tenant: tenant-1");

    app.navigateTo("tenant-summary");
    app.navigateTo("laboratory-summary");
    app.navigateTo("branch-summary");
    app.navigateTo("user-summary");
    app.navigateTo("audit-summary");

    expect(app.getNavigation().currentRoute).toBe("audit-summary");
    expect(app.getNavigation().history).toContain("home");
  });
});
