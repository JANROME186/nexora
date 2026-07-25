---
id: HOP-EVT-BCM-LAB-009
format: markdown_structured_payload
type: events
name: Medical Validation Events
version: 0.1.0
status: modeled
---

# Medical Validation Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-LAB-009
  type: events
  name: Medical Validation Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-009
domain_events:
- name: ResultMedicallyValidated
  description: A technically validated result passed clinical/medical review by a
    licensed authority.
  payload:
  - resultId
  - actorId
  - validatedAt
  audit: true
integration_events:
  published:
  - name: ResultMedicallyValidated
    description: Signals result release that the result is eligible for release.
    consumers:
    - laboratory-results
  consumed:
  - name: ResultTechnicallyValidated
    source: BCM-LAB-008
  - name: DoctorCredentialVerified
    source: BCM-PER-003
published_language:
- ResultMedicallyValidated
```
