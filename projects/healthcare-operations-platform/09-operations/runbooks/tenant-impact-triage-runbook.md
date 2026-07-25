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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUNBOOK-TENANT-TRIAGE-001
  type: operational-runbook
  name: Tenant-Impact Triage Runbook
  version: 1.0.0
  status: active
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: tenant-impact-triage-runbook.md
  machine_readable: tenant-impact-triage-runbook.md
  owner: Nexora Platform Operations
  date: 2026-07-22
purpose: 'Determine which tenant(s) are affected by a HOP incident, deployment or
  restore, and verify that tenant isolation held throughout. This runbook is HOP''s
  primary operational, defense-in-depth compensating control for TD-DB-004 (tenant
  scoping is enforced by application-level WHERE clauses, not native PostgreSQL row-level
  security): until RLS exists, every incident, restore and higher-risk deployment
  must run this runbook''s cross-tenant leakage check.

  '
applicable_environments:
  local: executable_today_single_or_seeded_multi_tenant
  dev: applicable
  qa: applicable
  staging: applicable
  prod: applicable_mandatory_for_any_multi_tenant_incident
capability_traceability:
  BCM-ORG-001:
    responsibility: Owns Tenant (AGG-017), subscription state and tenant isolation
      policy; the authoritative source of "which tenants exist and what state are
      they in."
  BCM-PLT-001:
    responsibility: Session/request tenant context (tenant_id) that every application-level
      WHERE clause depends on.
  BCM-PLT-007:
    responsibility: Audit events are the evidence source for reconstructing which
      tenant a given action targeted.
  BCM-PLT-006:
    responsibility: Telemetry is the fast-path signal for scoped-vs-broad impact;
      tenant_id MDC context on every log line (COM-MOD-012-BE-001) lets platform_operations_on_call
      filter logs by tenant directly.
  BCM-PLT-002:
    responsibility: Feature flags (COM-MOD-012-BE-001) give TRIAGE-STEP-004's single_tenant
      outcome a real, auditable tenant-scoped mitigation lever without a code deploy.
technical_debt_compensating_control:
  item: TD-DB-004
  disposition: materially_reduced
  reason: 'This runbook adds an executable, mandatory operational compensating control
    -- a cross-tenant leakage check run during every incident, restore and higher-risk
    deployment -- as defense-in-depth alongside the existing application-level WHERE
    tenant_id = ? pattern, pending native PostgreSQL Row Level Security. TD-DB-004''s
    own acceptance criteria (RLS enforcing tenant isolation at the database layer)
    remain open and require a future code-changing backlog item; this runbook does
    not close TD-DB-004, it reduces the operational risk window.

    '
iam_and_audit:
  required_permissions:
  - platform:tenant:read
  - audit:read
  minimum_role: platform_operations_on_call
  audit_expectation: Every triage run and its conclusion (scoped vs. broad impact)
    must be logged in the incident evidence bundle.
prerequisites:
- id: PRE-001
  name: Backend API and PostgreSQL reachable
  required_for: any_check
- id: PRE-002
  name: The incident, deployment or restore action being triaged is already identified
    with a timestamp window
  required_for: scoping_queries_by_time
procedure:
- id: TRIAGE-STEP-001
  name: Identify the tenant(s) directly implicated by the triggering event
  detail: 'From the alert, error report or user report, extract every tenant_id explicitly
    mentioned. If none is known yet, treat the incident as platform-wide until proven
    otherwise.

    '
- id: TRIAGE-STEP-002
  name: Query audit events for the time window across all tenants touched by the affected
    component
  working_directory: repository_root
  command: Invoke-RestMethod "http://localhost:8080/api/audit/events" -Headers @{
    "X-Tenant-Id" = "<tenant-id>" }
  expected_result: Audit events returned are scoped only to the requested tenant_id,
    with no cross-tenant leakage in the response.
- id: TRIAGE-STEP-003
  name: Cross-tenant leakage check (TD-DB-004 compensating control)
  working_directory: 07-implementation
  command: docker exec hop-local-postgres psql -U hop -d hop -c "SELECT tenant_id,
    count(*) FROM identity.user_accounts GROUP BY tenant_id ORDER BY tenant_id;"
  expected_result: 'Row counts per tenant_id match the expected baseline for each
    tenant; no unexpected tenant_id appears in a result set that a tenant-scoped API
    call should never have been able to touch. Repeat the equivalent grouped-count
    query against any other table implicated by the incident.

    '
