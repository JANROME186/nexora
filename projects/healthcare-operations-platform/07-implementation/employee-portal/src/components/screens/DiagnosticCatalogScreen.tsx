import { useMemo, useState, type FormEvent } from "react";
import {
  createAnalyte,
  createDiagnosticService,
  createPanel,
  createPreparation,
  createPriceList,
  createReferenceRange,
  createSampleType,
  createTest,
  listAnalytes,
  listDiagnosticServices,
  listPanels,
  listPreparations,
  listPriceLists,
  listReferenceRanges,
  listSampleRequirements,
  listSampleTypes,
  listTests,
  publishAnalyte,
  publishDiagnosticService,
  publishPanel,
  publishPreparation,
  publishPriceList,
  publishReferenceRange,
  publishSampleRequirement,
  publishSampleType,
  publishTest,
} from "../../api/catalogApi";
import type {
  AnalyteDefinition,
  CatalogStatus,
  DiagnosticService,
  PanelDefinition,
  PreparationInstruction,
  PriceList,
  ReferenceRange,
  SampleRequirement,
  SampleType,
  TestDefinition,
} from "../../api/types";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

type CatalogArea =
  | "services"
  | "tests"
  | "panels"
  | "analytes"
  | "preparations"
  | "reference-ranges"
  | "samples"
  | "price-lists";

interface CatalogRow {
  id: string;
  code: string;
  name: string;
  status: CatalogStatus | string;
  version: number;
  detail: string;
  publishTarget?: "sample-type" | "sample-requirement";
}

const AREAS: Array<{ key: CatalogArea; label: string }> = [
  { key: "services", label: "Services" },
  { key: "tests", label: "Tests" },
  { key: "panels", label: "Panels" },
  { key: "analytes", label: "Analytes" },
  { key: "preparations", label: "Preparations" },
  { key: "reference-ranges", label: "Reference Ranges" },
  { key: "samples", label: "Samples" },
  { key: "price-lists", label: "Price Lists" },
];

function upsertById<TRow extends { id: string }>(items: TRow[], next: TRow) {
  return [next, ...items.filter((item) => item.id !== next.id)];
}

function statusClass(status: string) {
  return `catalog-status catalog-status--${status.toLowerCase()}`;
}

