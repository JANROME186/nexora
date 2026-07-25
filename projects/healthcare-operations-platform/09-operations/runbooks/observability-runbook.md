# HOP Observability Operations Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Single entry point for confirming HOP telemetry (health probes, metrics, logs and traces) before,
during and after any deployment or incident. This runbook links to two detailed procedures:
`health-readiness-liveness-runbook.md` and `metrics-logs-traces-validation-runbook.md`.

## Applicable environment

Executable today against the local Docker Compose stack (`local`). `dev`, `qa`, `staging` and
`prod` postures are the target defined in `production-deployment-strategy.md`; their telemetry
infrastructure is not yet provisioned.

## Capability traceability

- `BCM-PLT-006` Observability — primary owner (health probes, metrics catalog, tracing, structured logging).
- `BCM-PLT-002` Platform Configuration — feature-flag-gated maintenance mode read during triage.
- `BCM-PLT-009` Workflow Engine — the operational workflow this runbook executes under.
- `BCM-PLT-007` Audit Trail — cross-check for privileged observability configuration changes.

## IAM and audit

Requires `metrics:read` and `health:read` at minimum, held by the platform operations or release
manager role. Read-only checks in this runbook do not require an audit event; any privileged change
to alert thresholds or dashboards does, via `BCM-PLT-007`.

## Prerequisites

- Local infrastructure services running (`INF-001` in `local-solution-runbook.md`).
- Backend API running (`BE-001` in `local-solution-runbook.md`).
- PowerShell or a compatible shell.

## Procedure

1. `Invoke-WebRequest http://localhost:13133` — OTel Collector health extension returns HTTP 200.
2. `Invoke-RestMethod http://localhost:8080/actuator/health` — status is `UP`.
3. `Invoke-RestMethod http://localhost:8080/actuator/info` — HTTP 200.
4. Run `health-readiness-liveness-runbook.md` for probe-level detail.
5. Run `metrics-logs-traces-validation-runbook.md` for telemetry-content detail.

## Success criteria

Collector and backend health checks return healthy status with no unexplained telemetry gap for
the environment's target posture.

## Failure criteria

Any health endpoint is unreachable or non-UP, or a deployed service exposes no liveness/readiness
endpoint at all (violates the `BCM-PLT-006` invariant that every deployed service must expose one).

## Evidence expected

Captured HTTP status and body per check with a UTC timestamp, linked to the deployment or incident
id, stored under `08-qa/qa/platform-hardening-and-saas-operations/` or the active incident's
evidence folder.

## Responsible role

Platform operations on-call.

## If this fails

Escalate immediately to `incident-response-runbook.md` on shared or production environments. On
local/dev, consult `local-solution-runbook.md` troubleshooting first.

## Known gaps and forward pointers

- No Prometheus-compatible metrics endpoint is exposed yet (`application.properties` only exposes
  `health,info`; no `micrometer-registry-prometheus` dependency in `backend/pom.xml`). Forward
  pointer: `COM-MOD-012-BE-001`.
- No distributed tracing exporter is wired from the backend to the local OTel Collector yet, even
  though the collector itself is reachable. Forward pointer: `COM-MOD-012-BE-001`.
- No Grafana/Prometheus/Loki-compatible stack is provisioned beyond `local`. Forward pointer: a
  future infrastructure-provisioning backlog item.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUNBOOK-OBS-001
  type: operational-runbook
  name: Observability Operations Runbook
  version: 1.0.0
  status: active
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: observability-runbook.md
  machine_readable: observability-runbook.md
  owner: Nexora Platform Operations
  date: 2026-07-22
purpose: 'Give an operator a single, executable entry point to confirm HOP telemetry
  (health probes, metrics, logs and traces) is present, correctly scoped and usable
  before, during and after any deployment or incident. This runbook is the parent
  of health-readiness-liveness-runbook.md and metrics-logs-traces-validation-runbook.md,
  which detail the two most frequently repeated validation procedures referenced from
  here.

  '
applicable_environments:
  local:
    posture: docker_compose_otel_collector_plus_actuator_health_info
    notes: Executable today against compose.local.json.
  dev:
    posture: shared_otel_collector_prometheus_grafana_loki_compatible_stack
    notes: Target posture per production-deployment-strategy.md; infrastructure
      not yet provisioned.
  qa:
    posture: full_telemetry_and_alert_dry_runs
    notes: Target posture; infrastructure not yet provisioned.
  staging:
    posture: production_equivalent_dashboards_alerts_slo_checks
    notes: Target posture; infrastructure not yet provisioned.
  prod:
    posture: required_metrics_logs_traces_slo_alerts_incident_notifications
    notes: Target posture; infrastructure not yet provisioned.
