---
id: HOP-CAP-PKG-BCM-RES-004
format: markdown_structured_payload
type: capability-package
name: Digital Delivery Capability Package
version: 0.1.0
status: modeled
---

# Digital Delivery Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-RES-004
  type: capability-package
  name: Digital Delivery Capability Package
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
  id: BCM-RES-004
  name:
    en: Digital Delivery
    es: Entrega Digital
  domain: DOM-07 Results
  priority: High
  roadmap: MVP1
  dependency_profile: results_delivery
  bounded_context: laboratory-results
  primary_aggregate: ResultDeliveryTicket (new entity owned by this capability; LaboratoryResult
    AGG-009 is read-only)
  process_ref: HRP-001-P07
scope:
  summary: 'Delivers a released result and its generated report to authorized patient,
    doctor and mobile channels. Enforces that only released results are ever delivered
    externally, that patients and representatives see only their own (or represented)
    results, and that doctors see only results linked to their referral or treatment
    relationship. Owns a ResultDeliveryTicket process record tracking delivery attempts
    and status; never mutates LaboratoryResult, Patient or Doctor. Notification dispatch
    itself is delegated to BCM-RES-007/BCM-PLT-003, keeping this capability decoupled
    from any specific delivery provider.

    '
  in_scope:
  - ResultDeliveryTicket entity: delivery channel, recipient, status, delivered/viewed
      timestamps.
  - Release-state and authorization gate before any external visibility (patient portal,
    doctor portal, mobile app).
  - Patient representative authorization check before delivering to a representative.
  - Delegated notification trigger through BCM-RES-007 when a delivery becomes visible.
  - Recording ResultViewed when an authorized recipient opens the result.
  out_of_scope:
  - LaboratoryResult aggregate ownership and release/amendment authority (BCM-LAB-006,
    BCM-LAB-010).
  - PDF report generation and document storage (BCM-RES-002, BCM-PLT-008).
  - Notification message composition and provider dispatch (BCM-RES-007, BCM-PLT-003).
  - Chronological multi-result history views (BCM-RES-005).
roadmap:
  module: COM-MOD-009
  release: REL-002
  package_status: modeled
  next_backlog_item: COM-MOD-009-BE-001
dependencies:
  required_capabilities:
  - BCM-LAB-010
  - BCM-RES-001
  - BCM-RES-002
  - BCM-PER-002
  - BCM-PER-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-RES-007
  downstream_capabilities:
  - BCM-RES-005
  - BCM-RES-007
  upstream_contexts:
  - laboratory-results
  - patient-management
  - medical-staff
  - notifications
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
