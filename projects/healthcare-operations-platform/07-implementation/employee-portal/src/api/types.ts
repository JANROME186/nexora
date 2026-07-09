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
