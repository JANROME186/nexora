import { useState } from "react";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import {
  listProcessingWorklist,
  captureResult,
  recordIncident,
  submitForValidation,
} from "../../api/laboratoryOperationsApi";
import type { LaboratoryResult, ResultValue } from "../../api/types";

export function LaboratoryProcessingScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [results, setResults] = useState<LaboratoryResult[]>([]);
  const [selected, setSelected] = useState<LaboratoryResult | undefined>(undefined);

  const [rawValue, setRawValue] = useState("");
  const [incidentType, setIncidentType] = useState("specimen_quality");
  const [incidentDesc, setIncidentDesc] = useState("");

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant to list processing worklist.");
    const loaded = await listProcessingWorklist(tenantId);
    setResults(loaded);
    return loaded;
  });

  const captureAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a result to capture.");
    const val: ResultValue = {
      rawValue,
      capturedAt: new Date().toISOString(),
      capturedBy: "current_user", // Simplified for MVP
    };
    const captured = await captureResult(selected.resultId, { values: [val] });
    setResults((current) => current.map((r) => (r.resultId === captured.resultId ? captured : r)));
    setSelected(captured);
    return captured;
  });

  const incidentAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a result to record incident.");
    const recorded = await recordIncident(selected.resultId, {
      incidentType,
      description: incidentDesc,
    });
    setResults((current) => current.map((r) => (r.resultId === recorded.resultId ? recorded : r)));
    setSelected(recorded);
    return recorded;
  });

  const submitAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a result to submit.");
    const submitted = await submitForValidation(selected.resultId);
    setResults((current) =>
      current.map((r) => (r.resultId === submitted.resultId ? submitted : r)),
    );
    setSelected(submitted);
    return submitted;
  });

  async function handleList() {
    await listAction.run();
  }

  function selectResult(result: LaboratoryResult) {
    setSelected(result);
    setRawValue("");
    setIncidentType("specimen_quality");
    setIncidentDesc("");
    captureAction.reset();
    incidentAction.reset();
    submitAction.reset();
  }

  async function handleCapture(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await captureAction.run();
  }

  async function handleIncident(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await incidentAction.run();
  }

  async function handleSubmit() {
    await submitAction.run();
  }

  return (
    <section aria-labelledby="laboratory-processing-heading">
      <h2 id="laboratory-processing-heading">Laboratory Processing</h2>
      <ScopeIndicator />
      {!canUse && (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before processing results.
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
        <p className="empty-state">No results pending processing for this tenant.</p>
      ) : null}

      {results.length > 0 ? (
        <table>
          <caption>Processing Worklist</caption>
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
                <th scope="row">Sample Id</th>
                <td>{selected.sampleId}</td>
              </tr>
              <tr>
                <th scope="row">Test Def Id</th>
                <td>{selected.testDefinitionId}</td>
              </tr>
              <tr>
                <th scope="row">Captured Values</th>
                <td>{selected.resultValues?.length || 0} values</td>
              </tr>
              <tr>
                <th scope="row">Incidents</th>
                <td>{selected.incidents?.length || 0} incidents</td>
              </tr>
            </tbody>
          </table>

          {selected.status === "captured" || selected.status === "pending_technical_validation" ? (
            <div className="panel" style={{ marginTop: "1rem" }}>
              <form onSubmit={handleCapture}>
                <h4>Capture Result</h4>
                <label htmlFor="raw-value">Raw Value</label>
                <input
                  id="raw-value"
                  value={rawValue}
                  onChange={(e) => setRawValue(e.target.value)}
                  required
                />
                <button type="submit" disabled={captureAction.status === "loading"}>
                  Capture
                </button>
                <StatusBanner
                  status={captureAction.status}
                  errorMessage={captureAction.errorMessage}
                  successMessage="Result captured."
                />
              </form>

              <form onSubmit={handleIncident} style={{ marginTop: "1rem" }}>
                <h4>Record Incident</h4>
                <label htmlFor="incident-type">Incident Type</label>
                <select
                  id="incident-type"
                  value={incidentType}
                  onChange={(e) => setIncidentType(e.target.value)}
                >
                  <option value="specimen_quality">Specimen Quality</option>
                  <option value="instrument_error">Instrument Error</option>
                  <option value="reagent_issue">Reagent Issue</option>
                  <option value="other">Other</option>
                </select>
                <label htmlFor="incident-desc">Description</label>
                <input
                  id="incident-desc"
                  value={incidentDesc}
                  onChange={(e) => setIncidentDesc(e.target.value)}
                  required
                />
                <button type="submit" disabled={incidentAction.status === "loading"}>
                  Record Incident
                </button>
                <StatusBanner
                  status={incidentAction.status}
                  errorMessage={incidentAction.errorMessage}
                  successMessage="Incident recorded."
                />
              </form>

              <div style={{ marginTop: "1rem" }}>
                <button
                  type="button"
                  disabled={submitAction.status === "loading"}
                  onClick={handleSubmit}
                >
                  Submit for Validation
                </button>
                <StatusBanner
                  status={submitAction.status}
                  errorMessage={submitAction.errorMessage}
                  successMessage="Result submitted for validation."
                />
              </div>
            </div>
          ) : null}
        </div>
      ) : (
        <p className="empty-state">Select a result to view details and perform actions.</p>
      )}
    </section>
  );
}
