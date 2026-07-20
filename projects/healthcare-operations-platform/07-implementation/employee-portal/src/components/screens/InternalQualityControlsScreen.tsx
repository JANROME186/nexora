/**
 * Internal quality control administration screen (COM-MOD-010-FE-001, BCM-QLT-001).
 *
 * Records quality control runs and allows a supervisor to override the automated acceptance
 * decision, backed by InternalQualityControlController. Decomposed into small sub-components
 * (TD-FE-010) so no single function exceeds the ESLint function-size/complexity thresholds.
 */
import { useState } from "react";
import {
  listQualityControlRuns,
  overrideQualityControlDecision,
  recordQualityControlRun,
} from "../../api/inventoryQualityApi";
import type { QualityControlRun } from "../../api/types";
import { useLocale } from "../../i18n/LocaleContext";
import type { MessageCatalog } from "../../i18n/locales/es-MX";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncStatus } from "../../state/useAsyncAction";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { statusClass } from "../common/statusPresentation";

type Labels = MessageCatalog["inventoryQuality"];

interface RecordRunFormProps {
  labels: Labels;
  disabled: boolean;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: {
    testDefinitionId: string;
    controlMaterialStockLotId: string;
    measuredValue: string;
    expectedMin: string;
    expectedMax: string;
    performedBy: string;
  }) => void;
}

function RecordRunForm({ labels, disabled, status, errorMessage, onSubmit }: RecordRunFormProps) {
  const [testDefinitionId, setTestDefinitionId] = useState("");
  const [controlMaterialStockLotId, setControlMaterialStockLotId] = useState("");
  const [measuredValue, setMeasuredValue] = useState("");
  const [expectedMin, setExpectedMin] = useState("");
  const [expectedMax, setExpectedMax] = useState("");
  const [performedBy, setPerformedBy] = useState("");

  return (
    <div className="panel">
      <label htmlFor="qc-test-id">{labels.inventoryStockMovements.testDefinitionId}</label>
      <input
        id="qc-test-id"
        value={testDefinitionId}
        onChange={(e) => setTestDefinitionId(e.target.value)}
      />
      <label htmlFor="qc-control-lot-id">
        {labels.internalQualityControls.controlMaterialStockLotId}
      </label>
      <input
        id="qc-control-lot-id"
        value={controlMaterialStockLotId}
        onChange={(e) => setControlMaterialStockLotId(e.target.value)}
      />
      <label htmlFor="qc-measured-value">{labels.internalQualityControls.measuredValue}</label>
      <input
        id="qc-measured-value"
        value={measuredValue}
        onChange={(e) => setMeasuredValue(e.target.value)}
      />
      <label htmlFor="qc-expected-min">{labels.internalQualityControls.expectedMin}</label>
      <input
        id="qc-expected-min"
        value={expectedMin}
        onChange={(e) => setExpectedMin(e.target.value)}
      />
      <label htmlFor="qc-expected-max">{labels.internalQualityControls.expectedMax}</label>
      <input
        id="qc-expected-max"
        value={expectedMax}
        onChange={(e) => setExpectedMax(e.target.value)}
      />
      <label htmlFor="qc-performed-by">{labels.internalQualityControls.performedBy}</label>
      <input
        id="qc-performed-by"
        value={performedBy}
        onChange={(e) => setPerformedBy(e.target.value)}
      />
      <button
        type="button"
        disabled={
          disabled ||
          !testDefinitionId ||
          !controlMaterialStockLotId ||
          !measuredValue ||
          status === "loading"
        }
        onClick={() =>
          onSubmit({
            testDefinitionId,
            controlMaterialStockLotId,
            measuredValue,
            expectedMin,
            expectedMax,
            performedBy,
          })
        }
      >
        {labels.shared.create}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.internalQualityControls.recordSuccess}
      />
    </div>
  );
}

interface OverridePanelProps {
  labels: Labels;
  selectedRun?: QualityControlRun;
  status: AsyncStatus;
  errorMessage?: string;
  onSubmit: (fields: {
    acceptanceDecision: string;
    overrideReason: string;
    supervisorId: string;
  }) => void;
}

