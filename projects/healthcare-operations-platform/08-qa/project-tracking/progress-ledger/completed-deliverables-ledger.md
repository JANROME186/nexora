---
artifact:
  id: HOP-TRACK-COMPLETED-DELIVERABLES
  type: project-progress-ledger
  status: active
  optimization: atomic_context
---

# Completed Deliverables Ledger

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
project: Healthcare Operations Platform
total_completed_deliverables: 132
payload_hash: '277579999803'
completed_deliverables:
- COM-MOD-015-FE-001 AI assistant and review UI compiled (added the employee-portal AI assistant review screen and typed
  aiOverlayApi client on the generic /api/ai/assistant/sessions backend surface for OCR document intake, result/case
  summaries, semantic search and retrieval grounding; exposed citations, confidence band, model provider/name and policy
  version metadata; blocked human review when citations are absent; enabled audit-record selection; wired SCREEN_AI_ASSISTANT
  navigation, i18n, tenant/actor header aliases and focused API/screen/session smoke tests; materially reduced TD-UX-001 by
  adopting shared DataTable, StatusBanner and ScopeIndicator patterns; synced frontend_typescript_web coverage floor to
  91.00% with 256 tests / 69 test files / 0 failures; QA evidence 08-qa/qa/ai-overlay/COM-MOD-015-FE-001-validation.md,
  security evidence 08-qa/security-quality/COM-MOD-015-FE-001/security-quality-evidence.md, handoff
  08-qa/handoffs/COM-MOD-015-FE-001-summary.md)
- COM-MOD-015-BE-002 OCR, summary, search and retrieval custom rules compiled (added AiOverlayCapability
  and AiOverlayCapabilityRuleEngine to platformfoundation.aioverlay, enforcing BCM-AI-002..005's guardrails --
  source context scoping per capability and mandatory citations -- on top of the BCM-AI-001 assistant
  orchestration; output stays human-review-required and no new REST surface or IAM permission entry was
  needed since /api/ai already covers it; synced TD-BE-017's stale status field and the technical-debt-index.md
  backend_java_maven coverage baseline (never synced since COM-MOD-017-CLOSEOUT through the COM-MOD-014
  backend expansion, now 70.14%); registered TD-BE-021 for the deliberately-not-compiled dedicated
  per-capability REST paths; QA evidence 08-qa/qa/ai-overlay/COM-MOD-015-BE-002-validation.md, security
  evidence 08-qa/security-quality/COM-MOD-015-BE-002/security-quality-evidence.md, handoff
  08-qa/handoffs/COM-MOD-015-BE-002-summary.md)
- COM-MOD-015-BE-001 AI Overlay backend outputs compiled (created provider-neutral aioverlay Spring Modulith module for
  assistant draft orchestration, safety policy enforcement, human review and audit-record listing; added local deterministic
  AiDraftGeneratorPort adapter with no proprietary model/runtime dependency; persisted ai_overlay.ai_interactions in local
  PostgreSQL profile; registered SCREEN_AI_ASSISTANT for /api/ai; added i18n messages and focused API/service/local DB tests;
  materially reduced TD-BE-017 by introducing a real orchestration/audit target and reduced test temp-path fragility in
  LocalFilesystemDocumentAdapterTest; QA evidence 08-qa/qa/ai-overlay/COM-MOD-015-BE-001-validation.md, security evidence
  08-qa/security-quality/COM-MOD-015-BE-001/security-quality-evidence.md, handoff 08-qa/handoffs/COM-MOD-015-BE-001-summary.md)
- COM-MOD-015-DEF Capability package models for AI Overlay (modeled 8 provider-neutral capability packages BCM-AI-001 through
  BCM-AI-008 under 01-product-definition/business-capabilities/packages/ with the standard 14 artifacts each; registered COM-MOD-015
  as definition_completed in capability-package-index.md; all AI outputs are advisory, attributable, source-cited, auditable
  and human-review controlled with autonomous clinical validation explicitly prohibited; materially reduced TD-FMT-001 through
  compact Markdown/frontmatter handoff and evidence; QA evidence 08-qa/qa/ai-overlay/COM-MOD-015-DEF-validation.md, security
  evidence 08-qa/security-quality/COM-MOD-015-DEF/security-quality-evidence.md, handoff 08-qa/handoffs/COM-MOD-015-DEF-summary.md)
- COM-MOD-014-CLOSEOUT Module closeout and registry update (BCM-IMG-001 through BCM-IMG-008 marked module_closed in capability-package-index.md -- moved from active_capability_package_groups to completed_capability_package_groups -- and in their capability-package.md files and BCM-IMG-001 traceability.md; documentation and registry-only closeout -- no source changed, coverage re-affirmed unchanged: backend 84.65%, employee portal 90.85% overall / 90.87% screens, public website 98.61%, mobile 99.21%, patient portal 94.11%, doctor portal 96.28%; synced technical-debt-index.md frontend line coverage baseline to 90.85%; recorded material reductions for TD-DEF-002, TD-I18N-002, TD-FE-010; QA evidence 08-qa/qa/imaging-operations/COM-MOD-014-CLOSEOUT-validation.md, security evidence 08-qa/security-quality/COM-MOD-014-CLOSEOUT/security-quality-evidence.md, handoff 08-qa/handoffs/COM-MOD-014-CLOSEOUT-summary.md)
- COM-MOD-014-QA-001 Imaging integration and report evidence QA validation (validated end-to-end quality assurance gates for
  all 8 BCM-IMG-* sub-packages across backend and frontend; resolved sonarjs/no-hardcoded-ip lint error in ImagingDicomScreen.tsx;
  corrected ImagingReportsScreen JSX markup; expanded unit test coverage to 249/249 passed; maintained backend line coverage
  >= 84.65% and employee portal line coverage at 90.85% overall / 90.87% screens; typecheck zero errors, ESLint zero errors,
  production build clean, 0 npm audit vulnerabilities; QA evidence 08-qa/qa/imaging-operations/COM-MOD-014-QA-001-validation.md,
  security evidence 08-qa/security-quality/COM-MOD-014-QA-001/security-quality-evidence.md, handoff 08-qa/handoffs/COM-MOD-014-QA-001-summary.md)
- COM-MOD-014-FE-001 Compile imaging operations UI outputs (created 8 employee-portal administration screens ImagingAppointmentsScreen,
  ImagingReceptionScreen, ImagingStudiesScreen, ImagingDicomScreen, ImagingPacsScreen, ImagingDictationScreen, ImagingReportsScreen,
  ImagingDeliveryScreen for BCM-IMG-001..008; registered 8 SCREEN_IMAGING_* permissions in permissions.ts; created typed API
  facade imagingOperationsApi.ts; added localized tab labels and imagingOperations i18n catalogs in es-MX.ts and en-US.ts;
  materially reduced TD-I18N-002 and TD-FE-010; unit tests 244/244 passed, coverage >= 90.68%, typecheck zero errors, lint
  zero errors in new code, build clean, 0 audit vulnerabilities; QA evidence 08-qa/qa/imaging-operations/COM-MOD-014-FE-001-validation.md,
  security evidence 08-qa/security-quality/COM-MOD-014-FE-001/security-quality-evidence.md, handoff 08-qa/handoffs/COM-MOD-014-FE-001-summary.md)
- COM-MOD-014-INT-001 Implement DICOM and PACS adapter custom boundaries (expanded DicomGatewayPort and PacsBridgePort with
  DICOM C-FIND MWL worklist query, C-MOVE study transfer, DICOM header metadata validation, QIDO-RS search, WADO-RS retrieve
  URL, and STOW-RS web store payload; implemented out-adapters DicomGatewayAdapter and PacsBridgeAdapter; added REST endpoints
  to DicomIntegrationController and PacsIntegrationController; materially reduced TD-I18N-002 with 3 new imaging.error.* integration
  error codes; QA evidence 08-qa/qa/imaging-operations/COM-MOD-014-INT-001-validation.md, security evidence 08-qa/security-quality/COM-MOD-014-INT-001/security-quality-evidence.md,
  handoff 08-qa/handoffs/COM-MOD-014-INT-001-summary.md)
