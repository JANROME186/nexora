/**
 * Stock movements administration screen (COM-MOD-010-FE-001, BCM-INV-005/006/007).
 *
 * Combines stock entries, stock exits and consumption tracking into one screen because the
 * backend gates all three under a single SCREEN_INVENTORY_STOCK_MOVEMENTS permission
 * (EndpointPermissionRegistry). Each section is its own component so hooks/state stay isolated
 * and no single function grows past the ESLint size/complexity thresholds (TD-FE-010).
 */
import { useState } from "react";
import {
  applyConsumption,
  applyStockExit,
  applyStockReceipt,
  listConsumptionRecords,
  listStockEntries,
  listStockExits,
} from "../../api/inventoryQualityApi";
import type { ConsumptionRecord, StockEntry, StockExit } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

const DEFAULT_ACTOR_ID = "current_user";

type Labels = MessageCatalog["inventoryQuality"];

interface ScopeIds {
  tenantId: string;
  laboratoryId: string;
  branchId: string;
}

function EntriesSection({ labels, ids }: { labels: Labels; ids: ScopeIds }) {
  const [inventoryItemId, setInventoryItemId] = useState("");
  const [entryType, setEntryType] = useState("PURCHASE_RECEIPT");
  const [quantity, setQuantity] = useState("");
  const [reasonCode, setReasonCode] = useState("");
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);
  const [entries, setEntries] = useState<StockEntry[]>([]);

  const createAction = useAsyncAction(async () => {
    const created = await applyStockReceipt({
      inventoryItemId,
      quantity,
      entryType,
      reasonCode: reasonCode || undefined,
      actorId,
    });
    setEntries((current) => [created, ...current]);
    return created;
  });

  const listAction = useAsyncAction(async () => {
    const loaded = await listStockEntries(ids.tenantId, ids.laboratoryId, ids.branchId);
    setEntries(loaded);
    return loaded;
  });

  const columns: DataTableColumn<StockEntry>[] = [
    { key: "item", header: labels.shared.inventoryItemId, render: (row) => row.inventoryItemId },
    {
      key: "type",
      header: labels.inventoryStockMovements.entryType,
      render: (row) => row.entryType,
    },
    { key: "qty", header: labels.shared.quantity, render: (row) => row.quantity },
  ];

  return (
    <div className="panel">
      <h3>{labels.inventoryStockMovements.entriesHeading}</h3>
      <label htmlFor="entry-item-id">{labels.shared.inventoryItemId}</label>
      <input
        id="entry-item-id"
        value={inventoryItemId}
        onChange={(e) => setInventoryItemId(e.target.value)}
      />
      <label htmlFor="entry-type">{labels.inventoryStockMovements.entryType}</label>
      <input id="entry-type" value={entryType} onChange={(e) => setEntryType(e.target.value)} />
      <label htmlFor="entry-qty">{labels.shared.quantity}</label>
      <input id="entry-qty" value={quantity} onChange={(e) => setQuantity(e.target.value)} />
      <label htmlFor="entry-reason">{labels.shared.reasonCode}</label>
      <input id="entry-reason" value={reasonCode} onChange={(e) => setReasonCode(e.target.value)} />
      <label htmlFor="entry-actor">{labels.shared.actorId}</label>
      <input id="entry-actor" value={actorId} onChange={(e) => setActorId(e.target.value)} />
      <div className="catalog-toolbar">
        <button
          type="button"
          disabled={!inventoryItemId || !quantity || createAction.status === "loading"}
          onClick={() => createAction.run()}
        >
          {labels.shared.create}
        </button>
        <button
          type="button"
          onClick={() => listAction.run()}
          disabled={listAction.status === "loading"}
        >
          {labels.inventoryStockMovements.loadEntries}
        </button>
      </div>
      <StatusBanner
        status={createAction.status}
        errorMessage={createAction.errorMessage}
        successMessage={labels.inventoryStockMovements.entrySuccess}
      />
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && entries.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.inventoryStockMovements.entriesHeading}
        columns={columns}
        rows={entries}
        rowKey={(row) => row.stockEntryId}
      />
    </div>
  );
}

