---
id: HOP-PROC-BCM-SVC-007
format: markdown_structured_payload
type: processes
name: Sample Catalog Processes
version: 0.1.0
status: modeled
---

# Sample Catalog Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-SVC-007
  type: processes
  name: Sample Catalog Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-007
actors:
- id: catalog-administrator
  name: Catalog Administrator
  source: ACM-001
- id: laboratory-manager
  name: Laboratory Manager
  source: ACM-001
processes:
- id: PRC-SVC-007-01
  name: Define sample type
  actor: catalog-administrator
  trigger: A new sample type must be configured.
  commands:
  - CreateSampleType
  preconditions:
  - Laboratory exists.
  - Actor holds catalog.sample.write.
  steps:
  - Capture code, name and matrix.
  - Persist as draft version 1.
  outcome: SampleTypeCreated
  rules:
  - RN-001
  - RN-006
- id: PRC-SVC-007-02
  name: Define sample requirement
  actor: catalog-administrator
  trigger: A test requires a sample specification.
  commands:
  - CreateSampleRequirement
  preconditions:
  - Sample type exists.
  steps:
  - Select sample type and container.
  - Set volume, handling and temperature.
  - Persist as draft version 1.
  outcome: SampleRequirementCreated
  rules:
  - RN-002
- id: PRC-SVC-007-03
  name: Publish sample requirement
  actor: laboratory-manager
  trigger: A draft requirement is ready for operational use.
  commands:
  - PublishSampleRequirement
  preconditions:
  - Sample type is published.
  steps:
  - Validate sample type publication and handling completeness.
  - Freeze requirement snapshot.
  - Mark requirement published.
  outcome: SampleRequirementPublished
  rules:
  - RN-003
  - RN-004
  - RN-005
commands:
- name: CreateSampleType
  generatable: true
- name: UpdateSampleType
  generatable: true
- name: CreateSampleRequirement
  generatable: true
- name: UpdateSampleRequirement
  generatable: true
- name: PublishSampleRequirement
  generatable: false
  custom_reason: Cross-entity publication validation and snapshot freeze.
- name: DeprecateSampleRequirement
  generatable: true
```
