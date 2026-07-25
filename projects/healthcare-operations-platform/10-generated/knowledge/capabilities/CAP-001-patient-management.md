---
id: CAP-001
format: markdown_structured_payload
name: Patient Management
version: 0.23.0
status: draft
---

# Patient Management

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: CAP-001
name: Patient Management
node_type: business_capability
version: 0.23.0
status: draft
links:
  markdown: capability-library/CAP-001-patient-management/README.md
  model: capability-library/CAP-001-patient-management/capability.md
relations:
  owns_rules:
  - CAP-001-BR-001
  - CAP-001-BR-020
  - CAP-001-BR-030
  owns_entities:
  - ENT-001
  - ENT-002
  - ENT-003
  - ENT-004
  - ENT-005
  - ENT-006
  owns_events:
  - EVT-001
  - EVT-002
  - EVT-003
  - EVT-004
  - EVT-005
  governed_by:
  - ADR-004
  - ADR-005
  - ADR-008
  exposes_contracts:
  - API-PATIENTS-V1
  consumes_capabilities:
  - CAP-002
  - CAP-003
  - CAP-004
  enables_capabilities:
  - CAP-005
  - CAP-006
impact_analysis:
  if_rule_changes:
    CAP-001-BR-020:
      impacts:
      - DT-001
      - US-001
      - US-006
      - WEB-PAT-002
      - WEB-PAT-006
      - MOB-PAT-005
      - API-PATIENTS-V1
```
