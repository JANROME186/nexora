import { describe, it, expect } from "vitest";
import { permissionsForRoles, PERMISSION_CODES, SCREEN_TO_PERMISSION } from "./permissions";

describe("permissions utils", () => {
  it("grants every doctor-portal screen permission to REFERRING_DOCTOR", () => {
    const doctorPerms = permissionsForRoles(["REFERRING_DOCTOR"]);
    expect(doctorPerms.has("PORTAL_DOCTOR_PATIENTS_VIEW")).toBe(true);
    expect(doctorPerms.has("PORTAL_DOCTOR_RESULTS_VIEW")).toBe(true);
    expect(doctorPerms.has("PORTAL_DOCTOR_ORDERS_VIEW")).toBe(true);
    expect(doctorPerms.has("PORTAL_DOCTOR_NOTIFICATIONS_VIEW")).toBe(true);
    expect(doctorPerms.size).toBe(PERMISSION_CODES.length);
  });

  it("denies by default for unknown roles", () => {
    const unknownPerms = permissionsForRoles(["UNKNOWN_ROLE"]);
    expect(unknownPerms.size).toBe(0);
  });

  it("keeps SCREEN_TO_PERMISSION and PERMISSION_CODES in sync", () => {
    expect(PERMISSION_CODES).toEqual(Object.values(SCREEN_TO_PERMISSION));
  });
});
