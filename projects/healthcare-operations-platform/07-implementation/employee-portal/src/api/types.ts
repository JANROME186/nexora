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

// -- Cashier Operations and Billing Request (MVP-MOD-005: BCM-ATT-005, BCM-ATT-008) ---------------

export type CashSessionStatus = "open" | "closed";

export interface CashSession {
  sessionId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  openedBy: string;
  openingAmount: Money;
  expectedAmount: Money;
  countedAmount?: Money;
  varianceAmount?: Money;
  varianceReason?: string;
  status: CashSessionStatus | string;
  openedAt?: string;
  closedAt?: string;
}

export interface OpenCashSessionRequest {
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  openedBy: string;
  openingAmount: number;
  currency?: string;
}

export interface CloseCashSessionRequest {
  countedAmount: number;
  currency?: string;
  varianceReason?: string;
}

export interface SaleTotals {
  subtotalAmount: Money;
  discountAmount: Money;
  totalAmount: Money;
  paidAmount: Money;
  outstandingAmount: Money;
}

export interface SaleLine {
  saleLineId: string;
  saleId: string;
  catalogItemId: string;
  catalogItemKind: string;
  descriptionSnapshot: string;
  quantity: number;
  unitAmount: Money;
  lineTotal: Money;
}

export interface PaymentAllocation {
  paymentId: string;
  saleId: string;
  sessionId?: string;
  amount: Money;
  method: string;
  reference?: string;
  registeredBy: string;
  registeredAt?: string;
}

export type SaleStatus = "payable" | "partially_paid" | "paid" | "cancelled" | "refunded";
export type SaleSourceType = "diagnostic_order" | "quotation";
export type PaymentMethod = "cash" | "card" | "transfer" | "wallet" | "mixed";

