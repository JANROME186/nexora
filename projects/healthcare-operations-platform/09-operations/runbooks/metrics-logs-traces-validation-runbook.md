# HOP Metrics, Logs and Traces Validation Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Confirm telemetry *content* quality — not just endpoint reachability — per `BCM-PLT-006`'s
`ServiceTelemetryProfile` and `SloDefinition` models: logs must carry tenant/user/trace context,
traces must propagate W3C `traceparent`, and a metrics catalog must exist for request rate, error
rate, latency and business signals.

## Applicable environment

Partially executable on `local` today (see known gaps below). `dev`/`qa`/`staging`/`prod` share the
target posture from `production-deployment-strategy.yaml`.

## Capability traceability

- `BCM-PLT-006` — `ServiceTelemetryProfile`, `SloDefinition`, structured-logging invariant.
- `BCM-PLT-007` — keeps audit events (compliance-grade evidence) distinct from ephemeral telemetry.

## IAM and audit

Requires `metrics:read`. Reading telemetry is not itself audited; exporting log content containing
patient-identifiable data must follow the same access discipline as clinical data access.

## Prerequisites

- Backend API running and having served at least one request this session.
- OTel Collector running.

## Procedure

1. `Invoke-RestMethod http://localhost:8080/actuator/health` to generate a fresh sample request.
2. Inspect the backend console output for a structured log line for that request; note whether
   `tenant_id`, `user_id` and `trace_id` are present.
3. `docker compose --env-file .env -f compose.local.yml logs otel-collector --tail 50` (in
   `07-implementation`) — collector alive, no repeated export errors.
4. `Invoke-RestMethod "http://localhost:8080/api/audit/events" -Headers @{ "X-Tenant-Id" = "<tenant-id>" }`
   — HTTP 200 with a JSON array of audit events (`event_id`, `actor_id`, `event_type`, `timestamp`).

## Success criteria

A request produces a log line, the collector pipeline is alive, and `/api/audit/events` stays
independently queryable regardless of ephemeral telemetry state.

## Failure criteria

No log output for a known request, repeated collector export errors, or `/api/audit/events`
unreachable/erroring for a valid tenant.

## Evidence expected

Redacted log excerpt, collector log tail, and one audit-events response sample, timestamped and
stored per `evidence-collection-runbook.md`.

## Responsible role

Platform operations on-call.

## If this fails

Audit-events failure: treat as a P1/P2 incident (`BCM-PLT-007` is a compliance control, not a
convenience). Ephemeral telemetry degradation only: register/update the relevant technical-debt
item and continue operating.

## Known gaps and forward pointers

- Backend structured logs are not yet confirmed to carry `tenant_id`/`user_id`/`trace_id` MDC
  context on every line. Forward pointer: `COM-MOD-012-BE-001`.
- No metrics catalog is exposed yet (no Prometheus registry dependency). Forward pointer:
  `COM-MOD-012-BE-001`.
- No SLO/SLA alerting backend is wired; `SloDefinition` exists only as a modeled value object.
  Forward pointer: `COM-MOD-012-BE-001` plus a future alerting-infrastructure backlog item.
