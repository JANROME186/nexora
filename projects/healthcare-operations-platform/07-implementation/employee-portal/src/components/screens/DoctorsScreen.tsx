import { useState, type FormEvent } from "react";
import {
  assignSpecialty,
  attachDoctorCredential,
  getDoctorSnapshot,
  listDoctorCredentials,
  listDoctors,
  listSpecialtyAssignments,
  preparePortalAccess,
  registerDoctor,
  retireDoctor,
  revokeDoctorCredential,
  suspendDoctor,
  unassignSpecialty,
  updateDoctor,
  verifyDoctorCredential,
} from "../../api/peopleApi";
import type {
  Doctor,
  DoctorSnapshot,
  ProfessionalCredential,
  SpecialtyAssignment,
} from "../../api/types";
import { MESSAGES } from "../../i18n/messages";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction, type AsyncActionState } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

interface DoctorEditPanelProps {
  editGivenName: string;
  onEditGivenNameChange: (value: string) => void;
  editFamilyName: string;
  onEditFamilyNameChange: (value: string) => void;
  editDoctorType: string;
  onEditDoctorTypeChange: (value: string) => void;
  editDocumentType: string;
  onEditDocumentTypeChange: (value: string) => void;
  editDocumentNumber: string;
  onEditDocumentNumberChange: (value: string) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  updateAction: AsyncActionState<Doctor>;
  onRequestRetire: () => void;
  retireAction: AsyncActionState<Doctor>;
}

/** TD-FE-002 remediation: doctor update and retirement, extracted so the top-level screen
 * component stays within the configured function-size/complexity lint thresholds. */
function DoctorEditPanel({
  editGivenName,
  onEditGivenNameChange,
  editFamilyName,
  onEditFamilyNameChange,
  editDoctorType,
  onEditDoctorTypeChange,
  editDocumentType,
  onEditDocumentTypeChange,
  editDocumentNumber,
  onEditDocumentNumberChange,
  onSubmit,
  updateAction,
  onRequestRetire,
  retireAction,
}: DoctorEditPanelProps) {
  return (
    <>
      <h4>Edit doctor</h4>
      <form onSubmit={onSubmit}>
        <label htmlFor="edit-doctor-given-name">Given name</label>
        <input
          id="edit-doctor-given-name"
          value={editGivenName}
          onChange={(event) => onEditGivenNameChange(event.target.value)}
          required
        />
        <label htmlFor="edit-doctor-family-name">Family name</label>
        <input
          id="edit-doctor-family-name"
          value={editFamilyName}
          onChange={(event) => onEditFamilyNameChange(event.target.value)}
          required
        />
        <label htmlFor="edit-doctor-type">Doctor type</label>
        <select
          id="edit-doctor-type"
          value={editDoctorType}
          onChange={(event) => onEditDoctorTypeChange(event.target.value)}
        >
          <option value="referring_external">Referring (external)</option>
          <option value="internal_medical_validator">Internal medical validator</option>
          <option value="both">Both</option>
        </select>
        <label htmlFor="edit-doctor-document-type">Primary document type</label>
        <select
          id="edit-doctor-document-type"
          value={editDocumentType}
          onChange={(event) => onEditDocumentTypeChange(event.target.value)}
        >
          <option value="professional_license">Professional license</option>
          <option value="national_id">National id</option>
          <option value="passport">Passport</option>
          <option value="other">Other</option>
        </select>
        <label htmlFor="edit-doctor-document-number">Primary document number</label>
        <input
          id="edit-doctor-document-number"
          value={editDocumentNumber}
          onChange={(event) => onEditDocumentNumberChange(event.target.value)}
          required
        />
        <button type="submit" disabled={updateAction.status === "loading"}>
          Save doctor
        </button>
        <StatusBanner
          status={updateAction.status}
          errorMessage={updateAction.errorMessage}
          successMessage="Doctor updated."
        />
      </form>

      <h4>Retire doctor</h4>
      <button type="button" onClick={onRequestRetire}>
        Retire doctor
      </button>
      <StatusBanner
        status={retireAction.status}
        errorMessage={retireAction.errorMessage}
        successMessage="Doctor retired."
      />
    </>
  );
}

