/**
 * Audit Management administration screen (COM-MOD-013-FE-001, BCM-QLT-007).
 *
 * Plans, opens, records findings and closes quality audits, backed by
 * QualityAuditController. Confirm dialog before close (sensitive action).
 * Decomposed into small sub-components (TD-FE-010 pattern).
 */
import { useState } from "react";
import {
  closeQualityAudit,
  listQualityAudits,
  openQualityAudit,
  planQualityAudit,
  recordAuditFinding,
} from "../../api/externalQualityComplianceApi";
import type { QualityAudit } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { ConfirmDialog } from "../common/ConfirmDialog";

type Labels = MessageCatalog["advancedQualityCompliance"]["auditManagement"];
type SharedLabels = MessageCatalog["advancedQualityCompliance"]["shared"];

interface PlanAuditFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: { auditType: string; auditorName: string; scheduledDate: string }) => void;
}

function PlanAuditForm({ labels, disabled, status, errorMessage, onSubmit }: PlanAuditFormProps) {
  const [auditType, setAuditType] = useState("");
  const [auditorName, setAuditorName] = useState("");
  const [scheduledDate, setScheduledDate] = useState("");

  return (
    <div className="panel">
      <label htmlFor="audit-type">{labels.auditType}</label>
      <input id="audit-type" value={auditType} onChange={(e) => setAuditType(e.target.value)} />
      <label htmlFor="audit-auditor-name">{labels.auditorName}</label>
      <input
        id="audit-auditor-name"
        value={auditorName}
        onChange={(e) => setAuditorName(e.target.value)}
      />
      <label htmlFor="audit-scheduled-date">{labels.scheduledDate}</label>
      <input
        id="audit-scheduled-date"
        type="date"
        value={scheduledDate}
        onChange={(e) => setScheduledDate(e.target.value)}
      />
      <button
        type="button"
        id="audit-plan-btn"
        disabled={disabled}
        onClick={() => onSubmit({ auditType, auditorName, scheduledDate })}
      >
        {labels.plan}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.planSuccess}
      />
    </div>
  );
}

interface RecordFindingFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: { category: string; findingDescription: string; severity: string }) => void;
}

function RecordFindingForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: RecordFindingFormProps) {
  const [category, setCategory] = useState("");
  const [findingDescription, setFindingDescription] = useState("");
  const [severity, setSeverity] = useState("");

  return (
    <div className="panel">
      <label htmlFor="finding-category">{labels.category}</label>
      <input id="finding-category" value={category} onChange={(e) => setCategory(e.target.value)} />
      <label htmlFor="finding-description">{labels.findingDescription}</label>
      <input
        id="finding-description"
        value={findingDescription}
        onChange={(e) => setFindingDescription(e.target.value)}
      />
      <label htmlFor="finding-severity">{labels.severity}</label>
      <input id="finding-severity" value={severity} onChange={(e) => setSeverity(e.target.value)} />
      <button
        type="button"
        id="audit-record-finding-btn"
        disabled={disabled}
        onClick={() => onSubmit({ category, findingDescription, severity })}
      >
        {labels.recordFinding}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.findingSuccess}
      />
    </div>
  );
}

function auditColumns(labels: Labels): DataTableColumn<QualityAudit>[] {
  return [
    { key: "auditorName", header: labels.auditorName, render: (r) => r.auditorName },
    { key: "auditType", header: labels.auditType, render: (r) => r.auditType },
    { key: "scheduledDate", header: labels.scheduledDate, render: (r) => r.scheduledDate ?? "-" },
    { key: "status", header: "Estado", render: (r) => r.status },
  ];
}

