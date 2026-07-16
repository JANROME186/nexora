import { useState, type FormEvent } from "react";
import {
  cancelBillingRequest,
  createBillingRequest,
  listBillingRequests,
  listTaxLines,
  retryBillingRequest,
  submitBillingRequest,
} from "../../api/cashSalesApi";
import { formatMoney } from "../../api/money";
import type { InvoiceRequest, TaxLine } from "../../api/types";
import { MESSAGES } from "../../i18n/messages";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

function billingStatusClass(status: string) {
  return `catalog-status catalog-status--${status.toLowerCase()}`;
}

/**
 * BCM-ATT-008 employee portal surface: billing request worklist, fiscal profile
 * capture and adapter response detail (SCR-BILL-001/002/003). Supports creating a
 * billing request from a paid sale, viewing tax lines and executing submit, retry
 * and cancel against the fiscal adapter boundary implemented in MVP-MOD-005-BE-002.
 */
export function BillingRequestsScreen() {
  const { scope } = useAdminScope();
  const { tenantId } = scope;

  const [requests, setRequests] = useState<InvoiceRequest[]>([]);
  const [selected, setSelected] = useState<InvoiceRequest | undefined>(undefined);
  const [taxLines, setTaxLines] = useState<TaxLine[]>([]);

  // -- Create billing request form state -----------------------------------------------------------
  const [saleId, setSaleId] = useState("");
  const [legalName, setLegalName] = useState("");
  const [taxIdentifier, setTaxIdentifier] = useState("");
  const [fiscalAddress, setFiscalAddress] = useState("");
  const [fiscalRegime, setFiscalRegime] = useState("");
  const [taxCode, setTaxCode] = useState("");
  const [taxRate, setTaxRate] = useState("");
  const [actorId, setActorId] = useState("");

  const [confirmingSubmit, setConfirmingSubmit] = useState(false);
  const [confirmingRetry, setConfirmingRetry] = useState(false);
  const [confirmingCancel, setConfirmingCancel] = useState(false);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before listing billing requests.");
    const loaded = await listBillingRequests(tenantId);
    setRequests(loaded);
    return loaded;
  });

  const taxLinesAction = useAsyncAction(async (invoiceRequestId: string) => {
    const loaded = await listTaxLines(invoiceRequestId);
    setTaxLines(loaded);
    return loaded;
  });

  const createAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before creating a billing request.");
    const created = await createBillingRequest({
      saleId,
      legalName,
      taxIdentifier,
      fiscalAddress,
      fiscalRegime: fiscalRegime || undefined,
      taxCode: taxCode || undefined,
      taxRate: taxRate ? Number(taxRate) : undefined,
      actorId: actorId || undefined,
    });
    setRequests((current) => [created, ...current]);
    selectRequest(created);
    setSaleId("");
    setLegalName("");
    setTaxIdentifier("");
    setFiscalAddress("");
    setFiscalRegime("");
    setTaxCode("");
    setTaxRate("");
    setActorId("");
    return created;
  });

  const submitAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectBillingRequestFirst);
    return submitBillingRequest(selected.invoiceRequestId);
  });

  const retryAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectBillingRequestFirst);
    return retryBillingRequest(selected.invoiceRequestId);
  });

  const cancelAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectBillingRequestFirst);
    return cancelBillingRequest(selected.invoiceRequestId);
  });

  function applyUpdated(updated: InvoiceRequest) {
    setSelected(updated);
    setRequests((current) =>
      current.map((request) =>
        request.invoiceRequestId === updated.invoiceRequestId ? updated : request,
      ),
    );
  }

  function selectRequest(request: InvoiceRequest) {
    setSelected(request);
    submitAction.reset();
    retryAction.reset();
    cancelAction.reset();
    taxLinesAction.run(request.invoiceRequestId);
  }

  async function handleList() {
    await listAction.run();
  }

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await createAction.run();
  }

  async function handleSubmit() {
    const result = await submitAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleRetry() {
    const result = await retryAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleCancel() {
    const result = await cancelAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  const canSubmit = selected?.status === "requested";
  const canRetry = selected?.status === "submitted" || selected?.status === "failed";
  const canCancel =
    Boolean(selected) && selected?.status !== "issued" && selected?.status !== "cancelled";

  return (
    <section aria-labelledby="billing-requests-heading">
      <h2 id="billing-requests-heading">Billing Requests</h2>
      <ScopeIndicator />

      <div className="panel">
        <h3>Create billing request from a paid sale</h3>
        <form onSubmit={handleCreate}>
          <label htmlFor="billing-sale-id">Sale id</label>
          <input
            id="billing-sale-id"
            value={saleId}
            onChange={(event) => setSaleId(event.target.value)}
            required
          />
          <label htmlFor="billing-legal-name">Fiscal legal name</label>
          <input
            id="billing-legal-name"
            value={legalName}
            onChange={(event) => setLegalName(event.target.value)}
            required
          />
          <label htmlFor="billing-tax-identifier">Tax identifier</label>
          <input
            id="billing-tax-identifier"
            value={taxIdentifier}
            onChange={(event) => setTaxIdentifier(event.target.value)}
            required
          />
          <label htmlFor="billing-fiscal-address">Fiscal address</label>
          <input
            id="billing-fiscal-address"
            value={fiscalAddress}
            onChange={(event) => setFiscalAddress(event.target.value)}
            required
          />
          <label htmlFor="billing-fiscal-regime">Fiscal regime (optional)</label>
          <input
            id="billing-fiscal-regime"
            value={fiscalRegime}
            onChange={(event) => setFiscalRegime(event.target.value)}
          />
          <label htmlFor="billing-tax-code">Tax code (optional)</label>
          <input
            id="billing-tax-code"
            value={taxCode}
            onChange={(event) => setTaxCode(event.target.value)}
          />
          <label htmlFor="billing-tax-rate">Tax rate percent (optional)</label>
          <input
            id="billing-tax-rate"
            type="number"
            min={0}
            step="0.01"
            value={taxRate}
            onChange={(event) => setTaxRate(event.target.value)}
          />
          <label htmlFor="billing-actor-id">Actor id (optional)</label>
          <input
            id="billing-actor-id"
            value={actorId}
            onChange={(event) => setActorId(event.target.value)}
          />
          <button type="submit" disabled={createAction.status === "loading"}>
            Create billing request
          </button>
          <StatusBanner
            status={createAction.status}
            errorMessage={createAction.errorMessage}
            successMessage="Billing request created."
          />
        </form>
      </div>

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={handleList}
      >
        Load billing requests
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Billing requests loaded."
      />

      {listAction.status === "success" && requests.length === 0 ? (
        <p className="empty-state">No billing requests exist yet for this tenant.</p>
      ) : null}

      {requests.length > 0 ? (
        <table>
          <caption>Billing requests</caption>
          <thead>
            <tr>
              <th scope="col">Id</th>
              <th scope="col">Sale id</th>
              <th scope="col">Legal name</th>
              <th scope="col">Status</th>
            </tr>
          </thead>
          <tbody>
            {requests.map((request) => (
              <tr key={request.invoiceRequestId}>
                <td>
                  <button
                    type="button"
                    className="link-button"
                    onClick={() => selectRequest(request)}
                  >
                    {request.invoiceRequestId}
                  </button>
                </td>
                <td>{request.saleId}</td>
                <td>{request.fiscalProfileSnapshot.legalName}</td>
                <td>
                  <span className={billingStatusClass(request.status)}>{request.status}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selected ? (
        <div className="panel">
          <h3>Billing request detail: {selected.invoiceRequestId}</h3>
          <table>
            <tbody>
              <tr>
                <th scope="row">Status</th>
                <td>
                  <span className={billingStatusClass(selected.status)}>{selected.status}</span>
                </td>
              </tr>
              <tr>
                <th scope="row">Sale id</th>
                <td>{selected.saleId}</td>
              </tr>
              <tr>
                <th scope="row">Fiscal profile</th>
                <td>
                  {selected.fiscalProfileSnapshot.legalName} —{" "}
                  {selected.fiscalProfileSnapshot.taxIdentifier} —{" "}
                  {selected.fiscalProfileSnapshot.fiscalAddress}
                  {selected.fiscalProfileSnapshot.fiscalRegime
                    ? ` (${selected.fiscalProfileSnapshot.fiscalRegime})`
                    : ""}
                </td>
              </tr>
              <tr>
                <th scope="row">Adapter correlation id</th>
                <td>{selected.adapterCorrelationId ?? "—"}</td>
              </tr>
              {selected.adapterResponseSnapshot ? (
                <tr>
                  <th scope="row">Adapter response snapshot</th>
                  <td>
                    <pre>{selected.adapterResponseSnapshot}</pre>
                  </td>
                </tr>
              ) : null}
            </tbody>
          </table>

          <h4>Tax lines</h4>
          <StatusBanner status={taxLinesAction.status} errorMessage={taxLinesAction.errorMessage} />
          {taxLinesAction.status === "success" && taxLines.length === 0 ? (
            <p className="empty-state">This billing request has no tax lines.</p>
          ) : null}
          {taxLines.length > 0 ? (
            <table>
              <caption>Tax lines</caption>
              <thead>
                <tr>
                  <th scope="col">Tax code</th>
                  <th scope="col">Tax rate</th>
                  <th scope="col">Base amount</th>
                  <th scope="col">Tax amount</th>
                </tr>
              </thead>
              <tbody>
                {taxLines.map((taxLine) => (
                  <tr key={taxLine.taxLineId}>
                    <td>{taxLine.taxCode}</td>
                    <td>{taxLine.taxRate}%</td>
                    <td>{formatMoney(taxLine.baseAmount)}</td>
                    <td>{formatMoney(taxLine.taxAmount)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : null}

          <h4>Actions</h4>
          {canSubmit ? (
            <button
              type="button"
              disabled={submitAction.status === "loading"}
              onClick={() => setConfirmingSubmit(true)}
            >
              Submit
            </button>
          ) : null}
          <StatusBanner
            status={submitAction.status}
            errorMessage={submitAction.errorMessage}
            successMessage="Billing request submitted."
          />

          {canRetry ? (
            <button
              type="button"
              disabled={retryAction.status === "loading"}
              onClick={() => setConfirmingRetry(true)}
            >
              Retry
            </button>
          ) : null}
          <StatusBanner
            status={retryAction.status}
            errorMessage={retryAction.errorMessage}
            successMessage="Billing request retried."
          />

          {canCancel ? (
            <button
              type="button"
              disabled={cancelAction.status === "loading"}
              onClick={() => setConfirmingCancel(true)}
            >
              Cancel
            </button>
          ) : null}
          <StatusBanner
            status={cancelAction.status}
            errorMessage={cancelAction.errorMessage}
            successMessage="Billing request cancelled."
          />
        </div>
      ) : (
        <p className="empty-state">
          Select a billing request row to view its detail and take action.
        </p>
      )}

      <ConfirmDialog
        open={confirmingSubmit}
        title="Confirm submission"
        description="This billing request will be submitted to the fiscal adapter. Continue?"
        onCancel={() => setConfirmingSubmit(false)}
        onConfirm={async () => {
          setConfirmingSubmit(false);
          await handleSubmit();
        }}
      />
      <ConfirmDialog
        open={confirmingRetry}
        title="Confirm retry"
        description="This billing request will be resubmitted to the fiscal adapter. Continue?"
        onCancel={() => setConfirmingRetry(false)}
        onConfirm={async () => {
          setConfirmingRetry(false);
          await handleRetry();
        }}
      />
      <ConfirmDialog
        open={confirmingCancel}
        title="Confirm cancellation"
        description="This billing request will be marked as cancelled and can no longer be submitted or retried. Continue?"
        onCancel={() => setConfirmingCancel(false)}
        onConfirm={async () => {
          setConfirmingCancel(false);
          await handleCancel();
        }}
      />
    </section>
  );
}
