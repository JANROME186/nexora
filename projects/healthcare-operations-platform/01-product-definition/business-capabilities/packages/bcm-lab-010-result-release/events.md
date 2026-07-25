---
id: HOP-EVT-BCM-LAB-010
format: markdown_structured_payload
type: events
name: Result Release Events
version: 0.1.0
status: modeled
---

# Result Release Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-LAB-010
  type: events
  name: Result Release Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-010
domain_events:
- name: ResultReleased
  description: A medically validated result was released and became eligible for report
    generation and delivery.
  payload:
  - resultId
  - orderId
  - actorId
  - releasedAt
  audit: true
- name: ResultAmended
  description: A released result was corrected through an explicit amendment.
  payload:
  - resultId
  - actorId
  - amendmentReason
  - amendedAt
  audit: true
integration_events:
  published:
  - name: ResultReleased
    description: Signals report generation and digital delivery (MVP-MOD-007) that
      the result is ready to publish.
    consumers:
    - laboratory-results
  - name: ResultAmended
    description: Signals report generation and digital delivery that a previously
      delivered result was corrected.
    consumers:
    - laboratory-results
  consumed:
  - name: ResultMedicallyValidated
    source: BCM-LAB-009
published_language:
- ResultReleased
- ResultAmended
```
