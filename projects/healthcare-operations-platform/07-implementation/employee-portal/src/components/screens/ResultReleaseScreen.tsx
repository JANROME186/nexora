import { useState } from "react";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { listReleaseWorklist, releaseResult, amendResult } from "../../api/laboratoryOperationsApi";
import type { LaboratoryResult, ResultValue } from "../../api/types";

export function ResultReleaseScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [results, setResults] = useState<LaboratoryResult[]>([]);
  const [selected, setSelected] = useState<LaboratoryResult | undefined>(undefined);

  const [releaseNotes, setReleaseNotes] = useState("");

  const [amendReason, setAmendReason] = useState("");
  const [amendRawValue, setAmendRawValue] = useState("");

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant to list release worklist.");
    const loaded = await listReleaseWorklist(tenantId);
    setResults(loaded);
    return loaded;
  });

  const releaseAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a result to release.");
    const released = await releaseResult(selected.resultId, { notes: releaseNotes || undefined });
    setResults((current) => current.map((r) => (r.resultId === released.resultId ? released : r)));
    setSelected(released);
    return released;
  });

  const amendAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a result to amend.");
    const val: ResultValue = {
      rawValue: amendRawValue,
      capturedAt: new Date().toISOString(),
      capturedBy: "current_user",
    };
    const amended = await amendResult(selected.resultId, { reason: amendReason, newValues: [val] });
    setResults((current) => current.map((r) => (r.resultId === amended.resultId ? amended : r)));
    setSelected(amended);
    return amended;
  });

  async function handleList() {
    await listAction.run();
  }

  function selectResult(result: LaboratoryResult) {
    setSelected(result);
    setReleaseNotes("");
    setAmendReason("");
    setAmendRawValue("");
    releaseAction.reset();
    amendAction.reset();
  }

  async function handleRelease(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await releaseAction.run();
  }

  async function handleAmend(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await amendAction.run();
  }

  return (
    <section aria-labelledby="result-release-heading">
      <h2 id="result-release-heading">Result Release</h2>
      <ScopeIndicator />
      {!canUse && (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before releasing results.
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

      {listAction.status === "success" && results.length === 0 ? (
        <p className="empty-state">No results pending release for this tenant.</p>
      ) : null}

      {results.length > 0 ? (
        <table>
          <caption>Release Worklist</caption>
          <thead>
            <tr>
              <th scope="col">Result Id</th>
              <th scope="col">Sample Id</th>
              <th scope="col">Test Def Id</th>
              <th scope="col">Status</th>
            </tr>
          </thead>
          <tbody>
            {results.map((result) => (
              <tr key={result.resultId}>
                <td>
                  <button
                    type="button"
                    className="link-button"
                    onClick={() => selectResult(result)}
                  >
                    {result.resultId}
                  </button>
                </td>
                <td>{result.sampleId}</td>
                <td>{result.testDefinitionId}</td>
                <td>
                  <span className={`catalog-status catalog-status--${result.status.toLowerCase()}`}>
                    {result.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selected ? (
        <div className="panel">
          <h3>Result detail: {selected.resultId}</h3>
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
                <th scope="row">Test Def Id</th>
                <td>{selected.testDefinitionId}</td>
              </tr>
              <tr>
                <th scope="row">Captured Values</th>
                <td>
                  {selected.resultValues?.map((val, idx) => (
                    <div key={idx}>
                      {val.rawValue} (by {val.capturedBy} at{" "}
                      {new Date(val.capturedAt).toLocaleString()})
                    </div>
                  ))}
                </td>
              </tr>
            </tbody>
          </table>

          {selected.status === "medically_validated" ? (
            <form onSubmit={handleRelease} className="panel" style={{ marginTop: "1rem" }}>
              <h4>Release Result</h4>
              <label htmlFor="rel-notes">Release Notes (optional)</label>
              <textarea
                id="rel-notes"
                value={releaseNotes}
                onChange={(e) => setReleaseNotes(e.target.value)}
                rows={2}
                style={{ width: "100%", marginBottom: "1rem" }}
              />
              <button type="submit" disabled={releaseAction.status === "loading"}>
                Release Result
              </button>
              <StatusBanner
                status={releaseAction.status}
                errorMessage={releaseAction.errorMessage}
                successMessage="Result released."
              />
            </form>
          ) : null}

          {selected.status === "released" || selected.status === "amended" ? (
            <form onSubmit={handleAmend} className="panel" style={{ marginTop: "1rem" }}>
              <h4>Amend Result</h4>
              <label htmlFor="amd-reason">Amendment Reason</label>
              <input
                id="amd-reason"
                value={amendReason}
                onChange={(e) => setAmendReason(e.target.value)}
                required
              />
              <label htmlFor="amd-val">New Raw Value</label>
              <input
                id="amd-val"
                value={amendRawValue}
                onChange={(e) => setAmendRawValue(e.target.value)}
                required
              />
              <button type="submit" disabled={amendAction.status === "loading"}>
                Amend Result
              </button>
              <StatusBanner
                status={amendAction.status}
                errorMessage={amendAction.errorMessage}
                successMessage="Result amended."
              />
            </form>
          ) : null}
        </div>
      ) : (
        <p className="empty-state">Select a result to view details and perform actions.</p>
      )}
    </section>
  );
}
