# COM-MOD-017-FE-001 Security and Quality Evidence

**Status**: validated · **Backlog Item**: COM-MOD-017-FE-001 · **Captured on**: 2026-07-25

---

## 1. Scope

Employee portal (`07-implementation/employee-portal/`) Product Marketplace and Entitlements administration screens (BCM-PLT-011): package catalog admin, commercial offers, tenant entitlements and package installations, plus a thin typed API facade (`marketplaceApi.ts`), IAM permission updates and i18n catalogs.

- **Local Toolchain Inventory Loaded**: `true` (`03-architecture/technology-architecture/local-toolchain-inventory.md`)

---

## 2. Open-Source-First Check

No new production or development dependencies were added for `COM-MOD-017-FE-001`. All existing dev tooling and runtime libraries remain open-source and licensed (MIT 5, UNLICENSED 1 project package). `package.json` was not modified; `package-lock.json` was updated only by running the non-breaking `npm audit fix` (see Section 5), which does not add any new dependency, only bumps existing transitive devDependency versions within their already-declared ranges.

---

## 3. Security Controls

- **IAM-Gated Navigation**: All 4 new screens are gated behind specific `PermissionCode` values (`SCREEN_MARKETPLACE_PACKAGES`, `SCREEN_MARKETPLACE_OFFERS`, `SCREEN_MARKETPLACE_ENTITLEMENTS`, `SCREEN_MARKETPLACE_INSTALLATIONS`) mapped 1:1 in `SCREEN_TO_PERMISSION`, confirmed to match `identityaccess.domain.PermissionCode.java` exactly. Unpermitted navigation tabs are hidden dynamically from non-privileged roles (verified: `FRONT_DESK` role sees 10 tabs unaffected by the 4 new marketplace tabs).
- **MARKETPLACE_OPERATOR / TENANT_ADMIN Roles**: Added to `permissions.ts`, mirroring `identityaccess/domain/RolePermissionCatalog.java`'s role -> permission assignments exactly (`MARKETPLACE_OPERATOR` -> packages/offers; `TENANT_ADMIN` -> offers/entitlements/installations). `ADMIN` continues to receive every permission automatically via `PERMISSION_CODES`.
- **Tenant Scoping**: Entitlements and installations are tenant-scoped (`{tenantId}` path segment); the corresponding screens disable their action buttons when `AdminScope.tenantId` is empty (same pattern as every other tenant-scoped screen). Packages and offers correctly have no tenant scoping (global catalog, matching the backend controllers' request mappings, which carry no `{tenantId}` segment).
- **Destructive Actions Confirmation**: The sensitive tenant entitlement revoke action requires explicit user confirmation via a blocking `ConfirmDialog` before the API call is dispatched (same pattern as `QualityEventIntakeScreen`'s `linkQualityEvent`).
- **Real Entitlement-Gated Runtime Decision (closes TD-BE-019)**: `MarketplaceInstallationsScreen`'s "Install package" control loads the tenant's real entitlements from the real backend (`TenantEntitlementController` via `marketplaceApi.listTenantEntitlements`) and only enables install when an entitlement matching the typed `packageId` is effectively active (mirrors the backend's `TenantEntitlement.isEffectivelyActive`: status `active` and, if set, `expiresAt` in the future). This is a genuine runtime-state-gated UI decision, not a fabricated relationship, and is covered by dedicated tests asserting both directions.
- **Message Externalization**: 0 hardcoded UI strings added; all 4 new screens use namespaced `es-MX`/`en-US` message groups (`t.marketplace.*`, `t.appShell.tabs.*`).
- **XSS Posture**: All user-supplied text fields (package code/name/category, offer codes, revoke reasons, actor IDs) are rendered safely using React's default JSX text-node escaping. No `dangerouslySetInnerHTML` is used anywhere in the new code.

---

## 4. Evidence Commands and Results

| Check | Command | Result |
|---|---|---|
| Frontend Typecheck | `npm run typecheck` | 0 TypeScript errors |
| Frontend Test + Coverage | `npm run test:coverage` | 224 tests, 65 files, 0 failures; 90.68% employee-portal line coverage, above the previous 89.75% floor |
| Frontend Lint | `npm run lint` | 0 errors; 55 non-blocking warnings (51 pre-existing + 4 new, same `TD-FE-010`/`TD-I18N-002` disposition every other module follows) |
| Frontend Duplication | `npm run duplication` | passed (0 duplicate code blocks) |
| Frontend Format | `npm run format:check` | passed (Prettier code style clean) |
| Frontend License | `npm run license:check` | passed (MIT 5, UNLICENSED 1) |
| Frontend npm audit (all) | `npm audit --audit-level=low` | 17 pre-existing high-severity devDependency-only findings found (unrelated to this item's diff); `npm audit fix` (non-breaking) reduced to 10, all requiring a breaking `eslint-plugin-jsx-a11y` downgrade shared repo-wide -- out of scope; registered as `TD-FE-012` |
| Frontend npm audit (production only) | `npm audit --omit=dev --audit-level=low` | **0 vulnerabilities** (production dependencies: react, react-dom only) |
| Frontend Trivy fs scan | `trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL --skip-dirs node_modules,dist,coverage .` | 0 vulnerabilities, 0 secrets, 0 misconfigurations |
| i18n Key Parity | TypeScript `MessageCatalog` literal-widening check against `en-US.ts` | 0 errors (complete `marketplace.*` key parity) |
| Agent-Agnostic Scan | Case-insensitive grep for vendor/agent patterns over all new/touched files | 0 real source code hits |
| Secrets Scan | Trivy secret scanner (part of the fs scan above) | 0 findings |
| git diff --check | `git diff --check` | 0 whitespace errors |

---

## 5. npm audit Residual Finding Detail (TD-FE-012)

`npm audit --audit-level=low` initially reported 17 high-severity findings, all newly-surfaced GitHub Advisory Database entries against already-installed devDependency versions (advisories are matched live against the registry at audit time, not pinned to the lockfile snapshot) -- a drift since `COM-MOD-013-FE-001`'s clean 0-vulnerability baseline (2026-07-23) with zero dependency changes made by this or any intervening backlog item. `package.json`/`package-lock.json` were confirmed untouched by this item's own diff prior to remediation.

Ran `npm audit fix` (non-breaking, does not alter `package.json`): resolved 7 of 17 (the `postcss` Path Traversal finding and the `@typescript-eslint/*` transitive chain), confirmed with a full re-run of `npm run quality` (typecheck, lint, test:coverage, build all still pass identically). The remaining 10 are all confined to transitive devDependencies (`eslint-plugin-jsx-a11y`, `eslint-plugin-react`, `glob` -> `read-package-json` -> `read-installed-packages` -> `license-checker-rseidelsohn`, `test-exclude` -> `@vitest/coverage-v8`) and require `npm audit fix --force`, which would downgrade `eslint-plugin-jsx-a11y` to `6.4.1` -- a breaking change to a lint plugin shared across every HOP frontend stack (employee-portal, public-website, patient-portal, doctor-portal, mobile-app), not scoped to this module's marketplace UI work. `npm audit --omit=dev --audit-level=low` confirms production dependencies (react, react-dom) are clean. Registered as new debt `TD-FE-012` (open, non-blocking, targeted at a future dedicated devDependency-maintenance backlog item).

---

## 6. Technical Debt & Closure

- **Closed Debt**: `TD-BE-019` closed for real (see Section 3, "Real Entitlement-Gated Runtime Decision").
- **New Debt Registered**: `TD-FE-012` (open, non-blocking) -- see Section 5.
- **Ready for Next Backlog Item**: `COM-MOD-017-QA-001`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-COM-MOD-017-FE-001
  type: security-quality-evidence
  name: COM-MOD-017-FE-001 Security and Quality Evidence
  version: 1.0.0
  status: validated
  backlog_item: COM-MOD-017-FE-001
  captured_on: 2026-07-25
scope: 'Employee portal (07-implementation/employee-portal/) Product Marketplace and
  Entitlements administration screens (BCM-PLT-011): package catalog admin, commercial
  offers, tenant entitlements and package installations, plus a thin typed API facade
  (marketplaceApi.ts), IAM permission updates and i18n catalogs.'
local_toolchain_inventory_loaded: true
open_source_first_check:
  new_dependency_added: false
  stack_reviewed: React 18, TypeScript 5, Vite 6, Vitest, ESLint, jscpd, Prettier,
    npm audit, Trivy
  vulnerabilities_found_production: 0
  license_check: passed
  notes: No new production or development dependencies were added for COM-MOD-017-FE-001.
    package.json was not modified; package-lock.json was updated only by the non-breaking
    npm audit fix described below.
security_controls:
  authentication_and_authorization:
    screens_gated_by_iam: All 4 new screens are gated behind specific PermissionCode
      values (SCREEN_MARKETPLACE_PACKAGES, SCREEN_MARKETPLACE_OFFERS, SCREEN_MARKETPLACE_ENTITLEMENTS,
      SCREEN_MARKETPLACE_INSTALLATIONS) mapped 1:1 in SCREEN_TO_PERMISSION, confirmed
      to match identityaccess.domain.PermissionCode.java exactly.
    role_assignment: Added MARKETPLACE_OPERATOR and TENANT_ADMIN roles in permissions.ts,
      mirroring identityaccess/domain/RolePermissionCatalog.java exactly.
    tenant_scoping: Entitlements and installations are tenant-scoped; action buttons
      disable when AdminScope.tenantId is empty. Packages/offers correctly have no
      tenant scoping, matching the backend controllers' request mappings.
  entitlement_gated_runtime_decision:
    closes: TD-BE-019
    mechanism: MarketplaceInstallationsScreen's install control loads real tenant
      entitlements via marketplaceApi.listTenantEntitlements and only enables install
      when an entitlement for the typed packageId is effectively active (mirrors
      TenantEntitlement.isEffectivelyActive). Not a fabricated relationship.
    verification: Dedicated isPackageEntitled unit tests plus MarketplaceInstallationsScreen.test.tsx
      assert both directions (entitled -> enabled; not entitled -> disabled with
      localized status).
  destructive_actions_confirmation:
    confirm_dialog_enforcement: The tenant entitlement revoke action requires explicit
      user confirmation via a blocking ConfirmDialog before the API call is dispatched.
  message_externalization:
    hardcoded_ui_strings_added: 0
    locales_covered:
    - es-MX
    - en-US
    namespaced_keys: t.marketplace.*, t.appShell.tabs.*
  xss_posture: All user-supplied text fields are rendered safely using React's default
    JSX text-node escaping. No dangerouslySetInnerHTML is used.
evidence_commands:
  frontend_typecheck:
    command: npm run typecheck
    result: 0 TypeScript errors
  frontend_test_and_coverage:
    command: npm run test:coverage
    result: 224 tests, 65 test files, 0 failures; 90.68% employee-portal line coverage
      (previous floor 89.75%)
  frontend_lint:
    command: npm run lint
    result: 0 errors, 55 non-blocking warnings; dispositioned under TD-FE-010/TD-I18N-002
  frontend_duplication:
    command: npm run duplication
    result: passed (0 duplicate code blocks)
  frontend_format_check:
    command: npm run format:check
    result: passed (Prettier code style clean)
  frontend_license_check:
    command: npm run license:check
    result: passed (MIT 5, UNLICENSED 1)
  frontend_npm_audit_all:
    command: npm audit --audit-level=low
    vulnerabilities_before_fix: 17
    vulnerabilities_after_non_breaking_fix: 10
    note: All 10 residual findings confined to transitive devDependencies, requiring
      a breaking eslint-plugin-jsx-a11y downgrade shared repo-wide; registered as
      TD-FE-012.
  frontend_npm_audit_production_only:
    command: npm audit --omit=dev --audit-level=low
    vulnerabilities: 0
  frontend_trivy_filesystem_scan:
    command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
      --skip-dirs node_modules,dist,coverage .
    version: 0.72.0
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
  i18n_key_parity:
    method: TypeScript MessageCatalog literal-widening check (es-MX.ts source of truth,
      en-US.ts typed against it)
    errors: 0
  agent_agnostic_scan:
    method: case-insensitive grep for vendor/agent keywords over all new/touched files
    real_source_code_hits: 0
  secrets_scan:
    tool: Trivy secret scanner
    findings: 0
  git_diff_check:
    command: git diff --check
    result: 0 whitespace errors
closure:
  vulnerabilities_fixed:
  - postcss Path Traversal in Previous Source Map Auto-Loading (via non-breaking
    npm audit fix)
  - '@typescript-eslint/* transitive minimatch/brace-expansion chain (via non-breaking
    npm audit fix)'
  technical_debt_closed:
  - id: TD-BE-019
    contribution: Real entitlement-gated install control implemented and verified;
      both acceptance criteria satisfied without a fabricated cross-capability relationship.
  technical_debt_remediated: []
  real_defects_fixed: []
  new_debt_registered:
  - id: TD-FE-012
    contribution: 10 residual npm audit high-severity findings confined to transitive
      devDependencies, requiring a cross-stack breaking-change fix out of this item's
      scope; production dependencies confirmed clean.
  status: closed
  next_backlog_item: COM-MOD-017-QA-001
```
