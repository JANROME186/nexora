import { useState, type FormEvent } from "react";
import { searchAuditEvents } from "../../api/platformFoundationApi";
import type { AuditEvent } from "../../api/types";
import { useAsyncAction } from "../../state/useAsyncAction";
import { useAdminScope } from "../../state/AdminScopeContext";
import { StatusBanner } from "../common/StatusBanner";
import { ScopeIndicator } from "../common/ScopeIndicator";

export function AuditEventsScreen() {
  const { scope } = useAdminScope();
  const [tenantId, setTenantId] = useState(scope.tenantId ?? "");
  const [subjectId, setSubjectId] = useState("");
  const [events, setEvents] = useState<AuditEvent[]>([]);

  const searchAction = useAsyncAction(searchAuditEvents);

  async function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await searchAction.run({
      tenantId: tenantId || undefined,
      subjectId: subjectId || undefined
    });
    if (result.ok) {
      setEvents(result.data);
    }
  }

  return (
    <section aria-labelledby="audit-events-heading">
      <h2 id="audit-events-heading">Audit Search</h2>
      <ScopeIndicator />
      <p>Results are limited to what the signed-in actor is authorized to see.</p>

      <form onSubmit={handleSearch}>
        <label htmlFor="audit-tenant-id">Tenant id (optional)</label>
        <input
          id="audit-tenant-id"
          value={tenantId}
          onChange={(event) => setTenantId(event.target.value)}
        />
        <label htmlFor="audit-subject-id">Subject id (optional)</label>
        <input
          id="audit-subject-id"
          value={subjectId}
          onChange={(event) => setSubjectId(event.target.value)}
        />
        <button type="submit" disabled={searchAction.status === "loading"}>
          Search audit events
        </button>
        <StatusBanner
          status={searchAction.status}
          errorMessage={searchAction.errorMessage}
          successMessage={`${events.length} event(s) found.`}
        />
      </form>

      <table>
        <caption>Audit events</caption>
        <thead>
          <tr>
            <th scope="col">Occurred at</th>
            <th scope="col">Actor</th>
            <th scope="col">Action</th>
            <th scope="col">Subject</th>
            <th scope="col">Tenant</th>
          </tr>
        </thead>
        <tbody>
          {events.map((event) => (
            <tr key={event.auditEventId}>
              <td>{event.occurredAt}</td>
              <td>
                {event.actorId} ({event.actorType})
              </td>
              <td>{event.action}</td>
              <td>
                {event.subjectType}:{event.subjectId}
              </td>
              <td>{event.tenantId ?? "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
