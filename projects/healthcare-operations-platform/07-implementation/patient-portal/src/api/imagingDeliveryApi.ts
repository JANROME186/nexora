import { get } from "./httpClient";

export interface ImagingDeliveryPackage {
  packageId: string;
  tenantId: string;
  studyId: string;
  patientId: string;
  deliveryFormat: string;
  deliveryStatus: string;
  portalAccessToken: string;
  expiresAt: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

export interface RadiologyReport {
  reportId: string;
  tenantId: string;
  studyId: string;
  radiologistId: string;
  findingsText: string;
  impressionText: string;
  reportStatus: string;
  signedAt?: string;
  digitalSignatureHash?: string;
  createdBy: string;
  createdAt: string;
  updatedBy: string;
  updatedAt: string;
}

/**
 * COM-MOD-014-PORTAL-001 / HOP-HARD-APP-001: lists the caller's own delivered imaging studies.
 * The backend (`ImagingStudyDeliveryController`/`ImagingStudyDeliveryService`) verifies the
 * PATIENT caller owns `patientId` before returning any package, mirroring
 * `patientResultHistoryApi.getPatientHistory`'s self-access boundary for laboratory results.
 */
export function getMyImagingDeliveryPackages(patientId: string): Promise<ImagingDeliveryPackage[]> {
  const params = new URLSearchParams({
    patientId,
    callerRoleCode: "PATIENT",
    callerId: patientId,
  });
  return get(`/api/v1/imaging/delivery-packages?${params.toString()}`);
}

/**
 * Lists the signed/draft radiology reports for one of the caller's own delivered studies. The
 * backend resolves the study's owning patient and denies access if it does not match `patientId`.
 */
export function getMyImagingReportsForStudy(
  studyId: string,
  patientId: string,
): Promise<RadiologyReport[]> {
  const params = new URLSearchParams({
    studyId,
    callerRoleCode: "PATIENT",
    callerId: patientId,
  });
  return get(`/api/v1/imaging/reports?${params.toString()}`);
}
