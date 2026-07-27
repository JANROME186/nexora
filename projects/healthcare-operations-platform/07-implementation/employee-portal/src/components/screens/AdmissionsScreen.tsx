import { useState, type FormEvent } from "react";
import {
  commitAdmissionRequest,
  markAdmissionReady,
  rejectAdmissionRequest,
  startAdmissionRequest,
  listAdmissionRequests,
} from "../../api/frontDeskApi";
import type { AdmissionRequest, CatalogSelectionInput } from "../../api/types";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncActionState } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { DataTable, type DataTableColumn } from "../common/DataTable";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";
import { statusClass } from "../common/statusPresentation";

const SELECT_ADMISSION_FIRST = "Select an admission request first.";

function newSelection(): CatalogSelectionInput {
  return { testDefinitionId: "", catalogItemKind: "test", quantity: 1 };
}

const columns: DataTableColumn<AdmissionRequest>[] = [
  { key: "admissionId", header: "Admission id", render: (row) => row.admissionId },
  { key: "patientId", header: "Patient", render: (row) => row.patientId },
  { key: "visitId", header: "Visit", render: (row) => row.visitId },
  {
    key: "admissionStatus",
    header: "Status",
    render: (row) => (
      <span className={statusClass(row.admissionStatus)}>{row.admissionStatus}</span>
    ),
  },
];

interface CatalogSelectionEditorProps {
  selections: CatalogSelectionInput[];
  onUpdate: (index: number, patch: Partial<CatalogSelectionInput>) => void;
  onRemove: (index: number) => void;
  onAdd: () => void;
}

/** Decomposed per TD-FE-010's shared remediation pattern. */
function CatalogSelectionEditor({
  selections,
  onUpdate,
  onRemove,
  onAdd,
}: CatalogSelectionEditorProps) {
  return (
    <>
      <h4>Catalog selection</h4>
      {selections.map((selection, index) => (
        <div className="order-line-row" key={index}>
          <label htmlFor={`admission-selection-kind-${index}`}>Kind</label>
          <select
            id={`admission-selection-kind-${index}`}
            value={selection.catalogItemKind}
            onChange={(event) => onUpdate(index, { catalogItemKind: event.target.value })}
          >
            <option value="test">Test</option>
            <option value="panel">Panel</option>
          </select>
          <label htmlFor={`admission-selection-test-id-${index}`}>Catalog item id</label>
          <input
            id={`admission-selection-test-id-${index}`}
            value={selection.testDefinitionId}
            onChange={(event) => onUpdate(index, { testDefinitionId: event.target.value })}
          />
          <label htmlFor={`admission-selection-quantity-${index}`}>Quantity</label>
          <input
            id={`admission-selection-quantity-${index}`}
            type="number"
            min={1}
            value={selection.quantity ?? 1}
            onChange={(event) => onUpdate(index, { quantity: Number(event.target.value) })}
          />
          {selections.length > 1 ? (
            <button type="button" onClick={() => onRemove(index)}>
              Remove
            </button>
          ) : null}
        </div>
      ))}
      <button type="button" onClick={onAdd}>
        Add selection
      </button>
    </>
  );
}

interface StartAdmissionFormProps {
  canUse: boolean;
  visitId: string;
  onVisitIdChange: (value: string) => void;
  patientId: string;
  onPatientIdChange: (value: string) => void;
  doctorId: string;
  onDoctorIdChange: (value: string) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  startAction: AsyncActionState<AdmissionRequest>;
}

/** Decomposed per TD-FE-010's shared remediation pattern. */
function StartAdmissionForm({
  canUse,
  visitId,
  onVisitIdChange,
  patientId,
  onPatientIdChange,
  doctorId,
  onDoctorIdChange,
  onSubmit,
  startAction,
}: StartAdmissionFormProps) {
  return (
    <div className="panel">
      <h3>Start admission request</h3>
      <form onSubmit={onSubmit}>
        <label htmlFor="admission-visit-id">Reception visit id</label>
        <input
          id="admission-visit-id"
          value={visitId}
          onChange={(event) => onVisitIdChange(event.target.value)}
          required
        />
        <label htmlFor="admission-patient-id">Patient id</label>
        <input
          id="admission-patient-id"
          value={patientId}
          onChange={(event) => onPatientIdChange(event.target.value)}
          required
        />
        <label htmlFor="admission-doctor-id">Referring doctor id (optional)</label>
        <input
          id="admission-doctor-id"
          value={doctorId}
          onChange={(event) => onDoctorIdChange(event.target.value)}
        />
        <button type="submit" disabled={!canUse || startAction.status === "loading"}>
          Start admission
        </button>
        <StatusBanner
          status={startAction.status}
          errorMessage={startAction.errorMessage}
          successMessage="Admission request started (draft)."
        />
      </form>
    </div>
  );
}

