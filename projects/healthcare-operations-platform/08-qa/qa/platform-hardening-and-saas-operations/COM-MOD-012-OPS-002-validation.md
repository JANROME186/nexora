# COM-MOD-012-OPS-002 QA Evidence

Observability, Backup, Restore and Incident Runbooks

## Scope

Definition/operations-only backlog item. No backend, frontend, mobile or infrastructure code
changed. Built on `09-operations/deployment/production-deployment-strategy.md`.

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

- Docker Compose health checks against `compose.local.json` (postgres, redis, otel-collector).
- Backend actuator health/info against `application.properties`'s
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

Reviewed `08-qa/technical-debt/technical-debt-index.md`. `TD-DB-004` (tenant scoping enforced by
application-level `WHERE` clauses, not native PostgreSQL Row Level Security) is **materially
reduced**: `tenant-impact-triage-runbook.md` adds an executable, mandatory cross-tenant leakage
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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-COM-MOD-012-OPS-002
  type: qa-validation-evidence
  name: Observability, Backup, Restore and Incident Runbooks QA Evidence
  version: 1.0.0
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  status: passed
  date: 2026-07-22
  human_readable: COM-MOD-012-OPS-002-validation.md
  machine_readable: COM-MOD-012-OPS-002-validation.md
scope:
  type: operations_definition
  code_changed: false
  runtime_changed: false
  deployment_assets_changed: false
  production_infrastructure_created: false
  base_artifact: ../../../09-operations/deployment/production-deployment-strategy.md
deliverables:
- 09-operations/runbooks/observability-runbook.md
- 09-operations/runbooks/observability-runbook.md
- 09-operations/runbooks/health-readiness-liveness-runbook.md
- 09-operations/runbooks/health-readiness-liveness-runbook.md
- 09-operations/runbooks/metrics-logs-traces-validation-runbook.md
- 09-operations/runbooks/metrics-logs-traces-validation-runbook.md
- 09-operations/runbooks/backup-runbook.md
- 09-operations/runbooks/backup-runbook.md
- 09-operations/runbooks/restore-runbook.md
- 09-operations/runbooks/restore-runbook.md
- 09-operations/runbooks/incident-response-runbook.md
- 09-operations/runbooks/incident-response-runbook.md
- 09-operations/runbooks/rollback-incident-handoff-runbook.md
- 09-operations/runbooks/rollback-incident-handoff-runbook.md
- 09-operations/runbooks/tenant-impact-triage-runbook.md
- 09-operations/runbooks/tenant-impact-triage-runbook.md
- 09-operations/runbooks/evidence-collection-runbook.md
- 09-operations/runbooks/evidence-collection-runbook.md
- 09-operations/runbooks/post-incident-review-runbook.md
- 09-operations/runbooks/post-incident-review-runbook.md
- 09-operations/runbooks/README.md
acceptance_criteria:
  observability_runbook_defined: passed
  health_readiness_liveness_procedure_defined_and_executable_on_local: passed
  metrics_logs_traces_validation_procedure_defined: passed
  backup_procedure_defined_and_executable_on_local: passed
  restore_procedure_defined_and_executable_on_local: passed
  incident_response_hub_runbook_defined: passed
  rollback_incident_handoff_procedure_defined: passed
  tenant_impact_triage_procedure_defined_and_executable_on_local: passed
  evidence_collection_contract_defined: passed
  post_incident_review_procedure_defined: passed
  ? every_runbook_includes_purpose_prerequisites_applicable_environment_commands_success_failure_criteria_evidence_responsible_role_capability_traceability_iam_audit_next_action_if_failed
  : passed
  capability_package_traceability_updated_for_all_8_com_mod_012_capabilities: passed
  agent_agnostic_alignment: passed
  open_source_first_alignment: passed
  no_proprietary_dependency_introduced_without_adr: passed
executability_verification:
  method: Every command referencing a currently-running local component (docker compose,
    actuator health, OTel Collector health endpoint, /api/audit/events, pg_dump/pg_restore/psql
    against the local PostgreSQL container) was cross-checked against 07-implementation/compose.local.json,
    07-implementation/.env.example, backend/src/main/resources/application.properties and
    the existing AuditComplianceController mapping (/api/audit) rather than assumed.
  confirmed_executable_on_local:
  - Docker Compose service health checks (postgres, redis, otel-collector) against
    compose.local.json.
  - Backend actuator health/info against application.properties management.endpoints.web.exposure.include=health,info.
  - OTel Collector health extension on port 13133 (HOP_OTEL_HEALTH_PORT default).
  - GET /api/audit/events against AuditComplianceController @RequestMapping("/api/audit").
  - pg_dump/pg_restore/psql/createdb/dropdb against container hop-local-postgres with
    HOP_POSTGRES_USER/HOP_POSTGRES_DB from .env.example.
  explicitly_documented_as_not_yet_executable:
  - Prometheus metrics scrape endpoint (no micrometer-registry-prometheus dependency
    in backend/pom.xml; management.endpoints.web.exposure.include is health,info only).
  - Distributed trace export from backend to OTel Collector (collector is reachable;
    no application-side exporter wired).
  - Kubernetes-compatible dev/qa/staging/prod observability, backup and rollback infrastructure
    (target posture only, per production-deployment-strategy.md target_runtime_topology).
  disposition: Each gap is recorded verbatim in its runbook's known_gaps_and_forward_pointers
    section with a forward pointer to COM-MOD-012-BE-001 or a named future backlog
    item, per the rule against silently marking an unexecutable step as passed.
technical_debt:
  reviewed_index: 08-qa/technical-debt/technical-debt-index.md
  debt_first_action:
    item: TD-DB-004
    disposition: materially_reduced
    reason: 'tenant-impact-triage-runbook.md adds an executable, mandatory cross-tenant
      leakage check (grouped tenant_id row counts against a known-good baseline) as
      an operational defense-in-depth compensating control, run during every incident,
      restore and higher-risk deployment, escalating any anomaly to P1. TD-DB-004''s
      own acceptance criteria (native PostgreSQL Row Level Security) remain open;
      its blocking dependency TD-IAM-001 is already closed, so RLS itself can now
      be scheduled as a future backend code-changing backlog item. No other open technical-debt
      item was a closer match to this backlog item''s observability/backup/restore/incident
      scope.

      '
coverage_preservation:
  backend_java_maven: 83.99% (floor preserved; not remeasured because no backend code
    changed)
  employee_portal_typescript_web: 88.68% (floor preserved; not remeasured because
    no frontend code changed)
  public_website_typescript_web: 98.61% (floor preserved; not remeasured because no
    public website code changed)
  mobile_typescript_foundation: 99.21% (floor preserved; not remeasured because no
    mobile code changed)
  patient_portal_typescript_web: 94.11% (floor preserved; not remeasured because no
    patient portal code changed)
  doctor_portal_typescript_web: 96.28% (floor preserved; not remeasured because no
    doctor portal code changed)
validation_commands:
  yaml_parse: passed
  stale_pointer_sweep: passed
  agent_agnostic_scan: passed
  secret_scan: passed
  git_diff_check: passed
  stack_tests: not_applicable_no_code_changed
  vulnerability_scans: not_applicable_no_code_or_dependency_change
  dast: not_applicable_no_runnable_surface_changed
closure:
  backlog_item_status: closed
  next_backlog_item: COM-MOD-012-BE-001
  ready_for_next_backlog_item: COM-MOD-012-BE-001
```
