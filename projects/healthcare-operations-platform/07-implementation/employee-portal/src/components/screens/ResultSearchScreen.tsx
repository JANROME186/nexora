/**
 * Result Search and Worklist screen (MVP-MOD-007-FE-001, BCM-RES-001 Result Management).
 *
 * Implements:
 *   SCR-RMG-001-01 Result Search and Worklist  (/results)
 *   SCR-RMG-001-02 Result Detail (internal)    (/results/{resultId})
 *
 * This is a read-only facade over LaboratoryResult (AGG-009 owned by BCM-LAB-006).
 * Includes a CriticalIndicatorBadge for results whose reference-range snapshots contain
 * critical-value flags, and a LifecycleStatusTimeline summary.
 *
 * Screens requiring patient_portal or doctor_portal surfaces (BCM-RES-004 Digital Delivery,
 * BCM-RES-005 Result History) are NOT rendered here; their ui-model.yaml surfaces.employee_portal
 * field is set to not_required. Employee-actor oversight of delivery tickets is available
 * via the ResultDelivery management surface in the API layer only.
 */
import { useState } from "react";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { listReleasedResults, getResultById } from "../../api/resultsDeliveryApi";
import { MESSAGES } from "../../i18n/messages";
import type { LaboratoryResult } from "../../api/types";

/** Status badge CSS class helper (mirrors lab-workflow screens). */
function resultStatusClass(status: string): string {
  return `catalog-status catalog-status--${status.toLowerCase().replace(/_/g, "-")}`;
}

/** Returns true when any reference-range snapshot indicates a critical value was observed. */
function hasCriticalFlag(result: LaboratoryResult): boolean {
  return result.referenceRangeSnapshots.some(
    (r) => r.criticalLow !== undefined || r.criticalHigh !== undefined,
  );
}

interface ResultDetailPanelProps {
  result: LaboratoryResult;
  detailStatus: AsyncStatus;
  detailErrorMessage?: string;
}

function ResultLifecycleTimeline({ result }: { result: LaboratoryResult }) {
  return (
    <table>
      <tbody>
        <tr>
          <th scope="row">Current Status</th>
          <td>
            <span className={resultStatusClass(result.status)}>{result.status}</span>
          </td>
        </tr>
        <tr>
          <th scope="row">Sample ID</th>
          <td>{result.sampleId}</td>
        </tr>
        <tr>
          <th scope="row">Test Definition</th>
          <td>{result.testDefinitionId}</td>
        </tr>
        {result.technicalValidation ? (
          <tr>
            <th scope="row">Technical Validation</th>
            <td>
              By {result.technicalValidation.validatedBy} at{" "}
              {result.technicalValidation.validatedAt
                ? new Date(result.technicalValidation.validatedAt).toLocaleString()
                : "—"}
            </td>
          </tr>
        ) : null}
        {result.medicalValidation ? (
          <tr>
            <th scope="row">Medical Validation</th>
            <td>
              By {result.medicalValidation.validatedBy} at{" "}
              {result.medicalValidation.validatedAt
                ? new Date(result.medicalValidation.validatedAt).toLocaleString()
                : "—"}
            </td>
          </tr>
        ) : null}
        {result.releaseRecord ? (
          <tr>
            <th scope="row">Released</th>
            <td>
              By {result.releaseRecord.releasedBy} at{" "}
              {result.releaseRecord.releasedAt
                ? new Date(result.releaseRecord.releasedAt).toLocaleString()
                : "—"}
            </td>
          </tr>
        ) : null}
      </tbody>
    </table>
  );
}

