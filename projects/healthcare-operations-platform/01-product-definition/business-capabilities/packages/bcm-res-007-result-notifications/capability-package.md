---
id: HOP-CAP-PKG-BCM-RES-007
format: markdown_structured_payload
type: capability-package
name: Result Notifications Capability Package
version: 0.1.0
status: modeled
---

# Result Notifications Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-RES-007
  type: capability-package
  name: Result Notifications Capability Package
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
  id: BCM-RES-007
  name:
    en: Result Notifications
    es: Notificaciones
  domain: DOM-07 Results
  priority: High
  roadmap: MVP1
  dependency_profile: results_delivery
  bounded_context: notifications
  primary_aggregate: ResultNotificationRequest (new entity owned by this capability;
    LaboratoryResult AGG-009 is read-only)
  process_ref: HRP-001-P07
scope:
  summary: 'Owns the business decision of when and what to notify a patient or doctor
    about a result (delivery available, critical result, amendment), composing the
    notification content and submitting it to BCM-PLT-003 for provider-agnostic dispatch.
    This capability decides the "what" and "why"; BCM-PLT-003 decides the "how" (channel
    adapter mechanics). Never mutates LaboratoryResult, Patient or Doctor.

    '
  in_scope:
  - ResultNotificationRequest entity: trigger reason, recipient, composed template
      reference, dispatch tracking reference.
  - Notification composition rules per trigger (delivery available, critical result,
    amendment).
  - Delegated submission to BCM-PLT-003's NotificationRequest via SubmitNotificationRequest.
  - Notification delivery status tracking (queued/dispatched/delivered/failed) mirrored
    from BCM-PLT-003 events.
  out_of_scope:
  - Physical dispatch mechanics and provider adapters (BCM-PLT-003).
  - LaboratoryResult aggregate ownership (BCM-LAB-006) and delivery authorization
    (BCM-RES-004).
  - Critical-result escalation/acknowledgement tracking (BCM-RES-006).
roadmap:
  module: COM-MOD-009
  release: REL-002
  package_status: modeled
  next_backlog_item: COM-MOD-009-BE-001
dependencies:
  required_capabilities:
  - BCM-LAB-010
  - BCM-RES-004
  - BCM-PLT-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-RES-006
  downstream_capabilities: []
  upstream_contexts:
  - laboratory-results
  - notifications
  - identity-access
  - audit-compliance
product_surfaces:
  backend: required
  employee_portal: not_required
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
