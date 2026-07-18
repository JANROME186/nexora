/**
 * Critical Result Escalation Worklist screen (MVP-MOD-007-FE-001, BCM-RES-006 Critical Results).
 *
 * Implements:
 *   SCR-CRR-006-01 Critical Result Escalation Worklist  (/results/critical-escalations)
 *   SCR-CRR-006-02 Acknowledge Critical Result          (/results/critical-escalations/{id}/acknowledge)
 *
 * Also exposes escalate and close actions matching the controller endpoints from
 * CriticalResultEscalationController (POST /escalate and POST /close).
 *
 * The close action enforces the terminal-state guard (both acknowledgedBy and acknowledgedAt
 * must be recorded — BCM-RES-006 business rule RN-003); the backend raises
 * ESCALATION_ACKNOWLEDGEMENT_INCOMPLETE if the guard is not satisfied.
 */
import { useState } from "react";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import {
  listOpenEscalations,
  acknowledgeCriticalEscalation,
  escalateCriticalEscalation,
  closeCriticalEscalation,
} from "../../api/resultsDeliveryApi";
import { MESSAGES } from "../../i18n/messages";
import type { CriticalResultEscalation } from "../../api/types";

const ACTOR_ID = "current_user";

function escalationStatusClass(status: string): string {
  const s = status.toUpperCase();
  if (s === "OPEN") return "catalog-status catalog-status--captured";
  if (s === "ACKNOWLEDGED") return "catalog-status catalog-status--released";
  if (s === "ESCALATED") return "catalog-status catalog-status--critical";
  if (s === "CLOSED") return "catalog-status catalog-status--deprecated";
  return "catalog-status";
}

/** DeadlineCountdown: shows time remaining until acknowledgement deadline. */
function DeadlineCountdown({ deadline }: { deadline: string }) {
  const ms = new Date(deadline).getTime() - Date.now();
  const hours = Math.max(0, Math.floor(ms / 3_600_000));
  const mins = Math.max(0, Math.floor((ms % 3_600_000) / 60_000));
  const overdue = ms < 0;
  return (
    <span
      className={overdue ? "catalog-status catalog-status--critical" : undefined}
      aria-label={overdue ? "Deadline overdue" : `${hours}h ${mins}m remaining`}
    >
      {overdue ? "OVERDUE" : `${hours}h ${mins}m`}
    </span>
  );
}

interface ActionState {
  status: AsyncStatus;
  errorMessage?: string;
}

interface EscalationDetailPanelProps {
  escalation: CriticalResultEscalation;
  acknowledgeUserId: string;
  onAcknowledgeUserIdChange: (value: string) => void;
  onAcknowledge: () => void;
  onEscalate: () => void;
  onClose: () => void;
  acknowledgeAction: ActionState;
  escalateAction: ActionState;
  closeAction: ActionState;
}

function EscalationDetailPanel({
  escalation,
  acknowledgeUserId,
  onAcknowledgeUserIdChange,
  onAcknowledge,
  onEscalate,
  onClose,
  acknowledgeAction,
  escalateAction,
  closeAction,
}: EscalationDetailPanelProps) {
  return (
    <div className="panel">
      <h3>Escalation Detail: {escalation.escalationId}</h3>
      <table>
        <tbody>
          <tr>
            <th scope="row">Result ID</th>
            <td>{escalation.resultId}</td>
          </tr>
          <tr>
            <th scope="row">Critical Reason</th>
            <td>{escalation.criticalReason}</td>
          </tr>
          <tr>
            <th scope="row">Escalation Tier</th>
            <td>{escalation.escalationTier}</td>
          </tr>
          <tr>
            <th scope="row">Status</th>
            <td>
              <span className={escalationStatusClass(escalation.status)}>{escalation.status}</span>
            </td>
          </tr>
          <tr>
            <th scope="row">Deadline</th>
            <td>
              {new Date(escalation.acknowledgementDeadline).toLocaleString()}
              {" — "}
              <DeadlineCountdown deadline={escalation.acknowledgementDeadline} />
            </td>
          </tr>
          {escalation.acknowledgedBy ? (
            <tr>
              <th scope="row">Acknowledged By</th>
              <td>
                {escalation.acknowledgedBy} at{" "}
                {escalation.acknowledgedAt
                  ? new Date(escalation.acknowledgedAt).toLocaleString()
                  : "—"}
              </td>
            </tr>
          ) : null}
        </tbody>
      </table>

      {escalation.status === "OPEN" || escalation.status === "ESCALATED" ? (
        <div className="panel" style={{ marginTop: "1rem" }}>
          <h4>Acknowledge</h4>
          <label htmlFor="ack-user-id">Acknowledging User ID</label>
          <input
            id="ack-user-id"
            type="text"
            value={acknowledgeUserId}
            onChange={(e) => onAcknowledgeUserIdChange(e.target.value)}
            placeholder="User ID"
          />
          <button
            type="button"
            disabled={acknowledgeAction.status === "loading"}
            onClick={onAcknowledge}
            style={{ marginLeft: "0.5rem" }}
          >
            Acknowledge
          </button>
        </div>
      ) : null}
      <StatusBanner
        status={acknowledgeAction.status}
        errorMessage={acknowledgeAction.errorMessage}
        successMessage={MESSAGES.escalationAcknowledged}
      />

      {escalation.status === "OPEN" || escalation.status === "ACKNOWLEDGED" ? (
        <div className="panel" style={{ marginTop: "1rem" }}>
          <h4>Escalate to Next Tier</h4>
          <button type="button" disabled={escalateAction.status === "loading"} onClick={onEscalate}>
            Escalate
          </button>
        </div>
      ) : null}
      <StatusBanner
        status={escalateAction.status}
        errorMessage={escalateAction.errorMessage}
        successMessage={MESSAGES.escalationEscalated}
      />

      {escalation.status === "ACKNOWLEDGED" ? (
        <div className="panel" style={{ marginTop: "1rem" }}>
          <h4>Close Escalation</h4>
          <p>
            Closing requires that both acknowledgedBy and acknowledgedAt are recorded (BCM-RES-006
            rule RN-003). The backend will reject this action if the acknowledgement is incomplete.
          </p>
          <button type="button" disabled={closeAction.status === "loading"} onClick={onClose}>
            Close Escalation
          </button>
        </div>
      ) : null}
      <StatusBanner
        status={closeAction.status}
        errorMessage={closeAction.errorMessage}
        successMessage={MESSAGES.escalationClosed}
      />
    </div>
  );
}