- COM-MOD-014-BE-001 Compile imaging workflow outputs (compiled Spring Modulith imagingoperations module with 8 capability
  sub-packages BCM-IMG-001 through BCM-IMG-008; registered db/imaging-operations/schema.sql 8 tables; added SCREEN_IMAGING_*
  permissions to PermissionCode, RolePermissionCatalog and EndpointPermissionRegistry; externalized imaging.error.* i18n messages;
  materially reduced TD-DEF-002 and TD-I18N-002; QA evidence 08-qa/qa/imaging-operations/COM-MOD-014-BE-001-validation.md,
  security evidence 08-qa/security-quality/COM-MOD-014-BE-001/security-quality-evidence.md, handoff 08-qa/handoffs/COM-MOD-014-BE-001-summary.md)
- COM-MOD-014-DEF Capability package models for Imaging Operations (modeled 8 capability packages BCM-IMG-001 through BCM-IMG-008
  under 01-product-definition/business-capabilities/packages/; registered COM-MOD-014 as definition_completed in capability-package-index.md;
  materially reduced TD-DEF-002 appointment capacity planning; QA evidence 08-qa/qa/imaging-operations/COM-MOD-014-DEF-validation.md,
  security evidence 08-qa/security-quality/COM-MOD-014-DEF/security-quality-evidence.md, handoff 08-qa/handoffs/COM-MOD-014-DEF-summary.md)
- COM-MOD-017-CLOSEOUT Module closeout and registry update (BCM-PLT-011 marked module_closed in capability-package-index.md
  -- moved from active_capability_package_groups to completed_capability_package_groups -- and in its own traceability.md;
  the 6 reused dependency capabilities BCM-PLT-001/002/005/006/007/009 now carry an explicit owning_roadmap_group pointer.
  Confirmed TD-BE-018/TD-BE-019/TD-BE-020 closed; TD-FE-012 re-confirmed open/non-blocking. Documentation and registry-only
  closeout -- no source changed, coverage re-affirmed unchanged (backend 84.65%, employee portal 90.68%, public website 98.61%,
  mobile 99.21%, patient portal 94.11%, doctor portal 96.28%). Found and corrected two stale technical-debt-index.md coverage_policy.current_stack_baselines
  entries (backend_java_maven 84.53% -> 84.65%, frontend_typescript_web 89.75% -> 90.68%) never synced from COM-MOD-017-BE-002/FE-001/QA-001.
  Found and registered new debt TD-WEB-001 -- ui-model.md's PUBLIC_MARKETPLACE_LISTING public_website surface was modeled
  but never compiled (COM-MOD-017-WEB-001 never scheduled); non-blocking, outward discovery surface only. Reconciled stale
  "next backlog item" pointers in HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md and HOP_COMMERCIAL_PRODUCT_BACKLOG.md. REL-003
  Commercial General Availability is now fully complete (COM-MOD-013, COM-MOD-016 and COM-MOD-017 all module_closed); advanced
  active backlog to COM-MOD-014-DEF)
- COM-MOD-017-QA-001 Integrated marketplace validation (4 traceability sweeps run -- openapi-source.md vs. the 6 marketplace
  controllers, IAM permissions across PermissionCode.java/RolePermissionCatalog.java/EndpointPermissionRegistry.java/permissions.ts,
  ui-model.md vs. the 4 employee-portal screens, and es-MX/en-US i18n key parity; i18n was clean (109 keys, full parity),
  backend/frontend IAM layers were fully consistent (4 SCREEN_MARKETPLACE_* codes, 6/6 controllers registered); found and
  fixed 3 real doc-vs-implementation drifts in capability-package model documents -- openapi-source.md documented 10 operations
  under a /tenants/{tenantId}/... path never actually used by the shipped, tested routes plus 1 undocumented getPackage endpoint,
  corrected the doc rather than changing a working tested contract; permissions.md/ui-model.md documented an unimplemented
  15-code fine-grained permission model while the shipped system correctly uses the platform's coarse 4-code SCREEN_MARKETPLACE_*
  model (TD-IAM-002 pattern), documented the enforced reality instead of reimplementing out-of-scope enforcement; completed
  ui-model.md's purpose/required_permissions text, which under-described 3 of 4 screens. Debt-first action -- closed TD-BE-018
  (all 5 of 5 named custom_implementation_points now closed via the TD-BE-019 chain closed by COM-MOD-017-FE-001); TD-FE-012
  re-confirmed still open/non-blocking (no non-breaking fix exists). Backend -- mvn -Pquality "-Dhop.local-db-tests=true"
  clean verify passed 484 tests/0 failures/errors/skipped, coverage 84.65% (floor 84.65%, no regression, no backend source
  changed); checkstyle/PMD/SpotBugs/CPD/duplicate-finder reproduced the same pre-existing baseline with 0 new findings and
  0 marketplace-attributable checkstyle/SpotBugs findings; OWASP Dependency-Check 72 dependencies/0 vulnerabilities. Employee
  portal -- npm run quality passed 224 tests/65 files/0 failures, coverage 90.68% (floor 89.75%, no regression), lint 0 errors/55
  warnings unchanged from FE-001; npm audit --omit=dev 0 vulnerabilities (TD-FE-012 unchanged). Trivy fs (vuln/secret/misconfig,
  all severities) 0 findings; advanced active backlog to COM-MOD-017-CLOSEOUT)
- COM-MOD-017-FE-001 Marketplace administration and package installation UI outputs compiled (4 new employee-portal screens
  -- MarketplacePackagesScreen, MarketplaceOffersScreen, MarketplaceEntitlementsScreen, MarketplaceInstallationsScreen --
  covering BCM-PLT-011's full employee_portal.screens ui-model.md scope -- package catalog admin/publish/certify/retire, commercial
  offer publish/accept, tenant entitlement grant/revoke and package installation install/activate/suspend/uninstall/upgrade/rollback;
  new typed marketplaceApi.ts facade over PackageCatalogController/CommercialOfferController/TenantEntitlementController/PackageInstallationController;
  permissions.ts/AppShell.tsx/App.tsx wired with the 4 new ScreenKeys/PermissionCodes and MARKETPLACE_OPERATOR/TENANT_ADMIN
  roles mirroring the backend RolePermissionCatalog.java exactly; complete es-MX/en-US marketplace i18n catalogs. Closed TD-BE-019
  for real as the debt-first action -- MarketplaceInstallationsScreen's "Install package" control is genuinely gated on real
  tenant entitlement runtime state (marketplaceApi.listTenantEntitlements, active/non-expired check mirroring the backend's
  TenantEntitlement.isEffectivelyActive), not a fabricated relationship. npm run quality passed (224 tests, 65 test files,
  0 failures; employee-portal line coverage 89.75% -> 90.68%, no regression); npm audit found 17 pre-existing high-severity
  devDependency-only advisories unrelated to this item's diff, non-breaking `npm audit fix` reduced it to 10 (all requiring
  a breaking eslint-plugin-jsx-a11y downgrade out of this item's scope; production dependencies 0 vulnerabilities), registered
  as new debt TD-FE-012; Trivy fs (vuln/secret/misconfig, all severities) reported 0 findings; advanced active backlog to
  COM-MOD-017-QA-001)
