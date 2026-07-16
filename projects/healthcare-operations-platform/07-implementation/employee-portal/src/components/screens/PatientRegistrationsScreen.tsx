import { useState, type FormEvent } from "react";
import { ApiError } from "../../api/httpClient";
import {
  cancelPatientRegistration,
  commitPatientRegistration,
  detectPersonDuplicates,
  listPatientRegistrations,
  startPatientRegistration,
} from "../../api/peopleApi";
import type { PatientRegistrationRequestRecord, PersonDuplicateCandidate } from "../../api/types";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ConfirmDialog } from "../common/ConfirmDialog";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

type CommitPhase = "idle" | "loading" | "success" | "error";

function confidenceClass(confidence: number) {
  if (confidence >= 0.85) return "confidence-badge confidence-badge--high";
  if (confidence >= 0.5) return "confidence-badge confidence-badge--medium";
  return "confidence-badge confidence-badge--low";
}

/**
 * BCM-ATT-002 employee portal surface: registration intake (SCR-REG-002-01), request list
 * (SCR-REG-002-02) and detail with match-resolution and commit (SCR-REG-002-03). The commit flow
 * explicitly branches on the backend's 2xx/4xx outcomes: a 409 REGISTRATION_MATCH_RESOLUTION_REQUIRED
 * response triggers a live duplicate-detection lookup so the operator can visually pick the correct
 * existing patient instead of guessing an id.
 */
