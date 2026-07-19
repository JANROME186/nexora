import { describe, it, expect } from "vitest";
import { permissionsForRoles } from "./permissions";

describe("permissions utils", () => {
  it("resolves permissions for roles correctly", () => {
    const patientPerms = permissionsForRoles(["PATIENT"]);
    expect(patientPerms.has("PORTAL_PATIENT_PROFILE_VIEW")).toBe(true);
    expect(patientPerms.has("PORTAL_PATIENT_RESULTS_VIEW")).toBe(true);

    const docPerms = permissionsForRoles(["REFERRING_DOCTOR"]);
    expect(docPerms.has("PORTAL_DOCTOR_RESULTS_VIEW")).toBe(true);
    expect(docPerms.has("PORTAL_PATIENT_PROFILE_VIEW")).toBe(false);

    const unknownPerms = permissionsForRoles(["UNKNOWN_ROLE"]);
    expect(unknownPerms.size).toBe(0);
  });
});
