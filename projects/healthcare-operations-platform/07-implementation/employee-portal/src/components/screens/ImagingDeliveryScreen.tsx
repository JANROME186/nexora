/**
 * Imaging Study Delivery administration screen (COM-MOD-014-FE-001 / BCM-IMG-008).
 *
 * Prepare study delivery packages (CD, DICOM/PDF, Patient Portal) and confirm delivery.
 */
import { useState } from "react";
import {
  createDeliveryPackage,
  listDeliveryPackagesForPatient,
  markDeliveryPackageDelivered,
  type ImagingDeliveryPackage,
} from "../../api/imagingOperationsApi";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

interface CreateDeliveryFormProps {
  onCreated: () => void;
}

function CreateDeliveryForm({ onCreated }: CreateDeliveryFormProps) {
  const { t } = useLocale();
  const labels = t.imagingOperations.delivery;
  const { scope } = useAdminScope();

  const [studyId, setStudyId] = useState("");
  const [patientId, setPatientId] = useState("");
  const [deliveryFormat, setDeliveryFormat] = useState("DICOM_PORTAL_PDF");

  const { status, errorMessage, run } = useAsyncAction(async () => {
    if (!studyId.trim() || !patientId.trim()) return;
    await createDeliveryPackage({
      studyId,
      patientId,
      deliveryFormat,
    });
    onCreated();
  });

  return (
    <div className="panel" style={{ marginBottom: "1rem" }}>
      <h3>{labels.createPackage}</h3>
      {!scope.tenantId && <p style={{ color: "orange" }}>{t.imagingOperations.shared.tenantRequired}</p>}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "0.5rem" }}>
        <div>
          <label htmlFor="del-study-id">{labels.studyId}</label>
          <input
            id="del-study-id"
            value={studyId}
            onChange={(e) => setStudyId(e.target.value)}
            placeholder="STUDY-1001"
          />
        </div>
        <div>
          <label htmlFor="del-patient-id">{labels.patientId}</label>
          <input
            id="del-patient-id"
            value={patientId}
            onChange={(e) => setPatientId(e.target.value)}
            placeholder="PAT-1001"
          />
        </div>
        <div>
          <label htmlFor="del-format">{labels.deliveryFormat}</label>
          <input
            id="del-format"
            value={deliveryFormat}
            onChange={(e) => setDeliveryFormat(e.target.value)}
          />
        </div>
      </div>
      <button
        type="button"
        id="del-create-btn"
        disabled={status === "loading" || !studyId.trim() || !patientId.trim()}
        onClick={() => { void run(); }}
        style={{ marginTop: "0.5rem" }}
      >
        {labels.createPackage}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.packageCreated}
      />
    </div>
  );
}

export function ImagingDeliveryScreen() {
  const { t } = useLocale();
  const labels = t.imagingOperations.delivery;
  const shared = t.imagingOperations.shared;

  const [searchPatientId, setSearchPatientId] = useState("");
  const [packages, setPackages] = useState<ImagingDeliveryPackage[]>([]);
  const [selectedPackage, setSelectedPackage] = useState<ImagingDeliveryPackage | null>(null);

  const { status: fetchStatus, errorMessage: fetchError, run: fetchPackages } = useAsyncAction(async () => {
    if (!searchPatientId.trim()) return;
    const res = await listDeliveryPackagesForPatient(searchPatientId.trim());
    setPackages(res);
  });

  const { status: deliverStatus, errorMessage: deliverError, run: runMarkDelivered } = useAsyncAction(async () => {
    if (!selectedPackage) return;
    const updated = await markDeliveryPackageDelivered(selectedPackage.id);
    setSelectedPackage(updated);
    if (searchPatientId.trim()) {
      await fetchPackages();
    }
  });

  const columns: DataTableColumn<ImagingDeliveryPackage>[] = [
    { key: "id", header: "ID", render: (item) => item.id },
    { key: "studyId", header: labels.studyId, render: (item) => item.studyId },
    { key: "patientId", header: labels.patientId, render: (item) => item.patientId },
    { key: "deliveryFormat", header: labels.deliveryFormat, render: (item) => item.deliveryFormat },
    { key: "status", header: shared.status, render: (item) => item.status },
  ];

  return (
    <div className="screen-container">
      <h2>{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />

      <CreateDeliveryForm onCreated={() => { if (searchPatientId) void fetchPackages(); }} />

      <div className="panel">
        <h3>{shared.search}</h3>
        <div style={{ display: "flex", gap: "0.5rem", marginBottom: "0.5rem" }}>
          <input
            id="del-search-patient-input"
            value={searchPatientId}
            onChange={(e) => setSearchPatientId(e.target.value)}
            placeholder="PAT-1001"
          />
          <button
            type="button"
            id="del-fetch-btn"
            disabled={fetchStatus === "loading" || !searchPatientId.trim()}
            onClick={() => { void fetchPackages(); }}
          >
            {shared.load}
          </button>
        </div>
        <StatusBanner status={fetchStatus} errorMessage={fetchError} />

        <DataTable
          caption={labels.heading}
          columns={columns}
          rows={packages}
          rowKey={(item) => item.id}
          onSelectRow={(item) => setSelectedPackage(item)}
        />

        {selectedPackage && (
          <div style={{ marginTop: "1rem", padding: "0.5rem", border: "1px solid #ccc" }}>
            <h4>Package Details: {selectedPackage.id}</h4>
            <p>{labels.studyId}: {selectedPackage.studyId} | {labels.patientId}: {selectedPackage.patientId}</p>
            <p>{labels.deliveryFormat}: {selectedPackage.deliveryFormat} | {shared.status}: <strong>{selectedPackage.status}</strong></p>

            {selectedPackage.status !== "DELIVERED" && (
              <button
                type="button"
                id="del-deliver-btn"
                disabled={deliverStatus === "loading"}
                onClick={() => { void runMarkDelivered(); }}
                style={{ marginTop: "0.5rem" }}
              >
                {labels.markDelivered}
              </button>
            )}
            <StatusBanner
              status={deliverStatus}
              errorMessage={deliverError}
              successMessage={labels.packageDelivered}
            />
          </div>
        )}
      </div>
    </div>
  );
}
