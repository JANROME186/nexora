---
id: HOP-CAP-PKG-BCM-LAB-002
format: markdown_structured_payload
type: capability-package
name: Sample Collection Capability Package
version: 0.1.0
status: validated
---

# Sample Collection Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-LAB-002
  type: capability-package
  name: Sample Collection Capability Package
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
  id: BCM-LAB-002
  name:
    en: Sample Collection
    es: Toma de Muestras
  domain: DOM-05 Clinical Operations
  priority: Critical
  roadmap: MVP1
  dependency_profile: clinical_operations
  bounded_context: orders-samples
  primary_aggregate: Sample
  aggregate_ref: AGG-008
  process_ref: HRP-001-P05
scope:
  summary: 'Owns the Sample aggregate end to end for the orders-samples bounded context:
    this capability creates the Sample from an accepted DiagnosticOrder line, captures
    collection data (collector, site, container, collection time) and defines every
    Sample state-transition command. Sample Labeling (BCM-LAB-003) and Sample Reception
    (BCM-LAB-005) are sibling capabilities within the same bounded context that hold
    delegated authority over specific, named subsets of Sample commands (label assignment;
    laboratory reception and rejection at reception; disposal) so that the aggregate
    always has exactly one authorized mutator per field, never two capabilities racing
    to write the same state. This mirrors the aggregate-ownership pattern already
    established for DiagnosticOrder (BCM-LAB-001) and its satellite intake capabilities
    in MVP-MOD-004.

    '
  in_scope:
  - Sample aggregate lifecycle: collect, reject at collection, dispose.
  - Immutable capture of order line reference, patient snapshot reference, branch,
    collector identity and collection timestamp.
  - Container and collection-site data capture against the published SampleRequirement
    from BCM-SVC-007.
  - Chain-of-custody trace initiation (first custody event at the point of collection).
  - Domain events consumed by labeling, reception and laboratory processing.
  - Declaring the full Sample aggregate model shared by BCM-LAB-003 and BCM-LAB-005.
  out_of_scope:
  - DiagnosticOrder ownership and pricing (BCM-LAB-001).
  - Sample requirement, container and analyte catalog definition (BCM-SVC-004, BCM-SVC-007).
  - Label design/printing and physical label assignment mechanics (BCM-LAB-003).
  - Laboratory reception, rejection-at-reception and disposal-at-reception (BCM-LAB-005).
  - Sample transport and chain-of-custody handoff between sites (BCM-LAB-004, MVP2,
    out of MVP-MOD-006).
  - Result capture, validation and release (BCM-LAB-006, BCM-LAB-008, BCM-LAB-009,
    BCM-LAB-010).
roadmap:
  module: MVP-MOD-006
  release: REL-001
  package_status: module_closed
  next_backlog_item: none (module closed; see MVP-MOD-007-DEF for the next roadmap
    module)
dependencies:
  required_capabilities:
  - BCM-LAB-001
  - BCM-SVC-007
  - BCM-PER-002
  - BCM-ORG-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-SVC-004
  downstream_capabilities:
  - BCM-LAB-003
  - BCM-LAB-005
  - BCM-LAB-006
  upstream_contexts:
  - orders-samples
  - catalog-test-configuration
  - patient-management
  - organization-management
  - identity-access
  - audit-compliance
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: status_later
  doctor_portal: status_later
  mobile_app: sample_collection_later
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
