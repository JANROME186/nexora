# Changelog

## [0.77.0] - 2026-07-09

### Added
- HOP MVP-MOD-003-BE-001 compiles the People and Clinical Master Data backend outputs
  (personmanagement, patientmanagement, doctormanagement, patientregistration) from the four
  MVP-MOD-003 capability packages (BCM-PER-001/002/003, BCM-ATT-002).
- New `people` PostgreSQL schema and JDBC adapters for the Patient and Doctor aggregates and the
  patient registration process record.
- Cross-module directory ports `PatientDirectory` and `DoctorDirectory` for downstream contexts to
  consume snapshots without depending on aggregate types.
- QA validation and security-quality evidence for MVP-MOD-003-BE-001.

### Changed
- Updated HOP `PROJECT_STATE.md`, root `PROJECT_STATE.md`, HOP `SOURCE_OF_TRUTH.md`, HOP
  security-quality index and capability-package traceability files (BCM-PER-001/002/003,
  BCM-ATT-002) to reflect that MVP-MOD-003-BE-001 is closed and MVP-MOD-003-BE-002 is next.
- Updated the HOP integrated local runbook with a People and Clinical Master Data smoke check.

## [0.70.0] - 2026-07-09

### Added
- Added Nexora Open Source First Security and Quality Standard in Markdown and YAML.
- Added reusable security quality gate prompts for open-source-first assessment, backlog gates and module closeout gates.

### Changed
- Updated framework execution sequence, bootstrap, generic lifecycle prompts, usage guide and HOP commercial backlog prompts so code-changing backlog items require security quality evidence.
- Updated repository and HOP source-of-truth registries to load the new standard and prompt playbook.

## [0.69.0] - 2026-07-08

### Added
- Added Nexora Business Requirement Versioning and Impact Standard in Markdown and YAML.
- Added business requirement impact prompts for resolving latest requirements and estimating impacted components, effort, timeline and cost.
- Added HOP `BUSINESS_REQUIREMENT_INDEX` under `00-intake/business-requirements/`.

### Changed
- Updated framework orchestration, lifecycle prompts, bootstrap and usage guide so agents always resolve the latest `BUSINESS_REQUIREMENT` before analysis, planning or development.
- Updated HOP ordered development guide and project state to block implementation when a newer business requirement lacks impact assessment.

## [0.68.0] - 2026-07-08

### Added
- Added a reusable Business Requirement to YAML prompt in Markdown and YAML for HOP project intake.
- Registered the HOP business requirement transformation prompt in repository and project source-of-truth files.

### Changed
- Redesigned HOP `BUSINESS_REQUIREMENT.md` as a clearer business-facing requirement and future-project intake template.
- Updated `BUSINESS_REQUIREMENT.md` to reflect the current HOP state, including MDPE, 92 capabilities, open data ingestion and product marketplace.

## [0.67.0] - 2026-07-08

### Added
- Added reusable Nexora Product Marketplace Standard in Markdown and YAML.
- Added ADR-032 for product marketplace and commercial extension architecture.
- Added `BCM-PLT-011 Product Marketplace and Entitlements` to the HOP capability map and dependency map.
- Added HOP Product Marketplace and Entitlements Contract in Markdown and YAML.
- Added `COM-MOD-017 Product Marketplace and Extension Packaging` to the HOP commercial backlog.

### Changed
- Promoted marketplace from product-evolution draft guidance to framework-level commercial extensibility guidance.
- Updated repository and HOP source-of-truth registries to load marketplace standards and contracts.

## [0.66.0] - 2026-07-08

### Added
- Added reusable Nexora Open Data Ingestion Standard in Markdown and YAML.
- Registered the Open Data Ingestion Standard in the framework execution sequence and repository source of truth.

### Changed
- Linked the HOP Open Data Ingestion Contract to the reusable framework standard.
- Updated repository and HOP project state to mark open data ingestion as framework-level guidance.

## [0.65.0] - 2026-07-08

