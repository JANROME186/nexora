---
id: HOP-TD-IDX-001
format: markdown_structured_payload
type: technical-debt-index
name: HOP Technology Debt Index
version: 1.0.0
status: active
---

# Hop Technology Debt Index

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TD-IDX-001
  type: technical-debt-index
  name: HOP Technology Debt Index
  version: 1.0.0
  status: active
  human_readable: README.md
  machine_readable: technical-debt-index.md
standard:
  framework_standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  prompt_playbook: ../../../../nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md
purpose: Track HOP technology modernization, migration and enterprise quality debt
  discovered by iterative quality reviews.
policy:
  initial_stack_is_baseline_not_permanent_constraint: true
  review_required_during:
  - every_code_changing_backlog_item
  - dependency_change
  - module_closeout
  - release_readiness
  remediation_strategy: debt_first_and_gradual_when_affected_components_are_touched
  final_project_closure_requires_no_open_debt: true
  missing_required_tooling_policy:
    required: true
    effective_from_backlog_item: COM-MOD-013-FE-001
    rule: If a HOP backlog item touches a stack or runnable surface and a mandatory
      validation category cannot run because the project lacks an executable script,
      plugin, tool or configuration, the agent must create or update technical debt
      before closure. Missing duplicate-code, complexity, SAST/static analysis, OWASP/secure-code,
      dependency, secrets, coverage, i18n, accessibility, DAST, SBOM or license checks
      cannot remain undocumented and cannot be closed as informal "not applicable"
      when the surface exists.
    valid_not_applicable_only_when:
    - product_surface_or_runtime_does_not_exist
    - artifact_type_is_not_part_of_the_selected_backlog_scope
    - stack_was_not_touched_and_prior_non_limited_evidence_is_referenced
    debt_item_required_fields:
    - missing_validation_category
    - expected_open_source_tool_or_equivalent
    - affected_stack
    - affected_component
    - source_backlog_item
    - risk_level
    - urgency
    - owner_or_responsible_role
    - target_backlog
    - acceptance_criteria
    - closure_blocking_decision
  debt_burndown_intensity:
    early_mvp: at_least_one_relevant_debt_item_per_code_changing_iteration
    module_closeout: reduce_multiple_relevant_items_when_open_debt_exists
    release_or_ga: all_debt_closed
  coverage_policy:
    target_line_coverage_percent: 80
    previous_iteration_coverage_is_hard_floor: true
    below_target_requires_improvement_each_iteration: true
    below_target_minimum_relevant_iteration_improvement_percentage_points: 3
    below_target_preferred_relevant_iteration_improvement_percentage_points: 5
    final_project_closure_requires_target: true
    current_stack_baselines:
      backend_java_maven:
        current_line_coverage_percent: 84.65
        source_evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-QA-001-validation.md
        next_iteration_minimum_line_coverage_percent: 84.65
        final_closure_target_percent: 80
        correction_note: 'COM-MOD-013-QA-001 found the recorded 84.14% figure was
          not reproducible from a clean rebuild (measured 82.57%); investigating that
          gap surfaced and closed TD-DB-005 (COM-MOD-013''s backend was silently using
          in-memory storage instead of PostgreSQL due to a missing schema.sql registration
          compounded by inverted @Profile wiring on 4 JDBC/in-memory repository pairs).
          After that fix, the reproducible clean-rebuild figure was 84.24% (381 tests,
          0 failures/errors/skipped). A subsequent OWASP ZAP DAST pass against the
          running backend (required for this validation item, not left not_applicable)
          found and fixed one further defect, TD-QA-007 (unhandled 500 on a malformed/abruptly-truncated
          multipart upload to POST /api/documents, remapped to 400), adding one regression
          test and raising it to 84.25% (382 tests, 0 failures/errors/skipped); the
          new floor is 84.25%. MVP-MOD-007-QA-001 raised backend line coverage to
          78.42%. MVP-MOD-007-CLOSEOUT''s TD-BE-010 fix added one real unit test for
          the new SampleReadPort-backed cancellation branch, raising it to 78.51%
          (211 tests, 0 failures/errors) with no regression. MVP-MOD-008-BE-001 raised
          it further to 80.08% (239 tests, 0 failures/errors), reaching the final
          80% target for this stack. MVP-MOD-008-BE-002 raised it further to 80.49%
          (265 tests, 0 failures/errors). COM-MOD-009-PORTAL-002 raised it further
          to 80.60% (280 tests, 0 failures/errors/skipped). COM-MOD-010-BE-001 raised
          it further to 82.94% (308 tests, 0 failures/errors/skipped). COM-MOD-010-QA-001
          found the 82.94% figure was not reproducible from a clean rebuild (measured
          81.90% twice, 0 backend source changes since BE-002) due to a jacoco.exec
          measurement-inflation artifact; fixed with a new InventoryQualityControlsLocalDatabaseTest.java,
          raising the corrected, reproducible figure to 83.73% (315 tests, 0 failures/errors/skipped).
          COM-MOD-010-CLOSEOUT corrected this registry entry''s stale 82.94% value
          to match, since it had not yet been synced from COM-MOD-010-QA-001''s own
          evidence; future backend iterations must not regress below 83.73%. COM-MOD-011-BE-001
          raised it further to 83.96% (324 tests, 0 failures/errors/ skipped) after
          compiling the anonymous public-website surface plus the PublicApiRateLimitInterceptor
          and closing TD-BE-015; the new floor is 83.96%. COM-MOD-011-FE-001 added
          a QuotationRequest.channel field (defect fix: quotations had no way to distinguish
          public-website-submitted drafts, unlike AppointmentSlot) plus 3 new tests,
          raising it further to 83.99% (327 tests, 0 failures/errors/skipped); the
          new floor is 83.99%. COM-MOD-012-BE-001 compiled BCM-ORG-001 tenant operations,
          the new BCM-PLT-002 platformconfiguration module and BCM-PLT-006 observability
          extensions, adding 8 new/ extended test classes, raising it further to 84.11%
          (362 tests, 0 failures/errors/ skipped); the new floor is 84.11%. COM-MOD-012-QA-001
          raised it further to 84.14% (367 tests, 0 failures/errors/skipped) by adding
          a resilience regression test (readiness-vs-database-outage) and 4 new GlobalExceptionHandler/AuthController
          regression tests for two real defects found and fixed by this backlog item''s
          DAST run (a cross-cutting null-byte/oversized-value 500, TD-QA-005; an AuthController
          exception-advice scope gap, TD-QA-006); the new floor is 84.14%. COM-MOD-013-QA-001
          raised it further to 84.25% (382 tests, 0 failures/errors/skipped) as described
          above. COM-MOD-017-BE-001 compiled the new marketplaceentitlements Spring
          Modulith module (BCM-PLT-011 packagecatalog, commercialoffers, tenantentitlements,
          packageinstallation, compatibilityevaluation and billingadapter capabilities)
          with 60 new tests (unit, API and a real-Postgres local-database test), raising
          it to a reproducible clean-rebuild 84.53% (442 tests, 0 failures/errors/skipped,
          Docker Compose PostgreSQL 16 up); registered TD-BE-018 for the deferred
          deeper entitlement-policy/compatibility/billing-adapter/ rollback sophistication
          named by generation-plan.md''s custom_implementation_points. COM-MOD-017-BE-002
          closed 4 of TD-BE-018''s 5 points and raised it further to 84.65% (484 tests,
          0 failures/errors/skipped); COM-MOD-017-QA-001 re-confirmed 84.65% unchanged
          (no backend source changed by that validation-only item). COM-MOD-017-CLOSEOUT
          corrected this registry entry''s stale 84.53% value to match, since it had
          not yet been synced from COM-MOD-017-BE-002/QA-001''s own evidence; the
          new floor is 84.65%.'
      frontend_typescript_web:
        current_line_coverage_percent: 90.85
        source_evidence: 08-qa/qa/imaging-operations/COM-MOD-014-QA-001-validation.md
        next_iteration_minimum_line_coverage_percent: 90.85
        final_closure_target_percent: 80
        correction_note: 'COM-MOD-013-QA-001 fixed a hardcoded string and a function-size
          violation in ComplianceEvidenceScreen.tsx, raising coverage from 89.74%
          to 89.75% (187 tests, 60 files, 0 failures); the new floor is 89.75%. MVP-MOD-007-CLOSEOUT
          found employee-portal coverage had regressed from the 84.44% floor to 84.03%:
          MVP-MOD-007-PORTAL-001 expanded laboratoryOperationsApi.ts and laboratoryResultMapper.ts
          (shared with the employee portal) without adding a dedicated unit test for
          the API module. Added src/test/laboratoryOperationsApi.test.ts (mirroring
          the existing cashSalesApi.test.ts/frontDeskApi.test.ts pattern), restoring
          coverage to 85.50%, above the previous 84.44% floor. MVP-MOD-008-QA-001
          later raised the employee-portal floor to 86.47% (101 tests, 0 failures).
          COM-MOD-010-FE-001 raised it further to 87.87% (124 tests, 48 test files,
          0 failures) by adding the Inventory and Internal Quality administration
          screens plus their tests. COM-MOD-010-QA-001 re-confirmed 88.24% with the
          same 124 tests/48 files (no source file touched by that validation-only
          backlog item; the small delta reflects re-measurement, not a regression).
          COM-MOD-010-CLOSEOUT corrected this registry entry''s stale 87.87% value
          to match, since it had not yet been synced from COM-MOD-010-QA-001''s own
          evidence. Must not regress below 88.24%. COM-MOD-011-FE-001 added the 3
          public-request-administration screens (154 tests, 54 test files, 0 failures),
          raising it further to 88.68%. COM-MOD-013-FE-001 added the Advanced Quality
          and Compliance employee-portal screens plus tests, raising it further to
          89.74% (187 tests, 60 test files, 0 failures); the new floor is 89.74%.
          COM-MOD-017-FE-001 added the 4 marketplace administration screens plus
          tests, raising it further to 90.68% (224 tests, 65 test files, 0 failures).
          COM-MOD-014-FE-001/QA-001 added the 8 imaging operations screens plus tests,
          raising it further to 90.85% (249 tests, 68 test files, 0 failures); COM-MOD-014-CLOSEOUT
          synced this entry to match; the new floor is 90.85%.'
      mobile_typescript_foundation:
        current_line_coverage_percent: 99.21
        source_evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-APP-001-validation.md
        next_iteration_minimum_line_coverage_percent: 99.21
        final_closure_target_percent: 80
        correction_note: 'Re-measured during MVP-MOD-007-CLOSEOUT with no code changes
          to the mobile app: 98.87% (31 tests), consistent with the 97.15% -> 98.87%
          growth already reflected upstream. No regression. COM-MOD-009-APP-001 later
          raised the mobile floor to 99.21% with 40 tests and 0 failures.'
      patient_portal_typescript_web:
        current_line_coverage_percent: 94.11
        source_evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-QA-001-validation.md
        next_iteration_minimum_line_coverage_percent: 94.11
        final_closure_target_percent: 80
        correction_note: First-ever coverage measurement for patient-portal, taken
          during MVP-MOD-007-CLOSEOUT, was 41.93%. COM-MOD-009-PORTAL-001 closed TD-FE-008
          and raised coverage to 89.58%. COM-MOD-009-QA-001 raised it further to 94.11%
          (18 tests, 0 failures) and resolved TD-FE-011. COM-MOD-010-CLOSEOUT corrected
          this registry entry's stale 89.58% value to match, since it had not yet
          been synced from COM-MOD-009-QA-001's own evidence.
      doctor_portal_typescript_web:
        current_line_coverage_percent: 96.28
        source_evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-QA-001-validation.md
        next_iteration_minimum_line_coverage_percent: 96.28
        final_closure_target_percent: 80
        correction_note: First-ever coverage measurement for doctor-portal, taken
          during MVP-MOD-007-CLOSEOUT, was 40.62% (TD-FE-009 registered). COM-MOD-009-PORTAL-002
          closed TD-FE-009 and raised coverage to 89.86%, exceeding the 80% final-closure
          target. COM-MOD-009-QA-001 raised it further to 96.28% (31 tests, 0 failures/errors).
          COM-MOD-010-CLOSEOUT corrected this registry entry's stale 89.86% value
          to match, since it had not yet been synced from COM-MOD-009-QA-001's own
          evidence.
      public_website_typescript_web:
        current_line_coverage_percent: 98.61
        source_evidence: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-WEB-001-validation.md
        next_iteration_minimum_line_coverage_percent: 98
        final_closure_target_percent: 80
        correction_note: 'First-ever coverage measurement for the new public-website
          module, taken during COM-MOD-011-WEB-001 (97 tests, 34 test files, 0 failures):
          98.61% lines/statements, 93.15% branches, 87.70% functions. vite.config.ts
          thresholds are set slightly below the measured figures (lines/statements
          98%, branches 90%, functions 85%) to leave headroom for incidental variance;
          the registry floor tracks the same conservative 98% figure.'
  promote_to_blocking_when:
  - vulnerability_or_secure_code_finding_without_disposition
  - unsupported_runtime_blocks_safe_development
  - quality_gate_cannot_run_without_upgrade
  - enterprise_quality_alignment_blocks_functional_development
  - final_project_closure_requested_with_open_debt
  - final_project_closure_requested_with_any_applicable_stack_below_80_percent_coverage
  - iteration_coverage_decreases_below_previous_baseline
