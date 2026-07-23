/**
 * External Quality Controls administration screen (COM-MOD-013-FE-001, BCM-QLT-002).
 *
 * Lists external quality controls, creates new ones, and allows a supervisor to approve
 * or reject them, backed by ExternalQualityControlController. Decomposed into small sub-components
 * (TD-FE-010 pattern) so no single function exceeds the ESLint function-size/complexity thresholds.
 */
import { useState } from "react";
import {
  approveExternalQualityControl,
  createExternalQualityControl,
  listExternalQualityControls,
  rejectExternalQualityControl,
} from "../../api/externalQualityComplianceApi";
import type { ExternalQualityControl } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { ConfirmDialog } from "../common/ConfirmDialog";

type Labels = MessageCatalog["advancedQualityCompliance"]["externalQualityControls"];
type SharedLabels = MessageCatalog["advancedQualityCompliance"]["shared"];

interface CreateControlFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: {
    controlType: string;
    providerName: string;
    referenceCode: string;
    description: string;
    performedAt: string;
  }) => void;
}

function CreateControlForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: CreateControlFormProps) {
  const [controlType, setControlType] = useState("");
  const [providerName, setProviderName] = useState("");
  const [referenceCode, setReferenceCode] = useState("");
  const [description, setDescription] = useState("");
  const [performedAt, setPerformedAt] = useState("");

  return (
    <div className="panel">
      <label htmlFor="eqc-control-type">{labels.controlType}</label>
      <input
        id="eqc-control-type"
        value={controlType}
        onChange={(e) => setControlType(e.target.value)}
      />
      <label htmlFor="eqc-provider-name">{labels.providerName}</label>
      <input
        id="eqc-provider-name"
        value={providerName}
        onChange={(e) => setProviderName(e.target.value)}
      />
      <label htmlFor="eqc-reference-code">{labels.referenceCode}</label>
      <input
        id="eqc-reference-code"
        value={referenceCode}
        onChange={(e) => setReferenceCode(e.target.value)}
      />
      <label htmlFor="eqc-description">{labels.description2}</label>
      <input
        id="eqc-description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <label htmlFor="eqc-performed-at">{labels.performedAt}</label>
      <input
        id="eqc-performed-at"
        type="date"
        value={performedAt}
        onChange={(e) => setPerformedAt(e.target.value)}
      />
      <button
        type="button"
        id="eqc-create-btn"
        disabled={disabled}
        onClick={() =>
          onSubmit({ controlType, providerName, referenceCode, description, performedAt })
        }
      >
        Registrar
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.createSuccess}
      />
    </div>
  );
}

interface ReviewFormProps {
  labels: Labels;
  shared: SharedLabels;
  disabled: boolean;
  reviewStatus: AsyncStatus;
  reviewError?: string;
  successMessage: string;
  actionLabel: string;
  onSubmit: (fields: { reviewedBy: string; comments: string }) => void;
}

function ReviewForm({
  labels,
  disabled,
  reviewStatus,
  reviewError,
  successMessage,
  actionLabel,
  onSubmit,
}: ReviewFormProps) {
  const [reviewedBy, setReviewedBy] = useState("");
  const [comments, setComments] = useState("");

  return (
    <div className="panel">
      <label htmlFor="eqc-reviewed-by">{labels.reviewedBy}</label>
      <input
        id="eqc-reviewed-by"
        value={reviewedBy}
        onChange={(e) => setReviewedBy(e.target.value)}
      />
      <label htmlFor="eqc-comments">{labels.comments}</label>
      <input id="eqc-comments" value={comments} onChange={(e) => setComments(e.target.value)} />
      <button type="button" disabled={disabled} onClick={() => onSubmit({ reviewedBy, comments })}>
        {actionLabel}
      </button>
      <StatusBanner
        status={reviewStatus}
        errorMessage={reviewError}
        successMessage={successMessage}
      />
    </div>
  );
}

function eqcColumns(labels: Labels): DataTableColumn<ExternalQualityControl>[] {
  return [
    { key: "refCode", header: labels.referenceCode, render: (r) => r.referenceCode },
    { key: "controlType", header: labels.controlType, render: (r) => r.controlType },
    { key: "providerName", header: labels.providerName, render: (r) => r.providerName },
    { key: "status", header: "Estado", render: (r) => r.status },
    { key: "performedAt", header: labels.performedAt, render: (r) => r.performedAt ?? "-" },
    { key: "id", header: labels.externalQCId, render: (r) => r.externalQCId },
  ];
}

