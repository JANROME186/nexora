import { del, get, post, put } from "./httpClient";
import type {
  AssignSpecialtyRequest,
  AttachCredentialRequest,
  AttachPatientDocumentRequest,
  AttachPatientRepresentativeRequest,
  CancelPatientRegistrationRequest,
  CommitPatientRegistrationRequest,
  DetectPersonDuplicatesRequest,
  Doctor,
  DoctorSnapshot,
  InitiateMergeCoordinationRequest,
  MergePatientRequest,
  Patient,
  PatientConsent,
  PatientDocument,
  PatientRegistrationRequestRecord,
  PatientRepresentative,
  PatientSnapshot,
  PersonDuplicateCandidate,
  PersonMergeCoordination,
  PersonSearchEntry,
  PersonSearchIndexRebuildResult,
  PreparePortalAccessRequest,
  ProfessionalCredential,
  RecordPatientConsentRequest,
  RegisterDoctorRequest,
  RegisterPatientRequest,
  SpecialtyAssignment,
  StartPatientRegistrationRequest,
  SuspendDoctorRequest,
  UpdateDoctorRequest,
  UpdatePatientRequest,
} from "./types";

const PERSONS_BASE = "/api/people/persons";
const PATIENTS_BASE = "/api/people/patients";
const DOCTORS_BASE = "/api/people/doctors";
const REGISTRATIONS_BASE = "/api/care-delivery/patient-registrations";

// -- BCM-PER-001 Person Management (global search, duplicate detection, merge coordination) -----

export function searchPersons(
  tenantId: string,
  filters?: { personKind?: string; familyName?: string; givenName?: string; birthDate?: string },
): Promise<PersonSearchEntry[]> {
  const query = new URLSearchParams({ tenantId });
  if (filters?.personKind) query.set("personKind", filters.personKind);
  if (filters?.familyName) query.set("familyName", filters.familyName);
  if (filters?.givenName) query.set("givenName", filters.givenName);
  if (filters?.birthDate) query.set("birthDate", filters.birthDate);
  return get<PersonSearchEntry[]>(`${PERSONS_BASE}/search?${query.toString()}`);
}

export function detectPersonDuplicates(
  request: DetectPersonDuplicatesRequest,
): Promise<PersonDuplicateCandidate[]> {
  return post<PersonDuplicateCandidate[], DetectPersonDuplicatesRequest>(
    `${PERSONS_BASE}/duplicates/detect`,
    request,
  );
}

export function rebuildPersonSearchIndex(
  tenantId: string,
): Promise<PersonSearchIndexRebuildResult> {
  const query = new URLSearchParams({ tenantId });
  return post<PersonSearchIndexRebuildResult, Record<string, never>>(
    `${PERSONS_BASE}/index/rebuild?${query.toString()}`,
    {},
  );
}

export function getPersonMergeCoordination(
  coordinationId: string,
): Promise<PersonMergeCoordination> {
  return get<PersonMergeCoordination>(
    `${PERSONS_BASE}/merges/${encodeURIComponent(coordinationId)}`,
  );
}

export function initiatePersonMergeCoordination(
  request: InitiateMergeCoordinationRequest,
): Promise<PersonMergeCoordination> {
  return post<PersonMergeCoordination, InitiateMergeCoordinationRequest>(
    `${PERSONS_BASE}/merges`,
    request,
  );
}

// -- BCM-PER-002 Patient Management --------------------------------------------------------------

export function listPatients(laboratoryId: string): Promise<Patient[]> {
  const query = new URLSearchParams({ laboratoryId });
  return get<Patient[]>(`${PATIENTS_BASE}?${query.toString()}`);
}

export function getPatientSnapshot(patientId: string): Promise<PatientSnapshot> {
  return get<PatientSnapshot>(`${PATIENTS_BASE}/${encodeURIComponent(patientId)}/snapshot`);
}