item_path_pattern: 08-qa/technical-debt/<debt-id>-<short-name>.yaml
entries:
- id: TD-FMT-001
  title: Transition monolithic YAML task/state artifacts to Markdown frontmatter handoffs
  status: materially_reduced
  risk_level: medium
  blocking: false
  source_backlog_item: NXF-CTX-001
  affected_area: framework_and_hop_execution_artifacts
  file: 08-qa/technical-debt/TD-FMT-001-yaml-monolith-to-frontmatter-transition.md
  remediation_strategy: materially_reduced_by_COM_MOD_015_DEF_compact_frontmatter_handoff_and_evidence_pattern
- id: TD-QA-001
  title: Automate DAST baseline scans for runnable web and API surfaces
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-002-QA-001
  affected_area: integrated_web_and_api_runtime
  file: 08-qa/technical-debt/TD-QA-001-dast-automation.md
  remediation_strategy: closed_by_HOP_QA_ALIGN_004_zap_baseline_and_api_scans
- id: TD-QA-002
  title: Upgrade Trivy scanner version in local and CI quality toolchain
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: MVP-MOD-002-QA-001
  affected_area: vulnerability_secret_misconfiguration_scanning
  file: 08-qa/technical-debt/TD-QA-002-trivy-tool-upgrade.md
  remediation_strategy: closed_by_HOP_QA_ALIGN_004_trivy_upgraded_to_0_72_0
