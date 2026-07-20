/**
 * Stock lot administration screen (COM-MOD-010-FE-001, BCM-INV-003).
 *
 * Registers lots for an inventory item, lists them, and quarantines or expires the selected lot,
 * backed by StockLotController. Decomposed into small sub-components (TD-FE-010) so no single
 * function exceeds the ESLint function-size/complexity thresholds.
 */
import { useState } from "react";
import {
  expireStockLot,
  listStockLots,
  quarantineStockLot,
  registerStockLot,
} from "../../api/inventoryQualityApi";
import type { StockLot } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { StatusBanner } from "../common/StatusBanner";
import { statusClass } from "../common/statusPresentation";

const DEFAULT_ACTOR_ID = "current_user";

type Labels = MessageCatalog["inventoryQuality"];

interface CreateLotFormProps {
  labels: Labels;
  inventoryItemId: string;
  onInventoryItemIdChange: (value: string) => void;
  registerStatus: AsyncStatus;
  registerErrorMessage?: string;
  listStatus: AsyncStatus;
  listErrorMessage?: string;
  onRegister: (fields: {
    lotNumber: string;
    supplierId: string;
    supplierName: string;
    expirationDate: string;
    receivedQuantity: string;
    actorId: string;
  }) => void;
  onLoad: () => void;
}

function CreateLotForm({
  labels,
  inventoryItemId,
  onInventoryItemIdChange,
  registerStatus,
  registerErrorMessage,
  listStatus,
  listErrorMessage,
  onRegister,
  onLoad,
}: CreateLotFormProps) {
  const [lotNumber, setLotNumber] = useState("");
  const [supplierId, setSupplierId] = useState("");
  const [supplierName, setSupplierName] = useState("");
  const [expirationDate, setExpirationDate] = useState("");
  const [receivedQuantity, setReceivedQuantity] = useState("");
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);

  return (
    <div className="panel">
      <label htmlFor="lots-item-id">{labels.shared.inventoryItemId}</label>
      <input
        id="lots-item-id"
        value={inventoryItemId}
        onChange={(e) => onInventoryItemIdChange(e.target.value)}
      />
      <form
        onSubmit={(event) => {
          event.preventDefault();
          onRegister({
            lotNumber,
            supplierId,
            supplierName,
            expirationDate,
            receivedQuantity,
            actorId,
          });
        }}
      >
        <label htmlFor="lots-lot-number">{labels.inventoryLots.lotNumber}</label>
        <input
          id="lots-lot-number"
          value={lotNumber}
          onChange={(e) => setLotNumber(e.target.value)}
        />
        <label htmlFor="lots-supplier-id">{labels.inventoryLots.supplierId}</label>
        <input
          id="lots-supplier-id"
          value={supplierId}
          onChange={(e) => setSupplierId(e.target.value)}
        />
        <label htmlFor="lots-supplier-name">{labels.inventoryLots.supplierName}</label>
        <input
          id="lots-supplier-name"
          value={supplierName}
          onChange={(e) => setSupplierName(e.target.value)}
        />
        <label htmlFor="lots-expiration">{labels.inventoryLots.expirationDate}</label>
        <input
          id="lots-expiration"
          type="date"
          value={expirationDate}
          onChange={(e) => setExpirationDate(e.target.value)}
        />
        <label htmlFor="lots-received-qty">{labels.inventoryLots.receivedQuantity}</label>
        <input
          id="lots-received-qty"
          value={receivedQuantity}
          onChange={(e) => setReceivedQuantity(e.target.value)}
        />
        <label htmlFor="lots-actor-id">{labels.shared.actorId}</label>
        <input id="lots-actor-id" value={actorId} onChange={(e) => setActorId(e.target.value)} />
        <div className="catalog-toolbar">
          <button
            type="submit"
            disabled={!inventoryItemId || !lotNumber || registerStatus === "loading"}
          >
            {labels.shared.create}
          </button>
          <button
            type="button"
            disabled={!inventoryItemId || listStatus === "loading"}
            onClick={onLoad}
          >
            {labels.inventoryLots.loadLots}
          </button>
        </div>
      </form>
      <StatusBanner
        status={registerStatus}
        errorMessage={registerErrorMessage}
        successMessage={labels.inventoryLots.registerSuccess}
      />
      <StatusBanner
        status={listStatus}
        errorMessage={listErrorMessage}
        successMessage={labels.shared.loaded}
      />
    </div>
  );
}

interface LotActionsPanelProps {
  labels: Labels;
  selectedLot?: StockLot;
  quarantineAction: { status: AsyncStatus; errorMessage?: string; run: () => void };
  expireAction: { status: AsyncStatus; errorMessage?: string; run: () => void };
}