function OverridePanel({
  labels,
  selectedRun,
  status,
  errorMessage,
  onSubmit,
}: OverridePanelProps) {
  const [acceptanceDecision, setAcceptanceDecision] = useState("ACCEPTED");
  const [overrideReason, setOverrideReason] = useState("");
  const [supervisorId, setSupervisorId] = useState("");

  return (
    <div className="panel">
      <h3>{selectedRun?.qcRunId ?? labels.shared.selectFirst}</h3>
      <label htmlFor="qc-override-decision">
        {labels.internalQualityControls.acceptanceDecision}
      </label>
      <input
        id="qc-override-decision"
        value={acceptanceDecision}
        onChange={(e) => setAcceptanceDecision(e.target.value)}
      />
      <label htmlFor="qc-override-reason">{labels.internalQualityControls.overrideReason}</label>
      <input
        id="qc-override-reason"
        value={overrideReason}
        onChange={(e) => setOverrideReason(e.target.value)}
      />
      <label htmlFor="qc-supervisor-id">{labels.internalQualityControls.supervisorId}</label>
      <input
        id="qc-supervisor-id"
        value={supervisorId}
        onChange={(e) => setSupervisorId(e.target.value)}
      />
      <button
        type="button"
        disabled={!selectedRun || !overrideReason || !supervisorId}
        onClick={() => onSubmit({ acceptanceDecision, overrideReason, supervisorId })}
      >
        {labels.internalQualityControls.override}
      </button>
      <StatusBanner
        status={status}
        errorMessage={errorMessage}
        successMessage={labels.internalQualityControls.overrideSuccess}
      />
    </div>
  );
}

function runColumns(labels: Labels): DataTableColumn<QualityControlRun>[] {
  return [
    {
      key: "test",
      header: labels.inventoryStockMovements.testDefinitionId,
      render: (row) => row.testDefinitionId,
    },
    {
      key: "evaluation",
      header: labels.internalQualityControls.ruleEvaluation,
      render: (row) => (
        <span className={statusClass(row.ruleEvaluation)}>{row.ruleEvaluation}</span>
      ),
    },
    {
      key: "decision",
      header: labels.internalQualityControls.acceptanceDecision,
      render: (row) => row.acceptanceDecision,
    },
  ];
}

export function InternalQualityControlsScreen() {
  const { t } = useLocale();
  const labels = t.inventoryQuality;
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;

  const [runs, setRuns] = useState<QualityControlRun[]>([]);
  const [selectedRun, setSelectedRun] = useState<QualityControlRun | undefined>();

  const canUseScope = Boolean(tenantId && laboratoryId && branchId);

  const recordAction = useAsyncAction(
    async (fields: {
      testDefinitionId: string;
      controlMaterialStockLotId: string;
      measuredValue: string;
      expectedMin: string;
      expectedMax: string;
      performedBy: string;
    }) => {
      const created = await recordQualityControlRun({
        tenantId: tenantId ?? "",
        laboratoryId: laboratoryId ?? "",
        branchId: branchId ?? "",
        ...fields,
      });
      setRuns((current) => [created, ...current]);
      return created;
    },
  );

  const listAction = useAsyncAction(async () => {
    const loaded = await listQualityControlRuns(tenantId ?? "", laboratoryId ?? "", branchId ?? "");
    setRuns(loaded);
    return loaded;
  });

  const overrideAction = useAsyncAction(
    async (fields: {
      acceptanceDecision: string;
      overrideReason: string;
      supervisorId: string;
    }) => {
      const updated = await overrideQualityControlDecision(selectedRun?.qcRunId ?? "", {
        ...fields,
        supervisorScoped: false,
      });
      setRuns((current) => current.map((run) => (run.qcRunId === updated.qcRunId ? updated : run)));
      setSelectedRun(updated);
      return updated;
    },
  );

  return (
    <section aria-labelledby="internal-quality-controls-heading">
      <h2 id="internal-quality-controls-heading">{labels.internalQualityControls.heading}</h2>
      <p>{labels.internalQualityControls.description}</p>
      <ScopeIndicator />
      {!canUseScope ? (
        <p className="status-banner status-banner--error">{labels.shared.branchRequired}</p>
      ) : null}

      <RecordRunForm
        labels={labels}
        disabled={!canUseScope}
        status={recordAction.status}
        errorMessage={recordAction.errorMessage}
        onSubmit={(fields) => recordAction.run(fields)}
      />

      <button
        type="button"
        disabled={!canUseScope || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        {labels.internalQualityControls.loadRuns}
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage={labels.shared.loaded}
      />
      {listAction.status === "success" && runs.length === 0 ? (
        <p className="empty-state">{labels.shared.noRecords}</p>
      ) : null}
      <DataTable
        caption={labels.internalQualityControls.heading}
        columns={runColumns(labels)}
        rows={runs}
        rowKey={(row) => row.qcRunId}
        onSelectRow={setSelectedRun}
      />

      <OverridePanel
        labels={labels}
        selectedRun={selectedRun}
        status={overrideAction.status}
        errorMessage={overrideAction.errorMessage}
        onSubmit={(fields) => overrideAction.run(fields)}
      />
    </section>
  );
}
