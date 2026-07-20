/**
 * Equipment registry administration screen (COM-MOD-010-FE-001, BCM-QLT-004).
 *
 * Sets the equipment profile of an inventory item, changes its availability status and displays
 * the availability change history, backed by EquipmentController. Decomposed into small
 * sub-components (TD-FE-010) so no single function exceeds the ESLint function-size/complexity
 * thresholds.
 */
import { useState } from "react";
import {
  changeEquipmentAvailability,
  getEquipmentProfile,
  listEquipmentAvailabilityHistory,
  setEquipmentProfile,
} from "../../api/inventoryQualityApi";
import type { AvailabilityChangeRecord, EquipmentProfileRecord } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { StatusBanner } from "../common/StatusBanner";
import { statusClass } from "../common/statusPresentation";

const DEFAULT_ACTOR_ID = "current_user";

type Labels = MessageCatalog["inventoryQuality"];
type ActionHandle = { status: AsyncStatus; errorMessage?: string };

interface ProfilePanelProps {
  labels: Labels;
  inventoryItemId: string;
  onInventoryItemIdChange: (value: string) => void;
  profile?: EquipmentProfileRecord;
  setProfileAction: ActionHandle & { run: (fields: SetProfileFields) => void };
  loadProfileAction: ActionHandle & { run: () => void };
}

interface SetProfileFields {
  assetTag: string;
  serialNumber: string;
  manufacturer: string;
  model: string;
  location: string;
  availabilityStatus: string;
}

