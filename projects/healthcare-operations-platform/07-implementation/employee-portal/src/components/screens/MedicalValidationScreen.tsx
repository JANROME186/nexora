import { useState } from "react";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import {
  listMedicalValidationWorklist,
  validateMedically,
} from "../../api/laboratoryOperationsApi";
import type { LaboratoryResult } from "../../api/types";

export function MedicalValidationScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [results, setResults] = useState<LaboratoryResult[]>([]);
  const [selected, setSelected] = useState<LaboratoryResult | undefined>(undefined);
  const [notes, setNotes] = useState("");

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant to list medical validation worklist.");
    const loaded = await listMedicalValidationWorklist(tenantId);
    setResults(loaded);
    return loaded;
  });

  const validateAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a result to validate.");
    const validated = await validateMedically(selected.resultId, { notes: notes || undefined });
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
    <section aria-labelledby="medical-validation-heading">
      <h2 id="medical-validation-heading">Medical Validation</h2>
      <ScopeIndicator />
      {!canUse && (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before medically validating results.
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
        <p className="empty-state">No results pending medical validation for this tenant.</p>
      ) : null}

      {results.length > 0 ? (
        <table>
          <caption>Medical Validation Worklist</caption>
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
              {selected.technicalValidation ? (
                <tr>
                  <th scope="row">Technical Validation</th>
                  <td>Notes: {selected.technicalValidation.notes || "None"}</td>
                </tr>
              ) : null}
            </tbody>
          </table>

          {selected.status === "pending_medical_validation" ? (
            <form onSubmit={handleValidate} className="panel" style={{ marginTop: "1rem" }}>
              <h4>Validate Result</h4>
              <label htmlFor="val-notes">Clinical Interpretation Note (optional)</label>
              <textarea
                id="val-notes"
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                rows={3}
                style={{ width: "100%", marginBottom: "1rem" }}
              />
              <button type="submit" disabled={validateAction.status === "loading"}>
                Medically Validate
              </button>
              <StatusBanner
                status={validateAction.status}
                errorMessage={validateAction.errorMessage}
                successMessage="Result medically validated."
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