- COM-MOD-017-BE-002 Marketplace entitlement enforcement and billing boundary custom rules implemented, closing 4 of TD-BE-018's
  5 named custom_implementation_points (EntitlementPolicyEvaluator now runs the full entitlement-policy.md evaluation_order
  via a policy-decision-point design that keeps marketplaceentitlements''s Spring Modulith dependency graph acyclic, verified
  by PlatformFoundationModulithTest; CompatibilityEvaluator evaluates all 9 compatibility.md dimensions via a new CompatibilityMetadata
  delimited-text parser; the billing adapter boundary gained retry/idempotency keyed on providerReference with INV-MKT-003
  preserved; packageinstallation gained a persisted multi-step InstallationStep audit trail that rollback now derives its
  target version from); the 5th point (runtime feature-availability into IAM/employee-portal menu) was investigated, found
  to require COM-MOD-017-FE-001-scale employee-portal screens that do not exist yet, and honestly repointed to new TD-BE-019
  rather than forced into a fabricated or build-breaking fix; TD-BE-018 updated to materially_reduced; found and fixed a real
  pre-existing infrastructure defect during validation -- application.properties unconditionally excluded DataSourceAutoConfiguration
  for every Spring profile including local (a regression from NXF-FMT-002''s YAML-to-properties migration), silently breaking
  every local-profile JDBC adapter and LocalDatabaseTest across the entire backend -- fixed and registered/closed as TD-BE-020;
  also closed the unrelated TD-QA-008 (stale OWASP ZAP toolchain documentation) per task instructions; 484 tests, 0 failures/errors/skipped
  against a fresh Docker Compose PostgreSQL 16 volume; backend coverage 84.65% (floor 84.53%, no regression); evidence at
  08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-002-validation.md and 08-qa/security-quality/COM-MOD-017-BE-002/security-quality-evidence.md;
  advanced active backlog to COM-MOD-017-FE-001)
- NXF-FMT-001 Frontmatter artifact migration tooling added (Nexora Framework now includes frontmatter-artifact-migration-standard.md/md
  and frontmatter_migrator.py for local Python/PyYAML deterministic conversion plus optional local Ollama narrative conversion;
  pilot compact inventory reports stored at 08-qa/format-migration/frontmatter-migration-report-projects-healthcare-operations-platform.md
  and 08-qa/format-migration/frontmatter-migration-report-nexora-framework.md. Batch 0 replaced oversized YAML migration reports
  with compact Markdown/frontmatter reports; no mass source migration, YAML archive or pointer replacement was applied yet)
- COM-MOD-017-BE-001 Product Marketplace and Entitlements backend outputs compiled (new marketplaceentitlements Spring Modulith
  module with packagecatalog, commercialoffers, tenantentitlements, packageinstallation, compatibilityevaluation and billingadapter
  capabilities; all 21 BCM-PLT-011 openapi-source.md operations functional with no endpoint responding unimplemented; new
  db/product-marketplace-and-entitlements/schema.sql with 6 tables; 4 new SCREEN_MARKETPLACE_* PermissionCode values and MARKETPLACE_OPERATOR/TENANT_ADMIN
  roles; 16 marketplace.error.* i18n keys in default/es-MX/en-US catalogs; 60 new tests (per-capability unit tests, a full-lifecycle
  API test, a real-Postgres local-database test); backend coverage raised from the 84.25% floor to a reproducible 84.53% (442
  tests, 0 failures/errors/skipped, Docker Compose PostgreSQL 16 up); found and fixed 2 real SpotBugs IMPROPER_UNICODE findings
  in this item's own new code; registered TD-BE-018 for generation-plan.md's custom_implementation_points (entitlement policy
  evaluator, compatibility evaluation strategy, billing provider adapter boundary, installation rollback orchestration, runtime
  feature-availability integration), deferred to a future COM-MOD-017-BE-002; evidence at 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-BE-001-validation.md
  and 08-qa/security-quality/COM-MOD-017-BE-001/security-quality-evidence.md; advanced active backlog to COM-MOD-017-BE-002)
- NXF-CTX-002 Mandatory open-source-first framework execution stack closed (Python, Ollama, qwen2.5-coder:0.5b, ripgrep and
  git are now required for local prompt generation and backlog orchestration; generated prompt for COM-MOD-017-BE-001 is reproducible
  and cached; active functional backlog remains COM-MOD-017-BE-001)
- NXF-CTX-001 Context efficient execution framework update (Nexora standard, prompt playbook, Python/Ollama orchestrator,
  HOP toolchain/runbook integration, compact handoffs and TD-FMT-001 migration debt created; active functional backlog remains
  COM-MOD-017-BE-001)
- COM-MOD-017-DEF Product Marketplace and Extension Packaging capability package models (BCM-PLT-011 created under capability
  packages with standard capability artifacts plus marketplace package, manifest, offer, license, entitlement, compatibility,
  installation, upgrade, security review, support and telemetry models; BCM-PLT-001/002/005/006/007/009 traceability extended
  for marketplace enablement; evidence at 08-qa/qa/product-marketplace-and-extension-packaging/COM-MOD-017-DEF-validation.md
  and 08-qa/security-quality/COM-MOD-017-DEF/security-quality-evidence.md; advanced active backlog to COM-MOD-017-BE-001)
- COM-MOD-016-CLOSEOUT Commercial Launch and Customer Enablement closeout (all 7 capability packages BCM-ORG-001, BCM-ORG-002,
  BCM-ORG-003, BCM-PLT-002, BCM-PLT-006, BCM-PLT-007, BCM-PLT-008 marked module_closed in capability-package-index.md and
  traceability.md files; TD-QA-008 kept open non-blocking; closeout evidence at 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-CLOSEOUT-validation.md
  and 08-qa/security-quality/COM-MOD-016-CLOSEOUT/security-quality-evidence.md; advanced active backlog to COM-MOD-017-DEF)
- COM-MOD-016-QA-001 Commercial readiness validation (validated capability packages, onboarding guides, governance specifications
  and commercial launch assets for completeness, traceability and consistency; fixed 4 stale-pointer/registry defects; registered
  TD-QA-008; evidence at 08-qa/qa/commercial-launch-and-customer-enablement/COM-MOD-016-QA-001-validation.md and 08-qa/security-quality/COM-MOD-016-QA-001/security-quality-evidence.md)
- COM-MOD-016-COM-001 Pricing package, sales demo and launch readiness assets (commercial packages Starter/Professional/Enterprise
  plus expansion packages, capability matrix, pricing model, upgrade/downgrade criteria, sales demo script, demo data checklist,
  one-pager, buyer personas, value proposition, launch readiness checklist, and customer acceptance/commercial handoff assets
  created under 06-delivery/commercial-product/)
- COM-MOD-016-OPS-001 Support, escalation and release governance (GOV-SPEC-001 through GOV-SPEC-010, MD and YAML specification
  pairs created under 09-operations/governance/ covering L1-L3 support model, escalation matrix, SLAs/SLOs, incident management,
  problem management/RCA, change management/CAB, release governance & readiness, rollback/hotfix governance, implementation-to-ops
  handoff, customer incident/release communication, and operational acceptance criteria OAC)
- COM-MOD-016-DOC-001 Customer onboarding and configuration guides (ONB-GUIDE-001 through ONB-GUIDE-008, MD and YAML specification
  pairs created under 09-operations/onboarding/ covering customer/tenant onboarding lifecycle, org/lab/branch/user config,
  RBAC 27 permissions, regional localization es-MX/en-US, technical prerequisites, BCM-PLT-010 data ingestion, role training/human
  validation/acceptance, and L1-L3 support SLAs)
- COM-MOD-016-DEF Commercial Launch and Customer Enablement capability package models (BCM-ORG-001, BCM-ORG-002, BCM-ORG-003,
  BCM-PLT-002, BCM-PLT-006, BCM-PLT-007, BCM-PLT-008 modeled and traced in capability-package-index.md and package registries)
- COM-MOD-013 Advanced Quality and Compliance closeout (COM-MOD-013-CLOSEOUT; all 5 capability packages BCM-QLT-002, BCM-QLT-006,
  BCM-QLT-007, BCM-PLT-007, BCM-PLT-008 marked module_closed; TD-DB-005 and TD-QA-007 closed; TD-IAM-004 open non-blocking;
  DAST 939 backend / 125 portal URLs clean; all coverage floors preserved; advanced active backlog to COM-MOD-016-DEF)
