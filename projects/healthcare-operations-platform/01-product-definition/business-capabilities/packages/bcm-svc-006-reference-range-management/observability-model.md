---
id: HOP-OBS-BCM-SVC-006
format: markdown_structured_payload
type: observability-model
name: Reference Range Management Observability Model
version: 0.1.0
status: modeled
---

# Reference Range Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-SVC-006
  type: observability-model
  name: Reference Range Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-006
  depends_on_capability: BCM-PLT-006
logs:
- event: reference_range_created
  level: info
  fields:
  - rangeId
  - analyteRefId
  - tenantId
  - actorId
- event: reference_range_published
  level: info
  fields:
  - rangeId
  - version
  - effectiveFrom
  - actorId
- event: reference_range_publish_rejected
  level: warn
  fields:
  - rangeId
  - reasonCode
metrics:
- name: catalog_reference_ranges_total
  type: gauge
  labels:
  - tenantId
  - status
- name: catalog_reference_range_publish_failures_total
  type: counter
  labels:
  - tenantId
  - reasonCode
- name: catalog_effective_range_resolution_latency_ms
  type: histogram
traces:
- span: CreateReferenceRange
- span: PublishReferenceRange
  child_spans:
  - ValidateThresholds
  - ValidateSegmentOverlap
  - ValidateAnalytePublication
  - FreezeRangeSnapshot
- span: ResolveEffectiveRange
audit_events:
- ReferenceRangeCreated
- ReferenceRangePublished
- ReferenceRangeRevised
- ReferenceRangeDeprecated
alerts:
- name: HighReferenceRangePublishFailureRate
  condition: catalog_reference_range_publish_failures_total rate exceeds threshold
  severity: warning
- name: EffectiveRangeResolutionMiss
  condition: effective range resolution returns no active version for a validated
    result
  severity: critical
```
