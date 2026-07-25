---
id: HOP-CAP-PKG-BCM-LAB-006
format: markdown_structured_payload
type: capability-package
name: Laboratory Processing Capability Package
version: 0.1.0
status: validated
---

# Laboratory Processing Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-LAB-006
  type: capability-package
  name: Laboratory Processing Capability Package
  version: 0.1.0
  status: validated
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-16
  roadmap_group: MVP-MOD-006
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-LAB-006
  name:
    en: Laboratory Processing
    es: Procesamiento
  domain: DOM-05 Clinical Operations
  priority: Critical
  roadmap: MVP1
  dependency_profile: clinical_operations
  bounded_context: laboratory-results
  primary_aggregate: LaboratoryResult
  aggregate_ref: AGG-009
  process_ref: HRP-001-P05
scope:
  summary: 'Owns the LaboratoryResult aggregate end to end for the laboratory-results
    bounded context: creates it from a received Sample and an analyte from the published
    test catalog, captures result values (manual entry or normalized device message),
    records processing incidents, and submits the result for validation. Technical
    Validation (BCM-LAB-008), Medical Validation (BCM-LAB-009) and Result Release
    (BCM-LAB-010) are sibling capabilities in the same bounded context with delegated
    authority over specific named validation/release fields, mirroring the Sample
    / BCM-LAB-002 ownership pattern used earlier in this module.

    '
  in_scope:
  - LaboratoryResult aggregate lifecycle creation (capture, incident recording, submission
    for validation).
  - Immutable capture of sample reference, analyte/test-definition reference and reference-range
    snapshot at capture time.
  - Manual result-value entry and normalized device-message ingestion (ASTM/HL7/file-based,
    normalized upstream by BCM-PLT-004 before reaching this capability).
  - Processing incident recording (equipment error, repeat-required, dilution required).
  - Declaring the full LaboratoryResult aggregate model shared by BCM-LAB-008, BCM-LAB-009
    and BCM-LAB-010.
  out_of_scope:
  - Sample collection, labeling and reception (BCM-LAB-002, BCM-LAB-003, BCM-LAB-005).
  - Technical validation, critical-result flagging (BCM-LAB-008).
  - Medical validation (BCM-LAB-009).
  - Result release and post-release amendment (BCM-LAB-010).
  - Report generation and digital delivery (BCM-RES-001, BCM-RES-002, MVP-MOD-007).
  - Raw external protocol normalization, which is an anti-corruption-layer responsibility
    of BCM-PLT-004 per the context map (REL-CTX-011).
roadmap:
  module: MVP-MOD-006
  release: REL-001
  package_status: module_closed
  next_backlog_item: none (module closed; see MVP-MOD-007-DEF for the next roadmap
    module)
dependencies:
  required_capabilities:
  - BCM-LAB-005
  - BCM-SVC-004
  - BCM-SVC-006
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-004
  downstream_capabilities:
  - BCM-LAB-008
  upstream_contexts:
  - orders-samples
  - catalog-test-configuration
  - identity-access
  - audit-compliance
  - integration-interoperability
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: status_later
  doctor_portal: status_later
  mobile_app: not_required
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