- id: TD-BE-001
  title: Configure Mockito Java agent for future JDK test compatibility
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: MVP-MOD-002-QA-001
  affected_area: backend_test_infrastructure
  file: 08-qa/technical-debt/TD-BE-001-mockito-java-agent.md
  remediation_strategy: closed_by_MVP_MOD_005_QA_001_mockito_javaagent_configuration
- id: TD-BE-002
  title: Configure backend Java/Maven static analysis and SAST toolchain
  status: materially_reduced
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-002-CLOSEOUT
  affected_area: backend_static_analysis_and_sast
  file: 08-qa/technical-debt/TD-BE-002-backend-static-analysis-toolchain.md
  remediation_strategy: gradual_pmd_and_semgrep_hardening_when_backend_code_is_touched
- id: TD-BE-003
  title: Add backend test coverage measurement and gate (JaCoCo)
  status: materially_reduced
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-002-CLOSEOUT
  affected_area: backend_test_coverage
  file: 08-qa/technical-debt/TD-BE-003-backend-coverage-gate.md
  remediation_strategy: gradual_coverage_increase_when_backend_tests_are_touched_now_at_77_92_percent
- id: TD-BE-004
  title: Add backend release readiness supply-chain gates (SBOM, license, build rules)
  status: materially_reduced
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-002-CLOSEOUT
  affected_area: backend_build_and_release_evidence
  file: 08-qa/technical-debt/TD-BE-004-release-supply-chain-gates.md
  remediation_strategy: gradual_release_policy_hardening_before_ga
- id: TD-STACK-001
  title: Gradual stack modernization roadmap for major framework and runtime upgrades
  status: materially_reduced
  risk_level: low
  blocking: false
  source_backlog_item: MVP-MOD-002-CLOSEOUT
  affected_area: full_stack
  file: 08-qa/technical-debt/TD-STACK-001-stack-modernization-roadmap.md
  remediation_strategy: materially_reduced_by_COM_MOD_012_OPS_001_deployment_strategy_then_gradual_when_affected_components_are_touched
