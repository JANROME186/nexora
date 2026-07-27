import { useState, type FormEvent } from "react";
import {
  cancelAppointment,
  checkInAppointment,
  confirmAppointment,
  listAppointments,
  markAppointmentNoShow,
  requestAppointment,
} from "../../api/frontDeskApi";
import type { AppointmentSlot, RequestedItemInput } from "../../api/types";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncActionState } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { statusClass } from "../common/statusPresentation";

const SELECT_APPOINTMENT_FIRST = "Select an appointment first.";

function newRequestedItem(): RequestedItemInput {
  return { testDefinitionId: "", catalogItemKind: "test" };
}

const columns: DataTableColumn<AppointmentSlot>[] = [
  { key: "appointmentId", header: "Appointment id", render: (row) => row.appointmentId },
  { key: "patientId", header: "Patient", render: (row) => row.patientId ?? "—" },
  { key: "scheduledStart", header: "Scheduled start", render: (row) => row.scheduledStart ?? "—" },
  { key: "channel", header: "Channel", render: (row) => row.channel },
  {
    key: "status",
    header: "Status",
    render: (row) => <span className={statusClass(row.status)}>{row.status}</span>,
  },
];

interface RequestedItemsEditorProps {
  items: RequestedItemInput[];
  onUpdate: (index: number, patch: Partial<RequestedItemInput>) => void;
  onRemove: (index: number) => void;
  onAdd: () => void;
}

/** Decomposed per TD-FE-010's shared remediation pattern: keeps the top-level screen component's
 * render function within the configured function-size lint threshold. */
function RequestedItemsEditor({ items, onUpdate, onRemove, onAdd }: RequestedItemsEditorProps) {
  return (
    <>
      <h4>Requested items (optional)</h4>
      {items.map((item, index) => (
        <div className="order-line-row" key={index}>
          <label htmlFor={`appointment-item-kind-${index}`}>Kind</label>
          <select
            id={`appointment-item-kind-${index}`}
            value={item.catalogItemKind}
            onChange={(event) => onUpdate(index, { catalogItemKind: event.target.value })}
          >
            <option value="test">Test</option>
            <option value="panel">Panel</option>
          </select>
          <label htmlFor={`appointment-item-test-id-${index}`}>Catalog item id</label>
          <input
            id={`appointment-item-test-id-${index}`}
            value={item.testDefinitionId}
            onChange={(event) => onUpdate(index, { testDefinitionId: event.target.value })}
          />
          {items.length > 0 ? (
            <button type="button" onClick={() => onRemove(index)}>
              Remove item
            </button>
          ) : null}
        </div>
      ))}
      <button type="button" onClick={onAdd}>
        Add requested item
      </button>
    </>
  );
}

interface RequestAppointmentFormProps {
  canUse: boolean;
  patientId: string;
  onPatientIdChange: (value: string) => void;
  doctorId: string;
  onDoctorIdChange: (value: string) => void;
  scheduledStart: string;
  onScheduledStartChange: (value: string) => void;
  scheduledEnd: string;
  onScheduledEndChange: (value: string) => void;
  requestedItems: RequestedItemInput[];
  onUpdateItem: (index: number, patch: Partial<RequestedItemInput>) => void;
  onRemoveItem: (index: number) => void;
  onAddItem: () => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  requestAction: AsyncActionState<AppointmentSlot>;
}

