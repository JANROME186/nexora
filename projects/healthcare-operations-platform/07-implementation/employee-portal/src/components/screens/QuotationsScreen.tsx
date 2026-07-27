import { useState, type FormEvent } from "react";
import { formatMoney } from "../../api/money";
import {
  acceptQuotation,
  cancelQuotation,
  convertQuotation,
  expireQuotation,
  issueQuotation,
  listQuotations,
  startQuotation,
} from "../../api/frontDeskApi";
import type { QuotationLineInput, QuotationRequest } from "../../api/types";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncActionState } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { statusClass } from "../common/statusPresentation";

const SELECT_QUOTATION_FIRST = "Select a quotation first.";

function newLine(): QuotationLineInput {
  return { testDefinitionId: "", catalogItemKind: "test", quantity: 1 };
}

const columns: DataTableColumn<QuotationRequest>[] = [
  { key: "quotationId", header: "Quotation id", render: (row) => row.quotationId },
  {
    key: "contact",
    header: "Patient / contact",
    render: (row) => row.patientId ?? row.prospectiveFullName ?? "—",
  },
  { key: "totalAmount", header: "Total", render: (row) => formatMoney(row.totalAmount) },
  {
    key: "status",
    header: "Status",
    render: (row) => <span className={statusClass(row.status)}>{row.status}</span>,
  },
];

interface QuotationLineEditorProps {
  lines: QuotationLineInput[];
  onUpdate: (index: number, patch: Partial<QuotationLineInput>) => void;
  onRemove: (index: number) => void;
  onAdd: () => void;
}

/** Decomposed per TD-FE-010's shared remediation pattern. */
function QuotationLineEditor({ lines, onUpdate, onRemove, onAdd }: QuotationLineEditorProps) {
  return (
    <>
      <h4>Quotation lines</h4>
      {lines.map((line, index) => (
        <div className="order-line-row" key={index}>
          <label htmlFor={`quotation-line-kind-${index}`}>Kind</label>
          <select
            id={`quotation-line-kind-${index}`}
            value={line.catalogItemKind}
            onChange={(event) => onUpdate(index, { catalogItemKind: event.target.value })}
          >
            <option value="test">Test</option>
            <option value="panel">Panel</option>
          </select>
          <label htmlFor={`quotation-line-test-id-${index}`}>Catalog item id</label>
          <input
            id={`quotation-line-test-id-${index}`}
            value={line.testDefinitionId}
            onChange={(event) => onUpdate(index, { testDefinitionId: event.target.value })}
          />
          <label htmlFor={`quotation-line-quantity-${index}`}>Quantity</label>
          <input
            id={`quotation-line-quantity-${index}`}
            type="number"
            min={1}
            value={line.quantity ?? 1}
            onChange={(event) => onUpdate(index, { quantity: Number(event.target.value) })}
          />
          {lines.length > 1 ? (
            <button type="button" onClick={() => onRemove(index)}>
              Remove line
            </button>
          ) : null}
        </div>
      ))}
      <button type="button" onClick={onAdd}>
        Add line
      </button>
    </>
  );
}

interface StartQuotationFormProps {
  canUse: boolean;
  patientId: string;
  onPatientIdChange: (value: string) => void;
  prospectiveFullName: string;
  onProspectiveFullNameChange: (value: string) => void;
  lines: QuotationLineInput[];
  onUpdateLine: (index: number, patch: Partial<QuotationLineInput>) => void;
  onRemoveLine: (index: number) => void;
  onAddLine: () => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  startAction: AsyncActionState<QuotationRequest>;
}