export interface Sale {
  saleId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  patientId: string;
  sourceType: SaleSourceType | string;
  sourceReferenceId: string;
  totals: SaleTotals;
  status: SaleStatus | string;
  cancellationReason?: string;
  actorId?: string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateSaleRequest {
  tenantId: string;
  sourceType: SaleSourceType | string;
  sourceReferenceId: string;
  actorId?: string;
}

export interface RegisterPaymentRequest {
  amount: number;
  currency?: string;
  method: PaymentMethod | string;
  sessionId?: string;
  reference?: string;
  registeredBy: string;
}

export interface CancelSaleRequest {
  reasonCode: string;
}

export type BillingRequestStatus = "requested" | "submitted" | "issued" | "failed" | "cancelled";

export interface FiscalProfileSnapshot {
  legalName: string;
  taxIdentifier: string;
  fiscalAddress: string;
  fiscalRegime?: string;
  capturedAt: string;
}

export interface TaxLine {
  taxLineId: string;
  invoiceRequestId: string;
  baseAmount: Money;
  taxCode: string;
  taxRate: number;
  taxAmount: Money;
}

export interface InvoiceRequest {
  invoiceRequestId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  saleId: string;
  patientId: string;
  fiscalProfileSnapshot: FiscalProfileSnapshot;
  status: BillingRequestStatus | string;
  adapterCorrelationId?: string;
  adapterResponseSnapshot?: string;
  actorId?: string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateBillingRequestRequest {
  saleId: string;
  legalName: string;
  taxIdentifier: string;
  fiscalAddress: string;
  fiscalRegime?: string;
  taxCode?: string;
  taxRate?: number;
  actorId?: string;
}

// -- Laboratory Workflow (MVP-MOD-006: BCM-LAB-002 to BCM-LAB-010) -------------------------

export type SampleStatus =
  | "collected"
  | "labeled"
  | "in_transit"
  | "received"
  | "rejected"
  | "in_process"
  | "disposed";

export interface Sample {
  sampleId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  orderId: string;
  patientId: string;
  sampleTypeRefId: string;
  status: SampleStatus | string;
  collectionDate?: string;
  collectedBy?: string;
  receptionDate?: string;
  receivedBy?: string;
  rejectionReason?: string;
  disposalReason?: string;
  version: number;
}

export interface CollectSampleRequest {
  collectedBy?: string;
}

export interface ReceiveSampleRequest {
  conditionCriteriaMet?: boolean;
}

export interface RejectSampleRequest {
  reasonCode: string;
}

export interface DisposeSampleRequest {
  reasonCode: string;
}

export type ResultStatus =
  | "captured"
  | "pending_technical_validation"
  | "technically_validated"
  | "pending_medical_validation"
  | "medically_validated"
  | "released"
  | "amended";

export interface AnalyteSnapshot {
  analyteRefId: string;
  name: string;
  unit?: string;
}

export interface ReferenceRangeSnapshot {
  rangeRefId: string;
  normalLow?: number;
  normalHigh?: number;
  criticalLow?: number;
  criticalHigh?: number;
}

export interface ProcessingIncident {
  incidentType: string;
  description: string;
  loggedAt: string;
  loggedBy: string;
}

export interface ResultValue {
  rawValue: string;
  numericValue?: number;
  unit?: string;
  method?: string;
  capturedAt: string;
  capturedBy: string;
  deviceReference?: string;
}

export interface TechnicalValidationRecord {
  notes?: string;
  validatedAt?: string;
  validatedBy?: string;
}

export interface MedicalValidationRecord {
  notes?: string;
  validatedAt?: string;
  validatedBy?: string;
}

export interface ReleaseRecord {
  notes?: string;
  releasedAt?: string;
  releasedBy?: string;
}

export interface AmendmentRecord {
  reason: string;
  amendedAt: string;
  amendedBy: string;
}

export interface LaboratoryResult {
  resultId: string;
  tenantId: string;
  laboratoryId: string;
  sampleId: string;
  testDefinitionId: string;
  status: ResultStatus | string;
  analyteSnapshots: AnalyteSnapshot[];
  referenceRangeSnapshots: ReferenceRangeSnapshot[];
  resultValues: ResultValue[];
  incidents: ProcessingIncident[];
  technicalValidation?: TechnicalValidationRecord;
  medicalValidation?: MedicalValidationRecord;
  releaseRecord?: ReleaseRecord;
  amendments: AmendmentRecord[];
  version: number;
}

export interface CaptureResultRequest {
  values: ResultValue[];
}

export interface RecordIncidentRequest {
  incidentType: string;
  description: string;
}

export interface ValidateResultRequest {
  notes?: string;
}

export interface ReleaseResultRequest {
  notes?: string;
}

export interface AmendResultRequest {
  reason: string;
  newValues: ResultValue[];
}

// -- Results and Digital Delivery (MVP-MOD-007: BCM-RES-001 to BCM-RES-007, BCM-PLT-003, BCM-PLT-008) ----

export type DeliveryTicketStatus =
  | "PENDING_AUTHORIZATION"
  | "AUTHORIZED"
  | "DELIVERED"
  | "VIEWED"
  | "WITHHELD"
  | "EXPIRED";

export interface ResultDeliveryTicket {
  ticketId: string;
  resultId: string;
  tenantId: string;
  patientId: string;
  accessCode: string;
  status: DeliveryTicketStatus | string;
  expiresAt: string;
  recipientType?: string;
  recipientId?: string;
  deliveryChannel?: string;
  deliveredAt?: string;
  viewedAt?: string;
}

export type CriticalEscalationStatus = "OPEN" | "ACKNOWLEDGED" | "ESCALATED" | "CLOSED";

export interface CriticalResultEscalation {
  escalationId: string;
  tenantId: string;
  laboratoryId: string;
  resultId: string;
  criticalReason: string;
  escalationTier: number;
  acknowledgementDeadline: string;
  acknowledgedBy?: string;
  acknowledgedAt?: string;
  assignedHandlerId?: string;
  status: CriticalEscalationStatus | string;
}

export type ReportStatus = "pending" | "generated" | "generation_failed" | "superseded";

export interface GeneratedResultReport {
  reportId: string;
  resultId: string;
  tenantId: string;
  status: ReportStatus | string;
  documentId?: string;
  integrityChecksum?: string;
  generatedAt?: string;
  generatedBy?: string;
}

export type NotificationStatus =
  | "pending_submission"
  | "submitted"
  | "dispatched"
  | "delivered"
  | "failed";

export interface ResultNotificationRequest {
  notificationRequestId: string;
  resultId: string;
  tenantId: string;
  recipientType: string;
  recipientId: string;
  channel: string;
  status: NotificationStatus | string;
  dispatchedAt?: string;
  deliveredAt?: string;
  failureReason?: string;
  createdAt?: string;
}

export interface AuthorizeDeliveryRequest {
  resultId: string;
  tenantId: string;
  actorId: string;
}

// -- Integration and Migration Readiness (MVP-MOD-008: BCM-PLT-004/005/010) ----

export interface IntegrationEndpoint {
  endpointId: string;
  tenantId: string;
  laboratoryId: string;
  endpointName: string;
  protocol: string;
  direction: string;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface RegisterIntegrationEndpointRequest {
  tenantId: string;
  laboratoryId: string;
  endpointName: string;
  protocol: string;
  direction: string;
  actorId: string;
}

export interface IntegrationMessage {
  messageId: string;
  endpointId: string;
  externalMessageId: string;
  correlationId?: string;
  normalizationStatus: string;
  canonicalErrorCode?: string;
  retryCount: number;
}

export interface IntegrationMessageDetail extends IntegrationMessage {
  sourceProtocol?: string;
  rawPayloadReference?: string;
  normalizedMessageType?: string;
  canonicalFields?: Record<string, string>;
  targetBoundedContext?: string;
  nextRetryAt?: string;
  deadLetterReason?: string;
}

export interface ReceiveIntegrationMessageRequest {
  externalMessageId: string;
  rawPayload?: string;
  actorId: string;
}

export interface RetryIntegrationMessageRequest {
  rawPayload?: string;
  actorId: string;
}

export interface ApiSurfaceRegistration {
  registrationId: string;
  tenantId?: string;
  ownerCapability: string;
  operationId: string;
  classification: string;
  apiVersion: string;
  deprecationStatus: string;
  deprecationWindowFrom?: string;
  deprecationWindowTo?: string;
  migrationNote?: string;
}

export interface ClassifyApiOperationRequest {
  ownerCapability: string;
  classification: string;
  apiVersion: string;
  tenantId?: string;
  actorId: string;
}

export interface ScheduleApiDeprecationRequest {
  deprecationWindowFrom?: string;
  deprecationWindowTo?: string;
  migrationNote?: string;
  actorId: string;
}

export interface PartnerApiKey {
  keyId: string;
  tenantId: string;
  consumerName: string;
  grantedScopes: string[];
  rateLimitPolicyRef?: string;
  status: string;
}

export interface IssuePartnerApiKeyRequest {
  tenantId: string;
  consumerName: string;
  grantedScopes: string[];
  actorId: string;
}

export interface RateLimitPolicy {
  policyId: string;
  classification: string;
  requestsPerMinute: number;
}

export interface SetRateLimitPolicyRequest {
  requestsPerMinute: number;
  actorId: string;
}

export interface MigrationJob {
  migrationJobId: string;
  tenantId: string;
  laboratoryId: string;
  sourceSystemName: string;
  status: string;
}

export interface CreateMigrationJobRequest {
  tenantId: string;
  laboratoryId: string;
  sourceSystemName: string;
  actorId: string;
}

export interface ImportBatch {
  importBatchId: string;
  migrationJobId: string;
  storedPackageReference: string;
  entityCounts: Record<string, number>;
}

export interface ImportExecution {
  executionId: string;
  migrationJobId: string;
  attemptNumber: number;
  domainCommandsInvoked: string[];
  checkpoint: string;
  status: string;
}

export interface DryRunReport {
  reportId: string;
  importBatchId: string;
  structuralErrors: string[];
  rowLevelErrors: string[];
  rowLevelWarnings: string[];
  validationCategoriesEvaluated: string[];
  passed: boolean;
}

export interface MigrationJobStatus {
  migrationJobId: string;
  status: string;
}

export interface ReconciliationReport {
  reconciliationReportId: string;
  migrationJobId: string;
  phase: string;
  importedCounts: Record<string, number>;
  rejectedCounts: Record<string, number>;
  skippedCounts: Record<string, number>;
  warningCounts: Record<string, number>;
}

// -- Inventory and Internal Quality (COM-MOD-010: BCM-INV-001..009, BCM-QLT-001/003/004/005) ----

export interface StockSummary {
  onHandQuantity?: string;
  reservedQuantity?: string;
  reorderPoint?: string;
  reorderQuantity?: string;
  lastMovementAt?: string;
}

export interface ReagentProfile {
  linkedTestDefinitionId?: string;
  reagentCategory: string;
  consumptionUnitRatio: string;
}

export interface EquipmentProfile {
  assetTag: string;
  serialNumber?: string;
  manufacturer?: string;
  model?: string;
  location?: string;
  availabilityStatus: string;
  installedAt?: string;
}

export interface InventoryItem {
  inventoryItemId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  itemCode: string;
  itemName: string;
  itemType: string;
  classification: string;
  unitOfMeasure: string;
  status: string;
  stockSummary?: StockSummary;
  reagentProfile?: ReagentProfile;
  equipmentProfile?: EquipmentProfile;
  createdAt?: string;
  updatedAt?: string;
}

export interface RegisterInventoryItemRequest {
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  itemCode: string;
  itemName: string;
  itemType: string;
  classification: string;
  unitOfMeasure: string;
  actorId: string;
}

export interface UpdateInventoryItemRequest {
  itemName: string;
  itemType: string;
  classification: string;
  unitOfMeasure: string;
  status: string;
  actorId: string;
}

export interface ReagentProfileRecord {
  inventoryItemId: string;
  linkedTestDefinitionId?: string;
  reagentCategory: string;
  consumptionUnitRatio: string;
}

export interface AssignReagentProfileRequest {
  linkedTestDefinitionId?: string;
  reagentCategory: string;
  consumptionUnitRatio: string;
  actorId: string;
}

export interface StockLot {
  stockLotId: string;
  inventoryItemId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  lotNumber: string;
  supplierId?: string;
  supplierName?: string;
  status: string;
  expirationDate?: string;
  receivedQuantity: string;
  remainingQuantity: string;
}

export interface RegisterStockLotRequest {
  lotNumber: string;
  supplierId?: string;
  supplierName?: string;
  expirationDate?: string;
  receivedQuantity?: string;
  actorId: string;
}

export interface PurchaseOrderLineRequest {
  inventoryItemId: string;
  orderedQuantity: string;
  unitCost: string;
}

export interface CreatePurchaseOrderRequest {
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  supplierId: string;
  supplierName: string;
  currencyCode: string;
  lines: PurchaseOrderLineRequest[];
  actorId: string;
}

export interface PurchaseOrderLine {
  purchaseOrderLineId: string;
  inventoryItemId: string;
  lineStatus: string;
  orderedQuantity: string;
  unitCost: string;
  receivedQuantity: string;
}

export interface PurchaseOrder {
  purchaseOrderId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  supplierId: string;
  supplierName: string;
  status: string;
  currencyCode: string;
  totalAmount: string;
  approverId?: string;
  cancellationReason?: string;
  lines: PurchaseOrderLine[];
}

export interface ApprovalRequest {
  actorId: string;
}

export interface CancelRequest {
  reason: string;
  actorId: string;
}

export interface ReceiveLineRequest {
  receivedQuantity: string;
  stockLotId?: string;
  actorId: string;
}

export interface StockEntry {
  stockEntryId: string;
  inventoryItemId: string;
  stockLotId?: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  purchaseOrderLineId?: string;
  entryType: string;
  reasonCode?: string;
  quantity: string;
  receivedAt?: string;
}

export interface ApplyStockReceiptRequest {
  inventoryItemId: string;
  stockLotId?: string;
  purchaseOrderId?: string;
  purchaseOrderLineId?: string;
  reasonCode?: string;
  quantity: string;
  entryType: string;
  actorId: string;
}

export interface StockExit {
  stockExitId: string;
  inventoryItemId: string;
  stockLotId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  destinationBranchId?: string;
  exitType: string;
  reasonCode?: string;
  quantity: string;
  occurredAt?: string;
}

export interface ApplyStockExitRequest {
  inventoryItemId: string;
  stockLotId: string;
  destinationBranchId?: string;
  reasonCode?: string;
  quantity: string;
  exitType: string;
  actorId: string;
}

export interface ConsumptionRecord {
  consumptionRecordId: string;
  inventoryItemId: string;
  stockLotId?: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  diagnosticOrderId?: string;
  testDefinitionId?: string;
  consumptionContext: string;
  consumedQuantity: string;
  occurredAt?: string;
}

export interface ApplyConsumptionRequest {
  inventoryItemId: string;
  stockLotId?: string;
  diagnosticOrderId?: string;
  testDefinitionId?: string;
  consumedQuantity: string;
  consumptionContext: string;
  actorId: string;
}

export interface AdjustmentRecord {
  adjustmentId: string;
  inventoryItemId: string;
  stockLotId?: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  reasonCode: string;
  reasonNote?: string;
  approverId: string;
  requestedBy: string;
  deltaQuantity: string;
  occurredAt?: string;
}

export interface ApplyAdjustmentRequest {
  inventoryItemId: string;
  stockLotId?: string;
  reasonNote?: string;
  deltaQuantity: string;
  reasonCode: string;
  requestedBy: string;
  approverId: string;
  actorId: string;
}

export interface WasteRecord {
  wasteRecordId: string;
  inventoryItemId: string;
  stockLotId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  reasonCode: string;
  reasonNote?: string;
  disposedQuantity: string;
  disposedAt?: string;
}

export interface ApplyWasteRequest {
  inventoryItemId: string;
  stockLotId: string;
  disposedQuantity: string;
  reasonCode: string;
  reasonNote?: string;
  actorId: string;
}

export interface QualityControlRun {
  qcRunId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  testDefinitionId: string;
  controlMaterialStockLotId: string;
  ruleEvaluation: string;
  acceptanceDecision: string;
  performedBy: string;
  evidenceReference?: string;
  overrideReason?: string;
  overrideBy?: string;
  measuredValue: string;
  expectedMin: string;
  expectedMax: string;
  linkedLaboratoryResultIds: string[];
  performedAt?: string;
}

export interface RecordQualityControlRunRequest {
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  testDefinitionId: string;
  controlMaterialStockLotId: string;
  measuredValue: string;
  expectedMin: string;
  expectedMax: string;
  linkedLaboratoryResultIds?: string[];
  performedBy: string;
  performedAt?: string;
  evidenceReference?: string;
}

export interface OverrideAcceptanceDecisionRequest {
  acceptanceDecision: string;
  overrideReason: string;
  supervisorId: string;
  supervisorScoped: boolean;
}

export interface CalibrationEvent {
  calibrationEventId: string;
  inventoryItemId: string;
  tenantId: string;
  branchId: string;
  calibrationStandardRef: string;
  performedBy: string;
  result: string;
  certificateReference?: string;
  performedAt?: string;
  nextDueDate?: string;
}

export interface RecordCalibrationRequest {
  calibrationStandardRef: string;
  performedBy: string;
  result: string;
  performedAt?: string;
  nextDueDate?: string;
  certificateReference?: string;
}

export interface EquipmentProfileRecord {
  assetTag: string;
  serialNumber?: string;
  manufacturer?: string;
  model?: string;
  location?: string;
  availabilityStatus: string;
  installedAt?: string;
}

export interface SetEquipmentProfileRequest {
  assetTag: string;
  serialNumber?: string;
  manufacturer?: string;
  model?: string;
  location?: string;
  availabilityStatus: string;
  actorId: string;
}

export interface ChangeAvailabilityRequest {
  newStatus: string;
  reasonCode: string;
  actorId: string;
}

export interface AvailabilityChangeRecord {
  changeId: string;
  inventoryItemId: string;
  previousStatus: string;
  newStatus: string;
  reasonCode: string;
  changedBy: string;
  changedAt?: string;
}

export interface MaintenanceEvent {
  maintenanceEventId: string;
  inventoryItemId: string;
  tenantId: string;
  branchId: string;
  maintenanceType: string;
  performedBy?: string;
  externalTechnicianRef?: string;
  description: string;
  startedAt?: string;
  completedAt?: string;
  downtimeMinutes?: number;
  nextScheduledAt?: string;
}

export interface RecordMaintenanceRequest {
  maintenanceType: string;
  description: string;
  performedBy?: string;
  externalTechnicianRef?: string;
  startedAt?: string;
  completedAt?: string;
  nextScheduledAt?: string;
  downtimeMinutes?: number;
}

export interface CompleteMaintenanceRequest {
  actorId: string;
  completedAt?: string;
  nextScheduledAt?: string;
  downtimeMinutes?: number;
}

// -- Public Website Content and Request Administration (COM-MOD-011-FE-001: BCM-SVC-001/002/003/005,
// BCM-ATT-001, BCM-ATT-006) -----------------------------------------------------------------------
//
// The published-content snapshot types mirror the anonymous public-website DTOs exactly (see
// public-website/src/api/types.ts and backend CatalogPublicReadPort.java): no tenantId, audit
// metadata or other internal identifier is present, so this screen shows staff exactly what a
// public visitor sees without needing to redact anything.

export interface PublicDiagnosticServiceSnapshot {
  serviceId: string;
  code: string;
  nameEn: string;
  nameEs: string;
  serviceType: string;
  version: number;
}

export interface PublicTestSnapshot {
  testDefinitionId: string;
  code: string;
  nameEn: string;
  nameEs: string;
  methodology: string;
  measurementUnit: string | null;
  resultType: string;
  turnaroundTimeHours: number;
  version: number;
}

export interface PublicPanelSnapshot {
  panelId: string;
  code: string;
  nameEn: string;
  nameEs: string;
  version: number;
}

export interface PublicPreparationSnapshot {
  preparationId: string;
  code: string;
  titleEn: string;
  titleEs: string;
  instructionTextEn: string;
  instructionTextEs: string;
  category: string;
  durationHours: number | null;
  version: number;
}

export type AppointmentChannel =
  | "walk_in_scheduling"
  | "phone"
  | "employee_portal"
  | "patient_portal_request_later"
  | "public_website";
export type AppointmentStatus =
  | "requested"
  | "confirmed"
  | "checked_in"
  | "cancelled"
  | "no_show"
  | "completed";

export interface AppointmentSlot {
  appointmentId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  patientId?: string;
  doctorId?: string;
  scheduledStart: string;
  scheduledEnd: string;
  channel: AppointmentChannel | string;
  status: AppointmentStatus | string;
  linkedOrderId?: string;
  cancellationReason?: string;
  actorId?: string;
  prospectiveFullName?: string;
  prospectivePhone?: string;
  prospectiveEmail?: string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CancelAppointmentRequest {
  reasonCode?: string;
}

export type QuotationChannel =
  | "walk_in_scheduling"
  | "phone"
  | "employee_portal"
  | "patient_portal_request_later"
  | "public_website";
export type QuotationStatus =
  | "draft"
  | "issued"
  | "accepted"
  | "expired"
  | "converted"
  | "cancelled";

export interface QuotationRequest {
  quotationId: string;
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  patientId?: string;
  prospectiveFullName?: string;
  prospectivePhone?: string;
  prospectiveEmail?: string;
  priceListId?: string;
  priceListVersion?: number;
  totalAmount?: Money;
  discountKind?: string;
  discountValue?: number;
  validUntil?: string;
  channel: QuotationChannel | string;
  status: QuotationStatus | string;
  convertedOrderId?: string;
  cancellationReason?: string;
  actorId?: string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface IssueQuotationRequest {
  currency?: string;
  discountKind?: string;
  discountValue?: number;
  validityDays?: number;
  discountOverride?: boolean;
}

export interface CancelQuotationRequest {
  reasonCode?: string;
}

// -- Advanced Quality and Compliance (COM-MOD-013-FE-001) ------------------------------------
// BCM-QLT-002 External Quality Controls, BCM-QLT-006 CAPA Management,
// BCM-QLT-007 Audit Management, BCM-PLT-007 Compliance Evidence Export,
// BCM-PLT-008 Document Management, Quality Event Intake.

// BCM-QLT-002 External Quality Controls
export type ExternalQCStatus = "pending" | "in_review" | "approved" | "rejected" | "closed";

export interface ExternalQualityControl {
  externalQCId: string;
  tenantId: string;
  laboratoryId: string;
  branchId?: string;
  controlType: string;
  providerName: string;
  referenceCode: string;
  description: string;
  status: ExternalQCStatus | string;
  performedAt?: string;
  reviewedBy?: string;
  reviewedAt?: string;
  comments?: string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateExternalQCRequest {
  controlType: string;
  providerName: string;
  referenceCode: string;
  description: string;
  performedAt?: string;
}

export interface ReviewExternalQCRequest {
  reviewedBy: string;
  comments?: string;
}

// BCM-QLT-006 CAPA Management
export type CapaStatus = "open" | "assigned" | "in_progress" | "closed" | "verified";

export interface CapaRecord {
  capaId: string;
  tenantId: string;
  laboratoryId: string;
  sourceEventType: string;
  sourceEventId?: string;
  description: string;
  rootCauseAnalysis?: string;
  correctiveAction?: string;
  preventiveAction?: string;
  assignedTo?: string;
  dueDate?: string;
  closedAt?: string;
  closedBy?: string;
  verifiedAt?: string;
  verifiedBy?: string;
  status: CapaStatus | string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface OpenCapaRequest {
  sourceEventType: string;
  sourceEventId?: string;
  description: string;
}

export interface AssignCapaRequest {
  assignedTo: string;
  dueDate?: string;
  rootCauseAnalysis?: string;
}

export interface CloseCapaRequest {
  closedBy: string;
  correctiveAction: string;
  preventiveAction?: string;
}

export interface VerifyCapaRequest {
  verifiedBy: string;
}

// BCM-QLT-007 Audit Management
export type QualityAuditStatus = "planned" | "open" | "findings_recorded" | "closed";

export interface QualityAuditFinding {
  findingId: string;
  category: string;
  description: string;
  severity: string;
  recordedAt?: string;
}

export interface QualityAudit {
  auditId: string;
  tenantId: string;
  laboratoryId: string;
  auditType: string;
  auditorName: string;
  scheduledDate?: string;
  openedAt?: string;
  closedAt?: string;
  closedBy?: string;
  status: QualityAuditStatus | string;
  findings: QualityAuditFinding[];
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface PlanQualityAuditRequest {
  auditType: string;
  auditorName: string;
  scheduledDate?: string;
}

export interface OpenQualityAuditRequest {
  auditorName?: string;
}

export interface RecordFindingRequest {
  category: string;
  description: string;
  severity: string;
}

export interface CloseQualityAuditRequest {
  closedBy: string;
}

// BCM-PLT-007 Compliance Evidence Export / Search
export interface ComplianceEvidenceExport {
  exportId: string;
  tenantId: string;
  requestedBy: string;
  fromDate?: string;
  toDate?: string;
  subjectType?: string;
  documentId?: string;
  exportedAt: string;
  recordCount: number;
  status: string;
}

export interface ExportComplianceEvidenceRequest {
  requestedBy: string;
  fromDate?: string;
  toDate?: string;
  subjectType?: string;
}

export interface SearchComplianceEvidenceParams {
  tenantId?: string;
  subjectType?: string;
  subjectId?: string;
  fromDate?: string;
  toDate?: string;
}

// BCM-PLT-008 Document Management (compliance surface)
export interface StoredDocument {
  documentId: string;
  tenantId: string;
  ownerId?: string;
  ownerType?: string;
  fileName: string;
  contentType: string;
  storedAt: string;
  retentionUntil?: string;
  tags?: string[];
  version: number;
}

export interface SearchDocumentsParams {
  tenantId?: string;
  ownerType?: string;
  ownerId?: string;
  tag?: string;
}

// Quality Event Intake (operational/clinical event linked to quality investigations)
export type QualityEventStatus = "open" | "under_investigation" | "linked" | "closed";

export interface QualityEvent {
  qualityEventId: string;
  tenantId: string;
  laboratoryId: string;
  eventType: string;
  description: string;
  reportedBy: string;
  reportedAt?: string;
  linkedInvestigationId?: string;
  linkedInvestigationType?: string;
  status: QualityEventStatus | string;
  version: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface RecordQualityEventRequest {
  eventType: string;
  description: string;
  reportedBy: string;
  reportedAt?: string;
}

export interface LinkQualityEventRequest {
  linkedInvestigationId: string;
  linkedInvestigationType: string;
}
