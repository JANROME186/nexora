# COM-MOD-017-BE-002 QA Validation Evidence

- **Backlog Item**: COM-MOD-017-BE-002 — Implement marketplace entitlement policy, compatibility, billing adapter and installation rollback custom rules (closing TD-BE-018)
- **Module**: COM-MOD-017 Product Marketplace and Extension Packaging
- **Date**: 2026-07-24
- **Status**: Validated

## Summary of Accomplishments

1. **`EntitlementPolicyEvaluator` implements the full entitlement-policy.md `evaluation_order`** (`tenant_status`, `package_status`, `license_status`, `compatibility_status`, `iam_permission`, `feature_flag`, `clinical_safety_control`, `usage_limit`). `tenant_status` blocks only `SUSPENDED`/`ARCHIVED` tenants (`PENDING_PROVISIONING` is a legitimate pre-activation state already exercised by existing tests). `iam_permission`/`feature_flag` are implemented as a policy-decision-point taking caller-resolved facts (`EntitlementEvaluationRequest.permissionGranted`/`featureFlagEnabled`) rather than pulling from `identityaccess`/`platformconfiguration` directly, keeping `marketplaceentitlements`'s Spring Modulith dependency graph acyclic (verified — see below). `clinical_safety_control` gates packages whose `capabilityMappings` touch `BCM-LAB`/`BCM-RES` behind an explicit acknowledgement flag. `usage_limit` is backed by a new nullable `TenantEntitlement.usageLimit` field.

2. **`CompatibilityEvaluator` evaluates all 9 compatibility.md dimensions** (`platform_version`, `api_contract_version`, `database_schema_version`, `dependency_capability_versions`, `tenant_region`, `language_support`, `currency_support`, `regulatory_profile`, `feature_flags`). A new `CompatibilityMetadata` parser reads a delimited `PackageVersion.compatibilityMetadataText` field (mirroring the existing `capabilityMappingsText`/`tierCodesText` convention instead of a JSON column); an undeclared dimension is treated as compatible, never as a claim of incompatibility, preserving BE-001 behavior for versions with no declared metadata.

3. **Billing adapter boundary gained retry/idempotency (INV-MKT-003 preserved)**: `BillingAdapterPort.retrySubmission` mirrors `FiscalAdapterPort`'s submit/retry pattern. `BillingEventService.publishBillingEvent` treats a caller-supplied `providerReference` as an idempotency key — a prior `accepted` record is replayed as-is (no duplicate provider call), a prior `rejected` record is retried in place (`retryCount` incremented, same `billingEventId`). A DB-level unique index on `(tenant_id, provider_reference)` backs this.

4. **Installation rollback gained a persisted multi-step `InstallationStep` audit trail**: every install/activate/suspend/uninstall/upgrade/rollback command now appends a step. `rollbackPackage` derives its target version from the trail's most recent matching `upgrade` step, falling back to the legacy `rollbackCheckpointVersion` field only when the trail has no matching step.

5. **Runtime feature-availability → IAM/menu wiring (TD-BE-018's 5th point) — investigated, partially closed**: closing it fully requires 4 real employee-portal marketplace screens (`App.tsx`'s `SCREEN_COMPONENTS` binds every `ScreenKey` to a component 1:1; adding the screen keys without the screens breaks the frontend build) — a `COM-MOD-017-FE-001`-scale deliverable per `generation-plan.md`'s own `employee_portal` output list, not a backend custom_implementation_point. No real, non-fabricated cross-capability relationship exists yet to gate a different IAM permission on marketplace state. Registered **TD-BE-019** (open, non-blocking, targeted at `COM-MOD-017-FE-001`) rather than force a fabricated or build-breaking fix. TD-BE-018 updated to `materially_reduced` (4 of 5 acceptance criteria closed).