export function PatientRegistrationsScreen() {
  const { scope } = useAdminScope();
  const { tenantId, laboratoryId, branchId } = scope;
  const canUse = Boolean(tenantId && laboratoryId && branchId);

  const [registrations, setRegistrations] = useState<PatientRegistrationRequestRecord[]>([]);
  const [selected, setSelected] = useState<PatientRegistrationRequestRecord | undefined>(undefined);

  const listAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before listing registrations.");
    const loaded = await listPatientRegistrations(tenantId);
    setRegistrations(loaded);
    return loaded;
  });

  const [intakeChannel, setIntakeChannel] = useState("walk_in");
  const [registrationKind, setRegistrationKind] = useState("new_patient");
  const [givenName, setGivenName] = useState("");
  const [familyName, setFamilyName] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [documentType, setDocumentType] = useState("national_id");
  const [documentNumber, setDocumentNumber] = useState("");

  const startAction = useAsyncAction(async () => {
    if (!tenantId || !laboratoryId || !branchId) {
      throw new Error("Select tenant, laboratory and branch scope before starting a registration.");
    }
    const created = await startPatientRegistration({
      tenantId,
      laboratoryId,
      branchId,
      intakeChannel,
      registrationKind,
      givenName,
      familyName,
      birthDate: birthDate || undefined,
      documentType,
      documentNumber,
    });
    setRegistrations((current) => [created, ...current]);
    setSelected(created);
    setGivenName("");
    setFamilyName("");
    setBirthDate("");
    setDocumentNumber("");
    return created;
  });

  // Commit is handled outside useAsyncAction so the 409 branch can inspect ApiError.status and
  // trigger a duplicate-candidate lookup instead of just showing a generic error message.
  const [commitPhase, setCommitPhase] = useState<CommitPhase>("idle");
  const [commitErrorMessage, setCommitErrorMessage] = useState<string | undefined>(undefined);
  const [duplicateCandidates, setDuplicateCandidates] = useState<PersonDuplicateCandidate[]>([]);
  const [resolvedExistingPatientId, setResolvedExistingPatientId] = useState("");
  const [commitPatientCode, setCommitPatientCode] = useState("");
  const [commitSexAtBirth, setCommitSexAtBirth] = useState("female");
  const [repRelationship, setRepRelationship] = useState("parent");
  const [repGivenName, setRepGivenName] = useState("");
  const [repFamilyName, setRepFamilyName] = useState("");
  const [repDocumentType, setRepDocumentType] = useState("national_id");
  const [repDocumentNumber, setRepDocumentNumber] = useState("");
  const [dataProcessingConsent, setDataProcessingConsent] = useState(false);
  const [consentGrantedBy, setConsentGrantedBy] = useState("patient");

  const [cancelReason, setCancelReason] = useState("");
  const [confirmingCancel, setConfirmingCancel] = useState(false);
  const cancelAction = useAsyncAction(async () => {
    if (!selected) throw new Error("Select a registration first.");
    return cancelPatientRegistration(
      selected.registrationRequestId,
      cancelReason ? { reasonCode: cancelReason } : undefined,
    );
  });

  async function handleList() {
    await listAction.run();
  }

  async function handleStart(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await startAction.run();
  }

  function selectRegistration(registration: PatientRegistrationRequestRecord) {
    setSelected(registration);
    setCommitPhase("idle");
    setCommitErrorMessage(undefined);
    setDuplicateCandidates([]);
    setResolvedExistingPatientId("");
    setCommitPatientCode(registration.draftPatientCode ?? "");
  }

  async function handleCommit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selected) return;
    setCommitPhase("loading");
    setCommitErrorMessage(undefined);
    try {
      const committed = await commitPatientRegistration(selected.registrationRequestId, {
        resolvedExistingPatientId: resolvedExistingPatientId || undefined,
        patientCode: resolvedExistingPatientId ? undefined : commitPatientCode,
        sexAtBirth: resolvedExistingPatientId ? undefined : commitSexAtBirth,
        representativeRelationship:
          registrationKindOf(selected) === "representative_registration"
            ? repRelationship
            : undefined,
        representativeGivenName:
          registrationKindOf(selected) === "representative_registration" ? repGivenName : undefined,
        representativeFamilyName:
          registrationKindOf(selected) === "representative_registration"
            ? repFamilyName
            : undefined,
        representativeDocumentType:
          registrationKindOf(selected) === "representative_registration"
            ? repDocumentType
            : undefined,
        representativeDocumentNumber:
          registrationKindOf(selected) === "representative_registration"
            ? repDocumentNumber
            : undefined,
        consents: dataProcessingConsent
          ? [{ consentType: "data_processing", granted: true, grantedBy: consentGrantedBy }]
          : [],
      });
      setCommitPhase("success");
      setDuplicateCandidates([]);
      setSelected(committed);
      setRegistrations((current) =>
        current.map((registration) =>
          registration.registrationRequestId === committed.registrationRequestId
            ? committed
            : registration,
        ),
      );
    } catch (error) {
      setCommitPhase("error");
      if (error instanceof ApiError) {
        setCommitErrorMessage(error.message);
        // Explicit 409 handling: a high-confidence duplicate blocks commit until the operator
        // resolves it. Look the candidates up so the choice is visual, not a guessed id.
        if (error.status === 409 && error.message.includes("MATCH_RESOLUTION_REQUIRED")) {
          try {
            const candidates = await detectPersonDuplicates({
              tenantId: selected.tenantId,
              personKind: "patient",
              familyName: selected.draftFamilyName,
              givenName: selected.draftGivenName,
              birthDate: selected.birthDate,
            });
            setDuplicateCandidates(candidates);
          } catch {
            // Duplicate lookup is a convenience; if it fails the operator still sees the 409 message.
          }
        }
      } else {
        setCommitErrorMessage("Unexpected error. Please try again.");
      }
    }
  }

  function registrationKindOf(registration: PatientRegistrationRequestRecord) {
    return registration.registrationKind;
  }

  return (
    <section aria-labelledby="registrations-heading">
      <h2 id="registrations-heading">Patient Registrations</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">
          Select a tenant, laboratory and branch before starting patient registrations.
        </p>
      ) : null}

      <div className="panel">
        <h3>Start registration</h3>
        <form onSubmit={handleStart}>
          <label htmlFor="reg-intake-channel">Intake channel</label>
          <select
            id="reg-intake-channel"
            value={intakeChannel}
            onChange={(event) => setIntakeChannel(event.target.value)}
          >
            <option value="walk_in">Walk in</option>
            <option value="appointment">Appointment</option>
            <option value="portal_handoff">Portal handoff</option>
            <option value="migration_import">Migration import</option>
          </select>
          <label htmlFor="reg-kind">Registration kind</label>
          <select
            id="reg-kind"
            value={registrationKind}
            onChange={(event) => setRegistrationKind(event.target.value)}
          >
            <option value="new_patient">New patient</option>
            <option value="existing_patient_confirmation">Existing patient confirmation</option>
            <option value="representative_registration">Representative registration</option>
          </select>
          <label htmlFor="reg-given-name">Given name</label>
          <input
            id="reg-given-name"
            value={givenName}
            onChange={(event) => setGivenName(event.target.value)}
            required
          />
          <label htmlFor="reg-family-name">Family name</label>
          <input
            id="reg-family-name"
            value={familyName}
            onChange={(event) => setFamilyName(event.target.value)}
            required
          />
          <label htmlFor="reg-birth-date">Birth date</label>
          <input
            id="reg-birth-date"
            type="date"
            value={birthDate}
            onChange={(event) => setBirthDate(event.target.value)}
          />
          <p className="field-hint">
            A birth date under the tenant&apos;s age-of-majority policy automatically switches a
            &quot;New patient&quot; intake to &quot;Representative registration&quot; (RN-008).
          </p>
          <label htmlFor="reg-document-type">Document type</label>
          <select
            id="reg-document-type"
            value={documentType}
            onChange={(event) => setDocumentType(event.target.value)}
          >
            <option value="national_id">National id</option>
            <option value="passport">Passport</option>
            <option value="other">Other</option>
          </select>
          <label htmlFor="reg-document-number">Document number</label>
          <input
            id="reg-document-number"
            value={documentNumber}
            onChange={(event) => setDocumentNumber(event.target.value)}
            required
          />
          <button type="submit" disabled={!canUse || startAction.status === "loading"}>
            Start registration
          </button>
          <StatusBanner
            status={startAction.status}
            errorMessage={startAction.errorMessage}
            successMessage="Registration started (pending)."
          />
        </form>
      </div>

      <button
        type="button"
        disabled={!tenantId || listAction.status === "loading"}
        onClick={handleList}
      >
        Load registrations
      </button>
      <StatusBanner
        status={listAction.status}
        errorMessage={listAction.errorMessage}
        successMessage="Registrations loaded."
      />

      {listAction.status === "success" && registrations.length === 0 ? (
        <p className="empty-state">No patient registrations started yet for this tenant.</p>
      ) : null}

      {registrations.length > 0 ? (
        <table>
          <caption>Patient registration requests</caption>
          <thead>
            <tr>
              <th scope="col">Id</th>
              <th scope="col">Kind</th>
              <th scope="col">Name</th>
              <th scope="col">Outcome</th>
            </tr>
          </thead>
          <tbody>
            {registrations.map((registration) => (
              <tr key={registration.registrationRequestId}>
                <td>
                  <button
                    type="button"
                    className="link-button"
                    onClick={() => selectRegistration(registration)}
                  >
                    {registration.registrationRequestId}
                  </button>
                </td>
                <td>{registration.registrationKind}</td>
                <td>
                  {registration.draftGivenName} {registration.draftFamilyName}
                </td>
                <td>
                  <span className="catalog-status">{registration.outcome}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : null}

      {selected ? (
        <div className="panel">
          <h3>Registration detail: {selected.registrationRequestId}</h3>
          <table>
            <tbody>
              <tr>
                <th scope="row">Kind</th>
                <td>{selected.registrationKind}</td>
              </tr>
              <tr>
                <th scope="row">Draft name</th>
                <td>
                  {selected.draftGivenName} {selected.draftFamilyName}
                </td>
              </tr>
              <tr>
                <th scope="row">Outcome</th>
                <td>
                  <span className="catalog-status">{selected.outcome}</span>
                </td>
              </tr>
              {selected.outcomePatientId ? (
                <tr>
                  <th scope="row">Resulting patient id</th>
                  <td>{selected.outcomePatientId}</td>
                </tr>
              ) : null}
            </tbody>
          </table>

          {selected.outcome === "pending" ? (
            <>
              <h4>Commit registration</h4>
              <form onSubmit={handleCommit}>
                <label htmlFor="commit-resolved-id">
                  Resolved existing patient id (leave blank for a new patient)
                </label>
                <input
                  id="commit-resolved-id"
                  value={resolvedExistingPatientId}
                  onChange={(event) => setResolvedExistingPatientId(event.target.value)}
                />
                {!resolvedExistingPatientId ? (
                  <>
                    <label htmlFor="commit-patient-code">New patient code</label>
                    <input
                      id="commit-patient-code"
                      value={commitPatientCode}
                      onChange={(event) => setCommitPatientCode(event.target.value)}
                    />
                    <label htmlFor="commit-sex">Sex at birth</label>
                    <select
                      id="commit-sex"
                      value={commitSexAtBirth}
                      onChange={(event) => setCommitSexAtBirth(event.target.value)}
                    >
                      <option value="female">Female</option>
                      <option value="male">Male</option>
                      <option value="intersex">Intersex</option>
                      <option value="unknown">Unknown</option>
                    </select>
                  </>
                ) : null}

                {selected.registrationKind === "representative_registration" ? (
                  <>
                    <p className="field-hint">
                      Representative details are required to commit this registration kind (RN-003).
                    </p>
                    <label htmlFor="commit-rep-relationship">Representative relationship</label>
                    <select
                      id="commit-rep-relationship"
                      value={repRelationship}
                      onChange={(event) => setRepRelationship(event.target.value)}
                    >
                      <option value="parent">Parent</option>
                      <option value="legal_guardian">Legal guardian</option>
                      <option value="spouse">Spouse</option>
                      <option value="power_of_attorney">Power of attorney</option>
                      <option value="other">Other</option>
                    </select>
                    <label htmlFor="commit-rep-given-name">Representative given name</label>
                    <input
                      id="commit-rep-given-name"
                      value={repGivenName}
                      onChange={(event) => setRepGivenName(event.target.value)}
                    />
                    <label htmlFor="commit-rep-family-name">Representative family name</label>
                    <input
                      id="commit-rep-family-name"
                      value={repFamilyName}
                      onChange={(event) => setRepFamilyName(event.target.value)}
                    />
                    <label htmlFor="commit-rep-document-type">Representative document type</label>
                    <select
                      id="commit-rep-document-type"
                      value={repDocumentType}
                      onChange={(event) => setRepDocumentType(event.target.value)}
                    >
                      <option value="national_id">National id</option>
                      <option value="passport">Passport</option>
                      <option value="other">Other</option>
                    </select>
                    <label htmlFor="commit-rep-document-number">
                      Representative document number
                    </label>
                    <input
                      id="commit-rep-document-number"
                      value={repDocumentNumber}
                      onChange={(event) => setRepDocumentNumber(event.target.value)}
                    />
                  </>
                ) : null}

                <label htmlFor="commit-consent-granted-by">Consent granted by</label>
                <select
                  id="commit-consent-granted-by"
                  value={consentGrantedBy}
                  onChange={(event) => setConsentGrantedBy(event.target.value)}
                >
                  <option value="patient">Patient</option>
                  <option value="representative">Representative</option>
                </select>
                <label htmlFor="commit-consent-data-processing">
                  <input
                    id="commit-consent-data-processing"
                    type="checkbox"
                    checked={dataProcessingConsent}
                    onChange={(event) => setDataProcessingConsent(event.target.checked)}
                  />{" "}
                  Data processing consent granted (mandatory for this tenant)
                </label>

                <button type="submit" disabled={commitPhase === "loading"}>
                  Commit registration
                </button>
                <StatusBanner
                  status={commitPhase}
                  errorMessage={commitErrorMessage}
                  successMessage="Registration committed."
                />
              </form>

              {duplicateCandidates.length > 0 ? (
                <div className="panel">
                  <h4>High-confidence duplicate candidates</h4>
                  <p className="field-hint">
                    Pick the correct existing patient to resolve the match, or leave unresolved to
                    confirm a genuinely new patient is intended and adjust the draft details before
                    retrying.
                  </p>
                  <table>
                    <thead>
                      <tr>
                        <th scope="col">Record id</th>
                        <th scope="col">Name</th>
                        <th scope="col">Confidence</th>
                        <th scope="col">Match reason</th>
                        <th scope="col">Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {duplicateCandidates.map((candidate) => (
                        <tr key={candidate.sourceAggregateId}>
                          <td>{candidate.sourceAggregateId}</td>
                          <td>{candidate.fullName}</td>
                          <td>
                            <span className={confidenceClass(candidate.confidence)}>
                              {(candidate.confidence * 100).toFixed(0)}%
                            </span>
                          </td>
                          <td>{candidate.matchReason}</td>
                          <td>
                            <button
                              type="button"
                              onClick={() =>
                                setResolvedExistingPatientId(candidate.sourceAggregateId)
                              }
                            >
                              Use this patient
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : null}

              <h4>Cancel registration</h4>
              <form
                onSubmit={(event) => {
                  event.preventDefault();
                  setConfirmingCancel(true);
                }}
              >
                <label htmlFor="cancel-reason">Reason code (optional)</label>
                <input
                  id="cancel-reason"
                  value={cancelReason}
                  onChange={(event) => setCancelReason(event.target.value)}
                />
                <button type="submit" disabled={cancelAction.status === "loading"}>
                  Cancel registration
                </button>
                <StatusBanner
                  status={cancelAction.status}
                  errorMessage={cancelAction.errorMessage}
                  successMessage="Registration cancelled."
                />
              </form>
            </>
          ) : null}
        </div>
      ) : (
        <p className="empty-state">
          Select a registration row to view its detail and commit or cancel it.
        </p>
      )}

      <ConfirmDialog
        open={confirmingCancel}
        title="Confirm cancellation"
        description="This pending registration will be marked as cancelled and can no longer be committed. Continue?"
        onCancel={() => setConfirmingCancel(false)}
        onConfirm={async () => {
          setConfirmingCancel(false);
          const result = await cancelAction.run();
          if (result.ok) {
            setSelected(result.data);
            setRegistrations((current) =>
              current.map((registration) =>
                registration.registrationRequestId === result.data.registrationRequestId
                  ? result.data
                  : registration,
              ),
            );
          }
        }}
      />
    </section>
  );
}
