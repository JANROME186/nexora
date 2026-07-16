import { get, post } from "./httpClient";
import type {
  AddPriceEntryRequest,
  AnalyteDefinition,
  CreateAnalyteDefinitionRequest,
  CreateDiagnosticServiceRequest,
  CreatePanelDefinitionRequest,
  CreatePreparationInstructionRequest,
  CreatePriceListRequest,
  CreateReferenceRangeRequest,
  CreateSampleRequirementRequest,
  CreateSampleTypeRequest,
  CreateTestDefinitionRequest,
  DiagnosticService,
  PanelDefinition,
  PreparationInstruction,
  PriceEntry,
  PriceList,
  ReferenceRange,
  SampleRequirement,
  SampleType,
  TestDefinition,
} from "./types";

const API_BASE = "/api/catalog";

function withLaboratory(basePath: string, laboratoryId: string) {
  const query = new URLSearchParams({ laboratoryId });
  return `${API_BASE}${basePath}?${query.toString()}`;
}

export function listDiagnosticServices(laboratoryId: string): Promise<DiagnosticService[]> {
  return get<DiagnosticService[]>(withLaboratory("/diagnostic-services", laboratoryId));
}

export function createDiagnosticService(
  request: CreateDiagnosticServiceRequest,
): Promise<DiagnosticService> {
  return post<DiagnosticService, CreateDiagnosticServiceRequest>(
    `${API_BASE}/diagnostic-services`,
    request,
  );
}

export function publishDiagnosticService(serviceId: string): Promise<DiagnosticService> {
  return post<DiagnosticService, Record<string, never>>(
    `${API_BASE}/diagnostic-services/${encodeURIComponent(serviceId)}/publish`,
    {},
  );
}

export function listTests(laboratoryId: string): Promise<TestDefinition[]> {
  return get<TestDefinition[]>(withLaboratory("/tests", laboratoryId));
}

export function createTest(request: CreateTestDefinitionRequest): Promise<TestDefinition> {
  return post<TestDefinition, CreateTestDefinitionRequest>(`${API_BASE}/tests`, request);
}

export function publishTest(testId: string): Promise<TestDefinition> {
  return post<TestDefinition, Record<string, never>>(
    `${API_BASE}/tests/${encodeURIComponent(testId)}/publish`,
    {},
  );
}

export function listPanels(laboratoryId: string): Promise<PanelDefinition[]> {
  return get<PanelDefinition[]>(withLaboratory("/panels", laboratoryId));
}

export function createPanel(request: CreatePanelDefinitionRequest): Promise<PanelDefinition> {
  return post<PanelDefinition, CreatePanelDefinitionRequest>(`${API_BASE}/panels`, request);
}

export function publishPanel(panelId: string): Promise<PanelDefinition> {
  return post<PanelDefinition, Record<string, never>>(
    `${API_BASE}/panels/${encodeURIComponent(panelId)}/publish`,
    {},
  );
}

export function listAnalytes(laboratoryId: string): Promise<AnalyteDefinition[]> {
  return get<AnalyteDefinition[]>(withLaboratory("/analytes", laboratoryId));
}

export function createAnalyte(request: CreateAnalyteDefinitionRequest): Promise<AnalyteDefinition> {
  return post<AnalyteDefinition, CreateAnalyteDefinitionRequest>(`${API_BASE}/analytes`, request);
}

export function publishAnalyte(analyteId: string): Promise<AnalyteDefinition> {
  return post<AnalyteDefinition, Record<string, never>>(
    `${API_BASE}/analytes/${encodeURIComponent(analyteId)}/publish`,
    {},
  );
}

export function listPreparations(laboratoryId: string): Promise<PreparationInstruction[]> {
  return get<PreparationInstruction[]>(withLaboratory("/preparations", laboratoryId));
}

export function createPreparation(
  request: CreatePreparationInstructionRequest,
): Promise<PreparationInstruction> {
  return post<PreparationInstruction, CreatePreparationInstructionRequest>(
    `${API_BASE}/preparations`,
    request,
  );
}

export function publishPreparation(preparationId: string): Promise<PreparationInstruction> {
  return post<PreparationInstruction, Record<string, never>>(
    `${API_BASE}/preparations/${encodeURIComponent(preparationId)}/publish`,
    {},
  );
}

export function listReferenceRanges(laboratoryId: string): Promise<ReferenceRange[]> {
  return get<ReferenceRange[]>(withLaboratory("/reference-ranges", laboratoryId));
}

export function createReferenceRange(
  request: CreateReferenceRangeRequest,
): Promise<ReferenceRange> {
  return post<ReferenceRange, CreateReferenceRangeRequest>(`${API_BASE}/reference-ranges`, request);
}

export function publishReferenceRange(rangeId: string): Promise<ReferenceRange> {
  return post<ReferenceRange, Record<string, never>>(
    `${API_BASE}/reference-ranges/${encodeURIComponent(rangeId)}/publish`,
    {},
  );
}

export function listSampleTypes(laboratoryId: string): Promise<SampleType[]> {
  return get<SampleType[]>(withLaboratory("/samples/types", laboratoryId));
}

export function createSampleType(request: CreateSampleTypeRequest): Promise<SampleType> {
  return post<SampleType, CreateSampleTypeRequest>(`${API_BASE}/samples/types`, request);
}

export function publishSampleType(sampleTypeId: string): Promise<SampleType> {
  return post<SampleType, Record<string, never>>(
    `${API_BASE}/samples/types/${encodeURIComponent(sampleTypeId)}/publish`,
    {},
  );
}

export function listSampleRequirements(laboratoryId: string): Promise<SampleRequirement[]> {
  return get<SampleRequirement[]>(withLaboratory("/samples/requirements", laboratoryId));
}

export function createSampleRequirement(
  request: CreateSampleRequirementRequest,
): Promise<SampleRequirement> {
  return post<SampleRequirement, CreateSampleRequirementRequest>(
    `${API_BASE}/samples/requirements`,
    request,
  );
}

export function publishSampleRequirement(requirementId: string): Promise<SampleRequirement> {
  return post<SampleRequirement, Record<string, never>>(
    `${API_BASE}/samples/requirements/${encodeURIComponent(requirementId)}/publish`,
    {},
  );
}

export function listPriceLists(laboratoryId: string): Promise<PriceList[]> {
  return get<PriceList[]>(withLaboratory("/price-lists", laboratoryId));
}

export function createPriceList(request: CreatePriceListRequest): Promise<PriceList> {
  return post<PriceList, CreatePriceListRequest>(`${API_BASE}/price-lists`, request);
}

export function addPriceEntry(
  priceListId: string,
  request: AddPriceEntryRequest,
): Promise<PriceEntry> {
  return post<PriceEntry, AddPriceEntryRequest>(
    `${API_BASE}/price-lists/${encodeURIComponent(priceListId)}/entries`,
    request,
  );
}

export function publishPriceList(priceListId: string): Promise<PriceList> {
  return post<PriceList, Record<string, never>>(
    `${API_BASE}/price-lists/${encodeURIComponent(priceListId)}/publish`,
    {},
  );
}
