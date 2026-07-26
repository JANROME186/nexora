---
id: HOP-CAP-PKG-BCM-IMG-004
format: markdown_structured_payload
type: capability-package
name: DICOM Integration Capability Package
version: 1.0.0
status: module_closed
---

# DICOM Integration Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-IMG-004
  type: capability-package
  name: DICOM Integration Capability Package
  version: 1.0.0
  status: module_closed
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-25
  roadmap_group: COM-MOD-014
  execution_flow_stage: closeout
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-IMG-004
  name:
    en: DICOM Integration
    es: Integración DICOM
  domain: DOM-06 Imaging
  priority: High
  roadmap: REL-004
  dependency_profile: imaging_operations
  bounded_context: imaging-adapters
  primary_aggregate: DicomAdapterConfiguration
  aggregate_ref: AGG-034
  process_ref: HRP-006-P04
scope:
  summary: 'Provides DICOM C-FIND, C-STORE, Modality Worklist (MWL) service class provider/user (SCP/SCU) adapter boundaries, tag normalization, and study instance UID mapping.'
dependencies:
  required_capabilities:
  - BCM-IMG-003
  - BCM-PLT-004
  - BCM-PLT-005
  downstream_capabilities:
  - BCM-IMG-005
```
