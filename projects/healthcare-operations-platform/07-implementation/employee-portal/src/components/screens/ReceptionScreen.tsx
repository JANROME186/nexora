import { useState, type FormEvent } from "react";
import {
  abandonReceptionVisit,
  advanceReceptionToAdmission,
  confirmReceptionIdentity,
  listReceptionVisits,
  startReceptionVisit,
  updateReceptionPriority,
} from "../../api/frontDeskApi";
import type { ReceptionVisit } from "../../api/types";
import { MESSAGES } from "../../i18n/messages";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

function queueStatusClass(status: string) {
  return `catalog-status catalog-status--${status.toLowerCase()}`;
}

/**
 * BCM-ATT-003 employee portal surface: the front desk worklist (SCR-REC-003-01) that lists
 * reception visits in the tenant-configurable priority order the backend already returns, plus
 * identity confirmation (SCR-REC-003-02) and visit detail actions (SCR-REC-003-03). Supports both
 * walk-in and scheduled (linked-appointment) intake channels per RN of BCM-ATT-003.
 */
export function ReceptionScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [visits, setVisits] = useState<ReceptionVisit[]>([]);
  const [selected, setSelected] = useState<ReceptionVisit | undefined>(undefined);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before listing the front desk worklist.");
    const loaded = await listReceptionVisits(tenantId);
    setVisits(loaded);
    return loaded;
  });

  const [intakeChannel, setIntakeChannel] = useState("walk_in");
  const [patientId, setPatientId] = useState("");
  const [linkedAppointmentId, setLinkedAppointmentId] = useState("");

  const startAction = useAsyncAction(async () => {
    if (!tenantId || !laboratoryId || !branchId) {
      throw new Error(
        "Select tenant, laboratory and branch scope before starting a reception visit.",
      );
    }
    const started = await startReceptionVisit({
      tenantId,
      laboratoryId,
      branchId,
      patientId,
      linkedAppointmentId: linkedAppointmentId || undefined,
      intakeChannel,
    });
    setVisits((current) => [started, ...current]);
    setSelected(started);
    setPatientId("");
    setLinkedAppointmentId("");
    return started;
  });

  const [identityMethod, setIdentityMethod] = useState("document_check");
  const confirmIdentityAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectReceptionVisitFirst);
    return confirmReceptionIdentity(selected.visitId, {
      identityConfirmationMethod: identityMethod,
    });
  });

  const advanceAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectReceptionVisitFirst);
    return advanceReceptionToAdmission(selected.visitId);
  });

  const [priority, setPriority] = useState("normal");
  const priorityAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectReceptionVisitFirst);
    return updateReceptionPriority(selected.visitId, { priority });
  });

  const [confirmingAbandon, setConfirmingAbandon] = useState(false);
  const abandonAction = useAsyncAction(async () => {
    if (!selected) throw new Error(MESSAGES.selectReceptionVisitFirst);
    return abandonReceptionVisit(selected.visitId);
  });

  function applyUpdated(updated: ReceptionVisit) {
    setSelected(updated);
    setVisits((current) =>
      current.map((visit) => (visit.visitId === updated.visitId ? updated : visit)),
    );
  }

  async function handleList() {
    await listAction.run();
  }

  async function handleStart(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await startAction.run();
  }

  function selectVisit(visit: ReceptionVisit) {
    setSelected(visit);
    setPriority(visit.priority);
    confirmIdentityAction.reset();
    advanceAction.reset();
    priorityAction.reset();
    abandonAction.reset();
  }

  async function handleConfirmIdentity(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await confirmIdentityAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleAdvance() {
    const result = await advanceAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleUpdatePriority(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await priorityAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  return (
    <section aria-labelledby="reception-heading">
      <h2 id="reception-heading">Front Desk Worklist</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before starting reception visits.
        </p>
      ) : null}

      <div className="panel">
        <h3>Start reception visit</h3>
        <form onSubmit={handleStart}>
          <label htmlFor="reception-intake-channel">Intake channel</label>
          <select
            id="reception-intake-channel"
            value={intakeChannel}
            onChange={(event) => setIntakeChannel(event.target.value)}
          >
            <option value="walk_in">Walk in</option>
            <option value="scheduled">Scheduled</option>
          </select>
          <label htmlFor="reception-patient-id">Patient id</label>
          <input
            id="reception-patient-id"
            value={patientId}
            onChange={(event) => setPatientId(event.target.value)}
            required
          />
          <label htmlFor="reception-linked-appointment-id">
            Linked appointment id (required for the scheduled channel)
          </label>
          <input
            id="reception-linked-appointment-id"
            value={linkedAppointmentId}
            onChange={(event) => setLinkedAppointmentId(event.target.value)}
          />
          <button type="submit" disabled={!canUse || startAction.status === "loading"}>
            Start visit
          </button>
          <StatusBanner
            status={startAction.status}
            errorMessage={startAction.errorMessage}
            successMessage="Reception visit started."
          />
        </form>
      </div>

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={handleList}
      >
        Load worklist
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Worklist loaded."
      />

      {listAction.status === "success" && visits.length === 0 ? (
        <p className="empty-state">No reception visits are currently queued for this tenant.</p>
      ) : null}

      {visits.length > 0 ? (
        <table>
          <caption>Reception queue (priority order)</caption>
          <thead>
            <tr>
              <th scope="col">Id</th>
              <th scope="col">Patient</th>
              <th scope="col">Channel</th>
              <th scope="col">Identity</th>
              <th scope="col">Priority</th>
              <th scope="col">Queue status</th>
            </tr>
          </thead>
          <tbody>
            {visits.map((visit) => (
              <tr key={visit.visitId}>
                <td>
                  <button type="button" className="link-button" onClick={() => selectVisit(visit)}>
                    {visit.visitId}
                  </button>
                </td>
                <td>{visit.patientId}</td>
                <td>{visit.intakeChannel}</td>
                <td>{visit.identityConfirmed ? "Confirmed" : "Pending"}</td>
                <td>{visit.priority}</td>
                <td>
                  <span className={queueStatusClass(visit.queueStatus)}>{visit.queueStatus}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selected ? (
        <div className="panel">
          <h3>Visit detail: {selected.visitId}</h3>
          <table>
            <tbody>
              <tr>
                <th scope="row">Patient</th>
                <td>{selected.patientId}</td>
              </tr>
              <tr>
                <th scope="row">Channel</th>
                <td>{selected.intakeChannel}</td>
              </tr>
              <tr>
                <th scope="row">Identity confirmed</th>
                <td>{selected.identityConfirmed ? "Yes" : "No"}</td>
              </tr>
              <tr>
                <th scope="row">Queue status</th>
                <td>
                  <span className={queueStatusClass(selected.queueStatus)}>
                    {selected.queueStatus}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>

          {!selected.identityConfirmed ? (
            <form onSubmit={handleConfirmIdentity}>
              <h4>Confirm identity</h4>
              <label htmlFor="reception-identity-method">Identity confirmation method</label>
              <select
                id="reception-identity-method"
                value={identityMethod}
                onChange={(event) => setIdentityMethod(event.target.value)}
              >
                <option value="document_check">Document check</option>
                <option value="portal_handoff">Portal handoff</option>
                <option value="representative_verification">Representative verification</option>
              </select>
              <button type="submit" disabled={confirmIdentityAction.status === "loading"}>
                Confirm identity
              </button>
            </form>
          ) : null}
          {/* Rendered unconditionally (not gated by identityConfirmed) so the success banner
              remains visible after a successful confirmation flips identityConfirmed to true and
              hides the form above. */}
          <StatusBanner
            status={confirmIdentityAction.status}
            errorMessage={confirmIdentityAction.errorMessage}
            successMessage="Identity confirmed."
          />

          <form onSubmit={handleUpdatePriority}>
            <h4>Update priority</h4>
            <label htmlFor="reception-priority">Priority</label>
            <select
              id="reception-priority"
              value={priority}
              onChange={(event) => setPriority(event.target.value)}
            >
              <option value="normal">Normal</option>
              <option value="priority">Priority</option>
              <option value="urgent">Urgent</option>
            </select>
            <button type="submit" disabled={priorityAction.status === "loading"}>
              Update priority
            </button>
            <StatusBanner
              status={priorityAction.status}
              errorMessage={priorityAction.errorMessage}
              successMessage="Priority updated."
            />
          </form>

          <h4>Advance to admission</h4>
          <p className="field-hint">
            Requires identity confirmation first (RN-001, RN-003). Hands off to Admission Management
            without mutating any diagnostic order.
          </p>
          <button
            type="button"
            disabled={advanceAction.status === "loading"}
            onClick={handleAdvance}
          >
            Advance to admission
          </button>
          <StatusBanner
            status={advanceAction.status}
            errorMessage={advanceAction.errorMessage}
            successMessage="Visit advanced to admission."
          />

          <h4>Abandon visit</h4>
          <button type="button" onClick={() => setConfirmingAbandon(true)}>
            Abandon visit
          </button>
          <StatusBanner
            status={abandonAction.status}
            errorMessage={abandonAction.errorMessage}
            successMessage="Visit abandoned."
          />
        </div>
      ) : (
        <p className="empty-state">Select a visit row to view its detail and take action.</p>
      )}

      <ConfirmDialog
        open={confirmingAbandon}
        title="Confirm abandon"
        description="This reception visit will be marked as abandoned and removed from the active worklist. Continue?"
        onCancel={() => setConfirmingAbandon(false)}
        onConfirm={async () => {
          setConfirmingAbandon(false);
          const result = await abandonAction.run();
          if (result.ok) applyUpdated(result.data);
        }}
      />
    </section>
  );
}