- id: TD-BE-005
  title: Doctor referring-eligibility is a computed query, not an activation-gated
    status field
  status: open
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-003-BE-002
  affected_area: doctor_management_activation
  file: 08-qa/technical-debt/TD-BE-005-doctor-activation-gating-via-eligibility-query.md
  remediation_strategy: gradual_when_doctor_state_machine_is_next_modeled
- id: TD-BE-006
  title: PatientRegistrationService.commit() orchestration is not wrapped in a database
    transaction
  status: open
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-003-BE-002
  affected_area: patient_registration_commit_orchestration
  file: 08-qa/technical-debt/TD-BE-006-patient-registration-commit-non-atomic.md
  remediation_strategy: gradual_when_backend_transaction_infrastructure_is_next_touched
- id: TD-BE-007
  title: Professional credential expiration is not proactively transitioned by a scheduler
    and does not flag doctors for re-verification
  status: open
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-003-QA-001
  affected_area: doctor_management_credential_lifecycle
  file: 08-qa/technical-debt/TD-BE-007-credential-expiration-scheduler-missing.md
  remediation_strategy: gradual_when_doctor_credential_lifecycle_is_next_touched
- id: TD-BE-008
  title: PatientSnapshot/DoctorSnapshot document and credential number masking is
    fixed, not tenant-configurable
  status: open
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-003-QA-001
  affected_area: patient_and_doctor_read_model_privacy
  file: 08-qa/technical-debt/TD-BE-008-read-model-masking-not-tenant-configurable.md
  remediation_strategy: gradual_when_tenant_configuration_surface_is_next_extended
- id: TD-FE-002
  title: Employee portal is missing patient/doctor update, patient document management
    and doctor specialty assignment UI
  status: open
  risk_level: low
  blocking: false
  source_backlog_item: MVP-MOD-003-QA-001
  affected_area: employee_portal_people_and_clinical_master_data_screens
  file: 08-qa/technical-debt/TD-FE-002-patient-doctor-update-documents-specialty-ui-missing.md
  remediation_strategy: gradual_when_a_future_ui_backlog_item_covers_patient_or_doctor_editing
- id: TD-DEF-001
  title: Quotation-to-Sale conversion path deferred until MVP-MOD-005 models the Sale
    aggregate
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: MVP-MOD-004-DEF
  affected_area: quotation_management_conversion_lifecycle
  file: 08-qa/technical-debt/TD-DEF-001-quotation-to-sale-conversion-deferred.md
  remediation_strategy: closed_by_MVP_MOD_005_BE_001_sale_from_accepted_quotation
- id: TD-DEF-002
  title: Appointment capacity planning against detailed branch schedules is deferred
    to BCM-ORG-007
  status: materially_reduced
  risk_level: low
  blocking: false
  source_backlog_item: MVP-MOD-004-DEF
  affected_area: appointment_scheduling_capacity_validation
  file: 08-qa/technical-debt/TD-DEF-002-appointment-capacity-planning-deferred.md
  remediation_strategy: materially_reduced_by_COM_MOD_014_DEF_imaging_modality_slot_and_procedure_room_schedule_models_and_BE_001_backend_room_concurrency_validation; gradual_until_BCM_ORG_007
- id: TD-BE-009
  title: Branch snapshot version is a fixed placeholder, not a real optimistic-concurrency
    counter
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: MVP-MOD-004-BE-001
  affected_area: organization_management_branch_versioning
  file: 08-qa/technical-debt/TD-BE-009-branch-snapshot-version-placeholder.md
  remediation_strategy: closed_by_HOP_ENT_FOUND_001_branch_gained_a_real_version_field_and_column
- id: TD-BE-010
  title: Diagnostic order cancellation override uses order-status as a proxy for downstream
    sample/processing state
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-004-BE-002
  affected_area: diagnostic_order_cancellation_downstream_state_check
  file: 08-qa/technical-debt/TD-BE-010-order-cancellation-sample-state-check-deferred.md
  remediation_strategy: closed_by_MVP_MOD_007_CLOSEOUT_sample_read_port_wired_into_diagnosticordermanagement
- id: TD-FE-003
  title: Implement frontend enterprise quality profile
  status: materially_reduced
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-QUALITY-ALIGNMENT-GAP-ANALYSIS
  affected_area: employee_portal_quality_profile
  file: 08-qa/technical-debt/TD-FE-003-frontend-enterprise-quality-profile.md
  remediation_strategy: gradual_eslint_warning_accessibility_and_i18n_hardening_when_frontend_is_touched
- id: TD-APP-001
  title: Establish mobile application enterprise quality baseline
  status: materially_reduced
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-QUALITY-ALIGNMENT-GAP-ANALYSIS
  affected_area: mobile_app_quality_profile
  file: 08-qa/technical-debt/TD-APP-001-mobile-quality-baseline.md
  remediation_strategy: gradual_native_mobile_quality_hardening_when_renderer_stack_is_selected
- id: TD-QA-003
  title: Refresh vulnerability evidence with all-severity scans
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-QUALITY-ALIGNMENT-GAP-ANALYSIS
  affected_area: vulnerability_evidence
  file: 08-qa/technical-debt/TD-QA-003-all-severity-vulnerability-evidence.md
  remediation_strategy: completed_by_HOP_QA_ALIGN_004_all_severity_scans
- id: TD-I18N-001
  title: Establish message externalization and magic-string remediation baseline
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-QUALITY-ALIGNMENT-GAP-ANALYSIS
  affected_area: backend_frontend_mobile_messages_and_magic_values
  file: 08-qa/technical-debt/TD-I18N-001-message-externalization-baseline.md
  remediation_strategy: completed_by_HOP_QA_ALIGN_005_inventory_strategy_and_safe_remediation
