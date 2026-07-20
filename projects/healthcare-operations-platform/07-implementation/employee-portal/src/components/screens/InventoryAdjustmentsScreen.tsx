/**
 * Inventory adjustments administration screen (COM-MOD-010-FE-001, BCM-INV-008).
 *
 * Records approved quantity adjustments backed by AdjustmentController.
 */
import { useState } from "react";
import { applyAdjustment, listAdjustments } from "../../api/inventoryQualityApi";
import type { AdjustmentRecord } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

const DEFAULT_ACTOR_ID = "current_user";

export function InventoryAdjustmentsScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;

  const [inventoryItemId, setInventoryItemId] = useState("");
  const [deltaQuantity, setDeltaQuantity] = useState("");
  const [reasonCode, setReasonCode] = useState("");
  const [reasonNote, setReasonNote] = useState("");
  const [requestedBy, setRequestedBy] = useState("");
  const [approverId, setApproverId] = useState("");
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);
  const [adjustments, setAdjustments] = useState<AdjustmentRecord[]>([]);

  const canUseScope = Boolean(tenantId && laboratoryId && branchId);

  const createAction = useAsyncAction(async () => {
    const created = await applyAdjustment({
      inventoryItemId,
      deltaQuantity,
      reasonCode,
      reasonNote: reasonNote || undefined,
      requestedBy,
      approverId,
      actorId,
    });
    setAdjustments((current) => [created, ...current]);
    return created;
  });

  const listAction = useAsyncAction(async () => {
    const loaded = await listAdjustments(tenantId ?? "", laboratoryId ?? "", branchId ?? "");
    setAdjustments(loaded);
    return loaded;
  });

  const columns: DataTableColumn<AdjustmentRecord>[] = [
    { key: "item", header: labels.shared.inventoryItemId, render: (row) => row.inventoryItemId },
    {
      key: "delta",
      header: labels.inventoryAdjustments.deltaQuantity,
      render: (row) => row.deltaQuantity,
    },
    { key: "reason", header: labels.shared.reasonCode, render: (row) => row.reasonCode },
    {
      key: "approver",
      header: labels.inventoryAdjustments.approverId,
      render: (row) => row.approverId,
    },
  ];

  return (
    <section aria-labelledby="inventory-adjustments-heading">
      <h2 id="inventory-adjustments-heading">{labels.inventoryAdjustments.heading}</h2>
      <p>{labels.inventoryAdjustments.description}</p>
      <ScopeIndicator />
      {!canUseScope ? (
        <p className="status-banner status-banner--error">{labels.shared.branchRequired}</p>
      ) : null}

      <div className="panel">
        <form
          onSubmit={(event) => {
            event.preventDefault();
            createAction.run();
          }}
        >
          <label htmlFor="adj-item-id">{labels.shared.inventoryItemId}</label>
          <input
            id="adj-item-id"
            value={inventoryItemId}
            onChange={(e) => setInventoryItemId(e.target.value)}
          />
          <label htmlFor="adj-delta">{labels.inventoryAdjustments.deltaQuantity}</label>
          <input
            id="adj-delta"
            value={deltaQuantity}
            onChange={(e) => setDeltaQuantity(e.target.value)}
          />
          <label htmlFor="adj-reason-code">{labels.shared.reasonCode}</label>
          <input
            id="adj-reason-code"
            value={reasonCode}
            onChange={(e) => setReasonCode(e.target.value)}
          />
          <label htmlFor="adj-reason-note">{labels.shared.reasonNote}</label>
          <input
            id="adj-reason-note"
            value={reasonNote}
            onChange={(e) => setReasonNote(e.target.value)}
          />
          <label htmlFor="adj-requested-by">{labels.inventoryAdjustments.requestedBy}</label>
          <input
            id="adj-requested-by"
            value={requestedBy}
            onChange={(e) => setRequestedBy(e.target.value)}
          />
          <label htmlFor="adj-approver-id">{labels.inventoryAdjustments.approverId}</label>
          <input
            id="adj-approver-id"
            value={approverId}
            onChange={(e) => setApproverId(e.target.value)}
          />
          <label htmlFor="adj-actor-id">{labels.shared.actorId}</label>
          <input id="adj-actor-id" value={actorId} onChange={(e) => setActorId(e.target.value)} />
          <button
            type="submit"
            disabled={
              !canUseScope ||
              !inventoryItemId ||
              !deltaQuantity ||
              !reasonCode ||
              !requestedBy ||
              !approverId ||
              createAction.status === "loading"
            }
          >
            {labels.shared.create}
          </button>
        </form>
        <StatusBanner
          status={createAction.status}
          errorMessage={createAction.errorMessage}
          successMessage={labels.inventoryAdjustments.createSuccess}
        />
      </div>

      <button
        type="button"
        disabled={!canUseScope || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        {labels.inventoryAdjustments.loadAdjustments}
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && adjustments.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.inventoryAdjustments.heading}
        columns={columns}
        rows={adjustments}
        rowKey={(row) => row.adjustmentId}
      />
    </section>
  );
}
