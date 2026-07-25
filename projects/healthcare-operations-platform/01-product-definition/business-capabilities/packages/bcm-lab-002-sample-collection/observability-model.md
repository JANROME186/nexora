---
id: HOP-OBS-BCM-LAB-002
format: markdown_structured_payload
type: observability-model
name: Sample Collection Observability Model
version: 0.1.0
status: modeled
---

# Sample Collection Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-LAB-002
  type: observability-model
  name: Sample Collection Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-002
  depends_on_capability: BCM-PLT-006
logs:
- event: sample_collected
  level: info
  fields:
  - sampleId
  - orderId
  - branchId
  - actorId
  - collectionMethod
  correlation_id: orderId
- event: sample_rejected_at_collection
  level: warn
  fields:
  - sampleId
  - orderId
  - actorId
  - reasonCode
  correlation_id: orderId
metrics:
- name: sample_collected_total
  type: counter
  labels:
  - tenantId
  - branchId
  - collectionMethod
- name: sample_rejected_at_collection_total
  type: counter
  labels:
  - tenantId
  - branchId
  - reasonCode
- name: sample_collection_duration_ms
  type: histogram
  labels:
  - tenantId
  - branchId
traces:
- span: CollectSample
  child_spans:
  - CapturePatientIdentitySnapshot
  - CaptureSampleRequirementSnapshot
  - CaptureCollectionData
  - AppendChainOfCustodyEvent
- span: RejectSampleAtCollection
  child_spans:
  - CaptureRejectionReason
  - AppendChainOfCustodyEvent
audit_events:
- SampleCollected
- SampleRejected
alerts:
- name: HighSampleRejectionRateAtCollection
  condition: sample_rejected_at_collection_total rate exceeds threshold
  severity: warning
```
