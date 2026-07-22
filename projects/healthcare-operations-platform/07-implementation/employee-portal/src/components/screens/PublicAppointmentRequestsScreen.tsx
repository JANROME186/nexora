/**
 * Public Appointment Requests screen (COM-MOD-011-FE-001, BCM-ATT-001).
 *
 * Staff triage queue for anonymous public-website appointment requests. Reuses the existing
 * internal GET /api/care-delivery/appointments listing and the existing confirm/cancel action
 * endpoints (no new backend endpoint) — the queue is derived by filtering the tenant's
 * appointments client-side to channel=="public_website" && status=="requested", since those two
 * fields already fully identify a pending public request.
 */
import { useMemo, useState } from "react";
import {
  cancelAppointment,
  confirmAppointment,
  listAppointments,
} from "../../api/publicRequestsApi";
import type { AppointmentSlot } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { statusClass } from "../common/statusPresentation";

type Labels = MessageCatalog["publicAppointmentRequests"];
type ActionHandle = { status: AsyncStatus; errorMessage?: string };

function isPublicPendingRequest(appointment: AppointmentSlot): boolean {
  return appointment.channel === "public_website" && appointment.status === "requested";
}

function contactLabel(appointment: AppointmentSlot): string {
  return (
    appointment.prospectiveFullName ||
    appointment.prospectiveEmail ||
    appointment.prospectivePhone ||
    "—"
  );
}

function columns(labels: Labels): DataTableColumn<AppointmentSlot>[] {
  return [
    {
      key: "appointmentId",
      header: labels.columns.appointmentId,
      render: (row) => row.appointmentId,
    },
    { key: "contact", header: labels.columns.contact, render: (row) => contactLabel(row) },
    {
      key: "scheduledStart",
      header: labels.columns.scheduledStart,
      render: (row) => row.scheduledStart,
    },
    { key: "scheduledEnd", header: labels.columns.scheduledEnd, render: (row) => row.scheduledEnd },
    {
      key: "status",
      header: labels.columns.status,
      render: (row) => <span className={statusClass(row.status)}>{row.status}</span>,
    },
  ];
}

interface DetailPanelProps {
  labels: Labels;
  appointment: AppointmentSlot;
  reasonCode: string;
  onReasonCodeChange: (value: string) => void;
  onConfirm: () => void;
  onReject: () => void;
  confirmAction: ActionHandle;
  rejectAction: ActionHandle;
}

function DetailPanel({
  labels,
  appointment,
  reasonCode,
  onReasonCodeChange,
  onConfirm,
  onReject,
  confirmAction,
  rejectAction,
}: DetailPanelProps) {
  const pending = appointment.status === "requested";
  return (
    <div className="panel">
      <h3>{labels.detail.heading}</h3>
      <table>
        <tbody>
          <tr>
            <th scope="row">{labels.detail.contactName}</th>
            <td>{appointment.prospectiveFullName || "—"}</td>
          </tr>
          <tr>
            <th scope="row">{labels.detail.contactPhone}</th>
            <td>{appointment.prospectivePhone || "—"}</td>
          </tr>
          <tr>
            <th scope="row">{labels.detail.contactEmail}</th>
            <td>{appointment.prospectiveEmail || "—"}</td>
          </tr>
          <tr>
            <th scope="row">{labels.detail.scheduledStart}</th>
            <td>{appointment.scheduledStart}</td>
          </tr>
          <tr>
            <th scope="row">{labels.detail.scheduledEnd}</th>
            <td>{appointment.scheduledEnd}</td>
          </tr>
          <tr>
            <th scope="row">{labels.detail.status}</th>
            <td>
              <span className={statusClass(appointment.status)}>{appointment.status}</span>
            </td>
          </tr>
        </tbody>
      </table>

      {pending ? (
        <div className="catalog-toolbar">
          <button type="button" disabled={confirmAction.status === "loading"} onClick={onConfirm}>
            {labels.actions.confirmAppointment}
          </button>
        </div>
      ) : null}
      <StatusBanner
        status={confirmAction.status}
        errorMessage={confirmAction.errorMessage}
        successMessage={labels.success.confirmed}
      />

      {pending ? (
        <div className="panel" style={{ marginTop: "1rem" }}>
          <label htmlFor="appointment-reject-reason">{labels.actions.reasonCode}</label>
          <input
            id="appointment-reject-reason"
            value={reasonCode}
            onChange={(event) => onReasonCodeChange(event.target.value)}
          />
          <button type="button" disabled={rejectAction.status === "loading"} onClick={onReject}>
            {labels.actions.rejectAppointment}
          </button>
        </div>
      ) : null}
      <StatusBanner
        status={rejectAction.status}
        errorMessage={rejectAction.errorMessage}
        successMessage={labels.success.rejected}
      />
    </div>
  );
}

