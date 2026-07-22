/**
 * Public Content Review screen (COM-MOD-011-FE-001, BCM-SVC-001/002/003/005).
 *
 * Staff-facing, read-only view of exactly what the public website currently shows: it consumes
 * the same anonymous `/api/public/catalog/**` published-snapshot endpoints the public website
 * itself calls, rather than the internal catalog-admin API (already owned by
 * DiagnosticCatalogScreen). This keeps the two screens distinct — full draft/publish lifecycle
 * management stays in DiagnosticCatalogScreen — and guarantees no tenantId, audit field or other
 * internal identifier can leak into this view, since the public snapshot DTOs never carry them.
 */
import { useMemo, useState } from "react";
import {
  listPublishedDiagnosticServices,
  listPublishedPanels,
  listPublishedPreparations,
  listPublishedTests,
} from "../../api/publicContentApi";
import type {
  PublicDiagnosticServiceSnapshot,
  PublicPanelSnapshot,
  PublicPreparationSnapshot,
  PublicTestSnapshot,
} from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

type Labels = MessageCatalog["publicContentReview"];
type ContentArea = "services" | "tests" | "panels" | "preparations";

interface ContentRow {
  id: string;
  code: string;
  nameEn: string;
  nameEs: string;
  detail: string;
  version: number;
}

const AREAS: ContentArea[] = ["services", "tests", "panels", "preparations"];

function toServiceRow(item: PublicDiagnosticServiceSnapshot): ContentRow {
  return {
    id: item.serviceId,
    code: item.code,
    nameEn: item.nameEn,
    nameEs: item.nameEs,
    detail: item.serviceType,
    version: item.version,
  };
}

function toTestRow(item: PublicTestSnapshot): ContentRow {
  return {
    id: item.testDefinitionId,
    code: item.code,
    nameEn: item.nameEn,
    nameEs: item.nameEs,
    detail: item.resultType,
    version: item.version,
  };
}

function toPanelRow(item: PublicPanelSnapshot): ContentRow {
  return {
    id: item.panelId,
    code: item.code,
    nameEn: item.nameEn,
    nameEs: item.nameEs,
    detail: "",
    version: item.version,
  };
}

function toPreparationRow(item: PublicPreparationSnapshot): ContentRow {
  return {
    id: item.preparationId,
    code: item.code,
    nameEn: item.titleEn,
    nameEs: item.titleEs,
    detail: item.category,
    version: item.version,
  };
}

async function fetchRows(area: ContentArea, laboratoryId: string): Promise<ContentRow[]> {
  if (area === "services") {
    return (await listPublishedDiagnosticServices(laboratoryId)).map(toServiceRow);
  }
  if (area === "tests") {
    return (await listPublishedTests(laboratoryId)).map(toTestRow);
  }
  if (area === "panels") {
    return (await listPublishedPanels(laboratoryId)).map(toPanelRow);
  }
  return (await listPublishedPreparations(laboratoryId)).map(toPreparationRow);
}

function detailColumnHeader(area: ContentArea, labels: Labels): string | undefined {
  if (area === "services") return labels.columns.serviceType;
  if (area === "tests") return labels.columns.resultType;
  if (area === "preparations") return labels.columns.category;
  return undefined;
}

function columnsFor(area: ContentArea, labels: Labels): DataTableColumn<ContentRow>[] {
  const columns: DataTableColumn<ContentRow>[] = [
    { key: "code", header: labels.columns.code, render: (row) => row.code },
    { key: "nameEn", header: labels.columns.nameEn, render: (row) => row.nameEn },
    { key: "nameEs", header: labels.columns.nameEs, render: (row) => row.nameEs },
  ];
  const detailHeader = detailColumnHeader(area, labels);
  if (detailHeader) {
    columns.push({ key: "detail", header: detailHeader, render: (row) => row.detail });
  }
  columns.push({
    key: "version",
    header: labels.columns.version,
    render: (row) => String(row.version),
  });
  return columns;
}

export function PublicContentReviewScreen() {
  const { t } = useLocale();
  const labels = t.publicContentReview;
  const { scope } = useAdminScope();
  const { laboratoryId } = scope;

  const [area, setArea] = useState<ContentArea>("services");
  const [rows, setRows] = useState<ContentRow[]>([]);

  const loadAction = useAsyncAction(async () => {
    if (!laboratoryId) {
      throw new Error(labels.shared.laboratoryRequired);
    }
    const loaded = await fetchRows(area, laboratoryId);
    setRows(loaded);
    return loaded;
  });

  function selectArea(nextArea: ContentArea) {
    setArea(nextArea);
    setRows([]);
    loadAction.reset();
  }

  const columns = useMemo(() => columnsFor(area, labels), [area, labels]);

  return (
    <section aria-labelledby="public-content-review-heading">
      <h2 id="public-content-review-heading">{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />

      <div className="catalog-toolbar" aria-label={labels.heading}>
        {AREAS.map((candidate) => (
          <button
            key={candidate}
            type="button"
            className={
              candidate === area
                ? "catalog-toolbar__button catalog-toolbar__button--active"
                : "catalog-toolbar__button"
            }
            aria-pressed={candidate === area}
            onClick={() => selectArea(candidate)}
          >
            {labels.areas[candidate]}
          </button>
        ))}
      </div>

      {!laboratoryId ? (
        <p className="status-banner status-banner--error">{labels.shared.laboratoryRequired}</p>
      ) : null}

      <button
        type="button"
        disabled={!laboratoryId || loadAction.status === "loading"}
        onClick={() => loadAction.run()}
      >
        {labels.shared.load}
      </button>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {loadAction.status === "success" && rows.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.areas[area]}
        columns={columns}
        rows={rows}
        rowKey={(row) => row.id}
      />
    </section>
  );
}