export function registerPatient(request: RegisterPatientRequest): Promise<Patient> {
  return post<Patient, RegisterPatientRequest>(PATIENTS_BASE, request);
}

export function updatePatient(patientId: string, request: UpdatePatientRequest): Promise<Patient> {
  return put<Patient, UpdatePatientRequest>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}`,
    request,
  );
}

export function deactivatePatient(patientId: string): Promise<Patient> {
  return post<Patient, Record<string, never>>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}/deactivate`,
    {},
  );
}

export function mergePatient(patientId: string, request: MergePatientRequest): Promise<Patient> {
  return post<Patient, MergePatientRequest>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}/merge`,
    request,
  );
}

export function listPatientRepresentatives(patientId: string): Promise<PatientRepresentative[]> {
  return get<PatientRepresentative[]>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}/representatives`,
  );
}

export function attachPatientRepresentative(
  patientId: string,
  request: AttachPatientRepresentativeRequest,
): Promise<PatientRepresentative> {
  return post<PatientRepresentative, AttachPatientRepresentativeRequest>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}/representatives`,
    request,
  );
}

export function revokePatientRepresentative(
  patientId: string,
  representativeId: string,
): Promise<PatientRepresentative> {
  return post<PatientRepresentative, Record<string, never>>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}/representatives/${encodeURIComponent(representativeId)}/revoke`,
    {},
  );
}

export function updatePatientRepresentative(
  patientId: string,
  representativeId: string,
  request: AttachPatientRepresentativeRequest,
): Promise<PatientRepresentative> {
  return put<PatientRepresentative, AttachPatientRepresentativeRequest>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}/representatives/${encodeURIComponent(representativeId)}`,
    request,
  );
}

export function listPatientDocuments(patientId: string): Promise<PatientDocument[]> {
  return get<PatientDocument[]>(`${PATIENTS_BASE}/${encodeURIComponent(patientId)}/documents`);
}

export function attachPatientDocument(
  patientId: string,
  request: AttachPatientDocumentRequest,
): Promise<PatientDocument> {
  return post<PatientDocument, AttachPatientDocumentRequest>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}/documents`,
    request,
  );
}

export function removePatientDocument(patientId: string, documentId: string): Promise<void> {
  return del<void>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}/documents/${encodeURIComponent(documentId)}`,
  );
}

export function listPatientConsents(patientId: string): Promise<PatientConsent[]> {
  return get<PatientConsent[]>(`${PATIENTS_BASE}/${encodeURIComponent(patientId)}/consents`);
}

export function recordPatientConsent(
  patientId: string,
  request: RecordPatientConsentRequest,
): Promise<PatientConsent> {
  return post<PatientConsent, RecordPatientConsentRequest>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}/consents`,
    request,
  );
}

export function revokePatientConsent(
  patientId: string,
  consentId: string,
): Promise<PatientConsent> {
  return post<PatientConsent, Record<string, never>>(
    `${PATIENTS_BASE}/${encodeURIComponent(patientId)}/consents/${encodeURIComponent(consentId)}/revoke`,
    {},
  );
}

// -- BCM-PER-003 Doctor Management ---------------------------------------------------------------

export function listDoctors(laboratoryId: string): Promise<Doctor[]> {
  const query = new URLSearchParams({ laboratoryId });
  return get<Doctor[]>(`${DOCTORS_BASE}?${query.toString()}`);
}

export function getDoctorSnapshot(doctorId: string): Promise<DoctorSnapshot> {
  return get<DoctorSnapshot>(`${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/snapshot`);
}

export function registerDoctor(request: RegisterDoctorRequest): Promise<Doctor> {
  return post<Doctor, RegisterDoctorRequest>(DOCTORS_BASE, request);
}

export function updateDoctor(doctorId: string, request: UpdateDoctorRequest): Promise<Doctor> {
  return put<Doctor, UpdateDoctorRequest>(
    `${DOCTORS_BASE}/${encodeURIComponent(doctorId)}`,
    request,
  );
}

