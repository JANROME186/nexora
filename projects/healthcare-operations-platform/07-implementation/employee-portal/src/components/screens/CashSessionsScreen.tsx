import { useState, type FormEvent } from "react";
import { closeCashSession, listCashSessions, openCashSession } from "../../api/cashSalesApi";
import { formatMoney } from "../../api/money";
import type { CashSession } from "../../api/types";
import { MESSAGES } from "../../i18n/messages";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

function sessionStatusClass(status: string) {
  return `catalog-status catalog-status--${status.toLowerCase()}`;
}

/**
 * BCM-ATT-005 employee portal surface: cash session management (SCR-CASH-001).
 * Supports opening a new cash session, listing sessions by tenant and closing an
 * open session with counted amount and optional variance reason (RN-004).
 */
export function CashSessionsScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [sessions, setSessions] = useState<CashSession[]>([]);
  const [selected, setSelected] = useState<CashSession | undefined>(undefined);

  // -- Open session form state -------------------------------------------------------------------
  const [openedBy, setOpenedBy] = useState("");
  const [openingAmount, setOpeningAmount] = useState("");
  const [currency, setCurrency] = useState("USD");

  // -- Close session form state ------------------------------------------------------------------
  const [countedAmount, setCountedAmount] = useState("");
  const [closeCurrency, setCloseCurrency] = useState("");
  const [varianceReason, setVarianceReason] = useState("");
  const [confirmingClose, setConfirmingClose] = useState(false);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before listing cash sessions.");
    const loaded = await listCashSessions(tenantId);
    setSessions(loaded);
    return loaded;
  });

  const openAction = useAsyncAction(async () => {
    if (!tenantId || !laboratoryId || !branchId) {
      throw new Error("Select tenant, laboratory and branch scope before opening a cash session.");
    }
    const created = await openCashSession({
      tenantId,
      laboratoryId,
      branchId,
      openedBy,
      openingAmount: Number(openingAmount),
      currency: currency || undefined,
    });
    setSessions((current) => [created, ...current]);
    setSelected(created);
    setOpenedBy("");
    setOpeningAmount("");
    return created;
  });

  const closeAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectCashSessionFirst);
    const updated = await closeCashSession(selected.sessionId, {
      countedAmount: Number(countedAmount),
      currency: closeCurrency || undefined,
      varianceReason: varianceReason || undefined,
    });
    applyUpdated(updated);
    setCountedAmount("");
    setCloseCurrency("");
    setVarianceReason("");
    return updated;
  });

  function applyUpdated(updated: CashSession) {
    setSelected(updated);
    setSessions((current) => current.map((s) => (s.sessionId === updated.sessionId ? updated : s)));
  }

  function selectSession(session: CashSession) {
    setSelected(session);
    setCountedAmount("");
    setCloseCurrency(session.openingAmount.currency);
    setVarianceReason("");
    closeAction.reset();
  }

  async function handleOpen(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await openAction.run();
  }

  async function handleList() {
    await listAction.run();
  }

  return (
    <section aria-labelledby="cash-sessions-heading">
      <h2 id="cash-sessions-heading">Cash Sessions</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before opening a cash session.
        </p>
      ) : null}

      <div className="panel">
        <h3>Open cash session</h3>
        <form onSubmit={handleOpen}>
          <label htmlFor="session-opened-by">Opened by (cashier id)</label>
          <input
            id="session-opened-by"
            value={openedBy}
            onChange={(event) => setOpenedBy(event.target.value)}
            required
          />
          <label htmlFor="session-opening-amount">Opening amount</label>
          <input
            id="session-opening-amount"
            type="number"
            min={0}
            step="0.01"
            value={openingAmount}
            onChange={(event) => setOpeningAmount(event.target.value)}
            required
          />
          <label htmlFor="session-currency">Currency</label>
          <input
            id="session-currency"
            value={currency}
            onChange={(event) => setCurrency(event.target.value)}
            required
          />
          <button type="submit" disabled={!canUse || openAction.status === "loading"}>
            Open session
          </button>
          <StatusBanner
            status={openAction.status}
            errorMessage={openAction.errorMessage}
            successMessage="Cash session opened."
          />
        </form>
      </div>

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={handleList}
      >
        Load sessions
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Sessions loaded."
      />

      {listAction.status === "success" && sessions.length === 0 ? (
        <p className="empty-state">No cash sessions exist yet for this tenant.</p>
      ) : null}

      {sessions.length > 0 ? (
        <table>
          <caption>Cash sessions</caption>
          <thead>
            <tr>
              <th scope="col">Id</th>
              <th scope="col">Opened by</th>
              <th scope="col">Opening amount</th>
              <th scope="col">Status</th>
            </tr>
          </thead>
          <tbody>
            {sessions.map((session) => (
              <tr key={session.sessionId}>
                <td>
                  <button
                    type="button"
                    className="link-button"
                    onClick={() => selectSession(session)}
                  >
                    {session.sessionId}
                  </button>
                </td>
                <td>{session.openedBy}</td>
                <td>{formatMoney(session.openingAmount)}</td>
                <td>
                  <span className={sessionStatusClass(session.status)}>{session.status}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selected ? (
        <div className="panel">
          <h3>Session detail: {selected.sessionId}</h3>
          <table>
            <tbody>
              <tr>
                <th scope="row">Status</th>
                <td>
                  <span className={sessionStatusClass(selected.status)}>{selected.status}</span>
                </td>
              </tr>
              <tr>
                <th scope="row">Opened by</th>
                <td>{selected.openedBy}</td>
              </tr>
              <tr>
                <th scope="row">Opening amount</th>
                <td>{formatMoney(selected.openingAmount)}</td>
              </tr>
              <tr>
                <th scope="row">Expected amount</th>
                <td>{formatMoney(selected.expectedAmount)}</td>
              </tr>
              {selected.countedAmount ? (
                <tr>
                  <th scope="row">Counted amount</th>
                  <td>{formatMoney(selected.countedAmount)}</td>
                </tr>
              ) : null}
              {selected.varianceAmount ? (
                <tr>
                  <th scope="row">Variance</th>
                  <td>
                    {formatMoney(selected.varianceAmount)}
                    {selected.varianceReason ? ` — ${selected.varianceReason}` : ""}
                  </td>
                </tr>
              ) : null}
              {selected.openedAt ? (
                <tr>
                  <th scope="row">Opened at</th>
                  <td>{selected.openedAt}</td>
                </tr>
              ) : null}
              {selected.closedAt ? (
                <tr>
                  <th scope="row">Closed at</th>
                  <td>{selected.closedAt}</td>
                </tr>
              ) : null}
            </tbody>
          </table>

          {selected.status === "open" ? (
            <form
              onSubmit={(event) => {
                event.preventDefault();
                setConfirmingClose(true);
              }}
            >
              <h4>Close session</h4>
              <label htmlFor="session-counted-amount">Counted amount</label>
              <input
                id="session-counted-amount"
                type="number"
                min={0}
                step="0.01"
                value={countedAmount}
                onChange={(event) => setCountedAmount(event.target.value)}
                required
              />
              <label htmlFor="session-close-currency">
                Currency (optional, defaults to session currency)
              </label>
              <input
                id="session-close-currency"
                value={closeCurrency}
                onChange={(event) => setCloseCurrency(event.target.value)}
              />
              <label htmlFor="session-variance-reason">
                Variance reason (required if counted amount differs from expected amount — RN-004)
              </label>
              <input
                id="session-variance-reason"
                value={varianceReason}
                onChange={(event) => setVarianceReason(event.target.value)}
              />
              <button type="submit" disabled={closeAction.status === "loading"}>
                Close session
              </button>
            </form>
          ) : null}
          {/* Rendered unconditionally so a success message stays visible after status flips to closed */}
          <StatusBanner
            status={closeAction.status}
            errorMessage={closeAction.errorMessage}
            successMessage="Cash session closed."
          />
        </div>
      ) : (
        <p className="empty-state">Select a session row to view its detail and close it.</p>
      )}

      <ConfirmDialog
        open={confirmingClose}
        title="Confirm session close"
        description="This cash session will be marked as closed. Continue?"
        onCancel={() => setConfirmingClose(false)}
        onConfirm={async () => {
          setConfirmingClose(false);
          await closeAction.run();
        }}
      />
    </section>
  );
}
