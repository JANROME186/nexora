/**
 * Waste and disposal administration screen (COM-MOD-010-FE-001, BCM-INV-009).
 *
 * Records inventory waste disposal backed by WasteController. Disposal is irreversible, so it is
 * gated behind a confirmation dialog before the request is sent.
 */
import { useState } from "react";
import { applyWaste, listWasteRecords } from "../../api/inventoryQualityApi";
import type { WasteRecord } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

const DEFAULT_ACTOR_ID = "current_user";

export function InventoryWasteScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;

  const [inventoryItemId, setInventoryItemId] = useState("");
  const [stockLotId, setStockLotId] = useState("");
  const [disposedQuantity, setDisposedQuantity] = useState("");
  const [reasonCode, setReasonCode] = useState("");
  const [reasonNote, setReasonNote] = useState("");
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);
  const [wasteRecords, setWasteRecords] = useState<WasteRecord[]>([]);
  const [confirmOpen, setConfirmOpen] = useState(false);

  const canUseScope = Boolean(tenantId && laboratoryId && branchId);

  const createAction = useAsyncAction(async () => {
    const created = await applyWaste({
      inventoryItemId,
      stockLotId,
      disposedQuantity,
      reasonCode,
      reasonNote: reasonNote || undefined,
      actorId,
    });
    setWasteRecords((current) => [created, ...current]);
    return created;
  });

  const listAction = useAsyncAction(async () => {
    const loaded = await listWasteRecords(tenantId ?? "", laboratoryId ?? "", branchId ?? "");
    setWasteRecords(loaded);
    return loaded;
  });

  const columns: DataTableColumn<WasteRecord>[] = [
    { key: "item", header: labels.shared.inventoryItemId, render: (row) => row.inventoryItemId },
    { key: "lot", header: labels.shared.stockLotId, render: (row) => row.stockLotId },
    {
      key: "qty",
      header: labels.inventoryWaste.disposedQuantity,
      render: (row) => row.disposedQuantity,
    },
    { key: "reason", header: labels.shared.reasonCode, render: (row) => row.reasonCode },
  ];

  const canSubmit = Boolean(
    canUseScope && inventoryItemId && stockLotId && disposedQuantity && reasonCode,
  );

  return (
    <section aria-labelledby="inventory-waste-heading">
      <h2 id="inventory-waste-heading">{labels.inventoryWaste.heading}</h2>
      <p>{labels.inventoryWaste.description}</p>
      <ScopeIndicator />
      {!canUseScope ? (
        <p className="status-banner status-banner--error">{labels.shared.branchRequired}</p>
      ) : null}

      <div className="panel">
        <label htmlFor="waste-item-id">{labels.shared.inventoryItemId}</label>
        <input
          id="waste-item-id"
          value={inventoryItemId}
          onChange={(e) => setInventoryItemId(e.target.value)}
        />
        <label htmlFor="waste-lot-id">{labels.shared.stockLotId}</label>
        <input
          id="waste-lot-id"
          value={stockLotId}
          onChange={(e) => setStockLotId(e.target.value)}
        />
        <label htmlFor="waste-qty">{labels.inventoryWaste.disposedQuantity}</label>
        <input
          id="waste-qty"
          value={disposedQuantity}
          onChange={(e) => setDisposedQuantity(e.target.value)}
        />
        <label htmlFor="waste-reason-code">{labels.shared.reasonCode}</label>
        <input
          id="waste-reason-code"
          value={reasonCode}
          onChange={(e) => setReasonCode(e.target.value)}
        />
        <label htmlFor="waste-reason-note">{labels.shared.reasonNote}</label>
        <input
          id="waste-reason-note"
          value={reasonNote}
          onChange={(e) => setReasonNote(e.target.value)}
        />
        <label htmlFor="waste-actor-id">{labels.shared.actorId}</label>
        <input id="waste-actor-id" value={actorId} onChange={(e) => setActorId(e.target.value)} />
        <button
          type="button"
          disabled={!canSubmit || createAction.status === "loading"}
          onClick={() => setConfirmOpen(true)}
        >
          {labels.shared.create}
        </button>
        <StatusBanner
          status={createAction.status}
          errorMessage={createAction.errorMessage}
          successMessage={labels.inventoryWaste.createSuccess}
        />
      </div>

      <ConfirmDialog
        open={confirmOpen}
        title={labels.inventoryWaste.confirmTitle}
        description={labels.inventoryWaste.confirmDescription}
        confirmLabel={labels.shared.confirm}
        cancelLabel={labels.shared.cancel}
        onCancel={() => setConfirmOpen(false)}
        onConfirm={() => {
          setConfirmOpen(false);
          createAction.run();
        }}
      />

      <button
        type="button"
        disabled={!canUseScope || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        {labels.inventoryWaste.loadWaste}
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && wasteRecords.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.inventoryWaste.heading}
        columns={columns}
        rows={wasteRecords}
        rowKey={(row) => row.wasteRecordId}
      />
    </section>
  );
}
