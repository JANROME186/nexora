import { useState, type FormEvent } from "react";
import {
  detectPersonDuplicates,
  getPersonMergeCoordination,
  initiatePersonMergeCoordination,
  rebuildPersonSearchIndex,
  searchPersons
} from "../../api/peopleApi";
import type { PersonDuplicateCandidate, PersonMergeCoordination, PersonSearchEntry } from "../../api/types";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useAsyncAction } from "../../state/useAsyncAction";
import { ScopeIndicator } from "../common/ScopeIndicator";
import { StatusBanner } from "../common/StatusBanner";

function confidenceClass(confidence: number) {
  if (confidence >= 0.85) return "confidence-badge confidence-badge--high";
  if (confidence >= 0.5) return "confidence-badge confidence-badge--medium";
  return "confidence-badge confidence-badge--low";
}

/**
 * BCM-PER-001 employee portal surface: global person search (SCR-PER-001-01), duplicate
 * detection with confidence scoring (SCR-PER-001-02) and merge coordination (SCR-PER-001-03).
 */
export function PersonSearchScreen() {
  const { scope } = useAdminScope();
  const { tenantId } = scope;
  const canUse = Boolean(tenantId);

  const [personKind, setPersonKind] = useState("");
  const [familyName, setFamilyName] = useState("");
  const [givenName, setGivenName] = useState("");
  const [results, setResults] = useState<PersonSearchEntry[]>([]);
  const searchAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before searching people.");
    const found = await searchPersons(tenantId, {
      personKind: personKind || undefined,
      familyName: familyName || undefined,
      givenName: givenName || undefined
    });
    setResults(found);
    return found;
  });

  const [dupFamilyName, setDupFamilyName] = useState("");
  const [dupGivenName, setDupGivenName] = useState("");
  const [dupBirthDate, setDupBirthDate] = useState("");
  const [candidates, setCandidates] = useState<PersonDuplicateCandidate[]>([]);
  const duplicateAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before detecting duplicates.");
    const found = await detectPersonDuplicates({
      tenantId,
      familyName: dupFamilyName || undefined,
      givenName: dupGivenName || undefined,
      birthDate: dupBirthDate || undefined
    });
    setCandidates(found);
    return found;
  });

  const rebuildAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before rebuilding the search index.");
    return rebuildPersonSearchIndex(tenantId);
  });

  const [sourceRecordId, setSourceRecordId] = useState("");
  const [targetRecordId, setTargetRecordId] = useState("");
  const [coordinationLookupId, setCoordinationLookupId] = useState("");
  const [coordination, setCoordination] = useState<PersonMergeCoordination | undefined>(undefined);
  const mergeAction = useAsyncAction(async () => {
    if (!tenantId) throw new Error("Select a tenant before coordinating a merge.");
    const created = await initiatePersonMergeCoordination({ tenantId, sourceRecordId, targetRecordId });
    setCoordination(created);
    setCoordinationLookupId(created.coordinationId);
    return created;
  });
  const lookupAction = useAsyncAction(async () => {
    const found = await getPersonMergeCoordination(coordinationLookupId);
    setCoordination(found);
    return found;
  });

  async function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await searchAction.run();
  }

  async function handleDetectDuplicates(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await duplicateAction.run();
  }

  async function handleInitiateMerge(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await mergeAction.run();
  }

  async function handleLookupCoordination(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await lookupAction.run();
  }

  return (
    <section aria-labelledby="person-search-heading">
      <h2 id="person-search-heading">People Search and Duplicate Resolution</h2>
      <ScopeIndicator />
      {!canUse ? (
        <p className="status-banner status-banner--error">Select a tenant before searching people.</p>
      ) : null}

      <div className="panel">
        <h3>Global search (patients and doctors)</h3>
        <form onSubmit={handleSearch}>
          <label htmlFor="search-person-kind">Person kind</label>
          <select id="search-person-kind" value={personKind} onChange={(event) => setPersonKind(event.target.value)}>
            <option value="">All</option>
            <option value="patient">Patient</option>
            <option value="doctor">Doctor</option>
          </select>
          <label htmlFor="search-family-name">Family name</label>
          <input id="search-family-name" value={familyName} onChange={(event) => setFamilyName(event.target.value)} />
          <label htmlFor="search-given-name">Given name</label>
          <input id="search-given-name" value={givenName} onChange={(event) => setGivenName(event.target.value)} />
          <button type="submit" disabled={!canUse || searchAction.status === "loading"}>
            Search
          </button>
          <StatusBanner
            status={searchAction.status}
            errorMessage={searchAction.errorMessage}
            successMessage="Search completed."
          />
        </form>

        {searchAction.status === "success" && results.length === 0 ? (
          <p className="empty-state">No patients or doctors matched this search.</p>
        ) : null}

        {results.length > 0 ? (
          <table>
            <caption>Search results</caption>
            <thead>
              <tr>
                <th scope="col">Kind</th>
                <th scope="col">Code</th>
                <th scope="col">Name</th>
                <th scope="col">Birth date</th>
                <th scope="col">Document</th>
                <th scope="col">Status</th>
              </tr>
            </thead>
            <tbody>
              {results.map((entry) => (
                <tr key={`${entry.personKind}-${entry.sourceAggregateId}`}>
                  <td>{entry.personKind}</td>
                  <td>{entry.personCode}</td>
                  <td>{entry.fullName}</td>
                  <td>{entry.birthDate ?? "-"}</td>
                  <td>
                    {entry.primaryDocumentType ?? "-"} {entry.primaryDocumentNumberMasked ?? ""}
                  </td>
                  <td>{entry.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : null}

        <button
          type="button"
          disabled={!canUse || rebuildAction.status === "loading"}
          onClick={() => rebuildAction.run()}
        >
          Rebuild search index for this tenant
        </button>
        <StatusBanner
          status={rebuildAction.status}
          errorMessage={rebuildAction.errorMessage}
          successMessage={
            rebuildAction.data
              ? `Index rebuilt: ${rebuildAction.data.patientCount} patients, ${rebuildAction.data.doctorCount} doctors.`
              : "Index rebuilt."
          }
        />
      </div>

      <div className="panel">
        <h3>Duplicate detection (tenant-configurable confidence scoring)</h3>
        <form onSubmit={handleDetectDuplicates}>
          <label htmlFor="dup-family-name">Family name</label>
          <input
            id="dup-family-name"
            value={dupFamilyName}
            onChange={(event) => setDupFamilyName(event.target.value)}
          />
          <label htmlFor="dup-given-name">Given name</label>
          <input id="dup-given-name" value={dupGivenName} onChange={(event) => setDupGivenName(event.target.value)} />
          <label htmlFor="dup-birth-date">Birth date</label>
          <input
            id="dup-birth-date"
            type="date"
            value={dupBirthDate}
            onChange={(event) => setDupBirthDate(event.target.value)}
          />
          <button type="submit" disabled={!canUse || duplicateAction.status === "loading"}>
            Detect duplicates
          </button>
          <StatusBanner
            status={duplicateAction.status}
            errorMessage={duplicateAction.errorMessage}
            successMessage="Duplicate detection completed."
          />
        </form>

        {duplicateAction.status === "success" && candidates.length === 0 ? (
          <p className="empty-state">No duplicate candidates were found.</p>
        ) : null}

        {candidates.length > 0 ? (
          <table>
            <caption>Duplicate candidates</caption>
            <thead>
              <tr>
                <th scope="col">Kind</th>
                <th scope="col">Record id</th>
                <th scope="col">Name</th>
                <th scope="col">Confidence</th>
                <th scope="col">Match reason</th>
              </tr>
            </thead>
            <tbody>
              {candidates.map((candidate) => (
                <tr key={`${candidate.personKind}-${candidate.sourceAggregateId}`}>
                  <td>{candidate.personKind}</td>
                  <td>{candidate.sourceAggregateId}</td>
                  <td>{candidate.fullName}</td>
                  <td>
                    <span className={confidenceClass(candidate.confidence)}>
                      {(candidate.confidence * 100).toFixed(0)}%
                    </span>
                  </td>
                  <td>{candidate.matchReason}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : null}
      </div>

      <div className="panel">
        <h3>Merge coordination workspace</h3>
        <form onSubmit={handleInitiateMerge}>
          <label htmlFor="merge-source-id">Source record id (duplicate)</label>
          <input
            id="merge-source-id"
            value={sourceRecordId}
            onChange={(event) => setSourceRecordId(event.target.value)}
            required
          />
          <label htmlFor="merge-target-id">Target record id (survivor)</label>
          <input
            id="merge-target-id"
            value={targetRecordId}
            onChange={(event) => setTargetRecordId(event.target.value)}
            required
          />
          <button type="submit" disabled={!canUse || mergeAction.status === "loading"}>
            Initiate merge coordination
          </button>
          <StatusBanner
            status={mergeAction.status}
            errorMessage={mergeAction.errorMessage}
            successMessage="Merge coordination initiated."
          />
        </form>

        <form onSubmit={handleLookupCoordination}>
          <label htmlFor="coordination-lookup-id">Coordination id</label>
          <input
            id="coordination-lookup-id"
            value={coordinationLookupId}
            onChange={(event) => setCoordinationLookupId(event.target.value)}
            required
          />
          <button type="submit" disabled={lookupAction.status === "loading"}>
            Look up coordination
          </button>
          <StatusBanner
            status={lookupAction.status}
            errorMessage={lookupAction.errorMessage}
            successMessage="Coordination loaded."
          />
        </form>

        {coordination ? (
          <table>
            <caption>Merge coordination detail</caption>
            <tbody>
              <tr>
                <th scope="row">Coordination id</th>
                <td>{coordination.coordinationId}</td>
              </tr>
              <tr>
                <th scope="row">Source</th>
                <td>
                  {coordination.sourceKind} / {coordination.sourceRecordId}
                </td>
              </tr>
              <tr>
                <th scope="row">Target</th>
                <td>
                  {coordination.targetKind} / {coordination.targetRecordId}
                </td>
              </tr>
              <tr>
                <th scope="row">Status</th>
                <td>{coordination.status}</td>
              </tr>
              <tr>
                <th scope="row">Patient merge applied</th>
                <td>{coordination.patientMergeApplied ? "Yes" : "No"}</td>
              </tr>
            </tbody>
          </table>
        ) : (
          <p className="empty-state">No merge coordination loaded yet.</p>
        )}
      </div>
    </section>
  );
}
