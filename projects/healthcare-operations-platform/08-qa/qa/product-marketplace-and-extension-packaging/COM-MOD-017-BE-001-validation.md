# COM-MOD-017-BE-001 QA Validation Evidence

- **Backlog Item**: COM-MOD-017-BE-001 — Compile marketplace catalog, offer, entitlement and installation backend outputs
- **Module**: COM-MOD-017 Product Marketplace and Extension Packaging
- **Date**: 2026-07-24
- **Status**: Validated

## Summary of Accomplishments

1. **New `marketplaceentitlements` Spring Modulith module** hosting six sibling capability sub-packages, mirroring how `integrationinteroperability` hosts BCM-PLT-004/BCM-PLT-005.

2. **Package Catalog (AGG-030 MarketplacePackage/PackageVersion)**:
   - `POST/GET /api/marketplace/packages`, `POST /api/marketplace/packages/{id}/publish`, `GET/POST /api/marketplace/packages/{id}/versions/{version}[/certify|/retire]`.
   - RN-MKT-001: submission requires at least one capability mapping. INV-MKT-001/RN-MKT-004: publication is blocked until a version is certified (all four of compatibility/security-review/support-model/telemetry-model approved).

3. **Commercial Offers (AGG-031 CommercialOffer)**:
   - `POST/GET /api/marketplace/offers`, `POST /api/marketplace/offers/{id}/accept`.
   - OFFER-001: an offer can only be published against an already-published package version. OFFER-002: acceptance runs a compatibility evaluation and rejects on failure. Acceptance directly grants a `TenantEntitlement`.

4. **Tenant Entitlements (AGG-032 TenantEntitlement)**:
   - `GET/POST /api/marketplace/entitlements/{tenantId}`, `POST /api/marketplace/entitlements/{tenantId}/{id}/revoke`.
   - A new centralized `EntitlementPolicyEvaluator` (RN-MKT-005) is the single gate every other capability calls to check entitlement state — never hard-coded in a controller.

5. **Package Installation (AGG-033 PackageInstallation)**:
   - `GET/POST /api/marketplace/installations/{tenantId}`, `.../{id}/activate|suspend|uninstall|upgrade`, `.../{id}/upgrade/rollback`.
   - RN-MKT-002: activation is gated by an active entitlement. INV-MKT-004: activation and upgrade preserve a rollback checkpoint before applying a version change; rollback fails cleanly with `ROLLBACK_NOT_AVAILABLE` when no checkpoint exists. Uninstall soft-disables and preserves the record (package-manifest.md `uninstall_policy`).

6. **Compatibility Evaluation**: `POST /api/marketplace/compatibility/evaluate` — stateless `platform_version` major/minor comparator producing `compatible`/`compatible_with_warning`/`incompatible`/`unknown` decisions matching compatibility.md's four effects.

7. **Billing Adapter Boundary (INV-MKT-003, OFFER-004)**: `POST /api/marketplace/billing/events` — a provider-agnostic `BillingAdapterPort` with a `LocalDeterministicBillingAdapter` implementation, mirroring `IntegrationAdapterPort`/`FiscalAdapterPort`. Billing events are persisted as an observability-only record and never become the source of truth for entitlement, license or clinical state.

8. **IAM Permissions & i18n**: Four new `PermissionCode` values (`SCREEN_MARKETPLACE_PACKAGES/OFFERS/ENTITLEMENTS/INSTALLATIONS`), registered in `EndpointPermissionRegistry` and a new `MARKETPLACE_OPERATOR`/`TENANT_ADMIN` role pair in `RolePermissionCatalog`. 16 `marketplace.error.*` i18n keys added to the default, es-MX and en-US catalogs.

9. **Persistence & Verification**: New `db/product-marketplace-and-entitlements/schema.sql` (6 tables), registered in `application-local.properties`. 60 new tests added (service-level unit tests per capability, a full-lifecycle API test, a real-Postgres local-database test) — all pass. Backend coverage raised from the 84.25% floor to a reproducible **84.53%** (442 tests, 0 failures/errors/skipped, Docker Compose PostgreSQL 16 up).

10. **Technical debt**: registered **TD-BE-018** for the deeper entitlement-policy/compatibility/billing-adapter/rollback sophistication generation-plan.md names as `custom_implementation_points`, targeting a future COM-MOD-017-BE-002 — matching the BE-001/BE-002 maturation pattern already used for MVP-MOD-005's fiscal adapter and MVP-MOD-008's integration adapter. Fixed 2 low-severity SpotBugs `IMPROPER_UNICODE` findings introduced by this item's own code before closure.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-COM-MOD-017-BE-001
  type: qa-validation-evidence
  name: COM-MOD-017-BE-001 Backend Compilation Validation Evidence
  version: 1.0.0
  status: validated
  backlog_item: COM-MOD-017-BE-001
  roadmap_group: COM-MOD-017
  created_date: 2026-07-24