### Added
- Added `BCM-PLT-010 Open Data Ingestion and Migration` as a critical MVP1 platform capability.
- Added the HOP Open Data Ingestion Contract in Markdown and YAML.
- Added simple migration package expectations for CSV, XLSX, JSON, NDJSON and ZIP bundles with manifest, validation, reconciliation and audit outputs.

### Changed
- Updated the HOP commercial backlog so `MVP-MOD-008` includes open data ingestion and migration readiness.
- Updated capability dependency coverage from 90 to 91 mapped capabilities.

## [0.64.0] - 2026-07-08

### Added
- Added Nexora Model Driven Product Engineering standard in Markdown and YAML.
- Added Business Capability Package standard in Markdown and YAML.
- Added ADR-031 for MDPE and capability package architecture.
- Added HOP Business Capability Package index under product definition.

### Changed
- Updated Nexora final vision to formalize Nexora Definition, Nexora Platform and the `Model -> Compile -> Implement Rules -> Validate -> Release` flow.
- Reframed HOP commercial backlog and execution prompts around Business Capability Packages instead of manual module artifacts.
- Updated repository and project state to make `MVP-MOD-002-DEF` the Diagnostic Catalog capability package modeling step.

## [0.63.0] - 2026-07-08

### Added
- Added HOP commercial product backlog in Markdown and YAML.
- Added HOP commercial backlog execution prompt playbook in Markdown and YAML.
- Added commercial module folder index for post-MVP module definition packages.

### Changed
- Updated repository and HOP project state to start `MVP-MOD-002-DEF Diagnostic Catalog definition package`.
- Registered commercial product backlog artifacts in repository and project source-of-truth files.

## [0.62.0] - 2026-07-08

### Added
- Added `MVP-MOD-001` Platform Foundation closeout evidence in Markdown and YAML.
- Added implementation closeout references to the Platform Foundation traceability record.

### Changed
- Marked `MVP-MOD-001 Platform Foundation` as implemented and ready for functional validation.
- Updated repository and project state to clear the active backlog item for the completed module.

## [0.61.0] - 2026-07-08

### Added
- Added `PF-QA-001` smoke and contract tests for `MVP-MOD-001 Platform Foundation`.
- Added backend OpenAPI contract tests for implemented Platform Foundation endpoints and schemas.
- Added backend MVP smoke test for tenant, laboratory, branch, user, role assignment and audit flow.
- Added employee portal navigation smoke test across all administration screens.
- Added mobile foundation smoke test for login, session, home and navigation.
- Added QA evidence for `PF-QA-001` in Markdown and YAML.

### Changed
- Updated repository and project state to mark `PF-QA-001` complete and move the active backlog item to `MVP-MOD-001-CLOSEOUT`.

## [0.60.0] - 2026-07-08

### Added
- Added `PF-APP-001` mobile app foundation under `projects/healthcare-operations-platform/07-implementation/mobile-app/`.
- Added local baseline login, session handling, authenticated navigation and initial home screen model.
- Added a mobile Platform Foundation API client prepared for backend integration.
- Added mobile tests for local authentication, navigation, API client behavior and app composition.
- Added QA evidence for `PF-APP-001` in Markdown and YAML.

### Changed
- Updated repository and project state to mark `PF-APP-001` complete and move the active backlog item to `PF-QA-001`.

## [0.59.0] - 2026-07-08

### Added
- Added `PF-FE-001` employee portal administration web shell under `projects/healthcare-operations-platform/07-implementation/employee-portal/`.
- Added React + TypeScript screens for Tenants, Laboratories, Branches, Users, Role Assignments and Audit Events.
- Added a Platform Foundation API client that consumes only the already-implemented backend endpoints.
- Added a shared administration scope indicator and a confirmation dialog for the access-changing role assignment action.
- Added Vitest and Testing Library specs for the API client, tenant creation flow and role assignment confirmation flow.
- Added a frontend README with install, run, build and test commands.
- Added QA evidence for `PF-FE-001` in Markdown and YAML.

