---
id: HOP-PROC-BCM-SVC-004
format: markdown_structured_payload
type: processes
name: Analyte Catalog Processes
version: 0.1.0
status: modeled
---

# Analyte Catalog Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-SVC-004
  type: processes
  name: Analyte Catalog Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-004
actors:
- id: catalog-administrator
  name: Catalog Administrator
  source: ACM-001
- id: laboratory-manager
  name: Laboratory Manager
  source: ACM-001
processes:
- id: PRC-SVC-004-01
  name: Define analyte
  actor: catalog-administrator
  trigger: A new measurable analyte must be configured.
  commands:
  - CreateAnalyte
  preconditions:
  - Laboratory exists.
  - Actor holds catalog.analyte.write.
  steps:
  - Capture code, name, data type, unit and precision.
  - Configure result constraints and coded values.
  - Persist as draft version 1.
  outcome: AnalyteCreated
  rules:
  - RN-001
  - RN-002
  - RN-005
  - RN-007
- id: PRC-SVC-004-02
  name: Publish analyte
  actor: laboratory-manager
  trigger: A draft analyte is ready for operational use.
  commands:
  - PublishAnalyte
  preconditions:
  - Coded analytes have coded values.
  steps:
  - Validate constraints and coded values.
  - Freeze analyte snapshot.
  - Mark analyte published.
  outcome: AnalyteDefinitionPublished
  rules:
  - RN-003
  - RN-004
- id: PRC-SVC-004-03
  name: Version analyte data type change
  actor: laboratory-manager
  trigger: A published analyte data type must change.
  commands:
  - UpdateAnalyte
  steps:
  - Create new draft version.
  - Flag dependent tests and reference ranges for review.
  outcome: AnalyteDefinitionRevised
  rules:
  - RN-006
commands:
- name: CreateAnalyte
  generatable: true
- name: UpdateAnalyte
  generatable: true
- name: PublishAnalyte
  generatable: false
  custom_reason: Snapshot freeze and dependent ripple flagging.
- name: DeprecateAnalyte
  generatable: true
```
