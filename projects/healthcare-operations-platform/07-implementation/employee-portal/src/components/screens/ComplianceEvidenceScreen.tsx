/**
 * Compliance Evidence administration screen (COM-MOD-013-FE-001, BCM-PLT-007 / BCM-PLT-008).
 *
 * Searches compliance audit evidence records, exports evidence bundles and lists compliance
 * documents with retention information. Confirm dialog before export (sensitive action).
 * Backed by AuditComplianceController and DocumentManagementController.
 */
import { useState } from "react";
import {
  exportComplianceEvidence,
  searchComplianceEvidence,
  searchDocuments,
} from "../../api/externalQualityComplianceApi";
import type { ComplianceEvidenceExport, StoredDocument } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { ConfirmDialog } from "../common/ConfirmDialog";

type Labels = MessageCatalog["advancedQualityCompliance"]["complianceEvidence"];
type SharedLabels = MessageCatalog["advancedQualityCompliance"]["shared"];

interface SearchFormProps {
  labels: Labels;
  disabled: boolean;
  onSubmit: (fields: {
    tenantId: string;
    subjectType: string;
    subjectId: string;
    fromDate: string;
    toDate: string;
  }) => void;
}

function SearchEvidenceForm({ labels, disabled, onSubmit }: SearchFormProps) {
  const [tenantId, setTenantId] = useState("");
  const [subjectType, setSubjectType] = useState("");
  const [subjectId, setSubjectId] = useState("");
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");

  return (
    <div className="panel">
      <label htmlFor="ce-tenant-id">{labels.tenantId}</label>
      <input id="ce-tenant-id" value={tenantId} onChange={(e) => setTenantId(e.target.value)} />
      <label htmlFor="ce-subject-type">{labels.subjectType}</label>
      <input
        id="ce-subject-type"
        value={subjectType}
        onChange={(e) => setSubjectType(e.target.value)}
      />
      <label htmlFor="ce-subject-id">{labels.subjectId}</label>
      <input id="ce-subject-id" value={subjectId} onChange={(e) => setSubjectId(e.target.value)} />
      <label htmlFor="ce-from-date">{labels.fromDate}</label>
      <input
        id="ce-from-date"
        type="date"
        value={fromDate}
        onChange={(e) => setFromDate(e.target.value)}
      />
      <label htmlFor="ce-to-date">{labels.toDate}</label>
      <input
        id="ce-to-date"
        type="date"
        value={toDate}
        onChange={(e) => setToDate(e.target.value)}
      />
      <button
        type="button"
        id="ce-search-btn"
        disabled={disabled}
        onClick={() => onSubmit({ tenantId, subjectType, subjectId, fromDate, toDate })}
      >
        {labels.search}
      </button>
    </div>
  );
}

interface ExportFormProps {
  labels: Labels;
  disabled: boolean;
  onRequest: (fields: {
    requestedBy: string;
    fromDate: string;
    toDate: string;
    subjectType: string;
  }) => void;
}

function ExportEvidenceForm({ labels, disabled, onRequest }: ExportFormProps) {
  const [requestedBy, setRequestedBy] = useState("");
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [subjectType, setSubjectType] = useState("");

  return (
    <div className="panel">
      <label htmlFor="ce-requested-by">{labels.requestedBy}</label>
      <input
        id="ce-requested-by"
        value={requestedBy}
        onChange={(e) => setRequestedBy(e.target.value)}
      />
      <label htmlFor="ce-export-from">{labels.fromDate}</label>
      <input
        id="ce-export-from"
        type="date"
        value={fromDate}
        onChange={(e) => setFromDate(e.target.value)}
      />
      <label htmlFor="ce-export-to">{labels.toDate}</label>
      <input
        id="ce-export-to"
        type="date"
        value={toDate}
        onChange={(e) => setToDate(e.target.value)}
      />
      <label htmlFor="ce-export-subject-type">{labels.subjectType}</label>
      <input
        id="ce-export-subject-type"
        value={subjectType}
        onChange={(e) => setSubjectType(e.target.value)}
      />
      <button
        type="button"
        id="ce-export-btn"
        disabled={disabled}
        onClick={() => onRequest({ requestedBy, fromDate, toDate, subjectType })}
      >
        {labels.export}
      </button>
    </div>
  );
}

function evidenceColumns(labels: Labels): DataTableColumn<ComplianceEvidenceExport>[] {
  return [
    { key: "exportId", header: labels.exportId, render: (r) => r.exportId },
    { key: "requestedBy", header: labels.requestedBy, render: (r) => r.requestedBy },
    { key: "recordCount", header: labels.recordCount, render: (r) => String(r.recordCount) },
    { key: "exportedAt", header: labels.exportedAt, render: (r) => r.exportedAt },
    { key: "status", header: "Estado", render: (r) => r.status },
  ];
}

