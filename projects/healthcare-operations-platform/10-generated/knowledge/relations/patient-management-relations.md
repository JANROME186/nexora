---
id: patient-management-relations
format: markdown_structured_payload
---

# Patient Management Relations

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
relations:
- id: REL-001
  from: CAP-001
  to: DOM-001
  type: depends_on
  description: Patient Management capability is implemented through the Patient Domain.
- id: REL-002
  from: US-001
  to: BR-001
  type: governed_by
  description: Register Patient story must enforce minor guardian rule.
- id: REL-003
  from: US-001
  to: API-001
  type: implemented_by
  description: Patients API exposes operations required by the story.
- id: REL-004
  from: END-001
  to: EVT-001
  type: produces
  description: Create Patient endpoint produces PatientCreated.
- id: REL-005
  from: UI-001
  to: API-001
  type: consumes
  description: Web screen consumes Patients API.
- id: REL-006
  from: MOB-001
  to: API-001
  type: consumes
  description: Mobile screen consumes Patients API.
- id: REL-007
  from: TEST-001
  to: END-001
  type: validates
  description: Contract test validates endpoint behavior.
```
