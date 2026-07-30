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

/**
 * COM-MOD-014-PORTAL-001 / HOP-HARD-APP-001: lists a referred patient's delivered imaging studies
 * as a referring doctor. The backend (`ImagingStudyDeliveryController`/
 * `ImagingStudyDeliveryService`) verifies `doctorId` has actually referred `patientId`
 * (`ReferringDoctorAuthorizationPort`) before returning any data, mirroring
 * `patientResultHistoryApi.getPatientHistoryAsDoctor`'s referral check for laboratory results.
 */
export function getPatientImagingDeliveryPackagesAsDoctor(
  patientId: string,
  doctorId: string,
): Promise<ImagingDeliveryPackage[]> {
  const params = new URLSearchParams({
    patientId,
    callerRoleCode: "REFERRING_DOCTOR",
    callerId: doctorId,
  });
  return get(`/api/v1/imaging/delivery-packages?${params.toString()}`);
}
