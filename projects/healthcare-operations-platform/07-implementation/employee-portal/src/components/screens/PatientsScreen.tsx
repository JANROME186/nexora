import { useState, type FormEvent } from "react";
import {
  attachPatientDocument,
  attachPatientRepresentative,
  deactivatePatient,
  getPatientSnapshot,
  listPatientConsents,
  listPatientDocuments,
  listPatientRepresentatives,
  listPatients,
  mergePatient,
  recordPatientConsent,
  registerPatient,
  removePatientDocument,
  revokePatientConsent,
  revokePatientRepresentative,
  updatePatient,
  updatePatientRepresentative,
} from "../../api/peopleApi";
import type {
  Patient,
  PatientConsent,
  PatientDocument,
  PatientRepresentative,
  PatientSnapshot,
} from "../../api/types";
import { MESSAGES } from "../../i18n/messages";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncActionState } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

const CONSENT_TYPES = [
  "data_processing",
  "portal_access",
  "notification_channel",
  "marketing",
  "research",
];

const DOCUMENT_CATEGORIES = [
  "identification",
  "insurance",
  "authorization",
  "medical_report",
  "other",
];

interface RepresentativesTableProps {
  representatives: PatientRepresentative[];
  onBeginEdit: (representative: PatientRepresentative) => void;
  onRequestRevoke: (representativeId: string) => void;
}