interface EscalationsTableProps {
  escalations: CriticalResultEscalation[];
  onSelect: (escalation: CriticalResultEscalation) => void;
}

function EscalationsTable({ escalations, onSelect }: EscalationsTableProps) {
  return (
    <table>
      <caption>Open Critical Escalations</caption>
      <thead>
        <tr>
          <th scope="col">Escalation ID</th>
          <th scope="col">Result ID</th>
          <th scope="col">Reason</th>
          <th scope="col">Tier</th>
          <th scope="col">Deadline</th>
          <th scope="col">Status</th>
        </tr>
      </thead>
      <tbody>
        {escalations.map((esc) => (
          <tr key={esc.escalationId}>
            <td>
              <button type="button" className="link-button" onClick={() => onSelect(esc)}>
                {esc.escalationId}
              </button>
            </td>
            <td>{esc.resultId}</td>
            <td>{esc.criticalReason}</td>
            <td>
              <span className="catalog-status">Tier {esc.escalationTier}</span>
            </td>
            <td>
              <DeadlineCountdown deadline={esc.acknowledgementDeadline} />
            </td>
            <td>
              <span className={escalationStatusClass(esc.status)}>{esc.status}</span>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export function CriticalEscalationsScreen() {
  const { scope } = useAdminScope();
  const { tenantId } = scope;

  const [escalations, setEscalations] = useState<CriticalResultEscalation[]>([]);
  const [selected, setSelected] = useState<CriticalResultEscalation | undefined>(undefined);
  const [acknowledgeUserId, setAcknowledgeUserId] = useState("");

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error(MESSAGES.selectEscalationFirst);
    const loaded = await listOpenEscalations(tenantId);
    setEscalations(loaded);
    return loaded;
  });

  const acknowledgeAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectEscalationFirst);
    const updated = await acknowledgeCriticalEscalation(
      selected.escalationId,
      acknowledgeUserId || ACTOR_ID,
      ACTOR_ID,
    );
    updateEscalationInList(updated);
    return updated;
  });

  const escalateAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectEscalationFirst);
    const updated = await escalateCriticalEscalation(selected.escalationId, ACTOR_ID);
    updateEscalationInList(updated);
    return updated;
  });

  const closeAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectEscalationFirst);
    const updated = await closeCriticalEscalation(selected.escalationId, ACTOR_ID);
    updateEscalationInList(updated);
    return updated;
  });

  function updateEscalationInList(updated: CriticalResultEscalation) {
    setEscalations((prev) =>
      prev.map((e) => (e.escalationId === updated.escalationId ? updated : e)),
    );
    setSelected(updated);
  }

  function selectEscalation(esc: CriticalResultEscalation) {
    setSelected(esc);
    setAcknowledgeUserId("");
    acknowledgeAction.reset();
    escalateAction.reset();
    closeAction.reset();
  }

  async function handleLoadEscalations() {
    await listAction.run();
  }

  return (
    <section aria-labelledby="critical-escalations-heading">
      <h2 id="critical-escalations-heading">Critical Result Escalation Worklist</h2>
      <ScopeIndicator />
      {!tenantId && (
        <p className="status-banner status-banner--error">
          Select a tenant to view critical escalations.
        </p>
      )}

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={handleLoadEscalations}
      >
        Load Open Escalations
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Escalations loaded."
      />

      {listAction.status === "success" && escalations.length === 0 ? (
        <p className="empty-state">{MESSAGES.noEscalationsOpen}</p>
      ) : null}

      {escalations.length > 0 ? (
        <EscalationsTable escalations={escalations} onSelect={selectEscalation} />
      ) : null}

      {selected ? (
        <EscalationDetailPanel
          escalation={selected}
          acknowledgeUserId={acknowledgeUserId}
          onAcknowledgeUserIdChange={setAcknowledgeUserId}
          onAcknowledge={() => acknowledgeAction.run()}
          onEscalate={() => escalateAction.run()}
          onClose={() => closeAction.run()}
          acknowledgeAction={acknowledgeAction}
          escalateAction={escalateAction}
          closeAction={closeAction}
        />
      ) : (
        <p className="empty-state">Select an escalation to manage it.</p>
      )}
    </section>
  );
}
