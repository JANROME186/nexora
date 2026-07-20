/**
 * Purchase order administration screen (COM-MOD-010-FE-001, BCM-INV-004).
 *
 * Creates purchase orders with lines, submits/approves/cancels them and receives individual
 * lines, backed by PurchaseOrderController. The receive-line action is served across the module
 * boundary by StockEntryService (BCM-INV-005) but exposed through the same controller endpoint.
 * Decomposed into small sub-components (TD-FE-010) so no single function exceeds the ESLint
 * function-size/complexity thresholds.
 */
import { useState } from "react";
import {
  approvePurchaseOrder,
  cancelPurchaseOrder,
  createPurchaseOrder,
  listPurchaseOrders,
  receivePurchaseOrderLine,
  submitPurchaseOrder,
} from "../../api/inventoryQualityApi";
import type { PurchaseOrder, PurchaseOrderLineRequest } from "../../api/types";
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
type ActionHandle = { status: AsyncStatus; errorMessage?: string; run: () => void };
type ActionState = { status: AsyncStatus; errorMessage?: string };

interface LineBuilderProps {
  labels: Labels;
  lines: PurchaseOrderLineRequest[];
  onAddLine: (line: PurchaseOrderLineRequest) => void;
}

function LineBuilder({ labels, lines, onAddLine }: LineBuilderProps) {
  const [lineItemId, setLineItemId] = useState("");
  const [orderedQuantity, setOrderedQuantity] = useState("");
  const [unitCost, setUnitCost] = useState("");

  return (
    <div>
      <label htmlFor="po-line-item-id">{labels.inventoryProcurement.lineInventoryItemId}</label>
      <input
        id="po-line-item-id"
        value={lineItemId}
        onChange={(e) => setLineItemId(e.target.value)}
      />
      <label htmlFor="po-line-qty">{labels.inventoryProcurement.lineOrderedQuantity}</label>
      <input
        id="po-line-qty"
        value={orderedQuantity}
        onChange={(e) => setOrderedQuantity(e.target.value)}
      />
      <label htmlFor="po-line-cost">{labels.inventoryProcurement.lineUnitCost}</label>
      <input id="po-line-cost" value={unitCost} onChange={(e) => setUnitCost(e.target.value)} />
      <button
        type="button"
        disabled={!lineItemId || !orderedQuantity || !unitCost}
        onClick={() => {
          onAddLine({ inventoryItemId: lineItemId, orderedQuantity, unitCost });
          setLineItemId("");
          setOrderedQuantity("");
          setUnitCost("");
        }}
      >
        {labels.shared.addLine}
      </button>
      {lines.length > 0 ? (
        <ul>
          {lines.map((line, index) => (
            <li key={`${line.inventoryItemId}-${index}`}>
              {line.inventoryItemId}: {line.orderedQuantity} x {line.unitCost}
            </li>
          ))}
        </ul>
      ) : (
        <p className="empty-state">{labels.inventoryProcurement.linesPending}</p>
      )}
    </div>
  );
}

interface CreateOrderPanelProps {
  labels: Labels;
  disabled: boolean;
  createAction: ActionState;
  onCreate: (fields: {
    supplierId: string;
    supplierName: string;
    currencyCode: string;
    lines: PurchaseOrderLineRequest[];
    actorId: string;
  }) => void;
}

