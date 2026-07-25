# HOP UX/UI Foundation Baseline

Machine-readable source: `ux-ui-foundation.md`. Produced by `HOP-ENT-FOUND-001`.

## Honesty note on scope

The employee portal is a functional, plain-CSS administrative tool with no pre-existing design
system. This document formalizes a **real, minimum baseline** (tokens extracted from actual
current styles, documented principles, an honest component inventory) rather than fabricating an
unused design system. A fuller shared component library remains tracked debt (below).

## Principles

1. **Function over decoration** — an operational tool used under time pressure.
2. **Explicit state and outcome feedback** — every action shows success/error/empty state.
3. **Scope-aware, not scope-hidden** — tenant/lab/branch scope is always visible.
4. **Permission-aware navigation** — menus reflect what the session can actually do.
5. **Locale-aware by construction** — new text goes through the locale catalog, not inline.

## Design tokens (implemented this iteration)

Hex values already used across `src/styles.css` were extracted into named CSS custom properties
(`--hop-color-*`) with **zero visual change** — a pure naming refactor derived from real
production values, not an aspirational token system with no consumers.

## Component inventory

Existing shared components: `AppShell` (header, permission-filtered nav, language switch),
`ConfirmDialog`, `ScopeIndicator`, `StatusBanner`. **Gap**: the 27 screens each implement their own
form/table markup rather than sharing a component library — tracked as **TD-UX-001**.

## Layout system

Web: single-column app shell (header + tab nav + content), free-form per-screen content region.
Responsive breakpoints are not yet formalized (**TD-UX-002**). Mobile: not applicable yet — no
renderer stack exists (**TD-UX-003**, depends on `TD-APP-001`).

## Accessibility baseline

Partial and pre-existing: `aria-current`/`aria-label` on the navigation landmark, extended
consistently to the new language switch. No automated accessibility scan (axe-core/Playwright) is
wired into `npm run quality` yet — tracked as part of **TD-UX-002**.

## Closure gate compliance

A real baseline exists and is used by this iteration's own new UI (language switch, permission
filtering) with no ad hoc styling introduced. Fuller maturity is explicit, owned debt.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UXUI-FOUND-001
  type: ux-ui-foundation
  name: HOP UX/UI Foundation Baseline
  version: 1.0.0
  status: approved
  human_readable: ux-ui-foundation.md
  machine_readable: ux-ui-foundation.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-17
  source_backlog_item: HOP-ENT-FOUND-001
purpose: 'Establish the minimum UX/UI baseline (principles, tokens, component inventory,
  layout, accessibility) required before new/modified screens continue, per ../../../../nexora-framework/02-standards/standards/enterprise-product-foundation-standard.md
  (mandatory_foundations.ux_ui_design_system).

  '
scope_and_honesty_note: 'The employee-portal today (27 screens) is a functional, unstyled-framework,
  plain-CSS administrative tool with no pre-existing design system. This document
  formalizes the minimum real baseline achievable in this foundation-alignment iteration
  (design tokens extracted from the actual current styles, documented principles,
  a real component inventory of what exists today) rather than fabricating a large
  unused design system. A fuller component library (buttons, form controls, tables
  as reusable components rather than per-screen markup) remains registered as technical
  debt below.

  '
ux_ui_principles:
- id: PRIN-001
  name: Function over decoration
  statement: HOP's employee portal is an operational tool used under time pressure
    (front desk, cashier, lab bench). Every screen prioritizes fast task completion
    over visual flourish.
- id: PRIN-002
  name: Explicit state and outcome feedback
  statement: Every mutating action shows a clear success/error banner (existing StatusBanner
    component) and every list/detail view shows an explicit empty state rather than
    a blank screen.
- id: PRIN-003
  name: Scope-aware, not scope-hidden
  statement: Tenant/laboratory/branch scope is always visible (existing ScopeIndicator
    component), never implicit, so multi-tenant/multi-branch staff always know what
    they are viewing.
