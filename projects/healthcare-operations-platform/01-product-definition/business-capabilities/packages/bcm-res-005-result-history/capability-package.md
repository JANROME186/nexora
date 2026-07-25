---
id: HOP-CAP-PKG-BCM-RES-005
format: markdown_structured_payload
type: capability-package
name: Result History Capability Package
version: 0.1.0
status: modeled
---

# Result History Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-RES-005
  type: capability-package
  name: Result History Capability Package
  version: 0.1.0
  status: modeled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-17
  roadmap_group: COM-MOD-009
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-RES-005
  name:
    en: Result History
    es: Históricos
  domain: DOM-07 Results
  priority: High
  roadmap: MVP1
  dependency_profile: results_delivery
  bounded_context: laboratory-results
  primary_aggregate: PatientResultHistoryView (new read-model entity owned by this
    capability; LaboratoryResult AGG-009 and Patient AGG-001 are read-only)
  process_ref: HRP-001-P07
scope:
  summary: 'Provides patients and referring doctors a chronological, trend-aware view
    across their multiple released results over time (e.g. tracking an analyte''s
    value across visits), distinct from BCM-RES-004''s single-result delivery view.
    Owns a read-only PatientResultHistoryView projection built from delivered ResultDeliveryTicket/released-result
    events; never mutates LaboratoryResult or Patient. This capability is also the
    designated upstream read source for future AI-assisted trend analysis (BCM-AI-005/006),
    which may only read, never write, this history.

    '
  in_scope:
  - PatientResultHistoryView read projection: chronological list of a patient's released,
      authorized results with trend indicators for repeated analytes.
  - Cross-result trend computation (e.g. flag a significant change vs the patient's
    prior value for the same analyte).
  - Access authorization mirroring BCM-RES-004's recipient rules (self, represented,
    referred).
  out_of_scope:
  - Single-result delivery, view-state recording and amendment withholding (BCM-RES-004).
  - LaboratoryResult and Patient aggregate ownership (BCM-LAB-006, BCM-PER-002).
  - AI-assisted interpretation of trends (BCM-AI-005, BCM-AI-006, future modules;
    read-only consumers of this capability, never writers).
roadmap:
  module: COM-MOD-009
  release: REL-002
  package_status: modeled
  next_backlog_item: COM-MOD-009-BE-001
dependencies:
  required_capabilities:
  - BCM-RES-001
  - BCM-RES-004
  - BCM-PER-002
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities: []
  downstream_capabilities:
  - BCM-AI-005
  - BCM-AI-006
  upstream_contexts:
  - laboratory-results
  - patient-management
  - identity-access
  - audit-compliance
product_surfaces:
  backend: required
  employee_portal: not_required
  patient_portal: required
  doctor_portal: required
  mobile_app: required
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
