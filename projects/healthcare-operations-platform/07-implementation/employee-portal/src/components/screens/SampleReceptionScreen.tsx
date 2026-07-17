import { useState } from "react";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import {
  listReceptionWorklist,
  receiveSample,
  rejectSample,
  disposeSample,
} from "../../api/laboratoryOperationsApi";
import type { Sample } from "../../api/types";

export function SampleReceptionScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [samples, setSamples] = useState<Sample[]>([]);
  const [selected, setSelected] = useState<Sample | undefined>(undefined);

  const [conditionMet, setConditionMet] = useState(true);
  const [rejectReason, setRejectReason] = useState("");
  const [disposeReason, setDisposeReason] = useState("");

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant to list reception worklist.");
    const loaded = await listReceptionWorklist(tenantId);
    setSamples(loaded);
    return loaded;
  });

  const receiveAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a sample to receive.");
    const received = await receiveSample(selected.sampleId, { conditionCriteriaMet: conditionMet });
    setSamples((current) => current.map((s) => (s.sampleId === received.sampleId ? received : s)));
    setSelected(received);
    return received;
  });

  const rejectAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a sample to reject.");
    const rejected = await rejectSample(selected.sampleId, { reasonCode: rejectReason });
    setSamples((current) => current.map((s) => (s.sampleId === rejected.sampleId ? rejected : s)));
    setSelected(rejected);
    return rejected;
  });

  const disposeAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a sample to dispose.");
    const disposed = await disposeSample(selected.sampleId, { reasonCode: disposeReason });
    setSamples((current) => current.map((s) => (s.sampleId === disposed.sampleId ? disposed : s)));
    setSelected(disposed);
    return disposed;
  });

  async function handleList() {
    await listAction.run();
  }

  function selectSample(sample: Sample) {
    setSelected(sample);
    setConditionMet(true);
    setRejectReason("");
    setDisposeReason("");
    receiveAction.reset();
    rejectAction.reset();
    disposeAction.reset();
  }

  async function handleReceive(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await receiveAction.run();
  }

  async function handleReject(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await rejectAction.run();
  }

  async function handleDispose(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await disposeAction.run();
  }

  return (
    <section aria-labelledby="sample-reception-heading">
      <h2 id="sample-reception-heading">Sample Reception</h2>
      <ScopeIndicator />
      {!canUse && (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before receiving samples.
        </p>
      )}

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={handleList}
      >
        Load Worklist
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Worklist loaded."
      />

      {listAction.status === "success" && samples.length === 0 ? (
        <p className="empty-state">No samples pending reception for this tenant.</p>
      ) : null}

      {samples.length > 0 ? (
        <table>
          <caption>Reception Worklist</caption>
          <thead>
            <tr>
              <th scope="col">Sample Id</th>
              <th scope="col">Order Id</th>
              <th scope="col">Status</th>
            </tr>
          </thead>
          <tbody>
            {samples.map((sample) => (
              <tr key={sample.sampleId}>
                <td>
                  <button
                    type="button"
                    className="link-button"
                    onClick={() => selectSample(sample)}
                  >
                    {sample.sampleId}
                  </button>
                </td>
                <td>{sample.orderId}</td>
                <td>
                  <span className={`catalog-status catalog-status--${sample.status.toLowerCase()}`}>
                    {sample.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selected ? (
        <div className="panel">
          <h3>Sample detail: {selected.sampleId}</h3>
          <table>
            <tbody>
              <tr>
                <th scope="row">Status</th>
                <td>
                  <span
                    className={`catalog-status catalog-status--${selected.status.toLowerCase()}`}
                  >
                    {selected.status}
                  </span>
                </td>
              </tr>
              <tr>
                <th scope="row">Order Id</th>
                <td>{selected.orderId}</td>
              </tr>
              <tr>
                <th scope="row">Patient Id</th>
                <td>{selected.patientId}</td>
              </tr>
            </tbody>
          </table>

          {selected.status !== "received" &&
          selected.status !== "rejected" &&
          selected.status !== "disposed" ? (
            <>
              <form onSubmit={handleReceive} className="panel" style={{ marginTop: "1rem" }}>
                <h4>Receive Sample</h4>
                <label htmlFor="condition-met">
                  <input
                    id="condition-met"
                    type="checkbox"
                    checked={conditionMet}
                    onChange={(e) => setConditionMet(e.target.checked)}
                  />
                  Condition criteria met?
                </label>
                <button type="submit" disabled={receiveAction.status === "loading"}>
                  Receive Sample
                </button>
                <StatusBanner
                  status={receiveAction.status}
                  errorMessage={receiveAction.errorMessage}
                  successMessage="Sample received."
                />
              </form>

              <form onSubmit={handleReject} className="panel" style={{ marginTop: "1rem" }}>
                <h4>Reject Sample</h4>
                <label htmlFor="reject-reason">Reason Code</label>
                <input
                  id="reject-reason"
                  value={rejectReason}
                  onChange={(e) => setRejectReason(e.target.value)}
                  required
                />
                <button type="submit" disabled={rejectAction.status === "loading"}>
                  Reject Sample
                </button>
                <StatusBanner
                  status={rejectAction.status}
                  errorMessage={rejectAction.errorMessage}
                  successMessage="Sample rejected."
                />
              </form>
            </>
          ) : null}

          {selected.status === "rejected" || selected.status === "received" ? (
            <form onSubmit={handleDispose} className="panel" style={{ marginTop: "1rem" }}>
              <h4>Dispose Sample</h4>
              <label htmlFor="dispose-reason">Reason Code</label>
              <input
                id="dispose-reason"
                value={disposeReason}
                onChange={(e) => setDisposeReason(e.target.value)}
                required
              />
              <button type="submit" disabled={disposeAction.status === "loading"}>
                Dispose Sample
              </button>
              <StatusBanner
                status={disposeAction.status}
                errorMessage={disposeAction.errorMessage}
                successMessage="Sample disposed."
              />
            </form>
          ) : null}
        </div>
      ) : (
        <p className="empty-state">Select a sample to view details and perform actions.</p>
      )}
    </section>
  );
}