- High-Level Business Requirement
- BCM-001 Business Capability Map
- BCM-002 Capability Dependency Map
- HOP-MVP-FWK-001 Agent-Agnostic MVP Implementation Framework
- ACM-001 Actor Catalog
- HRP-001 Healthcare Reference Processes
- BRM-001 Business Rules Catalog
- MVP-MOD-001 Platform Foundation Definition Package
- MVP-MOD-001 Backlog Execution Prompt Playbook
- YAML machine-readable execution artifacts for MVP-MOD-001
- Vision-aligned MVP development readiness decision
- Ordered project folder structure
- Agent-agnostic validation baseline
- PF-BE-001 Backend project skeleton
- PF-OPS-001 Local development compose profile
- PF-BE-002 Tenant, laboratory and branch commands
- PF-BE-003 User account and role assignment baseline
- PF-BE-004 Append-only audit event recording
- PF-FE-001 Employee portal administration screens
- PF-APP-001 Mobile app foundation
- PF-QA-001 Smoke and contract tests
- MVP-MOD-001 Platform Foundation closeout
- HOP Commercial Product Backlog
- HOP Commercial Backlog Execution Prompt Playbook
- Nexora Model Driven Product Engineering Standard
- Nexora Business Capability Package Standard
- HOP Business Capability Package Index
- HOP Open Data Ingestion Contract
- HOP Product Marketplace and Entitlements Contract
- HOP Business Requirement Reference Template
- Business Requirement to YAML Prompt
- HOP Business Requirement Version Index
- Nexora Business Requirement Versioning and Impact Standard
- MVP-MOD-002 Diagnostic Catalog Business Capability Packages (BCM-SVC-001/002/003/004/005/006/007/009)
- MVP-MOD-002-DEF capability package model validation evidence
- MVP-MOD-002-BE-001 catalog-test-configuration backend compilation (catalog, tests, panels, analytes, preparations, reference
  ranges, samples, price lists)
- MVP-MOD-002-BE-002 catalog custom business rules (publication, immutable versioning/snapshots, preparation assignment, effective-dated
  overlap prevention and effective-context resolution)
- Nexora Open Source First Security Quality Standard
- MVP-MOD-002-FE-001 employee catalog UI outputs and quality gates
- MVP-MOD-002-QA-001 integrated Diagnostic Catalog validation, dependency remediation and security quality evidence
- HOP client stack market validation baseline and stack quality toolchain baseline
- MVP-MOD-002 Diagnostic Catalog closeout (MVP-MOD-002-CLOSEOUT)
- MVP-MOD-002 official-source stack market refresh and quality toolchain gap disposition
- PostgreSQL JDBC 42.7.12 security patch applied and revalidated
- HOP framework feedback index and framework improvement proposal loop initialized
- HOP integrated local solution runbook
- MVP-MOD-003 People and Clinical Master Data Business Capability Packages (BCM-PER-001/002/003, BCM-ATT-002)
- MVP-MOD-003-DEF capability package model validation evidence
- MVP-MOD-003-BE-001 people-and-clinical-master-data backend compilation (person, patient, doctor and patient-registration
  generatable outputs plus explicit deferred custom-rule hooks)
- MVP-MOD-003-BE-002 duplicate detection, natural-key/document-uniqueness matching, tenant-configurable scoring, merge coordination
  and portal identity custom rules for BCM-PER-001/002/003 and BCM-ATT-002
- MVP-MOD-003-FE-001 employee portal UI outputs for people search/duplicate resolution, patient registration/snapshot/merge/representative/consent
  lifecycle and doctor directory/credential/ suspension/portal-access lifecycle
- MVP-MOD-003-QA-001 integrated validation of capability models, traceability, OpenAPI contracts, BE-002 custom rules, FE-001
  UI, QA/security evidence, the local runbook, backlog pointers and executable backend/frontend/security quality gates
- MVP-MOD-003 People and Clinical Master Data closeout (MVP-MOD-003-CLOSEOUT)
- MVP-MOD-004 Front Desk and Care Delivery Business Capability Packages (BCM-ATT-001/003/004/006, BCM-LAB-001)
- MVP-MOD-004-DEF capability package model validation evidence
- MVP-MOD-004-BE-001 front-desk-care-delivery backend compilation (diagnostic order, appointment, reception, admission and
  quotation generatable outputs, all endpoints functional with no endpoint responding 501; explicit BE-002 refinement hooks
  documented in code and QA evidence)
- MVP-MOD-004-BE-002 front-desk-care-delivery custom rules (referring-doctor eligibility gating, per-line multi-price-list
  resolution for orders and quotations, tiered order-cancellation override, tenant-configurable appointment branch capacity
  and no-show grace period, preparation-instruction surfacing, reception queue prioritization, tenant-configurable admission
  acknowledgement policy and quotation discount policy)
- HOP-QA-ALIGN-004 all-severity vulnerability, DAST and runtime security evidence (backend and frontend dependency scans,
  Trivy filesystem/secret/misconfiguration scan, OWASP ZAP baseline and API scans against the running local stack; 2 dependency
  CVEs and 2 unhandled-500 defects fixed; TD-QA-001 and TD-QA-002 closed; residual findings dispositioned as TD-FE-005/TD-QA-004)
- HOP-QA-ALIGN-005 message externalization and magic-string remediation baseline (full backend, employee-portal and mobile-app
  inventory, P0/P1/P2-classified; backend's 30 runtime-reachable domain error codes now named Java constants; frontend's repeated
  validation strings and duplicated confidence thresholds centralized; mobile's repeated validation strings centralized; TD-I18N-001
  closed, remaining scope tracked as TD-I18N-002)
- HOP-QA-ALIGN-CLOSEOUT enterprise quality alignment closeout (all six preceding alignment items validated as closed or closed
  with correctly dispositioned residual P1 debt; P0 minimum baseline satisfied; technical-debt index 25 entries with 0 blocking;
  two stale TD-QA-001/ TD-QA-002 status fields corrected to closed; seven P1 residual-debt items given explicit owner/target_backlog/priority;
  coverage preserved with no regression. Does not mark HOP commercially complete or GA-ready.)
- MVP-MOD-004-FE-001 front desk worklist and diagnostic order employee-portal UI (Reception Management worklist with walk-in/scheduled
  intake, identity confirmation, priority and advance-to-admission; Diagnostic Order Management creation/pricing/accept/cancel/complete
  with immutable patient/doctor/branch/pricing snapshots and the RN-005 tiered cancellation override; debt-first TD-FE-004
  materially reduced, coverage 73.04% -> 76.51%; TD-FE-006 registered for the deferred Appointment Scheduling/Admission Management/Quotation
  Management screens; a vanishing-success-banner UX defect found by testing and fixed before closure)
- MVP-MOD-004-QA-001 order lifecycle and snapshot validation evidence (diagnostic order patient snapshot immutability after
  source patient profile changes; backend local database tests ran with Docker Compose PostgreSQL and 0 skipped local tests;
  debt-first TD-BE-003 materially reduced, backend coverage 66.48% -> 66.52%)
- MVP-MOD-004 Front Desk and Care Delivery closeout (MVP-MOD-004-CLOSEOUT; all module backlog items closed, backend quality
  profile passed, Docker/PostgreSQL local tests passed with 0 skipped, frontend quality profile passed, backend/frontend dependency
  scans reported 0 vulnerabilities; not marked commercially complete or GA-ready)
- MVP-MOD-005 Cashier and Billing Request Business Capability Packages (BCM-ATT-005/008)
- MVP-MOD-005-DEF capability package model validation evidence
- MVP-MOD-005-BE-001 cashier and billing request backend compilation (cash sessions, sales, sale lines, payment allocations,
  billing requests, tax lines, local PostgreSQL schema and backend quality evidence; backend coverage 66.52% -> 66.58%; TD-DEF-001
  closed; TD-BE-011 registered for public front-desk source ports)
- MVP-MOD-005-BE-002 billing request adapter custom boundary (provider-agnostic fiscal adapter port, local deterministic adapter,
  submit/retry/cancel state transitions, idempotency keys, adapter exception snapshots, FrontDeskCareDelivery sale-source
  named interface, TD-BE-011 closed and backend coverage 66.58% -> 76.39%)
- MVP-MOD-005-FE-001 cashier and billing request employee-portal UI compilation (Cash Sessions, Sales and Billing Requests
  screens covering session open/close with variance, sale creation from an accepted order or quotation, payment allocation,
  sale cancellation, billing request creation from a paid sale, tax lines and submit/retry/cancel against the fiscal adapter
  boundary; a pre-existing uncommitted draft's Money-typed fields and status literals were corrected against the real backend
  contract before reuse; TD-FE-004 closed, frontend coverage 76.51% -> 80.57%, reaching the 80% final-closure target)
