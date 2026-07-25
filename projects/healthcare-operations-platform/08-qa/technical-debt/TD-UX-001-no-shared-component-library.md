---
id: TD-UX-001
format: markdown_structured_payload
type: technical-debt-item
name: No shared Button/FormField/DataTable component library; each of the 26 employee-portal
  screens implements its own markup
version: 1.0.0
status: open
---

# No Shared Button/Formfield/Datatable Component Library; Each Of The 26 Employee Portal Screens Implements Its Own Markup

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-UX-001
  type: technical-debt-item
  name: No shared Button/FormField/DataTable component library; each of the 26 employee-portal
    screens implements its own markup
  version: 1.0.0
  status: open
  created_date: 2026-07-17
source:
  discovered_during_backlog_item: HOP-ENT-FOUND-001
  module: HOP-ENTERPRISE-FOUNDATION-ALIGNMENT
  evidence: 03-architecture/ux-ui/ux-ui-foundation.md
classification:
  category: ux_ui_design_system_gap
  affected_area: employee_portal_component_reuse
  affected_components:
  - 07-implementation/employee-portal/src/components/screens/**
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: Functional correctness is unaffected; this is a maintainability/consistency
    improvement opportunity, not a defect.
current_state:
  issue: 26 screen components each implement their own form fields, tables and buttons
    with plain HTML and per-screen CSS classes rather than a shared component library.
  compensating_control:
  - AppShell, ConfirmDialog, ScopeIndicator and StatusBanner are already shared for
    the cross-cutting concerns they cover.
target_state:
  preferred_open_source_tooling: []
  expected_integration_points:
  - New src/components/common/{Button,FormField,DataTable}.tsx shared components,
    adopted incrementally per screen.
remediation:
  strategy: gradual_when_a_future_screen_backlog_item_next_touches_multiple_screens
  owner: frontend_platform_team
  estimated_effort: large (incremental across 26 screens)
  estimated_cost_impact: low
  target_backlog: gradual_when_a_future_screen_backlog_item_next_touches_multiple_screens
  dependencies_or_prerequisites: []
  incremental_remediation_triggers:
  - A future backlog item touches 3+ screens at once (natural point to extract shared
    markup).
  acceptance_criteria:
  - At least Button/FormField/DataTable exist as shared components and are adopted
    by newly touched screens going forward.
  owner_or_responsible_role: frontend_platform_team
```
