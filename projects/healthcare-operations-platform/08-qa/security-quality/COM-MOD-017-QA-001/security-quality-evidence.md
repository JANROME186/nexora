# COM-MOD-017-QA-001 Security and Quality Evidence

**Status**: validated · **Backlog Item**: COM-MOD-017-QA-001 · **Captured on**: 2026-07-25

---

## 1. Scope

Integrated security and quality re-verification of BCM-PLT-011 (Product Marketplace and Entitlements) across the backend `marketplaceentitlements` module (`07-implementation/backend`) and the 4 employee-portal marketplace administration screens (`07-implementation/employee-portal`). This is a validation-only backlog item: no production Java or TypeScript source was modified. Six capability-package model documents were corrected to match the already-shipped, already-tested implementation (see `COM-MOD-017-QA-001-validation.md` Section 2), and one stale technical-debt registry entry was corrected (Section 3 there).

- **Local Toolchain Inventory Loaded**: `true` (`03-architecture/technology-architecture/local-toolchain-inventory.md`)

---

## 2. Open-Source-First Check

No new production or development dependencies were added or changed by this item. `package.json`/`package-lock.json` (employee-portal) and `pom.xml` (backend) are byte-for-byte unchanged from `COM-MOD-017-FE-001`/`COM-MOD-017-BE-002`.

---

## 3. Security Controls Re-Verified

- **Backend request-time authorization**: `EndpointPermissionRegistry` maps all 6 marketplace controllers' paths to the correct `SCREEN_MARKETPLACE_*` `PermissionCode` and `BCM-PLT-011` capability tag, including path-variable-bearing prefixes (`/entitlements/{tenantId}`, `/installations/{tenantId}`) — confirmed via direct inspection of `EndpointPermissionRegistry.java`, not just its test file.
- **Frontend IAM-gated navigation**: all 4 marketplace screens remain gated behind their `PermissionCode`, mapped 1:1 in `SCREEN_TO_PERMISSION`, byte-for-byte identical between `RolePermissionCatalog.java` (backend) and `permissions.ts` (frontend) for `MARKETPLACE_OPERATOR`/`TENANT_ADMIN`.
- **Entitlement-gated runtime decision (`TD-BE-019`) re-verified, not re-implemented**: `MarketplaceInstallationsScreen`'s install control still genuinely gates on live tenant entitlement state from `TenantEntitlementController` via `marketplaceApi.listTenantEntitlements`/`isPackageEntitled`, covered by dedicated tests in both directions. This item confirmed the closure was real and used it as the basis for closing the parent `TD-BE-018` (see validation evidence Section 3).
- **Permission-model documentation gap closed**: `permissions.md`/`ui-model.md` previously implied a 15-code fine-grained enforcement model that does not exist at runtime. Left uncorrected, this could mislead a future reviewer into assuming finer-grained server-side checks exist than actually do. Both documents now accurately describe the coarse, 4-code `SCREEN_MARKETPLACE_*` enforcement that is actually in effect, cross-referenced to `TD-IAM-002`.
- **XSS/input-handling posture**: unchanged from `COM-MOD-017-FE-001` (no source touched) — all user-supplied text continues to render via React's default JSX escaping, no `dangerouslySetInnerHTML`.

---

## 4. Evidence Commands and Results

