# MVP-MOD-004-FE-001 — Security & Quality Evidence

Backlog item: `MVP-MOD-004-FE-001` — Compile front desk worklist and order creation
employee-portal UI outputs. Status: **passed**.

## Open-source-first

No new dependency was introduced. The new API client and screens are pure application code built
entirely on the existing employee-portal toolchain and existing shared components
(`StatusBanner`, `ConfirmDialog`, `ScopeIndicator`, `useAsyncAction`, `i18n/messages.ts`).

## Checks

| Check | Result |
|---|---|
| Tests (24, 13 files) | passed |
| SAST / static analysis (ESLint) | passed, 0 errors |
| Dependency vulnerability scan (`npm audit`) | passed, 0 vulnerabilities |
| Coverage | passed, 76.51% (floor 73.04%, no regression) |
| Message externalization / i18n review | passed |
| DAST (OWASP ZAP baseline) | passed with disposed warnings: 0 FAIL, 4 WARN tracked/disposed |
| Container-IaC scan | not applicable (no container or IaC assets changed) |

## Message externalization review

- **New repeated strings centralized** in `i18n/messages.ts`: `selectReceptionVisitFirst`
  ("Select a reception visit first.", 4 occurrences) and `selectOrderFirst` ("Select a diagnostic
  order first.", 4 occurrences).
- **New named business constant**: `MIN_CANCELLATION_OVERRIDE_JUSTIFICATION_LENGTH = 15`, mirroring
  the backend's own identically-named, identically-valued constant — not independently invented.
- Backend business-error prose (`RECEPTION_IDENTITY_NOT_CONFIRMED`,
  `ORDER_CANCELLATION_OVERRIDE_REQUIRED`, etc.) is rendered as-is, not re-authored on the frontend,
  consistent with every other employee-portal screen and the HOP-QA-ALIGN-005 baseline.
- Single-occurrence UI copy (headings, labels, hints) remains inline, consistent with the
  HOP-QA-ALIGN-005 closure rule and the broader scope tracked by `TD-I18N-002`.

## Application defect found and fixed

**MVP-MOD-004-FE-001-DEFECT-001**: success `StatusBanner`s for confirm-identity, price, accept,
complete and cancel were rendered only inside the same status-gated JSX block whose visibility
condition the action's own success response flips — e.g. pricing an order moves `order.status`
from `draft` to `priced`, hiding both the "Price order" button *and* its success banner in the
same render. Operators would see the control vanish with no visible confirmation. Fixed by moving
each `StatusBanner` outside its status-gated control so it renders against its own
`useAsyncAction` status (independent of order/visit status) and stays visible after the
transition. Found by the new tests (`findByText` timing out), not manual review.

## DAST Results

OWASP ZAP baseline was executed against the local employee portal at
`http://host.docker.internal:5173`. Reports were generated as `zap-employee-portal.html`,
`zap-employee-portal.json` and `zap.yaml`.

Summary: 0 FAIL, 4 WARN, 63 PASS.

Warnings disposition:

- `10038` Content Security Policy Header Not Set: tracked by `TD-FE-005`.
- `10049` Storable but Non-Cacheable Content: tracked by `TD-FE-005`.
- `10109` Modern Web Application: informational SPA detection, no debt required.
- `90004` Cross-Origin-Embedder-Policy Header Missing or Invalid: tracked by `TD-FE-005`.

## Vulnerabilities found and fixed

None in code or dependencies. DAST produced 0 FAIL findings; warning-level hosting header findings
are disposed through `TD-FE-005`.

## Residual findings — accepted risk

| ID | Finding | Risk | Owner | Target |
|---|---|---|---|---|
| TD-FE-005 | Production CSP, COEP and cache-control headers deferred to the production hosting layer | Medium | frontend_platform_team | production hosting/deployment backlog item |
| TD-FE-006 | No Appointment Scheduling/Admission Management/Quotation Management screens yet; reachable via direct API only | Low | frontend_platform_team | MVP-MOD-004 follow-up UI backlog item (not yet scheduled) |

## Technical debt

- **Materially reduced**: `TD-FE-004` (coverage 73.04% → 76.51%).
- **Newly registered**: `TD-FE-006`.
- **Unchanged, out of scope**: `TD-FE-003`, `TD-FE-005`, `TD-I18N-002`, `TD-APP-002`.
- **Blocking**: none.

## Readiness

Security/quality status: **passed**. Ready for next backlog item: **`MVP-MOD-004-QA-001`** —
Order lifecycle and snapshot evidence.
