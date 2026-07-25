---
id: HOP-CAP-PKG-BCM-IMG-008
format: markdown_structured_payload
type: capability-package
name: Imaging Study Delivery Capability Package
version: 1.0.0
status: modeled
---

# Imaging Study Delivery Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-IMG-008
  type: capability-package
  name: Imaging Study Delivery Capability Package
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
  id: BCM-IMG-008
  name:
    en: Imaging Study Delivery
    es: Entrega Digital de Estudios de Imagen
  domain: DOM-06 Imaging
  priority: High
  roadmap: REL-004
  dependency_profile: imaging_operations
  bounded_context: radiology-delivery
  primary_aggregate: ImagingDeliveryPackage
  aggregate_ref: AGG-038
  process_ref: HRP-006-P08
scope:
  summary: 'Governs multi-channel delivery of signed radiology PDF reports and DICOM viewer links to patient portal, doctor portal, email/SMS notification adapters, and external clinical referrers.'
dependencies:
  required_capabilities:
  - BCM-IMG-007
  - BCM-IMG-005
  - BCM-PLT-003
  - BCM-RES-004
  downstream_capabilities:
```
