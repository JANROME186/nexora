---
id: COM-MOD-017-FE-001-summary
status: closed
backlog_item: COM-MOD-017-FE-001
next_backlog_item: COM-MOD-017-QA-001
created_date: 2026-07-25
---

# COM-MOD-017-FE-001 Summary

## Status
Closed.

## Cambios Clave
Compiled all 4 marketplace employee-portal screens named in BCM-PLT-011's `ui-model.md`
`employee_portal.screens`: `MarketplacePackagesScreen.tsx` (package catalog admin -- list,
submit, publish/certify/retire a version, view a specific version), `MarketplaceOffersScreen.tsx`
(commercial offers -- list optionally filtered by `packageId`, publish, accept for a tenant),
`MarketplaceEntitlementsScreen.tsx` (tenant-scoped entitlements -- list, grant, revoke behind a
`ConfirmDialog`) and `MarketplaceInstallationsScreen.tsx` (tenant-scoped installations -- list,
install, activate, suspend, uninstall, upgrade, rollback). New typed `marketplaceApi.ts` facade
(20 functions, one per backend endpoint) over `PackageCatalogController`/`CommercialOfferController`/
`TenantEntitlementController`/`PackageInstallationController`, with request/response types added
to `api/types.ts` using field names copied verbatim from the backend controller records. Wired
the 4 new `ScreenKey`/`PermissionCode`/`SCREEN_TO_PERMISSION` entries and `MARKETPLACE_OPERATOR`/
`TENANT_ADMIN` `RoleCode` entries into `state/permissions.ts` (confirmed to match
`identityaccess.domain.PermissionCode.java`/`RolePermissionCatalog.java` exactly), `AppShell.tsx`
(`SCREEN_TAB_LABEL_KEYS`) and `App.tsx` (`SCREEN_COMPONENTS`). Added complete `marketplace.*`
i18n message catalogs (`shared`, `packages`, `offers`, `entitlements`, `installations`) plus 4
new `appShell.tabs.*` entries to both `es-MX.ts` and `en-US.ts` with real Spanish/English text.
Updated `AppSmoke.test.tsx`/`SessionContext.test.tsx`'s ADMIN tab-count assertions from 49 to 53.

## Deuda Técnica
- **TD-BE-019 (closed, for real)**: `MarketplaceInstallationsScreen`'s "Install package" control
  loads the tenant's real entitlements via `marketplaceApi.listTenantEntitlements` and only
  enables install when an entitlement matching the typed `packageId` is effectively active
  (`status === "active"` and, if set, `expiresAt` in the future -- mirroring the backend's own
  `TenantEntitlement.isEffectivelyActive`). Otherwise the control is disabled with a localized
  explanatory status. This is a genuine runtime-state-gated UI decision, not a fabricated
  cross-capability relationship; verified by dedicated tests asserting both directions.
- **TD-FE-012 (new, open, non-blocking)**: `npm audit` surfaced 17 pre-existing high-severity
  findings, all newly-matched GitHub Advisory Database entries against already-installed
  devDependency versions with zero dependency changes attributable to this item. A non-breaking
  `npm audit fix` reduced it to 10, all confined to transitive devDependencies requiring a
  breaking `eslint-plugin-jsx-a11y` downgrade shared across every HOP frontend stack -- out of
  this item's scope. `npm audit --omit=dev` confirms 0 production-dependency vulnerabilities.

## Validation
| Gate | Result |
|---|---|
| `npm run typecheck` | 0 TypeScript errors |
| `npm run lint` | 0 errors, 55 non-blocking warnings (dispositioned under TD-FE-010/TD-I18N-002) |
| `npm run test:coverage` | 224 tests, 65 test files, 0 failures |
| Employee-portal line coverage | 90.68% (floor 89.75%, no regression) |
| `npm run build` | Production bundle built cleanly |
| `npm run duplication` | 0 duplicated blocks |
| `npm run format:check` | Clean (after `npm run format:write`) |
| `npm run license:check` | MIT 5, UNLICENSED 1 |
| `npm audit --audit-level=low` | 17 pre-existing findings found; non-breaking fix reduced to 10 (all devDependency-only); registered TD-FE-012 |
| `npm audit --omit=dev --audit-level=low` | 0 vulnerabilities (production dependencies clean) |
| Trivy fs (vuln/secret/misconfig, all severities) | 0 findings |
| Agent-agnostic scan | 0 vendor/agent hits |
| `git diff --check` | 0 whitespace errors |

## Siguiente Paso
Run `COM-MOD-017-QA-001` (integrated marketplace validation): backend REST contracts vs.
`openapi-source.md`, IAM `PermissionCode`s/`RolePermissionCatalog.java` vs. `permissions.ts`/
`ROLE_PERMISSION_CATALOG`, `ui-model.md` screens vs. the 4 marketplace employee-portal screens,
and es-MX/en-US i18n key parity. `COM-MOD-017-WEB-001` (public marketplace listing surface)
remains a separate, not-yet-scheduled backlog item.