capability_traceability:
  BCM-PLT-006:
    responsibility: Primary owner. ObservabilityTarget aggregate, health probes, metrics
      catalog, tracing, structured logging.
    package: ../../01-product-definition/business-capabilities/packages/bcm-plt-006-observability/
  BCM-PLT-002:
    responsibility: Feature-flag-gated maintenance mode and environment configuration
      read during triage.
  BCM-PLT-009:
    responsibility: Operational workflow that this runbook is executed under (deployment/incident/rollback).
  BCM-PLT-007:
    responsibility: Audit trail cross-check for privileged observability actions (dashboard/alert
      changes).
iam_and_audit:
  required_permissions:
  - metrics:read
  - health:read
  minimum_role: platform_operations_or_release_manager
  audit_expectation: 'Any change to alert thresholds, dashboards or telemetry configuration
    must be traceable to an actor via BCM-PLT-007. Read-only observability checks
    in this runbook do not themselves require an audit event; privileged configuration
    changes do.

    '
prerequisites:
- id: PRE-001
  name: Local infrastructure services running (INF-001 in local-solution-runbook.md)
  required_for: otel_collector_health_check
- id: PRE-002
  name: Backend API running (BE-001 in local-solution-runbook.md)
  required_for: actuator_health_and_info_checks
- id: PRE-003
  name: PowerShell or compatible shell
  required_for: local_commands_on_windows
procedure:
- id: OBS-STEP-001
  name: Confirm OpenTelemetry Collector is healthy
  working_directory: 07-implementation
  command: Invoke-WebRequest http://localhost:13133
  expected_result: HTTP 200 from the collector health extension.
- id: OBS-STEP-002
  name: Confirm backend actuator health
  working_directory: repository_root
  command: Invoke-RestMethod http://localhost:8080/actuator/health
  expected_result: status is UP.
- id: OBS-STEP-003
  name: Confirm backend build/version info is resolvable
  working_directory: repository_root
  command: Invoke-RestMethod http://localhost:8080/actuator/info
  expected_result: HTTP 200 (payload may be minimal until build-info is wired).
- id: OBS-STEP-004
  name: Run the detailed health/readiness/liveness procedure
  reference: health-readiness-liveness-runbook.md
- id: OBS-STEP-005
  name: Run the detailed metrics/logs/traces validation procedure
  reference: metrics-logs-traces-validation-runbook.md
success_criteria:
- OTel Collector health extension returns HTTP 200.
- Backend actuator health returns status UP.
- No unexplained gap exists between expected and observed telemetry sources for the
  environment's target posture.
failure_criteria:
- OTel Collector or backend health endpoint is unreachable or returns a non-UP status.
- A deployed service exposes no /actuator/health equivalent (violates BCM-PLT-006
  invariant "every deployed service must expose liveness and readiness endpoints").
evidence_expected:
- Captured HTTP status and JSON body of each check, with UTC timestamp.
- Reference to the deployment or incident id this check was run for.
- Stored under 08-qa/qa/platform-hardening-and-saas-operations/ or the active incident's
  evidence folder (see evidence-collection-runbook.md).
responsible_role: platform_operations_on_call
next_action_if_failed: 'Escalate to incident-response-runbook.md immediately if
  the failure is on a shared or production environment. On local/dev, consult troubleshooting
  in local-solution-runbook.md first; if unresolved, register a technical-debt or
  framework-feedback item rather than silently skipping the check.

  '
related_runbooks:
- health-readiness-liveness-runbook.md
- metrics-logs-traces-validation-runbook.md
- incident-response-runbook.md
- evidence-collection-runbook.md
known_gaps_and_forward_pointers:
- gap: No Prometheus-compatible metrics endpoint is exposed yet (management.endpoints.web.exposure.include
    is health,info only in application.properties; no micrometer-registry-prometheus dependency
    in backend/pom.xml).
  status: closed_by_COM_MOD_012_BE_001
  resolution: micrometer-registry-prometheus added to backend/pom.xml; management.endpoints.web.exposure.include
    now includes prometheus; GET /actuator/prometheus verified reachable by ObservabilityEndpointsWebTest.
- gap: No distributed tracing exporter is wired from the backend to the local OTel
    Collector yet; the collector runs and is reachable, but no application spans are
    produced.
  status: confirmed_still_open_by_COM_MOD_012_QA_001
  forward_pointer: tracked as TD-OBS-001, owned by a future dedicated observability-infrastructure
    backlog item; COM-MOD-012-BE-001 only wired trace-id propagation into log MDC
    (see metrics-logs-traces-validation-runbook.md), not span export.
- gap: No Grafana/Prometheus/Loki-compatible stack is provisioned in dev/qa/staging/prod;
    only the environment_configuration_policy target posture is defined in production-deployment-strategy.md.
  status: confirmed_still_open_by_COM_MOD_012_QA_001
  forward_pointer: tracked as TD-OBS-001, owned by a future dedicated observability-infrastructure
    backlog item.
closure:
  backlog_item: COM-MOD-012-OPS-002
  status: active
```
