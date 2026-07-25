---
id: HOP-PROC-BCM-SVC-005
format: markdown_structured_payload
type: processes
name: Patient Preparation Management Processes
version: 0.1.0
status: modeled
---

# Patient Preparation Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-SVC-005
  type: processes
  name: Patient Preparation Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-005
actors:
- id: catalog-administrator
  name: Catalog Administrator
  source: ACM-001
- id: laboratory-manager
  name: Laboratory Manager
  source: ACM-001
processes:
- id: PRC-SVC-005-01
  name: Define preparation instruction
  actor: catalog-administrator
  trigger: A new preparation instruction must be configured.
  commands:
  - CreatePreparation
  preconditions:
  - Laboratory exists.
  - Actor holds catalog.preparation.write.
  steps:
  - Capture code, title, instruction text and category.
  - Set duration for fasting preparations.
  - Persist as draft version 1.
  outcome: PreparationCreated
  rules:
  - RN-001
  - RN-003
  - RN-006
- id: PRC-SVC-005-02
  name: Assign preparation to test or panel
  actor: catalog-administrator
  trigger: A preparation applies to a test or panel.
  commands:
  - AssignPreparation
  preconditions:
  - Target test or panel is published.
  steps:
  - Validate target publication state.
  - Create assignment.
  outcome: PreparationAssigned
  rules:
  - RN-004
- id: PRC-SVC-005-03
  name: Publish preparation instruction
  actor: laboratory-manager
  trigger: A draft preparation is ready for patient-facing use.
  commands:
  - PublishPreparation
  preconditions:
  - Localized text complete for supported languages.
  steps:
  - Validate localization completeness.
  - Freeze preparation snapshot.
  - Mark preparation published.
  outcome: PreparationPublished
  rules:
  - RN-002
  - RN-005
commands:
- name: CreatePreparation
  generatable: true
- name: UpdatePreparation
  generatable: true
- name: AssignPreparation
  generatable: false
  custom_reason: Cross-aggregate target publication validation.
- name: PublishPreparation
  generatable: false
  custom_reason: Localization validation and snapshot freeze.
- name: DeprecatePreparation
  generatable: true
```
