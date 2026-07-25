---
id: HOP-PROC-BCM-SVC-003
format: markdown_structured_payload
type: processes
name: Panel Catalog Processes
version: 0.1.0
status: modeled
---

# Panel Catalog Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-SVC-003
  type: processes
  name: Panel Catalog Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-003
actors:
- id: catalog-administrator
  name: Catalog Administrator
  source: ACM-001
- id: laboratory-manager
  name: Laboratory Manager
  source: ACM-001
processes:
- id: PRC-SVC-003-01
  name: Define panel
  actor: catalog-administrator
  trigger: A new panel must be configured.
  commands:
  - CreatePanelDefinition
  preconditions:
  - Member tests exist.
  - Actor holds catalog.panel.write.
  steps:
  - Capture panel code and name.
  - Add member tests with order and mandatory flags.
  - Persist as draft version 1.
  outcome: PanelDefinitionCreated
  rules:
  - RN-001
  - RN-005
- id: PRC-SVC-003-02
  name: Publish panel
  actor: laboratory-manager
  trigger: A draft panel is ready for operational use.
  commands:
  - PublishPanelDefinition
  preconditions:
  - Panel has at least two published member tests.
  steps:
  - Validate member composition and publication state.
  - Freeze panel snapshot.
  - Mark panel published.
  outcome: PanelDefinitionPublished
  rules:
  - RN-002
  - RN-003
  - RN-004
- id: PRC-SVC-003-03
  name: Deprecate panel
  actor: laboratory-manager
  trigger: A panel should no longer be orderable.
  commands:
  - DeprecatePanelDefinition
  steps:
  - Mark panel deprecated.
  - Preserve historical references.
  outcome: PanelDefinitionDeprecated
commands:
- name: CreatePanelDefinition
  generatable: true
- name: UpdatePanelDefinition
  generatable: true
- name: PublishPanelDefinition
  generatable: false
  custom_reason: Cross-aggregate member publication validation and snapshot freeze.
- name: DeprecatePanelDefinition
  generatable: true
```
