/**
 * DICOM Integration administration screen (COM-MOD-014-FE-001 / BCM-IMG-004).
 *
 * Register DICOM nodes, test C-ECHO, query C-FIND Worklist, trigger C-MOVE transfer, and validate headers.
 */
import { useState } from "react";
import {
  echoCEcho,
  listDicomConfigs,
  queryDicomWorklist,
  registerDicomConfig,
  requestDicomTransfer,
  validateDicomHeader,
  type DicomAdapterConfiguration,
  type DicomTransferResult,
  type DicomValidationResult,
  type DicomWorklistEntry,
} from "../../api/imagingOperationsApi";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

interface RegisterConfigFormProps {
  onRegistered: () => void;
}

function RegisterConfigForm({ onRegistered }: RegisterConfigFormProps) {
  const { t } = useLocale();
  const labels = t.imagingOperations.dicom;
  const { scope } = useAdminScope();

  const [aeTitle, setAeTitle] = useState("NEXORA_PACS_AE");
  const [host, setHost] = useState("192.168.1.100");
  const [port, setPort] = useState(104);
  const [modalityType, setModalityType] = useState("CT");

  const { status, errorMessage, run } = useAsyncAction(async () => {
    if (!aeTitle.trim() || !host.trim()) return;
    await registerDicomConfig({
      aeTitle,
      host,
      port: Number(port),
      modalityType,
    });
    onRegistered();
  });

  return (
    <div className="panel" style={{ marginBottom: "1rem" }}>
      <h3>{labels.registerConfig}</h3>
      {!scope.tenantId && <p style={{ color: "orange" }}>{t.imagingOperations.shared.tenantRequired}</p>}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.5rem" }}>
        <div>
          <label htmlFor="dicom-ae">{labels.aeTitle}</label>
          <input
            id="dicom-ae"
            value={aeTitle}
            onChange={(e) => setAeTitle(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="dicom-host">{labels.host}</label>
          <input
            id="dicom-host"
            value={host}
            onChange={(e) => setHost(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="dicom-port">{labels.port}</label>
          <input
            id="dicom-port"
            type="number"
            value={port}
            onChange={(e) => setPort(Number(e.target.value))}
          />
        </div>
        <div>
          <label htmlFor="dicom-modality">{labels.modalityType}</label>
          <input
            id="dicom-modality"
            value={modalityType}
            onChange={(e) => setModalityType(e.target.value)}
          />
        </div>
      </div>
      <button
        type="button"
        id="dicom-register-btn"
        disabled={status === "loading" || !aeTitle.trim() || !host.trim()}
        onClick={() => { void run(); }}
        style={{ marginTop: "0.5rem" }}
      >
        {labels.registerConfig}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.validationSuccess}
      />
    </div>
  );
}

export function ImagingDicomScreen() {
  const { t } = useLocale();
  const labels = t.imagingOperations.dicom;
  const shared = t.imagingOperations.shared;

  const [configs, setConfigs] = useState<DicomAdapterConfiguration[]>([]);
  const [selectedConfig, setSelectedConfig] = useState<DicomAdapterConfiguration | null>(null);

  const [echoResultMsg, setEchoResultMsg] = useState("");
  const [worklistEntries, setWorklistEntries] = useState<DicomWorklistEntry[]>([]);
  const [transferResult, setTransferResult] = useState<DicomTransferResult | null>(null);
  const [validationResult, setValidationResult] = useState<DicomValidationResult | null>(null);

  const [studyUid, setStudyUid] = useState("1.2.840.113619.2.55.3.28311512");
  const [destAe, setDestAe] = useState("DEST_PACS_AE");
  const [valPatientId, setValPatientId] = useState("PAT-1001");
  const [valModality, setValModality] = useState("CT");

  const { status: fetchStatus, errorMessage: fetchError, run: fetchConfigs } = useAsyncAction(async () => {
    const res = await listDicomConfigs();
    setConfigs(res);
  });

  const { status: echoStatus, errorMessage: echoError, run: runEcho } = useAsyncAction(async () => {
    if (!selectedConfig) return;
    const res = await echoCEcho(selectedConfig.id);
    setEchoResultMsg(res.result);
  });

  const { status: wlStatus, errorMessage: wlError, run: runWorklist } = useAsyncAction(async () => {
    if (!selectedConfig) return;
    const res = await queryDicomWorklist(selectedConfig.id);
    setWorklistEntries(res);
  });

  const { status: xferStatus, errorMessage: xferError, run: runTransfer } = useAsyncAction(async () => {
    if (!selectedConfig) return;
    const res = await requestDicomTransfer(selectedConfig.id, studyUid, destAe);
    setTransferResult(res);
  });

  const { status: validateStatus, errorMessage: validateError, run: runValidate } = useAsyncAction(async () => {
    if (!selectedConfig) return;
    const res = await validateDicomHeader(selectedConfig.id, valPatientId, studyUid, valModality);
    setValidationResult(res);
  });

  const columns: DataTableColumn<DicomAdapterConfiguration>[] = [
    { key: "id", header: "ID", render: (item) => item.id },
    { key: "aeTitle", header: labels.aeTitle, render: (item) => item.aeTitle },
    { key: "host", header: labels.host, render: (item) => item.host },
    { key: "port", header: labels.port, render: (item) => item.port },
    { key: "modalityType", header: labels.modalityType, render: (item) => item.modalityType },
    { key: "status", header: shared.status, render: (item) => item.status },
  ];

  return (
    <div className="screen-container">
      <h2>{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />

      <RegisterConfigForm onRegistered={() => { void fetchConfigs(); }} />

      <div className="panel">
        <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "0.5rem" }}>
          <h3>DICOM Configurations</h3>
          <button type="button" id="dicom-load-btn" onClick={() => { void fetchConfigs(); }}>
            {shared.load}
          </button>
        </div>
        <StatusBanner status={fetchStatus} errorMessage={fetchError} />

        <DataTable
          caption={labels.heading}
          columns={columns}
          rows={configs}
          rowKey={(item) => item.id}
          onSelectRow={(item) => setSelectedConfig(item)}
        />

        {selectedConfig && (
          <div style={{ marginTop: "1rem", padding: "0.5rem", border: "1px solid #ccc" }}>
            <h4>Node Operations: {selectedConfig.aeTitle} ({selectedConfig.host}:{selectedConfig.port})</h4>
            
            <div style={{ display: "flex", gap: "0.5rem", marginBottom: "1rem" }}>
              <button type="button" id="dicom-echo-btn" onClick={() => { void runEcho(); }} disabled={echoStatus === "loading"}>
                {labels.testEcho}
              </button>
              <button type="button" id="dicom-worklist-btn" onClick={() => { void runWorklist(); }} disabled={wlStatus === "loading"}>
                {labels.queryWorklist}
              </button>
            </div>
            {echoResultMsg && <p><strong>{labels.echoResult}</strong> {echoResultMsg}</p>}
            <StatusBanner status={echoStatus} errorMessage={echoError} />
            <StatusBanner status={wlStatus} errorMessage={wlError} />

            {worklistEntries.length > 0 && (
              <div style={{ margin: "0.5rem 0" }}>
                <h5>Worklist Entries ({worklistEntries.length}):</h5>
                <ul>
                  {worklistEntries.map((e, idx) => (
                    <li key={idx}>{e.accessionNumber} - {e.patientName} ({e.modality})</li>
                  ))}
                </ul>
              </div>
            )}

            <hr style={{ margin: "1rem 0" }} />

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "1rem" }}>
              <div>
                <h5>{labels.requestTransfer}</h5>
                <label htmlFor="dicom-study-uid">{labels.studyInstanceUid}</label>
                <input
                  id="dicom-study-uid"
                  value={studyUid}
                  onChange={(e) => setStudyUid(e.target.value)}
                />
                <label htmlFor="dicom-dest-ae">{labels.destinationAeTitle}</label>
                <input
                  id="dicom-dest-ae"
                  value={destAe}
                  onChange={(e) => setDestAe(e.target.value)}
                />
                <button
                  type="button"
                  id="dicom-transfer-btn"
                  onClick={() => { void runTransfer(); }}
                  disabled={xferStatus === "loading"}
                  style={{ marginTop: "0.5rem" }}
                >
                  {labels.requestTransfer}
                </button>
                <StatusBanner status={xferStatus} errorMessage={xferError} successMessage={labels.transferSuccess} />
                {transferResult && (
                  <p style={{ fontSize: "0.85rem" }}>Transfer ID: {transferResult.transferId} | Transferred: {transferResult.transferredInstances}</p>
                )}
              </div>

              <div>
                <h5>{labels.validateHeader}</h5>
                <label htmlFor="dicom-val-patient">{shared.actorId || "Patient ID"}</label>
                <input
                  id="dicom-val-patient"
                  value={valPatientId}
                  onChange={(e) => setValPatientId(e.target.value)}
                />
                <label htmlFor="dicom-val-modality">{labels.modalityType}</label>
                <input
                  id="dicom-val-modality"
                  value={valModality}
                  onChange={(e) => setValModality(e.target.value)}
                />
                <button
                  type="button"
                  id="dicom-validate-btn"
                  onClick={() => { void runValidate(); }}
                  disabled={validateStatus === "loading"}
                  style={{ marginTop: "0.5rem" }}
                >
                  {labels.validateHeader}
                </button>
                <StatusBanner status={validateStatus} errorMessage={validateError} successMessage={labels.validationSuccess} />
                {validationResult && (
                  <p style={{ fontSize: "0.85rem" }}>Valid: {validationResult.validHeader ? "Yes" : "No"} ({validationResult.validationMessage})</p>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
