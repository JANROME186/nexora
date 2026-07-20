/**
 * Maintenance log administration screen (COM-MOD-010-FE-001, BCM-QLT-005).
 *
 * Records and completes equipment maintenance events backed by MaintenanceController. Decomposed
 * into small sub-components (TD-FE-010) so no single function exceeds the ESLint
 * function-size/complexity thresholds.
 */
import { useState } from "react";
import {
  completeMaintenance,
  listMaintenanceEvents,
  recordMaintenance,
} from "../../api/inventoryQualityApi";
import type { MaintenanceEvent } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { StatusBanner } from "../common/StatusBanner";

const DEFAULT_ACTOR_ID = "current_user";

type Labels = MessageCatalog["inventoryQuality"];

interface RecordEventFields {
  maintenanceType: string;
  eventDescription: string;
  performedBy: string;
  downtimeMinutes: string;
}

interface RecordEventFormProps {
  labels: Labels;
  inventoryItemId: string;
  onInventoryItemIdChange: (value: string) => void;
  recordStatus: AsyncStatus;
  recordErrorMessage?: string;
  listStatus: AsyncStatus;
  listErrorMessage?: string;
  onRecord: (fields: RecordEventFields) => void;
  onLoad: () => void;
}

function RecordEventForm({
  labels,
  inventoryItemId,
  onInventoryItemIdChange,
  recordStatus,
  recordErrorMessage,
  listStatus,
  listErrorMessage,
  onRecord,
  onLoad,
}: RecordEventFormProps) {
  const [maintenanceType, setMaintenanceType] = useState("PREVENTIVE");
  const [eventDescription, setEventDescription] = useState("");
  const [performedBy, setPerformedBy] = useState("");
  const [downtimeMinutes, setDowntimeMinutes] = useState("");

  return (
    <div className="panel">
      <label htmlFor="maint-item-id">{labels.shared.inventoryItemId}</label>
      <input
        id="maint-item-id"
        value={inventoryItemId}
        onChange={(e) => onInventoryItemIdChange(e.target.value)}
      />
      <label htmlFor="maint-type">{labels.maintenance.maintenanceType}</label>
      <input
        id="maint-type"
        value={maintenanceType}
        onChange={(e) => setMaintenanceType(e.target.value)}
      />
      <label htmlFor="maint-description">{labels.maintenance.eventDescription}</label>
      <input
        id="maint-description"
        value={eventDescription}
        onChange={(e) => setEventDescription(e.target.value)}
      />
      <label htmlFor="maint-performed-by">{labels.maintenance.performedBy}</label>
      <input
        id="maint-performed-by"
        value={performedBy}
        onChange={(e) => setPerformedBy(e.target.value)}
      />
      <label htmlFor="maint-downtime">{labels.maintenance.downtimeMinutes}</label>
      <input
        id="maint-downtime"
        value={downtimeMinutes}
        onChange={(e) => setDowntimeMinutes(e.target.value)}
      />
      <div className="catalog-toolbar">
        <button
          type="button"
          disabled={!inventoryItemId || !eventDescription || recordStatus === "loading"}
          onClick={() =>
            onRecord({ maintenanceType, eventDescription, performedBy, downtimeMinutes })
          }
        >
          {labels.shared.create}
        </button>
        <button
          type="button"
          disabled={!inventoryItemId || listStatus === "loading"}
          onClick={onLoad}
        >
          {labels.maintenance.loadEvents}
        </button>
      </div>
      <StatusBanner
        status={recordStatus}
        errorMessage={recordErrorMessage}
        successMessage={labels.maintenance.recordSuccess}
      />
      <StatusBanner
        status={listStatus}
        errorMessage={listErrorMessage}
        successMessage={labels.shared.loaded}
      />
    </div>
  );
}

function eventColumns(labels: Labels): DataTableColumn<MaintenanceEvent>[] {
  return [
    {
      key: "type",
      header: labels.maintenance.maintenanceType,
      render: (row) => row.maintenanceType,
    },
    {
      key: "description",
      header: labels.maintenance.eventDescription,
      render: (row) => row.description,
    },
    {
      key: "downtime",
      header: labels.maintenance.downtimeMinutes,
      render: (row) => row.downtimeMinutes?.toString() ?? "-",
    },
  ];
}

export function MaintenanceScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;

  const [inventoryItemId, setInventoryItemId] = useState("");
  const [events, setEvents] = useState<MaintenanceEvent[]>([]);
  const [maintenanceEventId, setMaintenanceEventId] = useState("");

  const recordAction = useAsyncAction(async (fields: RecordEventFields) => {
    const created = await recordMaintenance(inventoryItemId, {
      maintenanceType: fields.maintenanceType,
      description: fields.eventDescription,
      performedBy: fields.performedBy || undefined,
      downtimeMinutes: fields.downtimeMinutes ? Number(fields.downtimeMinutes) : undefined,
    });
    setEvents((current) => [created, ...current]);
    return created;
  });

  const listAction = useAsyncAction(async () => {
    const loaded = await listMaintenanceEvents(inventoryItemId);
    setEvents(loaded);
    return loaded;
  });

  const completeAction = useAsyncAction(async () => {
    const updated = await completeMaintenance(maintenanceEventId, { actorId: DEFAULT_ACTOR_ID });
    setEvents((current) =>
      current.map((event) =>
        event.maintenanceEventId === updated.maintenanceEventId ? updated : event,
      ),
    );
    return updated;
  });

  return (
    <section aria-labelledby="maintenance-heading">
      <h2 id="maintenance-heading">{labels.maintenance.heading}</h2>
      <p>{labels.maintenance.description}</p>

      <RecordEventForm
        labels={labels}
        inventoryItemId={inventoryItemId}
        onInventoryItemIdChange={setInventoryItemId}
        recordStatus={recordAction.status}
        recordErrorMessage={recordAction.errorMessage}
        listStatus={listAction.status}
        listErrorMessage={listAction.errorMessage}
        onRecord={(fields) => recordAction.run(fields)}
        onLoad={() => listAction.run()}
      />

      {listAction.status === "success" && events.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.maintenance.heading}
        columns={eventColumns(labels)}
        rows={events}
        rowKey={(row) => row.maintenanceEventId}
      />

      <div className="panel">
        <label htmlFor="maint-event-id">{labels.maintenance.maintenanceEventId}</label>
        <input
          id="maint-event-id"
          value={maintenanceEventId}
          onChange={(e) => setMaintenanceEventId(e.target.value)}
        />
        <button
          type="button"
          disabled={!maintenanceEventId || completeAction.status === "loading"}
          onClick={() => completeAction.run()}
        >
          {labels.maintenance.complete}
        </button>
        <StatusBanner
          status={completeAction.status}
          errorMessage={completeAction.errorMessage}
          successMessage={labels.maintenance.completeSuccess}
        />
      </div>
    </section>
  );
}
