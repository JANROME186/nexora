---
id: TD-UX-001
format: markdown_structured_payload
type: technical-debt-item
name: No shared Button/FormField/DataTable component library; each of the 26 employee-portal
  screens implements its own markup
version: 1.0.0
status: materially_reduced
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
  status: materially_reduced
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
  issue: Legacy screen components still implement many form fields and buttons with
    plain HTML and per-screen CSS classes, but the original no-shared-table gap has
    been materially reduced.
  compensating_control:
  - AppShell, ConfirmDialog, ScopeIndicator and StatusBanner are already shared for
    the cross-cutting concerns they cover.
  - DataTable now exists under 07-implementation/employee-portal/src/components/common/DataTable.tsx
    and is adopted by newer administration screens, including COM-MOD-015-FE-001's
    AI assistant review screen.
target_state:
  preferred_open_source_tooling: []
  expected_integration_points:
  - Continue incremental extraction of Button and FormField components while keeping
    DataTable adoption mandatory for new tabular screens.
remediation:
  strategy: materially_reduced_by_shared_DataTable_StatusBanner_ScopeIndicator_and_COM_MOD_015_FE_001_adoption;
    continue_Button_FormField_extraction_when_future_screens_are_touched
  owner: frontend_platform_team
  estimated_effort: large (incremental across 26 screens)
  estimated_cost_impact: low
  target_backlog: gradual_when_a_future_screen_backlog_item_next_touches_multiple_screens
  dependencies_or_prerequisites: []
  incremental_remediation_triggers:
  - A future backlog item touches 3+ screens at once (natural point to extract shared
    markup).
  acceptance_criteria:
  - DataTable exists as a shared component and is adopted by newly touched tabular
    screens going forward.
  - Button/FormField extraction remains open as incremental follow-up.
  owner_or_responsible_role: frontend_platform_team
  progress_note: COM-MOD-015-FE-001 synced this item from open to materially_reduced
    before feature work because the employee portal already includes shared DataTable,
    StatusBanner, ScopeIndicator and ConfirmDialog components. The new AI assistant
    review UI adopts DataTable/StatusBanner/ScopeIndicator instead of adding another
    per-screen table implementation.
  progress_log:
  - backlog_item: HOP-HARD-FE-001
    date: 2026-07-27
    action: 'All 3 brand-new screens (AppointmentsScreen, AdmissionsScreen, QuotationsScreen)
      adopt the shared DataTable/StatusBanner/ScopeIndicator/ConfirmDialog components for their
      list views rather than hand-rolling per-screen table markup, extending the pattern
      COM-MOD-015-FE-001 established. Additionally, while extending the 2 legacy screens this item
      touched (PatientsScreen, DoctorsScreen) for TD-FE-002, 6 new focused, reusable panel
      components were extracted (PatientEditPanel, PatientDocumentsPanel, RepresentativesPanel,
      RepresentativesTable, DoctorEditPanel, SpecialtiesPanel) -- each a small, props-driven
      component in the same spirit as this item''s target Button/FormField extraction, though
      scoped to their specific panel rather than the generic primitives this item ultimately
      still wants. Generic Button/FormField extraction itself remains open as incremental
      follow-up.'
```
