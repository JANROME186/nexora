import { useState, type FormEvent } from "react";
import {
  cancelSale,
  createSale,
  getSale,
  listSaleLines,
  listSalePayments,
  listSales,
  registerPayment,
} from "../../api/cashSalesApi";
import { formatMoney } from "../../api/money";
import type { PaymentAllocation, Sale, SaleLine } from "../../api/types";
import { MESSAGES } from "../../i18n/messages";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

const TERMINAL_SALE_STATUSES = new Set(["cancelled", "refunded"]);

function saleStatusClass(status: string) {
  return `catalog-status catalog-status--${status.toLowerCase()}`;
}

/**
 * BCM-ATT-005 employee portal surface: sale worklist and payment registration
 * (SCR-CASH-002, SCR-CASH-003). Supports creating a sale from an accepted diagnostic
 * order or quotation, listing sales, viewing totals/outstanding balance/payment
 * status, registering payment allocations and cancelling a non-paid sale.
 */
export function SalesScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [sales, setSales] = useState<Sale[]>([]);
  const [selected, setSelected] = useState<Sale | undefined>(undefined);
  const [lines, setLines] = useState<SaleLine[]>([]);
  const [payments, setPayments] = useState<PaymentAllocation[]>([]);

  // -- Create sale form state ---------------------------------------------------------------------
  const [sourceType, setSourceType] = useState<"diagnostic_order" | "quotation">(
    "diagnostic_order",
  );
  const [sourceReferenceId, setSourceReferenceId] = useState("");
  const [actorId, setActorId] = useState("");

  // -- Register payment form state ----------------------------------------------------------------
  const [paymentAmount, setPaymentAmount] = useState("");
  const [paymentCurrency, setPaymentCurrency] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("cash");
  const [paymentSessionId, setPaymentSessionId] = useState("");
  const [paymentReference, setPaymentReference] = useState("");
  const [registeredBy, setRegisteredBy] = useState("");

  // -- Cancel sale form state ----------------------------------------------------------------------
  const [cancelReason, setCancelReason] = useState("");
  const [confirmingCancel, setConfirmingCancel] = useState(false);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before listing sales.");
    const loaded = await listSales(tenantId);
    setSales(loaded);
    return loaded;
  });

  const linesAction = useAsyncAction(async (saleId: string) => {
    const loaded = await listSaleLines(saleId);
    setLines(loaded);
    return loaded;
  });

  const paymentsAction = useAsyncAction(async (saleId: string) => {
    const loaded = await listSalePayments(saleId);
    setPayments(loaded);
    return loaded;
  });

  const createAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before creating a sale.");
    const created = await createSale({
      tenantId,
      sourceType,
      sourceReferenceId,
      actorId: actorId || undefined,
    });
    setSales((current) => [created, ...current]);
    selectSale(created);
    setSourceReferenceId("");
    setActorId("");
    return created;
  });

  const registerPaymentAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectSaleFirst);
    await registerPayment(selected.saleId, {
      amount: Number(paymentAmount),
      currency: paymentCurrency || undefined,
      method: paymentMethod,
      sessionId: paymentSessionId || undefined,
      reference: paymentReference || undefined,
      registeredBy,
    });
    const [updatedSale, updatedPayments] = await Promise.all([
      getSale(selected.saleId),
      listSalePayments(selected.saleId),
    ]);
    applyUpdated(updatedSale);
    setPayments(updatedPayments);
    setPaymentAmount("");
    setPaymentReference("");
    return updatedSale;
  });

  const cancelAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectSaleFirst);
    const updated = await cancelSale(selected.saleId, { reasonCode: cancelReason });
    applyUpdated(updated);
    setCancelReason("");
    return updated;
  });

  function applyUpdated(updated: Sale) {
    setSelected(updated);
    setSales((current) => current.map((sale) => (sale.saleId === updated.saleId ? updated : sale)));
  }

  function selectSale(sale: Sale) {
    setSelected(sale);
    setPaymentAmount("");
    setPaymentCurrency(sale.totals.totalAmount.currency);
    setPaymentSessionId("");
    setPaymentReference("");
    setRegisteredBy("");
    setCancelReason("");
    registerPaymentAction.reset();
    cancelAction.reset();
    linesAction.run(sale.saleId);
    paymentsAction.run(sale.saleId);
  }

  async function handleList() {
    await listAction.run();
  }

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await createAction.run();
  }

  async function handleRegisterPayment(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await registerPaymentAction.run();
  }

  const canCancel =
    Boolean(selected) &&
    !TERMINAL_SALE_STATUSES.has(selected?.status ?? "") &&
    selected?.status !== "paid";
  const canPay =
    Boolean(selected) && (selected?.status === "payable" || selected?.status === "partially_paid");

  return (
    <section aria-labelledby="sales-heading">
      <h2 id="sales-heading">Sales</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before creating a sale.
        </p>
      ) : null}

      <div className="panel">
        <h3>Create sale from accepted order or quotation</h3>
        <form onSubmit={handleCreate}>
          <label htmlFor="sale-source-type">Source</label>
          <select
            id="sale-source-type"
            value={sourceType}
            onChange={(event) =>
              setSourceType(event.target.value as "diagnostic_order" | "quotation")
            }
          >
            <option value="diagnostic_order">Accepted diagnostic order</option>
            <option value="quotation">Accepted quotation</option>
          </select>
          <label htmlFor="sale-source-reference-id">Source reference id</label>
          <input
            id="sale-source-reference-id"
            value={sourceReferenceId}
            onChange={(event) => setSourceReferenceId(event.target.value)}
            required
          />
          <label htmlFor="sale-actor-id">Actor id (optional)</label>
          <input
            id="sale-actor-id"
            value={actorId}
            onChange={(event) => setActorId(event.target.value)}
          />
          <button type="submit" disabled={!canUse || createAction.status === "loading"}>
            Create sale
          </button>
          <StatusBanner
            status={createAction.status}
            errorMessage={createAction.errorMessage}
            successMessage="Sale created."
          />
        </form>
      </div>

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={handleList}
      >
        Load sales
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Sales loaded."
      />

      {listAction.status === "success" && sales.length === 0 ? (
        <p className="empty-state">No sales exist yet for this tenant.</p>
      ) : null}

      {sales.length > 0 ? (
        <table>
          <caption>Sales</caption>
          <thead>
            <tr>
              <th scope="col">Id</th>
              <th scope="col">Source</th>
              <th scope="col">Total</th>
              <th scope="col">Outstanding</th>
              <th scope="col">Status</th>
            </tr>
          </thead>
          <tbody>
            {sales.map((sale) => (
              <tr key={sale.saleId}>
                <td>
                  <button type="button" className="link-button" onClick={() => selectSale(sale)}>
                    {sale.saleId}
                  </button>
                </td>
                <td>
                  {sale.sourceType}: {sale.sourceReferenceId}
                </td>
                <td>{formatMoney(sale.totals.totalAmount)}</td>
                <td>{formatMoney(sale.totals.outstandingAmount)}</td>
                <td>
                  <span className={saleStatusClass(sale.status)}>{sale.status}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selected ? (
        <div className="panel">
          <h3>Sale detail: {selected.saleId}</h3>
          <table>
            <tbody>
              <tr>
                <th scope="row">Status</th>
                <td>
                  <span className={saleStatusClass(selected.status)}>{selected.status}</span>
                </td>
              </tr>
              <tr>
                <th scope="row">Source</th>
                <td>
                  {selected.sourceType}: {selected.sourceReferenceId}
                </td>
              </tr>
              <tr>
                <th scope="row">Patient id</th>
                <td>{selected.patientId}</td>
              </tr>
              <tr>
                <th scope="row">Subtotal</th>
                <td>{formatMoney(selected.totals.subtotalAmount)}</td>
              </tr>
              <tr>
                <th scope="row">Discount</th>
                <td>{formatMoney(selected.totals.discountAmount)}</td>
              </tr>
              <tr>
                <th scope="row">Total</th>
                <td>{formatMoney(selected.totals.totalAmount)}</td>
              </tr>
              <tr>
                <th scope="row">Paid</th>
                <td>{formatMoney(selected.totals.paidAmount)}</td>
              </tr>
              <tr>
                <th scope="row">Outstanding balance</th>
                <td>{formatMoney(selected.totals.outstandingAmount)}</td>
              </tr>
              {selected.cancellationReason ? (
                <tr>
                  <th scope="row">Cancellation reason</th>
                  <td>{selected.cancellationReason}</td>
                </tr>
              ) : null}
            </tbody>
          </table>

          {selected.status === "paid" ? (
            <p className="empty-state">
              This sale is fully paid. Use this Sale Id in the Billing Requests screen to create a
              billing request.
            </p>
          ) : null}

          <h4>Sale lines</h4>
          <StatusBanner status={linesAction.status} errorMessage={linesAction.errorMessage} />
          {linesAction.status === "success" && lines.length === 0 ? (
            <p className="empty-state">This sale has no lines.</p>
          ) : null}
          {lines.length > 0 ? (
            <table>
              <caption>Sale lines</caption>
              <thead>
                <tr>
                  <th scope="col">Catalog item</th>
                  <th scope="col">Description</th>
                  <th scope="col">Quantity</th>
                  <th scope="col">Unit amount</th>
                  <th scope="col">Line total</th>
                </tr>
              </thead>
              <tbody>
                {lines.map((line) => (
                  <tr key={line.saleLineId}>
                    <td>
                      {line.catalogItemKind}: {line.catalogItemId}
                    </td>
                    <td>{line.descriptionSnapshot}</td>
                    <td>{line.quantity}</td>
                    <td>{formatMoney(line.unitAmount)}</td>
                    <td>{formatMoney(line.lineTotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : null}

          <h4>Payments</h4>
          <StatusBanner status={paymentsAction.status} errorMessage={paymentsAction.errorMessage} />
          {paymentsAction.status === "success" && payments.length === 0 ? (
            <p className="empty-state">No payments have been registered for this sale.</p>
          ) : null}
          {payments.length > 0 ? (
            <table>
              <caption>Payments</caption>
              <thead>
                <tr>
                  <th scope="col">Id</th>
                  <th scope="col">Amount</th>
                  <th scope="col">Method</th>
                  <th scope="col">Reference</th>
                  <th scope="col">Registered by</th>
                </tr>
              </thead>
              <tbody>
                {payments.map((payment) => (
                  <tr key={payment.paymentId}>
                    <td>{payment.paymentId}</td>
                    <td>{formatMoney(payment.amount)}</td>
                    <td>{payment.method}</td>
                    <td>{payment.reference ?? "—"}</td>
                    <td>{payment.registeredBy}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : null}

          {canPay ? (
            <form onSubmit={handleRegisterPayment}>
              <h4>Register payment</h4>
              <label htmlFor="payment-amount">Amount</label>
              <input
                id="payment-amount"
                type="number"
                min={0}
                step="0.01"
                value={paymentAmount}
                onChange={(event) => setPaymentAmount(event.target.value)}
                required
              />
              <label htmlFor="payment-currency">
                Currency (optional, defaults to sale currency)
              </label>
              <input
                id="payment-currency"
                value={paymentCurrency}
                onChange={(event) => setPaymentCurrency(event.target.value)}
              />
              <label htmlFor="payment-method">Method</label>
              <select
                id="payment-method"
                value={paymentMethod}
                onChange={(event) => setPaymentMethod(event.target.value)}
              >
                <option value="cash">Cash</option>
                <option value="card">Card</option>
                <option value="transfer">Transfer</option>
                <option value="wallet">Wallet</option>
                <option value="mixed">Mixed</option>
              </select>
              <label htmlFor="payment-session-id">
                Cash session id (required for cash payments; defaults to the branch&apos;s open
                session)
              </label>
              <input
                id="payment-session-id"
                value={paymentSessionId}
                onChange={(event) => setPaymentSessionId(event.target.value)}
              />
              <label htmlFor="payment-reference">Reference (optional)</label>
              <input
                id="payment-reference"
                value={paymentReference}
                onChange={(event) => setPaymentReference(event.target.value)}
              />
              <label htmlFor="payment-registered-by">Registered by (cashier id)</label>
              <input
                id="payment-registered-by"
                value={registeredBy}
                onChange={(event) => setRegisteredBy(event.target.value)}
                required
              />
              <button type="submit" disabled={registerPaymentAction.status === "loading"}>
                Register payment
              </button>
            </form>
          ) : null}
          <StatusBanner
            status={registerPaymentAction.status}
            errorMessage={registerPaymentAction.errorMessage}
            successMessage="Payment registered."
          />

          {canCancel ? (
            <form
              onSubmit={(event) => {
                event.preventDefault();
                setConfirmingCancel(true);
              }}
            >
              <h4>Cancel sale</h4>
              <label htmlFor="sale-cancel-reason">Reason code</label>
              <input
                id="sale-cancel-reason"
                value={cancelReason}
                onChange={(event) => setCancelReason(event.target.value)}
                required
              />
              <button type="submit" disabled={cancelAction.status === "loading"}>
                Cancel sale
              </button>
            </form>
          ) : null}
          <StatusBanner
            status={cancelAction.status}
            errorMessage={cancelAction.errorMessage}
            successMessage="Sale cancelled."
          />
        </div>
      ) : (
        <p className="empty-state">Select a sale row to view its detail and take action.</p>
      )}

      <ConfirmDialog
        open={confirmingCancel}
        title="Confirm sale cancellation"
        description="This sale will be marked as cancelled and can no longer receive payments. Continue?"
        onCancel={() => setConfirmingCancel(false)}
        onConfirm={async () => {
          setConfirmingCancel(false);
          await cancelAction.run();
        }}
      />
    </section>
  );
}
