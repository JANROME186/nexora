---
id: HOP-SOT-001
format: markdown_structured_payload
type: source-of-truth-registry
version: 1.0.0
status: approved
---

# Hop Sot 001

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SOT-001
  type: source-of-truth-registry
  version: 1.0.0
  status: approved
sources:
  business_requirement: BUSINESS_REQUIREMENT.md
  business_requirement_yaml: BUSINESS_REQUIREMENT.md
  business_requirement_index: 00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
  business_requirement_index_md: 00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
  business_requirement_to_yaml_prompt: 04-requirements/prompts/business-requirement-to-yaml-prompt.md
  business_requirement_to_yaml_prompt_yaml: 04-requirements/prompts/business-requirement-to-yaml-prompt.md
  project_brief: PROJECT_BRIEF.md
  project_brief_yaml: PROJECT_BRIEF.md
  project_state: PROJECT_STATE.md
  ordered_development_guide: ORDERED_DEVELOPMENT_GUIDE.md
  ordered_development_guide_yaml: ORDERED_DEVELOPMENT_GUIDE.md
  agent_agnostic_standard: ../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
  model_driven_product_engineering_standard: ../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  capability_package_standard: ../../nexora-framework/02-standards/standards/capability-package-standard.md
  open_data_ingestion_standard: ../../nexora-framework/02-standards/standards/open-data-ingestion-standard.md
  product_marketplace_standard: ../../nexora-framework/02-standards/standards/product-marketplace-standard.md
  business_requirement_versioning_standard: ../../nexora-framework/02-standards/standards/business-requirement-versioning-standard.md
  open_source_first_security_quality_standard: ../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  context_efficient_execution_standard: ../../nexora-framework/02-standards/standards/context-efficient-execution-standard.md
  context_efficient_execution_standard_md: ../../nexora-framework/02-standards/standards/context-efficient-execution-standard.md
  frontmatter_artifact_migration_standard: ../../nexora-framework/02-standards/standards/frontmatter-artifact-migration-standard.md
  frontmatter_artifact_migration_standard_md: ../../nexora-framework/02-standards/standards/frontmatter-artifact-migration-standard.md
  context_orchestrator_python: ../../nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
  backlog_closure_validator_python: ../../nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
  frontmatter_migrator_python: ../../nexora-framework/08-engineering/agents/context-orchestrator/frontmatter_migrator.py
  context_orchestrator_python_requirements: ../../nexora-framework/08-engineering/agents/context-orchestrator/requirements.txt
  context_orchestrator_runbook: ../../nexora-framework/08-engineering/agents/context-orchestrator/README.md
  nxf_ctx_002_handoff: 08-qa/handoffs/NXF-CTX-002-summary.md
  generated_backlog_prompt_com_mod_017_be_001: 08-qa/generated-prompts/COM-MOD-017-BE-001-prompt.md
  generated_backlog_prompt_com_mod_017_be_001_cache: 08-qa/generated-prompts/cache/COM-MOD-017-BE-001-prompt-cache.json
  backlog_closure_validation_com_mod_017_be_001: 08-qa/backlog-validations/COM-MOD-017-BE-001-closure-validation.md
  nxf_fmt_002_prompt: 08-qa/generated-prompts/NXF-FMT-002-prompt.md
  nxf_fmt_002_prompt_cache: 08-qa/generated-prompts/cache/NXF-FMT-002-prompt-cache.json
  nxf_fmt_002_validation: 08-qa/format-migration/NXF-FMT-002-validation.md
  nxf_fmt_002_migration_plan: 08-qa/format-migration/frontmatter-migration-plan.md
  nxf_fmt_002_handoff: 08-qa/handoffs/NXF-FMT-002-summary.md
  frontmatter_migration_report_hop: 08-qa/format-migration/frontmatter-migration-report-projects-healthcare-operations-platform.md
  frontmatter_migration_report_framework: 08-qa/format-migration/frontmatter-migration-report-nexora-framework.md
  generated_backlog_prompt_com_mod_017_be_002: 08-qa/generated-prompts/COM-MOD-017-BE-002-prompt.md
  generated_backlog_prompt_com_mod_017_be_002_cache: 08-qa/generated-prompts/cache/COM-MOD-017-BE-002-prompt-cache.json
  context_optimized_backlog_prompts_yaml: ../../nexora-framework/05-prompts/prompts/context-optimized-backlog-prompts.md
  context_optimized_backlog_prompts_md: ../../nexora-framework/05-prompts/prompts/context-optimized-backlog-prompts.md
  enterprise_product_foundation_standard: ../../nexora-framework/02-standards/standards/enterprise-product-foundation-standard.md
  integrated_local_solution_runbook_standard: ../../nexora-framework/02-standards/standards/integrated-local-solution-runbook-standard.md
  framework_feedback_continuous_improvement_standard: ../../nexora-framework/02-standards/standards/framework-feedback-continuous-improvement-standard.md
  business_requirement_impact_prompts_yaml: ../../nexora-framework/05-prompts/prompts/business-requirement-impact-prompts.md
  security_quality_gate_prompts_yaml: ../../nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md
  integrated_local_runbook_prompts_yaml: ../../nexora-framework/05-prompts/prompts/integrated-local-runbook-prompts.md
  framework_feedback_prompts_yaml: ../../nexora-framework/05-prompts/prompts/framework-feedback-prompts.md
  product_definition: 01-product-definition/products/healthcare-operations-platform/product.md
  business_capability_map: 01-product-definition/business-capabilities/bcm-001/business-capability-map.md
  capability_dependency_map: 01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md
  capability_package_index: 01-product-definition/business-capabilities/packages/capability-package-index.md
  capability_packages_root: 01-product-definition/business-capabilities/packages/
  com_mod_010_pkg_bcm_inv_001: 01-product-definition/business-capabilities/packages/bcm-inv-001-product-catalog/
  com_mod_010_pkg_bcm_inv_002: 01-product-definition/business-capabilities/packages/bcm-inv-002-reagent-management/
  com_mod_010_pkg_bcm_inv_003: 01-product-definition/business-capabilities/packages/bcm-inv-003-lot-management/
  com_mod_010_pkg_bcm_inv_004: 01-product-definition/business-capabilities/packages/bcm-inv-004-procurement-management/
  com_mod_010_pkg_bcm_inv_005: 01-product-definition/business-capabilities/packages/bcm-inv-005-stock-entries/
  com_mod_010_pkg_bcm_inv_006: 01-product-definition/business-capabilities/packages/bcm-inv-006-stock-exits/
  com_mod_010_pkg_bcm_inv_007: 01-product-definition/business-capabilities/packages/bcm-inv-007-consumption-tracking/
  com_mod_010_pkg_bcm_inv_008: 01-product-definition/business-capabilities/packages/bcm-inv-008-inventory-adjustments/
  com_mod_010_pkg_bcm_inv_009: 01-product-definition/business-capabilities/packages/bcm-inv-009-waste-management/
  com_mod_010_pkg_bcm_qlt_001: 01-product-definition/business-capabilities/packages/bcm-qlt-001-internal-quality-controls/
  com_mod_010_pkg_bcm_qlt_003: 01-product-definition/business-capabilities/packages/bcm-qlt-003-calibration-management/
  com_mod_010_pkg_bcm_qlt_004: 01-product-definition/business-capabilities/packages/bcm-qlt-004-equipment-management/
  com_mod_010_pkg_bcm_qlt_005: 01-product-definition/business-capabilities/packages/bcm-qlt-005-maintenance-management/
  com_mod_010_def_qa_evidence: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-DEF-validation.md
  com_mod_010_def_qa_evidence_yaml: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-DEF-validation.md
  com_mod_010_def_security_quality_evidence: 08-qa/security-quality/COM-MOD-010-DEF/security-quality-evidence.md
  com_mod_010_def_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-010-DEF/security-quality-evidence.md
  com_mod_010_be_001_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/inventoryquality/
  com_mod_010_be_001_schema: 07-implementation/backend/src/main/resources/db/inventory-and-internal-quality/schema.sql
  com_mod_010_be_001_qa_evidence: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-BE-001-validation.md
  com_mod_010_be_001_qa_evidence_yaml: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-BE-001-validation.md
  com_mod_010_be_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-010-BE-001/security-quality-evidence.md
  com_mod_010_be_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-010-BE-001/security-quality-evidence.md
  com_mod_010_be_002_qa_evidence: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-BE-002-validation.md
  com_mod_010_be_002_qa_evidence_yaml: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-BE-002-validation.md
  com_mod_010_be_002_security_quality_evidence: 08-qa/security-quality/COM-MOD-010-BE-002/security-quality-evidence.md
  com_mod_010_be_002_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-010-BE-002/security-quality-evidence.md
  com_mod_010_fe_001_employee_portal_api: 07-implementation/employee-portal/src/api/inventoryQualityApi.ts
  com_mod_010_fe_001_employee_portal_screens: 07-implementation/employee-portal/src/components/screens/
  com_mod_010_fe_001_qa_evidence: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-FE-001-validation.md
  com_mod_010_fe_001_qa_evidence_yaml: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-FE-001-validation.md
  com_mod_010_fe_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-010-FE-001/security-quality-evidence.md
  com_mod_010_fe_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-010-FE-001/security-quality-evidence.md
  com_mod_010_qa_001_qa_evidence: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-QA-001-validation.md
  com_mod_010_qa_001_qa_evidence_yaml: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-QA-001-validation.md
  com_mod_010_qa_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-010-QA-001/security-quality-evidence.md
  com_mod_010_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-010-QA-001/security-quality-evidence.md
  com_mod_010_closeout_evidence: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-CLOSEOUT-validation.md
  com_mod_010_closeout_evidence_yaml: 08-qa/qa/inventory-and-internal-quality/COM-MOD-010-CLOSEOUT-validation.md
  com_mod_010_closeout_security_quality_evidence: 08-qa/security-quality/COM-MOD-010-CLOSEOUT/security-quality-evidence.md
  com_mod_010_closeout_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-010-CLOSEOUT/security-quality-evidence.md
  com_mod_011_def_qa_evidence: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-DEF-validation.md
  com_mod_011_def_qa_evidence_yaml: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-DEF-validation.md
  com_mod_011_be_001_backend_publicweb: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/publicweb/
  com_mod_011_be_001_catalog_public_read_port: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/catalogtestconfiguration/publicreads/
  com_mod_011_be_001_frontdesk_public_intake_port: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/publicintake/
  com_mod_011_be_001_public_rate_limit_interceptor: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/integrationinteroperability/apimanagement/adapter/in/web/PublicApiRateLimitInterceptor.java
  com_mod_011_be_001_public_web_api_test: 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/publicweb/PublicWebApiTest.java
  com_mod_011_be_001_qa_evidence: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-BE-001-validation.md
  com_mod_011_be_001_qa_evidence_yaml: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-BE-001-validation.md
  com_mod_011_be_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-011-BE-001/security-quality-evidence.md
  com_mod_011_be_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-011-BE-001/security-quality-evidence.md
  com_mod_011_pkg_bcm_svc_001: 01-product-definition/business-capabilities/packages/bcm-svc-001-diagnostic-service-catalog/
  com_mod_011_pkg_bcm_svc_002: 01-product-definition/business-capabilities/packages/bcm-svc-002-test-catalog/
  com_mod_011_pkg_bcm_svc_003: 01-product-definition/business-capabilities/packages/bcm-svc-003-panel-catalog/
  com_mod_011_pkg_bcm_svc_005: 01-product-definition/business-capabilities/packages/bcm-svc-005-patient-preparation-management/
  com_mod_011_pkg_bcm_att_001: 01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/
  com_mod_011_pkg_bcm_att_006: 01-product-definition/business-capabilities/packages/bcm-att-006-quotation-management/
  com_mod_011_pkg_bcm_plt_005: 01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/
  com_mod_011_web_001_public_website: 07-implementation/public-website/
  com_mod_011_web_001_api_client: 07-implementation/public-website/src/api/
  com_mod_011_web_001_site_config: 07-implementation/public-website/src/config/siteConfig.ts
  com_mod_011_web_001_accessibility_test: 07-implementation/public-website/src/test/accessibility.test.tsx
  com_mod_011_web_001_qa_evidence: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-WEB-001-validation.md
  com_mod_011_web_001_qa_evidence_yaml: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-WEB-001-validation.md
  com_mod_011_web_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-011-WEB-001/security-quality-evidence.md
  com_mod_011_web_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-011-WEB-001/security-quality-evidence.md
  com_mod_011_fe_001_public_content_api: 07-implementation/employee-portal/src/api/publicContentApi.ts
  com_mod_011_fe_001_public_requests_api: 07-implementation/employee-portal/src/api/publicRequestsApi.ts
  com_mod_011_fe_001_content_review_screen: 07-implementation/employee-portal/src/components/screens/PublicContentReviewScreen.tsx
  com_mod_011_fe_001_appointment_requests_screen: 07-implementation/employee-portal/src/components/screens/PublicAppointmentRequestsScreen.tsx
  com_mod_011_fe_001_quotation_requests_screen: 07-implementation/employee-portal/src/components/screens/PublicQuotationRequestsScreen.tsx
  com_mod_011_fe_001_accessibility_test: 07-implementation/employee-portal/src/test/accessibility.test.tsx
  com_mod_011_fe_001_qa_evidence: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-FE-001-validation.md
  com_mod_011_fe_001_qa_evidence_yaml: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-FE-001-validation.md
  com_mod_011_fe_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-011-FE-001/security-quality-evidence.md
  com_mod_011_fe_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-011-FE-001/security-quality-evidence.md
  com_mod_011_qa_001_qa_evidence: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-QA-001-validation.md
  com_mod_011_qa_001_qa_evidence_yaml: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-QA-001-validation.md
  com_mod_011_qa_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-011-QA-001/security-quality-evidence.md
  com_mod_011_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-011-QA-001/security-quality-evidence.md
  com_mod_011_closeout_qa_evidence: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-CLOSEOUT-validation.md
  com_mod_011_closeout_qa_evidence_yaml: 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-CLOSEOUT-validation.md
  com_mod_011_closeout_security_quality_evidence: 08-qa/security-quality/COM-MOD-011-CLOSEOUT/security-quality-evidence.md
  com_mod_011_closeout_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-011-CLOSEOUT/security-quality-evidence.md
  com_mod_012_def_qa_evidence: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-DEF-validation.md
  com_mod_012_def_qa_evidence_yaml: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-DEF-validation.md
  com_mod_012_def_security_quality_evidence: 08-qa/security-quality/COM-MOD-012-DEF/security-quality-evidence.md
  com_mod_012_def_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-012-DEF/security-quality-evidence.md
  com_mod_012_ops_001_deployment_strategy: 09-operations/deployment/production-deployment-strategy.md
  com_mod_012_ops_001_deployment_strategy_yaml: 09-operations/deployment/production-deployment-strategy.md
  com_mod_012_ops_001_environment_matrix: 09-operations/deployment/environment-matrix.md
  com_mod_012_ops_001_environment_matrix_yaml: 09-operations/deployment/environment-matrix.md
  com_mod_012_ops_001_deployment_readiness_checklist: 09-operations/deployment/deployment-readiness-checklist.md
  com_mod_012_ops_001_deployment_readiness_checklist_yaml: 09-operations/deployment/deployment-readiness-checklist.md
  com_mod_012_ops_001_qa_evidence: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-OPS-001-validation.md
  com_mod_012_ops_001_qa_evidence_yaml: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-OPS-001-validation.md
  com_mod_012_ops_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-012-OPS-001/security-quality-evidence.md
  com_mod_012_ops_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-012-OPS-001/security-quality-evidence.md
  com_mod_012_ops_002_runbooks_root: 09-operations/runbooks/
  com_mod_012_ops_002_observability_runbook_yaml: 09-operations/runbooks/observability-runbook.md
  com_mod_012_ops_002_health_readiness_liveness_runbook_yaml: 09-operations/runbooks/health-readiness-liveness-runbook.md
  com_mod_012_ops_002_metrics_logs_traces_validation_runbook_yaml: 09-operations/runbooks/metrics-logs-traces-validation-runbook.md
  com_mod_012_ops_002_backup_runbook_yaml: 09-operations/runbooks/backup-runbook.md
  com_mod_012_ops_002_restore_runbook_yaml: 09-operations/runbooks/restore-runbook.md
  com_mod_012_ops_002_incident_response_runbook_yaml: 09-operations/runbooks/incident-response-runbook.md
  com_mod_012_ops_002_rollback_incident_handoff_runbook_yaml: 09-operations/runbooks/rollback-incident-handoff-runbook.md
  com_mod_012_ops_002_tenant_impact_triage_runbook_yaml: 09-operations/runbooks/tenant-impact-triage-runbook.md
  com_mod_012_ops_002_evidence_collection_runbook_yaml: 09-operations/runbooks/evidence-collection-runbook.md
  com_mod_012_ops_002_post_incident_review_runbook_yaml: 09-operations/runbooks/post-incident-review-runbook.md
  com_mod_012_ops_002_qa_evidence: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-OPS-002-validation.md
  com_mod_012_ops_002_qa_evidence_yaml: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-OPS-002-validation.md
  com_mod_012_ops_002_security_quality_evidence: 08-qa/security-quality/COM-MOD-012-OPS-002/security-quality-evidence.md
  com_mod_012_ops_002_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-012-OPS-002/security-quality-evidence.md
  com_mod_012_be_001_backend_organizationmanagement: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/organizationmanagement/
  com_mod_012_be_001_backend_platformconfiguration: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/platformconfiguration/
  com_mod_012_be_001_backend_observability: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/observability/
  com_mod_012_be_001_schema: 07-implementation/backend/src/main/resources/db/platform-hardening-and-saas-operations/schema.sql
  com_mod_012_be_001_qa_evidence: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-BE-001-validation.md
  com_mod_012_be_001_qa_evidence_yaml: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-BE-001-validation.md
  com_mod_012_be_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-012-BE-001/security-quality-evidence.md
  com_mod_012_be_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-012-BE-001/security-quality-evidence.md
  com_mod_012_qa_001_qa_evidence: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-QA-001-validation.md
  com_mod_012_qa_001_qa_evidence_yaml: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-QA-001-validation.md
  com_mod_012_qa_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-012-QA-001/security-quality-evidence.md
  com_mod_012_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-012-QA-001/security-quality-evidence.md
  com_mod_012_qa_001_zap_backend_api_report: 08-qa/security-quality/COM-MOD-012-QA-001/zap-backend-api.html
  com_mod_012_qa_001_zap_employee_portal_report: 08-qa/security-quality/COM-MOD-012-QA-001/zap-employee-portal.html
  com_mod_012_closeout_qa_evidence: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-CLOSEOUT-validation.md
  com_mod_012_closeout_qa_evidence_yaml: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-CLOSEOUT-validation.md
  com_mod_012_closeout_security_quality_evidence: 08-qa/security-quality/COM-MOD-012-CLOSEOUT/security-quality-evidence.md
  com_mod_012_closeout_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-012-CLOSEOUT/security-quality-evidence.md
  com_mod_013_def_qa_evidence: 08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-DEF-validation.md
  com_mod_013_def_qa_evidence_yaml: 08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-DEF-validation.md
  com_mod_013_def_security_quality_evidence: 08-qa/security-quality/COM-MOD-013-DEF/security-quality-evidence.md
  com_mod_013_def_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-013-DEF/security-quality-evidence.md
  com_mod_013_fe_001_qa_evidence: 08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-FE-001-validation.md
  com_mod_013_fe_001_qa_evidence_yaml: 08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-FE-001-validation.md
  com_mod_013_fe_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-013-FE-001/security-quality-evidence.md
  com_mod_013_fe_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-013-FE-001/security-quality-evidence.md
  com_mod_013_qa_001_qa_evidence: 08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-QA-001-validation.md
  com_mod_013_qa_001_qa_evidence_yaml: 08-qa/qa/advanced-quality-and-compliance/COM-MOD-013-QA-001-validation.md
  com_mod_013_qa_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-013-QA-001/security-quality-evidence.md
  com_mod_013_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-013-QA-001/security-quality-evidence.md
  com_mod_016_def_qa_evidence: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-DEF-validation.md
  com_mod_016_def_qa_evidence_yaml: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-DEF-validation.md
  com_mod_016_def_security_quality_evidence: 08-qa/security-quality/COM-MOD-016-DEF/security-quality-evidence.md
  com_mod_016_def_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-016-DEF/security-quality-evidence.md
  com_mod_016_pkg_bcm_org_001: 01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/
  com_mod_016_pkg_bcm_org_002: 01-product-definition/business-capabilities/packages/bcm-org-002-laboratory-management/
  com_mod_016_pkg_bcm_org_003: 01-product-definition/business-capabilities/packages/bcm-org-003-branch-management/
  com_mod_016_pkg_bcm_plt_002: 01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/
  com_mod_016_pkg_bcm_plt_006: 01-product-definition/business-capabilities/packages/bcm-plt-006-observability/
  com_mod_016_pkg_bcm_plt_007: 01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/
  com_mod_016_pkg_bcm_plt_008: 01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/
  technical_debt_td_db_005: 08-qa/technical-debt/TD-DB-005-quality-compliance-persistence-never-wired.md
  technical_debt_td_iam_004: 08-qa/technical-debt/TD-IAM-004-quality-compliance-controllers-synthetic-tenant.md
  com_mod_013_pkg_bcm_qlt_002: 01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/
  com_mod_013_pkg_bcm_qlt_006: 01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/
  com_mod_013_pkg_bcm_qlt_007: 01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/
  com_mod_013_pkg_bcm_plt_007: 01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/
  com_mod_013_pkg_bcm_plt_008: 01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/
  technical_debt_td_obs_001: 08-qa/technical-debt/TD-OBS-001-distributed-tracing-and-observability-stack-not-provisioned.md
  technical_debt_td_qa_005: 08-qa/technical-debt/TD-QA-005-null-byte-query-parameter-unhandled-500.md
  technical_debt_td_qa_006: 08-qa/technical-debt/TD-QA-006-authcontroller-not-found-exception-unmapped-500.md
  com_mod_012_pkg_bcm_org_001: 01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/
  com_mod_012_pkg_bcm_plt_001: 01-product-definition/business-capabilities/packages/bcm-plt-001-identity-and-access-management/
  com_mod_012_pkg_bcm_plt_002: 01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/
  com_mod_012_pkg_bcm_plt_005: 01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/
  com_mod_012_pkg_bcm_plt_006: 01-product-definition/business-capabilities/packages/bcm-plt-006-observability/
  com_mod_012_pkg_bcm_plt_007: 01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/
  com_mod_012_pkg_bcm_plt_008: 01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/
  com_mod_012_pkg_bcm_plt_009: 01-product-definition/business-capabilities/packages/bcm-plt-009-workflow-engine/
  mvp_mod_002_pkg_bcm_svc_001: 01-product-definition/business-capabilities/packages/bcm-svc-001-diagnostic-service-catalog/
  mvp_mod_002_pkg_bcm_svc_002: 01-product-definition/business-capabilities/packages/bcm-svc-002-test-catalog/
  mvp_mod_002_pkg_bcm_svc_003: 01-product-definition/business-capabilities/packages/bcm-svc-003-panel-catalog/
  mvp_mod_002_pkg_bcm_svc_004: 01-product-definition/business-capabilities/packages/bcm-svc-004-analyte-catalog/
  mvp_mod_002_pkg_bcm_svc_005: 01-product-definition/business-capabilities/packages/bcm-svc-005-patient-preparation-management/
  mvp_mod_002_pkg_bcm_svc_006: 01-product-definition/business-capabilities/packages/bcm-svc-006-reference-range-management/
  mvp_mod_002_pkg_bcm_svc_007: 01-product-definition/business-capabilities/packages/bcm-svc-007-sample-catalog/
  mvp_mod_002_pkg_bcm_svc_009: 01-product-definition/business-capabilities/packages/bcm-svc-009-price-list-management/
  mvp_mod_003_pkg_bcm_per_001: 01-product-definition/business-capabilities/packages/bcm-per-001-person-management/
  mvp_mod_003_pkg_bcm_per_002: 01-product-definition/business-capabilities/packages/bcm-per-002-patient-management/
  mvp_mod_003_pkg_bcm_per_003: 01-product-definition/business-capabilities/packages/bcm-per-003-doctor-management/
  mvp_mod_003_pkg_bcm_att_002: 01-product-definition/business-capabilities/packages/bcm-att-002-patient-registration/
  mvp_mod_004_pkg_bcm_att_001: 01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/
  mvp_mod_004_pkg_bcm_att_003: 01-product-definition/business-capabilities/packages/bcm-att-003-reception-management/
  mvp_mod_004_pkg_bcm_att_004: 01-product-definition/business-capabilities/packages/bcm-att-004-admission-management/
  mvp_mod_004_pkg_bcm_att_006: 01-product-definition/business-capabilities/packages/bcm-att-006-quotation-management/
  mvp_mod_004_pkg_bcm_lab_001: 01-product-definition/business-capabilities/packages/bcm-lab-001-diagnostic-order-management/
  mvp_mod_004_def_qa_evidence: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-DEF-validation.md
  mvp_mod_004_def_qa_evidence_yaml: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-DEF-validation.md
  mvp_mod_004_be_001_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/
  mvp_mod_004_be_001_schema: 07-implementation/backend/src/main/resources/db/front-desk-care-delivery/schema.sql
  mvp_mod_004_be_001_qa_evidence: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-BE-001-validation.md
  mvp_mod_004_be_001_qa_evidence_yaml: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-BE-001-validation.md
  mvp_mod_004_be_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-004-BE-001/security-quality-evidence.md
  mvp_mod_004_be_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-004-BE-001/security-quality-evidence.md
  mvp_mod_004_be_002_qa_evidence: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-BE-002-validation.md
  mvp_mod_004_be_002_qa_evidence_yaml: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-BE-002-validation.md
  mvp_mod_004_be_002_security_quality_evidence: 08-qa/security-quality/MVP-MOD-004-BE-002/security-quality-evidence.md
  mvp_mod_004_be_002_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-004-BE-002/security-quality-evidence.md
  mvp_mod_004_fe_001_qa_evidence: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-FE-001-validation.md
  mvp_mod_004_fe_001_qa_evidence_yaml: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-FE-001-validation.md
  mvp_mod_004_fe_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-004-FE-001/security-quality-evidence.md
  mvp_mod_004_fe_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-004-FE-001/security-quality-evidence.md
  mvp_mod_004_qa_001_qa_evidence: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-QA-001-validation.md
  mvp_mod_004_qa_001_qa_evidence_yaml: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-QA-001-validation.md
  mvp_mod_004_qa_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-004-QA-001/security-quality-evidence.md
  mvp_mod_004_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-004-QA-001/security-quality-evidence.md
  mvp_mod_004_closeout_evidence: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-CLOSEOUT.md
  mvp_mod_004_closeout_evidence_yaml: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-CLOSEOUT.md
  mvp_mod_004_closeout_security_quality_evidence: 08-qa/security-quality/MVP-MOD-004-CLOSEOUT/security-quality-evidence.md
  mvp_mod_004_closeout_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-004-CLOSEOUT/security-quality-evidence.md
  mvp_mod_005_pkg_bcm_att_005: 01-product-definition/business-capabilities/packages/bcm-att-005-cashier-operations/
  mvp_mod_005_pkg_bcm_att_008: 01-product-definition/business-capabilities/packages/bcm-att-008-billing-request-management/
  mvp_mod_005_def_qa_evidence: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-DEF-validation.md
  mvp_mod_005_def_qa_evidence_yaml: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-DEF-validation.md
  mvp_mod_005_be_001_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/cashsales/
  mvp_mod_005_be_001_schema: 07-implementation/backend/src/main/resources/db/cash-sales/schema.sql
  mvp_mod_005_be_001_qa_evidence: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-BE-001-validation.md
  mvp_mod_005_be_001_qa_evidence_yaml: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-BE-001-validation.md
  mvp_mod_005_be_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-005-BE-001/security-quality-evidence.md
  mvp_mod_005_be_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-005-BE-001/security-quality-evidence.md
  mvp_mod_005_be_002_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/cashsales/
  mvp_mod_005_be_002_frontdesk_sale_source_port: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery/application/FrontDeskSaleSourcePort.java
  mvp_mod_005_be_002_fiscal_adapter_port: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/cashsales/billingrequestmanagement/domain/FiscalAdapterPort.java
  mvp_mod_005_be_002_qa_evidence: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-BE-002-validation.md
  mvp_mod_005_be_002_qa_evidence_yaml: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-BE-002-validation.md
  mvp_mod_005_be_002_security_quality_evidence: 08-qa/security-quality/MVP-MOD-005-BE-002/security-quality-evidence.md
  mvp_mod_005_be_002_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-005-BE-002/security-quality-evidence.md
  mvp_mod_005_fe_001_employee_portal_implementation: 07-implementation/employee-portal/src/components/screens/CashSessionsScreen.tsx,
    07-implementation/employee-portal/src/components/screens/SalesScreen.tsx, 07-implementation/employee-portal/src/components/screens/BillingRequestsScreen.tsx
  mvp_mod_005_fe_001_cash_sales_api: 07-implementation/employee-portal/src/api/cashSalesApi.ts
  mvp_mod_005_fe_001_qa_evidence: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-FE-001-validation.md
  mvp_mod_005_fe_001_qa_evidence_yaml: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-FE-001-validation.md
  mvp_mod_005_fe_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-005-FE-001/security-quality-evidence.md
  mvp_mod_005_fe_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-005-FE-001/security-quality-evidence.md
  mvp_mod_005_qa_001_qa_evidence: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-QA-001-validation.md
  mvp_mod_005_qa_001_qa_evidence_yaml: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-QA-001-validation.md
  mvp_mod_005_qa_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-005-QA-001/security-quality-evidence.md
  mvp_mod_005_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-005-QA-001/security-quality-evidence.md
  mvp_mod_005_closeout_evidence: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-CLOSEOUT.md
  mvp_mod_005_closeout_evidence_yaml: 08-qa/qa/cashier-and-billing-request/MVP-MOD-005-CLOSEOUT.md
  mvp_mod_005_closeout_security_quality_evidence: 08-qa/security-quality/MVP-MOD-005-CLOSEOUT/security-quality-evidence.md
  mvp_mod_005_closeout_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-005-CLOSEOUT/security-quality-evidence.md
  mvp_mod_006_pkg_bcm_lab_002: 01-product-definition/business-capabilities/packages/bcm-lab-002-sample-collection/
  mvp_mod_006_pkg_bcm_lab_003: 01-product-definition/business-capabilities/packages/bcm-lab-003-sample-labeling/
  mvp_mod_006_pkg_bcm_lab_005: 01-product-definition/business-capabilities/packages/bcm-lab-005-sample-reception/
  mvp_mod_006_pkg_bcm_lab_006: 01-product-definition/business-capabilities/packages/bcm-lab-006-laboratory-processing/
  mvp_mod_006_pkg_bcm_lab_008: 01-product-definition/business-capabilities/packages/bcm-lab-008-technical-validation/
  mvp_mod_006_pkg_bcm_lab_009: 01-product-definition/business-capabilities/packages/bcm-lab-009-medical-validation/
  mvp_mod_006_pkg_bcm_lab_010: 01-product-definition/business-capabilities/packages/bcm-lab-010-result-release/
  mvp_mod_006_def_qa_evidence: 08-qa/qa/laboratory-workflow/MVP-MOD-006-DEF-validation.md
  mvp_mod_006_def_qa_evidence_yaml: 08-qa/qa/laboratory-workflow/MVP-MOD-006-DEF-validation.md
  mvp_mod_006_def_security_quality_evidence: 08-qa/security-quality/MVP-MOD-006-DEF/security-quality-evidence.md
  mvp_mod_006_def_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-006-DEF/security-quality-evidence.md
  mvp_mod_006_closeout_evidence_yaml: 08-qa/qa/laboratory-workflow/MVP-MOD-006-CLOSEOUT.md
  mvp_mod_007_pkg_bcm_res_001: 01-product-definition/business-capabilities/packages/bcm-res-001-result-management/
  mvp_mod_007_pkg_bcm_res_002: 01-product-definition/business-capabilities/packages/bcm-res-002-pdf-report-generation/
  mvp_mod_007_pkg_bcm_res_004: 01-product-definition/business-capabilities/packages/bcm-res-004-digital-delivery/
  mvp_mod_007_pkg_bcm_res_005: 01-product-definition/business-capabilities/packages/bcm-res-005-result-history/
  mvp_mod_007_pkg_bcm_res_006: 01-product-definition/business-capabilities/packages/bcm-res-006-critical-results/
  mvp_mod_007_pkg_bcm_res_007: 01-product-definition/business-capabilities/packages/bcm-res-007-result-notifications/
  mvp_mod_007_pkg_bcm_plt_003: 01-product-definition/business-capabilities/packages/bcm-plt-003-notification-management/
  mvp_mod_007_pkg_bcm_plt_008: 01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/
  mvp_mod_007_def_qa_evidence: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-DEF-validation.md
  mvp_mod_007_def_qa_evidence_yaml: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-DEF-validation.md
  mvp_mod_007_def_security_quality_evidence: 08-qa/security-quality/MVP-MOD-007-DEF/security-quality-evidence.md
  mvp_mod_007_def_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-007-DEF/security-quality-evidence.md
  mvp_mod_007_be_001_qa_evidence: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-BE-001-validation.md
  mvp_mod_007_be_001_qa_evidence_yaml: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-BE-001-validation.md
  mvp_mod_007_be_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-007-BE-001/security-quality-evidence.md
  mvp_mod_007_be_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-007-BE-001/security-quality-evidence.md
  mvp_mod_007_be_002_qa_evidence: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-BE-002-validation.md
  mvp_mod_007_be_002_qa_evidence_yaml: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-BE-002-validation.md
  mvp_mod_007_be_002_security_quality_evidence: 08-qa/security-quality/MVP-MOD-007-BE-002/security-quality-evidence.md
  mvp_mod_007_be_002_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-007-BE-002/security-quality-evidence.md
  mvp_mod_007_fe_001_qa_evidence: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-FE-001-validation.md
  mvp_mod_007_fe_001_qa_evidence_yaml: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-FE-001-validation.md
  mvp_mod_007_fe_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-007-FE-001/security-quality-evidence.md
  mvp_mod_007_fe_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-007-FE-001/security-quality-evidence.md
  mvp_mod_007_app_001_qa_evidence: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-APP-001-validation.md
  mvp_mod_007_app_001_qa_evidence_yaml: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-APP-001-validation.md
  mvp_mod_007_app_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-007-APP-001/MVP-MOD-007-APP-001-quality-report.md
  mvp_mod_007_qa_001_qa_evidence: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-QA-001-validation.md
  mvp_mod_007_qa_001_qa_evidence_yaml: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-QA-001-validation.md
  mvp_mod_007_qa_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-007-QA-001/security-quality-evidence.md
  mvp_mod_007_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-007-QA-001/security-quality-evidence.md
  mvp_mod_007_closeout_evidence: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-CLOSEOUT-validation.md
  mvp_mod_007_closeout_evidence_yaml: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-CLOSEOUT-validation.md
  mvp_mod_007_closeout_security_quality_evidence: 08-qa/security-quality/MVP-MOD-007-CLOSEOUT/security-quality-evidence.md
  mvp_mod_007_closeout_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-007-CLOSEOUT/security-quality-evidence.md
  technical_debt_td_fe_008: 08-qa/technical-debt/TD-FE-008-patient-portal-coverage-baseline.md
  technical_debt_td_fe_009: 08-qa/technical-debt/TD-FE-009-doctor-portal-coverage-baseline.md
  mvp_mod_008_pkg_bcm_plt_004: 01-product-definition/business-capabilities/packages/bcm-plt-004-integration-management/
  mvp_mod_008_pkg_bcm_plt_005: 01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/
  mvp_mod_008_pkg_bcm_plt_010: 01-product-definition/business-capabilities/packages/bcm-plt-010-open-data-ingestion-and-migration/
  mvp_mod_008_def_qa_evidence: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-DEF-validation.md
  mvp_mod_008_def_qa_evidence_yaml: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-DEF-validation.md
  mvp_mod_008_def_security_quality_evidence: 08-qa/security-quality/MVP-MOD-008-DEF/security-quality-evidence.md
  mvp_mod_008_def_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-008-DEF/security-quality-evidence.md
  mvp_mod_008_be_001_backend_implementation_integration_management: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/integrationinteroperability/integrationmanagement/
  mvp_mod_008_be_001_backend_implementation_api_management: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/integrationinteroperability/apimanagement/
  mvp_mod_008_be_001_backend_implementation_migration_management: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/datamigrationportability/migrationmanagement/
  mvp_mod_008_be_001_schema_integration: 07-implementation/backend/src/main/resources/db/integration-interoperability/schema.sql
  mvp_mod_008_be_001_schema_migration: 07-implementation/backend/src/main/resources/db/data-migration-portability/schema.sql
  mvp_mod_008_be_001_qa_evidence: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-BE-001-validation.md
  mvp_mod_008_be_001_qa_evidence_yaml: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-BE-001-validation.md
  mvp_mod_008_be_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-008-BE-001/security-quality-evidence.md
  mvp_mod_008_be_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-008-BE-001/security-quality-evidence.md
  mvp_mod_008_be_002_qa_evidence: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-BE-002-validation.md
  mvp_mod_008_be_002_qa_evidence_yaml: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-BE-002-validation.md
  mvp_mod_008_be_002_security_quality_evidence: 08-qa/security-quality/MVP-MOD-008-BE-002/security-quality-evidence.md
  mvp_mod_008_be_002_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-008-BE-002/security-quality-evidence.md
  mvp_mod_008_fe_001_qa_evidence: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-FE-001-validation.md
  mvp_mod_008_fe_001_qa_evidence_yaml: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-FE-001-validation.md
  mvp_mod_008_fe_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-008-FE-001/security-quality-evidence.md
  mvp_mod_008_fe_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-008-FE-001/security-quality-evidence.md
  mvp_mod_008_qa_001_qa_evidence: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-QA-001-validation.md
  mvp_mod_008_qa_001_qa_evidence_yaml: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-QA-001-validation.md
  mvp_mod_008_qa_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-008-QA-001/security-quality-evidence.md
  mvp_mod_008_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-008-QA-001/security-quality-evidence.md
  mvp_mod_008_closeout_evidence: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-CLOSEOUT-validation.md
  mvp_mod_008_closeout_evidence_yaml: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-CLOSEOUT-validation.md
  mvp_mod_008_closeout_security_quality_evidence: 08-qa/security-quality/MVP-MOD-008-CLOSEOUT/security-quality-evidence.md
  mvp_mod_008_closeout_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-008-CLOSEOUT/security-quality-evidence.md
  technical_debt_td_be_013: 08-qa/technical-debt/TD-BE-013-xlsx-migration-row-parsing-missing.md
  technical_debt_td_be_014: 08-qa/technical-debt/TD-BE-014-migration-domain-command-cross-module-wiring-deferred.md
  technical_debt_td_be_015: 08-qa/technical-debt/TD-BE-015-rate-limit-enforcement-scoped-to-partner-keys.md
  com_mod_009_pkg_bcm_plt_001: 01-product-definition/business-capabilities/packages/bcm-plt-001-identity-and-access-management/
  com_mod_009_def_qa_evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-DEF-validation.md
  com_mod_009_def_qa_evidence_yaml: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-DEF-validation.md
  com_mod_009_def_security_quality_evidence: 08-qa/security-quality/COM-MOD-009-DEF/security-quality-evidence.md
  com_mod_009_def_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-009-DEF/security-quality-evidence.md
  com_mod_009_be_001_qa_evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-BE-001-validation.md
  com_mod_009_be_001_qa_evidence_yaml: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-BE-001-validation.md
  com_mod_009_be_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-009-BE-001/security-quality-evidence.md
  com_mod_009_be_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-009-BE-001/security-quality-evidence.md
  com_mod_009_portal_001_qa_evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-PORTAL-001-validation.md
  com_mod_009_portal_001_qa_evidence_yaml: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-PORTAL-001-validation.md
  com_mod_009_portal_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-009-PORTAL-001/security-quality-evidence.md
  com_mod_009_portal_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-009-PORTAL-001/security-quality-evidence.md
  com_mod_009_portal_002_qa_evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-PORTAL-002-validation.md
  com_mod_009_portal_002_qa_evidence_yaml: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-PORTAL-002-validation.md
  com_mod_009_portal_002_security_quality_evidence: 08-qa/security-quality/COM-MOD-009-PORTAL-002/security-quality-evidence.md
  com_mod_009_portal_002_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-009-PORTAL-002/security-quality-evidence.md
  com_mod_009_app_001_qa_evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-APP-001-validation.md
  com_mod_009_app_001_qa_evidence_yaml: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-APP-001-validation.md
  com_mod_009_app_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-009-APP-001/security-quality-evidence.md
  com_mod_009_app_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-009-APP-001/security-quality-evidence.md
  com_mod_009_qa_001_qa_evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-QA-001-validation.md
  com_mod_009_qa_001_qa_evidence_yaml: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-QA-001-validation.md
  com_mod_009_qa_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-009-QA-001/security-quality-evidence.md
  com_mod_009_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-009-QA-001/security-quality-evidence.md
  com_mod_009_closeout_evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-CLOSEOUT.md
  com_mod_009_closeout_evidence_yaml: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-CLOSEOUT.md
  com_mod_009_closeout_security_quality_evidence: 08-qa/security-quality/COM-MOD-009-CLOSEOUT/security-quality-evidence.md
  com_mod_009_closeout_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-009-CLOSEOUT/security-quality-evidence.md
  mvp_mod_003_def_qa_evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-DEF-validation.md
  mvp_mod_003_def_qa_evidence_yaml: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-DEF-validation.md
  mvp_mod_003_be_001_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/peopleclinicalmasterdata/
  mvp_mod_003_be_001_schema: 07-implementation/backend/src/main/resources/db/people-and-clinical-master-data/schema.sql
  mvp_mod_003_be_001_qa_evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-BE-001-validation.md
  mvp_mod_003_be_001_qa_evidence_yaml: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-BE-001-validation.md
  mvp_mod_003_be_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-003-BE-001/security-quality-evidence.md
  mvp_mod_003_be_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-003-BE-001/security-quality-evidence.md
  mvp_mod_003_be_002_qa_evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-BE-002-validation.md
  mvp_mod_003_be_002_qa_evidence_yaml: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-BE-002-validation.md
  mvp_mod_003_be_002_security_quality_evidence: 08-qa/security-quality/MVP-MOD-003-BE-002/security-quality-evidence.md
  mvp_mod_003_be_002_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-003-BE-002/security-quality-evidence.md
  mvp_mod_003_fe_001_employee_portal_implementation: 07-implementation/employee-portal/src/components/screens/
  mvp_mod_003_fe_001_people_api: 07-implementation/employee-portal/src/api/peopleApi.ts
  mvp_mod_003_fe_001_people_types: 07-implementation/employee-portal/src/api/types.ts
  mvp_mod_003_fe_001_qa_evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-FE-001-validation.md
  mvp_mod_003_fe_001_qa_evidence_yaml: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-FE-001-validation.md
  mvp_mod_003_fe_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-003-FE-001/security-quality-evidence.md
  mvp_mod_003_fe_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-003-FE-001/security-quality-evidence.md
  mvp_mod_003_qa_001_qa_evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-QA-001-validation.md
  mvp_mod_003_qa_001_qa_evidence_yaml: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-QA-001-validation.md
  mvp_mod_003_qa_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-003-QA-001/security-quality-evidence.md
  mvp_mod_003_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-003-QA-001/security-quality-evidence.md
  mvp_mod_003_closeout_evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-CLOSEOUT.md
  mvp_mod_003_closeout_evidence_yaml: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-CLOSEOUT.md
  mvp_mod_003_closeout_security_quality_evidence: 08-qa/security-quality/MVP-MOD-003-CLOSEOUT/security-quality-evidence.md
  mvp_mod_003_closeout_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-003-CLOSEOUT/security-quality-evidence.md
  mvp_mod_002_def_qa_evidence: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-DEF-validation.md
  mvp_mod_002_def_qa_evidence_yaml: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-DEF-validation.md
  mvp_mod_002_be_001_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/catalogtestconfiguration/
  mvp_mod_002_be_001_schema: 07-implementation/backend/src/main/resources/db/catalog-test-configuration/schema.sql
  mvp_mod_002_be_001_qa_evidence: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-BE-001-validation.md
  mvp_mod_002_be_001_qa_evidence_yaml: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-BE-001-validation.md
  mvp_mod_002_be_002_backend_implementation: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/catalogtestconfiguration/
  mvp_mod_002_be_002_qa_evidence: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-BE-002-validation.md
  mvp_mod_002_be_002_qa_evidence_yaml: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-BE-002-validation.md
  mvp_mod_002_fe_001_employee_portal_implementation: 07-implementation/employee-portal/src/components/screens/DiagnosticCatalogScreen.tsx
  mvp_mod_002_fe_001_catalog_api: 07-implementation/employee-portal/src/api/catalogApi.ts
  mvp_mod_002_fe_001_catalog_types: 07-implementation/employee-portal/src/api/types.ts
  mvp_mod_002_fe_001_qa_evidence: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-FE-001-validation.md
  mvp_mod_002_fe_001_qa_evidence_yaml: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-FE-001-validation.md
  mvp_mod_002_fe_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-002-FE-001/security-quality-evidence.md
  mvp_mod_002_fe_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-002-FE-001/security-quality-evidence.md
  mvp_mod_002_qa_001_qa_evidence: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-QA-001-validation.md
  mvp_mod_002_qa_001_qa_evidence_yaml: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-QA-001-validation.md
  mvp_mod_002_qa_001_security_quality_evidence: 08-qa/security-quality/MVP-MOD-002-QA-001/security-quality-evidence.md
  mvp_mod_002_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-002-QA-001/security-quality-evidence.md
  mvp_mod_002_closeout_evidence: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-CLOSEOUT.md
  mvp_mod_002_closeout_evidence_yaml: 08-qa/qa/catalog-test-configuration/MVP-MOD-002-CLOSEOUT.md
  mvp_mod_002_closeout_security_quality_evidence: 08-qa/security-quality/MVP-MOD-002-CLOSEOUT/security-quality-evidence.md
  mvp_mod_002_closeout_security_quality_evidence_yaml: 08-qa/security-quality/MVP-MOD-002-CLOSEOUT/security-quality-evidence.md
  mvp_mod_002_backend_pom: 07-implementation/backend/pom.xml
  security_quality_evidence_root: 08-qa/security-quality/
  security_quality_evidence_index: 08-qa/security-quality/security-quality-index.md
  security_quality_evidence_readme: 08-qa/security-quality/README.md
  technical_debt_index: 08-qa/technical-debt/technical-debt-index.md
  technical_debt_readme: 08-qa/technical-debt/README.md
  technical_debt_td_qa_001: 08-qa/technical-debt/TD-QA-001-dast-automation.md
  technical_debt_td_qa_002: 08-qa/technical-debt/TD-QA-002-trivy-tool-upgrade.md
  technical_debt_td_be_001: 08-qa/technical-debt/TD-BE-001-mockito-java-agent.md
  technical_debt_td_be_002: 08-qa/technical-debt/TD-BE-002-backend-static-analysis-toolchain.md
  technical_debt_td_be_003: 08-qa/technical-debt/TD-BE-003-backend-coverage-gate.md
  technical_debt_td_be_004: 08-qa/technical-debt/TD-BE-004-release-supply-chain-gates.md
  technical_debt_td_stack_001: 08-qa/technical-debt/TD-STACK-001-stack-modernization-roadmap.md
  technical_debt_td_be_005: 08-qa/technical-debt/TD-BE-005-doctor-activation-gating-via-eligibility-query.md
  technical_debt_td_be_006: 08-qa/technical-debt/TD-BE-006-patient-registration-commit-non-atomic.md
  technical_debt_td_def_001: 08-qa/technical-debt/TD-DEF-001-quotation-to-sale-conversion-deferred.md
  technical_debt_td_def_002: 08-qa/technical-debt/TD-DEF-002-appointment-capacity-planning-deferred.md
  technical_debt_td_be_009: 08-qa/technical-debt/TD-BE-009-branch-snapshot-version-placeholder.md
  technical_debt_td_be_010: 08-qa/technical-debt/TD-BE-010-order-cancellation-sample-state-check-deferred.md
  technical_debt_td_be_011: 08-qa/technical-debt/TD-BE-011-cashsales-frontdesk-public-port-boundary.md
  technical_debt_td_fe_003: 08-qa/technical-debt/TD-FE-003-frontend-enterprise-quality-profile.md
  technical_debt_td_fe_004: 08-qa/technical-debt/TD-FE-004-frontend-coverage-80-target.md
  technical_debt_td_fe_006: 08-qa/technical-debt/TD-FE-006-appointment-admission-quotation-ui-missing.md
  technical_debt_td_app_001: 08-qa/technical-debt/TD-APP-001-mobile-quality-baseline.md
  technical_debt_td_app_002: 08-qa/technical-debt/TD-APP-002-mobile-coverage-80-target.md
  technical_debt_td_qa_003: 08-qa/technical-debt/TD-QA-003-all-severity-vulnerability-evidence.md
  technical_debt_td_i18n_001: 08-qa/technical-debt/TD-I18N-001-message-externalization-baseline.md
  framework_feedback_index: 08-qa/framework-feedback/framework-feedback-index.md
  framework_feedback_readme: 08-qa/framework-feedback/README.md
  framework_feedback_fwf_hop_001: 08-qa/framework-feedback/FWF-HOP-001-framework-feedback-loop.md
  framework_feedback_fwf_hop_002: 08-qa/framework-feedback/FWF-HOP-002-tenant-configurable-business-parameters.md
  framework_feedback_fwf_hop_003: 08-qa/framework-feedback/FWF-HOP-003-spring-modulith-cross-module-api-visibility.md
  client_stack_market_validation: 03-architecture/technology-architecture/client-stack-market-validation.md
  stack_quality_toolchain_baseline: 03-architecture/technology-architecture/stack-quality-toolchain-baseline.md
  local_toolchain_inventory: 03-architecture/technology-architecture/local-toolchain-inventory.md
  local_toolchain_inventory_md: 03-architecture/technology-architecture/local-toolchain-inventory.md
  open_data_ingestion_contract: 05-contracts/import-export/open-data-ingestion/open-data-ingestion-contract.md
  open_data_ingestion_contract_yaml: 05-contracts/import-export/open-data-ingestion/open-data-ingestion-contract.md
  open_data_ingestion_requirements: 04-requirements/capabilities/bcm-plt-010-open-data-ingestion-and-migration/requirements.md
  product_marketplace_contract: 05-contracts/marketplace/product-marketplace/product-marketplace-contract.md
  product_marketplace_contract_yaml: 05-contracts/marketplace/product-marketplace/product-marketplace-contract.md
  product_marketplace_requirements: 04-requirements/capabilities/bcm-plt-011-product-marketplace-and-entitlements/requirements.md
  com_mod_017_pkg_bcm_plt_011: 01-product-definition/business-capabilities/packages/bcm-plt-011-product-marketplace-and-entitlements/
  com_mod_017_def_qa_evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-DEF-validation.md
  com_mod_017_def_qa_evidence_yaml: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-DEF-validation.md
  com_mod_017_def_security_quality_evidence: 08-qa/security-quality/COM-MOD-017-DEF/security-quality-evidence.md
  com_mod_017_def_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-017-DEF/security-quality-evidence.md
  com_mod_017_def_handoff_summary: 08-qa/handoffs/COM-MOD-017-DEF-summary.md
  com_mod_017_be_001_qa_evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-001-validation.md
  com_mod_017_be_001_qa_evidence_yaml: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-001-validation.md
  com_mod_017_be_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-017-BE-001/security-quality-evidence.md
  com_mod_017_be_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-017-BE-001/security-quality-evidence.md
  com_mod_017_be_001_handoff_summary: 08-qa/handoffs/COM-MOD-017-BE-001-summary.md
  td_be_018: 08-qa/technical-debt/TD-BE-018-marketplace-entitlement-policy-and-billing-adapter-basic-only.md
  com_mod_017_be_002_qa_evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-002-validation.md
  com_mod_017_be_002_security_quality_evidence: 08-qa/security-quality/COM-MOD-017-BE-002/security-quality-evidence.md
  com_mod_017_be_002_handoff_summary: 08-qa/handoffs/COM-MOD-017-BE-002-summary.md
  td_be_019: 08-qa/technical-debt/TD-BE-019-marketplace-runtime-feature-availability-not-wired-into-iam-or-menu.md
  td_be_020: 08-qa/technical-debt/TD-BE-020-local-profile-datasource-autoconfiguration-excluded-by-format-migration.md
  com_mod_017_fe_001_qa_evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-FE-001-validation.md
  com_mod_017_fe_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-017-FE-001/security-quality-evidence.md
  com_mod_017_fe_001_handoff_summary: 08-qa/handoffs/COM-MOD-017-FE-001-summary.md
  td_fe_012: 08-qa/technical-debt/TD-FE-012-employee-portal-npm-audit-devdependency-high-severity-findings.md
  com_mod_017_qa_001_qa_evidence: 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-QA-001-validation.md
  com_mod_017_qa_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-017-QA-001/security-quality-evidence.md
  com_mod_017_qa_001_handoff_summary: 08-qa/handoffs/COM-MOD-017-QA-001-summary.md
  nxf_ctx_001_handoff_summary: 08-qa/handoffs/NXF-CTX-001-summary.md
  td_fmt_001: 08-qa/technical-debt/TD-FMT-001-yaml-monolith-to-frontmatter-transition.md
  actor_catalog: 02-domain-definition/actors/acm-001/actor-catalog.md
  healthcare_reference_processes: 02-domain-definition/processes/hrp-001/healthcare-reference-processes.md
  business_rules_catalog: 02-domain-definition/business-rules/brm-001/business-rules-catalog.md
  hop_mvp_framework: 06-delivery/mvp/healthcare-operations-platform-mvp-framework.md
  mvp_backlog_execution_prompts: 06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.md
  mvp_backlog_execution_prompts_yaml: 06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.md
  mvp_development_readiness_decision: 06-delivery/mvp/MVP_DEVELOPMENT_READINESS_DECISION.md
  mvp_development_readiness_decision_yaml: 06-delivery/mvp/MVP_DEVELOPMENT_READINESS_DECISION.md
  commercial_product_backlog: 06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  commercial_product_backlog_yaml: 06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  quality_alignment_backlog: 06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.md
  quality_alignment_backlog_yaml: 06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.md
  enterprise_foundation_alignment_backlog: 06-delivery/commercial-product/HOP_ENTERPRISE_FOUNDATION_ALIGNMENT_BACKLOG.md
  enterprise_foundation_alignment_backlog_yaml: 06-delivery/commercial-product/HOP_ENTERPRISE_FOUNDATION_ALIGNMENT_BACKLOG.md
  enterprise_foundation_alignment_master: 03-architecture/enterprise-foundation/enterprise-foundation-alignment.md
  enterprise_foundation_alignment_master_yaml: 03-architecture/enterprise-foundation/enterprise-foundation-alignment.md
  localization_strategy: 03-architecture/i18n-localization/localization-strategy.md
  localization_strategy_yaml: 03-architecture/i18n-localization/localization-strategy.md
  iam_permission_model: 03-architecture/security-compliance/iam-permission-model.md
  iam_permission_model_yaml: 03-architecture/security-compliance/iam-permission-model.md
  session_management_baseline: 03-architecture/security-compliance/session-management-baseline.md
  session_management_baseline_yaml: 03-architecture/security-compliance/session-management-baseline.md
  database_architecture: 03-architecture/data-architecture/database-architecture.md
  database_architecture_yaml: 03-architecture/data-architecture/database-architecture.md
  data_dictionary: 03-architecture/data-architecture/data-dictionary.md
  data_dictionary_yaml: 03-architecture/data-architecture/data-dictionary.md
  normalization_report: 03-architecture/data-architecture/normalization-report.md
  normalization_report_yaml: 03-architecture/data-architecture/normalization-report.md
  seed_data_catalog: 03-architecture/data-architecture/seed-data-catalog.md
  seed_data_catalog_yaml: 03-architecture/data-architecture/seed-data-catalog.md
  ux_ui_foundation: 03-architecture/ux-ui/ux-ui-foundation.md
  ux_ui_foundation_yaml: 03-architecture/ux-ui/ux-ui-foundation.md
  persistence_and_contract_generation_review: 03-architecture/technology-architecture/persistence-and-contract-generation-review.md
  persistence_and_contract_generation_review_yaml: 03-architecture/technology-architecture/persistence-and-contract-generation-review.md
  hop_ent_found_001_qa_evidence: 08-qa/qa/enterprise-foundation/HOP-ENT-FOUND-001-validation.md
  hop_ent_found_001_qa_evidence_yaml: 08-qa/qa/enterprise-foundation/HOP-ENT-FOUND-001-validation.md
  hop_ent_found_001_security_quality_evidence: 08-qa/security-quality/HOP-ENT-FOUND-001/security-quality-evidence.md
  hop_ent_found_001_security_quality_evidence_yaml: 08-qa/security-quality/HOP-ENT-FOUND-001/security-quality-evidence.md
  quality_alignment_gap_analysis: 08-qa/qa/quality-alignment/HOP-QUALITY-ALIGNMENT-GAP-ANALYSIS.md
  quality_alignment_gap_analysis_yaml: 08-qa/qa/quality-alignment/HOP-QUALITY-ALIGNMENT-GAP-ANALYSIS.md
  engineering_excellence_prioritization: 08-qa/qa/quality-alignment/HOP-ENGINEERING-EXCELLENCE-PRIORITIZATION.md
  engineering_excellence_prioritization_yaml: 08-qa/qa/quality-alignment/HOP-ENGINEERING-EXCELLENCE-PRIORITIZATION.md
  quality_alignment_002_qa_evidence: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-002-validation.md
  quality_alignment_002_qa_evidence_yaml: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-002-validation.md
  quality_alignment_002_security_quality_evidence: 08-qa/security-quality/HOP-QA-ALIGN-002/security-quality-evidence.md
  quality_alignment_002_security_quality_evidence_yaml: 08-qa/security-quality/HOP-QA-ALIGN-002/security-quality-evidence.md
  quality_alignment_003_qa_evidence: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-003-validation.md
  quality_alignment_003_qa_evidence_yaml: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-003-validation.md
  quality_alignment_003_security_quality_evidence: 08-qa/security-quality/HOP-QA-ALIGN-003/security-quality-evidence.md
  quality_alignment_003_security_quality_evidence_yaml: 08-qa/security-quality/HOP-QA-ALIGN-003/security-quality-evidence.md
  quality_alignment_004_qa_evidence: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-004-validation.md
  quality_alignment_004_qa_evidence_yaml: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-004-validation.md
  quality_alignment_004_security_quality_evidence: 08-qa/security-quality/HOP-QA-ALIGN-004/security-quality-evidence.md
  quality_alignment_004_security_quality_evidence_yaml: 08-qa/security-quality/HOP-QA-ALIGN-004/security-quality-evidence.md
  quality_alignment_005_qa_evidence: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-005-validation.md
  quality_alignment_005_qa_evidence_yaml: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-005-validation.md
  quality_alignment_005_security_quality_evidence: 08-qa/security-quality/HOP-QA-ALIGN-005/security-quality-evidence.md
  quality_alignment_005_security_quality_evidence_yaml: 08-qa/security-quality/HOP-QA-ALIGN-005/security-quality-evidence.md
  quality_alignment_005_message_externalization_inventory: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-005-message-externalization-inventory.md
  quality_alignment_005_message_externalization_inventory_yaml: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-005-message-externalization-inventory.md
  quality_alignment_006_qa_evidence: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-006-validation.md
  quality_alignment_006_qa_evidence_yaml: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-006-validation.md
  quality_alignment_closeout_qa_evidence: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-CLOSEOUT.md
  quality_alignment_closeout_qa_evidence_yaml: 08-qa/qa/quality-alignment/HOP-QA-ALIGN-CLOSEOUT.md
  quality_alignment_closeout_security_quality_evidence: 08-qa/security-quality/HOP-QA-ALIGN-CLOSEOUT/security-quality-evidence.md
  quality_alignment_closeout_security_quality_evidence_yaml: 08-qa/security-quality/HOP-QA-ALIGN-CLOSEOUT/security-quality-evidence.md
  commercial_backlog_execution_prompts: 06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
  commercial_backlog_execution_prompts_yaml: 06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
  commercial_module_folder_index_yaml: 06-delivery/commercial-product/modules/module-folder-index.md
  generic_project_lifecycle_prompts_yaml: ../../nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.md
  auxiliary_development_prompts_yaml: ../../nexora-framework/05-prompts/prompts/auxiliary-development-prompts.md
  mvp_mod_001_platform_foundation: 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/module-definition.md
  mvp_mod_001_api_contract: 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/api-contract.openapi.md
  mvp_mod_001_domain_model: 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/domain-model.md
  mvp_mod_001_database_migration_plan: 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/database-migration-plan.md
  mvp_mod_001_ui_screen_map: 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/ui-screen-map.md
  mvp_mod_001_security_and_audit_rules: 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/security-and-audit-rules.md
  mvp_mod_001_test_plan: 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/test-plan.md
  mvp_mod_001_traceability: 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/traceability.md
  mvp_mod_001_backend_implementation: 07-implementation/backend/
  mvp_mod_001_frontend_implementation: 07-implementation/employee-portal/
  mvp_mod_001_mobile_implementation: 07-implementation/mobile-app/
  mvp_mod_001_local_runtime_compose: 07-implementation/compose.local.json
  mvp_mod_001_local_runtime_env_example: 07-implementation/.env.example
  local_solution_runbook: 09-operations/runbooks/local-solution-runbook.md
  local_solution_runbook_yaml: 09-operations/runbooks/local-solution-runbook.md
  mvp_mod_001_pf_be_001_qa_evidence: 08-qa/qa/platform-foundation/PF-BE-001-backend-skeleton.md
  mvp_mod_001_pf_be_001_qa_evidence_yaml: 08-qa/qa/platform-foundation/PF-BE-001-backend-skeleton.md
  mvp_mod_001_pf_ops_001_qa_evidence: 08-qa/qa/platform-foundation/PF-OPS-001-local-runtime.md
  mvp_mod_001_pf_ops_001_qa_evidence_yaml: 08-qa/qa/platform-foundation/PF-OPS-001-local-runtime.md
  mvp_mod_001_pf_be_002_qa_evidence: 08-qa/qa/platform-foundation/PF-BE-002-organization-commands.md
  mvp_mod_001_pf_be_002_qa_evidence_yaml: 08-qa/qa/platform-foundation/PF-BE-002-organization-commands.md
  mvp_mod_001_pf_be_003_qa_evidence: 08-qa/qa/platform-foundation/PF-BE-003-identity-access.md
  mvp_mod_001_pf_be_003_qa_evidence_yaml: 08-qa/qa/platform-foundation/PF-BE-003-identity-access.md
  mvp_mod_001_pf_be_004_qa_evidence: 08-qa/qa/platform-foundation/PF-BE-004-audit-event-recording.md
  mvp_mod_001_pf_fe_001_qa_evidence: 08-qa/qa/platform-foundation/PF-FE-001-employee-portal-administration.md
  mvp_mod_001_pf_fe_001_qa_evidence_yaml: 08-qa/qa/platform-foundation/PF-FE-001-employee-portal-administration.md
  mvp_mod_001_pf_app_001_qa_evidence: 08-qa/qa/platform-foundation/PF-APP-001-mobile-app-foundation.md
  mvp_mod_001_pf_app_001_qa_evidence_yaml: 08-qa/qa/platform-foundation/PF-APP-001-mobile-app-foundation.md
  mvp_mod_001_pf_qa_001_qa_evidence: 08-qa/qa/platform-foundation/PF-QA-001-smoke-and-contract-tests.md
  mvp_mod_001_pf_qa_001_qa_evidence_yaml: 08-qa/qa/platform-foundation/PF-QA-001-smoke-and-contract-tests.md
  mvp_mod_001_closeout_evidence: 08-qa/qa/platform-foundation/MVP-MOD-001-closeout.md
  mvp_mod_001_closeout_evidence_yaml: 08-qa/qa/platform-foundation/MVP-MOD-001-closeout.md
  mvp_mod_001_pf_be_004_qa_evidence_yaml: 08-qa/qa/platform-foundation/PF-BE-004-audit-event-recording.md
  context_map: 02-domain-definition/domain-foundation/context-map/context-map.md
  shared_kernel: 02-domain-definition/domain-foundation/shared-kernel/shared-kernel.md
  aggregate_catalog: 02-domain-definition/domain-foundation/aggregates/aggregate-catalog.md
  com_mod_016_onboarding_master_index: 09-operations/onboarding/README.md
  com_mod_016_onboarding_master_index_yaml: 09-operations/onboarding/onboarding-index.md
  com_mod_016_customer_onboarding_guide: 09-operations/onboarding/customer-onboarding-guide.md
  com_mod_016_customer_onboarding_guide_yaml: 09-operations/onboarding/customer-onboarding-guide.md
  com_mod_016_initial_org_lab_config_guide: 09-operations/onboarding/initial-organization-and-laboratory-config-guide.md
  com_mod_016_initial_org_lab_config_guide_yaml: 09-operations/onboarding/initial-organization-and-laboratory-config-guide.md
  com_mod_016_roles_permissions_guide: 09-operations/onboarding/roles-permissions-navigation-and-session-guide.md
  com_mod_016_roles_permissions_guide_yaml: 09-operations/onboarding/roles-permissions-navigation-and-session-guide.md
  com_mod_016_regional_localization_guide: 09-operations/onboarding/regional-localization-and-currency-config-guide.md
  com_mod_016_regional_localization_guide_yaml: 09-operations/onboarding/regional-localization-and-currency-config-guide.md
  com_mod_016_technical_prerequisites_checklist: 09-operations/onboarding/technical-prerequisites-checklist.md
  com_mod_016_technical_prerequisites_checklist_yaml: 09-operations/onboarding/technical-prerequisites-checklist.md
  com_mod_016_data_migration_checklist: 09-operations/onboarding/data-migration-and-initial-ingestion-checklist.md
  com_mod_016_data_migration_checklist_yaml: 09-operations/onboarding/data-migration-and-initial-ingestion-checklist.md
  com_mod_016_training_acceptance_guide: 09-operations/onboarding/initial-training-human-validation-and-acceptance-guide.md
  com_mod_016_training_acceptance_guide_yaml: 09-operations/onboarding/initial-training-human-validation-and-acceptance-guide.md
  com_mod_016_support_escalation_guide: 09-operations/onboarding/support-escalation-and-initial-operations-guide.md
  com_mod_016_support_escalation_guide_yaml: 09-operations/onboarding/support-escalation-and-initial-operations-guide.md
  com_mod_016_doc_001_qa_evidence: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-DOC-001-validation.md
  com_mod_016_doc_001_qa_evidence_yaml: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-DOC-001-validation.md
  com_mod_016_doc_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-016-DOC-001/security-quality-evidence.md
  com_mod_016_doc_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-016-DOC-001/security-quality-evidence.md
  com_mod_016_governance_master_index: 09-operations/governance/README.md
  com_mod_016_governance_master_index_yaml: 09-operations/governance/governance-index.md
  com_mod_016_ops_001_qa_evidence: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-OPS-001-validation.md
  com_mod_016_ops_001_qa_evidence_yaml: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-OPS-001-validation.md
  com_mod_016_ops_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-016-OPS-001/security-quality-evidence.md
  com_mod_016_ops_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-016-OPS-001/security-quality-evidence.md
  com_mod_016_commercial_packages: 06-delivery/commercial-product/commercial-packages/hop-commercial-packages.md
  com_mod_016_pricing_model: 06-delivery/commercial-product/commercial-packages/pricing-model.md
  com_mod_016_capability_matrix_by_package: 06-delivery/commercial-product/commercial-packages/capability-matrix-by-package.md
  com_mod_016_upgrade_downgrade_criteria: 06-delivery/commercial-product/commercial-packages/upgrade-downgrade-criteria.md
  com_mod_016_sales_demo_script: 06-delivery/commercial-product/sales-enablement/sales-demo-script.md
  com_mod_016_demo_data_checklist: 06-delivery/commercial-product/sales-enablement/demo-data-checklist.md
  com_mod_016_buyer_personas_and_use_cases: 06-delivery/commercial-product/sales-enablement/buyer-personas-and-use-cases.md
  com_mod_016_customer_value_proposition: 06-delivery/commercial-product/sales-enablement/customer-value-proposition.md
  com_mod_016_sales_enablement_one_pager: 06-delivery/commercial-product/sales-enablement/sales-enablement-one-pager.md
  com_mod_016_launch_readiness_checklist: 06-delivery/commercial-product/launch-readiness/launch-readiness-checklist.md
  com_mod_016_customer_acceptance_and_commercial_handoff: 06-delivery/commercial-product/launch-readiness/customer-acceptance-and-commercial-handoff.md
  com_mod_016_com_001_qa_evidence: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-COM-001-validation.md
  com_mod_016_com_001_qa_evidence_yaml: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-COM-001-validation.md
  com_mod_016_com_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-016-COM-001/security-quality-evidence.md
  com_mod_016_com_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-016-COM-001/security-quality-evidence.md
  com_mod_016_qa_001_qa_evidence: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-QA-001-validation.md
  com_mod_016_qa_001_qa_evidence_yaml: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-QA-001-validation.md
  com_mod_016_qa_001_security_quality_evidence: 08-qa/security-quality/COM-MOD-016-QA-001/security-quality-evidence.md
  com_mod_016_qa_001_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-016-QA-001/security-quality-evidence.md
  com_mod_016_closeout_qa_evidence: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-CLOSEOUT-validation.md
  com_mod_016_closeout_qa_evidence_yaml: 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-CLOSEOUT-validation.md
  com_mod_016_closeout_security_quality_evidence: 08-qa/security-quality/COM-MOD-016-CLOSEOUT/security-quality-evidence.md
  com_mod_016_closeout_security_quality_evidence_yaml: 08-qa/security-quality/COM-MOD-016-CLOSEOUT/security-quality-evidence.md