- MVP-MOD-005-QA-001 financial audit and reconciliation evidence (integrated validation of cash session open/close and variance
  handling, sale creation from an accepted order or quotation, payment allocation and its outstanding-balance guard, sale
  cancellation, billing request creation from a paid sale, tax lines, adapter submit/retry/cancel including simulated failure/retry,
  audit-event traceability confirmed via a live /api/audit/events query, and Spring Modulith-verified module-boundary purity;
  OWASP ZAP API scan against the backend OpenAPI surface deferred by MVP-MOD-005-BE-002 executed with 0 FAIL/0 WARN/118 PASS;
  full local stack started, validated and stopped using only runbook-documented commands; TD-BE-001 closed. Its originally
  reported backend coverage of 68.66% was later found, during MVP-MOD-005-CLOSEOUT, to be inflated by a non-clean multi-run
  jacoco.exec accumulation; the corrected, reproducible clean-rebuild figure is 76.39% (unchanged from MVP-MOD-005-BE-002).
  Frontend coverage 80.57% -> 82.69%, no regression on either stack.)
- MVP-MOD-005-CLOSEOUT module closeout and registry update (all 6 module backlog items confirmed closed and traceable; a registry-consistency
  sweep found and corrected the MVP-MOD-005-QA-001 coverage measurement bug above plus a stale technical-debt-index.md frontend
  baseline; backend quality profile, dependency-check, integrated Trivy scan and the full frontend quality suite re-executed
  clean with 0 regressions; capability package index and BCM-ATT-005/BCM-ATT-008 traceability moved to module_closed; HOP
  explicitly documented as not commercially complete or GA-ready)