function RepresentativesTable({
  representatives,
  onBeginEdit,
  onRequestRevoke,
}: RepresentativesTableProps) {
  if (representatives.length === 0) {
    return null;
  }

  return (
    <table>
      <caption>Patient representatives</caption>
      <thead>
        <tr>
          <th scope="col">Id</th>
          <th scope="col">Relationship</th>
          <th scope="col">Name</th>
          <th scope="col">Status</th>
          <th scope="col">Action</th>
        </tr>
      </thead>
      <tbody>
        {representatives.map((representative) => (
          <tr key={representative.representativeId}>
            <td>{representative.representativeId}</td>
            <td>{representative.relationship}</td>
            <td>
              {representative.representativeName?.givenName}{" "}
              {representative.representativeName?.familyName}
            </td>
            <td>
              <span className="catalog-status">{representative.status}</span>
            </td>
            <td>
              <button
                type="button"
                disabled={representative.status !== "active"}
                onClick={() => onBeginEdit(representative)}
              >
                Edit
              </button>{" "}
              <button
                type="button"
                disabled={representative.status !== "active"}
                onClick={() => onRequestRevoke(representative.representativeId)}
              >
                Revoke
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

interface RepresentativesPanelProps {
  representatives: PatientRepresentative[];
  representativesAction: AsyncActionState<PatientRepresentative[]>;
  onLoad: () => void;
  repRelationship: string;
  onRepRelationshipChange: (value: string) => void;
  repGivenName: string;
  onRepGivenNameChange: (value: string) => void;
  repFamilyName: string;
  onRepFamilyNameChange: (value: string) => void;
  repDocumentType: string;
  onRepDocumentTypeChange: (value: string) => void;
  repDocumentNumber: string;
  onRepDocumentNumberChange: (value: string) => void;
  editingRepresentativeId: string | undefined;
  onCancelEdit: () => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  attachRepresentativeAction: AsyncActionState<PatientRepresentative>;
  onBeginEdit: (representative: PatientRepresentative) => void;
  onRequestRevoke: (representativeId: string) => void;
  revokeRepresentativeAction: AsyncActionState<PatientRepresentative>;
}

/** TD-FE-002 remediation: representative attach/update/revoke, extracted so the top-level screen
 * component stays within the configured function-size/complexity lint thresholds. */
function RepresentativesPanel({
  representatives,
  representativesAction,
  onLoad,
  repRelationship,
  onRepRelationshipChange,
  repGivenName,
  onRepGivenNameChange,
  repFamilyName,
  onRepFamilyNameChange,
  repDocumentType,
  onRepDocumentTypeChange,
  repDocumentNumber,
  onRepDocumentNumberChange,
  editingRepresentativeId,
  onCancelEdit,
  onSubmit,
  attachRepresentativeAction,
  onBeginEdit,
  onRequestRevoke,
  revokeRepresentativeAction,
}: RepresentativesPanelProps) {
  return (
    <div className="panel">
      <h3>Representatives</h3>
      <button type="button" disabled={representativesAction.status === "loading"} onClick={onLoad}>
        Load representatives
      </button>
      <StatusBanner
        status={representativesAction.status}
        errorMessage={representativesAction.errorMessage}
        successMessage="Representatives loaded."
      />

      <form onSubmit={onSubmit}>
        <label htmlFor="rep-relationship">Relationship</label>
        <select
          id="rep-relationship"
          value={repRelationship}
          onChange={(event) => onRepRelationshipChange(event.target.value)}
        >
          <option value="parent">Parent</option>
          <option value="legal_guardian">Legal guardian</option>
          <option value="spouse">Spouse</option>
          <option value="power_of_attorney">Power of attorney</option>
          <option value="other">Other</option>
        </select>
        <label htmlFor="rep-given-name">Given name</label>
        <input
          id="rep-given-name"
          value={repGivenName}
          onChange={(event) => onRepGivenNameChange(event.target.value)}
          required
        />
        <label htmlFor="rep-family-name">Family name</label>
        <input
          id="rep-family-name"
          value={repFamilyName}
          onChange={(event) => onRepFamilyNameChange(event.target.value)}
          required
        />
        <label htmlFor="rep-document-type">Document type</label>
        <select
          id="rep-document-type"
          value={repDocumentType}
          onChange={(event) => onRepDocumentTypeChange(event.target.value)}
        >
          <option value="national_id">National id</option>
          <option value="passport">Passport</option>
          <option value="other">Other</option>
        </select>
        <label htmlFor="rep-document-number">Document number</label>
        <input
          id="rep-document-number"
          value={repDocumentNumber}
          onChange={(event) => onRepDocumentNumberChange(event.target.value)}
          required
        />
        <button type="submit" disabled={attachRepresentativeAction.status === "loading"}>
          {editingRepresentativeId ? "Save representative" : "Attach representative"}
        </button>
        {editingRepresentativeId ? (
          <button type="button" onClick={onCancelEdit}>
            Cancel edit
          </button>
        ) : null}
        <StatusBanner
          status={attachRepresentativeAction.status}
          errorMessage={attachRepresentativeAction.errorMessage}
          successMessage={
            editingRepresentativeId ? "Representative updated." : "Representative attached."
          }
        />
      </form>

      {representativesAction.status === "success" && representatives.length === 0 ? (
        <p className="empty-state">No representatives attached to this patient.</p>
      ) : null}

      <RepresentativesTable
        representatives={representatives}
        onBeginEdit={onBeginEdit}
        onRequestRevoke={onRequestRevoke}
      />
      <StatusBanner
        status={revokeRepresentativeAction.status}
        errorMessage={revokeRepresentativeAction.errorMessage}
        successMessage="Representative revoked."
      />
    </div>
  );
}

interface PatientEditPanelProps {
  editGivenName: string;
  onEditGivenNameChange: (value: string) => void;
  editFamilyName: string;
  onEditFamilyNameChange: (value: string) => void;
  editBirthDate: string;
  onEditBirthDateChange: (value: string) => void;
  editSexAtBirth: string;
  onEditSexAtBirthChange: (value: string) => void;
  editDocumentType: string;
  onEditDocumentTypeChange: (value: string) => void;
  editDocumentNumber: string;
  onEditDocumentNumberChange: (value: string) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  updateAction: AsyncActionState<Patient>;
  onRequestDeactivate: () => void;
  deactivateAction: AsyncActionState<Patient>;
}

/** TD-FE-002 remediation: patient update and deactivation, extracted so the top-level screen
 * component stays within the configured function-size/complexity lint thresholds. */
function PatientEditPanel({
  editGivenName,
  onEditGivenNameChange,
  editFamilyName,
  onEditFamilyNameChange,
  editBirthDate,
  onEditBirthDateChange,
  editSexAtBirth,
  onEditSexAtBirthChange,
  editDocumentType,
  onEditDocumentTypeChange,
  editDocumentNumber,
  onEditDocumentNumberChange,
  onSubmit,
  updateAction,
  onRequestDeactivate,
  deactivateAction,
}: PatientEditPanelProps) {
  return (
    <div className="panel">
      <h3>Edit patient</h3>
      <form onSubmit={onSubmit}>
        <label htmlFor="edit-patient-given-name">Given name</label>
        <input
          id="edit-patient-given-name"
          value={editGivenName}
          onChange={(event) => onEditGivenNameChange(event.target.value)}
          required
        />
        <label htmlFor="edit-patient-family-name">Family name</label>
        <input
          id="edit-patient-family-name"
          value={editFamilyName}
          onChange={(event) => onEditFamilyNameChange(event.target.value)}
          required
        />
        <label htmlFor="edit-patient-birth-date">Birth date</label>
        <input
          id="edit-patient-birth-date"
          type="date"
          value={editBirthDate}
          onChange={(event) => onEditBirthDateChange(event.target.value)}
        />
        <label htmlFor="edit-patient-sex">Sex at birth</label>
        <select
          id="edit-patient-sex"
          value={editSexAtBirth}
          onChange={(event) => onEditSexAtBirthChange(event.target.value)}
        >
          <option value="female">Female</option>
          <option value="male">Male</option>
          <option value="intersex">Intersex</option>
          <option value="unknown">Unknown</option>
        </select>
        <label htmlFor="edit-patient-document-type">Primary document type</label>
        <select
          id="edit-patient-document-type"
          value={editDocumentType}
          onChange={(event) => onEditDocumentTypeChange(event.target.value)}
        >
          <option value="national_id">National id</option>
          <option value="passport">Passport</option>
          <option value="drivers_license">Driver&apos;s license</option>
          <option value="tax_id">Tax id</option>
          <option value="other">Other</option>
        </select>
        <label htmlFor="edit-patient-document-number">Primary document number</label>
        <input
          id="edit-patient-document-number"
          value={editDocumentNumber}
          onChange={(event) => onEditDocumentNumberChange(event.target.value)}
          required
        />
        <button type="submit" disabled={updateAction.status === "loading"}>
          Save patient
        </button>
        <StatusBanner
          status={updateAction.status}
          errorMessage={updateAction.errorMessage}
          successMessage="Patient updated."
        />
      </form>

      <h4>Deactivate patient</h4>
      <button type="button" onClick={onRequestDeactivate}>
        Deactivate patient
      </button>
      <StatusBanner
        status={deactivateAction.status}
        errorMessage={deactivateAction.errorMessage}
        successMessage="Patient deactivated."
      />
    </div>
  );
}

interface PatientDocumentsPanelProps {
  documents: PatientDocument[];
  documentsAction: AsyncActionState<PatientDocument[]>;
  onLoad: () => void;
  category: string;
  onCategoryChange: (value: string) => void;
  fileReference: string;
  onFileReferenceChange: (value: string) => void;
  onAttach: (event: FormEvent<HTMLFormElement>) => void;
  attachAction: AsyncActionState<PatientDocument>;
  onRequestRemove: (documentId: string) => void;
  removeAction: AsyncActionState<void>;
}

/** TD-FE-002 remediation: patient document management (list/attach/remove), extracted so the
 * top-level screen component stays within the configured function-size lint threshold. */
function PatientDocumentsPanel({
  documents,
  documentsAction,
  onLoad,
  category,
  onCategoryChange,
  fileReference,
  onFileReferenceChange,
  onAttach,
  attachAction,
  onRequestRemove,
  removeAction,
}: PatientDocumentsPanelProps) {
  return (
    <div className="panel">
      <h3>Documents</h3>
      <button type="button" disabled={documentsAction.status === "loading"} onClick={onLoad}>
        Load documents
      </button>
      <StatusBanner
        status={documentsAction.status}
        errorMessage={documentsAction.errorMessage}
        successMessage="Documents loaded."
      />

      <form onSubmit={onAttach}>
        <label htmlFor="document-category">Category</label>
        <select
          id="document-category"
          value={category}
          onChange={(event) => onCategoryChange(event.target.value)}
        >
          {DOCUMENT_CATEGORIES.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
        <label htmlFor="document-file-reference">File reference</label>
        <input
          id="document-file-reference"
          value={fileReference}
          onChange={(event) => onFileReferenceChange(event.target.value)}
          required
        />
        <button type="submit" disabled={attachAction.status === "loading"}>
          Attach document
        </button>
        <StatusBanner
          status={attachAction.status}
          errorMessage={attachAction.errorMessage}
          successMessage="Document attached."
        />
      </form>

      {documentsAction.status === "success" && documents.length === 0 ? (
        <p className="empty-state">No documents attached to this patient.</p>
      ) : null}

      {documents.length > 0 ? (
        <table>
          <caption>Patient documents</caption>
          <thead>
            <tr>
              <th scope="col">Id</th>
              <th scope="col">Category</th>
              <th scope="col">File reference</th>
              <th scope="col">Action</th>
            </tr>
          </thead>
          <tbody>
            {documents.map((document) => (
              <tr key={document.documentId}>
                <td>{document.documentId}</td>
                <td>{document.category}</td>
                <td>{document.fileReference}</td>
                <td>
                  <button type="button" onClick={() => onRequestRemove(document.documentId)}>
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}
      <StatusBanner
        status={removeAction.status}
        errorMessage={removeAction.errorMessage}
        successMessage="Document removed."
      />
    </div>
  );
}

/**
 * BCM-PER-002 employee portal surface: patient list, registration, snapshot, representative and
 * consent lifecycle (attach/revoke), and patient merge.
 */
export function PatientsScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId } = scope;
  const canUse = Boolean(tenantId && laboratoryId);

  const [patients, setPatients] = useState<Patient[]>([]);
  const [selectedPatientId, setSelectedPatientId] = useState("");
  const [snapshot, setSnapshot] = useState<PatientSnapshot | undefined>(undefined);
  const [representatives, setRepresentatives] = useState<PatientRepresentative[]>([]);
  const [consents, setConsents] = useState<PatientConsent[]>([]);

  const listAction = useAsyncAction(async () => {
    if (!laboratoryId) throw new Error("Select a laboratory before listing patients.");
    const loaded = await listPatients(laboratoryId);
    setPatients(loaded);
    return loaded;
  });

  const [patientCode, setPatientCode] = useState("");
  const [givenName, setGivenName] = useState("");
  const [familyName, setFamilyName] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [sexAtBirth, setSexAtBirth] = useState("female");
  const [documentType, setDocumentType] = useState("national_id");
  const [documentNumber, setDocumentNumber] = useState("");

  const registerAction = useAsyncAction(async () => {
    if (!tenantId || !laboratoryId)
      throw new Error("Select tenant and laboratory scope before registering a patient.");
    const created = await registerPatient({
      tenantId,
      laboratoryId,
      patientCode,
      givenName,
      familyName,
      birthDate: birthDate || undefined,
      sexAtBirth,
      primaryDocumentType: documentType,
      primaryDocumentNumber: documentNumber,
    });
    setPatients((current) => [
      created,
      ...current.filter((patient) => patient.patientId !== created.patientId),
    ]);
    setSelectedPatientId(created.patientId);
    setPatientCode("");
    setGivenName("");
    setFamilyName("");
    setBirthDate("");
    setDocumentNumber("");
    return created;
  });

  const snapshotAction = useAsyncAction(async () => {
    if (!selectedPatientId) throw new Error(MESSAGES.selectPatientFirst);
    const found = await getPatientSnapshot(selectedPatientId);
    setSnapshot(found);
    return found;
  });

  const [editGivenName, setEditGivenName] = useState("");
  const [editFamilyName, setEditFamilyName] = useState("");
  const [editBirthDate, setEditBirthDate] = useState("");
  const [editSexAtBirth, setEditSexAtBirth] = useState("female");
  const [editDocumentType, setEditDocumentType] = useState("national_id");
  const [editDocumentNumber, setEditDocumentNumber] = useState("");
  const updateAction = useAsyncAction(async () => {
    if (!selectedPatientId) throw new Error(MESSAGES.selectPatientFirst);
    return updatePatient(selectedPatientId, {
      givenName: editGivenName,
      familyName: editFamilyName,
      birthDate: editBirthDate || undefined,
      sexAtBirth: editSexAtBirth,
      primaryDocumentType: editDocumentType,
      primaryDocumentNumber: editDocumentNumber,
    });
  });

  const deactivateAction = useAsyncAction(async () => {
    if (!selectedPatientId) throw new Error(MESSAGES.selectPatientFirst);
    return deactivatePatient(selectedPatientId);
  });
  const [confirmingDeactivate, setConfirmingDeactivate] = useState(false);

  const [documents, setDocuments] = useState<PatientDocument[]>([]);
  const documentsAction = useAsyncAction(async () => {
    if (!selectedPatientId) throw new Error(MESSAGES.selectPatientFirst);
    const loaded = await listPatientDocuments(selectedPatientId);
    setDocuments(loaded);
    return loaded;
  });
  const [documentCategory, setDocumentCategory] = useState(DOCUMENT_CATEGORIES[0]);
  const [documentFileReference, setDocumentFileReference] = useState("");
  const attachDocumentAction = useAsyncAction(async () => {
    if (!selectedPatientId) throw new Error(MESSAGES.selectPatientFirst);
    const created = await attachPatientDocument(selectedPatientId, {
      category: documentCategory,
      fileReference: documentFileReference,
    });
    setDocuments((current) => [
      created,
      ...current.filter((document) => document.documentId !== created.documentId),
    ]);
    setDocumentFileReference("");
    return created;
  });
  const removeDocumentAction = useAsyncAction(async (documentId: string) => {
    if (!selectedPatientId) throw new Error(MESSAGES.selectPatientFirst);
    await removePatientDocument(selectedPatientId, documentId);
  });
  const [documentToRemove, setDocumentToRemove] = useState<string | undefined>(undefined);

  const representativesAction = useAsyncAction(async () => {
    if (!selectedPatientId) throw new Error(MESSAGES.selectPatientFirst);
    const loaded = await listPatientRepresentatives(selectedPatientId);
    setRepresentatives(loaded);
    return loaded;
  });

  const [repRelationship, setRepRelationship] = useState("parent");
  const [repGivenName, setRepGivenName] = useState("");
  const [repFamilyName, setRepFamilyName] = useState("");
  const [repDocumentType, setRepDocumentType] = useState("national_id");
  const [repDocumentNumber, setRepDocumentNumber] = useState("");
  const [editingRepresentativeId, setEditingRepresentativeId] = useState<string | undefined>(
    undefined,
  );
  const attachRepresentativeAction = useAsyncAction(async () => {
    if (!selectedPatientId) throw new Error(MESSAGES.selectPatientFirst);
    const request = {
      relationship: repRelationship,
      givenName: repGivenName,
      familyName: repFamilyName,
      documentType: repDocumentType,
      documentNumber: repDocumentNumber,
    };
    const saved = editingRepresentativeId
      ? await updatePatientRepresentative(selectedPatientId, editingRepresentativeId, request)
      : await attachPatientRepresentative(selectedPatientId, request);
    setRepresentatives((current) => [
      saved,
      ...current.filter((rep) => rep.representativeId !== saved.representativeId),
    ]);
    setEditingRepresentativeId(undefined);
    setRepGivenName("");
    setRepFamilyName("");
    setRepDocumentNumber("");
    return saved;
  });

  function beginEditRepresentative(representative: PatientRepresentative) {
    setEditingRepresentativeId(representative.representativeId);
    setRepRelationship(representative.relationship);
    setRepGivenName(representative.representativeName?.givenName ?? "");
    setRepFamilyName(representative.representativeName?.familyName ?? "");
    setRepDocumentNumber("");
  }
  const revokeRepresentativeAction = useAsyncAction((representativeId: string) =>
    revokePatientRepresentative(selectedPatientId, representativeId),
  );
  const [representativeToRevoke, setRepresentativeToRevoke] = useState<string | undefined>(
    undefined,
  );

  const consentsAction = useAsyncAction(async () => {
    if (!selectedPatientId) throw new Error(MESSAGES.selectPatientFirst);
    const loaded = await listPatientConsents(selectedPatientId);
    setConsents(loaded);
    return loaded;
  });

  const [consentType, setConsentType] = useState(CONSENT_TYPES[0]);
  const [consentGrantedBy, setConsentGrantedBy] = useState("patient");
  const recordConsentAction = useAsyncAction(async () => {
    if (!selectedPatientId) throw new Error(MESSAGES.selectPatientFirst);
    const created = await recordPatientConsent(selectedPatientId, {
      consentType,
      granted: true,
      grantedBy: consentGrantedBy,
    });
    setConsents((current) => [
      created,
      ...current.filter((consent) => consent.consentId !== created.consentId),
    ]);
    return created;
  });
  const revokeConsentAction = useAsyncAction((consentId: string) =>
    revokePatientConsent(selectedPatientId, consentId),
  );
  const [consentToRevoke, setConsentToRevoke] = useState<string | undefined>(undefined);

  const [survivingPatientId, setSurvivingPatientId] = useState("");
  const mergeAction = useAsyncAction(async () => {
    if (!selectedPatientId) throw new Error("Select the duplicate patient to merge first.");
    return mergePatient(selectedPatientId, { survivingPatientId });
  });
  const [confirmingMerge, setConfirmingMerge] = useState(false);

  async function handleList() {
    await listAction.run();
  }

  async function handleRegister(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await registerAction.run();
  }

  async function handleAttachRepresentative(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await attachRepresentativeAction.run();
  }

  async function handleRecordConsent(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await recordConsentAction.run();
  }

  function selectPatient(patient: Patient) {
    setSelectedPatientId(patient.patientId);
    setSnapshot(undefined);
    setRepresentatives([]);
    setConsents([]);
    setDocuments([]);
    setEditingRepresentativeId(undefined);
    setEditGivenName(patient.givenName ?? "");
    setEditFamilyName(patient.familyName ?? "");
    setEditBirthDate(patient.birthDate ?? "");
    setEditSexAtBirth(patient.sexAtBirth ?? "female");
    setEditDocumentType(patient.primaryDocumentType ?? "national_id");
    setEditDocumentNumber("");
    updateAction.reset();
    deactivateAction.reset();
  }

  async function handleUpdate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await updateAction.run();
  }

  async function handleAttachDocument(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await attachDocumentAction.run();
  }

  return (
    <section aria-labelledby="patients-heading">
      <h2 id="patients-heading">Patients</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">
          Select a tenant and laboratory before managing patients.
        </p>
      ) : null}

      <div className="panel">
        <h3>Register patient</h3>
        <form onSubmit={handleRegister}>
          <label htmlFor="patient-code">Patient code</label>
          <input
            id="patient-code"
            value={patientCode}
            onChange={(event) => setPatientCode(event.target.value)}
            required
          />
          <label htmlFor="patient-given-name">Given name</label>
          <input
            id="patient-given-name"
            value={givenName}
            onChange={(event) => setGivenName(event.target.value)}
            required
          />
          <label htmlFor="patient-family-name">Family name</label>
          <input
            id="patient-family-name"
            value={familyName}
            onChange={(event) => setFamilyName(event.target.value)}
            required
          />
          <label htmlFor="patient-birth-date">Birth date</label>
          <input
            id="patient-birth-date"
            type="date"
            value={birthDate}
            onChange={(event) => setBirthDate(event.target.value)}
          />
          <label htmlFor="patient-sex">Sex at birth</label>
          <select
            id="patient-sex"
            value={sexAtBirth}
            onChange={(event) => setSexAtBirth(event.target.value)}
          >
            <option value="female">Female</option>
            <option value="male">Male</option>
            <option value="intersex">Intersex</option>
            <option value="unknown">Unknown</option>
          </select>
          <label htmlFor="patient-document-type">Primary document type</label>
          <select
            id="patient-document-type"
            value={documentType}
            onChange={(event) => setDocumentType(event.target.value)}
          >
            <option value="national_id">National id</option>
            <option value="passport">Passport</option>
            <option value="drivers_license">Driver&apos;s license</option>
            <option value="tax_id">Tax id</option>
            <option value="other">Other</option>
          </select>
          <label htmlFor="patient-document-number">Primary document number</label>
          <input
            id="patient-document-number"
            value={documentNumber}
            onChange={(event) => setDocumentNumber(event.target.value)}
            required
          />
          <button type="submit" disabled={!canUse || registerAction.status === "loading"}>
            Register patient
          </button>
          <StatusBanner
            status={registerAction.status}
            errorMessage={registerAction.errorMessage}
            successMessage="Patient registered."
          />
        </form>
      </div>

      <button
        type="button"
        disabled={!canUse || listAction.status === "loading"}
        onClick={handleList}
      >
        Load patients
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Patients loaded."
      />

      {listAction.status === "success" && patients.length === 0 ? (
        <p className="empty-state">No patients registered yet in this laboratory.</p>
      ) : null}

      {patients.length > 0 ? (
        <table>
          <caption>Patients in this laboratory</caption>
          <thead>
            <tr>
              <th scope="col">Patient id</th>
              <th scope="col">Code</th>
              <th scope="col">Name</th>
              <th scope="col">Status</th>
            </tr>
          </thead>
          <tbody>
            {patients.map((patient) => (
              <tr key={patient.patientId}>
                <td>
                  <button
                    type="button"
                    className="link-button"
                    onClick={() => selectPatient(patient)}
                  >
                    {patient.patientId}
                  </button>
                </td>
                <td>{patient.patientCode}</td>
                <td>{patient.fullName}</td>
                <td>
                  <span className="catalog-status">{patient.status}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selectedPatientId ? (
        <>
          <div className="panel">
            <h3>Selected patient: {selectedPatientId}</h3>
            <button
              type="button"
              disabled={snapshotAction.status === "loading"}
              onClick={() => snapshotAction.run()}
            >
              Load snapshot
            </button>
            <StatusBanner
              status={snapshotAction.status}
              errorMessage={snapshotAction.errorMessage}
              successMessage="Snapshot loaded."
            />
            {snapshot ? (
              <table>
                <caption>Patient snapshot (cross-context read model)</caption>
                <tbody>
                  <tr>
                    <th scope="row">Code</th>
                    <td>{snapshot.patientCode}</td>
                  </tr>
                  <tr>
                    <th scope="row">Full name</th>
                    <td>{snapshot.fullName}</td>
                  </tr>
                  <tr>
                    <th scope="row">Birth date</th>
                    <td>{snapshot.birthDate ?? "-"}</td>
                  </tr>
                  <tr>
                    <th scope="row">Status</th>
                    <td>{snapshot.status}</td>
                  </tr>
                  <tr>
                    <th scope="row">Document</th>
                    <td>
                      {snapshot.primaryDocumentType ?? "-"}{" "}
                      {snapshot.primaryDocumentNumberMasked ?? ""}
                    </td>
                  </tr>
                </tbody>
              </table>
            ) : null}
          </div>

          <PatientEditPanel
            editGivenName={editGivenName}
            onEditGivenNameChange={setEditGivenName}
            editFamilyName={editFamilyName}
            onEditFamilyNameChange={setEditFamilyName}
            editBirthDate={editBirthDate}
            onEditBirthDateChange={setEditBirthDate}
            editSexAtBirth={editSexAtBirth}
            onEditSexAtBirthChange={setEditSexAtBirth}
            editDocumentType={editDocumentType}
            onEditDocumentTypeChange={setEditDocumentType}
            editDocumentNumber={editDocumentNumber}
            onEditDocumentNumberChange={setEditDocumentNumber}
            onSubmit={handleUpdate}
            updateAction={updateAction}
            onRequestDeactivate={() => setConfirmingDeactivate(true)}
            deactivateAction={deactivateAction}
          />

          <PatientDocumentsPanel
            documents={documents}
            documentsAction={documentsAction}
            onLoad={() => documentsAction.run()}
            category={documentCategory}
            onCategoryChange={setDocumentCategory}
            fileReference={documentFileReference}
            onFileReferenceChange={setDocumentFileReference}
            onAttach={handleAttachDocument}
            attachAction={attachDocumentAction}
            onRequestRemove={(documentId) => setDocumentToRemove(documentId)}
            removeAction={removeDocumentAction}
          />

          <RepresentativesPanel
            representatives={representatives}
            representativesAction={representativesAction}
            onLoad={() => representativesAction.run()}
            repRelationship={repRelationship}
            onRepRelationshipChange={setRepRelationship}
            repGivenName={repGivenName}
            onRepGivenNameChange={setRepGivenName}
            repFamilyName={repFamilyName}
            onRepFamilyNameChange={setRepFamilyName}
            repDocumentType={repDocumentType}
            onRepDocumentTypeChange={setRepDocumentType}
            repDocumentNumber={repDocumentNumber}
            onRepDocumentNumberChange={setRepDocumentNumber}
            editingRepresentativeId={editingRepresentativeId}
            onCancelEdit={() => setEditingRepresentativeId(undefined)}
            onSubmit={handleAttachRepresentative}
            attachRepresentativeAction={attachRepresentativeAction}
            onBeginEdit={beginEditRepresentative}
            onRequestRevoke={(representativeId) => setRepresentativeToRevoke(representativeId)}
            revokeRepresentativeAction={revokeRepresentativeAction}
          />

          <div className="panel">
            <h3>Consents</h3>
            <button
              type="button"
              disabled={consentsAction.status === "loading"}
              onClick={() => consentsAction.run()}
            >
              Load consents
            </button>
            <StatusBanner
              status={consentsAction.status}
              errorMessage={consentsAction.errorMessage}
              successMessage="Consents loaded."
            />

            <form onSubmit={handleRecordConsent}>
              <label htmlFor="consent-type">Consent type</label>
              <select
                id="consent-type"
                value={consentType}
                onChange={(event) => setConsentType(event.target.value)}
              >
                {CONSENT_TYPES.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
              <label htmlFor="consent-granted-by">Granted by</label>
              <select
                id="consent-granted-by"
                value={consentGrantedBy}
                onChange={(event) => setConsentGrantedBy(event.target.value)}
              >
                <option value="patient">Patient</option>
                <option value="representative">Representative</option>
              </select>
              <button type="submit" disabled={recordConsentAction.status === "loading"}>
                Record consent
              </button>
              <StatusBanner
                status={recordConsentAction.status}
                errorMessage={recordConsentAction.errorMessage}
                successMessage="Consent recorded."
              />
            </form>

            {consentsAction.status === "success" && consents.length === 0 ? (
              <p className="empty-state">No consents recorded for this patient.</p>
            ) : null}

            {consents.length > 0 ? (
              <table>
                <caption>Patient consents (append-only history)</caption>
                <thead>
                  <tr>
                    <th scope="col">Id</th>
                    <th scope="col">Type</th>
                    <th scope="col">Granted</th>
                    <th scope="col">Granted by</th>
                    <th scope="col">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {consents.map((consent) => (
                    <tr key={consent.consentId}>
                      <td>{consent.consentId}</td>
                      <td>{consent.consentType}</td>
                      <td>{consent.granted ? "Yes" : "No (revocation record)"}</td>
                      <td>{consent.grantedBy}</td>
                      <td>
                        <button
                          type="button"
                          disabled={!consent.granted}
                          onClick={() => setConsentToRevoke(consent.consentId)}
                        >
                          Revoke
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : null}
            <StatusBanner
              status={revokeConsentAction.status}
              errorMessage={revokeConsentAction.errorMessage}
              successMessage="Consent revoked."
            />
          </div>

          <div className="panel">
            <h3>Merge into a surviving patient</h3>
            <p className="field-hint">
              This merges the selected patient ({selectedPatientId}) as a duplicate into the
              surviving patient below. The merge never deletes data; it is archived and read
              requests are rewired automatically.
            </p>
            <form
              onSubmit={(event) => {
                event.preventDefault();
                setConfirmingMerge(true);
              }}
            >
              <label htmlFor="surviving-patient-id">Surviving patient id</label>
              <input
                id="surviving-patient-id"
                value={survivingPatientId}
                onChange={(event) => setSurvivingPatientId(event.target.value)}
                required
              />
              <button type="submit" disabled={mergeAction.status === "loading"}>
                Merge patient
              </button>
              <StatusBanner
                status={mergeAction.status}
                errorMessage={mergeAction.errorMessage}
                successMessage="Patient merged."
              />
            </form>
          </div>
        </>
      ) : (
        <p className="empty-state">
          Select a patient row to view its snapshot, representatives, consents and merge options.
        </p>
      )}

      <ConfirmDialog
        open={Boolean(representativeToRevoke)}
        title="Revoke representative"
        description="This representative will no longer be authorized to act on behalf of the patient. Continue?"
        onCancel={() => setRepresentativeToRevoke(undefined)}
        onConfirm={async () => {
          if (representativeToRevoke) {
            const revoked = await revokeRepresentativeAction.run(representativeToRevoke);
            if (revoked.ok) {
              setRepresentatives((current) =>
                current.map((rep) =>
                  rep.representativeId === revoked.data.representativeId ? revoked.data : rep,
                ),
              );
            }
          }
          setRepresentativeToRevoke(undefined);
        }}
      />

      <ConfirmDialog
        open={Boolean(consentToRevoke)}
        title="Revoke consent"
        description="A new revocation record will be appended; the original consent evidence is never modified. Continue?"
        onCancel={() => setConsentToRevoke(undefined)}
        onConfirm={async () => {
          if (consentToRevoke) {
            const revoked = await revokeConsentAction.run(consentToRevoke);
            if (revoked.ok) {
              setConsents((current) => [revoked.data, ...current]);
            }
          }
          setConsentToRevoke(undefined);
        }}
      />

      <ConfirmDialog
        open={confirmingMerge}
        title="Confirm patient merge"
        description={`Patient ${selectedPatientId} will be marked as merged into ${survivingPatientId}. This cannot be undone. Continue?`}
        onCancel={() => setConfirmingMerge(false)}
        onConfirm={async () => {
          setConfirmingMerge(false);
          const merged = await mergeAction.run();
          if (merged.ok) {
            setPatients((current) =>
              current.map((patient) =>
                patient.patientId === merged.data.patientId ? merged.data : patient,
              ),
            );
          }
        }}
      />

      <ConfirmDialog
        open={confirmingDeactivate}
        title="Confirm patient deactivation"
        description="This patient will be marked inactive and can no longer receive new orders, appointments or quotations. Continue?"
        onCancel={() => setConfirmingDeactivate(false)}
        onConfirm={async () => {
          setConfirmingDeactivate(false);
          const result = await deactivateAction.run();
          if (result.ok) {
            setPatients((current) =>
              current.map((patient) =>
                patient.patientId === result.data.patientId ? result.data : patient,
              ),
            );
          }
        }}
      />

      <ConfirmDialog
        open={Boolean(documentToRemove)}
        title="Remove document"
        description="This document reference will be permanently removed from the patient's file. Continue?"
        onCancel={() => setDocumentToRemove(undefined)}
        onConfirm={async () => {
          if (documentToRemove) {
            const removed = await removeDocumentAction.run(documentToRemove);
            if (removed.ok) {
              setDocuments((current) =>
                current.filter((document) => document.documentId !== documentToRemove),
              );
            }
          }
          setDocumentToRemove(undefined);
        }}
      />
    </section>
  );
}
