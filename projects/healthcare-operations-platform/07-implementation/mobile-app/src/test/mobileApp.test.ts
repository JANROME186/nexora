import { describe, expect, it } from "vitest";

import { createMobileApp } from "../mobileApp";

describe("mobile app composition", () => {
  it("moves from login to authenticated home and exposes home actions", () => {
    const app = createMobileApp({ apiBaseUrl: "http://localhost:8080" });

    expect(app.getNavigation().currentRoute).toBe("login");

    app.loginScreen.submit({
      tenantId: "tenant-1",
      userId: "user-1",
      displayName: "Mobile Admin",
      email: "mobile.admin@example.test"
    });

    expect(app.getNavigation().currentRoute).toBe("home");
    expect(app.homeScreen().actions.map((action) => action.route)).toContain("audit-summary");
  });
});