/** Decomposed per TD-FE-010's shared remediation pattern. */
function RequestAppointmentForm({
  canUse,
  patientId,
  onPatientIdChange,
  doctorId,
  onDoctorIdChange,
  scheduledStart,
  onScheduledStartChange,
  scheduledEnd,
  onScheduledEndChange,
  requestedItems,
  onUpdateItem,
  onRemoveItem,
  onAddItem,
  onSubmit,
  requestAction,
}: RequestAppointmentFormProps) {
  return (
    <div className="panel">
      <h3>Request appointment</h3>
      <form onSubmit={onSubmit}>
        <label htmlFor="appointment-patient-id">Patient id</label>
        <input
          id="appointment-patient-id"
          value={patientId}
          onChange={(event) => onPatientIdChange(event.target.value)}
          required
        />
        <label htmlFor="appointment-doctor-id">Referring doctor id (optional)</label>
        <input
          id="appointment-doctor-id"
          value={doctorId}
          onChange={(event) => onDoctorIdChange(event.target.value)}
        />
        <label htmlFor="appointment-scheduled-start">Scheduled start</label>
        <input
          id="appointment-scheduled-start"
          type="date"
          value={scheduledStart}
          onChange={(event) => onScheduledStartChange(event.target.value)}
        />
        <label htmlFor="appointment-scheduled-end">Scheduled end</label>
        <input
          id="appointment-scheduled-end"
          type="date"
          value={scheduledEnd}
          onChange={(event) => onScheduledEndChange(event.target.value)}
        />

        <RequestedItemsEditor
          items={requestedItems}
          onUpdate={onUpdateItem}
          onRemove={onRemoveItem}
          onAdd={onAddItem}
        />

        <button type="submit" disabled={!canUse || requestAction.status === "loading"}>
          Request appointment
        </button>
        <StatusBanner
          status={requestAction.status}
          errorMessage={requestAction.errorMessage}
          successMessage="Appointment requested."
        />
      </form>
    </div>
  );
}

interface AppointmentDetailPanelProps {
  appointment: AppointmentSlot;
  confirmAction: AsyncActionState<AppointmentSlot>;
  checkInAction: AsyncActionState<AppointmentSlot>;
  noShowAction: AsyncActionState<AppointmentSlot>;
  cancelAction: AsyncActionState<AppointmentSlot>;
  cancelReason: string;
  onCancelReasonChange: (value: string) => void;
  onConfirm: () => void;
  onCheckIn: () => void;
  onNoShow: () => void;
  onRequestCancel: () => void;
}

function AppointmentDetailPanel({
  appointment,
  confirmAction,
  checkInAction,
  noShowAction,
  cancelAction,
  cancelReason,
  onCancelReasonChange,
  onConfirm,
  onCheckIn,
  onNoShow,
  onRequestCancel,
}: AppointmentDetailPanelProps) {
  const isOpen = appointment.status === "requested" || appointment.status === "confirmed";

  return (
    <div className="panel">
      <h3>Appointment detail: {appointment.appointmentId}</h3>
      <table>
        <tbody>
          <tr>
            <th scope="row">Patient</th>
            <td>{appointment.patientId ?? appointment.prospectiveFullName ?? "—"}</td>
          </tr>
          <tr>
            <th scope="row">Doctor</th>
            <td>{appointment.doctorId ?? "None"}</td>
          </tr>
          <tr>
            <th scope="row">Scheduled</th>
            <td>
              {appointment.scheduledStart ?? "—"} - {appointment.scheduledEnd ?? "—"}
            </td>
          </tr>
          <tr>
            <th scope="row">Status</th>
            <td>
              <span className={statusClass(appointment.status)}>{appointment.status}</span>
            </td>
          </tr>
        </tbody>
      </table>

      {appointment.status === "requested" ? (
        <button type="button" disabled={confirmAction.status === "loading"} onClick={onConfirm}>
          Confirm appointment
        </button>
      ) : null}
      <StatusBanner
        status={confirmAction.status}
        errorMessage={confirmAction.errorMessage}
        successMessage="Appointment confirmed."
      />

      {appointment.status === "confirmed" ? (
        <button type="button" disabled={checkInAction.status === "loading"} onClick={onCheckIn}>
          Check in
        </button>
      ) : null}
      <StatusBanner
        status={checkInAction.status}
        errorMessage={checkInAction.errorMessage}
        successMessage="Patient checked in."
      />

      {appointment.status === "confirmed" ? (
        <button type="button" disabled={noShowAction.status === "loading"} onClick={onNoShow}>
          Mark as no-show
        </button>
      ) : null}
      <StatusBanner
        status={noShowAction.status}
        errorMessage={noShowAction.errorMessage}
        successMessage="Appointment marked as no-show."
      />

      {isOpen ? (
        <div className="panel">
          <h4>Cancel appointment</h4>
          <label htmlFor="appointment-cancel-reason">Reason code (optional)</label>
          <input
            id="appointment-cancel-reason"
            value={cancelReason}
            onChange={(event) => onCancelReasonChange(event.target.value)}
          />
          <button
            type="button"
            disabled={cancelAction.status === "loading"}
            onClick={onRequestCancel}
          >
            Cancel appointment
          </button>
        </div>
      ) : null}
      <StatusBanner
        status={cancelAction.status}
        errorMessage={cancelAction.errorMessage}
        successMessage="Appointment cancelled."
      />
    </div>
  );
}

