/**
 * Medical Dictation administration screen (COM-MOD-014-FE-001 / BCM-IMG-006).
 *
 * Register radiologist dictations for imaging studies and query dictations for a study.
 */
import { useState } from "react";
import {
  createDictation,
  listDictationsForStudy,
  type RadiologyDictation,
} from "../../api/imagingOperationsApi";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

interface CreateDictationFormProps {
  onCreated: () => void;
}

function CreateDictationForm({ onCreated }: CreateDictationFormProps) {
  const { t } = useLocale();
  const labels = t.imagingOperations.dictation;
  const { scope } = useAdminScope();

  const [studyId, setStudyId] = useState("");
  const [dictationText, setDictationText] = useState(
    "Patient exhibits normal lung parenchyma with no focal opacities.",
  );
  const [audioReferenceUrl, setAudioReferenceUrl] = useState(
    "https://storage.nexora.com/audio/dictation-1001.mp3",
  );

  const { status, errorMessage, run } = useAsyncAction(async () => {
    if (!studyId.trim() || !dictationText.trim()) return;
    await createDictation({
      studyId,
      dictationText,
      audioReferenceUrl,
    });
    onCreated();
  });

  return (
    <div className="panel" style={{ marginBottom: "1rem" }}>
      <h3>{labels.createDictation}</h3>
      {!scope.tenantId && (
        <p style={{ color: "orange" }}>{t.imagingOperations.shared.tenantRequired}</p>
      )}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.5rem" }}>
        <div>
          <label htmlFor="dict-study-id">{labels.studyId}</label>
          <input
            id="dict-study-id"
            value={studyId}
            onChange={(e) => setStudyId(e.target.value)}
            placeholder="STUDY-1001"
          />
        </div>
        <div>
          <label htmlFor="dict-audio-url">{labels.audioUrl}</label>
          <input
            id="dict-audio-url"
            value={audioReferenceUrl}
            onChange={(e) => setAudioReferenceUrl(e.target.value)}
          />
        </div>
        <div style={{ gridColumn: "span 2" }}>
          <label htmlFor="dict-text">{labels.dictationText}</label>
          <textarea
            id="dict-text"
            rows={3}
            value={dictationText}
            onChange={(e) => setDictationText(e.target.value)}
            style={{ width: "100%" }}
          />
        </div>
      </div>
      <button
        type="button"
        id="dict-create-btn"
        disabled={status === "loading" || !studyId.trim() || !dictationText.trim()}
        onClick={() => {
          void run();
        }}
        style={{ marginTop: "0.5rem" }}
      >
        {labels.createDictation}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.dictationCreated}
      />
    </div>
  );
}

export function ImagingDictationScreen() {
  const { t } = useLocale();
  const labels = t.imagingOperations.dictation;
  const shared = t.imagingOperations.shared;

  const [searchStudyId, setSearchStudyId] = useState("");
  const [dictations, setDictations] = useState<RadiologyDictation[]>([]);

  const {
    status: fetchStatus,
    errorMessage: fetchError,
    run: fetchDictations,
  } = useAsyncAction(async () => {
    if (!searchStudyId.trim()) return;
    const res = await listDictationsForStudy(searchStudyId.trim());
    setDictations(res);
  });

  const columns: DataTableColumn<RadiologyDictation>[] = [
    { key: "id", header: "ID", render: (item) => item.id },
    { key: "studyId", header: labels.studyId, render: (item) => item.studyId },
    { key: "dictationText", header: labels.dictationText, render: (item) => item.dictationText },
    { key: "status", header: shared.status, render: (item) => item.status },
    { key: "dictatedAt", header: "Timestamp", render: (item) => item.dictatedAt || "N/A" },
  ];

  return (
    <div className="screen-container">
      <h2>{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />

      <CreateDictationForm
        onCreated={() => {
          if (searchStudyId) void fetchDictations();
        }}
      />

      <div className="panel">
        <h3>{shared.search}</h3>
        <div style={{ display: "flex", gap: "0.5rem", marginBottom: "0.5rem" }}>
          <input
            id="dict-search-study-input"
            value={searchStudyId}
            onChange={(e) => setSearchStudyId(e.target.value)}
            placeholder="STUDY-1001"
          />
          <button
            type="button"
            id="dict-fetch-btn"
            disabled={fetchStatus === "loading" || !searchStudyId.trim()}
            onClick={() => {
              void fetchDictations();
            }}
          >
            {shared.load}
          </button>
        </div>
        <StatusBanner status={fetchStatus} errorMessage={fetchError} />

        <DataTable
          caption={labels.heading}
          columns={columns}
          rows={dictations}
          rowKey={(item) => item.id}
        />
      </div>
    </div>
  );
}