### Changed
- Updated repository and project state to mark `PF-FE-001` complete and move the active backlog item to `PF-APP-001`.

## [0.58.0] - 2026-07-08

### Added
- Added `PF-BE-004` audit-compliance baseline for append-only audit event recording.
- Added audit events for tenant, laboratory, branch, user account and role assignment actions.
- Added `GET /api/audit/events` query support with tenant and subject filters.
- Added default in-memory audit repository and local PostgreSQL JDBC audit repository.
- Added idempotent audit schema table for local profile and Docker Compose runtime.
- Added backend API tests and local PostgreSQL persistence tests for audit event recording.
- Added QA evidence for `PF-BE-004` in Markdown and YAML.

### Changed
- Updated repository and project state to mark `PF-BE-004` complete and move the active backlog item to `PF-FE-001`.
- Updated the MVP-MOD-001 API contract with the implemented audit event response fields.

## [0.57.0] - 2026-07-08

### Added
- Added `PF-BE-003` identity-access commands for user account creation, query and scoped role assignment.
- Added REST endpoints to create and query user accounts and to assign scoped roles.
- Added default in-memory identity repository and local PostgreSQL JDBC identity repository.
- Added idempotent identity schema tables for user accounts and role assignments.
- Added a read-only `TenantDirectory` port so identity-access can validate tenant existence without depending on internal organization-management types.
- Added backend API tests and local PostgreSQL persistence tests for the identity access command flow.
- Added QA evidence for `PF-BE-003` in Markdown and YAML.

### Changed
- Updated repository and project state to mark `PF-BE-003` complete and move the active backlog item to `PF-BE-004`.
- Updated the MVP-MOD-001 API contract with the get user account endpoint.

## [0.56.0] - 2026-07-08

### Added
- Added `PF-BE-002` organization-management commands for tenant, laboratory and branch.
- Added REST endpoints to create and query tenant, laboratory and branch records.
- Added default in-memory organization repository and local PostgreSQL JDBC repository.
- Added idempotent platform foundation schema initialization for application local profile and local runtime.
- Added backend API tests and local PostgreSQL persistence tests for the organization command flow.
- Added QA evidence for `PF-BE-002` in Markdown and YAML.

### Changed
- Updated repository and project state to mark `PF-BE-002` complete and move the active backlog item to `PF-BE-003`.
- Updated the MVP-MOD-001 API contract with tenant, laboratory and branch query endpoints.

## [0.55.0] - 2026-07-08

### Added
- Added `PF-OPS-001` local development runtime for Healthcare Operations Platform `MVP-MOD-001 Platform Foundation`.
- Added Docker Compose profile with PostgreSQL, Redis and OpenTelemetry Collector.
- Added local environment variable example file and PostgreSQL schema initialization script.
- Added optional backend local database integration test for the PostgreSQL profile.
- Added QA evidence for `PF-OPS-001` in Markdown and YAML.

### Changed
- Updated repository and project state to mark `PF-OPS-001` complete and move the active backlog item to `PF-BE-002`.

## [0.54.0] - 2026-07-08

### Added
- Added `PF-BE-001` backend skeleton for Healthcare Operations Platform `MVP-MOD-001 Platform Foundation`.
- Added Java 21, Spring Boot 3.x and Spring Modulith Maven baseline under `projects/healthcare-operations-platform/07-implementation/backend/`.
- Added organization management, identity access, audit compliance and observability module boundaries.
- Added actuator and platform health smoke tests plus Modulith boundary verification.
- Added QA evidence for `PF-BE-001` in Markdown and YAML.
- Added repository `.gitignore` for build outputs and local dependency caches.

### Changed
- Updated repository and project state to mark `PF-BE-001` complete and move the active backlog item to `PF-OPS-001`.

## [0.53.0] - 2026-07-08