- id: TD-FE-004
  title: Raise employee portal line coverage to the 80 percent final-closure target
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-QA-ALIGN-003
  affected_area: employee_portal_test_coverage
  file: 08-qa/technical-debt/TD-FE-004-frontend-coverage-80-target.md
  remediation_strategy: closed_by_MVP_MOD_005_FE_001_new_screen_and_api_test_coverage_80_57_percent
- id: TD-APP-002
  title: Establish mobile coverage measurement and raise it to the 80 percent final-closure
    target
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-QA-ALIGN-003
  affected_area: mobile_app_test_coverage
  file: 08-qa/technical-debt/TD-APP-002-mobile-coverage-80-target.md
  remediation_strategy: closed_by_HOP_ENT_FOUND_001_corrective_mobile_vitest_coverage_97_15_percent
- id: TD-FE-005
  title: Employee portal production security and cache headers deferred to the production
    hosting layer
  status: open
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-QA-ALIGN-004
  affected_area: employee_portal_response_headers
  file: 08-qa/technical-debt/TD-FE-005-production-hosting-security-headers-deferred.md
  remediation_strategy: gradual_before_first_production_deployment_of_employee_portal
- id: TD-QA-004
  title: Malformed empty-key query parameter causes an unhandled 500 on POST /api/platform/tenants
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: HOP-QA-ALIGN-004
  affected_area: backend_servlet_parameter_parsing
  file: 08-qa/technical-debt/TD-QA-004-malformed-query-parameter-unhandled-500.md
  remediation_strategy: closed_by_MVP_MOD_007_QA_001_GlobalExceptionHandler
- id: TD-I18N-002
  title: Complete full message-catalog and localization-library adoption beyond the
    HOP-QA-ALIGN-005 baseline
  status: materially_reduced
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-QA-ALIGN-005
  affected_area: backend_frontend_mobile_messages_and_magic_values
  file: 08-qa/technical-debt/TD-I18N-002-full-localization-adoption.md
  remediation_strategy: materially_reduced_by_HOP_ENT_FOUND_001_real_locale_keyed_catalogs_and_backend_MessageSource_baseline_established_remaining_modules_and_screens_tracked;
    further_reduced_by_MVP_MOD_008_DEF_first_class_code_field_and_reserved_message_key_namespaces_modeled_for_new_integration_capabilities;
    further_reduced_by_MVP_MOD_008_BE_002_adding_a_first_class_messageKey_field_alongside_code_on_every_BCM_PLT_004_005_010_error_response_with_es_MX_en_US_catalog_entries_for_every_code;
    further_reduced_by_COM_MOD_009_PORTAL_002_replacing_the_doctor_portal_wrong_domain_locale_catalog_with_a_complete_correct_doctor_domain_catalog;
    further_reduced_by_COM_MOD_012_BE_001_organizationmanagement_error_and_platformconfiguration_error_i18n_namespaces;
    further_reduced_by_COM_MOD_013_FE_001_AuditEventsScreen_hardcoded_string_retrofit_and_advancedQualityCompliance_i18n_namespaces;
    further_reduced_by_COM_MOD_013_QA_001_ComplianceEvidenceScreen_hardcoded_status_column_header_retrofit;
    further_reduced_by_COM_MOD_014_BE_001_imaging_operations_error_i18n_namespaces;
    further_reduced_by_COM_MOD_014_INT_001_dicom_and_pacs_integration_error_i18n_namespaces;
    further_reduced_by_COM_MOD_014_FE_001_imaging_operations_ui_i18n_namespaces
- id: TD-FE-006
  title: Employee portal is missing dedicated Appointment Scheduling, Admission Management
    and Quotation Management UI
  status: open
  risk_level: low
  blocking: false
  source_backlog_item: MVP-MOD-004-FE-001
  affected_area: employee_portal_front_desk_care_delivery_screens
  file: 08-qa/technical-debt/TD-FE-006-appointment-admission-quotation-ui-missing.md
  remediation_strategy: gradual_when_a_future_ui_backlog_item_covers_scheduling_admission_or_quotation_workflows
- id: TD-BE-011
  title: CashSales depends on open FrontDeskCareDelivery internals instead of stable
    public ports
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-005-BE-002
  affected_area: cashsales_frontdesk_module_boundary
  file: 08-qa/technical-debt/TD-BE-011-cashsales-frontdesk-public-port-boundary.md
  remediation_strategy: completed_by_MVP_MOD_005_BE_002_sale_source_named_interface
- id: TD-BE-012
  title: Burn down backend static-analysis findings discovered during MVP-MOD-007-BE-001
    reconciliation
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-007-BE-001
  affected_area: backend_static_analysis_and_secure_code
  file: 08-qa/technical-debt/TD-BE-012-backend-static-analysis-findings-burn-down.md
  remediation_strategy: start_with_document_management_findings_during_MVP_MOD_007_BE_002_then_gradually_burn_down_repo_wide_findings
- id: TD-FE-007
  title: LaboratoryResult wire-shape mismatch between the employee-portal FE type
    and the real BCM-LAB-006 backend record
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-007-FE-001
  affected_area: employee_portal_laboratory_result_type
  file: 08-qa/technical-debt/TD-FE-007-laboratoryresult-wire-shape-mismatch.md
  remediation_strategy: closed_by_MVP_MOD_007_PORTAL_001_laboratory_result_mapper
- id: TD-IAM-001
  title: Backend has no authenticated request context or server-side authorization
    enforcement
  status: closed
  risk_level: high
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: backend_wide_request_authentication_and_authorization
  file: 08-qa/technical-debt/TD-IAM-001-backend-authentication-missing.md
  remediation_strategy: closed_by_HOP_ENT_FOUND_001_corrective_request_time_authorization_baseline