scope:
  backlog_item_type: backend_compilation
  capabilities_compiled: BCM-PLT-011 Product Marketplace and Entitlements, all 21
    openapi-source.md operations -- packagecatalog (/api/marketplace/packages, submit/publish/certify/retire/list/get-version),
    commercialoffers (/api/marketplace/offers, publish/list/accept), tenantentitlements
    (/api/marketplace/entitlements/{tenantId}, grant/list/revoke), packageinstallation
    (/api/marketplace/installations/{tenantId}, install/activate/suspend/ uninstall/upgrade/rollback/list),
    compatibilityevaluation (/api/marketplace/compatibility/evaluate), billingadapter
    (/api/marketplace/billing/events)
  new_spring_modulith_module: marketplaceentitlements (allowedDependencies sharedkernel,
    organizationmanagement, auditcompliance)
  new_schema: db/product-marketplace-and-entitlements/schema.sql (marketplace_entitlements
    schema, 6 tables -- marketplace_packages, package_versions, commercial_offers,
    tenant_entitlements, package_installations, billing_event_records)
  new_permissions: SCREEN_MARKETPLACE_PACKAGES, SCREEN_MARKETPLACE_OFFERS, SCREEN_MARKETPLACE_ENTITLEMENTS,
    SCREEN_MARKETPLACE_INSTALLATIONS; MARKETPLACE_OPERATOR and TENANT_ADMIN roles
    added to RolePermissionCatalog
  i18n_namespace: marketplace.error.* (16 keys, es-MX and en-US, plus default messages.properties)
custom_implementation_points_basic_level:
- Central entitlement policy evaluator (EntitlementPolicyEvaluator) -- active/non-expired
  entitlement existence gate implemented (RN-MKT-002, RN-MKT-005); full 8-step entitlement-policy.md
  evaluation_order deferred to TD-BE-018.
- Compatibility evaluation strategy (CompatibilityEvaluator) -- platform_version major/minor
  comparison implemented; remaining 8 compatibility.md dimensions deferred to TD-BE-018.
- Billing provider adapter boundary (BillingAdapterPort / LocalDeterministicBillingAdapter)
  -- provider-agnostic port with a local deterministic implementation, mirroring IntegrationAdapterPort/FiscalAdapterPort;
  retry/idempotency deferred to TD-BE-018.
- Installation rollback orchestration -- single rollback-checkpoint-version field
  satisfying INV-MKT-004; multi-step InstallationStep audit trail deferred to TD-BE-018.
technical_debt_registered:
  item_id: TD-BE-018
  title: Marketplace entitlement policy, compatibility strategy, billing adapter and
    installation rollback orchestration are basic implementations only
  status: open
  blocking: false
  target_backlog: COM-MOD-017-BE-002
validations:
  unit_tests: passed_60_new_tests_0_failures
  full_suite_tests: passed_442_tests_0_failures_0_errors_0_skipped_with_docker_postgres_up
  local_db_tests: passed_against_real_postgres_16_marketplace_entitlements_schema_verified_6_tables
  modulith_boundary_verification: passed_PlatformFoundationModulithTest
  iam_permissions: passed_registered_in_PermissionCode_and_EndpointPermissionRegistry_and_RolePermissionCatalog
  i18n_externalization: passed_es_MX_en_US_and_default_catalogs_updated_16_marketplace_error_keys
  checkstyle: passed_0_findings_in_new_module_73_pre_existing_findings_elsewhere_unaffected
  pmd: passed_0_findings_beyond_baseline_pattern_already_present_in_reference_modules_repo_wide_590_pre_existing
  spotbugs: passed_0_findings_in_new_module_after_fixing_2_low_severity_IMPROPER_UNICODE_findings_63_pre_existing_elsewhere
  owasp_dependency_check: passed_72_dependencies_0_vulnerabilities
  trivy_fs_scan: passed_vuln_secret_misconfig_all_severities_0_findings_pom_xml_target
  yaml_parsing: passed_1369_files_0_errors
  agent_agnostic_scan: passed_0_vendor_specific_references_in_new_code
  git_diff_check: passed_no_whitespace_errors
  coverage_floor_check: passed_raised_84_25_to_84_53_percent_442_tests_0_skipped_clean_rebuild
summary: 'COM-MOD-017-BE-001 backend outputs successfully compiled for Product Marketplace
  and Entitlements (BCM-PLT-011): a new marketplaceentitlements Spring Modulith module
  hosting packagecatalog, commercialoffers, tenantentitlements, packageinstallation,
  compatibilityevaluation and billingadapter capabilities, all 21 openapi-source.md
  operations functional with no endpoint responding unimplemented. Each of generation-plan.md''s
  five custom_implementation_points is compiled to a basic, correct, testable level
  sufficient for every generated endpoint to work end to end; deeper policy sophistication
  is registered as TD-BE-018 targeting COM-MOD-017-BE-002, matching the BE-001/BE-002
  maturation precedent set by MVP-MOD-005, MVP-MOD-008 and COM-MOD-011. Backend coverage
  raised from the 84.25% floor to a reproducible 84.53% (442 tests, 0 failures/errors/skipped)
  with the full local Docker Compose PostgreSQL stack up. All mandatory backend gates
  (Maven verify, checkstyle, PMD, SpotBugs, OWASP Dependency-Check, Trivy, YAML parse,
  git diff --check) passed clean for this backlog item''s own code.

  '
```