export function ExternalQualityControlsScreen() {
  const { t } = useLocale();
  const shared = t.advancedQualityCompliance.shared;
  const labels = t.advancedQualityCompliance.externalQualityControls;
  const { scope } = useAdminScope();

  const [controls, setControls] = useState<ExternalQualityControl[]>([]);
  const [selected, setSelected] = useState<ExternalQualityControl | undefined>();
  const [confirmApprove, setConfirmApprove] = useState(false);
  const [confirmReject, setConfirmReject] = useState(false);
  const [pendingReview, setPendingReview] = useState<{
    reviewedBy: string;
    comments: string;
  } | null>(null);

  const loadAction = useAsyncAction(listExternalQualityControls);
  const createAction = useAsyncAction(createExternalQualityControl);
  const approveAction = useAsyncAction(approveExternalQualityControl);
  const rejectAction = useAsyncAction(rejectExternalQualityControl);

  const tenantId = scope.tenantId ?? "";
  const laboratoryId = scope.laboratoryId ?? "";

  async function handleLoad() {
    const result = await loadAction.run(tenantId, laboratoryId);
    if (result.ok) setControls(result.data);
  }

  async function handleCreate(fields: {
    controlType: string;
    providerName: string;
    referenceCode: string;
    description: string;
    performedAt: string;
  }) {
    const result = await createAction.run(tenantId, laboratoryId, {
      ...fields,
      performedAt: fields.performedAt || undefined,
    });
    if (result.ok) setControls((prev) => [...prev, result.data]);
  }

  async function confirmReviewAction(
    action: typeof approveAction | typeof rejectAction,
    setModalOpen: (open: boolean) => void,
  ) {
    if (!selected || !pendingReview) return;
    setModalOpen(false);
    const result = await action.run(selected.externalQCId, {
      reviewedBy: pendingReview.reviewedBy,
      comments: pendingReview.comments || undefined,
    });
    if (result.ok)
      setControls((prev) =>
        prev.map((c) => (c.externalQCId === result.data.externalQCId ? result.data : c)),
      );
  }

  return (
    <section aria-labelledby="eqc-heading">
      <h2 id="eqc-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>

      <button
        type="button"
        id="eqc-load-btn"
        disabled={loadAction.status === "loading"}
        onClick={handleLoad}
      >
        {labels.loadControls}
      </button>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage={shared.loaded}
      />
      {loadAction.status === "success" && controls.length === 0 ? (
        <p className="empty-state">{shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={eqcColumns(labels)}
        rows={controls}
        rowKey={(r) => r.externalQCId}
        onSelectRow={setSelected}
      />

      <CreateControlForm
        labels={labels}
        disabled={createAction.status === "loading" || !tenantId || !laboratoryId}
        status={createAction.status}
        errorMessage={createAction.errorMessage}
        onSubmit={handleCreate}
      />

      {selected ? (
        <>
          <ReviewForm
            labels={labels}
            shared={shared}
            disabled={approveAction.status === "loading"}
            reviewStatus={approveAction.status}
            reviewError={approveAction.errorMessage}
            successMessage={labels.approveSuccess}
            actionLabel={labels.approve}
            onSubmit={(f) => {
              setPendingReview(f);
              setConfirmApprove(true);
            }}
          />
          <ReviewForm
            labels={labels}
            shared={shared}
            disabled={rejectAction.status === "loading"}
            reviewStatus={rejectAction.status}
            reviewError={rejectAction.errorMessage}
            successMessage={labels.rejectSuccess}
            actionLabel={labels.reject}
            onSubmit={(f) => {
              setPendingReview(f);
              setConfirmReject(true);
            }}
          />
        </>
      ) : null}

      <ConfirmDialog
        open={confirmApprove}
        title={labels.approveDialog.title}
        description={labels.approveDialog.description}
        confirmLabel={shared.dialogConfirm}
        cancelLabel={shared.dialogCancel}
        onConfirm={() => confirmReviewAction(approveAction, setConfirmApprove)}
        onCancel={() => setConfirmApprove(false)}
      />
      <ConfirmDialog
        open={confirmReject}
        title={labels.rejectDialog.title}
        description={labels.rejectDialog.description}
        confirmLabel={shared.dialogConfirm}
        cancelLabel={shared.dialogCancel}
        onConfirm={() => confirmReviewAction(rejectAction, setConfirmReject)}
        onCancel={() => setConfirmReject(false)}
      />
    </section>
  );
}
