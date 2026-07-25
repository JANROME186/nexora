---
id: HOP-EVT-BCM-LAB-002
format: markdown_structured_payload
type: events
name: Sample Collection Events
version: 0.1.0
status: modeled
---

# Sample Collection Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-LAB-002
  type: events
  name: Sample Collection Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-002
domain_events:
- name: SampleCollected
  description: A sample was collected and the Sample aggregate created with its first
    chain-of-custody event.
  payload:
  - sampleId
  - orderId
  - orderLineId
  - tenantId
  - branchId
  - actorId
  - collectionMethod
  - collectedAt
  audit: true
- name: SampleRejected
  description: A sample was rejected, either at collection or (per BCM-LAB-005) at
    reception.
  payload:
  - sampleId
  - orderId
  - rejectionStage
  - reasonCode
  - actorId
  - rejectedAt
  audit: true
integration_events:
  published:
  - name: SampleCollected
    description: Signals labeling and reception that a sample exists and requires
      labeling and transport.
    consumers:
    - orders-samples
    - laboratory-results
  - name: SampleRejected
    description: Signals order management that a required sample cannot proceed.
    consumers:
    - orders-samples
  consumed:
  - name: OrderAccepted
    source: BCM-LAB-001
  - name: SampleRequirementPublished
    source: BCM-SVC-007
published_language:
- SampleCollected
- SampleRejected
```