/** Decomposed per TD-FE-010's shared remediation pattern. */
function StartQuotationForm({
  canUse,
  patientId,
  onPatientIdChange,
  prospectiveFullName,
  onProspectiveFullNameChange,
  lines,
  onUpdateLine,
  onRemoveLine,
  onAddLine,
  onSubmit,
  startAction,
}: StartQuotationFormProps) {
  return (
    <div className="panel">
      <h3>Start quotation</h3>
      <form onSubmit={onSubmit}>
        <label htmlFor="quotation-patient-id">Patient id (optional)</label>
        <input
          id="quotation-patient-id"
          value={patientId}
          onChange={(event) => onPatientIdChange(event.target.value)}
        />
        <label htmlFor="quotation-prospective-name">Prospective contact name (optional)</label>
        <input
          id="quotation-prospective-name"
          value={prospectiveFullName}
          onChange={(event) => onProspectiveFullNameChange(event.target.value)}
        />

        <QuotationLineEditor
          lines={lines}
          onUpdate={onUpdateLine}
          onRemove={onRemoveLine}
          onAdd={onAddLine}
        />

        <button type="submit" disabled={!canUse || startAction.status === "loading"}>
          Start quotation
        </button>
        <StatusBanner
          status={startAction.status}
          errorMessage={startAction.errorMessage}
          successMessage="Quotation drafted."
        />
      </form>
    </div>
  );
}

interface QuotationConfirmDialogsProps {
  confirmingExpire: boolean;
  onCancelExpire: () => void;
  onConfirmExpire: () => void;
  confirmingCancel: boolean;
  onCancelCancel: () => void;
  onConfirmCancel: () => void;
}

/** Decomposed per TD-FE-010's shared remediation pattern. */
function QuotationConfirmDialogs({
  confirmingExpire,
  onCancelExpire,
  onConfirmExpire,
  confirmingCancel,
  onCancelCancel,
  onConfirmCancel,
}: QuotationConfirmDialogsProps) {
  return (
    <>
      <ConfirmDialog
        open={confirmingExpire}
        title="Confirm expiration"
        description="This quotation will be marked as expired and can no longer be accepted. Continue?"
        onCancel={onCancelExpire}
        onConfirm={onConfirmExpire}
      />
      <ConfirmDialog
        open={confirmingCancel}
        title="Confirm cancellation"
        description="This quotation will be marked as cancelled. Continue?"
        onCancel={onCancelCancel}
        onConfirm={onConfirmCancel}
      />
    </>
  );
}

interface QuotationDetailPanelProps {
  quotation: QuotationRequest;
  onIssue: (event: FormEvent<HTMLFormElement>) => void;
  issueAction: AsyncActionState<QuotationRequest>;
  onAccept: () => void;
  acceptAction: AsyncActionState<QuotationRequest>;
  onConvert: () => void;
  convertAction: AsyncActionState<QuotationRequest>;
  onRequestExpire: () => void;
  expireAction: AsyncActionState<QuotationRequest>;
  cancelReason: string;
  onCancelReasonChange: (value: string) => void;
  onRequestCancel: () => void;
  cancelAction: AsyncActionState<QuotationRequest>;
}

function QuotationDetailPanel({
  quotation,
  onIssue,
  issueAction,
  onAccept,
  acceptAction,
  onConvert,
  convertAction,
  onRequestExpire,
  expireAction,
  cancelReason,
  onCancelReasonChange,
  onRequestCancel,
  cancelAction,
}: QuotationDetailPanelProps) {
  const isOpen =
    quotation.status === "draft" ||
    quotation.status === "issued" ||
    quotation.status === "accepted";

  return (
    <div className="panel">
      <h3>Quotation detail: {quotation.quotationId}</h3>
      <table>
        <tbody>
          <tr>
            <th scope="row">Patient / contact</th>
            <td>{quotation.patientId ?? quotation.prospectiveFullName ?? "—"}</td>
          </tr>
          <tr>
            <th scope="row">Total</th>
            <td>{formatMoney(quotation.totalAmount)}</td>
          </tr>
          <tr>
            <th scope="row">Valid until</th>
            <td>{quotation.validUntil ?? "—"}</td>
          </tr>
          <tr>
            <th scope="row">Status</th>
            <td>
              <span className={statusClass(quotation.status)}>{quotation.status}</span>
            </td>
          </tr>
          <tr>
            <th scope="row">Converted order</th>
            <td>{quotation.convertedOrderId ?? "None"}</td>
          </tr>
        </tbody>
      </table>

      {quotation.status === "draft" ? (
        <form onSubmit={onIssue}>
          <button type="submit" disabled={issueAction.status === "loading"}>
            Issue quotation
          </button>
        </form>
      ) : null}
      <StatusBanner
        status={issueAction.status}
        errorMessage={issueAction.errorMessage}
        successMessage="Quotation issued."
      />

      {quotation.status === "issued" ? (
        <button type="button" disabled={acceptAction.status === "loading"} onClick={onAccept}>
          Accept quotation
        </button>
      ) : null}
      <StatusBanner
        status={acceptAction.status}
        errorMessage={acceptAction.errorMessage}
        successMessage="Quotation accepted."
      />

      {quotation.status === "accepted" ? (
        <button type="button" disabled={convertAction.status === "loading"} onClick={onConvert}>
          Convert to diagnostic order
        </button>
      ) : null}
      <StatusBanner
        status={convertAction.status}
        errorMessage={convertAction.errorMessage}
        successMessage="Quotation converted to a diagnostic order."
      />

      {quotation.status === "issued" ? (
        <button
          type="button"
          disabled={expireAction.status === "loading"}
          onClick={onRequestExpire}
        >
          Mark expired
        </button>
      ) : null}
      <StatusBanner
        status={expireAction.status}
        errorMessage={expireAction.errorMessage}
        successMessage="Quotation marked expired."
      />

      {isOpen ? (
        <div className="panel">
          <h4>Cancel quotation</h4>
          <label htmlFor="quotation-cancel-reason">Reason code (optional)</label>
          <input
            id="quotation-cancel-reason"
            value={cancelReason}
            onChange={(event) => onCancelReasonChange(event.target.value)}
          />
          <button
            type="button"
            disabled={cancelAction.status === "loading"}
            onClick={onRequestCancel}
          >
            Cancel quotation
          </button>
        </div>
      ) : null}
      <StatusBanner
        status={cancelAction.status}
        errorMessage={cancelAction.errorMessage}
        successMessage="Quotation cancelled."
      />
    </div>
  );
}