export function PublicAppointmentRequestsScreen() {
  const { t } = useLocale();
  const labels = t.publicAppointmentRequests;
  const { scope } = useAdminScope();
  const { tenantId } = scope;

  const [appointments, setAppointments] = useState<AppointmentSlot[]>([]);
  const [selected, setSelected] = useState<AppointmentSlot | undefined>(undefined);
  const [reasonCode, setReasonCode] = useState("");
  const [confirmingConfirm, setConfirmingConfirm] = useState(false);
  const [confirmingReject, setConfirmingReject] = useState(false);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) {
      throw new Error(labels.shared.tenantRequired);
    }
    const loaded = (await listAppointments(tenantId)).filter(isPublicPendingRequest);
    setAppointments(loaded);
    return loaded;
  });

  function applyActionOutcome(updated: AppointmentSlot) {
    setAppointments((current) =>
      current.filter((item) => item.appointmentId !== updated.appointmentId),
    );
    setSelected(updated);
    setReasonCode("");
  }

  const confirmAction = useAsyncAction(async () => {
    if (!selected) {
      throw new Error(labels.shared.selectFirst);
    }
    const updated = await confirmAppointment(selected.appointmentId);
    applyActionOutcome(updated);
    return updated;
  });

  const rejectAction = useAsyncAction(async () => {
    if (!selected) {
      throw new Error(labels.shared.selectFirst);
    }
    const updated = await cancelAppointment(selected.appointmentId, {
      reasonCode: reasonCode || undefined,
    });
    applyActionOutcome(updated);
    return updated;
  });

  function selectAppointment(appointment: AppointmentSlot) {
    setSelected(appointment);
    setReasonCode("");
    confirmAction.reset();
    rejectAction.reset();
  }

  const tableColumns = useMemo(() => columns(labels), [labels]);

  return (
    <section aria-labelledby="public-appointment-requests-heading">
      <h2 id="public-appointment-requests-heading">{labels.heading}</h2>
      <p>{labels.description}</p>
      <ScopeIndicator />
      {!tenantId ? (
        <p className="status-banner status-banner--error">{labels.shared.tenantRequired}</p>
      ) : null}

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        {labels.shared.load}
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && appointments.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={tableColumns}
        rows={appointments}
        rowKey={(row) => row.appointmentId}
        onSelectRow={selectAppointment}
      />

      {selected ? (
        <DetailPanel
          labels={labels}
          appointment={selected}
          reasonCode={reasonCode}
          onReasonCodeChange={setReasonCode}
          onConfirm={() => setConfirmingConfirm(true)}
          onReject={() => setConfirmingReject(true)}
          confirmAction={confirmAction}
          rejectAction={rejectAction}
        />
      ) : (
        <p className="empty-state">{labels.shared.selectFirst}</p>
      )}

      <ConfirmDialog
        open={confirmingConfirm}
        title={labels.confirmAppointmentDialog.title}
        description={labels.confirmAppointmentDialog.description}
        confirmLabel={labels.shared.dialogConfirm}
        cancelLabel={labels.shared.dialogCancel}
        onCancel={() => setConfirmingConfirm(false)}
        onConfirm={async () => {
          setConfirmingConfirm(false);
          await confirmAction.run();
        }}
      />
      <ConfirmDialog
        open={confirmingReject}
        title={labels.rejectAppointmentDialog.title}
        description={labels.rejectAppointmentDialog.description}
        confirmLabel={labels.shared.dialogConfirm}
        cancelLabel={labels.shared.dialogCancel}
        onCancel={() => setConfirmingReject(false)}
        onConfirm={async () => {
          setConfirmingReject(false);
          await rejectAction.run();
        }}
      />
    </section>
  );
}