function AdmissionSummaryTable({ admission }: { admission: AdmissionRequest }) {
  return (
    <table>
      <tbody>
        <tr>
          <th scope="row">Patient</th>
          <td>{admission.patientId}</td>
        </tr>
        <tr>
          <th scope="row">Visit</th>
          <td>{admission.visitId}</td>
        </tr>
        <tr>
          <th scope="row">Status</th>
          <td>
            <span className={statusClass(admission.admissionStatus)}>
              {admission.admissionStatus}
            </span>
          </td>
        </tr>
        <tr>
          <th scope="row">Created order</th>
          <td>{admission.createdOrderId ?? "None"}</td>
        </tr>
      </tbody>
    </table>
  );
}

interface AdmissionDetailPanelProps {
  admission: AdmissionRequest;
  clinicalNotesDraft: string;
  onClinicalNotesDraftChange: (value: string) => void;
  selections: CatalogSelectionInput[];
  onUpdateSelection: (index: number, patch: Partial<CatalogSelectionInput>) => void;
  onRemoveSelection: (index: number) => void;
  onAddSelection: () => void;
  onMarkReady: (event: FormEvent<HTMLFormElement>) => void;
  markReadyAction: AsyncActionState<AdmissionRequest>;
  consentConfirmed: boolean;
  onConsentConfirmedChange: (value: boolean) => void;
  sampleRequirementsAcknowledged: boolean;
  onSampleRequirementsAcknowledgedChange: (value: boolean) => void;
  onCommit: (event: FormEvent<HTMLFormElement>) => void;
  commitAction: AsyncActionState<AdmissionRequest>;
  onRequestReject: () => void;
  rejectAction: AsyncActionState<AdmissionRequest>;
}

function AdmissionDetailPanel({
  admission,
  clinicalNotesDraft,
  onClinicalNotesDraftChange,
  selections,
  onUpdateSelection,
  onRemoveSelection,
  onAddSelection,
  onMarkReady,
  markReadyAction,
  consentConfirmed,
  onConsentConfirmedChange,
  sampleRequirementsAcknowledged,
  onSampleRequirementsAcknowledgedChange,
  onCommit,
  commitAction,
  onRequestReject,
  rejectAction,
}: AdmissionDetailPanelProps) {
  const isOpen =
    admission.admissionStatus === "draft" || admission.admissionStatus === "ready_for_order";

  return (
    <div className="panel">
      <h3>Admission detail: {admission.admissionId}</h3>
      <AdmissionSummaryTable admission={admission} />

      {admission.admissionStatus === "draft" ? (
        <form onSubmit={onMarkReady}>
          <h4>Mark ready for order</h4>
          <label htmlFor="admission-clinical-notes">Clinical notes draft (optional)</label>
          <input
            id="admission-clinical-notes"
            value={clinicalNotesDraft}
            onChange={(event) => onClinicalNotesDraftChange(event.target.value)}
          />
          <CatalogSelectionEditor
            selections={selections}
            onUpdate={onUpdateSelection}
            onRemove={onRemoveSelection}
            onAdd={onAddSelection}
          />
          <button type="submit" disabled={markReadyAction.status === "loading"}>
            Mark ready
          </button>
        </form>
      ) : null}
      <StatusBanner
        status={markReadyAction.status}
        errorMessage={markReadyAction.errorMessage}
        successMessage="Admission marked ready for order."
      />

      {admission.admissionStatus === "ready_for_order" ? (
        <form onSubmit={onCommit}>
          <h4>Commit to diagnostic order</h4>
          <label htmlFor="admission-consent-confirmed">
            <input
              id="admission-consent-confirmed"
              type="checkbox"
              checked={consentConfirmed}
              onChange={(event) => onConsentConfirmedChange(event.target.checked)}
            />
            Consent confirmed
          </label>
          <label htmlFor="admission-sample-requirements">
            <input
              id="admission-sample-requirements"
              type="checkbox"
              checked={sampleRequirementsAcknowledged}
              onChange={(event) => onSampleRequirementsAcknowledgedChange(event.target.checked)}
            />
            Sample requirements acknowledged
          </label>
          <button type="submit" disabled={commitAction.status === "loading"}>
            Commit admission
          </button>
        </form>
      ) : null}
      <StatusBanner
        status={commitAction.status}
        errorMessage={commitAction.errorMessage}
        successMessage="Admission committed to a diagnostic order."
      />

      {isOpen ? (
        <button
          type="button"
          disabled={rejectAction.status === "loading"}
          onClick={onRequestReject}
        >
          Reject admission
        </button>
      ) : null}
      <StatusBanner
        status={rejectAction.status}
        errorMessage={rejectAction.errorMessage}
        successMessage="Admission rejected."
      />
    </div>
  );
}

