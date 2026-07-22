/**
 * Public Quotation Requests screen (COM-MOD-011-FE-001, BCM-ATT-006).
 *
 * Staff triage queue for anonymous public-website quotation requests. Reuses the existing
 * internal GET /api/care-delivery/quotations listing and the existing issue/cancel action
 * endpoints (no new backend endpoint) — the queue is derived by filtering the tenant's quotations
 * client-side to channel=="public_website" && status=="draft". The channel field was added to
 * QuotationRequest as part of this backlog item (it previously had no way to distinguish
 * public-website-submitted drafts from staff-initiated ones, unlike AppointmentSlot).
 */
import { useMemo, useState } from "react";
import { cancelQuotation, issueQuotation, listQuotations } from "../../api/publicRequestsApi";
import type { QuotationRequest } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { statusClass } from "../common/statusPresentation";

type Labels = MessageCatalog["publicQuotationRequests"];
type ActionHandle = { status: AsyncStatus; errorMessage?: string };

function isPublicPendingRequest(quotation: QuotationRequest): boolean {
  return quotation.channel === "public_website" && quotation.status === "draft";
}

function contactLabel(quotation: QuotationRequest): string {
  return (
    quotation.prospectiveFullName || quotation.prospectiveEmail || quotation.prospectivePhone || "—"
  );
}

function columns(labels: Labels): DataTableColumn<QuotationRequest>[] {
  return [
    { key: "quotationId", header: labels.columns.quotationId, render: (row) => row.quotationId },
    { key: "contact", header: labels.columns.contact, render: (row) => contactLabel(row) },
    {
      key: "status",
      header: labels.columns.status,
      render: (row) => <span className={statusClass(row.status)}>{row.status}</span>,
    },
  ];
}

interface DetailPanelProps {
  labels: Labels;
  quotation: QuotationRequest;
  reasonCode: string;
  onReasonCodeChange: (value: string) => void;
  onIssue: () => void;
  onReject: () => void;
  issueAction: ActionHandle;
  rejectAction: ActionHandle;
}

function DetailPanel({
  labels,
  quotation,
  reasonCode,
  onReasonCodeChange,
  onIssue,
  onReject,
  issueAction,
  rejectAction,
}: DetailPanelProps) {
  const pending = quotation.status === "draft";
  return (
    <div className="panel">
      <h3>{labels.detail.heading}</h3>
      <table>
        <tbody>
          <tr>
            <th scope="row">{labels.detail.contactName}</th>
            <td>{quotation.prospectiveFullName || "—"}</td>
          </tr>
          <tr>
            <th scope="row">{labels.detail.contactPhone}</th>
            <td>{quotation.prospectivePhone || "—"}</td>
          </tr>
          <tr>
            <th scope="row">{labels.detail.contactEmail}</th>
            <td>{quotation.prospectiveEmail || "—"}</td>
          </tr>
          <tr>
            <th scope="row">{labels.detail.status}</th>
            <td>
              <span className={statusClass(quotation.status)}>{quotation.status}</span>
            </td>
          </tr>
        </tbody>
      </table>

      {pending ? (
        <div className="catalog-toolbar">
          <button type="button" disabled={issueAction.status === "loading"} onClick={onIssue}>
            {labels.actions.issue}
          </button>
        </div>
      ) : null}
      <StatusBanner
        status={issueAction.status}
        errorMessage={issueAction.errorMessage}
        successMessage={labels.success.issued}
      />

      {pending ? (
        <div className="panel" style={{ marginTop: "1rem" }}>
          <label htmlFor="quotation-reject-reason">{labels.actions.reasonCode}</label>
          <input
            id="quotation-reject-reason"
            value={reasonCode}
            onChange={(event) => onReasonCodeChange(event.target.value)}
          />
          <button type="button" disabled={rejectAction.status === "loading"} onClick={onReject}>
            {labels.actions.reject}
          </button>
        </div>
      ) : null}
      <StatusBanner
        status={rejectAction.status}
        errorMessage={rejectAction.errorMessage}
        successMessage={labels.success.rejected}
      />
    </div>
  );
}

export function PublicQuotationRequestsScreen() {
  const { t } = useLocale();
  const labels = t.publicQuotationRequests;
  const { scope } = useAdminScope();
  const { tenantId } = scope;

  const [quotations, setQuotations] = useState<QuotationRequest[]>([]);
  const [selected, setSelected] = useState<QuotationRequest | undefined>(undefined);
  const [reasonCode, setReasonCode] = useState("");
  const [confirmingIssue, setConfirmingIssue] = useState(false);
  const [confirmingReject, setConfirmingReject] = useState(false);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) {
      throw new Error(labels.shared.tenantRequired);
    }
    const loaded = (await listQuotations(tenantId)).filter(isPublicPendingRequest);
    setQuotations(loaded);
    return loaded;
  });

  function applyActionOutcome(updated: QuotationRequest) {
    setQuotations((current) => current.filter((item) => item.quotationId !== updated.quotationId));
    setSelected(updated);
    setReasonCode("");
  }

  const issueAction = useAsyncAction(async () => {
    if (!selected) {
      throw new Error(labels.shared.selectFirst);
    }
    const updated = await issueQuotation(selected.quotationId, {});
    applyActionOutcome(updated);
    return updated;
  });

  const rejectAction = useAsyncAction(async () => {
    if (!selected) {
      throw new Error(labels.shared.selectFirst);
    }
    const updated = await cancelQuotation(selected.quotationId, {
      reasonCode: reasonCode || undefined,
    });
    applyActionOutcome(updated);
    return updated;
  });

  function selectQuotation(quotation: QuotationRequest) {
    setSelected(quotation);
    setReasonCode("");
    issueAction.reset();
    rejectAction.reset();
  }

  const tableColumns = useMemo(() => columns(labels), [labels]);

  return (
    <section aria-labelledby="public-quotation-requests-heading">
      <h2 id="public-quotation-requests-heading">{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />
      {!tenantId ? (
        <p className="status-banner status-banner--error">{labels.shared.tenantRequired}</p>
      ) : null}

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        {labels.shared.load}
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && quotations.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={tableColumns}
        rows={quotations}
        rowKey={(row) => row.quotationId}
        onSelectRow={selectQuotation}
      />

      {selected ? (
        <DetailPanel
          labels={labels}
          quotation={selected}
          reasonCode={reasonCode}
          onReasonCodeChange={setReasonCode}
          onIssue={() => setConfirmingIssue(true)}
          onReject={() => setConfirmingReject(true)}
          issueAction={issueAction}
          rejectAction={rejectAction}
        />
      ) : (
        <p className="empty-state">{labels.shared.selectFirst}</p>
      )}

      <ConfirmDialog
        open={confirmingIssue}
        title={labels.issueDialog.title}
        description={labels.issueDialog.description}
        confirmLabel={labels.shared.dialogConfirm}
        cancelLabel={labels.shared.dialogCancel}
        onCancel={() => setConfirmingIssue(false)}
        onConfirm={async () => {
          setConfirmingIssue(false);
          await issueAction.run();
        }}
      />
      <ConfirmDialog
        open={confirmingReject}
        title={labels.rejectDialog.title}
        description={labels.rejectDialog.description}
        confirmLabel={labels.shared.dialogConfirm}
        cancelLabel={labels.shared.dialogCancel}
        onCancel={() => setConfirmingReject(false)}
        onConfirm={async () => {
          setConfirmingReject(false);
          await rejectAction.run();
        }}
      />
    </section>
  );
}
