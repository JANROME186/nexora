/**
 * Calibration log administration screen (COM-MOD-010-FE-001, BCM-QLT-003).
 *
 * Records calibration events for an equipment inventory item, backed by CalibrationController.
 */
import { useState } from "react";
import { listCalibrations, recordCalibration } from "../../api/inventoryQualityApi";
import type { CalibrationEvent } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { StatusBanner } from "../common/StatusBanner";

export function CalibrationsScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;

  const [inventoryItemId, setInventoryItemId] = useState("");
  const [calibrationStandardRef, setCalibrationStandardRef] = useState("");
  const [performedBy, setPerformedBy] = useState("");
  const [result, setResult] = useState("PASS");
  const [nextDueDate, setNextDueDate] = useState("");
  const [certificateReference, setCertificateReference] = useState("");
  const [calibrations, setCalibrations] = useState<CalibrationEvent[]>([]);

  const recordAction = useAsyncAction(async () => {
    const created = await recordCalibration(inventoryItemId, {
      calibrationStandardRef,
      performedBy,
      result,
      nextDueDate: nextDueDate || undefined,
      certificateReference: certificateReference || undefined,
    });
    setCalibrations((current) => [created, ...current]);
    return created;
  });

  const listAction = useAsyncAction(async () => {
    const loaded = await listCalibrations(inventoryItemId);
    setCalibrations(loaded);
    return loaded;
  });

  const columns: DataTableColumn<CalibrationEvent>[] = [
    {
      key: "standard",
      header: labels.calibrations.calibrationStandardRef,
      render: (row) => row.calibrationStandardRef,
    },
    { key: "result", header: labels.calibrations.result, render: (row) => row.result },
    {
      key: "nextDue",
      header: labels.calibrations.nextDueDate,
      render: (row) => row.nextDueDate ?? "-",
    },
  ];

  return (
    <section aria-labelledby="calibrations-heading">
      <h2 id="calibrations-heading">{labels.calibrations.heading}</h2>
      <p>{labels.calibrations.description}</p>

      <div className="panel">
        <label htmlFor="cal-item-id">{labels.shared.inventoryItemId}</label>
        <input
          id="cal-item-id"
          value={inventoryItemId}
          onChange={(e) => setInventoryItemId(e.target.value)}
        />
        <label htmlFor="cal-standard-ref">{labels.calibrations.calibrationStandardRef}</label>
        <input
          id="cal-standard-ref"
          value={calibrationStandardRef}
          onChange={(e) => setCalibrationStandardRef(e.target.value)}
        />
        <label htmlFor="cal-performed-by">{labels.calibrations.performedBy}</label>
        <input
          id="cal-performed-by"
          value={performedBy}
          onChange={(e) => setPerformedBy(e.target.value)}
        />
        <label htmlFor="cal-result">{labels.calibrations.result}</label>
        <input id="cal-result" value={result} onChange={(e) => setResult(e.target.value)} />
        <label htmlFor="cal-next-due">{labels.calibrations.nextDueDate}</label>
        <input
          id="cal-next-due"
          type="date"
          value={nextDueDate}
          onChange={(e) => setNextDueDate(e.target.value)}
        />
        <label htmlFor="cal-certificate">{labels.calibrations.certificateReference}</label>
        <input
          id="cal-certificate"
          value={certificateReference}
          onChange={(e) => setCertificateReference(e.target.value)}
        />
        <div className="catalog-toolbar">
          <button
            type="button"
            disabled={
              !inventoryItemId ||
              !calibrationStandardRef ||
              !performedBy ||
              recordAction.status === "loading"
            }
            onClick={() => recordAction.run()}
          >
            {labels.shared.create}
          </button>
          <button
            type="button"
            disabled={!inventoryItemId || listAction.status === "loading"}
            onClick={() => listAction.run()}
          >
            {labels.calibrations.loadCalibrations}
          </button>
        </div>
        <StatusBanner
          status={recordAction.status}
          errorMessage={recordAction.errorMessage}
          successMessage={labels.calibrations.recordSuccess}
        />
        <StatusBanner
          status={listAction.status}
          errorMessage={listAction.errorMessage}
          successMessage={labels.shared.loaded}
        />
      </div>

      {listAction.status === "success" && calibrations.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.calibrations.heading}
        columns={columns}
        rows={calibrations}
        rowKey={(row) => row.calibrationEventId}
      />
    </section>
  );
}