export function suspendDoctor(doctorId: string, request?: SuspendDoctorRequest): Promise<Doctor> {
  return post<Doctor, SuspendDoctorRequest>(
    `${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/suspend`,
    request ?? {},
  );
}

export function retireDoctor(doctorId: string): Promise<Doctor> {
  return post<Doctor, Record<string, never>>(
    `${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/retire`,
    {},
  );
}

export function preparePortalAccess(
  doctorId: string,
  request?: PreparePortalAccessRequest,
): Promise<Doctor> {
  return post<Doctor, PreparePortalAccessRequest>(
    `${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/portal-access/prepare`,
    request ?? {},
  );
}

export function listDoctorCredentials(doctorId: string): Promise<ProfessionalCredential[]> {
  return get<ProfessionalCredential[]>(
    `${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/credentials`,
  );
}

export function attachDoctorCredential(
  doctorId: string,
  request: AttachCredentialRequest,
): Promise<ProfessionalCredential> {
  return post<ProfessionalCredential, AttachCredentialRequest>(
    `${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/credentials`,
    request,
  );
}

export function verifyDoctorCredential(
  doctorId: string,
  credentialId: string,
): Promise<ProfessionalCredential> {
  return post<ProfessionalCredential, Record<string, never>>(
    `${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/credentials/${encodeURIComponent(credentialId)}/verify`,
    {},
  );
}

export function revokeDoctorCredential(
  doctorId: string,
  credentialId: string,
): Promise<ProfessionalCredential> {
  return post<ProfessionalCredential, Record<string, never>>(
    `${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/credentials/${encodeURIComponent(credentialId)}/revoke`,
    {},
  );
}

export function listSpecialtyAssignments(doctorId: string): Promise<SpecialtyAssignment[]> {
  return get<SpecialtyAssignment[]>(`${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/specialties`);
}

export function assignSpecialty(
  doctorId: string,
  request: AssignSpecialtyRequest,
): Promise<SpecialtyAssignment> {
  return post<SpecialtyAssignment, AssignSpecialtyRequest>(
    `${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/specialties`,
    request,
  );
}

export function unassignSpecialty(doctorId: string, assignmentId: string): Promise<void> {
  return del<void>(
    `${DOCTORS_BASE}/${encodeURIComponent(doctorId)}/specialties/${encodeURIComponent(assignmentId)}`,
  );
}

// -- BCM-ATT-002 Patient Registration -------------------------------------------------------------

export function listPatientRegistrations(
  tenantId: string,
): Promise<PatientRegistrationRequestRecord[]> {
  const query = new URLSearchParams({ tenantId });
  return get<PatientRegistrationRequestRecord[]>(`${REGISTRATIONS_BASE}?${query.toString()}`);
}

export function startPatientRegistration(
  request: StartPatientRegistrationRequest,
): Promise<PatientRegistrationRequestRecord> {
  return post<PatientRegistrationRequestRecord, StartPatientRegistrationRequest>(
    REGISTRATIONS_BASE,
    request,
  );
}

export function commitPatientRegistration(
  registrationRequestId: string,
  request?: CommitPatientRegistrationRequest,
): Promise<PatientRegistrationRequestRecord> {
  return post<PatientRegistrationRequestRecord, CommitPatientRegistrationRequest>(
    `${REGISTRATIONS_BASE}/${encodeURIComponent(registrationRequestId)}/commit`,
    request ?? {},
  );
}

export function cancelPatientRegistration(
  registrationRequestId: string,
  request?: CancelPatientRegistrationRequest,
): Promise<PatientRegistrationRequestRecord> {
  return post<PatientRegistrationRequestRecord, CancelPatientRegistrationRequest>(
    `${REGISTRATIONS_BASE}/${encodeURIComponent(registrationRequestId)}/cancel`,
    request ?? {},
  );
}
