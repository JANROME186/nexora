---
id: HOP-CAP-PKG-BCM-IMG-003
format: markdown_structured_payload
type: capability-package
name: Imaging Study Management Capability Package
version: 1.0.0
status: modeled
---

# Imaging Study Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-IMG-003
  type: capability-package
  name: Imaging Study Management Capability Package
  version: 1.0.0
  status: modeled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-25
  roadmap_group: COM-MOD-014
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-IMG-003
  name:
    en: Imaging Study Management
    es: Gestión de Estudios de Imagen
  domain: DOM-06 Imaging
  priority: High
  roadmap: REL-004
  dependency_profile: imaging_operations
  bounded_context: imaging-operations
  primary_aggregate: ImagingStudy
  aggregate_ref: AGG-033
  process_ref: HRP-006-P03
scope:
  summary: 'Tracks execution of diagnostic imaging procedures, DICOM Modality Worklist (MWL) status progression, image series acquisition counts, operator details, and study completion handoff.'
dependencies:
  required_capabilities:
  - BCM-IMG-002
  - BCM-PLT-007
  downstream_capabilities:
  - BCM-IMG-004
  - BCM-IMG-006
```
