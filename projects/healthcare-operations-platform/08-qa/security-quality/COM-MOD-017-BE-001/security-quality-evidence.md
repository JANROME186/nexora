# COM-MOD-017-BE-001 Security Quality Evidence

- **Backlog Item**: COM-MOD-017-BE-001
- **Module**: COM-MOD-017 Product Marketplace and Extension Packaging
- **Date**: 2026-07-24
- **Status**: Validated

## Checks Performed

| Check | Result |
|---|---|
| OWASP Dependency-Check (72 dependencies, online NVD-mirrored DB) | 0 vulnerabilities |
| Trivy fs scan (vuln/secret/misconfig, all severities, backend source) | 0 findings |
| Secrets scan | 0 secrets |
| Checkstyle | 0 findings in new module (73 pre-existing elsewhere, unrelated) |
| PMD | 0 findings beyond the existing accepted baseline pattern (590 pre-existing repo-wide) |
| SpotBugs | 0 findings in new module — 2 real Low-severity `IMPROPER_UNICODE` findings introduced by this item were found and fixed before closure (63 pre-existing elsewhere, unrelated) |
| Agent-agnostic scan | 0 vendor-specific references |
| IAM per-action authorization | 4 new `PermissionCode` screens + `MARKETPLACE_OPERATOR`/`TENANT_ADMIN` roles registered, deny-by-default preserved |
| Billing boundary isolation (INV-MKT-003) | Verified — `BillingEventRecord` is observability-only, never a source of truth for entitlement/license/clinical state |

## SpotBugs Findings Fixed

1. `LocalDeterministicBillingAdapter.submitBillingEvent` used `equalsIgnoreCase`/`toUpperCase(Locale.ROOT)` for the provider-outage marker and currency comparison — flagged `IMPROPER_UNICODE` (case folding can misbehave for non-ASCII scripts). Fixed by requiring exact-case canonical (ISO-4217 / literal-marker) input instead of locale-sensitive case folding.
2. `BillingAdapterExceptionHandler` duplicated `MarketplaceExceptionHandler`'s `messageKeyFor` case-folding logic inline — flagged `IMPROPER_UNICODE`. Fixed by making `MarketplaceExceptionHandler.messageKeyFor` public and reusing it, removing the duplicate expression entirely.

## Technical Debt Registered

**TD-BE-018** — the deeper sophistication named by `generation-plan.yaml`'s `custom_implementation_points` (full entitlement-policy.yaml evaluation order, all 9 compatibility dimensions, a mature billing adapter with retry/idempotency, a multi-step installation rollback audit trail, and runtime feature-availability integration with IAM/menu generation) remains basic-level only. Not blocking — every generated endpoint is functional today. Targeted at a future COM-MOD-017-BE-002, matching the BE-001/BE-002 maturation precedent already used for MVP-MOD-005's fiscal adapter and MVP-MOD-008's integration adapter.
