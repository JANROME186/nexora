import { useState } from "react";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { listCollectionWorklist, collectSample } from "../../api/laboratoryOperationsApi";
import type { Sample } from "../../api/types";

export function SampleCollectionScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [samples, setSamples] = useState<Sample[]>([]);
  const [selected, setSelected] = useState<Sample | undefined>(undefined);
  const [collectedBy, setCollectedBy] = useState("");

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant to list collection worklist.");
    const loaded = await listCollectionWorklist(tenantId);
    setSamples(loaded);
    return loaded;
  });

  const collectAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a sample to collect.");
    const collected = await collectSample(selected.sampleId, {
      collectedBy: collectedBy || undefined,
    });
    setSamples((current) =>
      current.map((s) => (s.sampleId === collected.sampleId ? collected : s)),
    );
    setSelected(collected);
    return collected;
  });

  async function handleList() {
    await listAction.run();
  }

  function selectSample(sample: Sample) {
    setSelected(sample);
    setCollectedBy("");
    collectAction.reset();
  }

  async function handleCollect(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await collectAction.run();
  }

  return (
    <section aria-labelledby="sample-collection-heading">
      <h2 id="sample-collection-heading">Sample Collection</h2>
      <ScopeIndicator />
      {!canUse && (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before collecting samples.
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
        <p className="empty-state">No samples pending collection for this tenant.</p>
      ) : null}

      {samples.length > 0 ? (
        <table>
          <caption>Collection Worklist</caption>
          <thead>
            <tr>
              <th scope="col">Sample Id</th>
              <th scope="col">Order Id</th>
              <th scope="col">Patient Id</th>
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
                <td>{sample.patientId}</td>
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
              <tr>
                <th scope="row">Sample Type Ref Id</th>
                <td>{selected.sampleTypeRefId}</td>
              </tr>
            </tbody>
          </table>

          {selected.status !== "collected" &&
          selected.status !== "rejected" &&
          selected.status !== "disposed" ? (
            <form onSubmit={handleCollect}>
              <h4>Collect Sample</h4>
              <label htmlFor="collected-by">Collected by (optional)</label>
              <input
                id="collected-by"
                value={collectedBy}
                onChange={(e) => setCollectedBy(e.target.value)}
              />
              <button type="submit" disabled={collectAction.status === "loading"}>
                Mark as Collected
              </button>
            </form>
          ) : null}
          <StatusBanner
            status={collectAction.status}
            errorMessage={collectAction.errorMessage}
            successMessage="Sample collected."
          />
        </div>
      ) : (
        <p className="empty-state">Select a sample to view details and perform actions.</p>
      )}
    </section>
  );
}
