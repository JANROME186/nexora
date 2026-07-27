---
id: TD-FE-012
format: markdown_structured_payload
type: technical-debt-item
name: employee-portal npm audit reports 10 high-severity findings confined to transitive
  devDependencies, requiring a breaking-change fix
version: 1.1.0
status: open
---

# Employee Portal Npm Audit Reports 10 High Severity Findings Confined To Transitive DevDependencies, Requiring A Breaking Change Fix

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-FE-012
  type: technical-debt-item
  name: employee-portal npm audit reports 10 high-severity findings confined to
    transitive devDependencies, requiring a breaking-change fix
  version: 1.1.0
  status: open
  created_date: 2026-07-25
  updated_date: 2026-07-27
source:
  discovered_during_backlog_item: COM-MOD-017-FE-001
  module: COM-MOD-017 Product Marketplace and Extension Packaging
  evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-FE-001-validation.md
classification:
  category: dependency_vulnerability_advisory_drift
  affected_area: employee_portal_npm_audit_gate
  affected_components:
  - 07-implementation/employee-portal/package-lock.json
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: 'All 10 remaining findings are in transitive devDependencies
    only (eslint-plugin-jsx-a11y, eslint-plugin-react, glob -> read-package-json ->
    read-installed-packages -> license-checker-rseidelsohn, and test-exclude ->
    @vitest/coverage-v8); none reach the production dependency graph.
    `npm audit --omit=dev --audit-level=low` reports 0 vulnerabilities (production
    dependencies are only react and react-dom, both clean). The findings do not
    affect the shipped application bundle, a deployed backend, or any runtime
    behavior -- they only affect local build/lint/test tooling.

    '
current_state:
  issue: 'While executing COM-MOD-017-FE-001''s mandatory `npm run audit:all` gate,
    `npm audit --audit-level=low` reported 17 high-severity findings, none caused
    by this backlog item (package.json/package-lock.json were untouched by COM-MOD-017-FE-001''s
    own diff prior to remediation) -- these are newly-surfaced GitHub Advisory
    Database entries against already-installed devDependency versions (advisories
    are matched live against the registry at audit time, not pinned to a point-in-time
    lockfile snapshot), a drift that occurred since COM-MOD-013-FE-001''s clean
    0-vulnerability baseline (2026-07-23) with zero dependency changes on our part.
    Ran `npm audit fix` (non-breaking): resolved 7 of 17 (the `postcss` Path Traversal
    finding and the `@typescript-eslint/*` chain), leaving 10 that all require
    `npm audit fix --force`, which would downgrade `eslint-plugin-jsx-a11y` to
    6.4.1 -- a breaking change to a lint plugin used repo-wide across every HOP
    frontend stack (employee-portal, public-website, patient-portal, doctor-portal,
    mobile-app), not a change scoped to this backlog item''s marketplace UI work.

    '
target_state:
  fix: 'Evaluate `eslint-plugin-jsx-a11y`''s current major version compatibility
    with a forced `minimatch`/`glob` upgrade (or an equivalent non-downgrading
    resolution/override), verify `npm run lint` and the `accessibility.test.tsx`
    jest-axe suite still pass across all touched frontend stacks, and apply as
    a dedicated, cross-stack devDependency-maintenance backlog item rather than
    inside a single module''s UI compilation item.

    '
remediation:
  strategy: gradual_when_a_dedicated_devdependency_maintenance_backlog_item_is_scheduled
  owner: frontend_platform_team
  estimated_effort: small
  estimated_cost_impact: low
  target_backlog: next_devdependency_maintenance_backlog_item
  acceptance_criteria:
  - employee-portal `npm audit --audit-level=low` exits 0 with no forced/breaking
    downgrade to eslint-plugin-jsx-a11y's currently-compatible major version line.
  - No lint or accessibility-test regression across any HOP frontend stack sharing
    the same devDependency.
review_log:
- backlog_item: HOP-HARD-FE-001
  date: 2026-07-27
  action: 'Re-ran `npm audit --audit-level=low` and `npm audit --omit=dev --audit-level=low`
    as part of this item''s mandatory dependency gate. Findings are unchanged from the
    COM-MOD-017-FE-001 baseline -- 10 high-severity findings, all still confined to the same
    brace-expansion/minimatch transitive chain under eslint-plugin-jsx-a11y, eslint-plugin-react,
    glob and test-exclude/@vitest/coverage-v8 devDependencies; `npm audit --omit=dev --audit-level=low`
    still reports 0 vulnerabilities (production dependencies remain only react/react-dom). No
    drift, no new findings, no change to package.json/package-lock.json in this backlog item;
    status remains open/non-blocking pending the dedicated devDependency-maintenance backlog item.'
```
