---
id: COM-MOD-017-BE-002-summary
status: closed
backlog_item: COM-MOD-017-BE-002
next_backlog_item: COM-MOD-017-FE-001
created_date: 2026-07-24
---

# COM-MOD-017-BE-002 Summary

## Status
Closed.

## Cambios Clave
Implemented 4 of TD-BE-018's 5 marketplace custom_implementation_points. `EntitlementPolicyEvaluator`
now runs the full entitlement-policy.md `evaluation_order` (tenant_status/package_status/license_status/
compatibility_status/iam_permission/feature_flag/clinical_safety_control/usage_limit) via a
policy-decision-point design (caller-resolved `permissionGranted`/`featureFlagEnabled` facts, not
cross-module port pulls, to keep `marketplaceentitlements` acyclic — `PlatformFoundationModulithTest`
confirms this). `CompatibilityEvaluator` evaluates all 9 compatibility.md dimensions via a new
`CompatibilityMetadata` delimited-text parser on `PackageVersion.compatibilityMetadataText`. The
billing adapter boundary (`BillingAdapterPort.retrySubmission`) gained retry/idempotency keyed on
`providerReference` (INV-MKT-003 preserved). `packageinstallation` gained a persisted multi-step
`InstallationStep` audit trail that `rollbackPackage` now derives its target version from. New
`TenantEntitlement.usageLimit` field, 6 new `MarketplaceErrorCodes`/`EntitlementDecision` constants,
6 new `marketplace.error.*` i18n keys (default/es-MX/en-US), schema additions (`tenant_entitlements.usage_limit`,
`package_versions.compatibility_metadata_text`, `billing_event_records.retry_count` + unique
idempotency index, new `installation_steps` table).

TD-BE-018's 5th point (runtime feature-availability → IAM/employee-portal menu) was investigated:
closing it requires 4 real marketplace employee-portal screens that don't exist yet (`App.tsx`'s
`SCREEN_COMPONENTS` map is a strict 1:1 `ScreenKey`→component binding — adding the screen keys
without the screens breaks the frontend build), which is `COM-MOD-017-FE-001` scope, not backend.
Repointed to new **TD-BE-019** rather than forced into a fabricated or build-breaking fix. TD-BE-018
updated `open` → `materially_reduced`.

While validating, found and fixed a real pre-existing infrastructure defect (**TD-BE-020**, closed):
`application.properties` unconditionally excluded `DataSourceAutoConfiguration` for every profile —
a regression from NXF-FMT-002's YAML→properties migration — silently breaking every `@Profile("local")`
JDBC adapter and `LocalDatabaseTest` across the *entire* backend (not just marketplace). Fixed via
an override in `application-local.properties`. Also closed **TD-QA-008** (stale OWASP ZAP toolchain
documentation), an unrelated non-blocking item, per task instructions to reduce at least one debt
item.

## Validation
| Gate | Result |
|---|---|
| `mvn -Pquality -Dhop.local-db-tests=true clean verify` (fresh Docker Postgres volume) | 484 tests, 0 failures/errors/skipped |
| Backend line coverage | 84.65% (floor 84.53%, no regression) |
| Spring Modulith boundary | `PlatformFoundationModulithTest` passed — confirms no dependency cycle |
| Checkstyle / PMD / SpotBugs | 0 findings in touched files (pre-existing repo-wide baselines unaffected) |
| Trivy fs (vuln/secret/misconfig, all severities) | 0 findings |
| OWASP Dependency-Check | Blocked offline (documented pre-existing environment constraint) |

## Deuda Técnica
- TD-BE-018: `materially_reduced` (4/5 acceptance criteria closed).
- TD-BE-019 (new, open, non-blocking): runtime feature-availability → IAM/menu wiring, targeted at `COM-MOD-017-FE-001`.
- TD-BE-020 (new, closed): DataSourceAutoConfiguration regression, fixed.
- TD-QA-008 (closed): stale ZAP toolchain documentation.

## Siguiente Paso
Run `COM-MOD-017-FE-001` (Compile marketplace administration and package installation UI outputs).
Wire the already-registered `SCREEN_MARKETPLACE_PACKAGES/OFFERS/ENTITLEMENTS/INSTALLATIONS`
`PermissionCode`s and `MARKETPLACE_OPERATOR`/`TENANT_ADMIN` roles into `employee-portal/src/state/permissions.ts`,
`AppShell.tsx` and `App.tsx`'s `SCREEN_COMPONENTS` map alongside the 4 new screens, and close
**TD-BE-019** as this item's debt-first action. `COM-MOD-017-QA-001` (integrated marketplace
validation) follows FE-001.
