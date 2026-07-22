# COM-MOD-012-OPS-002 QA Evidence

Observability, Backup, Restore and Incident Runbooks

## Scope

Definition/operations-only backlog item. No backend, frontend, mobile or infrastructure code
changed. Built on `09-operations/deployment/production-deployment-strategy.yaml`.

## Deliverables

Ten runbook pairs (YAML + Markdown) plus an index README, all under
`09-operations/runbooks/`: observability, health/readiness/liveness, metrics/logs/traces
validation, backup, restore, incident response, rollback incident handoff, tenant-impact triage,
evidence collection, and post-incident review.

## Acceptance criteria

All ten runbooks are defined with purpose, prerequisites, applicable environment, executable
commands, success/failure criteria, expected evidence, responsible role, capability-package
traceability, IAM/audit expectations and a documented next action if the runbook fails. Capability
traceability was updated for all 8 COM-MOD-012 capabilities (`BCM-ORG-001`, `BCM-PLT-001/002/005/
006/007/008/009`). The work stays agent-agnostic and open-source-first; no proprietary dependency
was introduced without an ADR (pgBackRest is recommended, not adopted, for future production
backup).

## Executability verification

Every command referencing a currently-running local component was cross-checked against real
repository state rather than assumed:

- Docker Compose health checks against `compose.local.yml` (postgres, redis, otel-collector).
- Backend actuator health/info against `application.yml`'s
  `management.endpoints.web.exposure.include=health,info`.
- OTel Collector health extension on port 13133.
- `GET /api/audit/events` against the real `AuditComplianceController` mapping.
- `pg_dump`/`pg_restore`/`psql`/`createdb`/`dropdb` against the `hop-local-postgres` container with
  the real `.env.example` credentials.

Gaps found during this cross-check — no Prometheus metrics endpoint, no trace exporter wired, no
shared-environment infrastructure yet — are recorded verbatim in each affected runbook's
`known_gaps_and_forward_pointers` section with a forward pointer to `COM-MOD-012-BE-001` or a named
future backlog item, rather than silently marked as passed.

## Technical debt

Reviewed `08-qa/technical-debt/technical-debt-index.yaml`. `TD-DB-004` (tenant scoping enforced by
application-level `WHERE` clauses, not native PostgreSQL Row Level Security) is **materially
reduced**: `tenant-impact-triage-runbook.yaml` adds an executable, mandatory cross-tenant leakage
check as an operational compensating control, run during every incident, restore and higher-risk
deployment. `TD-DB-004`'s own acceptance criteria (native RLS) remain open; its blocking dependency
`TD-IAM-001` is already closed, so RLS can now be scheduled as a future backend backlog item.

## Coverage preservation

No stack's code changed, so all six coverage floors are preserved unchanged: backend 83.99%,
employee portal 88.68%, public website 98.61%, mobile 99.21%, patient portal 94.11%, doctor portal
96.28%.

## Validation commands

YAML parse, stale-pointer sweep, agent-agnostic scan, secret scan and `git diff --check` all
passed. Stack tests, vulnerability scans and DAST are not applicable — no code or dependency
changed.

## Closure

Backlog item closed. Next backlog item: `COM-MOD-012-BE-001`.