- id: TRIAGE-STEP-004
  name: Classify impact scope
  outcomes:
  - single_tenant: Only one tenant_id shows an anomaly; proceed with tenant-scoped
      mitigation (feature flag, tenant-specific rollback per rollback-incident-handoff-runbook.md).
  - multi_tenant: More than one but not all tenants affected; escalate severity and
      broaden mitigation scope.
  - platform_wide: All tenants affected, or a cross-tenant leakage anomaly was found
      in TRIAGE-STEP-003; treat as P1 per incident-response-runbook.md severity_classification
      regardless of the originally reported severity.
- id: TRIAGE-STEP-004B
  name: Contain a single_tenant outcome (COM-MOD-012-BE-001 operational controls)
  working_directory: repository_root
  detail: "Once a single tenant is confirmed as the source or sole victim of the impact,\
    \ apply the lightest containment that stops the bleeding without a code deploy:\n\
    \ - Suspend the tenant (blocks further operational commands from that tenant while\
    \ it is\n   investigated): PUT /api/platform/tenants/{tenantId}/status with body\n\
    \   {\"status\":\"SUSPENDED\",\"reason\":\"<incident id and triage summary>\"\
    }. This is a privileged\n   operation and is always recorded to the audit trail\
    \ (BCM-PLT-007, action TenantStatusChanged).\n - Or disable a specific feature/experiment\
    \ for every tenant without suspending anyone: POST\n   /api/platform/feature-flags\
    \ with enabledByDefault=false (or remove the tenant from\n   targetTenants). This\
    \ is also a privileged, audited operation (action FeatureFlagUpdated).\nReactivate\
    \ the tenant (status ACTIVE) once the incident is resolved and the root cause\
    \ is understood; do not leave a tenant suspended silently.\n"
  expected_result: 'GET /api/platform/tenants/{tenantId} reflects the new status immediately;
    the corresponding audit event is retrievable via GET /api/audit/events?tenantId=<tenant-id>.

    '
- id: TRIAGE-STEP-005
  name: Record findings and hand back to the calling runbook
  detail: Return the impact classification to incident-response-runbook.md, restore-runbook.md
    or the deployment procedure that triggered this triage.
success_criteria:
- Impact scope is classified as single_tenant, multi_tenant or platform_wide with
  supporting query evidence.
- No cross-tenant leakage anomaly is found, or one is found and immediately escalated
  to P1.
failure_criteria:
- Triage is skipped for an incident, restore or higher-risk deployment.
- A cross-tenant leakage anomaly is found but not escalated to P1 / not reported.
- Query results cannot be obtained (database or API unreachable) and no fallback triage
  is attempted.
evidence_expected:
- Query commands and their output, impact classification, and the reasoning that led
  to it, timestamped and attached to the incident evidence bundle.
responsible_role: platform_operations_on_call
next_action_if_failed: 'If a cross-tenant leakage anomaly is found, immediately escalate
  to P1 in incident-response-runbook.md, notify the release manager and product
  owner, and treat this as a potential BCM-PLT-007 compliance event requiring its
  own post-incident-review-runbook.md entry even after mitigation. Do not close
  the underlying incident until the anomaly''s root cause is understood.

  '
related_runbooks:
- incident-response-runbook.md
- restore-runbook.md
- rollback-incident-handoff-runbook.md
- evidence-collection-runbook.md
- post-incident-review-runbook.md
known_gaps_and_forward_pointers:
- gap: Cross-tenant leakage check is a manual, ad hoc SQL query per affected table;
    no automated, repository-wide tenant-isolation verification tool exists yet.
  forward_pointer: TD-DB-004 native Row Level Security remains the durable fix; a
    future release-readiness-hardening backlog item.
- gap: No tenant_id MDC context in structured logs yet (see metrics-logs-traces-validation-runbook.md),
    so telemetry cannot yet fast-path this triage; the runbook currently relies on
    audit events and direct database queries.
  status: closed_by_COM_MOD_012_BE_001
  resolution: RequestObservabilityContextFilter now tags every log line with tenantId/userId/traceId;
    platform_operations_on_call can filter logs by tenant directly. TRIAGE-STEP-004B
    also adds a real containment control (tenant suspend/archive, feature-flag disablement)
    that did not exist before.
closure:
  backlog_item: COM-MOD-012-OPS-002
  status: active
```