function docColumns(labels: Labels): DataTableColumn<StoredDocument>[] {
  return [
    { key: "fileName", header: labels.fileName, render: (r) => r.fileName },
    { key: "storedAt", header: labels.storedAt, render: (r) => r.storedAt },
    {
      key: "retentionUntil",
      header: labels.retentionUntil,
      render: (r) => r.retentionUntil ?? "-",
    },
  ];
}

export function ComplianceEvidenceScreen() {
  const { t } = useLocale();
  const shared: SharedLabels = t.advancedQualityCompliance.shared;
  const labels = t.advancedQualityCompliance.complianceEvidence;
  const { scope } = useAdminScope();

  const [evidenceRecords, setEvidenceRecords] = useState<ComplianceEvidenceExport[]>([]);
  const [documents, setDocuments] = useState<StoredDocument[]>([]);
  const [confirmExport, setConfirmExport] = useState(false);
  const [pendingExport, setPendingExport] = useState<{
    requestedBy: string;
    fromDate: string;
    toDate: string;
    subjectType: string;
  } | null>(null);

  const searchAction = useAsyncAction(searchComplianceEvidence);
  const exportAction = useAsyncAction(exportComplianceEvidence);
  const docsAction = useAsyncAction(searchDocuments);

  const tenantId = scope.tenantId ?? "";

  async function handleSearch(fields: {
    tenantId: string;
    subjectType: string;
    subjectId: string;
    fromDate: string;
    toDate: string;
  }) {
    const result = await searchAction.run({
      tenantId: fields.tenantId || tenantId || undefined,
      subjectType: fields.subjectType || undefined,
      subjectId: fields.subjectId || undefined,
      fromDate: fields.fromDate || undefined,
      toDate: fields.toDate || undefined,
    });
    if (result.ok) setEvidenceRecords(result.data);
  }

  function requestExport(fields: {
    requestedBy: string;
    fromDate: string;
    toDate: string;
    subjectType: string;
  }) {
    setPendingExport(fields);
    setConfirmExport(true);
  }

  async function confirmExportAction() {
    if (!pendingExport) return;
    setConfirmExport(false);
    const result = await exportAction.run({
      requestedBy: pendingExport.requestedBy,
      fromDate: pendingExport.fromDate || undefined,
      toDate: pendingExport.toDate || undefined,
      subjectType: pendingExport.subjectType || undefined,
    });
    if (result.ok) setEvidenceRecords((prev) => [...prev, result.data]);
  }

  async function handleLoadDocuments() {
    const result = await docsAction.run({ tenantId: tenantId || undefined });
    if (result.ok) setDocuments(result.data);
  }

  return (
    <section aria-labelledby="ce-heading">
      <h2 id="ce-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>

      <h3>{labels.searchHeading}</h3>
      <SearchEvidenceForm
        labels={labels}
        disabled={searchAction.status === "loading"}
        onSubmit={handleSearch}
      />
      <StatusBanner
        status={searchAction.status}
        errorMessage={searchAction.errorMessage}
        successMessage={labels.searchSuccess}
      />
      {searchAction.status === "success" && evidenceRecords.length === 0 ? (
        <p className="empty-state">{shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={evidenceColumns(labels)}
        rows={evidenceRecords}
        rowKey={(r) => r.exportId}
      />

      <h3>{labels.exportHeading}</h3>
      <ExportEvidenceForm
        labels={labels}
        disabled={exportAction.status === "loading"}
        onRequest={requestExport}
      />
      <StatusBanner
        status={exportAction.status}
        errorMessage={exportAction.errorMessage}
        successMessage={labels.exportSuccess}
      />

      <h3>{labels.documentsHeading}</h3>
      <button
        type="button"
        id="ce-load-docs-btn"
        disabled={docsAction.status === "loading"}
        onClick={handleLoadDocuments}
      >
        {labels.loadDocuments}
      </button>
      <StatusBanner
        status={docsAction.status}
        errorMessage={docsAction.errorMessage}
        successMessage={shared.loaded}
      />
      {docsAction.status === "success" && documents.length === 0 ? (
        <p className="empty-state">{shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.documentsHeading}
        columns={docColumns(labels)}
        rows={documents}
        rowKey={(r) => r.documentId}
      />

      <ConfirmDialog
        open={confirmExport}
        title={labels.exportDialog.title}
        description={labels.exportDialog.description}
        confirmLabel={shared.dialogConfirm}
        cancelLabel={shared.dialogCancel}
        onConfirm={confirmExportAction}
        onCancel={() => setConfirmExport(false)}
      />
    </section>
  );
}