/** Decomposed per TD-FE-010's shared remediation pattern: bundles this screen's state and async
 * actions into a dedicated hook so the top-level component's render function stays within the
 * configured function-size lint threshold. */
function useAdmissionsScreenState(tenantId?: string, laboratoryId?: string, branchId?: string) {
  const [admissions, setAdmissions] = useState<AdmissionRequest[]>([]);
  const [selected, setSelected] = useState<AdmissionRequest | undefined>(undefined);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before listing admission requests.");
    const loaded = await listAdmissionRequests(tenantId);
    setAdmissions(loaded);
    return loaded;
  });

  const [visitId, setVisitId] = useState("");
  const [patientId, setPatientId] = useState("");
  const [doctorId, setDoctorId] = useState("");
  const startAction = useAsyncAction(async () => {
    if (!tenantId || !laboratoryId || !branchId) {
      throw new Error("Select tenant, laboratory and branch scope before starting an admission.");
    }
    const started = await startAdmissionRequest({
      tenantId,
      laboratoryId,
      branchId,
      visitId,
      patientId,
      doctorId: doctorId || undefined,
    });
    setAdmissions((current) => [started, ...current]);
    setSelected(started);
    setVisitId("");
    setPatientId("");
    setDoctorId("");
    return started;
  });

  const [clinicalNotesDraft, setClinicalNotesDraft] = useState("");
  const [selections, setSelections] = useState<CatalogSelectionInput[]>([newSelection()]);
  const markReadyAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_ADMISSION_FIRST);
    return markAdmissionReady(selected.admissionId, {
      clinicalNotesDraft: clinicalNotesDraft || undefined,
      catalogSelection: selections.filter((selection) => selection.testDefinitionId),
    });
  });

  const [consentConfirmed, setConsentConfirmed] = useState(false);
  const [sampleRequirementsAcknowledged, setSampleRequirementsAcknowledged] = useState(false);
  const commitAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_ADMISSION_FIRST);
    return commitAdmissionRequest(selected.admissionId, {
      consentConfirmed,
      sampleRequirementsAcknowledged,
    });
  });

  const [confirmingReject, setConfirmingReject] = useState(false);
  const rejectAction = useAsyncAction(async () => {
    if (!selected) throw new Error(SELECT_ADMISSION_FIRST);
    return rejectAdmissionRequest(selected.admissionId);
  });

  function applyUpdated(updated: AdmissionRequest) {
    setSelected(updated);
    setAdmissions((current) =>
      current.map((admission) =>
        admission.admissionId === updated.admissionId ? updated : admission,
      ),
    );
  }

  function selectAdmission(admission: AdmissionRequest) {
    setSelected(admission);
    setClinicalNotesDraft(admission.clinicalNotesDraft ?? "");
    setSelections([newSelection()]);
    setConsentConfirmed(false);
    setSampleRequirementsAcknowledged(false);
    markReadyAction.reset();
    commitAction.reset();
    rejectAction.reset();
  }

  async function handleStart(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await startAction.run();
  }

  async function handleMarkReady(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await markReadyAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  async function handleCommit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await commitAction.run();
    if (result.ok) applyUpdated(result.data);
  }

  return {
    admissions,
    selected,
    listAction,
    visitId,
    setVisitId,
    patientId,
    setPatientId,
    doctorId,
    setDoctorId,
    startAction,
    clinicalNotesDraft,
    setClinicalNotesDraft,
    selections,
    setSelections,
    markReadyAction,
    consentConfirmed,
    setConsentConfirmed,
    sampleRequirementsAcknowledged,
    setSampleRequirementsAcknowledged,
    commitAction,
    confirmingReject,
    setConfirmingReject,
    rejectAction,
    applyUpdated,
    selectAdmission,
    handleStart,
    handleMarkReady,
    handleCommit,
  };
}