- id: TD-IAM-002
  title: Permission model is screen-level only; per-action/per-API-operation granularity
    and the domain.resource.action.scope grammar remain unmodeled
  status: materially_reduced
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: iam_permission_granularity
  file: 08-qa/technical-debt/TD-IAM-002-permission-granularity-gap.md
  remediation_strategy: materially_reduced_by_endpoint_permission_registry_api_path_action_mapping;
    further_reduced_by_COM_MOD_009_PORTAL_002_doctor_portal_ownership_enforcement_doctorid_match_and_referral_relationship_verification;
    further_reduced_by_COM_MOD_012_BE_001_SCREEN_PLATFORM_CONFIGURATION_endpoint_registry_entries
- id: TD-DB-001
  title: resultsanddigitaldelivery, documentmanagement and notificationmanagement
    have no persistent (Jdbc) schema/repository - in-memory adapters only
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: results_and_digital_delivery_persistence
  file: 08-qa/technical-debt/TD-DB-001-missing-persistence-results-digital-delivery.md
  remediation_strategy: closed_by_MVP_MOD_007_QA_001_implementing_jdbc_repositories
- id: TD-DB-002
  title: Diagnostic catalog business tables are not yet translatable (single name
    column, no es-MX/en-US variants)
  status: open
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: catalog_test_configuration_localization
  file: 08-qa/technical-debt/TD-DB-002-catalog-not-translatable.md
  remediation_strategy: gradual_when_catalog_test_configuration_is_next_touched_by_a_code_changing_backlog_item
- id: TD-DB-003
  title: No backend read API exists yet for the new country/locale/currency reference
    tables
  status: open
  risk_level: low
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: reference_data_api
  file: 08-qa/technical-debt/TD-DB-003-reference-data-api-missing.md
  remediation_strategy: implement_when_a_screen_or_client_first_needs_country_locale_currency_options
- id: TD-DB-004
  title: Tenant scoping is enforced by application-level WHERE clauses, not PostgreSQL
    native row-level security policies
  status: materially_reduced
  risk_level: low
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: tenant_isolation
  file: 08-qa/technical-debt/TD-DB-004-no-native-row-level-security.md
  remediation_strategy: materially_reduced_by_COM_MOD_012_OPS_002_tenant_impact_triage_runbook_cross_tenant_leakage_check_
    as_operational_compensating_control; further_reduced_by_COM_MOD_012_BE_001_Tenant_isolationStrategy_field_persisted_per_tenant_and_
    updateTenantStatus_suspend_archive_containment_control_TRIAGE-STEP-004B; then_release_readiness_hardening_backlog_item_for_native_RLS
- id: TD-UX-001
  title: No shared Button/FormField/DataTable component library; each of the 26 employee-portal
    screens implements its own markup
  status: open
  risk_level: low
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: employee_portal_component_reuse
  file: 08-qa/technical-debt/TD-UX-001-no-shared-component-library.md
  remediation_strategy: gradual_when_a_future_screen_backlog_item_next_touches_multiple_screens
- id: TD-UX-002
  title: No formalized responsive breakpoints/layout system; no automated accessibility
    check wired into npm run quality
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: employee_portal_responsive_and_accessibility_tooling
  file: 08-qa/technical-debt/TD-UX-002-no-responsive-accessibility-automation.md
  remediation_strategy: next_frontend_quality_profile_hardening_backlog_item
  progress_note: 'COM-MOD-011-WEB-001 implemented both acceptance criteria as the
    reference pattern in the new public-website module. COM-MOD-011-FE-001 then retrofitted
    the same pattern into employee-portal itself (this debt''s originally discovered
    affected_area): a documented mobile-first responsive breakpoint set (--hop-bp-sm/md/lg,
    styles.css) applied to .app-shell and table th/td, and an automated axe-core accessibility
    check (src/test/accessibility.test.tsx, jest-axe) covering AppShell plus the 3
    new screens, wired into npm run test / npm run quality, plus eslint-plugin-jsx-a11y
    in the lint gate (which surfaced and fixed one real finding: ConfirmDialog.tsx''s
    autoFocus). Both acceptance criteria now hold in employee-portal; closed.'
- id: TD-UX-003
  title: No mobile layout system defined yet (no renderer stack selected)
  status: open
  risk_level: low
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: mobile_app_layout
  file: 08-qa/technical-debt/TD-UX-003-no-mobile-layout-system.md
  remediation_strategy: after_TD-APP-001_renderer_selection
- id: TD-STACK-002
  title: JPA/Hibernate adoption evaluated and deferred; JdbcTemplate-behind-ports
    remains the accepted persistence baseline
  status: open
  risk_level: low
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: backend_persistence_strategy
  file: 08-qa/technical-debt/TD-STACK-002-jpa-hibernate-evaluated-deferred.md
  remediation_strategy: revisit_if_a_future_module_needs_complex_object_graph_persistence
- id: TD-STACK-003
  title: No OpenAPI-Generator-based client/server generation; contracts and hand-written
    controllers/clients kept in sync by convention and manual review
  status: materially_reduced
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: contract_first_generation
  file: 08-qa/technical-debt/TD-STACK-003-no-contract-generation-tooling.md
  remediation_strategy: materially_reduced_by_MVP_MOD_008_DEF_scheduling_BCM_PLT_005_as_the_concrete_openapi_generator_typescript_client_pilot_target_for_MVP_MOD_008_FE_001
- id: TD-STACK-004
  title: Duplicate, manually-synced Docker-init copy of platform-foundation schema.sql
    can silently drift from the authoritative Spring Boot schema source
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: HOP-ENT-FOUND-001
  affected_area: local_postgres_schema_initialization
  file: 08-qa/technical-debt/TD-STACK-004-duplicate-docker-init-schema-file.md
  remediation_strategy: closed_by_MVP_MOD_007_APP_001_removing_duplicate_init_script
