---
id: HOP-OBS-BCM-LAB-005
format: markdown_structured_payload
type: observability-model
name: Sample Reception Observability Model
version: 0.1.0
status: modeled
---

# Sample Reception Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-LAB-005
  type: observability-model
  name: Sample Reception Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-005
  depends_on_capability: BCM-PLT-006
logs:
- event: sample_received
  level: info
  fields:
  - sampleId
  - orderId
  - branchId
  - actorId
  correlation_id: orderId
- event: sample_rejected_at_reception
  level: warn
  fields:
  - sampleId
  - orderId
  - actorId
  - reasonCode
  correlation_id: orderId
- event: sample_disposed
  level: info
  fields:
  - sampleId
  - disposalReason
  - actorId
  correlation_id: sampleId
metrics:
- name: sample_received_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: sample_rejected_at_reception_total
  type: counter
  labels:
  - tenantId
  - branchId
  - reasonCode
- name: sample_disposed_total
  type: counter
  labels:
  - tenantId
  - branchId
  - disposalReason
- name: sample_reception_turnaround_ms
  type: histogram
  labels:
  - tenantId
  - branchId
traces:
- span: ReceiveSampleAtLaboratory
  child_spans:
  - RunReceptionConditionCheck
  - RecordSampleReceptionRecord
  - AppendChainOfCustodyEvent
- span: RejectSampleAtReception
  child_spans:
  - CaptureRejectionReason
  - AppendChainOfCustodyEvent
- span: DisposeSample
  child_spans:
  - ValidateTerminalState
  - RecordDisposalRecord
audit_events:
- SampleReceived
- SampleRejected
- SampleDisposed
alerts:
- name: HighSampleRejectionRateAtReception
  condition: sample_rejected_at_reception_total rate exceeds threshold
  severity: warning
- name: SampleReceptionOverdue
  condition: sample awaiting reception exceeds expectedArrivalWindowMinutes
  severity: warning
```
