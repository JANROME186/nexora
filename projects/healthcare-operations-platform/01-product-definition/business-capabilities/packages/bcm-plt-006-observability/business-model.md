---
id: HOP-BUS-MOD-BCM-PLT-006
format: markdown_structured_payload
type: business-model
name: Observability Business Model
version: 1.0.0
---

# Observability Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BUS-MOD-BCM-PLT-006
  type: business-model
  name: Observability Business Model
  version: 1.0.0
aggregate:
  name: ObservabilityTarget
  bounded_context: platform-operations
  root_entity: ServiceTelemetryProfile
entities:
- name: ServiceTelemetryProfile
  type: root_entity
  fields:
    service_id: UUID
    service_name: String
    health_endpoint: String
    metrics_path: String
    tracing_sample_rate: Double (0.0 - 1.0)
    log_level: Enum [DEBUG, INFO, WARN, ERROR]
- name: SloDefinition
  type: value_object
  fields:
    name: String
    metric_name: String
    target_threshold_percent: Double (e.g. 99.9)
    window_duration: String (e.g. "30d")
    alert_severity: Enum [P1_CRITICAL, P2_HIGH, P3_WARNING]
- name: HealthCheckProbe
  type: value_object
  fields:
    probe_type: Enum [LIVENESS, READINESS, STARTUP]
    check_interval_seconds: Integer
    failure_threshold: Integer
invariants:
- Every deployed service must expose /health/liveness and /health/readiness endpoints.
- All log entries must include W3C traceparent context when trace context exists.
```
