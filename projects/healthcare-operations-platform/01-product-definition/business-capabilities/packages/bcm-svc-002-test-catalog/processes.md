---
id: HOP-PROC-BCM-SVC-002
format: markdown_structured_payload
type: processes
name: Test Catalog Processes
version: 0.1.0
status: modeled
---

# Test Catalog Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-SVC-002
  type: processes
  name: Test Catalog Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-002
actors:
- id: catalog-administrator
  name: Catalog Administrator
  source: ACM-001
- id: laboratory-manager
  name: Laboratory Manager
  source: ACM-001
processes:
- id: PRC-SVC-002-01
  name: Define diagnostic test
  actor: catalog-administrator
  trigger: A new test must be configured in the catalog.
  commands:
  - CreateTestDefinition
  preconditions:
  - Laboratory exists.
  - Actor holds catalog.test.write.
  steps:
  - Capture code, name, methodology, unit and result type.
  - Link analytes and sample requirements.
  - Persist as draft version 1.
  outcome: TestDefinitionCreated
  rules:
  - RN-001
  - RN-002
  - RN-006
- id: PRC-SVC-002-02
  name: Publish diagnostic test
  actor: laboratory-manager
  trigger: A draft test is ready for operational use.
  commands:
  - PublishTestDefinition
  preconditions:
  - Sample requirements and analytes are published.
  steps:
  - Validate linked component publication state.
  - Freeze test snapshot.
  - Mark test published.
  outcome: TestDefinitionPublished
  rules:
  - RN-003
  - RN-004
  - RN-005
- id: PRC-SVC-002-03
  name: Deprecate diagnostic test
  actor: laboratory-manager
  trigger: A test should no longer be orderable.
  commands:
  - DeprecateTestDefinition
  steps:
  - Mark test deprecated.
  - Preserve historical references.
  outcome: TestDefinitionDeprecated
commands:
- name: CreateTestDefinition
  generatable: true
- name: UpdateTestDefinition
  generatable: true
- name: PublishTestDefinition
  generatable: false
  custom_reason: Cross-aggregate publication validation and snapshot freeze.
- name: DeprecateTestDefinition
  generatable: true
```