function CreateOrderPanel({ labels, disabled, createAction, onCreate }: CreateOrderPanelProps) {
  const [supplierId, setSupplierId] = useState("");
  const [supplierName, setSupplierName] = useState("");
  const [currencyCode, setCurrencyCode] = useState("MXN");
  const [lines, setLines] = useState<PurchaseOrderLineRequest[]>([]);
  const [actorId, setActorId] = useState(DEFAULT_ACTOR_ID);

  return (
    <div className="panel">
      <h3>{labels.shared.create}</h3>
      <label htmlFor="po-supplier-id">{labels.inventoryProcurement.supplierId}</label>
      <input
        id="po-supplier-id"
        value={supplierId}
        onChange={(e) => setSupplierId(e.target.value)}
      />
      <label htmlFor="po-supplier-name">{labels.inventoryProcurement.supplierName}</label>
      <input
        id="po-supplier-name"
        value={supplierName}
        onChange={(e) => setSupplierName(e.target.value)}
      />
      <label htmlFor="po-currency">{labels.inventoryProcurement.currencyCode}</label>
      <input
        id="po-currency"
        value={currencyCode}
        onChange={(e) => setCurrencyCode(e.target.value)}
      />
      <LineBuilder
        labels={labels}
        lines={lines}
        onAddLine={(line) => setLines((c) => [...c, line])}
      />
      <label htmlFor="po-actor-id">{labels.shared.actorId}</label>
      <input id="po-actor-id" value={actorId} onChange={(e) => setActorId(e.target.value)} />
      <button
        type="button"
        disabled={
          disabled || !supplierId || lines.length === 0 || createAction.status === "loading"
        }
        onClick={() => {
          onCreate({ supplierId, supplierName, currencyCode, lines, actorId });
          setLines([]);
        }}
      >
        {labels.shared.create}
      </button>
      <StatusBanner
        status={createAction.status}
        errorMessage={createAction.errorMessage}
        successMessage={labels.inventoryProcurement.createSuccess}
      />
    </div>
  );
}

interface OrderActionsPanelProps {
  labels: Labels;
  selectedOrder?: PurchaseOrder;
  submitAction: ActionHandle;
  approveAction: ActionHandle;
  cancelAction: ActionState;
  receiveAction: ActionState;
  onCancel: (reason: string) => void;
  onReceive: (lineId: string, receivedQuantity: string) => void;
}

function OrderActionsPanel({
  labels,
  selectedOrder,
  submitAction,
  approveAction,
  cancelAction,
  receiveAction,
  onCancel,
  onReceive,
}: OrderActionsPanelProps) {
  const [cancellationReason, setCancellationReason] = useState("");
  const [lineId, setLineId] = useState("");
  const [receivedQuantity, setReceivedQuantity] = useState("");

  return (
    <div className="panel">
      <h3>{selectedOrder?.purchaseOrderId ?? labels.shared.selectFirst}</h3>
      <div className="catalog-toolbar">
        <button type="button" disabled={!selectedOrder} onClick={() => submitAction.run()}>
          {labels.inventoryProcurement.submit}
        </button>
        <button type="button" disabled={!selectedOrder} onClick={() => approveAction.run()}>
          {labels.inventoryProcurement.approve}
        </button>
      </div>
      <label htmlFor="po-cancel-reason">{labels.inventoryProcurement.cancellationReason}</label>
      <input
        id="po-cancel-reason"
        value={cancellationReason}
        onChange={(e) => setCancellationReason(e.target.value)}
      />
      <button
        type="button"
        disabled={!selectedOrder || !cancellationReason}
        onClick={() => onCancel(cancellationReason)}
      >
        {labels.inventoryProcurement.cancel}
      </button>
      <StatusBanner
        status={submitAction.status}
        errorMessage={submitAction.errorMessage}
        successMessage={labels.inventoryProcurement.submitSuccess}
      />
      <StatusBanner
        status={approveAction.status}
        errorMessage={approveAction.errorMessage}
        successMessage={labels.inventoryProcurement.approveSuccess}
      />
      <StatusBanner
        status={cancelAction.status}
        errorMessage={cancelAction.errorMessage}
        successMessage={labels.inventoryProcurement.cancelSuccess}
      />

      <label htmlFor="po-line-id">{labels.inventoryProcurement.lineId}</label>
      <input id="po-line-id" value={lineId} onChange={(e) => setLineId(e.target.value)} />
      <label htmlFor="po-received-qty">{labels.inventoryLots.receivedQuantity}</label>
      <input
        id="po-received-qty"
        value={receivedQuantity}
        onChange={(e) => setReceivedQuantity(e.target.value)}
      />
      <button
        type="button"
        disabled={!selectedOrder || !lineId || !receivedQuantity}
        onClick={() => onReceive(lineId, receivedQuantity)}
      >
        {labels.inventoryProcurement.receiveLine}
      </button>
      <StatusBanner
        status={receiveAction.status}
        errorMessage={receiveAction.errorMessage}
        successMessage={labels.inventoryProcurement.receiveSuccess}
      />
    </div>
  );
}

