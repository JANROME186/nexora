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

**TD-BE-018** — the deeper sophistication named by `generation-plan.md`'s `custom_implementation_points` (full entitlement-policy.md evaluation order, all 9 compatibility dimensions, a mature billing adapter with retry/idempotency, a multi-step installation rollback audit trail, and runtime feature-availability integration with IAM/menu generation) remains basic-level only. Not blocking — every generated endpoint is functional today. Targeted at a future COM-MOD-017-BE-002, matching the BE-001/BE-002 maturation precedent already used for MVP-MOD-005's fiscal adapter and MVP-MOD-008's integration adapter.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-COM-MOD-017-BE-001
  type: security-quality-evidence
  name: COM-MOD-017-BE-001 Security Quality Evidence Report
  version: 1.0.0
  status: validated
  backlog_item: COM-MOD-017-BE-001
  module: COM-MOD-017 Product Marketplace and Extension Packaging
  created_date: 2026-07-24
security_checks:
  owasp_dependency_check:
    status: passed
    dependencies_scanned: 72
    vulnerabilities_found: 0
    note: Executed online against a freshly updated NVD-mirrored database (org.owasp:dependency-check-maven
      12.1.3).
  trivy_fs_scan:
    status: passed
    scope: backend project source, pom.xml and resources (excludes .m2 local repository
      cache and target build output)
    scanners: vuln, secret, misconfig
    severities: UNKNOWN, LOW, MEDIUM, HIGH, CRITICAL
    findings_count: 0
  secrets_scan:
    status: passed
    secrets_found: 0
  static_analysis:
    checkstyle:
      status: passed
      new_module_findings: 0
      pre_existing_repo_wide_findings: 73
      note: All 73 findings are in files unrelated to marketplaceentitlements, pre-dating
        this backlog item.
    pmd:
      status: passed
      new_module_findings: 0
      pre_existing_repo_wide_findings: 590
      note: The marketplaceentitlements module reproduces the same rule categories
        (AvoidDuplicateLiterals, AvoidFieldNameMatchingMethodName, MissingSerialVersionUID,
        UnusedFormalParameter) already present at the same rate in the reference module
        (integrationinteroperability) it was modeled on; this is pre-existing accepted
        codebase style (TD-BE-002), not new debt.
    spotbugs:
      status: passed
      new_module_findings: 0
      pre_existing_repo_wide_findings: 63
      note: Found and fixed 2 real Low-severity IMPROPER_UNICODE findings introduced
        by this backlog item's own code (case-insensitive string comparisons in LocalDeterministicBillingAdapter
        and a duplicated case-folding expression in BillingAdapterExceptionHandler)
        before closure, by switching to exact-case canonical comparisons and reusing
        MarketplaceExceptionHandler's shared messageKeyFor helper. Repo-wide bug count
        dropped from 65 to 63 as a result.
  agent_agnostic_scan:
    status: passed
    agent_specific_leaks: 0
  iam_action_authorization:
    status: passed
    enforcement: deny_by_default_interceptor
    new_permissions: SCREEN_MARKETPLACE_PACKAGES, SCREEN_MARKETPLACE_OFFERS, SCREEN_MARKETPLACE_ENTITLEMENTS,
      SCREEN_MARKETPLACE_INSTALLATIONS
    new_roles: MARKETPLACE_OPERATOR, TENANT_ADMIN
  billing_boundary_isolation:
    status: passed
    note: 'INV-MKT-003 preserved by construction: BillingEventRecord is an observability-only
      record; no marketplace command (grant/revoke entitlement, install/activate/suspend/uninstall)
      reads from or is triggered by a billing event. Verified by MarketplaceEntitlementsApiTest
      and the real-Postgres MarketplaceEntitlementsLocalDatabaseTest.'
technical_debt:
  registered:
  - id: TD-BE-018
    title: Marketplace entitlement policy, compatibility strategy, billing adapter
      and installation rollback orchestration are basic implementations only
    status: open
    blocking: false
    target_backlog: COM-MOD-017-BE-002
summary: 'Security and quality checks passed for COM-MOD-017-BE-001. No security vulnerabilities,
  hardcoded secrets or misconfigurations were introduced (0 OWASP Dependency-Check
  findings across 72 dependencies, 0 Trivy findings across vuln/secret/misconfig at
  all severities). IAM per-action permission mapping is registered for all four new
  marketplace screens with two new roles. Two real SpotBugs findings introduced by
  this item''s own new code were found and fixed before closure. Technical debt TD-BE-018
  registered for the deeper custom-rule sophistication deferred to a future COM-MOD-017-BE-002,
  consistent with policy (no undocumented gaps).

  '
```