/**
 * BCM-ATT-004 employee portal surface (TD-FE-006 remediation): admission intake started from a
 * reception visit, catalog selection, commit-to-order and reject, completing the front-desk
 * handoff chain (ReceptionScreen "advance to admission" -> this screen -> DiagnosticOrdersScreen).
 */
export function AdmissionsScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);
  const {
    admissions,
    selected,
    listAction,
    visitId,
    setVisitId,
    patientId,
    setPatientId,
    doctorId,
    setDoctorId,
    startAction,
    clinicalNotesDraft,
    setClinicalNotesDraft,
    selections,
    setSelections,
    markReadyAction,
    consentConfirmed,
    setConsentConfirmed,
    sampleRequirementsAcknowledged,
    setSampleRequirementsAcknowledged,
    commitAction,
    confirmingReject,
    setConfirmingReject,
    rejectAction,
    applyUpdated,
    selectAdmission,
    handleStart,
    handleMarkReady,
    handleCommit,
  } = useAdmissionsScreenState(tenantId, laboratoryId, branchId);

  return (
    <section aria-labelledby="admissions-heading">
      <h2 id="admissions-heading">Admissions</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before starting admission requests.
        </p>
      ) : null}

      <StartAdmissionForm
        canUse={canUse}
        visitId={visitId}
        onVisitIdChange={setVisitId}
        patientId={patientId}
        onPatientIdChange={setPatientId}
        doctorId={doctorId}
        onDoctorIdChange={setDoctorId}
        onSubmit={handleStart}
        startAction={startAction}
      />

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={() => listAction.run()}
      >
        Load admissions
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Admissions loaded."
      />
      {listAction.status === "success" && admissions.length === 0 ? (
        <p className="empty-state">No admission requests exist yet for this tenant.</p>
      ) : null}

      <DataTable
        caption="Admission requests"
        columns={columns}
        rows={admissions}
        rowKey={(row) => row.admissionId}
        onSelectRow={selectAdmission}
      />

      {selected ? (
        <AdmissionDetailPanel
          admission={selected}
          clinicalNotesDraft={clinicalNotesDraft}
          onClinicalNotesDraftChange={setClinicalNotesDraft}
          selections={selections}
          onUpdateSelection={(index, patch) =>
            setSelections((current) =>
              current.map((selection, i) => (i === index ? { ...selection, ...patch } : selection)),
            )
          }
          onRemoveSelection={(index) =>
            setSelections((current) => current.filter((_, i) => i !== index))
          }
          onAddSelection={() => setSelections((current) => [...current, newSelection()])}
          onMarkReady={handleMarkReady}
          markReadyAction={markReadyAction}
          consentConfirmed={consentConfirmed}
          onConsentConfirmedChange={setConsentConfirmed}
          sampleRequirementsAcknowledged={sampleRequirementsAcknowledged}
          onSampleRequirementsAcknowledgedChange={setSampleRequirementsAcknowledged}
          onCommit={handleCommit}
          commitAction={commitAction}
          onRequestReject={() => setConfirmingReject(true)}
          rejectAction={rejectAction}
        />
      ) : (
        <p className="empty-state">Select an admission row to view its detail and take action.</p>
      )}

      <ConfirmDialog
        open={confirmingReject}
        title="Confirm rejection"
        description="This admission request will be marked as rejected and cannot be committed to a diagnostic order. Continue?"
        onCancel={() => setConfirmingReject(false)}
        onConfirm={async () => {
          setConfirmingReject(false);
          const result = await rejectAction.run();
          if (result.ok) applyUpdated(result.data);
        }}
      />
    </section>
  );
}
