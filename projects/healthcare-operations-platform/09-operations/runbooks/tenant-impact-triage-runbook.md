# HOP Tenant-Impact Triage Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Determine which tenant(s) are affected by a HOP incident, deployment or restore, and verify tenant
isolation held throughout. This runbook is HOP's primary operational, defense-in-depth compensating
control for `TD-DB-004` (tenant scoping enforced by application-level `WHERE` clauses, not native
PostgreSQL Row Level Security): until RLS exists, every incident, restore and higher-risk deployment
must run this runbook's cross-tenant leakage check.

## Applicable environment

Executable today on `local` (single or seeded multi-tenant); applicable unchanged across
`dev`/`qa`/`staging`/`prod`, mandatory for any multi-tenant `prod` incident.

## Capability traceability

- `BCM-ORG-001` — owns `Tenant` (`AGG-017`), subscription state and isolation policy.
- `BCM-PLT-001` — the session/request tenant context every `WHERE` clause depends on.
- `BCM-PLT-007` — audit events are the evidence source for reconstructing tenant scope.
- `BCM-PLT-006` — telemetry is the future fast-path signal once tenant context is in logs.

## Technical debt compensating control

`TD-DB-004` is **materially reduced** by this runbook: it adds an executable, mandatory
cross-tenant leakage check as defense-in-depth alongside the existing application-level
`WHERE tenant_id = ?` pattern, pending native Row Level Security. `TD-DB-004`'s own acceptance
criteria (RLS at the database layer) remain open and require a future code-changing backlog item —
this runbook reduces the operational risk window, it does not close the debt.

## IAM and audit

Requires `platform:tenant:read` and `audit:read`, held by platform operations on-call. Every triage
run and its conclusion must be logged in the incident evidence bundle.

## Prerequisites

- Backend API and PostgreSQL reachable.
- The incident, deployment or restore being triaged is already identified with a timestamp window.

## Procedure

1. **Identify the tenant(s) directly implicated.** If none is known yet, treat the incident as
   platform-wide until proven otherwise.
2. **Query audit events for the time window**:
   `Invoke-RestMethod "http://localhost:8080/api/audit/events" -Headers @{ "X-Tenant-Id" = "<tenant-id>" }`
   — results must be scoped only to the requested tenant, with no cross-tenant leakage.
3. **Cross-tenant leakage check** (the `TD-DB-004` compensating control):
   `docker exec hop-local-postgres psql -U hop -d hop -c "SELECT tenant_id, count(*) FROM
   identity.user_accounts GROUP BY tenant_id ORDER BY tenant_id;"` — row counts per tenant must
   match the expected baseline; repeat for any other table implicated by the incident.
4. **Classify impact scope**: `single_tenant`, `multi_tenant`, or `platform_wide` (any leakage
   anomaly automatically classifies as `platform_wide` / P1, regardless of the originally reported
   severity).
5. **Record findings and hand back** to the calling runbook (`incident-response-runbook.md`,
   `restore-runbook.md`, or the deployment procedure that triggered this triage).

## Success criteria

Impact scope classified with supporting query evidence; no leakage anomaly found, or one found and
immediately escalated to P1.

## Failure criteria

Triage skipped for an incident/restore/higher-risk deployment; a leakage anomaly found but not
escalated; queries fail with no fallback attempted.

## Evidence expected

Query commands and output, impact classification and reasoning, timestamped and attached to the
incident evidence bundle.

## Responsible role

Platform operations on-call.

## If this fails

A cross-tenant leakage anomaly escalates immediately to P1 in `incident-response-runbook.md`,
notifying the release manager and product owner, and requires its own
`post-incident-review-runbook.md` entry even after mitigation. Do not close the incident until the
root cause is understood.

## Known gaps and forward pointers

- The leakage check is manual, ad hoc SQL per affected table; no automated, repository-wide
  tenant-isolation verification tool exists yet. `TD-DB-004`'s native RLS remains the durable fix.
- No `tenant_id` MDC context in structured logs yet, so telemetry cannot fast-path this triage;
  it currently relies on audit events and direct database queries. Forward pointer:
  `COM-MOD-012-BE-001`.
