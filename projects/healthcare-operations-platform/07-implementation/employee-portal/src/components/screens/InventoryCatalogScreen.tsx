/**
 * Inventory catalog administration screen (COM-MOD-010-FE-001, BCM-INV-001).
 *
 * Register, update and discontinue inventory items backed by InventoryItemController. Reagent and
 * equipment profiles are managed on their own screens (BCM-INV-002/BCM-QLT-004); this screen only
 * owns the base catalog record.
 */
import { useState } from "react";
import {
  discontinueInventoryItem,
  listInventoryItems,
  registerInventoryItem,
  updateInventoryItem,
} from "../../api/inventoryQualityApi";
import type { InventoryItem } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { statusClass } from "../common/statusPresentation";

const DEFAULT_ACTOR_ID = "current_user";

type Labels = MessageCatalog["inventoryQuality"];

interface CreateItemFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: {
    itemCode: string;
    itemName: string;
    itemType: string;
    classification: string;
    unitOfMeasure: string;
  }) => void;
}

function CreateItemForm({ labels, disabled, status, errorMessage, onSubmit }: CreateItemFormProps) {
  const [itemCode, setItemCode] = useState("");
  const [itemName, setItemName] = useState("");
  const [itemType, setItemType] = useState("");
  const [classification, setClassification] = useState("");
  const [unitOfMeasure, setUnitOfMeasure] = useState("");

  return (
    <div className="panel">
      <h3>{labels.shared.create}</h3>
      <form
        onSubmit={(event) => {
          event.preventDefault();
          onSubmit({ itemCode, itemName, itemType, classification, unitOfMeasure });
        }}
      >
        <label htmlFor="inv-item-code">{labels.inventoryCatalog.itemCode}</label>
        <input id="inv-item-code" value={itemCode} onChange={(e) => setItemCode(e.target.value)} />
        <label htmlFor="inv-item-name">{labels.inventoryCatalog.itemName}</label>
        <input id="inv-item-name" value={itemName} onChange={(e) => setItemName(e.target.value)} />
        <label htmlFor="inv-item-type">{labels.inventoryCatalog.itemType}</label>
        <input id="inv-item-type" value={itemType} onChange={(e) => setItemType(e.target.value)} />
        <label htmlFor="inv-classification">{labels.inventoryCatalog.classification}</label>
        <input
          id="inv-classification"
          value={classification}
          onChange={(e) => setClassification(e.target.value)}
        />
        <label htmlFor="inv-unit">{labels.inventoryCatalog.unitOfMeasure}</label>
        <input
          id="inv-unit"
          value={unitOfMeasure}
          onChange={(e) => setUnitOfMeasure(e.target.value)}
        />
        <button type="submit" disabled={disabled || !itemCode || status === "loading"}>
          {labels.shared.create}
        </button>
      </form>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.inventoryCatalog.registerSuccess}
      />
    </div>
  );
}

interface UpdateItemPanelProps {
  labels: Labels;
  selectedItem?: InventoryItem;
  status: string;
  actorId: string;
  onStatusChange: (status: string) => void;
  onActorIdChange: (actorId: string) => void;
  updateAction: { status: AsyncStatus; errorMessage?: string; run: () => void };
  discontinueAction: { status: AsyncStatus; errorMessage?: string; run: () => void };
}

function UpdateItemPanel({
  labels,
  selectedItem,
  status,
  actorId,
  onStatusChange,
  onActorIdChange,
  updateAction,
  discontinueAction,
}: UpdateItemPanelProps) {
  return (
    <div className="panel">
      <h3>{selectedItem?.itemCode ?? labels.shared.selectFirst}</h3>
      <label htmlFor="inv-update-status">{labels.shared.status}</label>
      <input
        id="inv-update-status"
        value={status}
        onChange={(e) => onStatusChange(e.target.value)}
      />
      <label htmlFor="inv-actor-id">{labels.shared.actorId}</label>
      <input id="inv-actor-id" value={actorId} onChange={(e) => onActorIdChange(e.target.value)} />
      <div className="catalog-toolbar">
        <button type="button" disabled={!selectedItem} onClick={() => updateAction.run()}>
          {labels.shared.update}
        </button>
        <button type="button" disabled={!selectedItem} onClick={() => discontinueAction.run()}>
          {labels.inventoryCatalog.discontinue}
        </button>
      </div>
      <StatusBanner
        status={updateAction.status}
        errorMessage={updateAction.errorMessage}
        successMessage={labels.inventoryCatalog.updateSuccess}
      />
      <StatusBanner
        status={discontinueAction.status}
        errorMessage={discontinueAction.errorMessage}
        successMessage={labels.inventoryCatalog.discontinueSuccess}
      />
    </div>
  );
}

