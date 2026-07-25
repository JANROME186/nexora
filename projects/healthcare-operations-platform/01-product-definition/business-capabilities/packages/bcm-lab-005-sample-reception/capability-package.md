---
id: HOP-CAP-PKG-BCM-LAB-005
format: markdown_structured_payload
type: capability-package
name: Sample Reception Capability Package
version: 0.1.0
status: validated
---

# Sample Reception Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-LAB-005
  type: capability-package
  name: Sample Reception Capability Package
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
  id: BCM-LAB-005
  name:
    en: Sample Reception
    es: Recepción de Muestras
  domain: DOM-05 Clinical Operations
  priority: High
  roadmap: MVP1
  dependency_profile: clinical_operations
  bounded_context: orders-samples
  primary_aggregate: Sample (AGG-008, owned by BCM-LAB-002)
  process_ref: HRP-001-P05
scope:
  summary: 'Receives a labeled Sample at the laboratory, verifies specimen condition
    against reception criteria (labeling present, container intact, no hemolysis or
    other visible defect, within transport time window) and either accepts it (receivedRecord
    + status=received) or rejects it (rejectionReason with rejectionStage=at_reception).
    Also manages end-of-life disposal for samples that finish or fail the pipeline.
    Holds delegated authority over Sample.receptionRecord, rejection-at-reception
    and disposal only; it does not create, collect or label samples.

    '
  in_scope:
  - Reception worklist for labeled, in-transit samples awaiting laboratory intake.
  - SampleReceptionRecord condition verification (hemolysis, insufficient volume,
    wrong container, unlabeled, clotted).
  - Delegated mutation of Sample.receptionRecord through ReceiveSampleAtLaboratory.
  - Delegated rejection-at-reception through RejectSampleAtReception.
  - Sample disposal through DisposeSample once a sample reaches a terminal, evidence-preserved
    state.
  out_of_scope:
  - Sample creation, collection data, rejection at collection (BCM-LAB-002).
  - Specimen label printing and confirmation (BCM-LAB-003).
  - Sample transport between sites (BCM-LAB-004, MVP2, out of MVP-MOD-006).
  - Laboratory processing and result capture (BCM-LAB-006).
roadmap:
  module: MVP-MOD-006
  release: REL-001
  package_status: module_closed
  next_backlog_item: none (module closed; see MVP-MOD-007-DEF for the next roadmap
    module)
dependencies:
  required_capabilities:
  - BCM-LAB-002
  - BCM-LAB-003
  - BCM-ORG-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities: []
  downstream_capabilities:
  - BCM-LAB-006
  upstream_contexts:
  - orders-samples
  - organization-management
  - identity-access
  - audit-compliance
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: not_required
  doctor_portal: not_required
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
