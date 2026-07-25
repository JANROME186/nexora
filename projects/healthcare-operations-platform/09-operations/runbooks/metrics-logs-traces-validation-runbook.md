# HOP Metrics, Logs and Traces Validation Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Confirm telemetry *content* quality — not just endpoint reachability — per `BCM-PLT-006`'s
`ServiceTelemetryProfile` and `SloDefinition` models: logs must carry tenant/user/trace context,
traces must propagate W3C `traceparent`, and a metrics catalog must exist for request rate, error
rate, latency and business signals.

## Applicable environment

Partially executable on `local` today (see known gaps below). `dev`/`qa`/`staging`/`prod` share the
target posture from `production-deployment-strategy.md`.

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
3. `docker compose --env-file .env -f compose.local.json logs otel-collector --tail 50` (in
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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUNBOOK-TELEMETRY-001
  type: operational-runbook
  name: Metrics, Logs and Traces Validation Runbook
  version: 1.0.0
  status: active
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: metrics-logs-traces-validation-runbook.md
  machine_readable: metrics-logs-traces-validation-runbook.md
  owner: Nexora Platform Operations
  date: 2026-07-22
purpose: 'Confirm the content quality of HOP telemetry, not just endpoint reachability:
  that logs carry tenant/user/trace context, that trace propagation follows W3C traceparent,
  and that a metrics catalog exists for request rate, error rate, latency and business
  signals, per BCM-PLT-006''s ServiceTelemetryProfile and SloDefinition models.

  '
applicable_environments:
  local: partially_executable_see_known_gaps
  dev: target_posture
  qa: target_posture
  staging: target_posture
  prod: target_posture
capability_traceability:
  BCM-PLT-006:
    responsibility: ServiceTelemetryProfile (tracing_sample_rate, log_level, metrics_path),
      SloDefinition, structured logging invariant.
  BCM-PLT-007:
    responsibility: Distinguishes audit events (security/compliance) from ephemeral
      telemetry logs — do not conflate the two evidence types.
iam_and_audit:
  required_permissions:
  - metrics:read
  minimum_role: platform_operations
  audit_expectation: Reading logs/metrics/traces is not itself audited; exporting
    log content that includes patient-identifiable data must follow the same access-control
    discipline as clinical data access (see BCM-PLT-007 category CLINICAL).
prerequisites:
- id: PRE-001
  name: Backend API running and having served at least one request in this session
  required_for: log_and_trace_content_checks
- id: PRE-002
  name: OTel Collector running
  required_for: trace_pipeline_check
procedure:
- id: TEL-STEP-001
  name: Generate a sample request to produce fresh telemetry
  command: Invoke-RestMethod http://localhost:8080/actuator/health
  working_directory: repository_root
  expected_result: HTTP 200 recorded.
- id: TEL-STEP-002
  name: Inspect backend process console/log output for the request above
  working_directory: repository_root
  expected_result: 'A structured log line exists for the request. Confirm whether
    it currently includes tenant_id, user_id and trace_id fields (see known_gaps_and_forward_pointers
    if it does not).

    '
- id: TEL-STEP-003
  name: Confirm OTel Collector is receiving or forwarding data (pipeline liveness,
    not content)
  command: docker compose --env-file .env -f compose.local.json logs otel-collector
    --tail 50
  working_directory: 07-implementation
  expected_result: Collector process is running with no repeated export errors in
    the tail.
- id: TEL-STEP-004
  name: Confirm audit events are queryable as the compliance-grade evidence source
    (distinct from ephemeral logs)
  command: Invoke-RestMethod "http://localhost:8080/api/audit/events" -Headers @{
    "X-Tenant-Id" = "<tenant-id>" }
  working_directory: repository_root
  expected_result: HTTP 200 with a JSON array of audit events, each carrying event_id,
    actor_id, event_type, timestamp.
success_criteria:
- A request produces a corresponding log line and the pipeline components are alive.
- Audit events remain independently queryable via BCM-PLT-007's /api/audit/events,
  regardless of ephemeral telemetry state.
failure_criteria:
- No log output is produced for a known request.
- OTel Collector reports repeated export errors.
- /api/audit/events is unreachable or returns an error for a valid tenant.
evidence_expected:
- Sample log excerpt (redacted of any secret/PII beyond what policy allows), collector
  log tail, and one audit-events response sample, timestamped and stored per evidence-collection-runbook.md.
responsible_role: platform_operations_on_call
next_action_if_failed: 'If audit events fail: treat as a P1/P2 incident per incident-response-runbook.md
  because audit trail is a BCM-PLT-007 compliance control, not a convenience feature.
  If only ephemeral logs/metrics/traces are degraded: register or update the relevant
  technical-debt item and continue operating, since compliance evidence (audit) is
  unaffected.

  '
related_runbooks:
- observability-runbook.md
- evidence-collection-runbook.md
- incident-response-runbook.md
known_gaps_and_forward_pointers:
- gap: Backend structured logs are not yet confirmed to carry tenant_id/user_id/trace_id
    MDC context on every line (BCM-PLT-006 invariant "all log entries must include
    W3C traceparent context when trace context exists" is modeled but not yet implemented
    in code).
  status: closed_by_COM_MOD_012_BE_001
  resolution: RequestObservabilityContextFilter populates SLF4J MDC (tenantId, userId,
    traceId) for every request ahead of HopAuthorizationInterceptor; logging.pattern.console
    in application.properties renders all three on every log line; traceId reuses an inbound
    W3C traceparent trace-id segment when present and valid, otherwise mints one.
    Verified by RequestObservabilityContextFilterTest.
- gap: No metrics catalog (request rate, error rate, latency quantiles, JVM, DB pool,
    business metrics) is exposed yet; Prometheus registry dependency absent.
  status: closed_by_COM_MOD_012_BE_001
  resolution: micrometer-registry-prometheus dependency added; GET /actuator/prometheus
    exposes the standard Micrometer/JVM/HTTP-server metric catalog (request counts
    and latency histograms, JVM memory/GC/threads, DataSource pool). A curated business-metrics
    catalog beyond the Micrometer defaults remains future scope.
- gap: No SLO/SLA alert definitions are wired to an alerting backend; SloDefinition
    exists only as a modeled value object.
  status: confirmed_still_open_by_COM_MOD_012_QA_001
  forward_pointer: tracked as TD-OBS-001 (needs an actual alerting backend such as
    Alertmanager, which is not yet provisioned per observability-runbook.md).
closure:
  backlog_item: COM-MOD-012-OPS-002
  status: active
```
