/**
 * Imaging Study Management administration screen (COM-MOD-014-FE-001 / BCM-IMG-003).
 *
 * Register radiology/imaging studies, query studies for a patient, and update series/instance counts and status.
 */
import { useState } from "react";
import {
  createStudy,
  listStudiesForPatient,
  updateStudyStatus,
  type ImagingStudy,
} from "../../api/imagingOperationsApi";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

interface CreateStudyFormProps {
  onCreated: () => void;
}

function CreateStudyForm({ onCreated }: CreateStudyFormProps) {
  const { t } = useLocale();
  const labels = t.imagingOperations.studies;
  const { scope } = useAdminScope();

  const [accessionNumber, setAccessionNumber] = useState("ACC-1001");
  const [patientId, setPatientId] = useState("");
  const [modality, setModality] = useState("CT");
  const [studyDescription, setStudyDescription] = useState("Chest CT Scan");

  const { status, errorMessage, run } = useAsyncAction(async () => {
    if (!accessionNumber.trim() || !patientId.trim()) return;
    await createStudy({
      accessionNumber,
      patientId,
      modality,
      studyDescription,
    });
    onCreated();
  });

  return (
    <div className="panel" style={{ marginBottom: "1rem" }}>
      <h3>{labels.createStudy}</h3>
      {!scope.tenantId && (
        <p style={{ color: "orange" }}>{t.imagingOperations.shared.tenantRequired}</p>
      )}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.5rem" }}>
        <div>
          <label htmlFor="img-study-acc">{labels.accessionNumber}</label>
          <input
            id="img-study-acc"
            value={accessionNumber}
            onChange={(e) => setAccessionNumber(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="img-study-patient">{labels.patientId}</label>
          <input
            id="img-study-patient"
            value={patientId}
            onChange={(e) => setPatientId(e.target.value)}
            placeholder="PAT-1001"
          />
        </div>
        <div>
          <label htmlFor="img-study-mod">{labels.modality}</label>
          <input
            id="img-study-mod"
            value={modality}
            onChange={(e) => setModality(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="img-study-desc">{labels.studyDescription}</label>
          <input
            id="img-study-desc"
            value={studyDescription}
            onChange={(e) => setStudyDescription(e.target.value)}
          />
        </div>
      </div>
      <button
        type="button"
        id="img-study-create-btn"
        disabled={status === "loading" || !accessionNumber.trim() || !patientId.trim()}
        onClick={() => {
          void run();
        }}
        style={{ marginTop: "0.5rem" }}
      >
        {labels.createStudy}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.studyCreated}
      />
    </div>
  );
}

export function ImagingStudiesScreen() {
  const { t } = useLocale();
  const labels = t.imagingOperations.studies;
  const shared = t.imagingOperations.shared;

  const [searchPatientId, setSearchPatientId] = useState("");
  const [studies, setStudies] = useState<ImagingStudy[]>([]);
  const [selectedStudy, setSelectedStudy] = useState<ImagingStudy | null>(null);

  const [seriesCount, setSeriesCount] = useState(2);
  const [instanceCount, setInstanceCount] = useState(120);
  const [newStatus, setNewStatus] = useState("COMPLETED");

  const {
    status: fetchStatus,
    errorMessage: fetchError,
    run: fetchStudies,
  } = useAsyncAction(async () => {
    if (!searchPatientId.trim()) return;
    const res = await listStudiesForPatient(searchPatientId.trim());
    setStudies(res);
  });

  const {
    status: updateStatusState,
    errorMessage: updateError,
    run: runUpdateStudy,
  } = useAsyncAction(async () => {
    if (!selectedStudy) return;
    const updated = await updateStudyStatus(selectedStudy.id, {
      seriesCount: Number(seriesCount),
      instanceCount: Number(instanceCount),
      status: newStatus,
    });
    setSelectedStudy(updated);
    if (searchPatientId.trim()) {
      await fetchStudies();
    }
  });

  const columns: DataTableColumn<ImagingStudy>[] = [
    { key: "id", header: "ID", render: (item) => item.id },
    {
      key: "accessionNumber",
      header: labels.accessionNumber,
      render: (item) => item.accessionNumber,
    },
    { key: "patientId", header: labels.patientId, render: (item) => item.patientId },
    { key: "modality", header: labels.modality, render: (item) => item.modality },
    { key: "seriesCount", header: labels.seriesCount, render: (item) => item.seriesCount },
    { key: "instanceCount", header: labels.instanceCount, render: (item) => item.instanceCount },
    { key: "status", header: shared.status, render: (item) => item.status },
  ];

  return (
    <div className="screen-container">
      <h2>{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />

      <CreateStudyForm
        onCreated={() => {
          if (searchPatientId) void fetchStudies();
        }}
      />

      <div className="panel">
        <h3>{shared.search}</h3>
        <div style={{ display: "flex", gap: "0.5rem", marginBottom: "0.5rem" }}>
          <input
            id="img-study-search-patient"
            value={searchPatientId}
            onChange={(e) => setSearchPatientId(e.target.value)}
            placeholder="PAT-1001"
          />
          <button
            type="button"
            id="img-fetch-studies-btn"
            disabled={fetchStatus === "loading" || !searchPatientId.trim()}
            onClick={() => {
              void fetchStudies();
            }}
          >
            {shared.load}
          </button>
        </div>
        <StatusBanner status={fetchStatus} errorMessage={fetchError} />

        <DataTable
          caption={labels.heading}
          columns={columns}
          rows={studies}
          rowKey={(item) => item.id}
          onSelectRow={(item) => {
            setSelectedStudy(item);
            setSeriesCount(item.seriesCount || 2);
            setInstanceCount(item.instanceCount || 120);
            setNewStatus(item.status || "COMPLETED");
          }}
        />

        {selectedStudy && (
          <div style={{ marginTop: "1rem", padding: "0.5rem", border: "1px solid #ccc" }}>
            <h4>
              Update Study: {selectedStudy.id} ({selectedStudy.accessionNumber})
            </h4>
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr 1fr",
                gap: "0.5rem",
                marginTop: "0.5rem",
              }}
            >
              <div>
                <label htmlFor="img-update-series">{labels.seriesCount}</label>
                <input
                  id="img-update-series"
                  type="number"
                  value={seriesCount}
                  onChange={(e) => setSeriesCount(Number(e.target.value))}
                />
              </div>
              <div>
                <label htmlFor="img-update-instances">{labels.instanceCount}</label>
                <input
                  id="img-update-instances"
                  type="number"
                  value={instanceCount}
                  onChange={(e) => setInstanceCount(Number(e.target.value))}
                />
              </div>
              <div>
                <label htmlFor="img-update-status-select">{shared.status}</label>
                <select
                  id="img-update-status-select"
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value)}
                >
                  <option value="CREATED">CREATED</option>
                  <option value="IN_PROGRESS">IN_PROGRESS</option>
                  <option value="COMPLETED">COMPLETED</option>
                  <option value="VERIFIED">VERIFIED</option>
                </select>
              </div>
            </div>
            <button
              type="button"
              id="img-update-study-btn"
              disabled={updateStatusState === "loading"}
              onClick={() => {
                void runUpdateStudy();
              }}
              style={{ marginTop: "0.5rem" }}
            >
              {shared.update}
            </button>
            <StatusBanner
              status={updateStatusState}
              errorMessage={updateError}
              successMessage={labels.studyUpdated}
            />
          </div>
        )}
      </div>
    </div>
  );
}