rules:
- Healthcare Operations Platform is a project under Nexora.
- This folder is the boundary for HOP-specific artifacts.
- HOP project root must contain only numbered folders plus project control files.
- Nexora framework standards are inherited from ../../nexora-framework/.
- HOP source artifacts must remain agent agnostic and free of named-agent requirements.
- Agent-executable artifacts must have YAML machine-readable files and Markdown human-readable
  files when applicable.
- BUSINESS_REQUIREMENT.md remains requester-supplied source material; BUSINESS_REQUIREMENT.md
  is only a structured index derived from it.
- HOP BUSINESS_REQUIREMENT.md is curated as the business intake template for future
  Nexora projects.
- BUSINESS_REQUIREMENT.md must be derived from BUSINESS_REQUIREMENT.md using the
  documented transformation prompt.
- Agents must resolve the latest business requirement version from 00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
  before analysis or development.
- Business requirement changes require impact assessment before derived artifacts
  or implementation are modified.
- MVP-MOD-001 development is approved by the MVP development readiness decision; strategic
  enterprise roadmap items do not block the first module.
- HOP development after MVP-MOD-001 must follow 06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md.
- HOP follows Nexora Model Driven Product Engineering.
- Business Capability Packages are the primary development unit.
- Modules are roadmap groupings and must not replace capability packages as source
  of truth.
