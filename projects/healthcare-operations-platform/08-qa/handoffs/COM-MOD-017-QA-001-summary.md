---
id: COM-MOD-017-QA-001-summary
status: closed
backlog_item: COM-MOD-017-QA-001
next_backlog_item: COM-MOD-017-CLOSEOUT
created_date: 2026-07-25
---

# COM-MOD-017-QA-001 Summary

## Status
Closed.

## Cambios Clave
Integrated marketplace validation for BCM-PLT-011 across all 4 prior COM-MOD-017 backlog
items. Four traceability sweeps run (REST contract vs. controllers, IAM permissions across
4 layers, `ui-model.md` vs. the 4 employee-portal screens, i18n key parity); 3 real doc-vs-
implementation drifts found and corrected, 0 code defects found. `openapi-source.md`: 10 of
21 operations were documented under a `/tenants/{tenantId}/...` path never actually used by
the shipped, tested routes (which nest `{tenantId}` under the resource name instead), plus 1
undocumented `getPackage` endpoint -- corrected the doc to match the working, tested API
rather than changing a live contract for a validation-only item; bumped to `1.0.1`; synced
`traceability.md`'s `api_endpoints`. `permissions.md`/`ui-model.md`: both documented an
unimplemented 15-code fine-grained `marketplace.<resource>:<action>` permission model, while
the backend (`PermissionCode.java`/`RolePermissionCatalog.java`/`EndpointPermissionRegistry.java`)
and frontend (`permissions.ts`) are fully consistent on the platform's standard coarse 4-code
`SCREEN_MARKETPLACE_*` model (same pattern tracked platform-wide by `TD-IAM-002`) -- added
`implementation_note`/`enforced_permission_code` fields documenting the enforced reality
instead of reimplementing a 15-code enforcement layer out of scope for a QA item; also
completed `ui-model.md`'s `purpose`/`required_permissions` text, which under-described 3 of
4 screens (missing submit/retire/accept verbs and permission entries for suspend/uninstall/
upgrade/rollback, though all 4 installation actions are fully implemented and tested). i18n
key parity was clean: 109 `marketplace.*`/`appShell.tabs.marketplace*` keys, full es-MX/en-US
parity, 0 missing runtime keys, 0 hardcoded strings.

## Deuda Técnica
- **TD-BE-018 (closed)**: Debt-first action taken before the traceability work above. This
  item's own status had drifted stale at `materially_reduced` -- its 5th
  `custom_implementation_point` (runtime feature-availability -> IAM/menu wiring) was
  repointed to `TD-BE-019` by `COM-MOD-017-BE-002`, and `TD-BE-019` was subsequently closed
  for real by `COM-MOD-017-FE-001` (re-verified here: `MarketplaceInstallationsScreen`'s
  install control is genuinely gated on live tenant entitlement state, covered by tests in
  both directions). All 5 of 5 originally named points are now closed; corrected `TD-BE-018`
  to `closed`.
- **TD-FE-012 (confirmed still open, non-blocking)**: Re-checked for a non-breaking fix.
  `eslint-plugin-jsx-a11y@6.10.2`/`eslint-plugin-react@7.37.5` (already latest) still bundle
  `minimatch@3.1.5`->`brace-expansion@1.1.16`; no patched `1.x` release of `brace-expansion`
  exists (latest `1.x` is `1.1.16`, still vulnerable). `npm audit fix --force`'s only
  remediation is a breaking downgrade to `eslint-plugin-react@7.22.0`. Disposition unchanged.

## Validation
| Gate | Result |
|---|---|
| `mvn -Pquality "-Dhop.local-db-tests=true" clean verify` | 484 tests, 0 failures/errors/skipped |
| Backend line coverage | 84.65% (floor 84.65%, no regression -- no backend source changed) |
| Backend static analysis (checkstyle/pmd/cpd/spotbugs/duplicate-finder) | 0 new findings; 0 marketplace-attributable checkstyle/spotbugs findings |
| `mvn -Pquality org.owasp:dependency-check-maven:check` | 72 dependencies, 0 vulnerabilities |
| `npm run quality` (employee-portal) | 224 tests, 65 files, 0 failures; 0 lint errors, 55 warnings (unchanged from FE-001) |
| Employee-portal line coverage | 90.68% (floor 89.75%, no regression) |
| `npm audit --audit-level=low` | 10 pre-existing findings, all devDependency-only (TD-FE-012) |
| `npm audit --omit=dev --audit-level=low` | 0 vulnerabilities (production dependencies clean) |
| Trivy fs (vuln/secret/misconfig, all severities) | 0 findings |
| Markdown/frontmatter parse | 0 errors on all touched files |
| Agent-agnostic scan | 0 vendor/agent hits |
| `git diff --check` | 0 whitespace errors |

## Siguiente Paso
Run `COM-MOD-017-CLOSEOUT` (module closeout and registry update): mark all BCM-PLT-011
capability packages `module_closed` in `capability-package-index.md` and `traceability.md`,
confirm zero open blocking debt attributable to COM-MOD-017, and re-affirm coverage figures
unchanged. `COM-MOD-017-WEB-001` (public marketplace listing surface) remains a separate,
not-yet-scheduled backlog item.