| Check | Command | Result |
|---|---|---|
| Backend tests (incl. local Postgres) | `mvn -Pquality "-Dhop.local-db-tests=true" clean verify` | 484 tests, 0 failures/errors/skipped |
| Backend coverage | JaCoCo (bundled in the command above) | 84.65% line coverage (floor 84.65%, no regression) |
| Backend static analysis | `mvn -Pquality checkstyle:checkstyle pmd:pmd pmd:cpd spotbugs:spotbugs cyclonedx:makeAggregateBom duplicate-finder:check` | Checkstyle 74 (0 marketplace), PMD 597 (27 marketplace, all pre-existing/unchanged), SpotBugs 63 (0 marketplace), CPD 0, duplicate-finder passed |
| Backend dependency vulnerability scan | `mvn -Pquality org.owasp:dependency-check-maven:check -DautoUpdate=false` | 72 dependencies scanned, **0 vulnerabilities** |
| Frontend typecheck | `npm run typecheck` | 0 TypeScript errors |
| Frontend test + coverage | `npm run test:coverage` | 224 tests, 65 files, 0 failures; 90.68% line coverage (floor 89.75%, no regression) |
| Frontend lint | `npm run lint` | 0 errors, 55 non-blocking warnings (identical to `FE-001`'s baseline) |
| Frontend duplication | `npm run duplication` | passed (0 duplicate blocks) |
| Frontend format | `npm run format:check` | passed |
| Frontend license | `npm run license:check` | passed (MIT 5, UNLICENSED 1) |
| Frontend npm audit (all) | `npm audit --audit-level=low` | 10 pre-existing high-severity findings, all transitive devDependencies (`TD-FE-012`, re-confirmed non-fixable without a breaking change) |
| Frontend npm audit (production only) | `npm audit --omit=dev --audit-level=low` | **0 vulnerabilities** |
| Trivy filesystem scan | `trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL --skip-dirs "**/node_modules,**/target,**/dist,**/build" .` | 0 vulnerabilities, 0 secrets, 0 misconfigurations |
| Secrets scan | Trivy secret scanner (part of the fs scan above) | 0 findings |
| Agent-Agnostic Scan | Case-insensitive grep for vendor/agent patterns over all touched files | 0 hits |
| Markdown/frontmatter parse | Same `extract_structured_payload`/PyYAML logic `backlog_validator.py` uses, applied to every touched file | 0 errors |
| `git diff --check` | `git diff --check` | 0 whitespace errors |

---

## 5. Technical Debt & Closure

- **Closed Debt**: `TD-BE-018` (see validation evidence Section 3 — all 5 of 5 named custom_implementation_points now closed; the item's own status had drifted stale and was corrected).
- **Re-Confirmed Open, Non-Blocking**: `TD-FE-012` (no non-breaking fix available; re-checked, disposition unchanged).
- **New Debt Registered**: none.
- **Ready for Next Backlog Item**: `COM-MOD-017-CLOSEOUT`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-COM-MOD-017-QA-001
  type: security-quality-evidence
  status: validated
  backlog_item: COM-MOD-017-QA-001
  captured_on: 2026-07-25
scope: 'Integrated security and quality re-verification of BCM-PLT-011 across the
  backend marketplaceentitlements module and the 4 employee-portal marketplace screens.
  Validation-only item: no production source modified; 6 capability-package model
  documents corrected to match the shipped implementation.'
local_toolchain_inventory_loaded: true
open_source_first_check:
  new_dependency_added: false
  notes: package.json/package-lock.json and pom.xml unchanged from COM-MOD-017-FE-001/COM-MOD-017-BE-002.
security_controls:
  backend_request_time_authorization:
    method: EndpointPermissionRegistry.java direct inspection
    result: all_6_marketplace_controllers_correctly_mapped_including_path_variable_prefixes
  frontend_iam_gated_navigation:
    result: 4_screens_gated_role_grants_byte_for_byte_identical_backend_frontend
  entitlement_gated_runtime_decision:
    item: TD-BE-019
    result: re_verified_not_re_implemented_genuine_runtime_state_gate_confirmed
  permission_model_documentation_gap:
    result: closed_permissions_md_and_ui_model_md_now_accurately_describe_the_coarse_4_code_enforcement_actually_in_effect
    cross_reference: TD-IAM-002
  xss_input_handling_posture:
    result: unchanged_react_default_escaping_no_dangerouslySetInnerHTML
evidence_commands:
  backend_tests:
    command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
    result: 484 tests, 0 failures, 0 errors, 0 skipped
  backend_coverage:
    tool: JaCoCo
    line_coverage_percent: 84.65
    previous_baseline_percent: 84.65
    coverage_regression: false
  backend_static_analysis:
    command: mvn -Pquality checkstyle:checkstyle pmd:pmd pmd:cpd spotbugs:spotbugs
      cyclonedx:makeAggregateBom duplicate-finder:check
    checkstyle_total: 74
    checkstyle_marketplace: 0
    pmd_total: 597
    pmd_marketplace_pre_existing: 27
    pmd_new: 0
    spotbugs_total: 63
    spotbugs_marketplace: 0
    cpd_duplications: 0
    duplicate_finder: passed
  backend_dependency_check:
    command: mvn -Pquality org.owasp:dependency-check-maven:check -DautoUpdate=false
    dependencies_scanned: 72
    vulnerabilities: 0
  frontend_typecheck:
    command: npm run typecheck
    result: 0 errors
  frontend_test_and_coverage:
    command: npm run test:coverage
    result: 224 tests, 65 files, 0 failures, 90.68% coverage (floor 89.75%)
  frontend_lint:
    command: npm run lint
    result: 0 errors, 55 warnings (unchanged from FE-001)
  frontend_npm_audit_all:
    command: npm audit --audit-level=low
    vulnerabilities_found: 10
    disposition: TD-FE-012_reconfirmed_non_blocking
  frontend_npm_audit_production_only:
    command: npm audit --omit=dev --audit-level=low
    vulnerabilities: 0
  trivy_filesystem_scan:
    command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
      --skip-dirs "**/node_modules,**/target,**/dist,**/build" .
    version: 0.72.0
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
  agent_agnostic_scan:
    method: case-insensitive grep for vendor/agent keywords over all touched files
    findings: 0
  markdown_frontmatter_parse:
    method: extract_structured_payload/PyYAML logic matching backlog_validator.py
    errors: 0
  git_diff_check:
    command: git diff --check
    result: 0 whitespace errors
closure:
  technical_debt_closed:
  - id: TD-BE-018
    contribution: All 5 of 5 named custom_implementation_points confirmed closed;
      stale materially_reduced status corrected to closed.
  technical_debt_reconfirmed_open:
  - id: TD-FE-012
    contribution: Re-checked for a non-breaking fix; none available.
  new_debt_registered: []
  real_defects_fixed: []
  status: closed
  next_backlog_item: COM-MOD-017-CLOSEOUT
```