- Migration must support simple open ingestion formats that incumbent providers can
  reasonably export.
- Marketplace-ready capabilities must use package, offer, entitlement, installation,
  compatibility and lifecycle models.
- Optional commercial functionality must not bypass IAM, audit, privacy, clinical
  authority or financial controls.
- HOP must prefer open source, self-hostable and standards-based technologies unless
  an ADR approves an exception.
- HOP must validate requester-proposed and current stack choices against current stable
  or LTS open source market practice before release-oriented decisions.
- HOP must maintain stack-specific quality toolchain baselines and register non-blocking
  tooling gaps as technical debt.
- HOP must maintain a local toolchain inventory with executable paths, versions and
  generic commands for the current development machine; agents must load it before
  code-changing backlog work and update it or register technical debt when required
  tools are stale or missing.
- HOP code-changing backlog items must produce security quality evidence with applicable
  tests, best-practice and coding-standard checks, duplicate-code checks, complexity
  checks, SAST/static analysis, OWASP or equivalent secure-code checks, dependency
  vulnerability checks across all severities, secrets scan, coverage, message externalization/i18n
  review and DAST when runnable surfaces exist.
- If a mandatory validation category applies to a touched stack or runnable surface
  but HOP lacks the executable script, plugin, tool or configuration, the agent must
  create or update technical debt before closure; informal "if configured", "if scripts
  exist" and undocumented not-applicable dispositions are not valid closure evidence.
