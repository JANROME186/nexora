---
id: US-001
format: markdown_structured_payload
type: user_story
name: Register Patient
version: 0.13.0
status: draft
---

# Register Patient

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: US-001
name: Register Patient
type: user_story
status: draft
version: 0.13.0
owner: Product Management
source_path: knowledge/nodes/stories/US-001-register-patient.md
capability: CAP-001
domain: DOM-001
persona: Receptionist
story: 'As a receptionist, I want to register a new patient with demographic, contact
  and identification data so that the patient can be associated with orders, samples
  and results.

  '
acceptance_criteria:
- The system must validate required fields according to country and laboratory configuration.
- The system must prevent duplicate active patients when identity rules match.
- The system must require guardian information when the patient is a minor.
- The system must emit PatientCreated after successful registration.
related_nodes:
- BR-001
- API-001
- ENT-001
- EVT-001
- UI-001
- MOB-001
- TEST-001
```
