---
id: patient-management-context
format: markdown_structured_payload
---

# Patient Management Context

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
context_profile:
  id: CTX-001
  name: Patient Management Context
  target_capability: CAP-001
  load:
    required:
    - knowledge/indexes/patient-management-index.md
    - knowledge/relations/patient-management-relations.md
    - knowledge/nodes/business/capabilities/CAP-001-patient-management.md
    - knowledge/nodes/domains/DOM-001-patient-domain.md
    - knowledge/nodes/rules/BR-001-minor-patient-requires-guardian.md
    - knowledge/nodes/stories/US-001-register-patient.md
    - 05-contracts/contracts/openapi/patients/patients-api.md
    optional:
    - ux/screens/patient-registration.md
    - mobile/screens/patient-registration.md
    - qa/contract-tests/patients/register-patient.md
  do_not_load_by_default:
  - unrelated modules
  - full repository history
  - cloud-specific deployment files
```
