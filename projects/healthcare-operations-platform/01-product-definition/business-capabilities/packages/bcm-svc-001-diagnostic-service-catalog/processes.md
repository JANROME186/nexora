---
id: HOP-PROC-BCM-SVC-001
format: markdown_structured_payload
type: processes
name: Diagnostic Service Catalog Processes
version: 0.1.0
status: modeled
---

# Diagnostic Service Catalog Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-SVC-001
  type: processes
  name: Diagnostic Service Catalog Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-001
actors:
- id: catalog-administrator
  name: Catalog Administrator
  source: ACM-001
- id: laboratory-manager
  name: Laboratory Manager
  source: ACM-001
processes:
- id: PRC-SVC-001-01
  name: Create diagnostic service
  actor: catalog-administrator
  trigger: A new diagnostic service must be offered.
  commands:
  - CreateDiagnosticService
  preconditions:
  - Laboratory and category exist.
  - Actor holds catalog.service.write.
  steps:
  - Capture service code, name, category and type.
  - Attach test or panel component references.
  - Persist as draft version 1.
  outcome: DiagnosticServiceCreated
  rules:
  - RN-001
  - RN-005
  - RN-006
- id: PRC-SVC-001-02
  name: Publish diagnostic service
  actor: laboratory-manager
  trigger: A draft service is ready for operational use.
  commands:
  - PublishDiagnosticService
  preconditions:
  - Service references at least one published component.
  steps:
  - Validate component publication state.
  - Freeze service snapshot.
  - Mark service published.
  outcome: DiagnosticServicePublished
  rules:
  - RN-002
  - RN-003
- id: PRC-SVC-001-03
  name: Deprecate diagnostic service
  actor: laboratory-manager
  trigger: A service should no longer be orderable.
  commands:
  - DeprecateDiagnosticService
  steps:
  - Mark service deprecated.
  - Preserve historical references.
  outcome: DiagnosticServiceDeprecated
  rules:
  - RN-004
commands:
- name: CreateDiagnosticService
  generatable: true
- name: UpdateDiagnosticService
  generatable: true
- name: PublishDiagnosticService
  generatable: false
  custom_reason: Cross-aggregate publication validation and snapshot freeze.
- name: DeprecateDiagnosticService
  generatable: true
```
