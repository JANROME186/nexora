/**
 * Quality Event Intake administration screen (COM-MOD-013-FE-001).
 *
 * Records clinical/operational quality events and links them to quality investigations
 * (CAPA or Audit Management). Confirm dialog before linking (sensitive action).
 * Backed by QualityEventIntakeController.
 */
import { useState } from "react";
import {
  linkQualityEvent,
  listQualityEvents,
  recordQualityEvent,
} from "../../api/externalQualityComplianceApi";
import type { QualityEvent } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { ConfirmDialog } from "../common/ConfirmDialog";

type Labels = MessageCatalog["advancedQualityCompliance"]["qualityEventIntake"];
type SharedLabels = MessageCatalog["advancedQualityCompliance"]["shared"];

interface RecordEventFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: {
    eventType: string;
    description: string;
    reportedBy: string;
    reportedAt: string;
  }) => void;
}

function RecordEventForm({
  labels,
  disabled,
  status,
  errorMessage,
  onSubmit,
}: RecordEventFormProps) {
  const [eventType, setEventType] = useState("");
  const [description, setDescription] = useState("");
  const [reportedBy, setReportedBy] = useState("");
  const [reportedAt, setReportedAt] = useState("");

  return (
    <div className="panel">
      <label htmlFor="qei-event-type">{labels.eventType}</label>
      <input id="qei-event-type" value={eventType} onChange={(e) => setEventType(e.target.value)} />
      <label htmlFor="qei-description">{labels.description2}</label>
      <input
        id="qei-description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <label htmlFor="qei-reported-by">{labels.reportedBy}</label>
      <input
        id="qei-reported-by"
        value={reportedBy}
        onChange={(e) => setReportedBy(e.target.value)}
      />
      <label htmlFor="qei-reported-at">{labels.reportedAt}</label>
      <input
        id="qei-reported-at"
        type="date"
        value={reportedAt}
        onChange={(e) => setReportedAt(e.target.value)}
      />
      <button
        type="button"
        id="qei-record-btn"
        disabled={disabled}
        onClick={() => onSubmit({ eventType, description, reportedBy, reportedAt })}
      >
        {labels.record}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.recordSuccess}
      />
    </div>
  );
}

interface LinkFormProps {
  labels: Labels;
  disabled: boolean;
  onRequest: (fields: { linkedInvestigationId: string; linkedInvestigationType: string }) => void;
}

function LinkEventForm({ labels, disabled, onRequest }: LinkFormProps) {
  const [linkedInvestigationId, setLinkedInvestigationId] = useState("");
  const [linkedInvestigationType, setLinkedInvestigationType] = useState("");

  return (
    <div className="panel">
      <label htmlFor="qei-linked-id">{labels.linkedInvestigationId}</label>
      <input
        id="qei-linked-id"
        value={linkedInvestigationId}
        onChange={(e) => setLinkedInvestigationId(e.target.value)}
      />
      <label htmlFor="qei-linked-type">{labels.linkedInvestigationType}</label>
      <input
        id="qei-linked-type"
        value={linkedInvestigationType}
        onChange={(e) => setLinkedInvestigationType(e.target.value)}
      />
      <button
        type="button"
        id="qei-link-btn"
        disabled={disabled}
        onClick={() => onRequest({ linkedInvestigationId, linkedInvestigationType })}
      >
        {labels.link}
      </button>
    </div>
  );
}

function eventColumns(labels: Labels): DataTableColumn<QualityEvent>[] {
  return [
    { key: "eventType", header: labels.eventType, render: (r) => r.eventType },
    { key: "description", header: labels.description2, render: (r) => r.description },
    { key: "reportedBy", header: labels.reportedBy, render: (r) => r.reportedBy },
    { key: "reportedAt", header: labels.reportedAt, render: (r) => r.reportedAt ?? "-" },
    { key: "status", header: "Estado", render: (r) => r.status },
  ];
}

export function QualityEventIntakeScreen() {
  const { t } = useLocale();
  const shared: SharedLabels = t.advancedQualityCompliance.shared;
  const labels = t.advancedQualityCompliance.qualityEventIntake;
  const { scope } = useAdminScope();

  const [events, setEvents] = useState<QualityEvent[]>([]);
  const [selected, setSelected] = useState<QualityEvent | undefined>();
  const [confirmLink, setConfirmLink] = useState(false);
  const [pendingLink, setPendingLink] = useState<{
    linkedInvestigationId: string;
    linkedInvestigationType: string;
  } | null>(null);

  const loadAction = useAsyncAction(listQualityEvents);
  const recordAction = useAsyncAction(recordQualityEvent);
  const linkAction = useAsyncAction(linkQualityEvent);

  const tenantId = scope.tenantId ?? "";
  const laboratoryId = scope.laboratoryId ?? "";

  async function handleLoad() {
    const result = await loadAction.run(tenantId, laboratoryId);
    if (result.ok) setEvents(result.data);
  }

  async function handleRecord(fields: {
    eventType: string;
    description: string;
    reportedBy: string;
    reportedAt: string;
  }) {
    const result = await recordAction.run(tenantId, laboratoryId, {
      eventType: fields.eventType,
      description: fields.description,
      reportedBy: fields.reportedBy,
      reportedAt: fields.reportedAt || undefined,
    });
    if (result.ok) setEvents((prev) => [...prev, result.data]);
  }

  function requestLink(fields: { linkedInvestigationId: string; linkedInvestigationType: string }) {
    setPendingLink(fields);
    setConfirmLink(true);
  }

  async function confirmLinkAction() {
    if (!selected || !pendingLink) return;
    setConfirmLink(false);
    const result = await linkAction.run(selected.qualityEventId, {
      linkedInvestigationId: pendingLink.linkedInvestigationId,
      linkedInvestigationType: pendingLink.linkedInvestigationType,
    });
    if (result.ok)
      setEvents((prev) =>
        prev.map((e) => (e.qualityEventId === result.data.qualityEventId ? result.data : e)),
      );
  }

  return (
    <section aria-labelledby="qei-heading">
      <h2 id="qei-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>

      <button
        type="button"
        id="qei-load-btn"
        disabled={loadAction.status === "loading"}
        onClick={handleLoad}
      >
        {labels.loadEvents}
      </button>
      <StatusBanner
        status={loadAction.status}
        errorMessage={loadAction.errorMessage}
        successMessage={shared.loaded}
      />
      {loadAction.status === "success" && events.length === 0 ? (
        <p className="empty-state">{shared.noRecords}</p>
      ) : null}

      <DataTable
        caption={labels.heading}
        columns={eventColumns(labels)}
        rows={events}
        rowKey={(r) => r.qualityEventId}
        onSelectRow={setSelected}
      />

      <RecordEventForm
        labels={labels}
        disabled={recordAction.status === "loading" || !tenantId || !laboratoryId}
        status={recordAction.status}
        errorMessage={recordAction.errorMessage}
        onSubmit={handleRecord}
      />

      {selected ? (
        <>
          <LinkEventForm
            labels={labels}
            disabled={linkAction.status === "loading"}
            onRequest={requestLink}
          />
          <StatusBanner
            status={linkAction.status}
            errorMessage={linkAction.errorMessage}
            successMessage={labels.linkSuccess}
          />
        </>
      ) : null}

      <ConfirmDialog
        open={confirmLink}
        title={labels.linkDialog.title}
        description={labels.linkDialog.description}
        confirmLabel={shared.dialogConfirm}
        cancelLabel={shared.dialogCancel}
        onConfirm={confirmLinkAction}
        onCancel={() => setConfirmLink(false)}
      />
    </section>
  );
}
