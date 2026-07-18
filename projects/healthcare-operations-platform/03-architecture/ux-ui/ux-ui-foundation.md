# HOP UX/UI Foundation Baseline

Machine-readable source: `ux-ui-foundation.yaml`. Produced by `HOP-ENT-FOUND-001`.

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
