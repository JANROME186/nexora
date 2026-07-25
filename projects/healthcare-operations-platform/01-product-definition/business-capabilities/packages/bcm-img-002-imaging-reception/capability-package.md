---
id: HOP-CAP-PKG-BCM-IMG-002
format: markdown_structured_payload
type: capability-package
name: Imaging Reception Capability Package
version: 1.0.0
status: modeled
---

# Imaging Reception Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-IMG-002
  type: capability-package
  name: Imaging Reception Capability Package
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
  id: BCM-IMG-002
  name:
    en: Imaging Reception
    es: Recepción de Imagenología
  domain: DOM-06 Imaging
  priority: High
  roadmap: REL-004
  dependency_profile: imaging_operations
  bounded_context: imaging-operations
  primary_aggregate: ImagingReceptionIntake
  aggregate_ref: AGG-032
  process_ref: HRP-006-P02
scope:
  summary: 'Manages patient check-in at imaging centers, safety screening questionnaires (e.g. MRI safety clearance, renal function / eGFR for contrast), order verification, and radiologist/technician queue routing.'
dependencies:
  required_capabilities:
  - BCM-IMG-001
  - BCM-PER-002
  - BCM-ORG-003
  - BCM-PLT-001
  downstream_capabilities:
  - BCM-IMG-003
```
