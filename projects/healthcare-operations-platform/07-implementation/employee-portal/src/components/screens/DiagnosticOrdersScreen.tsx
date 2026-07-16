import { useState, type FormEvent } from "react";
import type { AsyncActionState } from "../../state/useAsyncAction";
import {
  acceptDiagnosticOrder,
  cancelDiagnosticOrder,
  completeDiagnosticOrder,
  createDiagnosticOrder,
  listDiagnosticOrders,
  priceDiagnosticOrder,
} from "../../api/frontDeskApi";
import type { DiagnosticOrder, OrderLineRequest } from "../../api/types";
import { MESSAGES } from "../../i18n/messages";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

const MIN_CANCELLATION_OVERRIDE_JUSTIFICATION_LENGTH = 15;

function orderStatusClass(status: string) {
  return `catalog-status catalog-status--${status.toLowerCase()}`;
}

function formatMoney(money?: { currency: string; amount: number }) {
  if (!money) return "—";
  return `${money.currency} ${money.amount.toFixed(2)}`;
}

function newLine(): OrderLineRequest {
  return { testDefinitionId: "", catalogItemKind: "test", quantity: 1 };
}

interface OrderLineEditorProps {
  lines: OrderLineRequest[];
  onUpdateLine: (index: number, patch: Partial<OrderLineRequest>) => void;
  onRemoveLine: (index: number) => void;
  onAddLine: () => void;
}

function OrderLineEditor({ lines, onUpdateLine, onRemoveLine, onAddLine }: OrderLineEditorProps) {
  return (
    <>
      <h4>Order lines</h4>
      {lines.map((line, index) => (
        <div className="order-line-row" key={index}>
          <label htmlFor={`order-line-kind-${index}`}>Kind</label>
          <select
            id={`order-line-kind-${index}`}
            value={line.catalogItemKind}
            onChange={(event) => onUpdateLine(index, { catalogItemKind: event.target.value })}
          >
            <option value="test">Test</option>
            <option value="panel">Panel</option>
          </select>
          <label htmlFor={`order-line-test-id-${index}`}>Catalog item id</label>
          <input
            id={`order-line-test-id-${index}`}
            value={line.testDefinitionId}
            onChange={(event) => onUpdateLine(index, { testDefinitionId: event.target.value })}
            required
          />
          <label htmlFor={`order-line-quantity-${index}`}>Quantity</label>
          <input
            id={`order-line-quantity-${index}`}
            type="number"
            min={1}
            value={line.quantity ?? 1}
            onChange={(event) => onUpdateLine(index, { quantity: Number(event.target.value) })}
          />
          {lines.length > 1 ? (
            <button type="button" onClick={() => onRemoveLine(index)}>
              Remove line
            </button>
          ) : null}
        </div>
      ))}
      <button type="button" onClick={onAddLine}>
        Add line
      </button>
    </>
  );
}

interface OrderLifecycleActionsProps {
  order: DiagnosticOrder;
  priceAction: AsyncActionState<DiagnosticOrder>;
  onPrice: () => void;
  acceptAction: AsyncActionState<DiagnosticOrder>;
  clinicalNotes: string;
  onClinicalNotesChange: (value: string) => void;
  onAccept: (event: FormEvent<HTMLFormElement>) => void;
  completeAction: AsyncActionState<DiagnosticOrder>;
  onComplete: () => void;
  cancelAction: AsyncActionState<DiagnosticOrder>;
  cancelReason: string;
  onCancelReasonChange: (value: string) => void;
  overrideJustification: string;
  onOverrideJustificationChange: (value: string) => void;
  onRequestCancel: (event: FormEvent<HTMLFormElement>) => void;
}