function itemColumns(labels: Labels): DataTableColumn<InventoryItem>[] {
  return [
    { key: "itemCode", header: labels.inventoryCatalog.itemCode, render: (row) => row.itemCode },
    { key: "itemName", header: labels.inventoryCatalog.itemName, render: (row) => row.itemName },
    { key: "itemType", header: labels.inventoryCatalog.itemType, render: (row) => row.itemType },
    {
      key: "onHand",
      header: labels.inventoryCatalog.onHandQuantity,
      render: (row) => row.stockSummary?.onHandQuantity ?? "-",
    },
    {
      key: "status",
      header: labels.shared.status,
      render: (row) => <span className={statusClass(row.status)}>{row.status}</span>,
    },
  ];
}

export function InventoryCatalogScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;

  const [items, setItems] = useState<InventoryItem[]>([]);
  const [selectedItem, setSelectedItem] = useState<InventoryItem | undefined>();
  const [status, setStatus] = useState("active");
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);

  const canUseScope = Boolean(tenantId && laboratoryId && branchId);

  const listAction = useAsyncAction(async () => {
    const loaded = await listInventoryItems(tenantId ?? "", laboratoryId ?? "", branchId ?? "");
    setItems(loaded);
    return loaded;
  });

  const registerAction = useAsyncAction(
    async (fields: {
      itemCode: string;
      itemName: string;
      itemType: string;
      classification: string;
      unitOfMeasure: string;
    }) => {
      const created = await registerInventoryItem({
        tenantId: tenantId ?? "",
        laboratoryId: laboratoryId ?? "",
        branchId: branchId ?? "",
        actorId,
        ...fields,
      });
      setItems((current) => [created, ...current]);
      return created;
    },
  );

  const updateAction = useAsyncAction(async () => {
    if (!selectedItem) {
      throw new Error(labels.shared.selectFirst);
    }
    const updated = await updateInventoryItem(selectedItem.inventoryItemId, {
      itemName: selectedItem.itemName,
      itemType: selectedItem.itemType,
      classification: selectedItem.classification,
      unitOfMeasure: selectedItem.unitOfMeasure,
      status,
      actorId,
    });
    setItems((current) =>
      current.map((item) => (item.inventoryItemId === updated.inventoryItemId ? updated : item)),
    );
    setSelectedItem(updated);
    return updated;
  });

  const discontinueAction = useAsyncAction(async () => {
    const updated = await discontinueInventoryItem(selectedItem?.inventoryItemId ?? "", actorId);
    setItems((current) =>
      current.map((item) => (item.inventoryItemId === updated.inventoryItemId ? updated : item)),
    );
    setSelectedItem(updated);
    return updated;
  });

  return (
    <section aria-labelledby="inventory-catalog-heading">
      <h2 id="inventory-catalog-heading">{labels.inventoryCatalog.heading}</h2>
      <p>{labels.inventoryCatalog.description}</p>
      <ScopeIndicator />
      {!canUseScope ? (
        <p className="status-banner status-banner--error">{labels.shared.branchRequired}</p>
      ) : null}

      <CreateItemForm
        labels={labels}
        disabled={!canUseScope}
        status={registerAction.status}
        errorMessage={registerAction.errorMessage}
        onSubmit={(fields) => registerAction.run(fields)}
      />

      <button
        type="button"
        disabled={!canUseScope || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        {labels.inventoryCatalog.loadItems}
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && items.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.inventoryCatalog.heading}
        columns={itemColumns(labels)}
        rows={items}
        rowKey={(row) => row.inventoryItemId}
        onSelectRow={setSelectedItem}
      />

      <UpdateItemPanel
        labels={labels}
        selectedItem={selectedItem}
        status={status}
        actorId={actorId}
        onStatusChange={setStatus}
        onActorIdChange={setActorId}
        updateAction={updateAction}
        discontinueAction={discontinueAction}
      />
    </section>
  );
}
