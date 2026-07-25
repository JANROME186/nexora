---
id: HOP-CAP-PKG-BCM-IMG-006
format: markdown_structured_payload
type: capability-package
name: Medical Dictation Capability Package
version: 1.0.0
status: modeled
---

# Medical Dictation Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-IMG-006
  type: capability-package
  name: Medical Dictation Capability Package
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
  id: BCM-IMG-006
  name:
    en: Medical Dictation
    es: Dictado Médico y Transcripción
  domain: DOM-06 Imaging
  priority: High
  roadmap: REL-004
  dependency_profile: imaging_operations
  bounded_context: radiology-reporting
  primary_aggregate: RadiologyDictation
  aggregate_ref: AGG-036
  process_ref: HRP-006-P06
scope:
  summary: 'Supports radiologist worklist management, audio/speech-to-text dictation recording, preliminary report drafting, structured template population, and diagnostic impression entry.'
dependencies:
  required_capabilities:
  - BCM-IMG-003
  - BCM-IMG-005
  - BCM-PER-003
  downstream_capabilities:
  - BCM-IMG-007
```