function OrderLifecycleActions({
  order,
  priceAction,
  onPrice,
  acceptAction,
  clinicalNotes,
  onClinicalNotesChange,
  onAccept,
  completeAction,
  onComplete,
  cancelAction,
  cancelReason,
  onCancelReasonChange,
  overrideJustification,
  onOverrideJustificationChange,
  onRequestCancel,
}: OrderLifecycleActionsProps) {
  const clinicallyEngaged = order.status === "accepted" || order.status === "in_progress";
  const isOpen = order.status !== "cancelled" && order.status !== "completed";

  return (
    <>
      <h4>Actions</h4>
      {/* Every StatusBanner below is rendered unconditionally (not gated by the order-status
          check that shows its control) so a success message stays visible after the action's own
          success flips order.status and hides the button/form that triggered it. */}
      {order.status === "draft" ? (
        <button type="button" disabled={priceAction.status === "loading"} onClick={onPrice}>
          Price order
        </button>
      ) : null}
      <StatusBanner
        status={priceAction.status}
        errorMessage={priceAction.errorMessage}
        successMessage="Order priced."
      />

      {order.status === "priced" ? (
        <form onSubmit={onAccept}>
          <label htmlFor="order-clinical-notes">Clinical notes (optional)</label>
          <input
            id="order-clinical-notes"
            value={clinicalNotes}
            onChange={(event) => onClinicalNotesChange(event.target.value)}
          />
          <button type="submit" disabled={acceptAction.status === "loading"}>
            Accept order
          </button>
        </form>
      ) : null}
      <StatusBanner
        status={acceptAction.status}
        errorMessage={acceptAction.errorMessage}
        successMessage="Order accepted."
      />

      {clinicallyEngaged ? (
        <button type="button" disabled={completeAction.status === "loading"} onClick={onComplete}>
          Complete order
        </button>
      ) : null}
      <StatusBanner
        status={completeAction.status}
        errorMessage={completeAction.errorMessage}
        successMessage="Order completed."
      />

      {isOpen ? (
        <form onSubmit={onRequestCancel}>
          <h4>Cancel order</h4>
          <label htmlFor="order-cancel-reason">Reason code</label>
          <input
            id="order-cancel-reason"
            value={cancelReason}
            onChange={(event) => onCancelReasonChange(event.target.value)}
            required
          />
          {clinicallyEngaged ? (
            <>
              <label htmlFor="order-cancel-override">
                Override justification (required, at least{" "}
                {MIN_CANCELLATION_OVERRIDE_JUSTIFICATION_LENGTH} characters, for an accepted or
                in-progress order)
              </label>
              <input
                id="order-cancel-override"
                value={overrideJustification}
                onChange={(event) => onOverrideJustificationChange(event.target.value)}
              />
            </>
          ) : null}
          <button type="submit" disabled={cancelAction.status === "loading"}>
            Cancel order
          </button>
        </form>
      ) : null}
      <StatusBanner
        status={cancelAction.status}
        errorMessage={cancelAction.errorMessage}
        successMessage="Order cancelled."
      />
    </>
  );
}

/**
 * BCM-LAB-001 employee portal surface: diagnostic order creation (SCR-ORD-001-01) supporting both
 * walk-in and scheduled/admission/quotation-linked intake channels, an order list (SCR-ORD-001-02),
 * and order detail with immutable patient/doctor/branch/pricing snapshots plus lifecycle actions —
 * price, accept, cancel (with the tiered override justification RN-005 requires for clinically
 * engaged orders) and complete (SCR-ORD-001-03/04).
 */
