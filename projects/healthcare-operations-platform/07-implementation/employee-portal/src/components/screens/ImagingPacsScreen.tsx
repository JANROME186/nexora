/**
 * PACS Integration administration screen (COM-MOD-014-FE-001 / BCM-IMG-005).
 *
 * Register PACS endpoints, query PACS studies, execute QIDO-RS searches, retrieve WADO URLs, and trigger STOW-RS stores.
 */
import { useState } from "react";
import {
  getPacsWadoUrl,
  listPacsEndpoints,
  qidoSearchPacsStudies,
  queryPacsStudy,
  registerPacsEndpoint,
  stowStorePacs,
  type PacsIntegrationEndpoint,
  type PacsQidoSearchResult,
  type PacsStowStoreResult,
  type PacsWadoRetrieveResponse,
} from "../../api/imagingOperationsApi";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

interface RegisterEndpointFormProps {
  onRegistered: () => void;
}

function RegisterEndpointForm({ onRegistered }: RegisterEndpointFormProps) {
  const { t } = useLocale();
  const labels = t.imagingOperations.pacs;
  const { scope } = useAdminScope();

  const [pacsNodeId, setPacsNodeId] = useState("PACS-MAIN-01");
  const [baseUrl, setBaseUrl] = useState("https://pacs.hospital.org/dicom-web");
  const [protocol, setProtocol] = useState("DICOM_WEB");

  const { status, errorMessage, run } = useAsyncAction(async () => {
    if (!pacsNodeId.trim() || !baseUrl.trim()) return;
    await registerPacsEndpoint({
      pacsNodeId,
      baseUrl,
      protocol,
    });
    onRegistered();
  });

  return (
    <div className="panel" style={{ marginBottom: "1rem" }}>
      <h3>{labels.registerEndpoint}</h3>
      {!scope.tenantId && <p style={{ color: "orange" }}>{t.imagingOperations.shared.tenantRequired}</p>}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.5rem" }}>
        <div>
          <label htmlFor="pacs-node-id">{labels.pacsNodeId}</label>
          <input
            id="pacs-node-id"
            value={pacsNodeId}
            onChange={(e) => setPacsNodeId(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="pacs-base-url">{labels.baseUrl}</label>
          <input
            id="pacs-base-url"
            value={baseUrl}
            onChange={(e) => setBaseUrl(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="pacs-protocol">{labels.protocol}</label>
          <input
            id="pacs-protocol"
            value={protocol}
            onChange={(e) => setProtocol(e.target.value)}
          />
        </div>
      </div>
      <button
        type="button"
        id="pacs-register-btn"
        disabled={status === "loading" || !pacsNodeId.trim() || !baseUrl.trim()}
        onClick={() => { void run(); }}
        style={{ marginTop: "0.5rem" }}
      >
        {labels.registerEndpoint}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
      />
    </div>
  );
}

export function ImagingPacsScreen() {
  const { t } = useLocale();
  const labels = t.imagingOperations.pacs;
  const shared = t.imagingOperations.shared;

  const [endpoints, setEndpoints] = useState<PacsIntegrationEndpoint[]>([]);
  const [selectedEndpoint, setSelectedEndpoint] = useState<PacsIntegrationEndpoint | null>(null);

  const [accessionQuery, setAccessionQuery] = useState("ACC-1001");
  const [queryResultText, setQueryResultText] = useState("");
  const [qidoResults, setQidoResults] = useState<PacsQidoSearchResult[]>([]);
  const [wadoResult, setWadoResult] = useState<PacsWadoRetrieveResponse | null>(null);
  const [stowResult, setStowResult] = useState<PacsStowStoreResult | null>(null);

  const [studyUid, setStudyUid] = useState("1.2.840.113619.2.55.3.28311512");
  const [stowPayload, setStowPayload] = useState("SGVsbG8gRElDT00=");

  const { status: fetchStatus, errorMessage: fetchError, run: fetchEndpoints } = useAsyncAction(async () => {
    const res = await listPacsEndpoints();
    setEndpoints(res);
  });

  const { status: queryStatus, errorMessage: queryError, run: runQuery } = useAsyncAction(async () => {
    if (!selectedEndpoint || !accessionQuery.trim()) return;
    const res = await queryPacsStudy(selectedEndpoint.id, accessionQuery.trim());
    setQueryResultText(res.result);
  });

  const { status: qidoStatus, errorMessage: qidoError, run: runQido } = useAsyncAction(async () => {
    if (!selectedEndpoint) return;
    const res = await qidoSearchPacsStudies(selectedEndpoint.id);
    setQidoResults(res);
  });

  const { status: wadoStatus, errorMessage: wadoError, run: runWado } = useAsyncAction(async () => {
    if (!selectedEndpoint || !studyUid.trim()) return;
    const res = await getPacsWadoUrl(selectedEndpoint.id, studyUid.trim());
    setWadoResult(res);
  });

  const { status: stowStatus, errorMessage: stowError, run: runStow } = useAsyncAction(async () => {
    if (!selectedEndpoint || !studyUid.trim()) return;
    const res = await stowStorePacs(selectedEndpoint.id, studyUid.trim(), "application/dicom", stowPayload);
    setStowResult(res);
  });

  const columns: DataTableColumn<PacsIntegrationEndpoint>[] = [
    { key: "id", header: "ID", render: (item) => item.id },
    { key: "pacsNodeId", header: labels.pacsNodeId, render: (item) => item.pacsNodeId },
    { key: "baseUrl", header: labels.baseUrl, render: (item) => item.baseUrl },
    { key: "protocol", header: labels.protocol, render: (item) => item.protocol },
    { key: "status", header: shared.status, render: (item) => item.status },
  ];

  return (
    <div className="screen-container">
      <h2>{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />

      <RegisterEndpointForm onRegistered={() => { void fetchEndpoints(); }} />

      <div className="panel">
        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "0.5rem" }}>
          <h3>PACS Integration Endpoints</h3>
          <button type="button" id="pacs-load-btn" onClick={() => { void fetchEndpoints(); }}>
            {shared.load}
          </button>
        </div>
        <StatusBanner status={fetchStatus} errorMessage={fetchError} />

        <DataTable
          caption={labels.heading}
          columns={columns}
          rows={endpoints}
          rowKey={(item) => item.id}
          onSelectRow={(item) => setSelectedEndpoint(item)}
        />

        {selectedEndpoint && (
          <div style={{ marginTop: "1rem", padding: "0.5rem", border: "1px solid #ccc" }}>
            <h4>Endpoint Operations: {selectedEndpoint.pacsNodeId} ({selectedEndpoint.baseUrl})</h4>

            <div style={{ display: "flex", gap: "0.5rem", alignItems: "center", marginBottom: "0.5rem" }}>
              <input
                id="pacs-acc-query"
                value={accessionQuery}
                onChange={(e) => setAccessionQuery(e.target.value)}
                placeholder="Accession #"
              />
              <button type="button" id="pacs-query-btn" onClick={() => { void runQuery(); }} disabled={queryStatus === "loading"}>
                Query Accession
              </button>
              <button type="button" id="pacs-qido-btn" onClick={() => { void runQido(); }} disabled={qidoStatus === "loading"}>
                {labels.qidoSearch}
              </button>
            </div>
            {queryResultText && <p>Query Result: {queryResultText}</p>}
            <StatusBanner status={queryStatus} errorMessage={queryError} successMessage={labels.querySuccess} />
            <StatusBanner status={qidoStatus} errorMessage={qidoError} />

            {qidoResults.length > 0 && (
              <div style={{ margin: "0.5rem 0" }}>
                <h5>QIDO-RS Results ({qidoResults.length}):</h5>
                <ul>
                  {qidoResults.map((r, idx) => (
                    <li key={idx}>{r.studyInstanceUid} - {r.patientName} ({r.modality})</li>
                  ))}
                </ul>
              </div>
            )}

            <hr style={{ margin: "1rem 0" }} />

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
              <div>
                <h5>{labels.getWadoUrl}</h5>
                <label htmlFor="pacs-study-uid">{shared.selectStudyFirst || "Study Instance UID"}</label>
                <input
                  id="pacs-study-uid"
                  value={studyUid}
                  onChange={(e) => setStudyUid(e.target.value)}
                />
                <button
                  type="button"
                  id="pacs-wado-btn"
                  onClick={() => { void runWado(); }}
                  disabled={wadoStatus === "loading"}
                  style={{ marginTop: "0.5rem" }}
                >
                  {labels.getWadoUrl}
                </button>
                <StatusBanner status={wadoStatus} errorMessage={wadoError} />
                {wadoResult && (
                  <p style={{ fontSize: "0.85rem", wordBreak: "break-all" }}>WADO URL: <a href={wadoResult.wadoUrl} target="_blank" rel="noreferrer">{wadoResult.wadoUrl}</a></p>
                )}
              </div>

              <div>
                <h5>{labels.stowStore}</h5>
                <label htmlFor="pacs-stow-payload">Payload (Base64)</label>
                <input
                  id="pacs-stow-payload"
                  value={stowPayload}
                  onChange={(e) => setStowPayload(e.target.value)}
                />
                <button
                  type="button"
                  id="pacs-stow-btn"
                  onClick={() => { void runStow(); }}
                  disabled={stowStatus === "loading"}
                  style={{ marginTop: "0.5rem" }}
                >
                  {labels.stowStore}
                </button>
                <StatusBanner status={stowStatus} errorMessage={stowError} successMessage={labels.stowSuccess} />
                {stowResult && (
                  <p style={{ fontSize: "0.85rem" }}>Stored Instances: {stowResult.storedInstances} ({stowResult.responseMessage})</p>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
