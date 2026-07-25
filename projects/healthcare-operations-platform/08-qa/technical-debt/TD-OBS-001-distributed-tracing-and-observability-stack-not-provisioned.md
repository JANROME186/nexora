---
id: TD-OBS-001
format: markdown_structured_payload
type: technical-debt-item
name: Distributed trace export, provisioned Grafana/Prometheus/Loki stack and SLO/SLA
  alerting backend not implemented
version: 1.0.0
status: open
---

# Distributed Trace Export, Provisioned Grafana/Prometheus/Loki Stack And Slo/Sla Alerting Backend Not Implemented

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-OBS-001
  type: technical-debt-item
  name: Distributed trace export, provisioned Grafana/Prometheus/Loki stack and SLO/SLA
    alerting backend not implemented
  version: 1.0.0
  status: open
  created_date: 2026-07-23
source:
  discovered_during_backlog_item: COM-MOD-012-QA-001
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  evidence: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-QA-001-validation.md
classification:
  category: infrastructure_not_provisioned
  affected_area: distributed_tracing_metrics_stack_and_alerting
  affected_components:
  - 07-implementation/backend/pom.xml
  - 07-implementation/compose.local.json
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: Audit events (BCM-PLT-007), MDC-correlated structured logs
    and the Prometheus scrape endpoint (all compiled by COM-MOD-012-BE-001) remain
    the compliance-grade and operational evidence sources today; no runbook or module
    depends on span export, a provisioned dashboard stack or automated alerting to
    function. metrics-logs-traces-validation-runbook.md and observability-runbook.md
    both already document this as an explicit forward pointer rather than a silently-passed
    gate.
current_state:
  issue: 'Three related gaps remain from COM-MOD-012-BE-001''s known_gaps_and_forward_pointers,
    confirmed still open during COM-MOD-012-QA-001''s runtime validation: (1) the
    local OTel Collector container (hop-local-otel-collector, provisioned since PF-OPS-001)
    is running and reachable on 4317/4318/13133, but the backend has no OpenTelemetry/micrometer-tracing
    dependency (verified: no tracing/opentelemetry/otlp/zipkin reference anywhere
    in backend/pom.xml or application*.yml) and therefore emits no application spans
    -- the collector receives nothing; (2) no Grafana/Prometheus/Loki-compatible stack
    is provisioned in any environment, only the Prometheus-format /actuator/prometheus
    scrape endpoint itself exists (compiled by COM-MOD-012-BE-001); (3) SloDefinition
    exists only as a modeled value object with no alerting backend (e.g. Alertmanager)
    to evaluate it against.'
  verification_method: grep -in "tracing|opentelemetry|otlp|zipkin" across backend/pom.xml
    and backend/src/main/resources/application*.yml returned no matches; docker compose
    ps confirmed the otel-collector container is Up and healthy with no export traffic
    configured to reach it.
target_state:
  preferred_remediation: 'A future dedicated observability-infrastructure backlog
    item should: add micrometer-tracing-bridge-otel plus an OTLP exporter dependency
    and management.tracing.sampling.probability/management.otlp.tracing.endpoint configuration
    pointed at the already-running local OTel Collector (closing gap 1 first, since
    the receiving infrastructure already exists locally); provision an open-source
    Grafana + Prometheus + Loki stack (docker compose service additions, matching
    the project''s open-source-first policy) for at least the local/dev environments
    (closing gap 2); and adopt Prometheus Alertmanager (or an equivalent open-source
    alerting backend) wired to SloDefinition thresholds (closing gap 3).'
  quality_goal: Do not wire trace/metrics export to infrastructure that is not actually
    running; provision the receiving stack (or confirm it is already running, as with
    the OTel Collector) before adding the corresponding exporter dependency and configuration.
remediation:
  strategy: gradual_dedicated_observability_infrastructure_backlog_item
  owner: platform_operations_team
  estimated_effort: large
  estimated_cost_impact: medium
  target_backlog: a_future_dedicated_observability_infrastructure_backlog_item
  dependencies_or_prerequisites:
  - Decide whether Grafana/Prometheus/Loki is provisioned per-environment via docker
    compose (local/dev) or a managed equivalent (staging/prod), consistent with 09-operations/deployment/production-deployment-strategy.md's
    environment path.
  acceptance_criteria:
  - Backend emits real OTLP spans received by the OTel Collector (verifiable via collector
    logs or a configured backend, not just an idle "no export errors" log tail).
  - A Grafana dashboard (or equivalent open-source stack) can query real Prometheus
    metrics from GET /actuator/prometheus in at least one environment.
  - At least one SloDefinition threshold triggers a real alert through an open-source
    alerting backend.
runbook_references:
- 09-operations/runbooks/observability-runbook.md
- 09-operations/runbooks/metrics-logs-traces-validation-runbook.md
```
