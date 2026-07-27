---
id: HOP-CAP-PKG-BCM-AI-001
format: markdown_structured_payload
type: capability-package
name: Assistant Orchestration Capability Package
version: 1.0.0
status: module_closed
---

# Assistant Orchestration Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-AI-001
  type: capability-package
  name: Assistant Orchestration Capability Package
  version: 1.0.0
  status: module_closed
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-26
  roadmap_group: COM-MOD-015
  execution_flow_stage: closeout
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-AI-001
  name:
    en: Assistant Orchestration
    es: Orquestacion de Asistente
  domain: DOM-10 Platform
  priority: High
  roadmap: MVP2
  dependency_profile: ai_overlay
  bounded_context: ai-overlay
  primary_aggregate: AssistantSession (AGG-039)
scope:
  summary: Coordinates user-requested assistant sessions across operational workflows with auditable context, role-bound scope and mandatory human control.
  in_scope:
  - Tenant-scoped AI assistance with explicit source, purpose and human reviewer.
  - Provider-neutral model routing and adapter boundaries.
  - Safety policy enforcement, audit evidence and explainability metadata.
  - Operational handoff to backend, employee portal and optional portal/mobile surfaces.
  out_of_scope:
  - Autonomous diagnosis, autonomous treatment decisions or autonomous clinical validation.
  - Provider-locked SDK or non-replaceable runtime dependency in the durable model.
  - Use of patient data outside tenant consent, IAM, audit and privacy controls.
roadmap:
  module: COM-MOD-015
  release: REL-004
  package_status: module_closed
  next_backlog_item: COM-MOD-015-CLOSEOUT
dependencies:
  required_capabilities:
  - BCM-PLT-001
  - BCM-PLT-004
  - BCM-PLT-005
  - BCM-PLT-006
  - BCM-PLT-007
  - BCM-PLT-008
  - BCM-PLT-009
  optional_capabilities:
  - BCM-LAB-006
  - BCM-RES-001
  - BCM-IMG-003
  - BCM-IMG-007
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: optional
  doctor_portal: optional
  mobile_app: optional
  model_provider: replaceable
required_artifacts:
  - capability-package.md
  - business-model.md
  - business-rules.md
  - processes.md
  - events.md
  - openapi-source.md
  - permissions.md
  - ui-model.md
  - mobile-model.md
  - test-model.md
  - observability-model.md
  - generation-plan.md
  - traceability.md
  - README.md
```