function ExitsSection({ labels, ids }: { labels: Labels; ids: ScopeIds }) {
  const [inventoryItemId, setInventoryItemId] = useState("");
  const [stockLotId, setStockLotId] = useState("");
  const [exitType, setExitType] = useState("BRANCH_TRANSFER");
  const [quantity, setQuantity] = useState("");
  const [destinationBranchId, setDestinationBranchId] = useState("");
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);
  const [exits, setExits] = useState<StockExit[]>([]);

  const createAction = useAsyncAction(async () => {
    const created = await applyStockExit({
      inventoryItemId,
      stockLotId,
      quantity,
      exitType,
      destinationBranchId: destinationBranchId || undefined,
      actorId,
    });
    setExits((current) => [created, ...current]);
    return created;
  });

  const listAction = useAsyncAction(async () => {
    const loaded = await listStockExits(ids.tenantId, ids.laboratoryId, ids.branchId);
    setExits(loaded);
    return loaded;
  });

  const columns: DataTableColumn<StockExit>[] = [
    { key: "item", header: labels.shared.inventoryItemId, render: (row) => row.inventoryItemId },
    { key: "type", header: labels.inventoryStockMovements.exitType, render: (row) => row.exitType },
    { key: "qty", header: labels.shared.quantity, render: (row) => row.quantity },
  ];

  return (
    <div className="panel">
      <h3>{labels.inventoryStockMovements.exitsHeading}</h3>
      <label htmlFor="exit-item-id">{labels.shared.inventoryItemId}</label>
      <input
        id="exit-item-id"
        value={inventoryItemId}
        onChange={(e) => setInventoryItemId(e.target.value)}
      />
      <label htmlFor="exit-lot-id">{labels.shared.stockLotId}</label>
      <input id="exit-lot-id" value={stockLotId} onChange={(e) => setStockLotId(e.target.value)} />
      <label htmlFor="exit-type">{labels.inventoryStockMovements.exitType}</label>
      <input id="exit-type" value={exitType} onChange={(e) => setExitType(e.target.value)} />
      <label htmlFor="exit-qty">{labels.shared.quantity}</label>
      <input id="exit-qty" value={quantity} onChange={(e) => setQuantity(e.target.value)} />
      <label htmlFor="exit-destination">{labels.inventoryStockMovements.destinationBranchId}</label>
      <input
        id="exit-destination"
        value={destinationBranchId}
        onChange={(e) => setDestinationBranchId(e.target.value)}
      />
      <label htmlFor="exit-actor">{labels.shared.actorId}</label>
      <input id="exit-actor" value={actorId} onChange={(e) => setActorId(e.target.value)} />
      <div className="catalog-toolbar">
        <button
          type="button"
          disabled={
            !inventoryItemId || !stockLotId || !quantity || createAction.status === "loading"
          }
          onClick={() => createAction.run()}
        >
          {labels.shared.create}
        </button>
        <button
          type="button"
          onClick={() => listAction.run()}
          disabled={listAction.status === "loading"}
        >
          {labels.inventoryStockMovements.loadExits}
        </button>
      </div>
      <StatusBanner
        status={createAction.status}
        errorMessage={createAction.errorMessage}
        successMessage={labels.inventoryStockMovements.exitSuccess}
      />
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && exits.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.inventoryStockMovements.exitsHeading}
        columns={columns}
        rows={exits}
        rowKey={(row) => row.stockExitId}
      />
    </div>
  );
}