function orderColumns(labels: Labels): DataTableColumn<PurchaseOrder>[] {
  return [
    {
      key: "id",
      header: labels.inventoryProcurement.purchaseOrderId,
      render: (row) => row.purchaseOrderId,
    },
    {
      key: "supplier",
      header: labels.inventoryProcurement.supplierName,
      render: (row) => row.supplierName,
    },
    {
      key: "total",
      header: labels.inventoryProcurement.totalAmount,
      render: (row) => row.totalAmount,
    },
    {
      key: "status",
      header: labels.shared.status,
      render: (row) => <span className={statusClass(row.status)}>{row.status}</span>,
    },
  ];
}

export function InventoryProcurementScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;

  const [orders, setOrders] = useState<PurchaseOrder[]>([]);
  const [selectedOrder, setSelectedOrder] = useState<PurchaseOrder | undefined>();
  const [pendingActorId] = useState(DEFAULT_ACTOR_ID);

  const canUseScope = Boolean(tenantId && laboratoryId && branchId);

  const createAction = useAsyncAction(
    async (fields: {
      supplierId: string;
      supplierName: string;
      currencyCode: string;
      lines: PurchaseOrderLineRequest[];
      actorId: string;
    }) => {
      const created = await createPurchaseOrder({
        tenantId: tenantId ?? "",
        laboratoryId: laboratoryId ?? "",
        branchId: branchId ?? "",
        ...fields,
      });
      setOrders((current) => [created, ...current]);
      return created;
    },
  );

  const listAction = useAsyncAction(async () => {
    const loaded = await listPurchaseOrders(tenantId ?? "", laboratoryId ?? "", branchId ?? "");
    setOrders(loaded);
    return loaded;
  });

  function updateOrder(updated: PurchaseOrder) {
    setOrders((current) =>
      current.map((o) => (o.purchaseOrderId === updated.purchaseOrderId ? updated : o)),
    );
    setSelectedOrder(updated);
  }

  const submitAction = useAsyncAction(async () => {
    const updated = await submitPurchaseOrder(selectedOrder?.purchaseOrderId ?? "", pendingActorId);
    updateOrder(updated);
    return updated;
  });

  const approveAction = useAsyncAction(async () => {
    const updated = await approvePurchaseOrder(selectedOrder?.purchaseOrderId ?? "", {
      actorId: pendingActorId,
    });
    updateOrder(updated);
    return updated;
  });

  const cancelAction = useAsyncAction(async (reason: string) => {
    const updated = await cancelPurchaseOrder(selectedOrder?.purchaseOrderId ?? "", {
      reason,
      actorId: pendingActorId,
    });
    updateOrder(updated);
    return updated;
  });

  const receiveAction = useAsyncAction(async (lineId: string, receivedQuantity: string) => {
    const updated = await receivePurchaseOrderLine(selectedOrder?.purchaseOrderId ?? "", lineId, {
      receivedQuantity,
      actorId: pendingActorId,
    });
    updateOrder(updated);
    return updated;
  });

  return (
    <section aria-labelledby="inventory-procurement-heading">
      <h2 id="inventory-procurement-heading">{labels.inventoryProcurement.heading}</h2>
      <p>{labels.inventoryProcurement.description}</p>
      <ScopeIndicator />
      {!canUseScope ? (
        <p className="status-banner status-banner--error">{labels.shared.branchRequired}</p>
      ) : null}

      <CreateOrderPanel
        labels={labels}
        disabled={!canUseScope}
        createAction={createAction}
        onCreate={(fields) => createAction.run(fields)}
      />

      <button
        type="button"
        disabled={!canUseScope || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        {labels.inventoryProcurement.loadOrders}
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && orders.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.inventoryProcurement.heading}
        columns={orderColumns(labels)}
        rows={orders}
        rowKey={(row) => row.purchaseOrderId}
        onSelectRow={setSelectedOrder}
      />

      <OrderActionsPanel
        labels={labels}
        selectedOrder={selectedOrder}
        submitAction={submitAction}
        approveAction={approveAction}
        cancelAction={cancelAction}
        receiveAction={receiveAction}
        onCancel={(reason) => cancelAction.run(reason)}
        onReceive={(lineId, receivedQuantity) => receiveAction.run(lineId, receivedQuantity)}
      />
    </section>
  );
}
