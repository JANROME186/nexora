import { describe, expect, it } from "vitest";

import { permissionsForRoles } from "../auth/permissions";
import { visibleRoutesForPermissions } from "../navigation/routes";

describe("mobile permission model", () => {
  it("grants every screen permission for ADMIN", () => {
    const permissions = permissionsForRoles(["ADMIN"]);

    expect(permissions.has("SCREEN_TENANTS")).toBe(true);
    expect(permissions.has("SCREEN_AUDIT_EVENTS")).toBe(true);
    expect(permissions.size).toBe(32);
  });

  it("grants only the FRONT_DESK role's permissions", () => {
    const permissions = permissionsForRoles(["FRONT_DESK"]);

    expect(permissions.has("SCREEN_PATIENTS")).toBe(true);
    expect(permissions.has("SCREEN_TENANTS")).toBe(false);
  });
});

describe("visibleRoutesForPermissions", () => {
  it("always includes login and home regardless of permissions", () => {
    const routes = visibleRoutesForPermissions(new Set());

    expect(routes).toContain("login");
    expect(routes).toContain("home");
    expect(routes).not.toContain("tenant-summary");
  });

  it("includes a permission-gated route only when the permission is present", () => {
    const withTenants = visibleRoutesForPermissions(new Set(["SCREEN_TENANTS"]));
    expect(withTenants).toContain("tenant-summary");
    expect(withTenants).not.toContain("audit-summary");
  });

  it("includes every route for an ADMIN permission set", () => {
    const routes = visibleRoutesForPermissions(permissionsForRoles(["ADMIN"]));

    expect(routes).toEqual([
      "login",
      "home",
      "tenant-summary",
      "laboratory-summary",
      "branch-summary",
      "user-summary",
      "audit-summary",
      "results",
      "resultDetail",
      "resultHistory",
      "patient-profile",
      "patient-appointments",
      "patient-orders",
      "patient-results",
      "patient-notifications",
    ]);
  });

  it("includes only patient portal mobile routes for PATIENT role", () => {
    const routes = visibleRoutesForPermissions(permissionsForRoles(["PATIENT"]));

    expect(routes).toContain("patient-profile");
    expect(routes).toContain("patient-results");
    expect(routes).not.toContain("audit-summary");
  });
});
