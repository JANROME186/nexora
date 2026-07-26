/**
 * Radiology Signature & Report administration screen (COM-MOD-014-FE-001 / BCM-IMG-007).
 *
 * Draft findings and diagnostic impressions for radiology reports, and execute digital signatures.
 */
import { useState } from "react";
import {
  createRadiologyReport,
  listRadiologyReportsForStudy,
  signRadiologyReport,
  type RadiologyReport,
} from "../../api/imagingOperationsApi";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

interface CreateReportFormProps {
  onCreated: () => void;
}

function CreateReportForm({ onCreated }: CreateReportFormProps) {
  const { t } = useLocale();
  const labels = t.imagingOperations.reports;
  const { scope } = useAdminScope();

  const [studyId, setStudyId] = useState("");
  const [findingsText, setFindingsText] = useState(
    "Multidetector CT of the chest demonstrates normal cardiac size and clear lungs.",
  );
  const [impressionText, setImpressionText] = useState("No acute cardiopulmonary process.");

  const { status, errorMessage, run } = useAsyncAction(async () => {
    if (!studyId.trim() || !findingsText.trim()) return;
    await createRadiologyReport({
      studyId,
      findingsText,
      impressionText,
    });
    onCreated();
  });

  return (
    <div className="panel" style={{ marginBottom: "1rem" }}>
      <h3>{labels.createReport}</h3>
      {!scope.tenantId && (
        <p style={{ color: "orange" }}>{t.imagingOperations.shared.tenantRequired}</p>
      )}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.5rem" }}>
        <div style={{ gridColumn: "span 2" }}>
          <label htmlFor="rep-study-id">{labels.studyId}</label>
          <input
            id="rep-study-id"
            value={studyId}
            onChange={(e) => setStudyId(e.target.value)}
            placeholder="STUDY-1001"
          />
        </div>
        <div>
          <label htmlFor="rep-findings">{labels.findingsText}</label>
          <textarea
            id="rep-findings"
            rows={3}
            value={findingsText}
            onChange={(e) => setFindingsText(e.target.value)}
            style={{ width: "100%" }}
          />
        </div>
        <div>
          <label htmlFor="rep-impression">{labels.impressionText}</label>
          <textarea
            id="rep-impression"
            rows={3}
            value={impressionText}
            onChange={(e) => setImpressionText(e.target.value)}
            style={{ width: "100%" }}
          />
        </div>
      </div>
      <button
        type="button"
        id="rep-create-btn"
        disabled={status === "loading" || !studyId.trim() || !findingsText.trim()}
        onClick={() => {
          void run();
        }}
        style={{ marginTop: "0.5rem" }}
      >
        {labels.createReport}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.reportCreated}
      />
    </div>
  );
}

export function ImagingReportsScreen() {
  const { t } = useLocale();
  const labels = t.imagingOperations.reports;
  const shared = t.imagingOperations.shared;

  const [searchStudyId, setSearchStudyId] = useState("");
  const [reports, setReports] = useState<RadiologyReport[]>([]);
  const [selectedReport, setSelectedReport] = useState<RadiologyReport | null>(null);

  const {
    status: fetchStatus,
    errorMessage: fetchError,
    run: fetchReports,
  } = useAsyncAction(async () => {
    if (!searchStudyId.trim()) return;
    const res = await listRadiologyReportsForStudy(searchStudyId.trim());
    setReports(res);
  });

  const {
    status: signStatus,
    errorMessage: signError,
    run: runSignReport,
  } = useAsyncAction(async () => {
    if (!selectedReport) return;
    const signed = await signRadiologyReport(selectedReport.id);
    setSelectedReport(signed);
    if (searchStudyId.trim()) {
      await fetchReports();
    }
  });

  const columns: DataTableColumn<RadiologyReport>[] = [
    { key: "id", header: "ID", render: (item) => item.id },
    { key: "studyId", header: labels.studyId, render: (item) => item.studyId },
    { key: "findingsText", header: labels.findingsText, render: (item) => item.findingsText },
    { key: "impressionText", header: labels.impressionText, render: (item) => item.impressionText },
    { key: "signed", header: "Signed", render: (item) => (item.signed ? "Yes" : "No") },
  ];

  return (
    <div className="screen-container">
      <h2>{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />
      <CreateReportForm
        onCreated={() => {
          if (searchStudyId) void fetchReports();
        }}
      />
      <div className="panel">
        <h3>{shared.search}</h3>
        <div style={{ display: "flex", gap: "0.5rem", marginBottom: "0.5rem" }}>
          <input
            id="rep-search-study-input"
            value={searchStudyId}
            onChange={(e) => setSearchStudyId(e.target.value)}
            placeholder="STUDY-1001"
          />
          <button
            type="button"
            id="rep-fetch-btn"
            disabled={fetchStatus === "loading" || !searchStudyId.trim()}
            onClick={() => {
              void fetchReports();
            }}
          >
            {shared.load}
          </button>
        </div>
        <StatusBanner status={fetchStatus} errorMessage={fetchError} />

        <DataTable
          caption={labels.heading}
          columns={columns}
          rows={reports}
          rowKey={(item) => item.id}
          onSelectRow={(item) => setSelectedReport(item)}
        />

        {selectedReport && (
          <div style={{ marginTop: "1rem", padding: "0.5rem", border: "1px solid #ccc" }}>
            <h4>Report Details: {selectedReport.id}</h4>
            <p>
              <strong>{labels.findingsText}:</strong> {selectedReport.findingsText}
            </p>
            <p>
              <strong>{labels.impressionText}:</strong> {selectedReport.impressionText}
            </p>
            <p>
              <strong>Signed:</strong>{" "}
              {selectedReport.signed
                ? `Yes (by ${selectedReport.signedBy} at ${selectedReport.signedAt})`
                : "No"}
            </p>

            {!selectedReport.signed && (
              <button
                type="button"
                id="rep-sign-btn"
                disabled={signStatus === "loading"}
                onClick={() => {
                  void runSignReport();
                }}
                style={{ marginTop: "0.5rem" }}
              >
                {labels.signReport}
              </button>
            )}
            <StatusBanner
              status={signStatus}
              errorMessage={signError}
              successMessage={labels.reportSigned}
            />
          </div>
        )}
      </div>
    </div>
  );
}