- MVP-MOD-006-DEF Laboratory Workflow capability package models (BCM-LAB-002 Sample Collection, BCM-LAB-003 Sample Labeling,
  BCM-LAB-005 Sample Reception, BCM-LAB-006 Laboratory Processing, BCM-LAB-008 Technical Validation, BCM-LAB-009 Medical Validation,
  BCM-LAB-010 Result Release; Sample (AGG-008) owned by BCM-LAB-002 with delegated field-level mutation authority for BCM-LAB-003/005;
  LaboratoryResult (AGG-009) owned by BCM-LAB-006 with delegated field-level mutation authority for BCM-LAB-008/009/010; no
  code implemented, definition only; TD-BE-010's modeling precondition satisfied, code fix remains pending MVP-MOD-006-BE-002)
- MVP-MOD-006 Laboratory Workflow fully implemented and closed (MVP-MOD-006-BE-001 through MVP-MOD-006-CLOSEOUT; backend coverage
  76.39%, frontend coverage 82.69%)
- MVP-MOD-007-DEF Results and Digital Delivery capability package models (BCM-RES-001 Result Management, BCM-RES-002 PDF Report
  Generation, BCM-RES-004 Digital Delivery, BCM-RES-005 Result History, BCM-RES-006 Critical Results, BCM-RES-007 Result Notifications,
  BCM-PLT-003 Notification Management, BCM-PLT-008 Document Management; LaboratoryResult/AGG-009 read-only end to end, no
  ownership duplication; GeneratedResultReport, ResultDeliveryTicket, CriticalResultEscalation, ResultNotificationRequest,
  NotificationRequest and StoredDocument modeled as new, non-duplicating entities; NotificationProviderPort and DocumentStoragePort
  modeled as provider-agnostic adapter boundaries mirroring FiscalAdapterPort from MVP-MOD-005; no code implemented, definition
  only)
- MVP-MOD-007-BE-002 Implement digital delivery, notification and critical result custom rules (BCM-RES-004/006/007 custom
  rules; TD-BE-012 closed for document-management/results-delivery scope; backend coverage 76.77% -> 76.93%)
- MVP-MOD-007-FE-001 Employee portal UI for Results and Digital Delivery (Result Search/Detail, Result Reports, Critical Escalations,
  Result Notifications; closed a real backend contract gap left by BE-001/BE-002 by adding the missing search/report-generation/notification-history
  adapters end to end; frontend coverage 82.69% -> 83.98%, backend coverage 76.93% -> 76.99%; TD-FE-007 registered for a pre-existing,
  out-of-scope LaboratoryResult wire-shape mismatch)
- HOP-ENT-FOUND-001 Enterprise Product Foundation Alignment closed and corrected (localization es-MX/en-US baseline for backend/frontend/mobile
  with a working employee-portal language switch; a 27-permission IAM catalog with request-time backend authorization enforcement,
  API/action permission mapping and permission-filtered dynamic navigation in the employee portal; session-management headers
  for web/mobile; database architecture/data-dictionary/ normalization-report/seed-data-catalog deliverables plus country/locale/currency
  and minimal diagnostic catalog seed data; UX/UI foundation with real CSS design tokens; persistence/ contract-first generation
  review; TD-BE-009, TD-IAM-001 and TD-APP-002 closed, TD-I18N-002 and TD-IAM-002 materially reduced; backend coverage 76.99%
  -> 77.92%, frontend coverage 83.98% -> 84.44%, mobile coverage measured at 97.15%, all with no regression; found and fixed
  a real stale-duplicate Docker-init schema.sql bug during validation (TD-STACK-004 registered))
- MVP-MOD-007-PORTAL-001 Compile patient and doctor released result views (permission-filtered, authorized-only released-result
  access; TD-STACK-004 closed)
- MVP-MOD-007-APP-001 Compile mobile result view and notification baseline (mobile coverage 97.15% -> 98.87%)
- MVP-MOD-007-QA-001 Result access, PDF and notification evidence (TD-DB-001 and TD-QA-004 closed; backend coverage 77.92%
  -> 78.42%; 210 backend tests, 0 failures/errors)
- MVP-MOD-007-CLOSEOUT Module closeout and registry update (closed TD-BE-010 -- diagnostic order cancellation now checks real
  Sample state via the SampleReadPort cross-module read port instead of order status alone; a real employee-portal coverage
  regression from 84.44% to 84.03% left uncaught by MVP-MOD-007-PORTAL-001 was found and fixed, reaching 85.50%; patient-portal
  and doctor-portal line coverage measured for the first time at 41.93% and 40.62%, TD-FE-008/TD-FE-009 registered; backend
  coverage 78.42% -> 78.51%, mobile re-confirmed at 98.87%, no regressions; MVP-MOD-007 acceptance summary re-validated --
  PDF report generation, authorized-only released-result access and traceable critical-result notifications)
- MVP-MOD-008-DEF Integration and Migration Readiness capability package models (BCM-PLT-004 Integration Management, BCM-PLT-005
  API Management, BCM-PLT-010 Open Data Ingestion and Migration; BCM-PLT-010 correctly reuses AGG-016 MigrationJob and the
  pre-existing HOP Open Data Ingestion Standard/Contract; BCM-PLT-004's IntegrationEndpoint and BCM-PLT-005's ApiSurfaceRegistration
  are new, non-duplicating aggregates in the integration-interoperability bounded context; no code implemented, definition
  only; TD-STACK-003 and TD-I18N-002 materially reduced via modeling-stage decisions)
- MVP-MOD-008-BE-001 Integration adapter contracts and API governance backend compilation (two new Spring Modulith modules,
  integrationinteroperability and datamigrationportability; every BCM-PLT-004/BCM-PLT-005/BCM-PLT-010 openapi-source.md operation
  functional with no endpoint responding unimplemented; real manifest/checksum verification and CSV/JSON/NDJSON/ZIP parsing
  for migration ingestion; first-class structured `code` error field implemented for the first time in HOP's backend; TD-STACK-003
  and TD-I18N-002 further reduced with real implementation, TD-BE-013 registered for deferred XLSX parsing; backend coverage
  78.51% -> 80.08%, reaching the stack's 80% final-closure target; 239 tests, 0 failures/errors)
- MVP-MOD-008-BE-002 Integration retry/dead-letter, API deprecation/rate-limit and migration checkpoint custom rules (closed
  TD-BE-013 as the debt-first action, adding real Apache POI XLSX row parsing; implemented CUS-INT-004-04/05 bounded exponential-backoff
  retry with a dead-letter transition and a deterministic correlation id propagated across every retry; implemented BCM-PLT-005's
  RN-003 deprecation-window-elapsed retirement transition and RN-004 rate-limit enforcement via a new PartnerApiKeyRateLimitInterceptor/PartnerApiRateLimiter
  fixed-window counter, and closed a real RN-005 audit gap in setRateLimitPolicy; implemented CUS-MIG-010-04/05/06 with a
  new MigrationDomainCommandPort the commit/retry flow uses as its sole interaction point (INV-MIG-003 preserved by construction),
  real checkpointed idempotent resume that skips already-completed entity categories, and incremental post_import reconciliation
  reports; added a first-class `messageKey` field alongside `code` on every BCM-PLT-004/005/010 error response, further reducing
  TD-I18N-002; registered TD-BE-014 (migration domain-command port has no real cross-module wiring yet) and TD-BE-015 (rate-limit
  enforcement scoped to partner-API-key-bearing requests only); backend coverage 80.08% -> 80.49%; 265 tests, 0 failures/errors/skipped)
- MVP-MOD-008-FE-001 integration and migration employee-portal administration UI (typed integrationMigrationApi facade for
  BCM-PLT-004/005/010, dynamic permission-filtered screens for integration endpoints/messages, API governance/partner keys/rate
  limits and migration jobs/import packages/dry-run/approval/commit/reconciliation; multipart FormData handling fixed; visible
  labels externalized in es-MX/en-US; npm run quality passed with 101 tests and employee-portal coverage 85.50% -> 86.47%;
  npm audit and Trivy reported 0 vulnerabilities; TD-STACK-003 and TD-I18N-002 further reduced; TD-FE-010 registered)
- MVP-MOD-008-QA-001 integration and migration QA/security evidence (backend verify passed with 265 tests and 80.49% coverage;
  employee-portal quality passed with 101 tests and 86.47% coverage; Trivy and npm audit reported 0 vulnerabilities; YAML
  parse and git diff checks passed; evidence files created under 08-qa)
- COM-MOD-009-PORTAL-001 patient portal commercial workflow fully implemented (login, profile, appointments, orders, results,
  notifications, i18n switcher, error states, and secure patient self-access interceptor) with unit test coverage raised from
  41.93% to 89.58% (TD-FE-008 closed)
- COM-MOD-009-PORTAL-002 doctor portal commercial workflow fully rebuilt (login, permission-filtered dynamic navigation, referred-patients/results/orders/notifications
  views, i18n switcher, loading/ empty/error/no-permission/session-expired states) replacing a stale employee-portal-domain
  scaffold; added backend least-privilege enforcement (doctorId-filtered diagnostic orders, a new ReferringDoctorAuthorizationPort
  Spring Modulith named interface, 3 new interceptor self-access blocks) with unit test coverage raised from 40.62% to 89.86%
  (TD-FE-009 closed); backend coverage 80.49% -> 80.60% (280 tests, 0 failures/errors/skipped); TD-IAM-002 and TD-I18N-002
  materially reduced further; TD-FE-011 registered (pre-existing, unrelated patient-portal lint regression)
- COM-MOD-009-APP-001 patient mobile workflow compiled (PATIENT role and granular patient mobile permissions, profile/appointments/orders/results/notifications
  routes, localized es-MX/en-US home and workflow labels, permission-filtered mobile actions, patientMobileApi facade and
  patientMobileWorkflowModel with loading/ready/empty/forbidden/error states); mobile coverage raised from 98.87% to 99.21%;
  npm quality and npm audit passed with 0 vulnerabilities; TD-I18N-002 and TD-IAM-002 materially reduced further
- COM-MOD-010-BE-001 Inventory Product/Reagent/Lot/Stock backend outputs compiled (nine BCM-INV capability packages implemented
  as a single inventoryquality Spring Modulith module with 27 REST operations across nine hexagonal sub-packages, JDBC + in-memory
  dual adapters, first-class code+messageKey error envelope, seven new SCREEN_INVENTORY_* PermissionCode values registered
  in EndpointPermissionRegistry, 38 new inventory.error.<code> keys in the es-MX/en-US catalogs further materially reducing
  TD-I18N-002; one new schema file db/inventory-and-internal-quality/ schema.sql; 308 tests passed with 0 failures/errors/skipped;
  backend line coverage 80.60% -> 82.94% with no regression; Trivy fs reported 0 findings across all severities. BCM-QLT-001/003/004/005
  compiled in COM-MOD-010-BE-002)
- COM-MOD-010-BE-002 Inventory and Internal Quality equipment/calibration/maintenance/internal-QC backend outputs compiled
  (four BCM-QLT capability packages added to the existing inventoryquality Spring Modulith module for equipment, calibration,
  maintenance and internal quality-control REST endpoints/services/domain records/adapters; four new inventory_quality schema
  tables; four new SCREEN_* PermissionCode values; 16 localized inventory.error.<code> keys; 312 backend tests, 0 failures/errors,
  backend line coverage 82.94%; OWASP Dependency-Check and Trivy 0 findings)
- COM-MOD-010-FE-001 Inventory and Internal Quality employee-portal UI compiled (11 permission-filtered screens covering all
  13 COM-MOD-010 capability packages -- inventory catalog, reagent profiles, stock lots, purchase orders, combined stock entries/exits/consumption
  movements, adjustments, waste disposal, internal quality control runs, calibrations, equipment profile/availability and
  maintenance events; typed inventoryQualityApi facade over all 27 backend REST operations; full es-MX/en-US externalization;
  TD-FE-010 materially reduced via a new shared DataTable component and small-sub-component decomposition convention applied
  to all 11 new screens with 0 new lint size/complexity warnings; npm run quality passed with 124 tests, 48 test files, 0
  failures, employee-portal coverage 86.47% -> 87.87%; npm audit and Trivy fs (vuln/secret/misconfig, all severities) reported
  0 findings)
- COM-MOD-010-QA-001 integrated traceability, stock and quality evidence (validated end-to-end traceability across all 13
  COM-MOD-010 capability packages -- openapi-source.md vs. controllers, permissions.md vs. EndpointPermissionRegistry/RolePermissionCatalog,
  ui-model.md vs. employee-portal screens, es-MX/en-US i18n key parity; fixed a stale backlog_items.custom_rules traceability
  pointer across the 9 BCM-INV-001..009 traceability.md files (was COM-MOD-010-BE-002/pending, corrected to COM-MOD-010-BE-001/closed)
  and a stale capability-package-index.md COM-MOD-010 roadmap-group pointer; found and fixed a real backend coverage gap --
  a clean rebuild reproducibly measured 81.90% (0 backend source changes since BE-002) because BE-002's 4 new JDBC adapters
  had no local-database integration test; added InventoryQualityControlsLocalDatabaseTest.java, raising corrected backend
  coverage to 83.73% (315 tests, 0 failures/errors/skipped); employee-portal coverage confirmed at 88.24% (124 tests/48 files,
  0 failures); OWASP Dependency-Check (65 dependencies), npm audit and Trivy fs (all severities) reported 0 vulnerabilities/secrets/misconfigurations;
  YAML parse (1105 files) and agent-agnostic scan passed, git diff --check clean)
- COM-MOD-010-CLOSEOUT module closeout and registry update (confirmed all 13 COM-MOD-010 capability packages -- BCM-INV-001..009,
  BCM-QLT-001/003/004/005 -- module_closed in capability-package-index.md and their traceability.md files; reviewed technical-debt-index.md
  and found zero open or materially-reduced debt attributable to COM-MOD-010; documentation/registry-only closeout, no source
  code touched, so full backend/ frontend/mobile quality suites were not re-executed and coverage figures (backend 83.73%,
  employee portal 88.24%, mobile 99.21%, patient portal 94.11%, doctor portal 96.28%) are re-affirmed unchanged from COM-MOD-010-QA-001/COM-MOD-009
  evidence; executed YAML parse, stale-pointer sweep and git diff --check for this closeout; advanced the active commercial
  backlog item to COM-MOD-011-DEF)
- COM-MOD-011-DEF Public Website and Digital Growth capability package models (all 7 module capabilities -- BCM-SVC-001/002/003/005,
  BCM-ATT-001/006, BCM-PLT-005 -- confirmed reused from already-modeled/compiled capability packages owned by MVP-MOD-002,
  MVP-MOD-004 and MVP-MOD-008; zero new capability package, aggregate or schema created; each package's product_surfaces,
  openapi-source.md, ui-model.md, permissions.md and traceability.md extended with a public_website surface realizing pre-existing
  future_surfaces/deferred_to placeholders; catalog capabilities gained an anonymous public_read scope and a published-only
  list projection reusing existing Published*Snapshot schemas; BCM-ATT-001/006 gained RN-008/ RN-009 restricting anonymous
  public requests to a requested/draft-state record from a reused ProspectiveContact, never a confirmed/issued one; BCM-PLT-005
  gained RN-007 and a RateLimitPolicy.consumerIdentificationMethod field materially reducing TD-BE-015; found and corrected
  three pre-existing stale roadmap/status pointers unrelated to this item's own scope in BCM-ATT-001, BCM-ATT-006 and BCM-PLT-005;
  definition-only, no code implemented, coverage unchanged; advanced the active commercial backlog item to COM-MOD-011-BE-001)
- COM-MOD-011-BE-001 Public Website backend outputs compiled (all 10 public REST operations modeled by COM-MOD-011-DEF --
  GET /api/public/catalog/{diagnostic-services,tests,panels, preparations}/published, GET /api/public/catalog/{diagnostic-services,tests,panels,
  preparations}/{id}/published-snapshot, POST /api/public/care-delivery/appointment-requests (RN-008) and POST /api/public/care-delivery/quotation-requests
  (RN-009) -- functional and anonymous; a new publicweb Spring Modulith module hosts them, depending only on the new catalogtestconfiguration::catalog-public-read-port
  and frontdeskcaredelivery::public-intake-port named interfaces; BCM-PLT-005 RN-007 rate-limit enforcement compiled as a
  new PublicApiRateLimitInterceptor co-located with the partner interceptor, driven by the new RateLimitPolicy.consumerIdentificationMethod
  field persisted end-to-end with an ADD-COLUMN-IF-NOT-EXISTS schema migration; RN-008 extends AppointmentSlot inline with
  three nullable prospective_contact fields and relaxes patient_id nullability for the public_website channel only, additive
  DDL; every response DTO strips tenantId/audit/patient- linkage fields so the anonymous public surface never leaks operational
  identifiers; new public.error.* and public.rate_limit.* i18n key namespaces added in es-MX and en-US (TD-I18N-002 further
  reduced); closes TD-BE-015 with a real end-to-end test (PublicWebApiTest.publicRateLimitBlocksAnonymousTrafficByIpAddress);
  found and fixed a pre-existing openapi-source.md vs Spring route gap in BCM-SVC-005 (getPublishedPreparationSnapshot had
  no matching PreparationInstructionController route); backend coverage 83.73% -> 83.96% with 324 tests and 0 failures/errors/skipped
  (Docker Compose PostgreSQL 16 up); OWASP Dependency-Check 108 deps 0 vulns; Trivy fs vuln/secret/ misconfig 0 findings across
  all severities)
- COM-MOD-011-WEB-001 Public website frontend compiled (new 07-implementation/public-website/ module, React 19 + TypeScript
  5 strict + Vite 6, following patient-portal/doctor-portal conventions -- hand-rolled LocaleContext, plain-fetch httpClient,
  no state-management library; a new hand-rolled History-API router instead of a router dependency); consumes the anonymous
  /api/public/** surface from COM-MOD-011-BE-001 -- published catalog discovery for diagnostic services/tests/panels/preparations
  (BCM-SVC-001/002/003/005, each with a list and detail page sharing CatalogListView/CatalogDetailView chrome) and public
  appointment/quotation request intake (BCM-ATT-001 RN-008, BCM-ATT-006 RN-009) with an explicit client-side cooldown for
  BCM-PLT-005 RN-007's 429 responses (backend sends no Retry-After header); deployment identity (tenantId/laboratoryId/branch
  list) is deployment-owned site configuration since COM-MOD-011-DEF modeled no public branch-directory capability; added
  SEO (per-page metadata, robots.txt, sitemap.xml), accessibility (eslint-plugin-jsx-a11y + automated jest-axe regression
  check wired into npm run test/quality) and privacy (a /privacy notice page, required consent checkboxes) foundations, materially
  reducing TD-UX-002 as the reference pattern for a new module (employee-portal itself untouched, so not closed); first coverage
  baseline for this stack -- 97 tests, 34 files, 0 failures, 98.61% line/statement coverage; ESLint 0 errors/16 non-blocking
  warnings; jscpd 3.9% duplication; npm audit and Trivy fs (all severities) 0 findings; verified locally via npm run build
  && vite preview. Docker later became reachable in the same session, enabling full live end-to-end verification against a
  real backend and Postgres instance for all 10 /api/public/** operations; this surfaced and fixed a real pre-existing defect
  in backend/src/main/resources/db/catalog-test-configuration/schema.sql, whose seed rows used status='PUBLISHED' (uppercase)
  against the lowercase published domain constant, silently hiding every seeded catalog row from published-only views project-wide.
  Corrected the 10 seed literals to lowercase; no Java source changed. Backend regression gates re-run clean -- 324 tests/0
  failures, coverage unchanged at 83.96%, checkstyle/pmd/spotbugs/duplicate-finder 0 new violations, OWASP Dependency-Check
  65 deps/0 vulnerabilities, Trivy fs 0 findings)
- COM-MOD-011-FE-001 Content and request administration screens in employee portal compiled (PublicContentReviewScreen, PublicAppointmentRequestsScreen,
  PublicQuotationRequestsScreen; fixed QuotationRequest.channel defect; backend coverage 83.99% with 327 tests; employee portal
  coverage 88.68% with 154 tests; TD-UX-002 closed; Trivy CVE-2026-59889 fixed)
- COM-MOD-011-QA-001 integrated quality, privacy, SEO, accessibility, and security validation clean across all 7 capability
  packages (0 vulnerabilities, 0 security findings, 0 test failures, 0 coverage regressions across all 6 stacks)
- COM-MOD-011-CLOSEOUT module closeout and registry update (confirmed all 7 COM-MOD-011 capability packages -- BCM-SVC-001/002/003/005,
  BCM-ATT-001/006, BCM-PLT-005 -- module_closed in capability-package-index.md and their traceability.md files; verified TD-BE-015
  and TD-UX-002 closed with zero open or blocking technical debt attributable to COM-MOD-011; documentation/registry-only
  closeout, no source code touched, coverage figures -- backend 83.99%, employee portal 88.68%, public website 98.61%, mobile
  99.21%, patient portal 94.11%, doctor portal 96.28% -- re-affirmed unchanged; YAML parse, stale-pointer sweep and git diff
  --check clean; advanced active commercial backlog item to COM-MOD-012-DEF)
- COM-MOD-012-DEF Platform Hardening and SaaS Operations capability package models (BCM-ORG-001 Tenant Management, BCM-PLT-001
  IAM, BCM-PLT-002 Platform Configuration & Feature Flags, BCM-PLT-005 API Management, BCM-PLT-006 Observability, BCM-PLT-007
  Audit Trail, BCM-PLT-008 Document Management, BCM-PLT-009 Workflow Engine; 5 new capability packages created, 3 existing
  extended; standard 14 model artifacts each; TD-DB-004, TD-BE-008, TD-FE-005 and TD-IAM-002 architectural parameters incorporated
  into source models; definition only, no code implemented)
- COM-MOD-012-OPS-001 Production deployment and environment strategy (local/dev/qa/staging/prod environment path, deployment
  units, configuration and secret policy, tenant onboarding, release promotion, rollback and deployment readiness checklist;
  TD-STACK-001 materially reduced; definition only, no code implemented)
- COM-MOD-012-OPS-002 Observability, backup, restore and incident runbooks (10 executable runbook pairs under 09-operations/runbooks/
  -- observability, health/readiness/liveness, metrics/logs/traces validation, backup, restore, incident response, rollback
  incident handoff, tenant-impact triage, evidence collection, post-incident review -- each with purpose, prerequisites, applicable
  environment, executable commands cross-checked against compose.local.json/.env.example/application.properties/AuditComplianceController,
  success/failure criteria, expected evidence, responsible role, capability traceability and IAM/audit expectations; capability
  traceability updated for all 8 COM-MOD-012 capabilities; TD-DB-004 materially reduced via tenant-impact-triage-runbook.md's
  mandatory cross-tenant leakage check as an operational compensating control pending native Row Level Security; definition
  only, no code implemented, coverage unchanged)
- COM-MOD-012-BE-001 Compile tenant operations, feature flags and operational controls (BCM-ORG-001 provisionTenant extended
  in place with code/legalName/tradeName/taxId/tier/isolationStrategy, listTenants and updateTenantStatus added, both privileged
  and audited; new BCM-PLT-002 platformconfiguration Spring Modulith module -- getPlatformConfig, evaluateFeatureFlags, updateFeatureFlag,
  all validated per business-model.md invariants and audited; BCM-PLT-006 observability extensions -- micrometer-registry-prometheus
  + GET /actuator/prometheus, explicit liveness/readiness health groups, new RequestObservabilityContextFilter populating
  tenantId/userId/traceId MDC on every log line; closed 5 of 8 named COM-MOD-012-OPS-002 runbook known_gaps_and_forward_pointers
  entries, remaining 3 -- distributed trace export, provisioned observability stack, SLO/SLA alerting -- re-pointed to future
  items pending infrastructure; BCM-PLT-001/005/007/008/009 extensions deliberately deferred and registered as TD-BE-016/TD-BE-017/TD-IAM-003;
  real SpotBugs/FindSecBugs SERVLET_HEADER finding on the new MDC filter fixed in code, not suppressed; 362 tests/0 failures
  (up from 360), backend coverage 83.99% -> 84.11%; TD-IAM-002 and TD-DB-004 materially reduced further, TD-I18N-002 further
  reduced; checkstyle/pmd/spotbugs/duplicate-finder/cyclonedx clean vs baseline, OWASP Dependency-Check 115 deps/0 vulnerabilities,
  Trivy fs 0 findings, YAML parse 1,248 files/0 errors, agent-agnostic scan 0 real hits, git diff --check clean; advanced
  active commercial backlog item to COM-MOD-012-QA-001)
- COM-MOD-012-QA-001 Performance, resilience and security evidence (validated all 8 COM-MOD-012 capabilities end to end against
  a running local backend -- tenant provisioning/listing/status transition, platform config, feature flags, Prometheus, health
  groups, MDC logging, audit events; light local load check (30 sequential + 20 concurrent tenant requests, 0 failures/ races);
  found and fixed a real resilience defect -- the readiness probe did not reflect database connectivity because management.endpoint.health.group.readiness.include
  was unset, fixed by scoping the include to application-local.properties, re-verified live via a real docker stop/start of
  hop-local-postgres (readiness correctly DOWN/503 then UP, liveness stays UP throughout); executed a dedicated OWASP ZAP
  DAST pass against the full backend API surface (deferred by BE-001) that found and this item fixed 2 real defects -- TD-QA-005
  (a cross-cutting null-byte/oversized-value unhandled 500 across laboratoryworkflow and cashsales, fixed via a narrow SQLState-class-22
  GlobalExceptionHandler mapping) and TD-QA-006 (AuthController.initiateAssistance returned 500 instead of 404 for a nonexistent
  assistedUserId, fixed by widening IdentityAccessExceptionHandler's assignableTypes); a ZAP baseline scan against the unchanged
  employee portal found 0 FAIL-NEW; executed a real backup (pg_dump, SHA-256 checksum, pg_restore --list showing 415 TOC entries)
  and restore rehearsal (isolated database, matching row counts 40=40); confirmed the 3 remaining COM-MOD-012-BE-001 infrastructure
  forward pointers (distributed trace export, provisioned Grafana/Prometheus/Loki, SLO/SLA alerting) still genuinely require
  infrastructure not available locally and registered them as TD-OBS-001 rather than closing them; 367 tests/0 failures (up
  from 362), backend coverage 84.11% -> 84.14%; checkstyle/pmd/spotbugs/ duplicate-finder/cyclonedx clean vs baseline, OWASP
  Dependency-Check 115 deps/0 vulnerabilities, Trivy fs 0 findings (backend and repo-wide), YAML parse 1,256 files/0 errors,
  agent-agnostic scan 0 real hits, git diff --check clean; advanced active commercial backlog item to COM-MOD-012-CLOSEOUT)
- ? COM-MOD-013-FE-001 is closed. Advanced Quality and Compliance employee portal UI compiled (ExternalQualityControlsScreen,
    CapaManagementScreen, AuditManagementScreen, ComplianceEvidenceScreen, QualityEventIntakeScreen; thin typed externalQualityComplianceApi
    facade; IAM permissions, SCREEN_TO_PERMISSION, QUALITY_MANAGER role; complete es-MX and en-US i18n catalogs; TD-I18N-002
    debt-first AuditEventsScreen retrofit). Validation gates executed clean
  : npm run typecheck, npm run test:coverage (187 tests, 60 files, 0 failures, employee-portal line coverage 88.68% -> 89.74%),
    npm run build, npm run duplication, npm run format:check, npm run license:check, npm run audit:all (0 vulnerabilities),
    and Trivy fs vuln/secret/misconfig all severities (0 findings). npm run lint passed with 0 errors and 51 non-blocking
    warnings; warnings are dispositioned as residual TD-FE-010/TD-I18N-002 follow-up, not closure blockers. Ready for COM-MOD-013-QA-001.
- COM-MOD-013-QA-001 is closed. Integrated validation found and closed a major persistence-wiring defect (TD-DB-005) -- application-local.properties
  never registered db/external-quality-and-compliance/schema.sql, compounded by an inverted @Profile on the 4 externalqualitycompliance
  JDBC/in-memory repository pairs (real JDBC classes were @Profile("!local & !test") instead of the codebase's @Profile("local")
  convention), so the module silently persisted External Quality Control, CAPA, Audit Management and Quality Event Intake
  data in memory only, never to PostgreSQL. Fixed both root causes; re-ran the pre-existing ExternalQualityComplianceLocalDatabaseTest
  live against real PostgreSQL (passed). Backend coverage rose from a clean-rebuild 82.57% to 84.24% at that point (381 tests,
  0 failures/errors/skipped), above the previously recorded 84.14% floor. Also fixed 2 SpotBugs High findings (DM_DEFAULT_ENCODING,
  NM_SAME_SIMPLE_NAME_AS_SUPERCLASS), 5 Medium CT_CONSTRUCTOR_THROW findings, 1 hardcoded i18n string and 1 TD-FE-010 function-size
  violation in ComplianceEvidenceScreen.tsx (employee-portal coverage 89.74% -> 89.75%, 187 tests, 60 files, lint warnings
  51 -> 50). A required follow-up real OWASP ZAP DAST pass against the running backend (939 URLs) and employee-portal (125
  URLs) found and fixed one further backend defect -- TD-QA-007, an unhandled 500 on a malformed/abruptly-truncated multipart
  upload to POST /api/documents, remapped to 400 -- confirmed by a clean re-scan (0 FAIL-NEW/0 WARN-NEW), raising backend
  coverage to 84.25% (382 tests). The portal baseline scan found 0 FAIL-NEW and 6 WARN-NEW, all matching the already-known
  TD-FE-005 or dev-server-only artifacts. Registered new debt TD-IAM-004 (synthetic tenant id in 5 controllers, deferred pending
  a Spring Modulith module-boundary decision; deny-by-default authorization itself unaffected). OWASP Dependency-Check (72
  deps), npm audit and Trivy (backend/employee-portal/repo-wide, all severities) reported 0 vulnerabilities/secrets/misconfigurations.
  Ready for COM-MOD-013-CLOSEOUT.
```
