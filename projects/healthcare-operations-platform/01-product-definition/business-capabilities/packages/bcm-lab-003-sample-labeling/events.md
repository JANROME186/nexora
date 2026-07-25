---
id: HOP-EVT-BCM-LAB-003
format: markdown_structured_payload
type: events
name: Sample Labeling Events
version: 0.1.0
status: modeled
---

# Sample Labeling Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-LAB-003
  type: events
  name: Sample Labeling Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-003
domain_events:
- name: SpecimenLabelPrinted
  description: A specimen barcode label was generated and queued for printing.
  payload:
  - printJobId
  - sampleId
  - actorId
  - printAttempts
  audit: true
- name: SpecimenLabelAssigned
  description: A confirmed, matched label was assigned to the Sample aggregate.
  payload:
  - sampleId
  - printJobId
  - actorId
  - barcodeValue
  - confirmedAt
  audit: true
- name: SpecimenLabelReprinted
  description: A label was reprinted after damage, misprint or mismatch.
  payload:
  - printJobId
  - sampleId
  - reasonCode
  - actorId
  audit: true
integration_events:
  published:
  - name: SpecimenLabelAssigned
    description: Signals sample reception that the sample is now traceably identified.
    consumers:
    - orders-samples
  consumed:
  - name: SampleCollected
    source: BCM-LAB-002
published_language:
- SpecimenLabelAssigned
```