export function DiagnosticOrdersScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [orders, setOrders] = useState<DiagnosticOrder[]>([]);
  const [selected, setSelected] = useState<DiagnosticOrder | undefined>(undefined);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before listing diagnostic orders.");
    const loaded = await listDiagnosticOrders(tenantId);
    setOrders(loaded);
    return loaded;
  });

  const [intakeChannel, setIntakeChannel] = useState("walk_in");
  const [sourceReferenceId, setSourceReferenceId] = useState("");
  const [patientId, setPatientId] = useState("");
  const [doctorId, setDoctorId] = useState("");
  const [lines, setLines] = useState<OrderLineRequest[]>([newLine()]);

  const createAction = useAsyncAction(async () => {
    if (!tenantId || !laboratoryId || !branchId) {
      throw new Error(
        "Select tenant, laboratory and branch scope before creating a diagnostic order.",
      );
    }
    const created = await createDiagnosticOrder({
      tenantId,
      laboratoryId,
      branchId,
      intakeChannel,
      sourceReferenceId: sourceReferenceId || undefined,
      patientId,
      doctorId: doctorId || undefined,
      lines,
    });
    setOrders((current) => [created, ...current]);
    setSelected(created);
    setPatientId("");
    setDoctorId("");
    setSourceReferenceId("");
    setLines([newLine()]);
    return created;
  });

  const priceAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectOrderFirst);
    return priceDiagnosticOrder(selected.orderId);
  });

  const [clinicalNotes, setClinicalNotes] = useState("");
  const acceptAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectOrderFirst);
    return acceptDiagnosticOrder(selected.orderId, { clinicalNotes: clinicalNotes || undefined });
  });

  const [cancelReason, setCancelReason] = useState("");
  const [overrideJustification, setOverrideJustification] = useState("");
  const [confirmingCancel, setConfirmingCancel] = useState(false);
  const cancelAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectOrderFirst);
    return cancelDiagnosticOrder(selected.orderId, {
      reasonCode: cancelReason,
      overrideJustification: overrideJustification || undefined,
    });
  });

  const completeAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectOrderFirst);
    return completeDiagnosticOrder(selected.orderId);
  });

  function applyUpdated(updated: DiagnosticOrder) {
    setSelected(updated);
    setOrders((current) =>
      current.map((order) => (order.orderId === updated.orderId ? updated : order)),
    );
  }

  async function handleList() {
    await listAction.run();
  }

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await createAction.run();
  }

  function selectOrder(order: DiagnosticOrder) {
    setSelected(order);
    setClinicalNotes(order.clinicalNotes ?? "");
    setCancelReason("");
    setOverrideJustification("");
    priceAction.reset();
    acceptAction.reset();
    cancelAction.reset();
    completeAction.reset();
  }

  async function handlePrice() {
    const result = await priceAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleAccept(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await acceptAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleComplete() {
    const result = await completeAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  function updateLine(index: number, patch: Partial<OrderLineRequest>) {
    setLines((current) => current.map((line, i) => (i === index ? { ...line, ...patch } : line)));
  }

  function removeLine(index: number) {
    setLines((current) => current.filter((_, i) => i !== index));
  }

  return (
    <section aria-labelledby="orders-heading">
      <h2 id="orders-heading">Diagnostic Orders</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before creating diagnostic orders.
        </p>
      ) : null}

      <div className="panel">
        <h3>Create order</h3>
        <form onSubmit={handleCreate}>
          <label htmlFor="order-intake-channel">Intake channel</label>
          <select
            id="order-intake-channel"
            value={intakeChannel}
            onChange={(event) => setIntakeChannel(event.target.value)}
          >
            <option value="walk_in">Walk in</option>
            <option value="appointment">Appointment (scheduled)</option>
            <option value="admission">Admission</option>
            <option value="quotation_conversion">Quotation conversion</option>
          </select>
          <label htmlFor="order-source-reference-id">
            Source reference id (appointment, admission or quotation id, when applicable)
          </label>
          <input
            id="order-source-reference-id"
            value={sourceReferenceId}
            onChange={(event) => setSourceReferenceId(event.target.value)}
          />
          <label htmlFor="order-patient-id">Patient id</label>
          <input
            id="order-patient-id"
            value={patientId}
            onChange={(event) => setPatientId(event.target.value)}
            required
          />
          <label htmlFor="order-doctor-id">Referring doctor id (optional)</label>
          <input
            id="order-doctor-id"
            value={doctorId}
            onChange={(event) => setDoctorId(event.target.value)}
          />

          <OrderLineEditor
            lines={lines}
            onUpdateLine={updateLine}
            onRemoveLine={removeLine}
            onAddLine={() => setLines((current) => [...current, newLine()])}
          />

          <button type="submit" disabled={!canUse || createAction.status === "loading"}>
            Create order
          </button>
          <StatusBanner
            status={createAction.status}
            errorMessage={createAction.errorMessage}
            successMessage="Diagnostic order created (draft)."
          />
        </form>
      </div>

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={handleList}
      >
        Load orders
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Orders loaded."
      />

      {listAction.status === "success" && orders.length === 0 ? (
        <p className="empty-state">No diagnostic orders exist yet for this tenant.</p>
      ) : null}

      {orders.length > 0 ? (
        <table>
          <caption>Diagnostic orders</caption>
          <thead>
            <tr>
              <th scope="col">Id</th>
              <th scope="col">Patient</th>
              <th scope="col">Channel</th>
              <th scope="col">Status</th>
              <th scope="col">Total</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.orderId}>
                <td>
                  <button type="button" className="link-button" onClick={() => selectOrder(order)}>
                    {order.orderId}
                  </button>
                </td>
                <td>{order.patientSnapshot.fullName}</td>
                <td>{order.intakeChannel}</td>
                <td>
                  <span className={orderStatusClass(order.status)}>{order.status}</span>
                </td>
                <td>{formatMoney(order.pricingSnapshot?.totalAmount)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selected ? (
        <div className="panel">
          <h3>Order detail: {selected.orderId}</h3>
          <table>
            <tbody>
              <tr>
                <th scope="row">Status</th>
                <td>
                  <span className={orderStatusClass(selected.status)}>{selected.status}</span>
                </td>
              </tr>
              <tr>
                <th scope="row">Patient snapshot</th>
                <td>
                  {selected.patientSnapshot.fullName} ({selected.patientSnapshot.documentType}{" "}
                  {selected.patientSnapshot.documentNumberMasked})
                </td>
              </tr>
              <tr>
                <th scope="row">Doctor snapshot</th>
                <td>{selected.doctorSnapshot ? selected.doctorSnapshot.fullName : "None"}</td>
              </tr>
              <tr>
                <th scope="row">Branch snapshot</th>
                <td>{selected.branchSnapshot.name}</td>
              </tr>
              <tr>
                <th scope="row">Pricing snapshot</th>
                <td>
                  {selected.pricingSnapshot
                    ? `${formatMoney(selected.pricingSnapshot.totalAmount)} (price list ${selected.pricingSnapshot.priceListId} v${selected.pricingSnapshot.priceListVersion})`
                    : "Not priced yet"}
                </td>
              </tr>
            </tbody>
          </table>

          <OrderLifecycleActions
            order={selected}
            priceAction={priceAction}
            onPrice={handlePrice}
            acceptAction={acceptAction}
            clinicalNotes={clinicalNotes}
            onClinicalNotesChange={setClinicalNotes}
            onAccept={handleAccept}
            completeAction={completeAction}
            onComplete={handleComplete}
            cancelAction={cancelAction}
            cancelReason={cancelReason}
            onCancelReasonChange={setCancelReason}
            overrideJustification={overrideJustification}
            onOverrideJustificationChange={setOverrideJustification}
            onRequestCancel={(event) => {
              event.preventDefault();
              setConfirmingCancel(true);
            }}
          />
        </div>
      ) : (
        <p className="empty-state">Select an order row to view its detail and take action.</p>
      )}

      <ConfirmDialog
        open={confirmingCancel}
        title="Confirm cancellation"
        description="This diagnostic order will be marked as cancelled and can no longer be priced, accepted or completed. Continue?"
        onCancel={() => setConfirmingCancel(false)}
        onConfirm={async () => {
          setConfirmingCancel(false);
          const result = await cancelAction.run();
          if (result.ok) applyUpdated(result.data);
        }}
      />
    </section>
  );
}
