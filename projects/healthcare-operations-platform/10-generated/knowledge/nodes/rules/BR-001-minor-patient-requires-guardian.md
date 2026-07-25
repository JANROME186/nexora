---
id: BR-001
format: markdown_structured_payload
type: business_rule
name: Minor Patient Requires Guardian
version: 0.13.0
status: draft
---

# Minor Patient Requires Guardian

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: BR-001
name: Minor Patient Requires Guardian
type: business_rule
status: draft
version: 0.13.0
owner: Business Architecture
source_path: knowledge/nodes/rules/BR-001-minor-patient-requires-guardian.md
description: 'If the patient is a minor according to the applicable country pack,
  the patient record must include at least one responsible guardian or legal representative.

  '
related_nodes:
- CAP-001
- DOM-001
- US-001
- API-001
- ENT-001
- TEST-001
rule_expression:
  when: patient.age < country_pack.legal_adult_age
  then: guardian.required == true
impact_level: high
```
