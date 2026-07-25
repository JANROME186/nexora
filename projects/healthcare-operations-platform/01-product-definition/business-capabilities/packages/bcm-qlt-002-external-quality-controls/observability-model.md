---
id: HOP-OBS-BCM-QLT-002
format: markdown_structured_payload
type: observability-model
name: External Quality Controls Observability Model
version: 0.1.0
status: modeled
---

# External Quality Controls Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-QLT-002
  type: observability-model
  name: External Quality Controls Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-002
metrics:
- name: eqc_evaluations_total
  type: counter
  description: Total number of EQA evaluations registered.
  labels:
  - tenant_id
  - provider_name
  - performance_rating
- name: eqc_zscore_distribution
  type: histogram
  description: Distribution of calculated z-scores across EQA survey rounds.
  labels:
  - tenant_id
  - program_code
logs:
- event: ExternalQualityOutOfRangeDetected
  level: WARN
  mdc_fields:
  - tenantId
  - userId
  - traceId
  - evaluationId
  - zScore
  pattern: 'EQA evaluation out-of-range detected: evaluationId={evaluationId}, zScore={zScore}'
tracing:
  propagation_headers:
  - X-Correlation-ID
  - X-Tenant-ID
```
