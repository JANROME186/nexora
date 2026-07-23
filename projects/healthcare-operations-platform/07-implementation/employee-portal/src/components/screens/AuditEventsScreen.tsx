import { useState, type FormEvent } from "react";
import { searchAuditEvents } from "../../api/platformFoundationApi";
import type { AuditEvent } from "../../api/types";
import { useAsyncAction } from "../../state/useAsyncAction";
import { useAdminScope } from "../../state/AdminScopeContext";
import { useLocale } from "../../i18n/LocaleContext";
import { StatusBanner } from "../common/StatusBanner";
import { ScopeIndicator } from "../common/ScopeIndicator";

/**
 * Audit event search screen (PF-FE-001 / BCM-PLT-007).
 *
 * Debt-first action for COM-MOD-013-FE-001 (TD-I18N-002 material reduction): all previously
 * hardcoded English strings are now sourced from the active locale catalog, matching the
 * enterprise-product-foundation-standard `localization_and_i18n` requirement that no user-visible
 * text may be hardcoded.
 */
export function AuditEventsScreen() {
  const { scope } = useAdminScope();
  const { t } = useLocale();
  const labels = t.auditEvents;
  const [tenantId, setTenantId] = useState(scope.tenantId ?? "");
  const [subjectId, setSubjectId] = useState("");
  const [events, setEvents] = useState<AuditEvent[]>([]);

  const searchAction = useAsyncAction(searchAuditEvents);

  async function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const result = await searchAction.run({
      tenantId: tenantId || undefined,
      subjectId: subjectId || undefined,
    });
    if (result.ok) {
      setEvents(result.data);
    }
  }

  return (
    <section aria-labelledby="audit-events-heading">
      <h2 id="audit-events-heading">{labels.heading}</h2>
      <ScopeIndicator />
      <p>{labels.description}</p>

      <form onSubmit={handleSearch}>
        <label htmlFor="audit-tenant-id">{labels.tenantIdLabel}</label>
        <input
          id="audit-tenant-id"
          value={tenantId}
          onChange={(event) => setTenantId(event.target.value)}
        />
        <label htmlFor="audit-subject-id">{labels.subjectIdLabel}</label>
        <input
          id="audit-subject-id"
          value={subjectId}
          onChange={(event) => setSubjectId(event.target.value)}
        />
        <button type="submit" disabled={searchAction.status === "loading"}>
          {labels.searchButton}
        </button>
        <StatusBanner
          status={searchAction.status}
          errorMessage={searchAction.errorMessage}
          successMessage={`${events.length} ${labels.found}`}
        />
      </form>

      <table>
        <caption>{labels.tableCaption}</caption>
        <thead>
          <tr>
            <th scope="col">{labels.columns.occurredAt}</th>
            <th scope="col">{labels.columns.actor}</th>
            <th scope="col">{labels.columns.action}</th>
            <th scope="col">{labels.columns.subject}</th>
            <th scope="col">{labels.columns.tenant}</th>
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
