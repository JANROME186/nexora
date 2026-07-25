# DOM-001 Patient Domain

The Patient Domain protects patient identity, demographic information, guardianship, contact information and patient lifecycle events.

It must not contain billing, order processing or result validation logic.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: DOM-001
type: domain
name: Patient Domain
status: draft
version: 0.15.0
owner: Architecture
boundedContext: patient
responsibilities:
- patient identity
- demographic data
- guardian data
- patient lifecycle events
exclusions:
- billing
- laboratory result validation
- inventory
relations:
- type: realizes
  target: CAP-001
- type: ownsEntity
  target: ENT-001
- type: emits
  target: EVT-001
```
