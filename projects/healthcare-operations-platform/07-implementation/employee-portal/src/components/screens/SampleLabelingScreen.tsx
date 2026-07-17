import { useState } from "react";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { getSample, getLabelPrintJob } from "../../api/laboratoryOperationsApi";
import type { Sample } from "../../api/types";

export function SampleLabelingScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [sampleIdInput, setSampleIdInput] = useState("");
  const [sample, setSample] = useState<Sample | undefined>(undefined);
  const [printJob, setPrintJob] = useState<unknown>(undefined);

  const loadAction = useAsyncAction(async () => {
    if (!sampleIdInput) throw new Error("Enter a Sample Id.");
    const loaded = await getSample(sampleIdInput);
    setSample(loaded);
    setPrintJob(undefined);
    return loaded;
  });

  const printAction = useAsyncAction(async () => {
    if (!sample) throw new Error("Load a sample first.");
    const job = await getLabelPrintJob(sample.sampleId);
    setPrintJob(job);
    return job;
  });

  async function handleLoad(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await loadAction.run();
  }

  async function handlePrint() {
    await printAction.run();
  }

  return (
    <section aria-labelledby="sample-labeling-heading">
      <h2 id="sample-labeling-heading">Sample Labeling</h2>
      <ScopeIndicator />
      {!canUse && (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before labeling samples.
        </p>
      )}

      <form onSubmit={handleLoad} className="panel">
        <label htmlFor="label-sample-id">Sample Id</label>
        <input
          id="label-sample-id"
          value={sampleIdInput}
          onChange={(e) => setSampleIdInput(e.target.value)}
          required
        />
        <button type="submit" disabled={!canUse || loadAction.status === "loading"}>
          Load Sample
        </button>
      </form>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage="Sample loaded."
      />

      {sample ? (
        <div className="panel">
          <h3>Labeling: {sample.sampleId}</h3>
          <table>
            <tbody>
              <tr>
                <th scope="row">Status</th>
                <td>{sample.status}</td>
              </tr>
              <tr>
                <th scope="row">Patient Id</th>
                <td>{sample.patientId}</td>
              </tr>
              <tr>
                <th scope="row">Sample Type Ref Id</th>
                <td>{sample.sampleTypeRefId}</td>
              </tr>
            </tbody>
          </table>

          <button type="button" disabled={printAction.status === "loading"} onClick={handlePrint}>
            Print Label
          </button>
          <StatusBanner
            status={printAction.status}
            errorMessage={printAction.errorMessage}
            successMessage="Label print job queued."
          />

          {printJob ? (
            <div className="panel" style={{ marginTop: "1rem", backgroundColor: "#f0f0f0" }}>
              <h4>Print Job Details</h4>
              <pre>{JSON.stringify(printJob, null, 2)}</pre>
            </div>
          ) : null}
        </div>
      ) : null}
    </section>
  );
}