- id: PRIN-004
  name: Permission-aware navigation
  statement: Navigation reflects what the authenticated session is actually allowed
    to do (see iam-permission-model.md's dynamic-menu filtering, added this iteration)
    rather than showing then blocking.
- id: PRIN-005
  name: Locale-aware by construction
  statement: New UI text is written against the locale catalog (localization-strategy.md),
    not inline, so the product can serve es-MX and en-US users without a rewrite.
design_tokens:
  status: implemented_this_iteration
  location: 07-implementation/employee-portal/src/styles.css (:root custom-property
    block)
  approach: 'Extracted the hex values already in use across styles.css into named
    CSS custom properties (--hop-color-background, --hop-color-text, --hop-color-border,
    --hop-color-accent, and similar, exact names per the implemented CSS) with zero
    visual change — a pure token-naming refactor, not a redesign. This is a deliberately
    small, low-risk first step; a fuller token set (spacing scale, typography scale,
    elevation/shadow tokens) is registered as technical debt below.

    '
  rationale_for_scope: 'Introducing a large, aspirational token system with no consuming
    screens would be premature and effectively unused code. Tokens were derived from
    real, already-in-production values so every token is immediately meaningful.

    '
component_inventory:
  existing_shared_components:
  - name: AppShell
    path: 07-implementation/employee-portal/src/components/layout/AppShell.tsx
    purpose: Header, permission-filtered navigation tabs, language switch, content
      region.
  - name: ConfirmDialog
    path: 07-implementation/employee-portal/src/components/common/ConfirmDialog.tsx
    purpose: Confirmation prompt before a destructive/irreversible action.
  - name: ScopeIndicator
    path: 07-implementation/employee-portal/src/components/common/ScopeIndicator.tsx
    purpose: Displays the active tenant/laboratory/branch scope.
  - name: StatusBanner
    path: 07-implementation/employee-portal/src/components/common/StatusBanner.tsx
    purpose: Success/error/info feedback banner for mutating actions.
  gap: 'Each of the 27 screen components implements its own form fields, tables and
    buttons with plain HTML elements and per-screen CSS classes rather than a shared
    component library. Extracting reusable Button/FormField/DataTable components is
    registered as technical debt below rather than attempted in this iteration (would
    touch all 27 screens, well beyond a foundation- alignment slice''s safe blast
    radius).

    '
layout_system:
  web:
    status: documented (existing, informal)
    pattern: Single-column app shell (header + horizontal tab nav + content region),
      each screen free-form within the content region (typically a filter/search bar
      + a list/table + an optional detail panel).
    responsive_behavior: not_yet_formalized (no explicit breakpoints/media queries
      found in styles.css); registered as technical debt below.
  mobile:
    status: not_applicable_yet
    reason: mobile-app has no renderable UI layer yet (TD-APP-001); a mobile layout
      system will be defined once a renderer stack is selected.
accessibility_baseline:
  status: partial (pre-existing, unformalized)
  evidence_found: 'AppShell''s navigation already uses aria-current="page" for the
    active tab and an aria-label="Administration screens" on the nav landmark (pre-existing,
    confirmed by reading AppShell.tsx). The new language-switch control added this
    iteration follows the same pattern (semantic control with an accessible name).

    '
  gaps: 'No repository-wide accessibility audit (axe-core/Playwright accessibility
    checks) has been run against the employee-portal. Registered as technical debt
    below (the standard''s frontend required tool category "accessibility_when_ui_changes"
    is not yet wired into npm run quality).

    '
technical_debt_registered:
- id: TD-UX-001
  title: No shared Button/FormField/DataTable component library; each of the 27 screens
    implements its own markup
  status: open
  risk_level: low
  blocking: false
  target_backlog: gradual_when_a_future_screen_backlog_item_next_touches_multiple_screens
  owner: frontend_platform_team
- id: TD-UX-002
  title: No formalized responsive breakpoints/layout system; no automated accessibility
    (axe-core/Playwright) check wired into npm run quality
  status: open
  risk_level: medium
  blocking: false
  target_backlog: next_frontend_quality_profile_hardening_backlog_item
  owner: frontend_platform_team
- id: TD-UX-003
  title: No mobile layout system defined yet (no renderer stack selected)
  status: open
  risk_level: low
  blocking: false
  depends_on: TD-APP-001
  target_backlog: after_TD-APP-001_renderer_selection
  owner: mobile_platform_team
closure_gate_compliance: 'A real, honest UX/UI baseline exists (principles, real design
  tokens extracted from production CSS, a documented component inventory including
  its gaps, a documented layout pattern, and a documented partial accessibility baseline).
  New/changed UI this iteration (AppShell language switch, permission-filtered navigation)
  uses the existing shared components and the new tokens; no new ad hoc styling was
  introduced. Fuller design-system maturity is explicit, owned technical debt, not
  a silent gap.

  '
```