### Changed
- Reordered `nexora-framework/` into numbered folders that reflect the framework execution sequence.
- Moved standards, orchestration, recipes, prompts, templates, governance, engineering, specifications and examples under ordered framework stages.
- Updated repository and project references to the new numbered framework paths.

### Added
- Added `00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md` and `.yaml` as the first framework loading artifact.
- Added README files for the numbered framework stages so each folder explains its role in the execution sequence.

## [0.52.0] - 2026-07-08

### Added
- Added agent-agnostic strategic handoff and YAML vision artifacts.
- Added Healthcare Operations Platform MVP development readiness decision in Markdown and YAML.
- Registered vision and readiness decision artifacts in repository and project source-of-truth files.

### Changed
- Replaced tool-specific handoff with an agent-agnostic strategic handoff.
- Marked MVP-MOD-001 development start as approved while keeping enterprise-wide roadmap items non-blocking for the first module.

## [0.51.0] - 2026-07-08

### Added
- Added auxiliary development prompts for module kickoff, backlog slices, backend, web, mobile, QA and closeout.
- Added prompt hierarchy rules so auxiliary prompts support but do not override generic lifecycle prompts or project source artifacts.

### Changed
- Aligned Healthcare Operations Platform MVP-MOD-001 backlog order across the module definition and backlog execution playbook.
- Added the mobile foundation backlog item to the MVP-MOD-001 module definition.

## [0.50.0] - 2026-07-08

### Added
- Added generic framework-level lifecycle prompts for project analysis, framework compliance validation and MVP development.
- Added Markdown and YAML prompt playbooks under `nexora-framework/05-prompts/prompts/`.
- Registered the generic prompt playbook in repository source-of-truth and project manifest files.

## [0.49.0] - 2026-07-08

### Added
- Added YAML machine-readable counterparts for Healthcare Operations Platform agent execution artifacts.
- Added structured YAML files for project brief, ordered development guide, backlog execution playbook and MVP-MOD-001 implementation package documents.
- Added `BUSINESS_REQUIREMENT.md` as a structured index derived from requester-supplied `BUSINESS_REQUIREMENT.md`.

### Changed
- Updated source-of-truth registries and module definition to prefer YAML for agent execution while preserving Markdown for human readability.

## [0.48.0] - 2026-07-08

### Added
- Added the Healthcare Operations Platform MVP backlog execution prompt playbook.
- Registered the playbook in project and repository source-of-truth files so agents can start from a generic instruction and execute backlog items in order.

## [0.47.0] - 2026-07-07

### Changed
- Enforced `BUSINESS_REQUIREMENT.md` as requester-supplied source material, not agent-generated output.
- Updated orchestration, recipe, bootstrap, usage guide and project folder standards so agents stop when the business requirement is missing.
- Removed the `BUSINESS_REQUIREMENT.md` project template to avoid implying that agents should generate the business requirement.

## [0.46.0] - 2026-07-07

### Added
- Added `NEXORA_FRAMEWORK_USAGE_GUIDE.md` as the root-level operating guide for using the Nexora framework.
- Documented analysis-agent startup, expected analysis outputs, validation gates and development-agent handoff instructions.

### Changed
- Updated repository source of truth, manifest, state and README to make the usage guide an official entry point.

## [0.45.0] - 2026-07-07

### Added
- Added Nexora project orchestration workflow for scanning `projects/`, validating analysis status and completing MVP definitions.
- Added `BUSINESS_REQUIREMENT.md` as the required high-level business input for every project.
- Added Healthcare Operations Platform business requirement.
- Added project template support for `BUSINESS_REQUIREMENT.md`.

### Changed
- Updated framework entrypoints so agents apply project orchestration before MVP implementation.
- Updated Agent-to-MVP recipe to start from the business requirement and then structure `PROJECT_BRIEF.md`.

## [0.44.0] - 2026-07-07

### Added
- Added Nexora Agent-Agnostic Standard in Markdown and YAML.
- Added agent-agnostic readiness flags to repository and HOP project state.