6. **Real pre-existing infrastructure defect found and fixed (TD-BE-020, closed)**: `application.properties` unconditionally excluded `DataSourceAutoConfiguration` for every profile (a regression from NXF-FMT-002's YAML→properties migration, commit `a446ef4`), silently breaking every `@Profile("local")` JDBC adapter and every `LocalDatabaseTest` across the *entire* backend, not just marketplace. Fixed by re-enabling it in `application-local.properties`. Verified: a clean rebuild with a fresh Docker Postgres volume went from 27 skipped/1 failing local-DB test to 0 skipped/0 failing across all modules.

7. **Non-blocking debt reduction (unrelated to marketplace scope)**: closed **TD-QA-008** — `local-toolchain-inventory.md` now documents OWASP ZAP's proven local availability (Docker `ghcr.io/zaproxy/zaproxy:stable`) with invocation commands; `stack-quality-toolchain-baseline.md`'s stale "ZAP unavailable locally" note removed.

8. **Persistence & verification**: `schema.sql` gained `tenant_entitlements.usage_limit`, `package_versions.compatibility_metadata_text`, `billing_event_records.retry_count` plus a unique idempotency index, and a new `installation_steps` table. 484 tests total (up from 442 at BE-001), 0 failures/errors/skipped, clean rebuild with Docker Compose PostgreSQL 16 up. Backend line coverage **84.65%** (floor 84.53%, no regression).

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-COM-MOD-017-BE-002
  type: qa-validation-evidence
  name: COM-MOD-017-BE-002 Marketplace Entitlement Enforcement Custom Rules Validation Evidence
  version: 1.0.0
  status: validated
  backlog_item: COM-MOD-017-BE-002
  roadmap_group: COM-MOD-017
  created_date: 2026-07-24
scope:
  backlog_item_type: backend_custom_rules
  custom_implementation_points_matured:
  - id: central_entitlement_policy_evaluator
    status: closed
    detail: Full entitlement-policy.md evaluation_order (8 steps) implemented; PDP
      pattern for iam_permission/feature_flag steps to keep Spring Modulith dependency
      graph acyclic.
  - id: compatibility_evaluation_strategy
    status: closed
    detail: All 9 compatibility.md dimensions evaluated via a new CompatibilityMetadata
      delimited-text parser on PackageVersion.
  - id: billing_provider_adapter_boundary
    status: closed
    detail: Retry/idempotency added (BillingAdapterPort.retrySubmission), keyed on
      providerReference; INV-MKT-003 preserved (BillingEventRecord stays observability-only).
  - id: installation_rollback_orchestration
    status: closed
    detail: Persisted multi-step InstallationStep audit trail; rollback derives target
      version from the trail, not only the single checkpoint field.
  - id: runtime_feature_availability_iam_menu_integration
    status: open
    detail: Requires employee-portal marketplace screens that do not exist yet (COM-MOD-017-FE-001
      scope); repointed to TD-BE-019.
technical_debt_updated:
- id: TD-BE-018
  status: materially_reduced
  from_status: open
  acceptance_criteria_closed: 4
  acceptance_criteria_total: 5
technical_debt_registered:
- id: TD-BE-019
  title: Marketplace runtime feature-availability is not wired into IAM permission
    evaluation or employee-portal menu generation
  status: open
  blocking: false
  target_backlog: COM-MOD-017-FE-001
technical_debt_closed:
- id: TD-BE-020
  title: local profile silently had no real datasource because DataSourceAutoConfiguration
    stayed globally excluded after the YAML-to-properties migration
  reason: real_pre_existing_infrastructure_defect_found_and_fixed_during_this_items_own_validation
- id: TD-QA-008
  title: OWASP ZAP local availability is undocumented in the toolchain inventory
    and baseline
  reason: opportunistic_non_blocking_debt_reduction_per_task_instructions_unrelated_to_marketplace_scope
validations:
  unit_tests: passed_484_tests_total_up_from_442_at_BE-001_0_failures
  full_suite_tests: passed_484_tests_0_failures_0_errors_0_skipped_with_docker_postgres_up_fresh_volume
  local_db_tests: passed_against_real_postgres_marketplace_entitlements_schema_verified_including_new_installation_steps_table
  modulith_boundary_verification: passed_PlatformFoundationModulithTest_confirms_no_dependency_cycle_from_the_PDP_design_choice
  checkstyle: passed_0_findings_in_touched_files_74_pre_existing_findings_elsewhere_unaffected
  pmd: passed_0_new_findings_beyond_the_same_pre_existing_accepted_rule_categories_already_present_repo_wide_597_pre_existing
  spotbugs: passed_0_findings_in_touched_files_1_pre_existing_elsewhere_unaffected
  owasp_dependency_check: blocked_offline_environment_requires_online_NVD_mirrored_database_documented_pre_existing_constraint
  trivy_fs_scan: passed_vuln_secret_misconfig_all_severities_0_findings_backend_source_pom_xml_excluding_target
  yaml_parsing: not_reapplicable_this_repo_uses_markdown_frontmatter_payloads_post_NXF-FMT-002
  agent_agnostic_scan: passed_0_vendor_specific_references_in_new_code
  git_diff_check: pending_final_pre_commit_check
  coverage_floor_check: passed_84_65_percent_floor_84_53_percent_clean_rebuild_fresh_postgres_volume_no_regression
summary: 'COM-MOD-017-BE-002 closed 4 of TD-BE-018''s 5 custom_implementation_point
  gaps: EntitlementPolicyEvaluator now implements the full entitlement-policy.md evaluation_order,
  CompatibilityEvaluator evaluates all 9 compatibility.md dimensions, the billing adapter
  gained retry/idempotency without becoming a domain source of truth, and installation
  rollback gained a real persisted multi-step audit trail. The 5th point (runtime
  feature-availability gating IAM/menu decisions) was investigated and found to require
  employee-portal screens that do not exist yet; rather than force a build-breaking
  or fabricated fix, it was honestly repointed to a new TD-BE-019 targeting a future
  COM-MOD-017-FE-001. While validating, a real pre-existing infrastructure defect
  was found and fixed: application.properties unconditionally excluded DataSourceAutoConfiguration,
  silently breaking every local-profile JDBC adapter and LocalDatabaseTest across
  the entire backend since the NXF-FMT-002 YAML-to-properties migration (registered
  and closed as TD-BE-020). One additional, unrelated non-blocking debt item (TD-QA-008,
  stale OWASP ZAP toolchain documentation) was also closed. 484 tests pass with 0
  failures/errors/skipped against a fresh Docker Compose PostgreSQL 16 volume; backend
  line coverage measured at 84.65% (floor 84.53%, no regression). Checkstyle and SpotBugs
  show 0 findings in every file this item touched; PMD reproduces only the same pre-existing
  accepted rule categories already present repo-wide. OWASP Dependency-Check could
  not run (requires online NVD access, a documented pre-existing environment constraint);
  Trivy filesystem scan (vuln/secret/misconfig, all severities) found 0 findings.

  '
```