/** Decomposed per TD-FE-010's shared remediation pattern: bundles this screen's state and async
 * actions into a dedicated hook so the top-level component's render function stays within the
 * configured function-size lint threshold. */
function useQuotationsScreenState(tenantId?: string, laboratoryId?: string, branchId?: string) {
  const [quotations, setQuotations] = useState<QuotationRequest[]>([]);
  const [selected, setSelected] = useState<QuotationRequest | undefined>(undefined);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before listing quotations.");
    const loaded = await listQuotations(tenantId);
    setQuotations(loaded);
    return loaded;
  });

  const [patientId, setPatientId] = useState("");
  const [prospectiveFullName, setProspectiveFullName] = useState("");
  const [lines, setLines] = useState<QuotationLineInput[]>([newLine()]);
  const startAction = useAsyncAction(async () => {
    if (!tenantId || !laboratoryId || !branchId) {
      throw new Error("Select tenant, laboratory and branch scope before starting a quotation.");
    }
    const started = await startQuotation({
      tenantId,
      laboratoryId,
      branchId,
      patientId: patientId || undefined,
      prospectiveFullName: prospectiveFullName || undefined,
      channel: "employee_portal",
      lines: lines.filter((line) => line.testDefinitionId),
    });
    setQuotations((current) => [started, ...current]);
    setSelected(started);
    setPatientId("");
    setProspectiveFullName("");
    setLines([newLine()]);
    return started;
  });

  const issueAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_QUOTATION_FIRST);
    return issueQuotation(selected.quotationId, {});
  });
  const acceptAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_QUOTATION_FIRST);
    return acceptQuotation(selected.quotationId);
  });
  const convertAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_QUOTATION_FIRST);
    return convertQuotation(selected.quotationId);
  });
  const [confirmingExpire, setConfirmingExpire] = useState(false);
  const expireAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_QUOTATION_FIRST);
    return expireQuotation(selected.quotationId);
  });
  const [cancelReason, setCancelReason] = useState("");
  const [confirmingCancel, setConfirmingCancel] = useState(false);
  const cancelAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_QUOTATION_FIRST);
    return cancelQuotation(selected.quotationId, { reasonCode: cancelReason || undefined });
  });

  function applyUpdated(updated: QuotationRequest) {
    setSelected(updated);
    setQuotations((current) =>
      current.map((quotation) =>
        quotation.quotationId === updated.quotationId ? updated : quotation,
      ),
    );
  }

  function selectQuotation(quotation: QuotationRequest) {
    setSelected(quotation);
    setCancelReason("");
    issueAction.reset();
    acceptAction.reset();
    convertAction.reset();
    expireAction.reset();
    cancelAction.reset();
  }

  async function handleStart(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await startAction.run();
  }

  async function handleIssue(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await issueAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleAccept() {
    const result = await acceptAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleConvert() {
    const result = await convertAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  return {
    quotations,
    selected,
    listAction,
    patientId,
    setPatientId,
    prospectiveFullName,
    setProspectiveFullName,
    lines,
    setLines,
    startAction,
    issueAction,
    acceptAction,
    convertAction,
    confirmingExpire,
    setConfirmingExpire,
    expireAction,
    cancelReason,
    setCancelReason,
    confirmingCancel,
    setConfirmingCancel,
    cancelAction,
    applyUpdated,
    selectQuotation,
    handleStart,
    handleIssue,
    handleAccept,
    handleConvert,
  };
}

/**
 * BCM-ATT-006 employee portal surface (TD-FE-006 remediation): staff-initiated quotation drafting,
 * issue, accept, convert-to-order, expire and cancel, complementing the existing
 * PublicQuotationRequestsScreen (public-website triage of the same aggregate).
 */
export function QuotationsScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);
  const {
    quotations,
    selected,
    listAction,
    patientId,
    setPatientId,
    prospectiveFullName,
    setProspectiveFullName,
    lines,
    setLines,
    startAction,
    issueAction,
    acceptAction,
    convertAction,
    confirmingExpire,
    setConfirmingExpire,
    expireAction,
    cancelReason,
    setCancelReason,
    confirmingCancel,
    setConfirmingCancel,
    cancelAction,
    applyUpdated,
    selectQuotation,
    handleStart,
    handleIssue,
    handleAccept,
    handleConvert,
  } = useQuotationsScreenState(tenantId, laboratoryId, branchId);

  return (
    <section aria-labelledby="quotations-heading">
      <h2 id="quotations-heading">Quotations</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before drafting quotations.
        </p>
      ) : null}

      <StartQuotationForm
        canUse={canUse}
        patientId={patientId}
        onPatientIdChange={setPatientId}
        prospectiveFullName={prospectiveFullName}
        onProspectiveFullNameChange={setProspectiveFullName}
        lines={lines}
        onUpdateLine={(index, patch) =>
          setLines((current) =>
            current.map((line, i) => (i === index ? { ...line, ...patch } : line)),
          )
        }
        onRemoveLine={(index) => setLines((current) => current.filter((_, i) => i !== index))}
        onAddLine={() => setLines((current) => [...current, newLine()])}
        onSubmit={handleStart}
        startAction={startAction}
      />

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        Load quotations
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Quotations loaded."
      />
      {listAction.status === "success" && quotations.length === 0 ? (
        <p className="empty-state">No quotations exist yet for this tenant.</p>
      ) : null}

      <DataTable
        caption="Quotations"
        columns={columns}
        rows={quotations}
        rowKey={(row) => row.quotationId}
        onSelectRow={selectQuotation}
      />

      {selected ? (
        <QuotationDetailPanel
          quotation={selected}
          onIssue={handleIssue}
          issueAction={issueAction}
          onAccept={() => void handleAccept()}
          acceptAction={acceptAction}
          onConvert={() => void handleConvert()}
          convertAction={convertAction}
          onRequestExpire={() => setConfirmingExpire(true)}
          expireAction={expireAction}
          cancelReason={cancelReason}
          onCancelReasonChange={setCancelReason}
          onRequestCancel={() => setConfirmingCancel(true)}
          cancelAction={cancelAction}
        />
      ) : (
        <p className="empty-state">Select a quotation row to view its detail and take action.</p>
      )}

      <QuotationConfirmDialogs
        confirmingExpire={confirmingExpire}
        onCancelExpire={() => setConfirmingExpire(false)}
        onConfirmExpire={async () => {
          setConfirmingExpire(false);
          const result = await expireAction.run();
          if (result.ok) applyUpdated(result.data);
        }}
        confirmingCancel={confirmingCancel}
        onCancelCancel={() => setConfirmingCancel(false)}
        onConfirmCancel={async () => {
          setConfirmingCancel(false);
          const result = await cancelAction.run();
          if (result.ok) applyUpdated(result.data);
        }}
      />
    </section>
  );
}
