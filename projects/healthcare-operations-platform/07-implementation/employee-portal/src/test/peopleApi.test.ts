import { afterEach, describe, expect, it, vi } from "vitest";
import * as api from "../api/peopleApi";

function mockFetch(responseBody: unknown = {}) {
  const fetchMock = vi.fn().mockResolvedValue({
    ok: true,
    status: 200,
    statusText: "OK",
    json: async () => responseBody
  } as Response);
  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

function lastFetchCall(fetchMock: ReturnType<typeof mockFetch>) {
  return fetchMock.mock.calls[fetchMock.mock.calls.length - 1] as [string, RequestInit];
}

describe("peopleApi", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("builds person search, duplicate, index and merge coordination requests", async () => {
    const fetchMock = mockFetch([]);

    await api.searchPersons("tenant-1", {
      personKind: "patient",
      familyName: "Lovelace",
      givenName: "Ada",
      birthDate: "1990-01-01"
    });
    expect(lastFetchCall(fetchMock)[0]).toBe(
      "/api/people/persons/search?tenantId=tenant-1&personKind=patient&familyName=Lovelace&givenName=Ada&birthDate=1990-01-01"
    );

    await api.searchPersons("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/persons/search?tenantId=tenant-1");

    await api.detectPersonDuplicates({ tenantId: "tenant-1", personKind: "patient", familyName: "Lovelace" });
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/people/persons/duplicates/detect",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ tenantId: "tenant-1", personKind: "patient", familyName: "Lovelace" })
      })
    ]);

    await api.rebuildPersonSearchIndex("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/persons/index/rebuild?tenantId=tenant-1");

    await api.initiatePersonMergeCoordination({
      tenantId: "tenant-1",
      sourceRecordId: "patient/source",
      targetRecordId: "patient/target"
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/persons/merges");

    await api.getPersonMergeCoordination("merge/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/persons/merges/merge%2F1");
  });

  it("builds patient lifecycle requests", async () => {
    const fetchMock = mockFetch({});

    await api.listPatients("lab-1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/patients?laboratoryId=lab-1");

    await api.getPatient("patient/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/patients/patient%2F1");

    await api.getPatientSnapshot("patient/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/patients/patient%2F1/snapshot");

    await api.registerPatient({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      patientCode: "P-001",
      givenName: "Ada",
      familyName: "Lovelace",
      sexAtBirth: "female",
      primaryDocumentType: "national_id",
      primaryDocumentNumber: "DOC-1"
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/patients");

    await api.mergePatient("patient/source", { survivingPatientId: "patient/target" });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/patients/patient%2Fsource/merge");

    await api.listPatientRepresentatives("patient/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/patients/patient%2F1/representatives");

    await api.attachPatientRepresentative("patient/1", {
      relationship: "parent",
      givenName: "Grace",
      familyName: "Hopper",
      documentType: "national_id",
      documentNumber: "REP-1"
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/patients/patient%2F1/representatives");

    await api.revokePatientRepresentative("patient/1", "rep/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/people/patients/patient%2F1/representatives/rep%2F1/revoke",
      expect.objectContaining({ method: "POST", body: "{}" })
    ]);

    await api.listPatientConsents("patient/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/patients/patient%2F1/consents");

    await api.recordPatientConsent("patient/1", {
      consentType: "RESULTS_RELEASE",
      granted: true,
      grantedBy: "operator",
      evidenceReference: "signed"
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/patients/patient%2F1/consents");

    await api.revokePatientConsent("patient/1", "consent/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/people/patients/patient%2F1/consents/consent%2F1/revoke",
      expect.objectContaining({ method: "POST", body: "{}" })
    ]);
  });

  it("builds doctor lifecycle requests", async () => {
    const fetchMock = mockFetch({});

    await api.listDoctors("lab-1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/doctors?laboratoryId=lab-1");

    await api.getDoctor("doctor/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/doctors/doctor%2F1");

    await api.getDoctorSnapshot("doctor/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/doctors/doctor%2F1/snapshot");

    await api.registerDoctor({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      doctorCode: "D-001",
      givenName: "Marie",
      familyName: "Curie",
      doctorType: "referring_external",
      primaryDocumentType: "professional_license",
      primaryDocumentNumber: "MD-1"
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/doctors");

    await api.suspendDoctor("doctor/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/people/doctors/doctor%2F1/suspend",
      expect.objectContaining({ method: "POST", body: "{}" })
    ]);

    await api.preparePortalAccess("doctor/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/people/doctors/doctor%2F1/portal-access/prepare",
      expect.objectContaining({ method: "POST", body: "{}" })
    ]);

    await api.listDoctorCredentials("doctor/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/doctors/doctor%2F1/credentials");

    await api.attachDoctorCredential("doctor/1", {
      credentialType: "license",
      credentialNumber: "MD-1",
      issuingAuthority: "board",
      issuingCountry: "MX"
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/people/doctors/doctor%2F1/credentials");

    await api.verifyDoctorCredential("doctor/1", "credential/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/people/doctors/doctor%2F1/credentials/credential%2F1/verify",
      expect.objectContaining({ method: "POST", body: "{}" })
    ]);

    await api.revokeDoctorCredential("doctor/1", "credential/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/people/doctors/doctor%2F1/credentials/credential%2F1/revoke",
      expect.objectContaining({ method: "POST", body: "{}" })
    ]);
  });

  it("builds patient registration workflow requests", async () => {
    const fetchMock = mockFetch({});

    await api.listPatientRegistrations("tenant-1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/patient-registrations?tenantId=tenant-1");

    await api.getPatientRegistration("registration/1");
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/patient-registrations/registration%2F1");

    await api.startPatientRegistration({
      tenantId: "tenant-1",
      laboratoryId: "lab-1",
      branchId: "branch-1",
      intakeChannel: "front_desk",
      registrationKind: "new_patient",
      givenName: "Ada",
      familyName: "Lovelace",
      documentType: "national_id",
      documentNumber: "DOC-1"
    });
    expect(lastFetchCall(fetchMock)[0]).toBe("/api/care-delivery/patient-registrations");

    await api.commitPatientRegistration("registration/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/patient-registrations/registration%2F1/commit",
      expect.objectContaining({ method: "POST", body: "{}" })
    ]);

    await api.cancelPatientRegistration("registration/1");
    expect(lastFetchCall(fetchMock)).toEqual([
      "/api/care-delivery/patient-registrations/registration%2F1/cancel",
      expect.objectContaining({ method: "POST", body: "{}" })
    ]);
  });
});
