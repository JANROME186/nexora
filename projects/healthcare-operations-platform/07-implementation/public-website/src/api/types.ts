// Field names mirror the backend `publicweb` DTOs exactly (see
// CatalogPublicReadPort.java / PublicAppointmentIntakeController.java /
// PublicQuotationIntakeController.java). None of these types carry tenantId, laboratoryId (in the
// response body), audit metadata or any other internal identifier — the backend deliberately
// omits them from the anonymous public surface.

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

export type PublicCatalogItemKind = "test" | "panel";

export interface PublicRequestedItemBody {
  testDefinitionId: string;
  catalogItemKind: PublicCatalogItemKind;
}

export interface PublicAppointmentRequestBody {
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  prospectiveFullName?: string;
  prospectivePhone?: string;
  prospectiveEmail?: string;
  scheduledStart: string;
  scheduledEnd: string;
  requestedItems: PublicRequestedItemBody[];
}

export interface PublicAppointmentIntakeResult {
  appointmentId: string;
  laboratoryId: string;
  branchId: string;
  scheduledStart: string;
  scheduledEnd: string;
  status: string;
  channel: string;
}

export interface PublicQuotationLineBody {
  testDefinitionId: string;
  catalogItemKind: PublicCatalogItemKind;
  quantity?: number;
}

export interface PublicQuotationRequestBody {
  tenantId: string;
  laboratoryId: string;
  branchId: string;
  prospectiveFullName?: string;
  prospectivePhone?: string;
  prospectiveEmail?: string;
  lines: PublicQuotationLineBody[];
}

export interface PublicQuotationIntakeResult {
  quotationId: string;
  laboratoryId: string;
  branchId: string;
  status: string;
}

/** RFC7807-inspired error envelope shared by every `/api/public/**` error response. */
export interface PublicApiErrorPayload {
  status?: number;
  code?: string;
  messageKey?: string;
  message?: string;
  occurredAt?: string;
}

export interface PublicMarketplacePackageSnapshot {
  packageId: string;
  code: string;
  name: string;
  category: string;
  capabilityMappings: string[];
  status: string;
}

export interface PublicMarketplaceOfferSnapshot {
  offerId: string;
  packageId: string;
  packageVersion: string;
  offerCode: string;
  offerType: string;
  lifecycleStatus: string;
  tierCodes: string[];
  trialPeriodDays: number | null;
  billingEventRulesSummary: string | null;
}