/** Decomposed per TD-FE-010's shared remediation pattern: bundles this screen's state and async
 * actions into a dedicated hook so the top-level component's render function stays within the
 * configured function-size lint threshold. */
function useAppointmentsScreenState(tenantId?: string, laboratoryId?: string, branchId?: string) {
  const [appointments, setAppointments] = useState<AppointmentSlot[]>([]);
  const [selected, setSelected] = useState<AppointmentSlot | undefined>(undefined);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before listing appointments.");
    const loaded = await listAppointments(tenantId);
    setAppointments(loaded);
    return loaded;
  });

  const [patientId, setPatientId] = useState("");
  const [doctorId, setDoctorId] = useState("");
  const [scheduledStart, setScheduledStart] = useState("");
  const [scheduledEnd, setScheduledEnd] = useState("");
  const [requestedItems, setRequestedItems] = useState<RequestedItemInput[]>([]);

  const requestAction = useAsyncAction(async () => {
    if (!tenantId || !laboratoryId || !branchId) {
      throw new Error("Select tenant, laboratory and branch before requesting an appointment.");
    }
    const created = await requestAppointment({
      tenantId,
      laboratoryId,
      branchId,
      patientId,
      doctorId: doctorId || undefined,
      scheduledStart: scheduledStart || undefined,
      scheduledEnd: scheduledEnd || undefined,
      channel: "employee_portal",
      requestedItems: requestedItems.filter((item) => item.testDefinitionId),
    });
    setAppointments((current) => [created, ...current]);
    setSelected(created);
    setPatientId("");
    setDoctorId("");
    setScheduledStart("");
    setScheduledEnd("");
    setRequestedItems([]);
    return created;
  });

  const confirmAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_APPOINTMENT_FIRST);
    return confirmAppointment(selected.appointmentId);
  });
  const checkInAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_APPOINTMENT_FIRST);
    return checkInAppointment(selected.appointmentId);
  });
  const noShowAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_APPOINTMENT_FIRST);
    return markAppointmentNoShow(selected.appointmentId);
  });
  const [cancelReason, setCancelReason] = useState("");
  const [confirmingCancel, setConfirmingCancel] = useState(false);
  const cancelAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_APPOINTMENT_FIRST);
    return cancelAppointment(selected.appointmentId, { reasonCode: cancelReason || undefined });
  });

  function applyUpdated(updated: AppointmentSlot) {
    setSelected(updated);
    setAppointments((current) =>
      current.map((appointment) =>
        appointment.appointmentId === updated.appointmentId ? updated : appointment,
      ),
    );
  }

  function selectAppointment(appointment: AppointmentSlot) {
    setSelected(appointment);
    setCancelReason("");
    confirmAction.reset();
    checkInAction.reset();
    noShowAction.reset();
    cancelAction.reset();
  }

  async function handleRequest(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await requestAction.run();
  }

  async function handleConfirm() {
    const result = await confirmAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleCheckIn() {
    const result = await checkInAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleNoShow() {
    const result = await noShowAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  return {
    appointments,
    selected,
    listAction,
    patientId,
    setPatientId,
    doctorId,
    setDoctorId,
    scheduledStart,
    setScheduledStart,
    scheduledEnd,
    setScheduledEnd,
    requestedItems,
    setRequestedItems,
    requestAction,
    confirmAction,
    checkInAction,
    noShowAction,
    cancelReason,
    setCancelReason,
    confirmingCancel,
    setConfirmingCancel,
    cancelAction,
    applyUpdated,
    selectAppointment,
    handleRequest,
    handleConfirm,
    handleCheckIn,
    handleNoShow,
  };
}

/**
 * BCM-ATT-001 employee portal surface (TD-FE-006 remediation): staff-initiated appointment
 * scheduling (request/confirm/check-in/cancel/no-show), complementing the existing
 * PublicAppointmentRequestsScreen (public-website triage of the same aggregate).
 */
export function AppointmentsScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);
  const {
    appointments,
    selected,
    listAction,
    patientId,
    setPatientId,
    doctorId,
    setDoctorId,
    scheduledStart,
    setScheduledStart,
    scheduledEnd,
    setScheduledEnd,
    requestedItems,
    setRequestedItems,
    requestAction,
    confirmAction,
    checkInAction,
    noShowAction,
    cancelReason,
    setCancelReason,
    confirmingCancel,
    setConfirmingCancel,
    cancelAction,
    applyUpdated,
    selectAppointment,
    handleRequest,
    handleConfirm,
    handleCheckIn,
    handleNoShow,
  } = useAppointmentsScreenState(tenantId, laboratoryId, branchId);

  return (
    <section aria-labelledby="appointments-heading">
      <h2 id="appointments-heading">Appointments</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before scheduling appointments.
        </p>
      ) : null}

      <RequestAppointmentForm
        canUse={canUse}
        patientId={patientId}
        onPatientIdChange={setPatientId}
        doctorId={doctorId}
        onDoctorIdChange={setDoctorId}
        scheduledStart={scheduledStart}
        onScheduledStartChange={setScheduledStart}
        scheduledEnd={scheduledEnd}
        onScheduledEndChange={setScheduledEnd}
        requestedItems={requestedItems}
        onUpdateItem={(index, patch) =>
          setRequestedItems((current) =>
            current.map((item, i) => (i === index ? { ...item, ...patch } : item)),
          )
        }
        onRemoveItem={(index) =>
          setRequestedItems((current) => current.filter((_, i) => i !== index))
        }
        onAddItem={() => setRequestedItems((current) => [...current, newRequestedItem()])}
        onSubmit={handleRequest}
        requestAction={requestAction}
      />

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        Load appointments
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Appointments loaded."
      />
      {listAction.status === "success" && appointments.length === 0 ? (
        <p className="empty-state">No appointments exist yet for this tenant.</p>
      ) : null}

      <DataTable
        caption="Appointments"
        columns={columns}
        rows={appointments}
        rowKey={(row) => row.appointmentId}
        onSelectRow={selectAppointment}
      />

      {selected ? (
        <AppointmentDetailPanel
          appointment={selected}
          confirmAction={confirmAction}
          checkInAction={checkInAction}
          noShowAction={noShowAction}
          cancelAction={cancelAction}
          cancelReason={cancelReason}
          onCancelReasonChange={setCancelReason}
          onConfirm={() => void handleConfirm()}
          onCheckIn={() => void handleCheckIn()}
          onNoShow={() => void handleNoShow()}
          onRequestCancel={() => setConfirmingCancel(true)}
        />
      ) : (
        <p className="empty-state">Select an appointment row to view its detail and take action.</p>
      )}

      <ConfirmDialog
        open={confirmingCancel}
        title="Confirm cancellation"
        description="This appointment will be marked as cancelled. Continue?"
        onCancel={() => setConfirmingCancel(false)}
        onConfirm={async () => {
          setConfirmingCancel(false);
          const result = await cancelAction.run();
          if (result.ok) applyUpdated(result.data);
        }}
      />
    </section>
  );
}
