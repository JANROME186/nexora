/**
 * CAPA Management administration screen (COM-MOD-013-FE-001, BCM-QLT-006).
 *
 * Manages the Corrective and Preventive Action lifecycle: open, assign, close and verify,
 * backed by CapaManagementController. Confirm dialogs before close and verify (sensitive actions).
 * Decomposed into small sub-components (TD-FE-010 pattern).
 */
import { useState } from "react";
import {
  assignCapa,
  closeCapa,
  listCapaRecords,
  openCapa,
  verifyCapa,
} from "../../api/externalQualityComplianceApi";
import type { CapaRecord } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { ConfirmDialog } from "../common/ConfirmDialog";

type Labels = MessageCatalog["advancedQualityCompliance"]["capaManagement"];
type SharedLabels = MessageCatalog["advancedQualityCompliance"]["shared"];

interface OpenCapaFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: {
    sourceEventType: string;
    sourceEventId: string;
    description: string;
  }) => void;
}

function OpenCapaForm({ labels, disabled, status, errorMessage, onSubmit }: OpenCapaFormProps) {
  const [sourceEventType, setSourceEventType] = useState("");
  const [sourceEventId, setSourceEventId] = useState("");
  const [description, setDescription] = useState("");

  return (
    <div className="panel">
      <label htmlFor="capa-source-type">{labels.sourceEventType}</label>
      <input
        id="capa-source-type"
        value={sourceEventType}
        onChange={(e) => setSourceEventType(e.target.value)}
      />
      <label htmlFor="capa-source-id">{labels.sourceEventId}</label>
      <input
        id="capa-source-id"
        value={sourceEventId}
        onChange={(e) => setSourceEventId(e.target.value)}
      />
      <label htmlFor="capa-description">{labels.description2}</label>
      <input
        id="capa-description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <button
        type="button"
        id="capa-open-btn"
        disabled={disabled}
        onClick={() => onSubmit({ sourceEventType, sourceEventId, description })}
      >
        {labels.open}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.openSuccess}
      />
    </div>
  );
}

interface AssignFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: { assignedTo: string; dueDate: string; rootCauseAnalysis: string }) => void;
}

function AssignCapaForm({ labels, disabled, status, errorMessage, onSubmit }: AssignFormProps) {
  const [assignedTo, setAssignedTo] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [rootCauseAnalysis, setRootCauseAnalysis] = useState("");

  return (
    <div className="panel">
      <label htmlFor="capa-assigned-to">{labels.assignedTo}</label>
      <input
        id="capa-assigned-to"
        value={assignedTo}
        onChange={(e) => setAssignedTo(e.target.value)}
      />
      <label htmlFor="capa-due-date">{labels.dueDate}</label>
      <input
        id="capa-due-date"
        type="date"
        value={dueDate}
        onChange={(e) => setDueDate(e.target.value)}
      />
      <label htmlFor="capa-root-cause">{labels.rootCauseAnalysis}</label>
      <input
        id="capa-root-cause"
        value={rootCauseAnalysis}
        onChange={(e) => setRootCauseAnalysis(e.target.value)}
      />
      <button
        type="button"
        id="capa-assign-btn"
        disabled={disabled}
        onClick={() => onSubmit({ assignedTo, dueDate, rootCauseAnalysis })}
      >
        {labels.assign}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.assignSuccess}
      />
    </div>
  );
}

interface CloseCapaFormProps {
  labels: Labels;
  disabled: boolean;
  onRequest: (fields: {
    closedBy: string;
    correctiveAction: string;
    preventiveAction: string;
  }) => void;
}

function CloseCapaForm({ labels, disabled, onRequest }: CloseCapaFormProps) {
  const [closedBy, setClosedBy] = useState("");
  const [correctiveAction, setCorrectiveAction] = useState("");
  const [preventiveAction, setPreventiveAction] = useState("");

  return (
    <div className="panel">
      <label htmlFor="capa-closed-by">{labels.closedBy}</label>
      <input id="capa-closed-by" value={closedBy} onChange={(e) => setClosedBy(e.target.value)} />
      <label htmlFor="capa-corrective-action">{labels.correctiveAction}</label>
      <input
        id="capa-corrective-action"
        value={correctiveAction}
        onChange={(e) => setCorrectiveAction(e.target.value)}
      />
      <label htmlFor="capa-preventive-action">{labels.preventiveAction}</label>
      <input
        id="capa-preventive-action"
        value={preventiveAction}
        onChange={(e) => setPreventiveAction(e.target.value)}
      />
      <button
        type="button"
        id="capa-close-btn"
        disabled={disabled}
        onClick={() => onRequest({ closedBy, correctiveAction, preventiveAction })}
      >
        {labels.close}
      </button>
    </div>
  );
}

function capaColumns(labels: Labels): DataTableColumn<CapaRecord>[] {
  return [
    { key: "sourceEventType", header: labels.sourceEventType, render: (r) => r.sourceEventType },
    { key: "description", header: labels.description2, render: (r) => r.description },
    { key: "assignedTo", header: labels.assignedTo, render: (r) => r.assignedTo ?? "-" },
    { key: "status", header: "Estado", render: (r) => r.status },
    { key: "dueDate", header: labels.dueDate, render: (r) => r.dueDate ?? "-" },
  ];
}