### Changed
- Renamed bootstrap entrypoints to `AGENT_BOOTSTRAP.md`.
- Removed named-agent, named-assistant and named-provider references from source artifacts.
- Updated HOP framework paths to the current numbered project structure.
- Generalized AI provider registry to replaceable provider and local-runtime categories.

## [0.43.0] - 2026-07-07

### Changed
- Reorganized Healthcare Operations Platform so the project root contains only numbered folders.
- Moved product artifacts under `01-product-definition`.
- Moved domain artifacts under `02-domain-definition`.
- Moved architecture artifacts under `03-architecture`.
- Moved requirements artifacts under `04-requirements`.
- Moved contracts under `05-contracts`.
- Moved MVP delivery packages under `06-delivery`.
- Moved project engineering governance under `09-operations`.
- Moved generated knowledge and diagrams under `10-generated`.

### Added
- Added `ORDERED_DEVELOPMENT_GUIDE.md` for agent loading order and incremental development rules.
- Added `99-legacy` for historical readmes and migration continuity.

## [0.42.0] - 2026-07-07

### Added
- Added `nexora-framework/` as the reusable Nexora documentation and agent-delivery framework.
- Added project folder standard, documentation standard and Agent-to-MVP recipe.
- Added reusable new-project template under `nexora-framework/06-templates/templates/project-template/`.
- Added `projects/` as the home for self-contained Nexora solutions.
- Added `projects/healthcare-operations-platform/PROJECT_BRIEF.md`.
- Added project-level state and source-of-truth registries for Healthcare Operations Platform.
- Added canonical project-stage folders for Healthcare Operations Platform.

### Changed
- Moved Healthcare Operations Platform artifacts into `projects/healthcare-operations-platform/`.
- Moved reusable/company/framework assets into `nexora-framework/`.
- Rewrote root `README.md`, `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, `PROJECT_MANIFEST.md`, `KNOWLEDGE_INDEX.md` and bootstraps for the multi-project structure.

## [0.41.0] - 2026-07-07

### Added
- Added ACM-001 Actor Catalog with MVP actors, permissions, scopes and audit expectations.
- Added HRP-001 Healthcare Reference Processes for the MVP operational spine.
- Added BRM-001 Business Rules Catalog for security, privacy, clinical validation, audit and integration guardrails.
- Added MVP-MOD-001 Platform Foundation Definition Package with API contract, domain model, migration plan, UI map, security rules, tests and traceability.
- Added validators for actor catalog, reference processes and business rules.
- Added ADR-030 for actor catalog and access scopes.

### Changed
- Updated project phase to Ready for MVP Development.
- Updated bootstraps so the next task starts implementation of MVP-MOD-001.
- Updated SOURCE_OF_TRUTH with actor, process, rule and MVP module sources.

## [0.40.0] - 2026-07-07

### Added
- Imported `nexora-agent-base-v0.39.0` into the local Git repository.
- Added BCM-002 Capability Dependency Map covering all 90 BCM-001 capabilities.
- Added HOP MVP Agent-Agnostic Implementation Framework.
- Added HOP MVP Agent Framework for repository-driven implementation planning.
- Added ADR-029 for capability dependency governance.
- Added capability dependency validator guidance and generated Mermaid dependency view.

### Changed
- Updated `PROJECT_STATE.md` to v0.40.0.
- Updated `SOURCE_OF_TRUTH.md` with BCM-002 and HOP MVP framework sources.
- Updated root and `.ai` agent bootstrap files so the next task starts at ACM-001.

## [0.39.0] - 2026-07-07

### Added
- Updated agent bootstrap.
- Company/product separation.
- Nexora company profile.
- Healthcare Operations Platform product definition.
- ADR-028.

### Changed
- Root README now defines Nexora as a development and AI company.
- PROJECT_STATE updated.
- SOURCE_OF_TRUTH updated.

### Clarified
- Nexora is the company.
- Healthcare Operations Platform is the first product.