interface SpecialtiesPanelProps {
  specialties: SpecialtyAssignment[];
  specialtiesAction: AsyncActionState<SpecialtyAssignment[]>;
  onLoad: () => void;
  specialtyCode: string;
  onSpecialtyCodeChange: (value: string) => void;
  primary: boolean;
  onPrimaryChange: (value: boolean) => void;
  onAssign: (event: FormEvent<HTMLFormElement>) => void;
  assignAction: AsyncActionState<SpecialtyAssignment>;
  onRequestUnassign: (assignmentId: string) => void;
  unassignAction: AsyncActionState<void>;
}

/** TD-FE-002 remediation: doctor specialty assignment (list/assign/unassign), extracted so the
 * top-level screen component stays within the configured function-size lint threshold. */
function SpecialtiesPanel({
  specialties,
  specialtiesAction,
  onLoad,
  specialtyCode,
  onSpecialtyCodeChange,
  primary,
  onPrimaryChange,
  onAssign,
  assignAction,
  onRequestUnassign,
  unassignAction,
}: SpecialtiesPanelProps) {
  return (
    <div className="panel">
      <h3>Specialties</h3>
      <button type="button" disabled={specialtiesAction.status === "loading"} onClick={onLoad}>
        Load specialties
      </button>
      <StatusBanner
        status={specialtiesAction.status}
        errorMessage={specialtiesAction.errorMessage}
        successMessage="Specialties loaded."
      />

      <form onSubmit={onAssign}>
        <label htmlFor="specialty-code">Specialty code</label>
        <input
          id="specialty-code"
          value={specialtyCode}
          onChange={(event) => onSpecialtyCodeChange(event.target.value)}
          required
        />
        <label htmlFor="specialty-primary">
          <input
            id="specialty-primary"
            type="checkbox"
            checked={primary}
            onChange={(event) => onPrimaryChange(event.target.checked)}
          />
          Primary specialty
        </label>
        <button type="submit" disabled={assignAction.status === "loading"}>
          Assign specialty
        </button>
        <StatusBanner
          status={assignAction.status}
          errorMessage={assignAction.errorMessage}
          successMessage="Specialty assigned."
        />
      </form>

      {specialtiesAction.status === "success" && specialties.length === 0 ? (
        <p className="empty-state">No specialties assigned to this doctor.</p>
      ) : null}

      {specialties.length > 0 ? (
        <table>
          <caption>Doctor specialties</caption>
          <thead>
            <tr>
              <th scope="col">Id</th>
              <th scope="col">Specialty code</th>
              <th scope="col">Primary</th>
              <th scope="col">Action</th>
            </tr>
          </thead>
          <tbody>
            {specialties.map((specialty) => (
              <tr key={specialty.assignmentId}>
                <td>{specialty.assignmentId}</td>
                <td>{specialty.specialtyCode}</td>
                <td>{specialty.primary ? "Yes" : "No"}</td>
                <td>
                  <button type="button" onClick={() => onRequestUnassign(specialty.assignmentId)}>
                    Unassign
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}
      <StatusBanner
        status={unassignAction.status}
        errorMessage={unassignAction.errorMessage}
        successMessage="Specialty unassigned."
      />
    </div>
  );
}

/**
 * BCM-PER-003 employee portal surface: doctor directory, registration, snapshot, credential
 * lifecycle (attach/verify/revoke), suspension and portal-access preparation.
 */
export function DoctorsScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId } = scope;
  const canUse = Boolean(tenantId && laboratoryId);

  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [selectedDoctorId, setSelectedDoctorId] = useState("");
  const [snapshot, setSnapshot] = useState<DoctorSnapshot | undefined>(undefined);
  const [credentials, setCredentials] = useState<ProfessionalCredential[]>([]);

  const listAction = useAsyncAction(async () => {
    if (!laboratoryId) throw new Error("Select a laboratory before listing doctors.");
    const loaded = await listDoctors(laboratoryId);
    setDoctors(loaded);
    return loaded;
  });

  const [doctorCode, setDoctorCode] = useState("");
  const [givenName, setGivenName] = useState("");
  const [familyName, setFamilyName] = useState("");
  const [doctorType, setDoctorType] = useState("referring_external");
  const [documentType, setDocumentType] = useState("professional_license");
  const [documentNumber, setDocumentNumber] = useState("");

  const registerAction = useAsyncAction(async () => {
    if (!tenantId || !laboratoryId)
      throw new Error("Select tenant and laboratory scope before registering a doctor.");
    const created = await registerDoctor({
      tenantId,
      laboratoryId,
      doctorCode,
      givenName,
      familyName,
      doctorType,
      primaryDocumentType: documentType,
      primaryDocumentNumber: documentNumber,
    });
    setDoctors((current) => [
      created,
      ...current.filter((doctor) => doctor.doctorId !== created.doctorId),
    ]);
    setSelectedDoctorId(created.doctorId);
    setDoctorCode("");
    setGivenName("");
    setFamilyName("");
    setDocumentNumber("");
    return created;
  });

  const snapshotAction = useAsyncAction(async () => {
    if (!selectedDoctorId) throw new Error(MESSAGES.selectDoctorFirst);
    const found = await getDoctorSnapshot(selectedDoctorId);
    setSnapshot(found);
    return found;
  });

  const [editGivenName, setEditGivenName] = useState("");
  const [editFamilyName, setEditFamilyName] = useState("");
  const [editDoctorType, setEditDoctorType] = useState("referring_external");
  const [editDocumentType, setEditDocumentType] = useState("professional_license");
  const [editDocumentNumber, setEditDocumentNumber] = useState("");
  const updateAction = useAsyncAction(async () => {
    if (!selectedDoctorId) throw new Error(MESSAGES.selectDoctorFirst);
    return updateDoctor(selectedDoctorId, {
      givenName: editGivenName,
      familyName: editFamilyName,
      doctorType: editDoctorType,
      primaryDocumentType: editDocumentType,
      primaryDocumentNumber: editDocumentNumber,
    });
  });

  const retireAction = useAsyncAction(async () => {
    if (!selectedDoctorId) throw new Error(MESSAGES.selectDoctorFirst);
    return retireDoctor(selectedDoctorId);
  });
  const [confirmingRetire, setConfirmingRetire] = useState(false);

  const [specialties, setSpecialties] = useState<SpecialtyAssignment[]>([]);
  const specialtiesAction = useAsyncAction(async () => {
    if (!selectedDoctorId) throw new Error(MESSAGES.selectDoctorFirst);
    const loaded = await listSpecialtyAssignments(selectedDoctorId);
    setSpecialties(loaded);
    return loaded;
  });
  const [specialtyCode, setSpecialtyCode] = useState("");
  const [specialtyPrimary, setSpecialtyPrimary] = useState(false);
  const assignSpecialtyAction = useAsyncAction(async () => {
    if (!selectedDoctorId) throw new Error(MESSAGES.selectDoctorFirst);
    const created = await assignSpecialty(selectedDoctorId, {
      specialtyCode,
      primary: specialtyPrimary,
    });
    setSpecialties((current) => [
      created,
      ...current.filter((specialty) => specialty.assignmentId !== created.assignmentId),
    ]);
    setSpecialtyCode("");
    setSpecialtyPrimary(false);
    return created;
  });
  const unassignSpecialtyAction = useAsyncAction(async (assignmentId: string) => {
    if (!selectedDoctorId) throw new Error(MESSAGES.selectDoctorFirst);
    await unassignSpecialty(selectedDoctorId, assignmentId);
  });
  const [specialtyToUnassign, setSpecialtyToUnassign] = useState<string | undefined>(undefined);

  const credentialsAction = useAsyncAction(async () => {
    if (!selectedDoctorId) throw new Error(MESSAGES.selectDoctorFirst);
    const loaded = await listDoctorCredentials(selectedDoctorId);
    setCredentials(loaded);
    return loaded;
  });

  const [credentialType, setCredentialType] = useState("medical_license");
  const [credentialNumber, setCredentialNumber] = useState("");
  const [issuingAuthority, setIssuingAuthority] = useState("");
  const attachCredentialAction = useAsyncAction(async () => {
    if (!selectedDoctorId) throw new Error(MESSAGES.selectDoctorFirst);
    const created = await attachDoctorCredential(selectedDoctorId, {
      credentialType,
      credentialNumber,
      issuingAuthority,
    });
    setCredentials((current) => [
      created,
      ...current.filter((credential) => credential.credentialId !== created.credentialId),
    ]);
    setCredentialNumber("");
    setIssuingAuthority("");
    return created;
  });

  const verifyCredentialAction = useAsyncAction((credentialId: string) =>
    verifyDoctorCredential(selectedDoctorId, credentialId),
  );
  const updateCredential = (updated: ProfessionalCredential) => {
    setCredentials((current) =>
      current.map((item) => (item.credentialId === updated.credentialId ? updated : item)),
    );
  };
  const handleVerifyCredential = async (credentialId: string) => {
    const result = await verifyCredentialAction.run(credentialId);
    if (result.ok) {
      updateCredential(result.data);
    }
  };
  const revokeCredentialAction = useAsyncAction((credentialId: string) =>
    revokeDoctorCredential(selectedDoctorId, credentialId),
  );
  const [credentialToRevoke, setCredentialToRevoke] = useState<string | undefined>(undefined);

  const [suspendReason, setSuspendReason] = useState("");
  const suspendAction = useAsyncAction(async () => {
    if (!selectedDoctorId) throw new Error(MESSAGES.selectDoctorFirst);
    return suspendDoctor(
      selectedDoctorId,
      suspendReason ? { reasonCode: suspendReason } : undefined,
    );
  });
  const [confirmingSuspend, setConfirmingSuspend] = useState(false);

  const [portalEmail, setPortalEmail] = useState("");
  const portalAccessAction = useAsyncAction(async () => {
    if (!selectedDoctorId) throw new Error(MESSAGES.selectDoctorFirst);
    return preparePortalAccess(selectedDoctorId, portalEmail ? { portalEmail } : undefined);
  });

  async function handleList() {
    await listAction.run();
  }

  async function handleRegister(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await registerAction.run();
  }

  async function handleAttachCredential(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await attachCredentialAction.run();
  }

  async function handlePreparePortalAccess(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await portalAccessAction.run();
    if (result.ok) {
      setDoctors((current) =>
        current.map((doctor) => (doctor.doctorId === result.data.doctorId ? result.data : doctor)),
      );
    }
  }

  function selectDoctor(doctor: Doctor) {
    setSelectedDoctorId(doctor.doctorId);
    setSnapshot(undefined);
    setCredentials([]);
    setSpecialties([]);
    setEditGivenName(doctor.givenName ?? "");
    setEditFamilyName(doctor.familyName ?? "");
    setEditDoctorType(doctor.doctorType ?? "referring_external");
    setEditDocumentType(doctor.primaryDocumentType ?? "professional_license");
    setEditDocumentNumber("");
    updateAction.reset();
    retireAction.reset();
  }

  async function handleUpdate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await updateAction.run();
  }

  async function handleAssignSpecialty(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await assignSpecialtyAction.run();
  }

  return (
    <section aria-labelledby="doctors-heading">
      <h2 id="doctors-heading">Doctors</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">
          Select a tenant and laboratory before managing doctors.
        </p>
      ) : null}

      <div className="panel">
        <h3>Register doctor</h3>
        <form onSubmit={handleRegister}>
          <label htmlFor="doctor-code">Doctor code</label>
          <input
            id="doctor-code"
            value={doctorCode}
            onChange={(event) => setDoctorCode(event.target.value)}
            required
          />
          <label htmlFor="doctor-given-name">Given name</label>
          <input
            id="doctor-given-name"
            value={givenName}
            onChange={(event) => setGivenName(event.target.value)}
            required
          />
          <label htmlFor="doctor-family-name">Family name</label>
          <input
            id="doctor-family-name"
            value={familyName}
            onChange={(event) => setFamilyName(event.target.value)}
            required
          />
          <label htmlFor="doctor-type">Doctor type</label>
          <select
            id="doctor-type"
            value={doctorType}
            onChange={(event) => setDoctorType(event.target.value)}
          >
            <option value="referring_external">Referring (external)</option>
            <option value="internal_medical_validator">Internal medical validator</option>
            <option value="both">Both</option>
          </select>
          <label htmlFor="doctor-document-type">Primary document type</label>
          <select
            id="doctor-document-type"
            value={documentType}
            onChange={(event) => setDocumentType(event.target.value)}
          >
            <option value="professional_license">Professional license</option>
            <option value="national_id">National id</option>
            <option value="passport">Passport</option>
            <option value="other">Other</option>
          </select>
          <label htmlFor="doctor-document-number">Primary document number</label>
          <input
            id="doctor-document-number"
            value={documentNumber}
            onChange={(event) => setDocumentNumber(event.target.value)}
            required
          />
          <button type="submit" disabled={!canUse || registerAction.status === "loading"}>
            Register doctor
          </button>
          <StatusBanner
            status={registerAction.status}
            errorMessage={registerAction.errorMessage}
            successMessage="Doctor registered."
          />
        </form>
      </div>

      <button
        type="button"
        disabled={!canUse || listAction.status === "loading"}
        onClick={handleList}
      >
        Load doctors
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Doctors loaded."
      />

      {listAction.status === "success" && doctors.length === 0 ? (
        <p className="empty-state">No doctors registered yet in this laboratory.</p>
      ) : null}

      {doctors.length > 0 ? (
        <table>
          <caption>Doctors in this laboratory</caption>
          <thead>
            <tr>
              <th scope="col">Doctor id</th>
              <th scope="col">Code</th>
              <th scope="col">Name</th>
              <th scope="col">Status</th>
              <th scope="col">Portal status</th>
            </tr>
          </thead>
          <tbody>
            {doctors.map((doctor) => (
              <tr key={doctor.doctorId}>
                <td>
                  <button
                    type="button"
                    className="link-button"
                    onClick={() => selectDoctor(doctor)}
                  >
                    {doctor.doctorId}
                  </button>
                </td>
                <td>{doctor.doctorCode}</td>
                <td>{doctor.fullName}</td>
                <td>
                  <span className="catalog-status">{doctor.status}</span>
                </td>
                <td>
                  <span className="catalog-status">{doctor.portalStatus}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selectedDoctorId ? (
        <>
          <div className="panel">
            <h3>Selected doctor: {selectedDoctorId}</h3>
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
                <caption>Doctor snapshot (cross-context read model)</caption>
                <tbody>
                  <tr>
                    <th scope="row">Code</th>
                    <td>{snapshot.doctorCode}</td>
                  </tr>
                  <tr>
                    <th scope="row">Full name</th>
                    <td>{snapshot.fullName}</td>
                  </tr>
                  <tr>
                    <th scope="row">Type</th>
                    <td>{snapshot.doctorType}</td>
                  </tr>
                  <tr>
                    <th scope="row">Status</th>
                    <td>{snapshot.status}</td>
                  </tr>
                </tbody>
              </table>
            ) : null}

            <DoctorEditPanel
              editGivenName={editGivenName}
              onEditGivenNameChange={setEditGivenName}
              editFamilyName={editFamilyName}
              onEditFamilyNameChange={setEditFamilyName}
              editDoctorType={editDoctorType}
              onEditDoctorTypeChange={setEditDoctorType}
              editDocumentType={editDocumentType}
              onEditDocumentTypeChange={setEditDocumentType}
              editDocumentNumber={editDocumentNumber}
              onEditDocumentNumberChange={setEditDocumentNumber}
              onSubmit={handleUpdate}
              updateAction={updateAction}
              onRequestRetire={() => setConfirmingRetire(true)}
              retireAction={retireAction}
            />

            <h4>Suspend doctor</h4>
            <form
              onSubmit={(event) => {
                event.preventDefault();
                setConfirmingSuspend(true);
              }}
            >
              <label htmlFor="suspend-reason">Reason code (optional)</label>
              <input
                id="suspend-reason"
                value={suspendReason}
                onChange={(event) => setSuspendReason(event.target.value)}
              />
              <button type="submit" disabled={suspendAction.status === "loading"}>
                Suspend doctor
              </button>
              <StatusBanner
                status={suspendAction.status}
                errorMessage={suspendAction.errorMessage}
                successMessage="Doctor suspended."
              />
            </form>

            <h4>Prepare portal access</h4>
            <form onSubmit={handlePreparePortalAccess}>
              <label htmlFor="portal-email">Portal email (optional)</label>
              <input
                id="portal-email"
                type="email"
                value={portalEmail}
                onChange={(event) => setPortalEmail(event.target.value)}
              />
              <button type="submit" disabled={portalAccessAction.status === "loading"}>
                Prepare portal access
              </button>
              <StatusBanner
                status={portalAccessAction.status}
                errorMessage={portalAccessAction.errorMessage}
                successMessage={
                  portalAccessAction.data
                    ? `Portal status: ${portalAccessAction.data.portalStatus}.`
                    : "Portal access prepared."
                }
              />
            </form>
          </div>

          <div className="panel">
            <h3>Credentials</h3>
            <button
              type="button"
              disabled={credentialsAction.status === "loading"}
              onClick={() => credentialsAction.run()}
            >
              Load credentials
            </button>
            <StatusBanner
              status={credentialsAction.status}
              errorMessage={credentialsAction.errorMessage}
              successMessage="Credentials loaded."
            />

            <form onSubmit={handleAttachCredential}>
              <label htmlFor="credential-type">Credential type</label>
              <select
                id="credential-type"
                value={credentialType}
                onChange={(event) => setCredentialType(event.target.value)}
              >
                <option value="medical_license">Medical license</option>
                <option value="specialty_certification">Specialty certification</option>
                <option value="board_certification">Board certification</option>
                <option value="institutional_registration">Institutional registration</option>
                <option value="other">Other</option>
              </select>
              <label htmlFor="credential-number">Credential number</label>
              <input
                id="credential-number"
                value={credentialNumber}
                onChange={(event) => setCredentialNumber(event.target.value)}
                required
              />
              <label htmlFor="credential-authority">Issuing authority</label>
              <input
                id="credential-authority"
                value={issuingAuthority}
                onChange={(event) => setIssuingAuthority(event.target.value)}
                required
              />
              <button type="submit" disabled={attachCredentialAction.status === "loading"}>
                Attach credential
              </button>
              <StatusBanner
                status={attachCredentialAction.status}
                errorMessage={attachCredentialAction.errorMessage}
                successMessage="Credential attached."
              />
            </form>

            {credentialsAction.status === "success" && credentials.length === 0 ? (
              <p className="empty-state">No credentials attached to this doctor.</p>
            ) : null}

            {credentials.length > 0 ? (
              <table>
                <caption>Doctor credentials</caption>
                <thead>
                  <tr>
                    <th scope="col">Id</th>
                    <th scope="col">Type</th>
                    <th scope="col">Number</th>
                    <th scope="col">Verification status</th>
                    <th scope="col">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {credentials.map((credential) => (
                    <tr key={credential.credentialId}>
                      <td>{credential.credentialId}</td>
                      <td>{credential.credentialType}</td>
                      <td>{credential.credentialNumber}</td>
                      <td>
                        <span className="catalog-status">{credential.verificationStatus}</span>
                      </td>
                      <td>
                        <button
                          type="button"
                          disabled={
                            credential.verificationStatus === "verified" ||
                            credential.verificationStatus === "revoked" ||
                            verifyCredentialAction.status === "loading"
                          }
                          onClick={() => void handleVerifyCredential(credential.credentialId)}
                        >
                          Verify
                        </button>{" "}
                        <button
                          type="button"
                          disabled={credential.verificationStatus === "revoked"}
                          onClick={() => setCredentialToRevoke(credential.credentialId)}
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
              status={verifyCredentialAction.status}
              errorMessage={verifyCredentialAction.errorMessage}
              successMessage="Credential verified."
            />
            <StatusBanner
              status={revokeCredentialAction.status}
              errorMessage={revokeCredentialAction.errorMessage}
              successMessage="Credential revoked."
            />
          </div>

          <SpecialtiesPanel
            specialties={specialties}
            specialtiesAction={specialtiesAction}
            onLoad={() => specialtiesAction.run()}
            specialtyCode={specialtyCode}
            onSpecialtyCodeChange={setSpecialtyCode}
            primary={specialtyPrimary}
            onPrimaryChange={setSpecialtyPrimary}
            onAssign={handleAssignSpecialty}
            assignAction={assignSpecialtyAction}
            onRequestUnassign={(assignmentId) => setSpecialtyToUnassign(assignmentId)}
            unassignAction={unassignSpecialtyAction}
          />
        </>
      ) : (
        <p className="empty-state">
          Select a doctor row to view its snapshot, credentials, suspension and portal-access
          actions.
        </p>
      )}

      <ConfirmDialog
        open={confirmingSuspend}
        title="Confirm doctor suspension"
        description="A suspended doctor immediately loses referring eligibility, even with verified credentials. Continue?"
        onCancel={() => setConfirmingSuspend(false)}
        onConfirm={async () => {
          setConfirmingSuspend(false);
          const result = await suspendAction.run();
          if (result.ok) {
            setDoctors((current) =>
              current.map((doctor) =>
                doctor.doctorId === result.data.doctorId ? result.data : doctor,
              ),
            );
          }
        }}
      />

      <ConfirmDialog
        open={Boolean(credentialToRevoke)}
        title="Revoke credential"
        description="This credential will no longer count toward referring eligibility. Continue?"
        onCancel={() => setCredentialToRevoke(undefined)}
        onConfirm={async () => {
          if (credentialToRevoke) {
            const result = await revokeCredentialAction.run(credentialToRevoke);
            if (result.ok) {
              setCredentials((current) =>
                current.map((item) =>
                  item.credentialId === result.data.credentialId ? result.data : item,
                ),
              );
            }
          }
          setCredentialToRevoke(undefined);
        }}
      />

      <ConfirmDialog
        open={confirmingRetire}
        title="Confirm doctor retirement"
        description="A retired doctor permanently loses referring eligibility. Continue?"
        onCancel={() => setConfirmingRetire(false)}
        onConfirm={async () => {
          setConfirmingRetire(false);
          const result = await retireAction.run();
          if (result.ok) {
            setDoctors((current) =>
              current.map((doctor) =>
                doctor.doctorId === result.data.doctorId ? result.data : doctor,
              ),
            );
          }
        }}
      />

      <ConfirmDialog
        open={Boolean(specialtyToUnassign)}
        title="Unassign specialty"
        description="This specialty assignment will be removed from the doctor. Continue?"
        onCancel={() => setSpecialtyToUnassign(undefined)}
        onConfirm={async () => {
          if (specialtyToUnassign) {
            const result = await unassignSpecialtyAction.run(specialtyToUnassign);
            if (result.ok) {
              setSpecialties((current) =>
                current.filter((specialty) => specialty.assignmentId !== specialtyToUnassign),
              );
            }
          }
          setSpecialtyToUnassign(undefined);
        }}
      />
    </section>
  );
}
