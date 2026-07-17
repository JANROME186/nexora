import { useState } from "react";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import {
  listTechnicalValidationWorklist,
  validateTechnically,
} from "../../api/laboratoryOperationsApi";
import type { LaboratoryResult } from "../../api/types";

export function TechnicalValidationScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [results, setResults] = useState<LaboratoryResult[]>([]);
  const [selected, setSelected] = useState<LaboratoryResult | undefined>(undefined);
  const [notes, setNotes] = useState("");

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant to list technical validation worklist.");
    const loaded = await listTechnicalValidationWorklist(tenantId);
    setResults(loaded);
    return loaded;
  });

  const validateAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a result to validate.");
    const validated = await validateTechnically(selected.resultId, { notes: notes || undefined });
    setResults((current) =>
      current.map((r) => (r.resultId === validated.resultId ? validated : r)),
    );
    setSelected(validated);
    return validated;
  });

  async function handleList() {
    await listAction.run();
  }

  function selectResult(result: LaboratoryResult) {
    setSelected(result);
    setNotes("");
    validateAction.reset();
  }

  async function handleValidate(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await validateAction.run();
  }

  return (
    <section aria-labelledby="technical-validation-heading">
      <h2 id="technical-validation-heading">Technical Validation</h2>
      <ScopeIndicator />
      {!canUse && (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before validating results.
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
        <p className="empty-state">No results pending technical validation for this tenant.</p>
      ) : null}

      {results.length > 0 ? (
        <table>
          <caption>Technical Validation Worklist</caption>
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

          {selected.status === "pending_technical_validation" ? (
            <form onSubmit={handleValidate} className="panel" style={{ marginTop: "1rem" }}>
              <h4>Validate Result</h4>
              <label htmlFor="val-notes">Validation Notes (optional)</label>
              <input id="val-notes" value={notes} onChange={(e) => setNotes(e.target.value)} />
              <button type="submit" disabled={validateAction.status === "loading"}>
                Technically Validate
              </button>
              <StatusBanner
                status={validateAction.status}
                errorMessage={validateAction.errorMessage}
                successMessage="Result technically validated."
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
