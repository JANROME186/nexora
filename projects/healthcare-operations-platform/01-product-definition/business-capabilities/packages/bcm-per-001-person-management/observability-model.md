---
id: HOP-OBS-BCM-PER-001
format: markdown_structured_payload
type: observability-model
name: Person Management Observability Model
version: 0.1.0
status: modeled
---

# Person Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-PER-001
  type: observability-model
  name: Person Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-001
  depends_on_capability: BCM-PLT-006
logs:
- event: person_duplicate_detection_requested
  level: info
  fields:
  - detectionId
  - tenantId
  - actorId
  - candidateCount
- event: person_search_index_rebuilt
  level: info
  fields:
  - tenantId
  - actorId
  - fromEventOffset
  - toEventOffset
- event: person_merge_coordination_completed
  level: info
  fields:
  - coordinationId
  - tenantId
  - actorId
- event: person_natural_key_normalization_failed
  level: warn
  fields:
  - tenantId
  - reasonCode
metrics:
- name: person_duplicate_detection_total
  type: counter
  labels:
  - tenantId
  - hasCandidates
- name: person_duplicate_detection_top_confidence
  type: histogram
  labels:
  - tenantId
- name: person_search_index_projection_lag_seconds
  type: histogram
  labels:
  - tenantId
traces:
- span: DetectPersonDuplicates
  child_spans:
  - NormalizeNaturalKey
  - QueryPersonSearchIndex
  - ScoreCandidates
- span: RebuildPersonSearchIndex
  child_spans:
  - ReplayProjection
  - VerifyIdempotence
audit_events:
- PersonDuplicateDetectionRequested
- PersonSearchIndexRebuilt
- PersonMergeCoordinationCompleted
alerts:
- name: PersonSearchIndexLagHigh
  condition: person_search_index_projection_lag_seconds p95 exceeds threshold
  severity: warning
- name: PersonNaturalKeyNormalizationFailures
  condition: person_natural_key_normalization_failed rate exceeds threshold
  severity: warning
```
