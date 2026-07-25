/**
 * Imaging Appointment Scheduling administration screen (COM-MOD-014-FE-001 / BCM-IMG-001).
 *
 * Schedule appointment slots, query slots by patient, and update slot status.
 */
import { useState } from "react";
import {
  listAppointmentSlotsForPatient,
  scheduleAppointmentSlot,
  updateAppointmentSlotStatus,
  type ImagingAppointmentSlot,
} from "../../api/imagingOperationsApi";
import { useLocale } from "../../i18n/LocaleContext";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

interface ScheduleSlotFormProps {
  onScheduled: () => void;
}

function ScheduleSlotForm({ onScheduled }: ScheduleSlotFormProps) {
  const { t } = useLocale();
  const labels = t.imagingOperations.appointments;
  const { scope } = useAdminScope();

  const [patientId, setPatientId] = useState("");
  const [modality, setModality] = useState("CT");
  const [procedureCode, setProcedureCode] = useState("CT-CHEST-001");
  const [procedureRoomId, setProcedureRoomId] = useState("ROOM-101");
  const [startTime, setStartTime] = useState(new Date().toISOString().substring(0, 16));
  const [durationMinutes, setDurationMinutes] = useState(30);
  const [notes, setNotes] = useState("");

  const { status, errorMessage, run } = useAsyncAction(async () => {
    if (!patientId.trim()) return;
    await scheduleAppointmentSlot({
      patientId,
      branchId: scope.branchId || "BRANCH-001",
      modality,
      procedureCode,
      procedureRoomId,
      startTime: new Date(startTime).toISOString(),
      durationMinutes: Number(durationMinutes),
      notes,
    });
    onScheduled();
  });

  return (
    <div className="panel" style={{ marginBottom: "1rem" }}>
      <h3>{labels.scheduleSlot}</h3>
      {!scope.tenantId && <p style={{ color: "orange" }}>{t.imagingOperations.shared.tenantRequired}</p>}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.5rem" }}>
        <div>
          <label htmlFor="img-slot-patient">{labels.patientId}</label>
          <input
            id="img-slot-patient"
            value={patientId}
            onChange={(e) => setPatientId(e.target.value)}
            placeholder="PAT-1001"
          />
        </div>
        <div>
          <label htmlFor="img-slot-branch">{labels.branchId}</label>
          <input
            id="img-slot-branch"
            value={scope.branchId || "BRANCH-001"}
            disabled
          />
        </div>
        <div>
          <label htmlFor="img-slot-modality">{labels.modality}</label>
          <input
            id="img-slot-modality"
            value={modality}
            onChange={(e) => setModality(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="img-slot-proc">{labels.procedureCode}</label>
          <input
            id="img-slot-proc"
            value={procedureCode}
            onChange={(e) => setProcedureCode(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="img-slot-room">{labels.procedureRoomId}</label>
          <input
            id="img-slot-room"
            value={procedureRoomId}
            onChange={(e) => setProcedureRoomId(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="img-slot-start">{labels.startTime}</label>
          <input
            id="img-slot-start"
            type="datetime-local"
            value={startTime}
            onChange={(e) => setStartTime(e.target.value)}
          />
        </div>
        <div>
          <label htmlFor="img-slot-duration">{labels.durationMinutes}</label>
          <input
            id="img-slot-duration"
            type="number"
            value={durationMinutes}
            onChange={(e) => setDurationMinutes(Number(e.target.value))}
          />
        </div>
        <div>
          <label htmlFor="img-slot-notes">{labels.notes}</label>
          <input
            id="img-slot-notes"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
          />
        </div>
      </div>
      <button
        type="button"
        id="img-slot-schedule-btn"
        disabled={status === "loading" || !patientId.trim()}
        onClick={() => { void run(); }}
        style={{ marginTop: "0.5rem" }}
      >
        {labels.scheduleSlot}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.slotScheduled}
      />
    </div>
  );
}

export function ImagingAppointmentsScreen() {
  const { t } = useLocale();
  const labels = t.imagingOperations.appointments;
  const shared = t.imagingOperations.shared;

  const [searchPatientId, setSearchPatientId] = useState("");
  const [slots, setSlots] = useState<ImagingAppointmentSlot[]>([]);
  const [selectedSlot, setSelectedSlot] = useState<ImagingAppointmentSlot | null>(null);
  const [newStatus, setNewStatus] = useState("CONFIRMED");

  const { status: fetchStatus, errorMessage: fetchError, run: fetchSlots } = useAsyncAction(async () => {
    if (!searchPatientId.trim()) return;
    const res = await listAppointmentSlotsForPatient(searchPatientId.trim());
    setSlots(res);
  });

  const { status: updateStatusState, errorMessage: updateError, run: runUpdateStatus } = useAsyncAction(async () => {
    if (!selectedSlot) return;
    const updated = await updateAppointmentSlotStatus(selectedSlot.id, newStatus);
    setSelectedSlot(updated);
    if (searchPatientId.trim()) {
      await fetchSlots();
    }
  });

  const columns: DataTableColumn<ImagingAppointmentSlot>[] = [
    { key: "id", header: "ID", render: (item) => item.id },
    { key: "patientId", header: labels.patientId, render: (item) => item.patientId },
    { key: "modality", header: labels.modality, render: (item) => item.modality },
    { key: "procedureCode", header: labels.procedureCode, render: (item) => item.procedureCode },
    { key: "procedureRoomId", header: labels.procedureRoomId, render: (item) => item.procedureRoomId },
    { key: "startTime", header: labels.startTime, render: (item) => item.startTime },
    { key: "status", header: shared.status, render: (item) => item.status },
  ];

  return (
    <div className="screen-container">
      <h2>{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />

      <ScheduleSlotForm onScheduled={() => { if (searchPatientId) void fetchSlots(); }} />

      <div className="panel">
        <h3>{shared.search}</h3>
        <div style={{ display: "flex", gap: "0.5rem", marginBottom: "0.5rem" }}>
          <input
            id="img-search-patient"
            value={searchPatientId}
            onChange={(e) => setSearchPatientId(e.target.value)}
            placeholder="PAT-1001"
          />
          <button
            type="button"
            id="img-fetch-slots-btn"
            disabled={fetchStatus === "loading" || !searchPatientId.trim()}
            onClick={() => { void fetchSlots(); }}
          >
            {shared.load}
          </button>
        </div>
        <StatusBanner status={fetchStatus} errorMessage={fetchError} />

        <DataTable
          caption={labels.heading}
          columns={columns}
          rows={slots}
          rowKey={(item) => item.id}
          onSelectRow={(item) => setSelectedSlot(item)}
        />

        {selectedSlot && (
          <div style={{ marginTop: "1rem", padding: "0.5rem", border: "1px solid #ccc" }}>
            <h4>{labels.updateStatus}: {selectedSlot.id}</h4>
            <p>{labels.patientId}: {selectedSlot.patientId} | {labels.modality}: {selectedSlot.modality} | {shared.status}: <strong>{selectedSlot.status}</strong></p>
            <div style={{ display: "flex", gap: "0.5rem", alignItems: "center" }}>
              <select
                id="img-status-select"
                value={newStatus}
                onChange={(e) => setNewStatus(e.target.value)}
              >
                <option value="SCHEDULED">SCHEDULED</option>
                <option value="CONFIRMED">CONFIRMED</option>
                <option value="CANCELLED">CANCELLED</option>
                <option value="COMPLETED">COMPLETED</option>
              </select>
              <button
                type="button"
                id="img-update-status-btn"
                disabled={updateStatusState === "loading"}
                onClick={() => { void runUpdateStatus(); }}
              >
                {shared.update}
              </button>
            </div>
            <StatusBanner
              status={updateStatusState}
              errorMessage={updateError}
              successMessage={labels.statusUpdated}
            />
          </div>
        )}
      </div>
    </div>
  );
}