- HOP OWASP Dependency-Check uses a local advisory database refreshed manually once
  per day by the project operator or security reviewer. Agents must execute the scan
  against the database available at execution time and document its path and freshness;
  refreshing or downloading the NVD database is not a framework or backlog-agent responsibility
  unless explicitly assigned.
- HOP functional development was unblocked by HOP-QA-ALIGN-CLOSEOUT; MVP-MOD-005 and
  MVP-MOD-006 (through MVP-MOD-006-CLOSEOUT) are closed, MVP-MOD-007 Results and Digital
  Delivery is closed, and MVP-MOD-008 Integration and Migration Readiness is closed.
  MVP-MOD-008-DEF, MVP-MOD-008-BE-001, MVP-MOD-008-BE-002, MVP-MOD-008-FE-001, MVP-MOD-008-QA-001
  and MVP-MOD-008-CLOSEOUT are closed. BE-002 raised backend coverage to 80.49% and
  registered TD-BE-014/TD-BE-015. FE-001 closed with integration/API-governance/migration
  employee-portal screens, employee-portal coverage raised from 85.50% to 86.47%,
  npm audit and Trivy at 0 vulnerabilities, TD-STACK-003 and TD-I18N-002 further reduced,
  and TD-FE-010 registered for non-blocking generated admin-screen composition warnings.
  QA-001 confirmed backend quality at 265 tests and 80.49% coverage, employee-portal
  quality at 101 tests and 86.47% coverage, Trivy/npm/Dependency-Check at 0 vulnerabilities
  and no coverage regression. COM-MOD-009-BE-001, COM-MOD-009-PORTAL-001, COM-MOD-009-PORTAL-002,
  COM-MOD-009-APP-001 and COM-MOD-009-QA-001 are closed; COM-MOD-009-APP-001 raised
  mobile coverage to 99.21% and COM-MOD-009-QA-001 validated channel access/privacy
  evidence with npm quality, npm audit, and Trivy passing at 0 vulnerabilities. COM-MOD-009-CLOSEOUT
  is closed. COM-MOD-010-DEF modeled 13 Inventory and Internal Quality capability
  packages with 14 artifacts each and preserved runtime/coverage baselines because
  it was definition-only. COM-MOD-010-BE-001 compiled the BCM-INV-001..009 backend
  outputs as a single inventoryquality Spring Modulith module with 27 REST operations
  across nine hexagonal sub-packages, JDBC + in-memory dual adapters, first-class
  code+messageKey error envelope, seven new SCREEN_INVENTORY_* PermissionCode values
  and 38 new inventory.error.<code> catalog keys; backend line coverage 80.60% ->
  82.94% with 308 tests. COM-MOD-010-BE-002 compiled BCM-QLT-001/003/004/005 backend
  outputs with equipment, calibration, maintenance and internal quality-control APIs,
  JDBC + in-memory adapters, four schema tables, four quality screen permissions and
  16 localized inventory.error.<code> keys; 312 backend tests passed, backend coverage
  stayed at 82.94%, OWASP Dependency-Check scanned 65 dependencies with 0 vulnerabilities
  using the local manual database, Trivy reported 0 vulnerability/misconfiguration
  findings, YAML parse covered 1,583 files and the agent-agnostic scan had 0 findings.
  COM-MOD-010-FE-001 compiled the employee-portal administration UI for all 13 COM-MOD-010
  capability packages, comprising 11 permission-filtered screens, a typed inventoryQualityApi
  facade over all 27 backend REST operations, and full es-MX/en-US externalization;
  employee-portal coverage rose from 86.47% to 87.87% with 124 tests (48 test files,
  0 failures); npm audit and Trivy fs (vuln/secret/misconfig, all severities) reported
  0 findings; TD-FE-010 was materially reduced by implementing its own preferred remediation
  (a shared DataTable component and a small-sub-component decomposition convention)
  applied to all 11 new screens with 0 new lint size/complexity warnings; TD-STACK-003
  and TD-I18N-002 were further reduced. COM-MOD-010-QA-001 validated end-to-end traceability
  across all 13 COM-MOD-010 capability packages, fixed a stale backlog_items.custom_rules
  traceability pointer in the 9 BCM-INV-001..009 traceability.md files (was pointing
  at COM-MOD-010-BE-002/pending; corrected to COM-MOD-010-BE-001/closed) and a stale
  capability-package-index.md roadmap-group pointer, and found and fixed a real
  backend coverage gap -- a clean rebuild reproducibly measured 81.90% (below the
  82.94% floor, 0 backend source changes since BE-002) because BE-002's 4 new JDBC
  adapters (quality-control/calibration/equipment/maintenance) had no local-database
  integration test; added InventoryQualityControlsLocalDatabaseTest.java, raising
  corrected backend coverage to 83.73% (315 tests, 0 failures/errors/skipped). Employee-portal
  coverage confirmed at 88.24% (floor 87.87%, 124 tests/48 files, 0 failures). OWASP
  Dependency-Check (65 dependencies), npm audit and Trivy fs (vuln/secret/misconfig,
  all severities) reported 0 vulnerabilities/secrets/misconfigurations; YAML parse
  (1,105 files) and agent-agnostic scan passed; git diff --check clean. COM-MOD-010-CLOSEOUT
  closed the module -- all 13 COM-MOD-010 capability packages (BCM-INV-001..009, BCM-QLT-001/003/004/005)
  were confirmed module_closed in capability-package-index.md and their traceability.md
  files; the technical-debt index was reviewed and found zero open or materially-reduced
  debt attributable to COM-MOD-010; this was a documentation/registry-only closeout
  (no source code touched), so backend (83.73%), employee-portal (88.24%), mobile
  (99.21%), patient-portal (94.11%) and doctor-portal (96.28%) coverage are re-affirmed
  unchanged rather than re-measured; YAML parse, a stale-pointer sweep and git diff
  --check were executed for the closeout itself. COM-MOD-011-DEF is closed. All 7
  COM-MOD-011 capabilities (BCM-SVC-001/002/003/005, BCM-ATT-001/006, BCM-PLT-005)
  were confirmed reused from already-modeled/compiled capability packages owned by
  MVP-MOD-002, MVP-MOD-004 and MVP-MOD-008, with zero new capability package, aggregate
  or schema created; each package's product_surfaces, openapi-source.md, ui-model.md,
  permissions.md and traceability.md were extended with a public_website surface;
  TD-BE-015 was materially reduced via a new BCM-PLT-005 RN-007 and RateLimitPolicy.consumerIdentificationMethod
  field; three pre-existing stale roadmap/status pointers unrelated to this item's
  own scope were found and corrected in BCM-ATT-001, BCM-ATT-006 and BCM-PLT-005;
  definition-only, no code implemented, coverage unchanged. COM-MOD-011-BE-001 is
  closed. It compiled the backend for HOP's anonymous public-website surface -- ten
  new REST operations under /api/public/** (published-only catalog reads for BCM-SVC-001/002/003/005,
  BCM-ATT-001 RN-008 anonymous appointment intake, BCM-ATT-006 RN-009 anonymous quotation
  intake), a new publicweb Spring Modulith module depending only on the new catalogtestconfiguration::catalog-public-read-port
  and frontdeskcaredelivery::public-intake-port named interfaces, and BCM-PLT-005
  RN-007 rate-limit enforcement via a new PublicApiRateLimitInterceptor driven by
  RateLimitPolicy.consumerIdentificationMethod (closes TD-BE-015). Two additive DDL
  migrations (ADD COLUMN IF NOT EXISTS) extended the existing rate_limit_policies
  and appointments schema files. Added the public.error.* and public.rate_limit.*
  i18n key namespaces in es-MX and en-US (TD-I18N-002 further reduced). Backend line
  coverage 83.73% -> 83.96% with 324 tests and 0 failures/errors/skipped against a
  running compose.local.json PostgreSQL 16 container; OWASP Dependency-Check reported
  108 dependencies scanned and 0 vulnerabilities; Trivy fs (vuln/secret/misconfig,
  all severities) reported 0 findings; YAML parse succeeded for 1,156 files with 0
  errors; agent-agnostic scan reported 0 real source-code hits; git diff --check reported
  0 whitespace errors. COM-MOD-011-WEB-001 is closed. It compiled the public website
  frontend at 07-implementation/public-website/ (React 19 + TypeScript 5 strict +
  Vite 6, following the patient-portal/doctor-portal conventions -- hand-rolled LocaleContext,
  plain-fetch httpClient, no state-management library, plus a new hand-rolled History-API
  router instead of a router dependency), consuming the anonymous /api/public/** surface
  from COM-MOD-011-BE-001 -- published catalog discovery for diagnostic services/tests/panels/preparations
  (BCM-SVC-001/002/003/005) and public appointment/quotation request intake (BCM-ATT-001
  RN-008, BCM-ATT-006 RN-009) with an explicit client-side cooldown for BCM-PLT-005
  RN-007's 429 rate-limit responses, since the backend sends no Retry-After header.
  Deployment identity (tenantId/laboratoryId/branch list) is deployment-owned site
  configuration since COM-MOD-011-DEF modeled no public branch-directory capability.
  Added SEO (per-page title/description/canonical/Open Graph metadata, robots.txt,
  sitemap.xml), accessibility (eslint-plugin-jsx-a11y plus an automated jest-axe regression
  check wired into npm run test/quality) and privacy (a /privacy notice page, required
  consent checkboxes on both request forms) foundations, materially reducing TD-UX-002
  -- documented responsive breakpoints and an automated accessibility check, the debt's
  own acceptance criteria -- as the reference pattern for this new module; employee-portal
  itself was not touched, so the debt is not closed. Established the first coverage
  baseline for this stack -- 97 tests, 34 test files, 0 failures, 98.61% line/statement
  coverage, 93.15% branch coverage, 87.70% function coverage. ESLint reported 0 errors
  and 16 non-blocking warnings; jscpd reported 3.9% duplication, below the 5% threshold;
  npm audit and Trivy fs (vuln/secret/misconfig, all severities) reported 0 findings;
  agent-agnostic scan reported 1 false positive (a CSS cursor property) and 0 real
  hits; git diff --check reported 0 whitespace errors. Verified locally via npm run
  build && npm run preview (production shell served correctly). Docker later became
  reachable in the same session, so full live end-to-end verification was then performed
  against a real backend and Postgres instance for all 10 /api/public/** operations
  through the real dev proxy; this surfaced and fixed a real pre-existing defect where
  catalog-test-configuration/schema.sql seed rows used status='PUBLISHED' (uppercase)
  against the lowercase published domain constant used by every catalog listPublished/getPublishedSnapshot
  filter, silently hiding every seeded catalog row from published-only views project-wide.
  Corrected the 10 seed literals to lowercase (no Java source changed) and re-ran
  backend regression gates clean -- mvn -Pquality -Dhop.local-db-tests=true clean
  verify (324 tests, 0 failures/errors/skipped, coverage unchanged at 83.96%), checkstyle/pmd/spotbugs/duplicate-finder
  (0 new violations), OWASP Dependency-Check (65 dependencies, 0 vulnerabilities)
  and Trivy fs on the backend directory (0 vulnerabilities/secrets/misconfigurations).
  COM-MOD-011-FE-001 is closed. It compiled the staff-facing content and public-request
  administration screens in the existing employee portal at 07-implementation/employee-portal/
  -- PublicContentReviewScreen consumes the same anonymous /api/public/catalog/**/published
  endpoints the public website itself calls (rather than the internal catalog-admin
  API already owned by DiagnosticCatalogScreen), so no tenantId/audit/internal field
  can leak into the staff view by construction; PublicAppointmentRequestsScreen and
  PublicQuotationRequestsScreen triage the existing internal /api/care-delivery/appointments
  and /api/care-delivery/quotations endpoints, filtered client-side to channel=="public_website"
  and the pending status, with Confirm/Reject and Issue/Reject actions reusing existing
  confirm/cancel/issue endpoints -- no new backend action endpoint was created. A
  real defect was found and fixed along the way -- QuotationRequest had no channel
  field, unlike AppointmentSlot, so public-website-submitted quotation drafts could
  not be reliably distinguished from staff-initiated ones; added QuotationRequest.channel
  (mirroring AppointmentSlot's CHANNEL_* constants), an additive nullable care_delivery.quotations.channel
  column, and QuotationManagementService validation defaulting to channel=employee_portal
  when omitted (preserving every untouched caller) while startPublic() always stamps
  channel=public_website regardless of input. This touched the backend, so its full
  Maven quality profile was re-run -- 327 tests, 0 failures, backend line coverage
  83.96% -> 83.99%. 3 new ScreenKey/PermissionCode pairs were added, granted to ADMIN
  and FRONT_DESK, with navigation tabs hidden (not disabled) for other roles per the
  IAM permission model; all visible text uses new namespaced es-MX/en-US message groups.
  TD-UX-002 is now closed (not just materially reduced) -- retrofitted the same documented
  responsive breakpoints and automated jest-axe accessibility check COM-MOD-011-WEB-001
  established as the reference pattern into employee-portal itself, the debt's originally
  discovered affected_area, closing the remaining_scope that item left open; eslint-plugin-jsx-a11y
  surfaced and fixed one real finding (ConfirmDialog.tsx's autoFocus). Employee-portal
  coverage rose 88.24% -> 88.68% with 154 tests (54 test files, 0 failures). Trivy
  fs on the backend found 1 MEDIUM vulnerability (CVE-2026-59889, tools.jackson.core:jackson-databind
  3.1.4, a Jackson 3.x line managed by spring-boot-starter-parent) before the fix;
  pinned to 3.1.5 in pom.xml (mirroring the existing classic-Jackson-2.x pin pattern),
  re-scan confirmed 0. OWASP Dependency-Check post-fix revalidation passed with the
  project quality profile (65 dependencies, 0 vulnerable dependencies, 0 vulnerabilities;
  report dated 2026-07-22T18:03:17Z) using the local advisory database, and Trivy
  independently confirmed 0 findings after the Jackson pin. npm audit and Trivy fs
  (frontend and backend, vuln/secret/misconfig, all severities) reported 0 findings
  after the fix; agent-agnostic scan reported 4 false positives (CSS cursor property)
  and 0 real hits; git diff --check reported 0 whitespace errors. COM-MOD-011-QA-001
  is closed. Integrated quality, privacy, SEO, accessibility, and security validation
  for COM-MOD-011 was executed clean across all 7 capability packages (BCM-SVC-001/002/003/005,
  BCM-ATT-001/006, BCM-PLT-005) with 0 vulnerabilities, 0 security findings, 0 test
  failures, and 0 coverage regressions (backend 83.99%, public website 98.61%, employee
  portal 88.68%, mobile 99.21%, patient portal 94.11%, doctor portal 96.28%). Maven
  quality profile (327 tests), OWASP Dependency-Check (0 vulnerabilities), npm audit
  on public-website and employee-portal (0 vulnerabilities), Trivy fs (0 vulnerabilities,
  0 secrets, 0 misconfigurations), repository YAML parse (1,157 files, 0 errors),
  agent-agnostic scan (0 hits), and git diff --check (0 whitespace errors) passed
  clean. Stale backlog pointers in all 7 capability package traceability.md files
  and capability-package-index.md were updated with COM-MOD-011-QA-001 validation
  entries. COM-MOD-011-CLOSEOUT closed the module -- all 7 COM-MOD-011 capability
  packages (BCM-SVC-001/002/003/005, BCM-ATT-001/006, BCM-PLT-005) were confirmed
  module_closed in capability-package-index.md and their traceability.md files;
  technical-debt items TD-BE-015 and TD-UX-002 were verified closed with zero open
  or blocking technical debt attributable to COM-MOD-011; documentation/registry-only
  closeout, no source code touched, so full backend/frontend/mobile quality suites
  were not re-executed and coverage figures -- backend 83.99%, employee portal 88.68%,
  public website 98.61%, mobile 99.21%, patient portal 94.11%, doctor portal 96.28%
  -- are re-affirmed unchanged from COM-MOD-011-FE-001/QA-001 evidence; executed YAML
  parse, stale-pointer sweep and git diff --check for this closeout; advanced the
  active commercial backlog item to COM-MOD-012-DEF.
- COM-MOD-012-OPS-001 is closed. It added the production deployment and environment
  strategy under 09-operations/deployment, covering local/dev/qa/staging/prod, deployment
  units, configuration and secrets, tenant onboarding, release promotion, rollback
  and deployment readiness. TD-STACK-001 was materially reduced. This was an operations-definition
  backlog item with no code, runtime, port or dependency changes; coverage floors
  remain backend 83.99%, employee portal 88.68%, public website 98.61%, mobile 99.21%,
  patient portal 94.11% and doctor portal 96.28%. Next active backlog item is COM-MOD-012-OPS-002.
- COM-MOD-012-OPS-002 is closed. It added 10 executable runbook pairs (observability,
  health/readiness/liveness, metrics/logs/traces validation, backup, restore, incident
  response, rollback incident handoff, tenant-impact triage, evidence collection,
  post-incident review) plus an index README under 09-operations/runbooks/, built
  on production-deployment-strategy.md; every local-executable command was cross-checked
  against real repository state (compose.local.json, .env.example, application.properties,
  AuditComplianceController) rather than assumed, and unimplemented telemetry (Prometheus
  metrics, trace export, tenant_id/user_id/trace_id MDC logging) and unprovisioned
  dev/qa/staging/prod infrastructure were documented per-runbook as known_gaps_and_forward_pointers
  rather than silently marked passed. TD-DB-004 was materially reduced via tenant-impact-triage-runbook.md's
  mandatory cross-tenant leakage check, an operational compensating control pending
  native Row Level Security. Capability traceability was updated for all 8 COM-MOD-012
  capabilities. This was a definition-only operations backlog item with no code, runtime,
  port or dependency changes; coverage floors remain unchanged. Next active backlog
  item is COM-MOD-012-BE-001.
- COM-MOD-012-BE-001 is closed. It compiled BCM-ORG-001 tenant operations (provisionTenant
  extended in place with code/legalName/tradeName/taxId/tier/isolationStrategy; listTenants
  and updateTenantStatus added, both privileged and audited via the existing BCM-PLT-007
  AuditRecorder), a new BCM-PLT-002 platformconfiguration Spring Modulith module (getPlatformConfig,
  evaluateFeatureFlags, updateFeatureFlag, validated per business-model.md invariants),
  and BCM-PLT-006 observability extensions (micrometer-registry-prometheus dependency
  plus GET /actuator/prometheus, explicit liveness/readiness health groups, a new
  RequestObservabilityContextFilter populating tenantId/userId/traceId MDC on every
  log line). This closed 5 of 8 named COM-MOD-012-OPS-002 runbook known_gaps_and_forward_pointers
  entries; the remaining 3 (distributed trace export, a provisioned Grafana/Prometheus/Loki
  stack, SLO/SLA alerting) require infrastructure not yet provisioned and were re-pointed
  to future items. BCM-PLT-001/005/007/008/009 extensions were deliberately deferred
  (not exposed by any operation this item needed to compile) and registered as TD-BE-016/TD-BE-017/TD-IAM-003
  rather than built speculatively. A backward-compatible ProvisionTenantRequest.name
  fallback plus an auto-derived tenant code kept roughly 20 pre-existing module test
  fixtures working unchanged against the richer Tenant model. A real SpotBugs/FindSecBugs
  SERVLET_HEADER finding on the new MDC filter was fixed in code (control-character
  stripping, strict W3C traceparent validation), not suppressed. Backend line coverage
  83.99% -> 84.11% with 362 tests and 0 failures/errors/skipped against a running
  compose.local.json PostgreSQL 16 container; OWASP Dependency-Check reported 115 dependencies
  scanned and 0 vulnerabilities; Trivy fs (vuln/secret/misconfig, all severities)
  reported 0 findings; YAML parse succeeded for 1,248 files with 0 errors; agent-agnostic
  scan reported 0 real source-code hits; git diff --check reported 0 whitespace errors.
  TD-IAM-002 and TD-DB-004 were materially reduced further; TD-I18N-002 was further
  reduced. Next active backlog item is COM-MOD-012-QA-001.
- COM-MOD-012-QA-001 is closed. It validated all 8 COM-MOD-012 capabilities end to
  end against a running local backend (tenant provisioning/listing/status transition,
  platform config, feature flags, Prometheus, health groups, MDC logging, audit events),
  with a light local load check (30 sequential + 20 concurrent requests, 0 failures).
  It found and fixed a real resilience defect -- the readiness probe did not reflect
  database connectivity because management.endpoint.health.group.readiness.include
  was unset in application-local.properties; re-verified live via a real docker stop/start
  of hop-local-postgres. A dedicated OWASP ZAP API scan against the full backend surface
  (353 URLs, deferred by BE-001) found and this item fixed 2 real defects -- TD-QA-005
  (a null byte or oversized string value reaching JDBC caused an unhandled 500, fixed
  via a narrow GlobalExceptionHandler SQLState-class-22 mapping) and TD-QA-006 (AuthController.initiateAssistance
  returned 500 instead of 404 for a nonexistent assistedUserId, fixed by widening
  IdentityAccessExceptionHandler's scope); a final rescan confirmed 0 FAIL-NEW/0 WARN-NEW.
  A ZAP baseline scan against the unchanged employee portal found 0 FAIL-NEW. It executed
  a real backup (pg_dump, SHA-256 checksum, pg_restore --list showing 415 TOC entries)
  and restore rehearsal (isolated database, matching row counts 40=40). It reviewed
  the 3 remaining COM-MOD-012-BE-001 infrastructure forward pointers (distributed
  trace export, provisioned Grafana/Prometheus/Loki, SLO/SLA alerting) and registered
  TD-OBS-001 rather than closing them without real infrastructure. Backend line coverage
  84.11% -> 84.14% with 367 tests and 0 failures/errors/skipped. Next active backlog
  item is COM-MOD-012-CLOSEOUT.
- ? COM-MOD-012-CLOSEOUT is closed. COM-MOD-012 Platform Hardening and SaaS Operations
    is module_closed -- all 8 capability packages (BCM-ORG-001, BCM-PLT-001, BCM-PLT-002,
    BCM-PLT-005, BCM-PLT-006, BCM-PLT-007, BCM-PLT-008, BCM-PLT-009) were confirmed
    module_closed in capability-package-index.md and their traceability.md files;
    TD-QA-005 and TD-QA-006 were confirmed closed; TD-OBS-001, TD-BE-016, TD-BE-017
    and TD-IAM-003 were confirmed open, non-blocking and correctly classified with
    owner, risk level and target backlog, and were not closed without real infrastructure
    or implementation. This closeout found and corrected 2 stale registry defects
    predating it
  : a stale operational_strategy status of active left across all 8 traceability.md
    files after COM-MOD-012-OPS-002 closed (corrected to closed), and a duplicate
    active_capability_package_groups block in capability-package-index.md still
    listing the already-closed COM-MOD-011 as active (removed). Documentation/registry-only
    closeout, no source code touched; backend (84.14%), employee portal (88.68%),
    public website (98.61%), mobile (99.21%), patient portal (94.11%) and doctor portal
    (96.28%) coverage are re-affirmed unchanged from COM-MOD-012-QA-001 evidence;
    YAML parse, stale-pointer sweep, evidence-state sweep, agent-agnostic scan, secrets
    scan and git diff --check were executed for this closeout; advanced the active
    commercial backlog item to COM-MOD-013-DEF.
- COM-MOD-013-QA-001 is closed. Integrated validation found and closed a major persistence-wiring
  defect (TD-DB-005) -- application-local.properties never registered db/external-quality-and-compliance/schema.sql,
  compounded by an inverted @Profile on the 4 externalqualitycompliance JDBC/in-memory
  repository pairs (real JDBC classes were @Profile("!local & !test") instead of the
  codebase's @Profile("local") convention). Fixed both root causes; re-ran ExternalQualityComplianceLocalDatabaseTest
  live against real PostgreSQL (passed). Backend coverage rose to 84.24% at that point
  (381 tests, 0 failures). Also fixed 2 SpotBugs High and 5 Medium findings, 1 hardcoded
  i18n string and 1 TD-FE-010 function-size violation in ComplianceEvidenceScreen.tsx
  (employee-portal coverage 89.74% -> 89.75%, 187 tests, 60 files). A required OWASP
  ZAP DAST pass against the running backend (939 URLs) and employee-portal (125 URLs)
  found and fixed TD-QA-007 (unhandled 500 on malformed multipart upload, remapped
  to 400), confirmed clean by re-scan (0 FAIL-NEW/0 WARN-NEW), raising backend coverage
  to 84.25% (382 tests). Registered TD-IAM-004 (synthetic tenant ID, open non-blocking).
  OWASP Dependency-Check, npm audit and Trivy reported 0 findings.
- COM-MOD-013-CLOSEOUT is closed. COM-MOD-013 Advanced Quality and Compliance is module_closed
  -- all 5 capability packages (BCM-QLT-002, BCM-QLT-006, BCM-QLT-007, BCM-PLT-007,
  BCM-PLT-008) were confirmed module_closed in capability-package-index.md and their
  traceability.md files; TD-DB-005 and TD-QA-007 were confirmed closed; TD-IAM-004
  was confirmed open, non-blocking and correctly classified; TD-I18N-002, TD-FE-010,
  TD-BE-002 and TD-FE-005 maintain honest status. Documentation/registry-only closeout,
  no source code touched; backend (84.25%), employee portal (89.75%), public website
  (98.61%), mobile (99.21%), patient portal (94.11%) and doctor portal (96.28%) coverage
  floors re-affirmed clean; YAML parse, stale-pointer sweep, evidence-state sweep,
  agent-agnostic scan, secrets scan and git diff --check clean; advanced active commercial
  backlog item to COM-MOD-016-DEF.
- COM-MOD-016-DEF is closed. Capability package models for Commercial Launch and Customer
  Enablement (BCM-ORG-001, BCM-ORG-002, BCM-ORG-003, BCM-PLT-002, BCM-PLT-006, BCM-PLT-007,
  BCM-PLT-008) modeled and traced in capability-package-index.md and package registries.
  Definition-only backlog item, no code implemented. Coverage baselines across all
  6 stacks (backend 84.25%, employee portal 89.75%, public website 98.61%, mobile
  99.21%, patient portal 94.11%, doctor portal 96.28%) re-affirmed clean. Full repository
  sweeps (YAML parse across 1,347 files, stale-pointer sweep, evidence-state sweep,
  agent-agnostic scan, secrets scan and git diff --check) executed clean. Advanced
  active commercial backlog item to COM-MOD-016-DOC-001.
- COM-MOD-016-DOC-001 is closed. Customer onboarding and configuration guides (ONB-GUIDE-001
  through ONB-GUIDE-008, MD and YAML specification pairs created under 09-operations/onboarding/)
  completed and validated. Covered customer/tenant onboarding lifecycle, organization/lab/branch/user
  setup, RBAC (27 permissions), dynamic navigation, regional localization (es-MX/en-US,
  ISO currency MXN/USD), technical prerequisites, open data ingestion (BCM-PLT-010),
  training/human validation/acceptance protocol, and L1-L3 support/escalation SLAs.
  Coverage floors across all 6 stacks re-affirmed clean. Full repository sweeps (YAML
  parse, stale pointer sweep, evidence-state sweep, agent-agnostic scan, secrets scan
  and git diff --check) executed clean. Advanced active commercial backlog item to
  COM-MOD-016-OPS-001.
- COM-MOD-016-OPS-001 is closed. Support, escalation and release governance (GOV-SPEC-001
  through GOV-SPEC-010, MD and YAML specification pairs created under 09-operations/governance/)
  completed and validated. Covered L1-L3 support model, escalation matrix, SLAs/SLOs,
  incident management, problem management/RCA, change management/CAB, release governance
  & readiness checklist, rollback & hotfix governance, implementation-to-ops handoff,
  customer incident/release communication, and operational acceptance criteria (OAC).
  Integrated with onboarding/ and runbooks/. Coverage floors across all 6 stacks re-affirmed
  clean. Full repository sweeps (YAML parse, stale pointer sweep, evidence-state sweep,
  agent-agnostic scan, secrets scan and git diff --check) executed clean. Advanced
  active commercial backlog item to COM-MOD-016-COM-001.
- 'COM-MOD-016-COM-001 is closed. Pricing package, sales demo and launch readiness
  assets completed and validated. Commercial Packages: created product packages (Starter/Professional/Enterprise
  with expansion packages), capability matrix mapping all 70+ BCM capabilities to
  tiers, initial pricing model with subscription/volume/add-on pricing and pilot program,
  and tenant upgrade/downgrade criteria with migration paths and rollback policy under
  06-delivery/commercial-product/commercial-packages/. Sales Enablement: created 45-minute
  sales demo script with 12 sections covering the complete operating cycle, demo data
  checklist, sales enablement one-pager with value proposition and competitive positioning,
  5 buyer personas (Lab Director, Quality Manager, IT Manager, CFO, Operations Manager)
  with pain points and use cases, and customer value proposition with ROI indicators
  under 06-delivery/commercial-product/sales-enablement/. Launch Readiness: created
  launch readiness checklist mapped to all 9 Commercial Readiness Pillars (CRP-001
  through CRP-009, 7 verified, 2 planned for future releases), and customer acceptance
  criteria with 3-phase commercial handoff protocol (sales-to-implementation, implementation-to-hypercare,
  hypercare-to-BAU) plus pilot-to-GA promotion gates under 06-delivery/commercial-product/launch-readiness/.
  Integration: updated onboarding-index.md and governance-index.md with cross-references.
  Total 25 new documentation files (YAML+MD pairs plus 3 READMEs). Coverage floors
  across all 6 stacks re-affirmed clean (documentation item, no code changes). Full
  repository sweeps (YAML parse, stale-pointer sweep, agent-agnostic scan, secrets
  scan and git diff --check) executed clean. Advanced active commercial backlog item
  to COM-MOD-016-QA-001.'
- 'COM-MOD-016-QA-001 is closed. Commercial readiness validation confirmed COM-MOD-016-DEF
  (7 capability packages BCM-ORG-001, BCM-ORG-002, BCM-ORG-003, BCM-PLT-002, BCM-PLT-006,
  BCM-PLT-007, BCM-PLT-008), COM-MOD-016-DOC-001 (8 onboarding guides), COM-MOD-016-OPS-001
  (10 governance specifications) and COM-MOD-016-COM-001 (commercial packages, sales
  enablement and launch readiness assets) are complete, internally consistent and
  traceable, with no stub or placeholder content, no vendor/agent lock-in terms, no
  real or synthetic patient/personal data in demo assets, no secrets, and no forbidden
  execution-status markers anywhere in COM-MOD-016 scope. Found and fixed a stale-pointer
  defect: capability-package-index.md''s COM-MOD-016 active group and all 7 package
  traceability.md commercial_enablement blocks were still pointing at COM-MOD-016-OPS-001
  even though COM-MOD-016-COM-001 had since closed; advanced all 8 to COM-MOD-016-QA-001
  with COM-001/OPS-001 history preserved. Found and fixed PROJECT_STATE.md''s implementation_progress.completed_backlog_items
  omitting COM-MOD-016-COM-001. Added SOURCE_OF_TRUTH.md sources: keys for the COM-MOD-016-OPS-001
  and COM-MOD-016-COM-001 output artifacts, which had not been registered when those
  items closed (only COM-MOD-016-DEF and COM-MOD-016-DOC-001 had been). Confirmed
  capability_package_progress.COM-MOD-016 is correctly absent from PROJECT_STATE.md
  (that block is populated at module CLOSEOUT, matching the pattern for every prior
  module, not at the QA-001 stage). Registered TD-QA-008 (open, non-blocking, low
  risk): OWASP ZAP''s local availability is undocumented in local-toolchain-inventory.md
  and stack-quality-toolchain-baseline.md still states ZAP is unavailable even though
  TD-QA-001 closed on real ZAP runs; this did not affect COM-MOD-016 since no runnable
  surface changed in this module and DAST was correctly treated as not_applicable_now
  for a documentation-only validation. pricing-model.md''s status: draft is an intentional,
  self-disclosed gate (final pricing requires market validation and executive approval
  before commercial launch), already reflected as a non-blocking planned pillar in
  launch-readiness-checklist.md (overall_launch_readiness_assessment: conditionally_ready,
  blocking_items: []); not a defect. Reviewed the technical-debt index: 18 open and
  11 materially-reduced items exist project-wide, none blocking and none scoped to
  COM-MOD-016, consistent with prior QA-001 closures (e.g. COM-MOD-013-QA-001 closed
  with TD-IAM-004 open non-blocking); final_project_closure_requires_ no_open_debt
  applies at final GA/project closure, not at this module gate. Coverage floors across
  all 6 stacks re-affirmed unchanged (documentation/registry-only item, no source
  code touched): backend 84.25%, employee portal 89.75%, mobile 99.21%, patient portal
  94.11%, doctor portal 96.28%, public website 98.61%. Full repository sweeps executed
  clean: YAML parse, stale-pointer sweep, forbidden execution-status token sweep,
  agent-agnostic scan, secrets scan, MD/YAML consistency check, capability traceability
  check and git diff --check. Advanced active commercial backlog item to COM-MOD-016-CLOSEOUT.'
- 'COM-MOD-016-CLOSEOUT is closed. Commercial Launch and Customer Enablement is module_closed:
  all 6 backlog items are closed, all 7 capability packages (BCM-ORG-001, BCM-ORG-002,
  BCM-ORG-003, BCM-PLT-002, BCM-PLT-006, BCM-PLT-007, BCM-PLT-008) are marked module_closed
  in capability-package-index.md and their traceability.md files, and PROJECT_STATE.md
  now contains capability_package_progress.COM-MOD-016. TD-QA-008 was reviewed and
  remains open non-blocking as project-wide toolchain inventory debt, not a capability-specific
  blocker. Documentation/registry-only closeout, no source code touched; backend 84.25%,
  employee portal 89.75%, public website 98.61%, mobile 99.21%, patient portal 94.11%
  and doctor portal 96.28% coverage floors are re-affirmed unchanged. YAML parse,
  stale-pointer sweep, evidence-state sweep, agent-agnostic scan, secrets scan and
  git diff --check executed clean. Advanced active commercial backlog item to COM-MOD-017-DEF.'
- HOP agents must execute at least one technical-debt remediation or material reduction
  before feature implementation in each code-changing backlog item unless no open
  debt exists.
- HOP technical-debt burn-down must become stricter as the project advances; final
  project closure requires no open technical debt.
- HOP line coverage target is 80 percent for every applicable delivered stack.
- If a stack is below 80 percent during an intermediate iteration, the previous measured
  coverage is the hard lower bound and must not decrease.
- If a changed stack remains below 80 percent, each relevant iteration must target
  a 3 to 5 percentage point improvement or document why that is not achievable with
  immediate coverage debt.
- HOP must satisfy enterprise product foundations before customer-facing portal/app expansion continues: es-MX/en-US
    localization, language switching, IAM permission mapping, dynamic menus/actions,
    login/session context, product database deliverables, UX/UI design baseline, code
    documentation, persistence architecture and OpenAPI/contract-first generation
    review.
- HOP cannot be marked commercially complete, GA-ready or finally closed while any
  applicable stack is below 80 percent line coverage.
- HOP engineering excellence findings must be classified as P0 minimum, P1 technical-debt
  backlog or P2 contextual/desirable before deciding whether they block delivery.
- HOP must maintain 09-operations/runbooks/local-solution-runbook.md and .yaml as
  the single integrated local startup, validation and shutdown guide for human reviewers.
- HOP agents must update or explicitly confirm the integrated local runbook whenever
  runtime components, ports, variables, startup order, validation commands or review
  surfaces change.
- HOP technology reviews must not be constrained permanently by the initial stack;
  agents must register non-blocking upgrade, migration, dependency, framework or tooling
  findings in 08-qa/technical-debt/ and remediate gradually when affected components
  are touched.
- HOP execution feedback that can improve the Nexora framework must be registered
  under 08-qa/framework-feedback/.
- HOP agents may propose central framework backlog items but must not implement framework
  improvements unless Nexora explicitly assigns them.
- Generated artifacts must not be edited manually.
- Architecture changes require ADR.
```
