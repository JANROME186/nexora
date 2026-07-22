# HOP Observability Operations Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Single entry point for confirming HOP telemetry (health probes, metrics, logs and traces) before,
during and after any deployment or incident. This runbook links to two detailed procedures:
`health-readiness-liveness-runbook.md` and `metrics-logs-traces-validation-runbook.md`.

## Applicable environment

Executable today against the local Docker Compose stack (`local`). `dev`, `qa`, `staging` and
`prod` postures are the target defined in `production-deployment-strategy.yaml`; their telemetry
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

- Local infrastructure services running (`INF-001` in `local-solution-runbook.yaml`).
- Backend API running (`BE-001` in `local-solution-runbook.yaml`).
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
local/dev, consult `local-solution-runbook.yaml` troubleshooting first.

## Known gaps and forward pointers

- No Prometheus-compatible metrics endpoint is exposed yet (`application.yml` only exposes
  `health,info`; no `micrometer-registry-prometheus` dependency in `backend/pom.xml`). Forward
  pointer: `COM-MOD-012-BE-001`.
- No distributed tracing exporter is wired from the backend to the local OTel Collector yet, even
  though the collector itself is reachable. Forward pointer: `COM-MOD-012-BE-001`.
- No Grafana/Prometheus/Loki-compatible stack is provisioned beyond `local`. Forward pointer: a
  future infrastructure-provisioning backlog item.