function ConsumptionSection({ labels, ids }: { labels: Labels; ids: ScopeIds }) {
  const [inventoryItemId, setInventoryItemId] = useState("");
  const [consumptionContext, setConsumptionContext] = useState("DIAGNOSTIC_TESTING");
  const [consumedQuantity, setConsumedQuantity] = useState("");
  const [diagnosticOrderId, setDiagnosticOrderId] = useState("");
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);
  const [records, setRecords] = useState<ConsumptionRecord[]>([]);

  const createAction = useAsyncAction(async () => {
    const created = await applyConsumption({
      inventoryItemId,
      consumedQuantity,
      consumptionContext,
      diagnosticOrderId: diagnosticOrderId || undefined,
      actorId,
    });
    setRecords((current) => [created, ...current]);
    return created;
  });

  const listAction = useAsyncAction(async () => {
    const loaded = await listConsumptionRecords(ids.tenantId, ids.laboratoryId, ids.branchId);
    setRecords(loaded);
    return loaded;
  });

  const columns: DataTableColumn<ConsumptionRecord>[] = [
    { key: "item", header: labels.shared.inventoryItemId, render: (row) => row.inventoryItemId },
    {
      key: "context",
      header: labels.inventoryStockMovements.consumptionContext,
      render: (row) => row.consumptionContext,
    },
    { key: "qty", header: labels.shared.quantity, render: (row) => row.consumedQuantity },
  ];

  return (
    <div className="panel">
      <h3>{labels.inventoryStockMovements.consumptionHeading}</h3>
      <label htmlFor="consumption-item-id">{labels.shared.inventoryItemId}</label>
      <input
        id="consumption-item-id"
        value={inventoryItemId}
        onChange={(e) => setInventoryItemId(e.target.value)}
      />
      <label htmlFor="consumption-context">
        {labels.inventoryStockMovements.consumptionContext}
      </label>
      <input
        id="consumption-context"
        value={consumptionContext}
        onChange={(e) => setConsumptionContext(e.target.value)}
      />
      <label htmlFor="consumption-qty">{labels.shared.quantity}</label>
      <input
        id="consumption-qty"
        value={consumedQuantity}
        onChange={(e) => setConsumedQuantity(e.target.value)}
      />
      <label htmlFor="consumption-order-id">
        {labels.inventoryStockMovements.diagnosticOrderId}
      </label>
      <input
        id="consumption-order-id"
        value={diagnosticOrderId}
        onChange={(e) => setDiagnosticOrderId(e.target.value)}
      />
      <label htmlFor="consumption-actor">{labels.shared.actorId}</label>
      <input id="consumption-actor" value={actorId} onChange={(e) => setActorId(e.target.value)} />
      <div className="catalog-toolbar">
        <button
          type="button"
          disabled={!inventoryItemId || !consumedQuantity || createAction.status === "loading"}
          onClick={() => createAction.run()}
        >
          {labels.shared.create}
        </button>
        <button
          type="button"
          onClick={() => listAction.run()}
          disabled={listAction.status === "loading"}
        >
          {labels.inventoryStockMovements.loadConsumption}
        </button>
      </div>
      <StatusBanner
        status={createAction.status}
        errorMessage={createAction.errorMessage}
        successMessage={labels.inventoryStockMovements.consumptionSuccess}
      />
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && records.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.inventoryStockMovements.consumptionHeading}
        columns={columns}
        rows={records}
        rowKey={(row) => row.consumptionRecordId}
      />
    </div>
  );
}

export function InventoryStockMovementsScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;
  const { scope } = useAdminScope();
  const ids: ScopeIds = {
    tenantId: scope.tenantId ?? "",
    laboratoryId: scope.laboratoryId ?? "",
    branchId: scope.branchId ?? "",
  };

  return (
    <section aria-labelledby="inventory-stock-movements-heading">
      <h2 id="inventory-stock-movements-heading">{labels.inventoryStockMovements.heading}</h2>
      <p>{labels.inventoryStockMovements.description}</p>
      <ScopeIndicator />
      <EntriesSection labels={labels} ids={ids} />
      <ExitsSection labels={labels} ids={ids} />
      <ConsumptionSection labels={labels} ids={ids} />
    </section>
  );
}