function LotActionsPanel({
  labels,
  selectedLot,
  quarantineAction,
  expireAction,
}: LotActionsPanelProps) {
  return (
    <div className="panel">
      <h3>{selectedLot?.lotNumber ?? labels.shared.selectFirst}</h3>
      <div className="catalog-toolbar">
        <button type="button" disabled={!selectedLot} onClick={() => quarantineAction.run()}>
          {labels.inventoryLots.quarantine}
        </button>
        <button type="button" disabled={!selectedLot} onClick={() => expireAction.run()}>
          {labels.inventoryLots.expire}
        </button>
      </div>
      <StatusBanner
        status={quarantineAction.status}
        errorMessage={quarantineAction.errorMessage}
        successMessage={labels.inventoryLots.quarantineSuccess}
      />
      <StatusBanner
        status={expireAction.status}
        errorMessage={expireAction.errorMessage}
        successMessage={labels.inventoryLots.expireSuccess}
      />
    </div>
  );
}

function lotColumns(labels: Labels): DataTableColumn<StockLot>[] {
  return [
    { key: "lotNumber", header: labels.inventoryLots.lotNumber, render: (row) => row.lotNumber },
    {
      key: "supplier",
      header: labels.inventoryLots.supplierName,
      render: (row) => row.supplierName ?? "-",
    },
    {
      key: "remaining",
      header: labels.inventoryLots.remainingQuantity,
      render: (row) => row.remainingQuantity,
    },
    {
      key: "expiration",
      header: labels.inventoryLots.expirationDate,
      render: (row) => row.expirationDate ?? "-",
    },
    {
      key: "status",
      header: labels.shared.status,
      render: (row) => <span className={statusClass(row.status)}>{row.status}</span>,
    },
  ];
}

export function InventoryLotsScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;

  const [inventoryItemId, setInventoryItemId] = useState("");
  const [lots, setLots] = useState<StockLot[]>([]);
  const [selectedLot, setSelectedLot] = useState<StockLot | undefined>();

  const registerAction = useAsyncAction(
    async (fields: {
      lotNumber: string;
      supplierId: string;
      supplierName: string;
      expirationDate: string;
      receivedQuantity: string;
      actorId: string;
    }) => {
      const created = await registerStockLot(inventoryItemId, {
        lotNumber: fields.lotNumber,
        supplierId: fields.supplierId || undefined,
        supplierName: fields.supplierName || undefined,
        expirationDate: fields.expirationDate || undefined,
        receivedQuantity: fields.receivedQuantity || undefined,
        actorId: fields.actorId,
      });
      setLots((current) => [created, ...current]);
      return created;
    },
  );

  const listAction = useAsyncAction(async () => {
    const loaded = await listStockLots(inventoryItemId);
    setLots(loaded);
    return loaded;
  });

  const quarantineAction = useAsyncAction(async () => {
    const updated = await quarantineStockLot(selectedLot?.stockLotId ?? "", DEFAULT_ACTOR_ID);
    setLots((current) =>
      current.map((lot) => (lot.stockLotId === updated.stockLotId ? updated : lot)),
    );
    setSelectedLot(updated);
    return updated;
  });

  const expireAction = useAsyncAction(async () => {
    const updated = await expireStockLot(selectedLot?.stockLotId ?? "", DEFAULT_ACTOR_ID);
    setLots((current) =>
      current.map((lot) => (lot.stockLotId === updated.stockLotId ? updated : lot)),
    );
    setSelectedLot(updated);
    return updated;
  });

  return (
    <section aria-labelledby="inventory-lots-heading">
      <h2 id="inventory-lots-heading">{labels.inventoryLots.heading}</h2>
      <p>{labels.inventoryLots.description}</p>

      <CreateLotForm
        labels={labels}
        inventoryItemId={inventoryItemId}
        onInventoryItemIdChange={setInventoryItemId}
        registerStatus={registerAction.status}
        registerErrorMessage={registerAction.errorMessage}
        listStatus={listAction.status}
        listErrorMessage={listAction.errorMessage}
        onRegister={(fields) => registerAction.run(fields)}
        onLoad={() => listAction.run()}
      />

      {listAction.status === "success" && lots.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.inventoryLots.heading}
        columns={lotColumns(labels)}
        rows={lots}
        rowKey={(row) => row.stockLotId}
        onSelectRow={setSelectedLot}
      />

      <LotActionsPanel
        labels={labels}
        selectedLot={selectedLot}
        quarantineAction={quarantineAction}
        expireAction={expireAction}
      />
    </section>
  );
}
