---
id: HOP-CAP-PKG-BCM-IMG-007
format: markdown_structured_payload
type: capability-package
name: Radiology Signature Capability Package
version: 1.0.0
status: module_closed
---

# Radiology Signature Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-IMG-007
  type: capability-package
  name: Radiology Signature Capability Package
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
  id: BCM-IMG-007
  name:
    en: Radiology Signature
    es: Firma e Interpretación Radiológica
  domain: DOM-06 Imaging
  priority: High
  roadmap: REL-004
  dependency_profile: imaging_operations
  bounded_context: radiology-reporting
  primary_aggregate: RadiologyReport
  aggregate_ref: AGG-037
  process_ref: HRP-006-P07
scope:
  summary: 'Manages medical validation, cryptographic digital signing of radiology reports, report status transition (DRAFT -> SIGNED -> AMENDED), critical finding alert dispatch, and audit logging.'
dependencies:
  required_capabilities:
  - BCM-IMG-006
  - BCM-PLT-001
  - BCM-PLT-007
  downstream_capabilities:
  - BCM-IMG-008
```