export function DiagnosticCatalogScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId } = scope;
  const [area, setArea] = useState<CatalogArea>("services");
  const [rows, setRows] = useState<CatalogRow[]>([]);
  const [code, setCode] = useState("");
  const [nameEn, setNameEn] = useState("");
  const [nameEs, setNameEs] = useState("");
  const [selectedId, setSelectedId] = useState("");

  const loadAction = useAsyncAction(loadRows);
  const createAction = useAsyncAction(createRow);
  const publishAction = useAsyncAction(publishRow);

  const selectedArea = useMemo(() => AREAS.find((item) => item.key === area), [area]);
  const canUseCatalog = Boolean(tenantId && laboratoryId);

  async function loadRows() {
    if (!laboratoryId) {
      return [];
    }
    const loaded = await fetchRows(area, laboratoryId);
    setRows(loaded);
    return loaded;
  }

  async function createRow() {
    if (!tenantId || !laboratoryId) {
      throw new Error("Select tenant and laboratory scope before using the catalog.");
    }
    const created = await createCatalogRow(area, tenantId, laboratoryId, code, nameEn, nameEs);
    setRows((current) => upsertById(current, created));
    setSelectedId(created.id);
    setCode("");
    setNameEn("");
    setNameEs("");
    return created;
  }

  async function publishRow() {
    if (!selectedId) {
      throw new Error("Select an item to publish.");
    }
    const selectedRow = rows.find((row) => row.id === selectedId);
    const published = await publishCatalogRow(area, selectedId, selectedRow?.publishTarget);
    setRows((current) => upsertById(current, published));
    return published;
  }

  async function handleLoad() {
    await loadAction.run();
  }

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await createAction.run();
  }

  async function handlePublish(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await publishAction.run();
  }

  return (
    <section aria-labelledby="diagnostic-catalog-heading">
      <h2 id="diagnostic-catalog-heading">Diagnostic Catalog</h2>
      <ScopeIndicator />

      <div className="catalog-toolbar" aria-label="Catalog areas">
        {AREAS.map((item) => (
          <button
            key={item.key}
            type="button"
            className={
              item.key === area
                ? "catalog-toolbar__button catalog-toolbar__button--active"
                : "catalog-toolbar__button"
            }
            aria-pressed={item.key === area}
            onClick={() => {
              setArea(item.key);
              setRows([]);
              setSelectedId("");
            }}
          >
            {item.label}
          </button>
        ))}
      </div>

      {!canUseCatalog ? (
        <p className="status-banner status-banner--error">
          Select a tenant and laboratory before managing diagnostic catalog records.
        </p>
      ) : null}

      <form onSubmit={handleCreate}>
        <h3>Create {selectedArea?.label}</h3>
        <label htmlFor="catalog-code">Code</label>
        <input
          id="catalog-code"
          value={code}
          onChange={(event) => setCode(event.target.value)}
          required
        />
        <label htmlFor="catalog-name-en">Name EN</label>
        <input
          id="catalog-name-en"
          value={nameEn}
          onChange={(event) => setNameEn(event.target.value)}
          required
        />
        <label htmlFor="catalog-name-es">Name ES</label>
        <input
          id="catalog-name-es"
          value={nameEs}
          onChange={(event) => setNameEs(event.target.value)}
          required
        />
        <button type="submit" disabled={!canUseCatalog || createAction.status === "loading"}>
          Create catalog item
        </button>
        <StatusBanner
          status={createAction.status}
          errorMessage={createAction.errorMessage}
          successMessage="Catalog item created."
        />
      </form>

      <form onSubmit={handlePublish}>
        <h3>Publish {selectedArea?.label}</h3>
        <label htmlFor="catalog-publish-id">Catalog item id</label>
        <input
          id="catalog-publish-id"
          value={selectedId}
          onChange={(event) => setSelectedId(event.target.value)}
          required
        />
        <button type="submit" disabled={!canUseCatalog || publishAction.status === "loading"}>
          Publish selected item
        </button>
        <StatusBanner
          status={publishAction.status}
          errorMessage={publishAction.errorMessage}
          successMessage="Catalog item published."
        />
      </form>

      <button
        type="button"
        disabled={!canUseCatalog || loadAction.status === "loading"}
        onClick={handleLoad}
      >
        Load {selectedArea?.label}
      </button>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage="Catalog loaded."
      />

      <table>
        <caption>{selectedArea?.label} in this laboratory</caption>
        <thead>
          <tr>
            <th scope="col">Id</th>
            <th scope="col">Code</th>
            <th scope="col">Name</th>
            <th scope="col">Detail</th>
            <th scope="col">Status</th>
            <th scope="col">Version</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr key={row.id}>
              <td>
                <button type="button" className="link-button" onClick={() => setSelectedId(row.id)}>
                  {row.id}
                </button>
              </td>
              <td>{row.code}</td>
              <td>{row.name}</td>
              <td>{row.detail}</td>
              <td>
                <span className={statusClass(row.status)}>{row.status}</span>
              </td>
              <td>{row.version}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

async function fetchRows(area: CatalogArea, laboratoryId: string): Promise<CatalogRow[]> {
  switch (area) {
    case "services":
      return (await listDiagnosticServices(laboratoryId)).map(fromService);
    case "tests":
      return (await listTests(laboratoryId)).map(fromTest);
    case "panels":
      return (await listPanels(laboratoryId)).map(fromPanel);
    case "analytes":
      return (await listAnalytes(laboratoryId)).map(fromAnalyte);
    case "preparations":
      return (await listPreparations(laboratoryId)).map(fromPreparation);
    case "reference-ranges":
      return (await listReferenceRanges(laboratoryId)).map(fromReferenceRange);
    case "samples":
      return [
        ...(await listSampleTypes(laboratoryId)).map(fromSampleType),
        ...(await listSampleRequirements(laboratoryId)).map(fromSampleRequirement),
      ];
    case "price-lists":
      return (await listPriceLists(laboratoryId)).map(fromPriceList);
  }
}

async function createCatalogRow(
  area: CatalogArea,
  tenantId: string,
  laboratoryId: string,
  code: string,
  nameEn: string,
  nameEs: string,
): Promise<CatalogRow> {
  switch (area) {
    case "services":
      return fromService(
        await createDiagnosticService({
          tenantId,
          laboratoryId,
          code,
          nameEn,
          nameEs,
          serviceType: "laboratory",
          components: [{ componentType: "test", componentRefId: `${code}-TEST`, displayOrder: 1 }],
        }),
      );
    case "tests":
      return fromTest(
        await createTest({
          tenantId,
          laboratoryId,
          code,
          nameEn,
          nameEs,
          resultType: "numeric",
          measurementUnit: "mg/dL",
          analyteRefIds: [`${code}-ANALYTE`],
          sampleRequirementRefIds: [`${code}-SAMPLE-REQ`],
        }),
      );
    case "panels":
      return fromPanel(
        await createPanel({
          tenantId,
          laboratoryId,
          code,
          nameEn,
          nameEs,
          members: [
            { testRefId: `${code}-TEST-1`, displayOrder: 1, mandatory: true },
            { testRefId: `${code}-TEST-2`, displayOrder: 2, mandatory: false },
          ],
        }),
      );
    case "analytes":
      return fromAnalyte(
        await createAnalyte({
          tenantId,
          laboratoryId,
          code,
          nameEn,
          nameEs,
          resultDataType: "numeric",
          measurementUnit: "mg/dL",
          decimalPrecision: 2,
        }),
      );
    case "preparations":
      return fromPreparation(
        await createPreparation({
          tenantId,
          laboratoryId,
          code,
          titleEn: nameEn,
          titleEs: nameEs,
          instructionTextEn: "Follow laboratory preparation instructions.",
          instructionTextEs: "Seguir instrucciones de preparacion del laboratorio.",
          category: "general",
          durationHours: 1,
        }),
      );
    case "reference-ranges":
      return fromReferenceRange(
        await createReferenceRange({
          tenantId,
          laboratoryId,
          analyteRefId: code,
          effectiveFrom: new Date().toISOString().slice(0, 10),
          segments: [
            {
              sex: "any",
              normalLow: 0,
              normalHigh: 100,
              criticalLow: 0,
              criticalHigh: 150,
              unit: "mg/dL",
            },
          ],
        }),
      );
    case "samples":
      return fromSampleType(
        await createSampleType({ tenantId, laboratoryId, code, nameEn, nameEs, matrix: "blood" }),
      );
    case "price-lists":
      return fromPriceList(
        await createPriceList({
          tenantId,
          laboratoryId,
          code,
          nameEn,
          nameEs,
          currency: "MXN",
          effectiveFrom: new Date().toISOString().slice(0, 10),
        }),
      );
  }
}

async function publishCatalogRow(
  area: CatalogArea,
  id: string,
  publishTarget?: CatalogRow["publishTarget"],
): Promise<CatalogRow> {
  switch (area) {
    case "services":
      return fromService(await publishDiagnosticService(id));
    case "tests":
      return fromTest(await publishTest(id));
    case "panels":
      return fromPanel(await publishPanel(id));
    case "analytes":
      return fromAnalyte(await publishAnalyte(id));
    case "preparations":
      return fromPreparation(await publishPreparation(id));
    case "reference-ranges":
      return fromReferenceRange(await publishReferenceRange(id));
    case "samples":
      return publishTarget === "sample-requirement"
        ? fromSampleRequirement(await publishSampleRequirement(id))
        : fromSampleType(await publishSampleType(id));
    case "price-lists":
      return fromPriceList(await publishPriceList(id));
  }
}

function fromService(item: DiagnosticService): CatalogRow {
  return {
    id: item.serviceId,
    code: item.code,
    name: item.nameEs,
    status: item.status,
    version: item.version,
    detail: item.serviceType,
  };
}

function fromTest(item: TestDefinition): CatalogRow {
  return {
    id: item.testDefinitionId,
    code: item.code,
    name: item.nameEs,
    status: item.status,
    version: item.version,
    detail: item.resultType,
  };
}

function fromPanel(item: PanelDefinition): CatalogRow {
  return {
    id: item.panelId,
    code: item.code,
    name: item.nameEs,
    status: item.status,
    version: item.version,
    detail: `${item.members?.length ?? 0} members`,
  };
}

function fromAnalyte(item: AnalyteDefinition): CatalogRow {
  return {
    id: item.analyteId,
    code: item.code,
    name: item.nameEs,
    status: item.status,
    version: item.version,
    detail: item.resultDataType,
  };
}

function fromPreparation(item: PreparationInstruction): CatalogRow {
  return {
    id: item.preparationId,
    code: item.code,
    name: item.titleEs,
    status: item.status,
    version: item.version,
    detail: item.category,
  };
}

function fromReferenceRange(item: ReferenceRange): CatalogRow {
  const effectiveTo = item.effectiveTo ? ` to ${item.effectiveTo}` : "";
  return {
    id: item.rangeId,
    code: item.analyteRefId,
    name: item.analyteRefId,
    status: item.status,
    version: item.version,
    detail: `${item.effectiveFrom}${effectiveTo}`,
  };
}

function fromSampleType(item: SampleType): CatalogRow {
  return {
    id: item.sampleTypeId,
    code: item.code,
    name: item.nameEs,
    status: item.status,
    version: item.version,
    detail: item.matrix,
    publishTarget: "sample-type",
  };
}

function fromSampleRequirement(item: SampleRequirement): CatalogRow {
  return {
    id: item.requirementId,
    code: item.sampleTypeRefId,
    name: item.sampleTypeRefId,
    status: item.status,
    version: item.version,
    detail: `${item.minVolumeMl ?? 0} mL`,
    publishTarget: "sample-requirement",
  };
}

function fromPriceList(item: PriceList): CatalogRow {
  return {
    id: item.priceListId,
    code: item.code,
    name: item.nameEs,
    status: item.status,
    version: item.version,
    detail: `${item.currency} ${item.effectiveFrom}`,
  };
}
