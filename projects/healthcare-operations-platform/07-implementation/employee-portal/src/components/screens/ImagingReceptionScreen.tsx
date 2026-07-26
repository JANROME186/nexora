/**
 * Imaging Reception Intake administration screen (COM-MOD-014-FE-001 / BCM-IMG-002).
 *
 * Perform patient check-in and preparation verification for imaging studies.
 */
import { useState } from "react";
import {
  checkInReception,
  getReceptionIntakeBySlot,
  type ImagingReceptionIntake,
} from "../../api/imagingOperationsApi";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

interface CheckInFormProps {
  onCheckInSuccess: (intake: ImagingReceptionIntake) => void;
}

function CheckInForm({ onCheckInSuccess }: CheckInFormProps) {
  const { t } = useLocale();
  const labels = t.imagingOperations.reception;
  const { scope } = useAdminScope();

  const [appointmentSlotId, setAppointmentSlotId] = useState("");
  const [patientId, setPatientId] = useState("");
  const [preparationVerified, setPreparationVerified] = useState(true);
  const [intakeNotes, setIntakeNotes] = useState("");

  const { status, errorMessage, run } = useAsyncAction(async () => {
    if (!appointmentSlotId.trim() || !patientId.trim()) return;
    const intake = await checkInReception({
      appointmentSlotId,
      patientId,
      preparationVerified,
      intakeNotes,
    });
    onCheckInSuccess(intake);
  });

  return (
    <div className="panel" style={{ marginBottom: "1rem" }}>
      <h3>{labels.checkIn}</h3>
      {!scope.tenantId && (
        <p style={{ color: "orange" }}>{t.imagingOperations.shared.tenantRequired}</p>
      )}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.5rem" }}>
        <div>
          <label htmlFor="img-rec-slot">{labels.slotId}</label>
          <input
            id="img-rec-slot"
            value={appointmentSlotId}
            onChange={(e) => setAppointmentSlotId(e.target.value)}
            placeholder="SLOT-1001"
          />
        </div>
        <div>
          <label htmlFor="img-rec-patient">{labels.patientId}</label>
          <input
            id="img-rec-patient"
            value={patientId}
            onChange={(e) => setPatientId(e.target.value)}
            placeholder="PAT-1001"
          />
        </div>
        <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", marginTop: "1.5rem" }}>
          <input
            id="img-rec-prep"
            type="checkbox"
            checked={preparationVerified}
            onChange={(e) => setPreparationVerified(e.target.checked)}
          />
          <label htmlFor="img-rec-prep">{labels.preparationVerified}</label>
        </div>
        <div>
          <label htmlFor="img-rec-notes">{labels.intakeNotes}</label>
          <input
            id="img-rec-notes"
            value={intakeNotes}
            onChange={(e) => setIntakeNotes(e.target.value)}
          />
        </div>
      </div>
      <button
        type="button"
        id="img-rec-checkin-btn"
        disabled={status === "loading" || !appointmentSlotId.trim() || !patientId.trim()}
        onClick={() => {
          void run();
        }}
        style={{ marginTop: "0.5rem" }}
      >
        {labels.checkIn}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.intakeSuccess}
      />
    </div>
  );
}

export function ImagingReceptionScreen() {
  const { t } = useLocale();
  const labels = t.imagingOperations.reception;
  const shared = t.imagingOperations.shared;

  const [querySlotId, setQuerySlotId] = useState("");
  const [currentIntake, setCurrentIntake] = useState<ImagingReceptionIntake | null>(null);

  const {
    status: fetchStatus,
    errorMessage: fetchError,
    run: fetchIntake,
  } = useAsyncAction(async () => {
    if (!querySlotId.trim()) return;
    const intake = await getReceptionIntakeBySlot(querySlotId.trim());
    setCurrentIntake(intake);
  });

  return (
    <div className="screen-container">
      <h2>{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />

      <CheckInForm onCheckInSuccess={(intake) => setCurrentIntake(intake)} />

      <div className="panel">
        <h3>{shared.search}</h3>
        <div style={{ display: "flex", gap: "0.5rem", marginBottom: "0.5rem" }}>
          <input
            id="img-query-slot-input"
            value={querySlotId}
            onChange={(e) => setQuerySlotId(e.target.value)}
            placeholder="SLOT-1001"
          />
          <button
            type="button"
            id="img-fetch-intake-btn"
            disabled={fetchStatus === "loading" || !querySlotId.trim()}
            onClick={() => {
              void fetchIntake();
            }}
          >
            {shared.load}
          </button>
        </div>
        <StatusBanner status={fetchStatus} errorMessage={fetchError} />

        {currentIntake && (
          <div style={{ marginTop: "1rem", padding: "0.5rem", border: "1px solid #ccc" }}>
            <h4>Intake: {currentIntake.id}</h4>
            <p>
              {labels.slotId}: {currentIntake.appointmentSlotId} | {labels.patientId}:{" "}
              {currentIntake.patientId}
            </p>
            <p>
              {labels.preparationVerified}: {currentIntake.preparationVerified ? "Yes" : "No"} |{" "}
              {shared.status}: <strong>{currentIntake.status}</strong>
            </p>
            {currentIntake.intakeNotes && (
              <p>
                {labels.intakeNotes}: {currentIntake.intakeNotes}
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
