# Changelog

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
- Added reusable new-project template under `nexora-framework/templates/project-template/`.
- Added `projects/` as the home for self-contained Nexora solutions.
- Added `projects/healthcare-operations-platform/PROJECT_BRIEF.md`.
- Added project-level state and source-of-truth registries for Healthcare Operations Platform.
- Added canonical project-stage folders for Healthcare Operations Platform.

### Changed
- Moved Healthcare Operations Platform artifacts into `projects/healthcare-operations-platform/`.
- Moved reusable/company/framework assets into `nexora-framework/`.
- Rewrote root `README.md`, `PROJECT_STATE.yaml`, `SOURCE_OF_TRUTH.yaml`, `PROJECT_MANIFEST.yaml`, `KNOWLEDGE_INDEX.yaml` and bootstraps for the multi-project structure.

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
- Updated `PROJECT_STATE.yaml` to v0.40.0.
- Updated `SOURCE_OF_TRUTH.yaml` with BCM-002 and HOP MVP framework sources.
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