- id: TD-FE-008
  title: Establish patient portal test coverage baseline and raise it to the 80 percent
    target
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-007-CLOSEOUT
  affected_area: patient_portal_test_coverage
  file: 08-qa/technical-debt/TD-FE-008-patient-portal-coverage-baseline.md
  remediation_strategy: closed_by_COM_MOD_009_PORTAL_001_patient_portal_test_coverage_89_58_percent
- id: TD-FE-009
  title: Establish doctor portal test coverage baseline and raise it to the 80 percent
    target
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-007-CLOSEOUT
  affected_area: doctor_portal_test_coverage
  file: 08-qa/technical-debt/TD-FE-009-doctor-portal-coverage-baseline.md
  remediation_strategy: closed_by_COM_MOD_009_PORTAL_002_doctor_portal_rebuilt_coverage_40_62_to_89_86_percent
- id: TD-FE-011
  title: patient-portal npm run lint fails with 2 pre-existing sonarjs/no-hardcoded-passwords
    errors
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-009-PORTAL-002
  affected_area: patient_portal_lint_gate
  file: 08-qa/technical-debt/TD-FE-011-patient-portal-lint-regression.md
  remediation_strategy: gradual_when_patient_portal_is_next_touched_rename_login_password_locale_key
- id: TD-BE-013
  title: XLSX row-level parsing not implemented for open data ingestion and migration
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: MVP-MOD-008-BE-001
  affected_area: open_data_ingestion_and_migration_file_parsing
  file: 08-qa/technical-debt/TD-BE-013-xlsx-migration-row-parsing-missing.md
  remediation_strategy: closed_by_MVP_MOD_008_BE_002_apache_poi_xlsx_row_counting
- id: TD-BE-014
  title: Migration domain-command execution has no real cross-module wiring
  status: open
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-008-BE-002
  affected_area: open_data_ingestion_and_migration_domain_command_execution
  file: 08-qa/technical-debt/TD-BE-014-migration-domain-command-cross-module-wiring-deferred.md
  remediation_strategy: gradual_when_an_owning_module_next_exposes_a_migration_import_command
- id: TD-BE-015
  title: Rate-limit enforcement is scoped to partner-API-key-bearing requests only
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: MVP-MOD-008-BE-002
  affected_area: api_management_rate_limit_enforcement
  file: 08-qa/technical-debt/TD-BE-015-rate-limit-enforcement-scoped-to-partner-keys.md
  remediation_strategy: closed_by_COM_MOD_011_BE_001_PublicApiRateLimitInterceptor_and_RateLimitPolicy_consumerIdentificationMethod_
    enforce_rate_limits_for_anonymous_public_traffic_via_ip_address_or_session_token_verified_by_PublicWebApiTest
- id: TD-FE-010
  title: Employee-portal generated administration screens exceed function-size and
    complexity warning thresholds
  status: materially_reduced
  risk_level: medium
  blocking: false
  source_backlog_item: MVP-MOD-008-FE-001
  affected_area: employee_portal_admin_screen_composition
  file: 08-qa/technical-debt/TD-FE-010-employee-portal-admin-screen-composition.md
  remediation_strategy: preferred_remediation_pattern_implemented_and_proven_on_11_com_mod_010_screens_in
    COM-MOD-010-FE-001;_COM-MOD-013-FE-001_added_tests_and_boundaries_but_lint_still_reports
    non_blocking_long_function_warnings_for_new_and_legacy_admin_screens;_COM-MOD-013-QA-001
    extracted_DocumentsSection_from_ComplianceEvidenceScreen_bringing_it_under_the_120_line
    threshold_51_to_50_warnings;_continue_component extraction_when_employee_portal_admin_screens_are_next_touched
- id: TD-BE-016
  title: BCM-PLT-007 searchAuditEvents/exportAuditEvents openapi-source.md operations
    not fully compiled
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-012-BE-001
  affected_area: audit_trail_api_surface
  file: 08-qa/technical-debt/TD-BE-016-audit-trail-search-export-not-fully-compiled.md
  remediation_strategy: closed_by_COM_MOD_013_BE_001_audit_events_export_and_filtered_search
- id: TD-BE-017
  title: BCM-PLT-009 Workflow Engine (listWorkflowExecutions/triggerWorkflow/rollbackWorkflow)
    not implemented
  status: open
  risk_level: medium
  blocking: false
  source_backlog_item: COM-MOD-012-BE-001
  affected_area: operational_workflow_orchestration
  file: 08-qa/technical-debt/TD-BE-017-workflow-engine-not-implemented.md
  remediation_strategy: gradual_dedicated_backlog_item_once_a_real_orchestration_target_exists
- id: TD-IAM-003
  title: BCM-PLT-001 MFA, service-account credentials and the domain.resource.action.scope
    permission grammar are not implemented
  status: open
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-012-BE-001
  affected_area: identity_access_extensions
  file: 08-qa/technical-debt/TD-IAM-003-mfa-service-account-scope-grammar-not-implemented.md
  remediation_strategy: gradual_when_a_future_backlog_item_next_touches_identityaccess
- id: TD-OBS-001
  title: Distributed trace export, provisioned Grafana/Prometheus/Loki stack and SLO/SLA
    alerting backend not implemented
  status: open
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-012-QA-001
  affected_area: distributed_tracing_metrics_stack_and_alerting
  file: 08-qa/technical-debt/TD-OBS-001-distributed-tracing-and-observability-stack-not-provisioned.md
  remediation_strategy: gradual_dedicated_observability_infrastructure_backlog_item
