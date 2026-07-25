---
id: HOP-CAP-PKG-BCM-IMG-005
format: markdown_structured_payload
type: capability-package
name: PACS Integration Capability Package
version: 1.0.0
status: modeled
---

# PACS Integration Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-IMG-005
  type: capability-package
  name: PACS Integration Capability Package
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
  id: BCM-IMG-005
  name:
    en: PACS Integration
    es: Integración PACS
  domain: DOM-06 Imaging
  priority: High
  roadmap: REL-004
  dependency_profile: imaging_operations
  bounded_context: imaging-adapters
  primary_aggregate: PacsIntegrationEndpoint
  aggregate_ref: AGG-035
  process_ref: HRP-006-P05
scope:
  summary: 'Governs PACS archive integration via WADO-RS / WADO-URI standards, web viewer token generation, thumbnail retrieval, and study storage location resolution.'
dependencies:
  required_capabilities:
  - BCM-IMG-004
  - BCM-PLT-004
  downstream_capabilities:
  - BCM-IMG-006
  - BCM-IMG-008
```