function ProfilePanel({
  labels,
  inventoryItemId,
  onInventoryItemIdChange,
  profile,
  setProfileAction,
  loadProfileAction,
}: ProfilePanelProps) {
  const [assetTag, setAssetTag] = useState("");
  const [serialNumber, setSerialNumber] = useState("");
  const [manufacturer, setManufacturer] = useState("");
  const [model, setModel] = useState("");
  const [location, setLocation] = useState("");
  const [availabilityStatus, setAvailabilityStatus] = useState("AVAILABLE");

  return (
    <div className="panel">
      <label htmlFor="eq-item-id">{labels.shared.inventoryItemId}</label>
      <input
        id="eq-item-id"
        value={inventoryItemId}
        onChange={(e) => onInventoryItemIdChange(e.target.value)}
      />
      <label htmlFor="eq-asset-tag">{labels.equipment.assetTag}</label>
      <input id="eq-asset-tag" value={assetTag} onChange={(e) => setAssetTag(e.target.value)} />
      <label htmlFor="eq-serial">{labels.equipment.serialNumber}</label>
      <input
        id="eq-serial"
        value={serialNumber}
        onChange={(e) => setSerialNumber(e.target.value)}
      />
      <label htmlFor="eq-manufacturer">{labels.equipment.manufacturer}</label>
      <input
        id="eq-manufacturer"
        value={manufacturer}
        onChange={(e) => setManufacturer(e.target.value)}
      />
      <label htmlFor="eq-model">{labels.equipment.model}</label>
      <input id="eq-model" value={model} onChange={(e) => setModel(e.target.value)} />
      <label htmlFor="eq-location">{labels.equipment.location}</label>
      <input id="eq-location" value={location} onChange={(e) => setLocation(e.target.value)} />
      <label htmlFor="eq-availability">{labels.equipment.availabilityStatus}</label>
      <input
        id="eq-availability"
        value={availabilityStatus}
        onChange={(e) => setAvailabilityStatus(e.target.value)}
      />
      <div className="catalog-toolbar">
        <button
          type="button"
          disabled={!inventoryItemId || !assetTag || setProfileAction.status === "loading"}
          onClick={() =>
            setProfileAction.run({
              assetTag,
              serialNumber,
              manufacturer,
              model,
              location,
              availabilityStatus,
            })
          }
        >
          {labels.shared.create}
        </button>
        <button
          type="button"
          disabled={!inventoryItemId || loadProfileAction.status === "loading"}
          onClick={() => loadProfileAction.run()}
        >
          {labels.equipment.loadProfile}
        </button>
      </div>
      <StatusBanner
        status={setProfileAction.status}
        errorMessage={setProfileAction.errorMessage}
        successMessage={labels.equipment.setProfileSuccess}
      />
      <StatusBanner
        status={loadProfileAction.status}
        errorMessage={loadProfileAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {profile ? (
        <table>
          <tbody>
            <tr>
              <th scope="row">{labels.equipment.assetTag}</th>
              <td>{profile.assetTag}</td>
            </tr>
            <tr>
              <th scope="row">{labels.equipment.availabilityStatus}</th>
              <td>
                <span className={statusClass(profile.availabilityStatus)}>
                  {profile.availabilityStatus}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      ) : null}
    </div>
  );
}

interface AvailabilityPanelProps {
  labels: Labels;
  history: AvailabilityChangeRecord[];
  changeAction: ActionHandle & { run: (newStatus: string, reasonCode: string) => void };
  loadHistoryAction: ActionHandle & { run: () => void };
  loadDisabled: boolean;
}

function availabilityColumns(labels: Labels): DataTableColumn<AvailabilityChangeRecord>[] {
  return [
    {
      key: "previous",
      header: labels.equipment.availabilityStatus,
      render: (row) => (
        <span className={statusClass(row.previousStatus)}>{row.previousStatus}</span>
      ),
    },
    {
      key: "new",
      header: labels.equipment.newStatus,
      render: (row) => <span className={statusClass(row.newStatus)}>{row.newStatus}</span>,
    },
    { key: "reason", header: labels.shared.reasonCode, render: (row) => row.reasonCode },
  ];
}

function AvailabilityPanel({
  labels,
  history,
  changeAction,
  loadHistoryAction,
  loadDisabled,
}: AvailabilityPanelProps) {
  const [newStatus, setNewStatus] = useState("IN_USE");
  const [reasonCode, setReasonCode] = useState("");

  return (
    <div className="panel">
      <h3>{labels.equipment.changeAvailability}</h3>
      <label htmlFor="eq-new-status">{labels.equipment.newStatus}</label>
      <input id="eq-new-status" value={newStatus} onChange={(e) => setNewStatus(e.target.value)} />
      <label htmlFor="eq-reason-code">{labels.shared.reasonCode}</label>
      <input
        id="eq-reason-code"
        value={reasonCode}
        onChange={(e) => setReasonCode(e.target.value)}
      />
      <div className="catalog-toolbar">
        <button
          type="button"
          disabled={loadDisabled || !newStatus || !reasonCode || changeAction.status === "loading"}
          onClick={() => changeAction.run(newStatus, reasonCode)}
        >
          {labels.equipment.changeAvailability}
        </button>
        <button
          type="button"
          disabled={loadDisabled || loadHistoryAction.status === "loading"}
          onClick={() => loadHistoryAction.run()}
        >
          {labels.equipment.loadHistory}
        </button>
      </div>
      <StatusBanner
        status={changeAction.status}
        errorMessage={changeAction.errorMessage}
        successMessage={labels.equipment.changeAvailabilitySuccess}
      />
      <StatusBanner
        status={loadHistoryAction.status}
        errorMessage={loadHistoryAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {loadHistoryAction.status === "success" && history.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.equipment.loadHistory}
        columns={availabilityColumns(labels)}
        rows={history}
        rowKey={(row) => row.changeId}
      />
    </div>
  );
}

export function EquipmentScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;

  const [inventoryItemId, setInventoryItemId] = useState("");
  const [profile, setProfile] = useState<EquipmentProfileRecord | undefined>();
  const [history, setHistory] = useState<AvailabilityChangeRecord[]>([]);

  const setProfileAction = useAsyncAction(async (fields: SetProfileFields) => {
    const created = await setEquipmentProfile(inventoryItemId, {
      assetTag: fields.assetTag,
      serialNumber: fields.serialNumber || undefined,
      manufacturer: fields.manufacturer || undefined,
      model: fields.model || undefined,
      location: fields.location || undefined,
      availabilityStatus: fields.availabilityStatus,
      actorId: DEFAULT_ACTOR_ID,
    });
    setProfile(created);
    return created;
  });

  const loadProfileAction = useAsyncAction(async () => {
    const loaded = await getEquipmentProfile(inventoryItemId);
    setProfile(loaded);
    return loaded;
  });

  const changeAvailabilityAction = useAsyncAction(async (newStatus: string, reasonCode: string) => {
    const updated = await changeEquipmentAvailability(inventoryItemId, {
      newStatus,
      reasonCode,
      actorId: DEFAULT_ACTOR_ID,
    });
    setProfile(updated);
    return updated;
  });

  const loadHistoryAction = useAsyncAction(async () => {
    const loaded = await listEquipmentAvailabilityHistory(inventoryItemId);
    setHistory(loaded);
    return loaded;
  });

  return (
    <section aria-labelledby="equipment-heading">
      <h2 id="equipment-heading">{labels.equipment.heading}</h2>
      <p>{labels.equipment.description}</p>

      <ProfilePanel
        labels={labels}
        inventoryItemId={inventoryItemId}
        onInventoryItemIdChange={setInventoryItemId}
        profile={profile}
        setProfileAction={setProfileAction}
        loadProfileAction={loadProfileAction}
      />

      <AvailabilityPanel
        labels={labels}
        history={history}
        changeAction={changeAvailabilityAction}
        loadHistoryAction={loadHistoryAction}
        loadDisabled={!inventoryItemId}
      />
    </section>
  );
}