- id: TD-QA-005
  title: A null byte or oversized value in a request field or query parameter caused
    an unhandled 500 instead of a 400 across multiple modules
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-012-QA-001
  affected_area: cross_cutting_jdbc_string_parameter_encoding_validation
  file: 08-qa/technical-debt/TD-QA-005-null-byte-query-parameter-unhandled-500.md
  remediation_strategy: closed_by_COM_MOD_012_QA_001_GlobalExceptionHandler_SQLState_class_22_mapping
- id: TD-QA-006
  title: AuthController.initiateAssistance returned an unhandled 500 for a nonexistent
    assistedUserId instead of 404
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-012-QA-001
  affected_area: identityaccess_exception_advice_scope
  file: 08-qa/technical-debt/TD-QA-006-authcontroller-not-found-exception-unmapped-500.md
  remediation_strategy: closed_by_COM_MOD_012_QA_001_widened_exception_advice_assignableTypes
- id: TD-DB-005
  title: COM-MOD-013 quality/compliance backend silently used in-memory storage instead
    of PostgreSQL
  status: closed
  risk_level: high
  blocking: false
  source_backlog_item: COM-MOD-013-QA-001
  affected_area: external_quality_capa_audit_and_quality_event_intake_persistence
  file: 08-qa/technical-debt/TD-DB-005-quality-compliance-persistence-never-wired.md
  remediation_strategy: closed_by_COM_MOD_013_QA_001_schema_sql_registration_and_inverted_Profile_wiring_correction
- id: TD-IAM-004
  title: External Quality/CAPA/Audit/Document-Management controllers assign a synthetic
    random TenantId instead of the authenticated request tenant
  status: open
  risk_level: medium
  blocking: false
  source_backlog_item: COM-MOD-013-QA-001
  affected_area: quality_compliance_and_document_management_tenant_traceability
  file: 08-qa/technical-debt/TD-IAM-004-quality-compliance-controllers-synthetic-tenant.md
  remediation_strategy: gradual_when_a_shared_tenant_context_port_is_next_designed_or_when_these_controllers_are_next_touched
- id: TD-QA-007
  title: Malformed/truncated multipart upload caused an unhandled 500 instead of 400
    on POST /api/documents
  status: closed
  risk_level: medium
  blocking: false
  source_backlog_item: COM-MOD-013-QA-001
  affected_area: document_management_multipart_upload
  file: 08-qa/technical-debt/TD-QA-007-multipart-upload-abrupt-disconnect-unhandled-500.md
  remediation_strategy: closed_by_COM_MOD_013_QA_001_MultipartException_handler_and_DAST_rescan
- id: TD-QA-008
  title: OWASP ZAP local availability is undocumented in the toolchain inventory and
    baseline
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-016-QA-001
  affected_area: local_quality_toolchain_inventory
  file: 08-qa/technical-debt/TD-QA-008-zap-tooling-undocumented-in-inventory.md
  remediation_strategy: closed_by_COM_MOD_017_BE_002_inventory_and_baseline_doc_correction
- id: TD-BE-018
  title: Marketplace entitlement policy, compatibility strategy, billing adapter and
    installation rollback orchestration are basic implementations only
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-017-BE-001
  affected_area: marketplace_entitlements_custom_implementation_points
  file: 08-qa/technical-debt/TD-BE-018-marketplace-entitlement-policy-and-billing-adapter-basic-only.md
  remediation_strategy: closed_by_COM_MOD_017_BE_002_plus_COM_MOD_017_FE_001_TD_BE_019_closure_confirmed_by_COM_MOD_017_QA_001
- id: TD-BE-019
  title: Marketplace runtime feature-availability is not wired into IAM permission
    evaluation or employee-portal menu generation
  status: closed
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-017-BE-002
  affected_area: marketplace_entitlements_runtime_feature_availability
  file: 08-qa/technical-debt/TD-BE-019-marketplace-runtime-feature-availability-not-wired-into-iam-or-menu.md
  remediation_strategy: closed_by_COM_MOD_017_FE_001_marketplace_screen_iam_wiring_plus_entitlement_gated_install_control
- id: TD-BE-020
  title: local profile silently had no real datasource because DataSourceAutoConfiguration
    stayed globally excluded after the YAML-to-properties migration
  status: closed
  risk_level: high
  blocking: false
  source_backlog_item: COM-MOD-017-BE-002
  affected_area: local_profile_spring_boot_datasource_autoconfiguration
  file: 08-qa/technical-debt/TD-BE-020-local-profile-datasource-autoconfiguration-excluded-by-format-migration.md
  remediation_strategy: closed_by_COM_MOD_017_BE_002_adding_spring.autoconfigure.exclude=_override_to_application-local.properties
- id: TD-FE-012
  title: employee-portal npm audit reports 10 high-severity findings confined to
    transitive devDependencies, requiring a breaking-change fix
  status: open
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-017-FE-001
  affected_area: employee_portal_npm_audit_gate
  file: 08-qa/technical-debt/TD-FE-012-employee-portal-npm-audit-devdependency-high-severity-findings.md
  remediation_strategy: gradual_when_a_dedicated_devdependency_maintenance_backlog_item_is_scheduled
- id: TD-WEB-001
  title: Public marketplace listing surface (PUBLIC_MARKETPLACE_LISTING) modeled
    by BCM-PLT-011 but never compiled
  status: open
  risk_level: low
  blocking: false
  source_backlog_item: COM-MOD-017-CLOSEOUT
  affected_area: bcm_plt_011_public_website_marketplace_listing
  file: 08-qa/technical-debt/TD-WEB-001-marketplace-public-listing-surface-not-implemented.md
  remediation_strategy: gradual_dedicated_backlog_item_com_mod_017_web_001
```
