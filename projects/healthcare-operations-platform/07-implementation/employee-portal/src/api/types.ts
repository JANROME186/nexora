export type AccessScopeType = "platform" | "tenant" | "laboratory" | "branch";

export interface AccessScope {
  type: AccessScopeType;
  id: string;
}

export interface Tenant {
  tenantId: string;
  name: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateTenantRequest {
  name: string;
}

export interface Laboratory {
  laboratoryId: string;
  tenantId: string;
  name: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateLaboratoryRequest {
  tenantId: string;
  name: string;
}

export interface Branch {
  branchId: string;
  tenantId: string;
  laboratoryId: string;
  name: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateBranchRequest {
  laboratoryId: string;
  name: string;
}

export type UserStatus = "created" | "active" | "locked" | "suspended";

export interface UserAccount {
  userId: string;
  tenantId: string;
  displayName: string;
  email: string;
  status: UserStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateUserRequest {
  tenantId: string;
  displayName: string;
  email: string;
}

export interface AssignRoleRequest {
  roleCode: string;
  scope: AccessScope;
}

export interface AuditEvent {
  auditEventId: string;
  occurredAt: string;
  tenantId?: string;
  actorId: string;
  actorType: string;
  action: string;
  subjectType: string;
  subjectId: string;
  metadataJson?: string;
}

export interface AuditEventSearchParams {
  tenantId?: string;
  subjectId?: string;
}

export type CatalogStatus = "draft" | "published" | "deprecated" | "retired";

export interface CatalogEntityBase {
  tenantId: string;
  laboratoryId: string;
  code: string;
  status: CatalogStatus;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface DiagnosticService extends CatalogEntityBase {
  serviceId: string;
  nameEn: string;
  nameEs: string;
  categoryId?: string;
  serviceType: string;
  components?: ServiceComponentLink[];
}

export interface ServiceComponentLink {
  linkId?: string;
  componentType: string;
  componentRefId: string;
  displayOrder?: number;
}

export interface CreateDiagnosticServiceRequest {
  tenantId: string;
  laboratoryId: string;
  code: string;
  nameEn: string;
  nameEs: string;
  categoryId?: string;
  serviceType: string;
  components?: ServiceComponentLink[];
}

export interface TestDefinition extends CatalogEntityBase {
  testDefinitionId: string;
  nameEn: string;
  nameEs: string;
  methodology?: string;
  measurementUnit?: string;
  resultType: string;
  turnaroundTimeHours?: number;
}

export interface CreateTestDefinitionRequest {
  tenantId: string;
  laboratoryId: string;
  code: string;
  nameEn: string;
  nameEs: string;
  methodology?: string;
  measurementUnit?: string;
  resultType: string;
  turnaroundTimeHours?: number;
  analyteRefIds?: string[];
  sampleRequirementRefIds?: string[];
}

export interface PanelDefinition extends CatalogEntityBase {
  panelId: string;
  nameEn: string;
  nameEs: string;
  members?: PanelMember[];
}

export interface PanelMember {
  memberId?: string;
  testRefId: string;
  displayOrder?: number;
  mandatory: boolean;
}

export interface CreatePanelDefinitionRequest {
  tenantId: string;
  laboratoryId: string;
  code: string;
  nameEn: string;
  nameEs: string;
  members?: PanelMember[];
}

export interface AnalyteDefinition extends CatalogEntityBase {
  analyteId: string;
  nameEn: string;
  nameEs: string;
  loincCode?: string;
  resultDataType: string;
  measurementUnit?: string;
  decimalPrecision?: number;
  minValue?: number;
  maxValue?: number;
}

export interface CreateAnalyteDefinitionRequest {
  tenantId: string;
  laboratoryId: string;
  code: string;
  nameEn: string;
  nameEs: string;
  loincCode?: string;
  resultDataType: string;
  measurementUnit?: string;
  decimalPrecision?: number;
  minValue?: number;
  maxValue?: number;
  codedValues?: Array<{ code: string; displayEn: string; displayEs: string }>;
}

export interface PreparationInstruction extends CatalogEntityBase {
  preparationId: string;
  titleEn: string;
  titleEs: string;
  instructionTextEn: string;
  instructionTextEs: string;
  category: string;
  durationHours?: number;
}

export interface CreatePreparationInstructionRequest {
  tenantId: string;
  laboratoryId: string;
  code: string;
  titleEn: string;
  titleEs: string;
  instructionTextEn: string;
  instructionTextEs: string;
  category: string;
  durationHours?: number;
}

export interface ReferenceRange extends Omit<CatalogEntityBase, "code"> {
  rangeId: string;
  analyteRefId: string;
  effectiveFrom: string;
  effectiveTo?: string;
  segments?: ReferenceRangeSegment[];
}

export interface ReferenceRangeSegment {
  segmentId?: string;
  sex: string;
  ageMinDays?: number;
  ageMaxDays?: number;
  condition?: string;
  normalLow?: number;
  normalHigh?: number;
  criticalLow?: number;
  criticalHigh?: number;
  unit?: string;
}

export interface CreateReferenceRangeRequest {
  tenantId: string;
  laboratoryId: string;
  analyteRefId: string;
  effectiveFrom: string;
  effectiveTo?: string;
  segments?: ReferenceRangeSegment[];
}

export interface SampleType extends CatalogEntityBase {
  sampleTypeId: string;
  nameEn: string;
  nameEs: string;
  matrix: string;
}

export interface CreateSampleTypeRequest {
  tenantId: string;
  laboratoryId: string;
  code: string;
  nameEn: string;
  nameEs: string;
  matrix: string;
}

export interface SampleRequirement extends Omit<CatalogEntityBase, "code"> {
  requirementId: string;
  sampleTypeRefId: string;
  minVolumeMl?: number;
  containerRefId?: string;
  handlingInstructionsEn?: string;
  handlingInstructionsEs?: string;
  storageTemperature?: string;
}

export interface CreateSampleRequirementRequest {
  tenantId: string;
  laboratoryId: string;
  sampleTypeRefId: string;
  minVolumeMl?: number;
  containerRefId?: string;
  handlingInstructionsEn?: string;
  handlingInstructionsEs?: string;
  storageTemperature?: string;
}

export interface PriceList extends CatalogEntityBase {
  priceListId: string;
  nameEn: string;
  nameEs: string;
  currency: string;
  agreementRefId?: string;
  effectiveFrom: string;
  effectiveTo?: string;
  entries?: PriceEntry[];
}

export interface PriceEntry {
  entryId?: string;
  itemType: string;
  itemRefId: string;
  currency?: string;
  amount: number;
}

export interface CreatePriceListRequest {
  tenantId: string;
  laboratoryId: string;
  code: string;
  nameEn: string;
  nameEs: string;
  currency: string;
  agreementRefId?: string;
  effectiveFrom: string;
  effectiveTo?: string;
}

export interface AddPriceEntryRequest {
  itemType: string;
  itemRefId: string;
  amount: number;
}

// -- People and Clinical Master Data (MVP-MOD-003: BCM-PER-001/002/003, BCM-ATT-002) -----------

export type PersonKind = "patient" | "doctor";

export interface PersonSearchEntry {
  tenantId: string;
  laboratoryId: string;
  personKind: string;
  sourceAggregateId: string;
  personCode: string;
  fullName: string;
  normalizedFamilyName: string;
  normalizedGivenName: string;
  birthDate?: string;
  primaryDocumentType?: string;
  primaryDocumentNumberMasked?: string;
  status: string;
}

export interface PersonDuplicateCandidate {
  personKind: string;
  sourceAggregateId: string;
  fullName: string;
  confidence: number;
  matchReason: string;
}

export interface DetectPersonDuplicatesRequest {
  tenantId: string;
  personKind?: string;
  familyName?: string;
  givenName?: string;
  birthDate?: string;
  sexAtBirth?: string;
  nationalIdentifier?: string;
}

export interface PersonSearchIndexRebuildResult {
  tenantId: string;
  patientCount: number;
  doctorCount: number;
  rebuiltAt: string;
}

export interface PersonMergeCoordination {
  coordinationId: string;
  tenantId: string;
  sourceKind: string;
  sourceRecordId: string;
  targetKind: string;
  targetRecordId: string;
  status: string;
  patientMergeApplied: boolean;
}

export interface InitiateMergeCoordinationRequest {
  tenantId: string;
  sourceRecordId: string;
  targetRecordId: string;
}

export interface PersonName {
  givenName?: string;
  middleName?: string;
  familyName?: string;
  secondFamilyName?: string;
  preferredName?: string;
}

export interface PersonDocumentValue {
  documentType: string;
  documentNumber?: string;
  issuingCountry?: string;
  issuedAt?: string;
  expiresAt?: string;
}

export interface Patient {
  patientId: string;
  tenantId: string;
  laboratoryId: string;
  patientCode: string;
  givenName?: string;
  middleName?: string;
  familyName?: string;
  secondFamilyName?: string;
  preferredName?: string;
  fullName?: string;
  birthDate?: string;
  sexAtBirth?: string;
  primaryDocumentType?: string;
  primaryDocumentNumberMasked?: string;
  status: string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface PatientSnapshot {
  patientId: string;
  tenantId: string;
  laboratoryId: string;
  patientCode: string;
  fullName?: string;
  birthDate?: string;
  sexAtBirth?: string;
  primaryDocumentType?: string;
  primaryDocumentNumberMasked?: string;
  status: string;
  version: number;
}

export interface RegisterPatientRequest {
  tenantId: string;
  laboratoryId: string;
  patientCode: string;
  givenName: string;
  middleName?: string;
  familyName: string;
  secondFamilyName?: string;
  preferredName?: string;
  birthDate?: string;
  sexAtBirth: string;
  primaryDocumentType: string;
  primaryDocumentNumber: string;
  primaryDocumentIssuingCountry?: string;
  primaryDocumentIssuedAt?: string;
  primaryDocumentExpiresAt?: string;
  addressCountry?: string;
  addressState?: string;
  addressCity?: string;
  addressPostalCode?: string;
  addressStreet?: string;
  preferredLocale?: string;
}

export interface MergePatientRequest {
  survivingPatientId: string;
}

export interface PatientRepresentative {
  representativeId: string;
  patientId: string;
  relationship: string;
  representativeName?: PersonName;
  representativeDocument?: PersonDocumentValue;
  authorizationFrom?: string;
  authorizationTo?: string;
  status: string;
}

export interface AttachPatientRepresentativeRequest {
  relationship: string;
  givenName: string;
  middleName?: string;
  familyName: string;
  secondFamilyName?: string;
  documentType: string;
  documentNumber: string;
  authorizationFrom?: string;
  authorizationTo?: string;
}

export interface PatientConsent {
  consentId: string;
  patientId: string;
  consentType: string;
  granted: boolean;
  grantedBy: string;
  grantedAt?: string;
  revokedAt?: string;
  evidenceReference?: string;
}

export interface RecordPatientConsentRequest {
  consentType: string;
  granted: boolean;
  grantedBy: string;
  evidenceReference?: string;
}

export interface Doctor {
  doctorId: string;
  tenantId: string;
  laboratoryId: string;
  doctorCode: string;
  givenName?: string;
  middleName?: string;
  familyName?: string;
  secondFamilyName?: string;
  fullName?: string;
  doctorType: string;
  primaryDocumentType?: string;
  primaryDocumentNumberMasked?: string;
  status: string;
  portalStatus: string;
  portalEmail?: string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface DoctorSnapshot {
  doctorId: string;
  tenantId: string;
  laboratoryId: string;
  doctorCode: string;
  fullName?: string;
  primaryDocumentType?: string;
  primaryDocumentNumberMasked?: string;
  doctorType: string;
  status: string;
  version: number;
}

export interface RegisterDoctorRequest {
  tenantId: string;
  laboratoryId: string;
  doctorCode: string;
  givenName: string;
  middleName?: string;
  familyName: string;
  secondFamilyName?: string;
  doctorType: string;
  primaryDocumentType: string;
  primaryDocumentNumber: string;
  primaryDocumentIssuingCountry?: string;
  primaryDocumentIssuedAt?: string;
  primaryDocumentExpiresAt?: string;
  addressCountry?: string;
  addressCity?: string;
  addressStreet?: string;
}

export interface SuspendDoctorRequest {
  reasonCode?: string;
}

export interface PreparePortalAccessRequest {
  portalEmail?: string;
}

export interface ProfessionalCredential {
  credentialId: string;
  doctorId: string;
  credentialType: string;
  credentialNumber: string;
  issuingAuthority: string;
  issuingCountry?: string;
  issuedAt?: string;
  expiresAt?: string;
  verificationStatus: string;
  verifiedAt?: string;
}

export interface AttachCredentialRequest {
  credentialType: string;
  credentialNumber: string;
  issuingAuthority: string;
  issuingCountry?: string;
  issuedAt?: string;
  expiresAt?: string;
}

export type PatientRegistrationOutcome = "pending" | "committed" | "cancelled" | "rejected";

export interface PatientRegistrationRequestRecord {
  registrationRequestId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  intakeChannel: string;
  candidatePatientId?: string;
  registrationKind: string;
  normalizedFamilyName?: string;
  normalizedGivenName?: string;
  birthDate?: string;
  draftGivenName?: string;
  draftFamilyName?: string;
  draftDocumentType?: string;
  draftDocumentNumber?: string;
  draftPatientCode?: string;
  outcome: PatientRegistrationOutcome | string;
  outcomePatientId?: string;
  actorId?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface StartPatientRegistrationRequest {
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  intakeChannel: string;
  registrationKind: string;
  givenName: string;
  familyName: string;
  birthDate?: string;
  documentType: string;
  documentNumber: string;
  draftPatientCode?: string;
  actorId?: string;
}

export interface ConsentSelectionRequest {
  consentType: string;
  granted: boolean;
  grantedBy: string;
  evidenceReference?: string;
}

export interface CommitPatientRegistrationRequest {
  resolvedExistingPatientId?: string;
  patientCode?: string;
  sexAtBirth?: string;
  addressCountry?: string;
  addressState?: string;
  addressCity?: string;
  addressPostalCode?: string;
  addressStreet?: string;
  preferredLocale?: string;
  representativeRelationship?: string;
  representativeGivenName?: string;
  representativeMiddleName?: string;
  representativeFamilyName?: string;
  representativeSecondFamilyName?: string;
  representativeDocumentType?: string;
  representativeDocumentNumber?: string;
  representativeAuthorizationFrom?: string;
  representativeAuthorizationTo?: string;
  consents?: ConsentSelectionRequest[];
}

export interface CancelPatientRegistrationRequest {
  reasonCode?: string;
}

// -- Front Desk and Care Delivery (MVP-MOD-004: BCM-ATT-003, BCM-LAB-001) -------------------------

export type ReceptionQueueStatus =
  | "waiting"
  | "called"
  | "in_admission"
  | "completed"
  | "abandoned";
export type ReceptionPriority = "normal" | "priority" | "urgent";

export interface ReceptionVisit {
  visitId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  patientId: string;
  linkedAppointmentId?: string;
  intakeChannel: string;
  identityConfirmed: boolean;
  identityConfirmationMethod?: string;
  queueStatus: ReceptionQueueStatus | string;
  priority: ReceptionPriority | string;
  actorId?: string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface StartReceptionVisitRequest {
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  patientId: string;
  linkedAppointmentId?: string;
  intakeChannel: string;
  actorId?: string;
}

export interface ConfirmReceptionIdentityRequest {
  identityConfirmationMethod: string;
}

export interface UpdateReceptionPriorityRequest {
  priority: string;
}

export type DiagnosticOrderStatus =
  | "draft"
  | "priced"
  | "accepted"
  | "in_progress"
  | "cancelled"
  | "completed";
export type OrderLineStatus = "pending" | "accepted" | "cancelled" | "completed";

export interface Money {
  currency: string;
  amount: number;
}

export interface OrderPatientSnapshot {
  patientId: string;
  sourceVersion: number;
  fullName: string;
  documentType: string;
  documentNumberMasked: string;
  birthDate?: string;
  capturedAt: string;
}

export interface OrderDoctorSnapshot {
  doctorId: string;
  sourceVersion: number;
  fullName: string;
  licenseNumber: string;
  capturedAt: string;
}

export interface OrderBranchSnapshot {
  branchId: string;
  sourceVersion: number;
  name: string;
  capturedAt: string;
}

export interface OrderPricingSnapshot {
  priceListId: string;
  priceListVersion: number;
  totalAmount: Money;
  capturedAt: string;
}

export interface DiagnosticOrder {
  orderId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  intakeChannel: string;
  sourceReferenceId?: string;
  patientSnapshot: OrderPatientSnapshot;
  doctorSnapshot?: OrderDoctorSnapshot;
  branchSnapshot: OrderBranchSnapshot;
  clinicalNotes?: string;
  pricingSnapshot?: OrderPricingSnapshot;
  status: DiagnosticOrderStatus | string;
  cancellationReason?: string;
  actorId?: string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface OrderLine {
  orderLineId: string;
  orderId: string;
  testDefinitionId: string;
  catalogItemKind: string;
  catalogItemName: string;
  catalogPublishedVersion: number;
  quantity: number;
  unitAmount?: Money;
  lineStatus: OrderLineStatus | string;
}

export interface OrderLineRequest {
  testDefinitionId: string;
  catalogItemKind: string;
  quantity?: number;
}

export interface CreateDiagnosticOrderRequest {
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  intakeChannel: string;
  sourceReferenceId?: string;
  patientId: string;
  doctorId?: string;
  actorId?: string;
  lines: OrderLineRequest[];
}

export interface PriceOrderRequest {
  currency?: string;
}

export interface AcceptOrderRequest {
  clinicalNotes?: string;
}

export interface CancelOrderRequest {
  reasonCode: string;
  overrideJustification?: string;
}