export function AuditManagementScreen() {
  const { t } = useLocale();
  const shared: SharedLabels = t.advancedQualityCompliance.shared;
  const labels = t.advancedQualityCompliance.auditManagement;
  const { scope } = useAdminScope();

  const [audits, setAudits] = useState<QualityAudit[]>([]);
  const [selected, setSelected] = useState<QualityAudit | undefined>();
  const [confirmClose, setConfirmClose] = useState(false);
  const [closedBy, setClosedBy] = useState("");

  const loadAction = useAsyncAction(listQualityAudits);
  const planAction = useAsyncAction(planQualityAudit);
  const openAction = useAsyncAction(openQualityAudit);
  const findingAction = useAsyncAction(recordAuditFinding);
  const closeAction = useAsyncAction(closeQualityAudit);

  const tenantId = scope.tenantId ?? "";
  const laboratoryId = scope.laboratoryId ?? "";

  async function handleLoad() {
    const result = await loadAction.run(tenantId, laboratoryId);
    if (result.ok) setAudits(result.data);
  }

  async function handlePlan(fields: {
    auditType: string;
    auditorName: string;
    scheduledDate: string;
  }) {
    const result = await planAction.run(tenantId, laboratoryId, {
      auditType: fields.auditType,
      auditorName: fields.auditorName,
      scheduledDate: fields.scheduledDate || undefined,
    });
    if (result.ok) setAudits((prev) => [...prev, result.data]);
  }

  async function handleOpen() {
    if (!selected) return;
    const result = await openAction.run(selected.auditId, {});
    if (result.ok)
      setAudits((prev) => prev.map((a) => (a.auditId === result.data.auditId ? result.data : a)));
  }

  async function handleFinding(fields: {
    category: string;
    findingDescription: string;
    severity: string;
  }) {
    if (!selected) return;
    const result = await findingAction.run(selected.auditId, {
      category: fields.category,
      description: fields.findingDescription,
      severity: fields.severity,
    });
    if (result.ok)
      setAudits((prev) => prev.map((a) => (a.auditId === result.data.auditId ? result.data : a)));
  }

  async function confirmCloseAction() {
    if (!selected) return;
    setConfirmClose(false);
    const result = await closeAction.run(selected.auditId, { closedBy });
    if (result.ok)
      setAudits((prev) => prev.map((a) => (a.auditId === result.data.auditId ? result.data : a)));
  }

  return (
    <section aria-labelledby="audit-mgmt-heading">
      <h2 id="audit-mgmt-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>

      <button
        type="button"
        id="audit-load-btn"
        disabled={loadAction.status === "loading"}
        onClick={handleLoad}
      >
        {labels.loadAudits}
      </button>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage={shared.loaded}
      />
      {loadAction.status === "success" && audits.length === 0 ? (
        <p className="empty-state">{shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={auditColumns(labels)}
        rows={audits}
        rowKey={(r) => r.auditId}
        onSelectRow={setSelected}
      />

      <PlanAuditForm
        labels={labels}
        disabled={planAction.status === "loading" || !tenantId || !laboratoryId}
        status={planAction.status}
        errorMessage={planAction.errorMessage}
        onSubmit={handlePlan}
      />

      {selected ? (
        <>
          <button
            type="button"
            id="audit-open-btn"
            disabled={openAction.status === "loading"}
            onClick={handleOpen}
          >
            {labels.open}
          </button>
          <StatusBanner
            status={openAction.status}
            errorMessage={openAction.errorMessage}
            successMessage={labels.openSuccess}
          />

          <RecordFindingForm
            labels={labels}
            disabled={findingAction.status === "loading"}
            status={findingAction.status}
            errorMessage={findingAction.errorMessage}
            onSubmit={handleFinding}
          />

          <div className="panel">
            <p>{labels.findings}</p>
            {selected.findings.length === 0 ? (
              <p>{labels.noFindings}</p>
            ) : (
              <ul>
                {selected.findings.map((f) => (
                  <li key={f.findingId}>
                    [{f.severity}] {f.category}: {f.description}
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div className="panel">
            <label htmlFor="audit-closed-by">{labels.closedBy}</label>
            <input
              id="audit-closed-by"
              value={closedBy}
              onChange={(e) => setClosedBy(e.target.value)}
            />
            <button
              type="button"
              id="audit-close-btn"
              disabled={closeAction.status === "loading"}
              onClick={() => setConfirmClose(true)}
            >
              {labels.close}
            </button>
            <StatusBanner
              status={closeAction.status}
              errorMessage={closeAction.errorMessage}
              successMessage={labels.closeSuccess}
            />
          </div>
        </>
      ) : null}

      <ConfirmDialog
        open={confirmClose}
        title={labels.closeDialog.title}
        description={labels.closeDialog.description}
        confirmLabel={shared.dialogConfirm}
        cancelLabel={shared.dialogCancel}
        onConfirm={confirmCloseAction}
        onCancel={() => setConfirmClose(false)}
      />
    </section>
  );
}