function ResultDetailPanel({ result, detailStatus, detailErrorMessage }: ResultDetailPanelProps) {
  return (
    <div className="panel">
      <h3>Result Detail: {result.resultId}</h3>
      <StatusBanner
        status={detailStatus}
        errorMessage={detailErrorMessage}
        successMessage="Detail refreshed."
      />

      <h4>Lifecycle Status Timeline</h4>
      <ResultLifecycleTimeline result={result} />

      {result.resultValues.length > 0 ? (
        <>
          <h4>Captured Values</h4>
          <table>
            <thead>
              <tr>
                <th scope="col">Raw Value</th>
                <th scope="col">Captured By</th>
                <th scope="col">Captured At</th>
              </tr>
            </thead>
            <tbody>
              {result.resultValues.map((val, idx) => (
                <tr key={idx}>
                  <td>{val.rawValue}</td>
                  <td>{val.capturedBy}</td>
                  <td>{new Date(val.capturedAt).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      ) : null}

      {result.amendments.length > 0 ? (
        <>
          <h4>Amendments</h4>
          {result.amendments.map((a, idx) => (
            <p key={idx}>
              {a.reason} — by {a.amendedBy} at {new Date(a.amendedAt).toLocaleString()}
            </p>
          ))}
        </>
      ) : null}

      {hasCriticalFlag(result) ? (
        <p
          className="status-banner status-banner--error"
          role="alert"
          aria-label="Critical result alert"
        >
          Critical: this result has critical reference-range flags. Verify escalation is managed.
        </p>
      ) : null}
    </div>
  );
}

export function ResultSearchScreen() {
  const { scope } = useAdminScope();
  const { tenantId } = scope;

  const [results, setResults] = useState<LaboratoryResult[]>([]);
  const [selected, setSelected] = useState<LaboratoryResult | undefined>(undefined);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error(MESSAGES.selectResultFirst);
    const loaded = await listReleasedResults(tenantId);
    setResults(loaded);
    return loaded;
  });

  const detailAction = useAsyncAction(async (resultId: string) => {
    if (!tenantId) throw new Error(MESSAGES.selectResultFirst);
    const detail = await getResultById(resultId, tenantId);
    setSelected(detail);
    return detail;
  });

  async function handleLoadResults() {
    await listAction.run();
  }

  async function handleSelectResult(result: LaboratoryResult) {
    setSelected(result);
    detailAction.reset();
    await detailAction.run(result.resultId);
  }

  return (
    <section aria-labelledby="result-search-heading">
      <h2 id="result-search-heading">Result Search and Worklist</h2>
      <ScopeIndicator />
      {!tenantId && (
        <p className="status-banner status-banner--error">
          Select a tenant before searching results.
        </p>
      )}

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={handleLoadResults}
      >
        Load Released Results
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Results loaded."
      />

      {listAction.status === "success" && results.length === 0 ? (
        <p className="empty-state">{MESSAGES.noResultsPendingRelease}</p>
      ) : null}

      {results.length > 0 ? (
        <table>
          <caption>Released Results Worklist</caption>
          <thead>
            <tr>
              <th scope="col">Result ID</th>
              <th scope="col">Test Definition</th>
              <th scope="col">Status</th>
              <th scope="col">Critical</th>
            </tr>
          </thead>
          <tbody>
            {results.map((result) => (
              <tr key={result.resultId}>
                <td>
                  <button
                    type="button"
                    className="link-button"
                    onClick={() => handleSelectResult(result)}
                  >
                    {result.resultId}
                  </button>
                </td>
                <td>{result.testDefinitionId}</td>
                <td>
                  <span className={resultStatusClass(result.status)}>{result.status}</span>
                </td>
                <td>
                  {hasCriticalFlag(result) ? (
                    <span
                      className="catalog-status catalog-status--critical"
                      aria-label="Critical indicator"
                    >
                      CRITICAL
                    </span>
                  ) : null}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selected ? (
        <ResultDetailPanel
          result={selected}
          detailStatus={detailAction.status}
          detailErrorMessage={detailAction.errorMessage}
        />
      ) : (
        <p className="empty-state">Select a result to view its full detail.</p>
      )}
    </section>
  );
}