export function CapaManagementScreen() {
  const { t } = useLocale();
  const shared: SharedLabels = t.advancedQualityCompliance.shared;
  const labels = t.advancedQualityCompliance.capaManagement;
  const { scope } = useAdminScope();

  const [capas, setCapas] = useState<CapaRecord[]>([]);
  const [selected, setSelected] = useState<CapaRecord | undefined>();
  const [confirmClose, setConfirmClose] = useState(false);
  const [confirmVerify, setConfirmVerify] = useState(false);
  const [pendingClose, setPendingClose] = useState<{
    closedBy: string;
    correctiveAction: string;
    preventiveAction: string;
  } | null>(null);
  const [verifyBy, setVerifyBy] = useState("");

  const loadAction = useAsyncAction(listCapaRecords);
  const openAction = useAsyncAction(openCapa);
  const assignAction = useAsyncAction(assignCapa);
  const closeAction = useAsyncAction(closeCapa);
  const verifyAction = useAsyncAction(verifyCapa);

  const tenantId = scope.tenantId ?? "";
  const laboratoryId = scope.laboratoryId ?? "";

  async function handleLoad() {
    const result = await loadAction.run(tenantId, laboratoryId);
    if (result.ok) setCapas(result.data);
  }

  async function handleOpen(fields: {
    sourceEventType: string;
    sourceEventId: string;
    description: string;
  }) {
    const result = await openAction.run(tenantId, laboratoryId, {
      sourceEventType: fields.sourceEventType,
      sourceEventId: fields.sourceEventId || undefined,
      description: fields.description,
    });
    if (result.ok) setCapas((prev) => [...prev, result.data]);
  }

  async function handleAssign(fields: {
    assignedTo: string;
    dueDate: string;
    rootCauseAnalysis: string;
  }) {
    if (!selected) return;
    const result = await assignAction.run(selected.capaId, {
      assignedTo: fields.assignedTo,
      dueDate: fields.dueDate || undefined,
      rootCauseAnalysis: fields.rootCauseAnalysis || undefined,
    });
    if (result.ok)
      setCapas((prev) => prev.map((c) => (c.capaId === result.data.capaId ? result.data : c)));
  }

  function requestClose(fields: {
    closedBy: string;
    correctiveAction: string;
    preventiveAction: string;
  }) {
    setPendingClose(fields);
    setConfirmClose(true);
  }

  async function confirmCloseAction() {
    if (!selected || !pendingClose) return;
    setConfirmClose(false);
    const result = await closeAction.run(selected.capaId, {
      closedBy: pendingClose.closedBy,
      correctiveAction: pendingClose.correctiveAction,
      preventiveAction: pendingClose.preventiveAction || undefined,
    });
    if (result.ok)
      setCapas((prev) => prev.map((c) => (c.capaId === result.data.capaId ? result.data : c)));
  }

  async function confirmVerifyAction() {
    if (!selected) return;
    setConfirmVerify(false);
    const result = await verifyAction.run(selected.capaId, { verifiedBy: verifyBy });
    if (result.ok)
      setCapas((prev) => prev.map((c) => (c.capaId === result.data.capaId ? result.data : c)));
  }

  return (
    <section aria-labelledby="capa-heading">
      <h2 id="capa-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>

      <button
        type="button"
        id="capa-load-btn"
        disabled={loadAction.status === "loading"}
        onClick={handleLoad}
      >
        {labels.loadCapas}
      </button>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage={shared.loaded}
      />
      {loadAction.status === "success" && capas.length === 0 ? (
        <p className="empty-state">{shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={capaColumns(labels)}
        rows={capas}
        rowKey={(r) => r.capaId}
        onSelectRow={setSelected}
      />

      <OpenCapaForm
        labels={labels}
        disabled={openAction.status === "loading" || !tenantId || !laboratoryId}
        status={openAction.status}
        errorMessage={openAction.errorMessage}
        onSubmit={handleOpen}
      />

      {selected ? (
        <>
          <AssignCapaForm
            labels={labels}
            disabled={assignAction.status === "loading"}
            status={assignAction.status}
            errorMessage={assignAction.errorMessage}
            onSubmit={handleAssign}
          />

          <CloseCapaForm
            labels={labels}
            disabled={closeAction.status === "loading"}
            onRequest={requestClose}
          />
          <StatusBanner
            status={closeAction.status}
            errorMessage={closeAction.errorMessage}
            successMessage={labels.closeSuccess}
          />

          <div className="panel">
            <label htmlFor="capa-verify-by">{labels.verifiedBy}</label>
            <input
              id="capa-verify-by"
              value={verifyBy}
              onChange={(e) => setVerifyBy(e.target.value)}
            />
            <button
              type="button"
              id="capa-verify-btn"
              disabled={verifyAction.status === "loading"}
              onClick={() => setConfirmVerify(true)}
            >
              {labels.verify}
            </button>
            <StatusBanner
              status={verifyAction.status}
              errorMessage={verifyAction.errorMessage}
              successMessage={labels.verifySuccess}
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
      <ConfirmDialog
        open={confirmVerify}
        title={labels.verifyDialog.title}
        description={labels.verifyDialog.description}
        confirmLabel={shared.dialogConfirm}
        cancelLabel={shared.dialogCancel}
        onConfirm={confirmVerifyAction}
        onCancel={() => setConfirmVerify(false)}
      />
    </section>
  );
}
