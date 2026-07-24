# COM-MOD-013-QA-001 Security and Quality Evidence

**Status**: passed
**Backlog Item**: COM-MOD-013-QA-001
**Date**: 2026-07-24

---

## Summary

Integrated backend + employee-portal security/quality validation for COM-MOD-013 Advanced Quality
and Compliance. No new dependency was introduced. All mandatory gates ran and passed. Two real
defects were found and fixed, one major (a persistence-wiring defect, TD-DB-005) and several minor
(SpotBugs High/Medium findings, one hardcoded string, one function-size violation). One new
technical-debt item was registered (TD-IAM-004) for a finding judged out of narrow validation
scope to fix safely.

## Vulnerability / Dependency Scans

| Scope | Tool | Result |
|---|---|---|
| Backend | OWASP Dependency-Check 12.1.3 | 72 dependencies, **0 vulnerable** (local DB, freshness 2026-07-20) |
| Employee portal | `npm audit --audit-level=low` | **0 vulnerabilities** |
| Backend / employee-portal / repo-wide | Trivy 0.72.0 (vuln, secret, misconfig; all severities) | **0 / 0 / 0** across every scope |

No unresolved vulnerability, secret or misconfiguration finding exists at any severity.

## SAST / Secure Code

- **SpotBugs/FindSecBugs**: 70 → **63** findings. 2 High-severity findings fixed
  (`DM_DEFAULT_ENCODING`, `NM_SAME_SIMPLE_NAME_AS_SUPERCLASS`); 5 Medium `CT_CONSTRUCTOR_THROW`
  findings fixed (marked the 5 new domain classes `final`); 3 Medium `DE_MIGHT_IGNORE` findings
  dispositioned as accepted risk (documented, intentional best-effort pattern already used
  elsewhere in this codebase).
- **Checkstyle / PMD / CPD**: 73 / 570 / 2, all non-blocking (`failOnViolation=false`), tracked
  under the existing `TD-BE-002`.
- **ESLint**: 0 errors, 50 warnings (down from 51), tracked under `TD-FE-010` / `TD-I18N-002`.

No blocking SAST finding remains without a disposition.

## Technical Debt First Action

Reviewed `technical-debt-index.yaml`; selected `TD-I18N-002` and `TD-FE-010` (both explicitly named
in this backlog item's mandatory scope). Materially reduced both via a real fix in
`ComplianceEvidenceScreen.tsx`. During the same validation, a materially larger defect was found
and **fully closed**: `TD-DB-005` — COM-MOD-013's backend was silently persisting to an in-memory
map instead of PostgreSQL, due to a missing `schema.sql` registration compounded by inverted
`@Profile` wiring on 4 JDBC/in-memory repository pairs. See the QA validation evidence for full
root-cause detail.

## New Technical Debt

- **`TD-IAM-004`** (open): 5 controllers assign a synthetic random `TenantId` instead of the
  authenticated request's real tenant. Access control (deny-by-default authorization) is
  unaffected; the gap is data attribution. Deferred because a correct fix requires a Spring
  Modulith module-boundary decision beyond safe scope for a validation-only backlog item.

## Accepted Risks

| Finding | Severity | Reason |
|---|---|---|
| SpotBugs `DE_MIGHT_IGNORE` × 3 | Medium | Intentional best-effort side-effect swallow; primary record save is never blocked; matches an existing codebase convention |

## Decision

**Approved.** Ready for `COM-MOD-013-CLOSEOUT`.
