# Nexora Project Analysis and MVP Workflow

## What This Workflow Does

An agent uses this workflow to inspect the `projects/` folder, determine whether each project has already been analyzed with the Nexora framework, and complete missing definitions until the project is ready for MVP implementation.

The workflow starts from a high-level business requirement and ends with a project folder that specialized subagents can use without chat history.

## Required Project Input

Every project must have:

- `BUSINESS_REQUIREMENT.md`: high-level business need and user context.
- `PROJECT_BRIEF.md`: structured summary refined from the business requirement.
- `SOURCE_OF_TRUTH.md`: authoritative artifact registry.
- `PROJECT_STATE.md`: readiness state and blocking gaps.

If `BUSINESS_REQUIREMENT.md` is missing, the project is not ready to analyze.

## Agent Procedure

1. Load the Nexora framework files listed in `project-analysis-and-mvp-workflow.md`.
2. Enumerate `projects/<project-slug>/`.
3. For each project, load its business requirement, brief, state and source of truth.
4. Decide whether the project is already analyzed.
5. If it is not analyzed, apply `nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md`.
6. Produce source artifacts first.
7. Define the open-source-first technology baseline and security quality gate strategy.
8. Create the MVP delivery framework and first module package.
9. Update `SOURCE_OF_TRUTH.md` and `PROJECT_STATE.md`.
10. Handoff module packages to specialized subagents.

## MVP Readiness

A project is ready for development when:

- The business requirement and project brief are complete.
- Product, domain, architecture, requirements, contracts and QA definitions exist.
- MVP modules are ordered incrementally.
- The first module has a complete implementation package.
- Open-source-first technology selection and security quality gate expectations are defined.
- `PROJECT_STATE.md` has `development_readiness.status: ready`.
- `blocking_definition_gaps` is empty.

## Subagent Handoff

Specialized subagents implement only from repository artifacts. A handoff must include the module id, source paths, capability ids, domain ownership, contracts, UI surfaces, security rules, tests and traceability.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-ORCH-001
  type: project-orchestration-workflow
  name: Nexora Project Analysis and MVP Readiness Workflow
  version: 1.0.0
  status: approved
  owner: Nexora Engineering
purpose: Inspect projects under projects/, validate whether each project has been
  analyzed with the Nexora framework, and complete missing definitions until specialized
  subagents can start MVP implementation from repository artifacts.
non_negotiable_input_rule: BUSINESS_REQUIREMENT.md is external input supplied by the
  requester. Agents must consume it, validate that it exists, and stop if it is missing.
  Agents must not generate, synthesize or silently create BUSINESS_REQUIREMENT.md.
scope:
  applies_to: projects/*
  excludes:
  - projects/README.md
  - generated artifacts as source of truth
  agent_policy: agent_agnostic
repository_context:
  required_before_project_scan:
  - AGENT_BOOTSTRAP.md
  - SOURCE_OF_TRUTH.md
  - PROJECT_STATE.md
  - nexora-framework/README.md
  - nexora-framework/02-standards/standards/project-folder-standard.md
  - nexora-framework/02-standards/standards/documentation-standard.md
  - nexora-framework/02-standards/standards/agent-agnostic-standard.md
  - nexora-framework/02-standards/standards/business-requirement-versioning-standard.md
  - nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  - nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md
  - nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md
project_discovery:
  root: projects/
  project_folder_pattern: projects/<project-slug>/
  ignore:
  - projects/README.md
  for_each_project:
    load_if_present:
    - BUSINESS_REQUIREMENT.md
    - PROJECT_BRIEF.md
    - PROJECT_BRIEF.md
    - SOURCE_OF_TRUTH.md
    - PROJECT_STATE.md
    stop_if_missing:
    - BUSINESS_REQUIREMENT.md
    create_missing_control_files_from_template:
    - PROJECT_BRIEF.md
    - PROJECT_BRIEF.md
    - SOURCE_OF_TRUTH.md
    - PROJECT_STATE.md
business_requirement:
  required_file: BUSINESS_REQUIREMENT.md
  supplied_by: requester
  agent_may_create: false
  agent_may_modify_without_explicit_request: false
  purpose: Provide the high-level business need, user problem and expected outcome
    that drive analysis, proposed solution and MVP scope.
  latest_version_resolution:
    standard: nexora-framework/02-standards/standards/business-requirement-versioning-standard.md
    index_file: 00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
    current_pointer: BUSINESS_REQUIREMENT.md
    structured_index: BUSINESS_REQUIREMENT.md
    rule: Agents must resolve the latest business requirement version before analysis,
      validation, planning or development.
    if_changed_since_last_analysis:
      required_action: create_business_requirement_impact_assessment
      impact_prompt: nexora-framework/05-prompts/prompts/business-requirement-impact-prompts.md
      implementation_behavior: do_not_implement_changed_scope_until_impact_is_accepted_or_governance_allows_progress
  minimum_sections:
  - Business Context
  - User Need
  - Current Pain
  - Desired Outcome
  - MVP Expectation
  - Constraints
  blocking_gap_if_missing: business_requirement_missing
  missing_behavior:
    action: stop_project_analysis
    allowed_outputs:
    - Existing or newly initialized PROJECT_STATE.md with business_requirement_missing,
      only if this can be done without creating or inventing BUSINESS_REQUIREMENT.md.
    - A user-facing request asking the requester to provide BUSINESS_REQUIREMENT.md.
    forbidden_outputs:
    - Generated BUSINESS_REQUIREMENT.md
    - PROJECT_BRIEF.md inferred without BUSINESS_REQUIREMENT.md
    - Capability map
    - MVP framework
    - Module package
  relation_to_project_brief: PROJECT_BRIEF.md explains and PROJECT_BRIEF.md structures
    BUSINESS_REQUIREMENT.md; neither replaces requester-supplied source material.
analysis_status_detection:
  ready_when_all_exist:
  - BUSINESS_REQUIREMENT.md
  - PROJECT_BRIEF.md
  - PROJECT_BRIEF.md
  - SOURCE_OF_TRUTH.md
  - PROJECT_STATE.md
  - 01-product-definition/
  - 02-domain-definition/
  - 03-architecture/
  - 04-requirements/
  - 05-contracts/
  - 06-delivery/mvp/
  - 08-qa/technical-debt/
  ready_state_requirements:
    development_readiness.status: ready
    development_readiness.blocking_definition_gaps: []
    development_readiness.ready_to_start_module: not_null
  not_analyzed_indicators:
  - PROJECT_STATE.md is missing
  - SOURCE_OF_TRUTH.md is missing
  - BUSINESS_REQUIREMENT.md is missing
  - PROJECT_BRIEF.md contains placeholder text
  - capability map is missing
  - domain foundation is missing
  - MVP framework is missing
  - no module definition package exists
completion_workflow:
  if_not_analyzed:
    apply_recipe: nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md
    phases:
    - resolve_latest_business_requirement_version
    - assess_business_requirement_change_impact_when_needed
    - normalize_business_requirement
    - complete_project_brief
    - create_project_folder_structure
    - define_product_and_capabilities
    - define_domain_foundation
    - define_architecture_baseline
    - define_open_source_first_security_quality_baseline
    - initialize_technology_debt_backlog
    - define_requirements_contracts_and_tests
    - create_mvp_delivery_framework
    - create_first_module_package
    - update_source_of_truth_and_project_state
  source_first_rule: YAML and Markdown source artifacts must be completed before generated
    artifacts; agents consume YAML first and humans read Markdown companions.
  implementation_rule: Do not create application code until the first module package
    satisfies the readiness gate.
mvp_required_outputs:
  project_level:
  - BUSINESS_REQUIREMENT.md supplied by requester
  - PROJECT_BRIEF.md
  - PROJECT_BRIEF.md
  - SOURCE_OF_TRUTH.md
  - PROJECT_STATE.md
  - ORDERED_DEVELOPMENT_GUIDE.md
  - ORDERED_DEVELOPMENT_GUIDE.md
  product_definition:
  - capability map
  - capability dependency map
  - personas or actors
  - MVP boundary
  domain_definition:
  - bounded contexts or context map
  - aggregate catalog
  - canonical vocabulary
  - business rules catalog
  - reference processes
  architecture:
  - application architecture
  - data architecture
  - security and compliance baseline
  - integration architecture
  - deployment or runtime baseline
  - open-source-first technology baseline
  - security quality gate strategy
  - technology debt backlog and gradual remediation policy
  delivery:
  - MVP framework
  - first module definition package
  - traceability from requirement to module
specialized_subagent_handoff:
  required_package_per_module:
  - module-definition.md
  - domain-model.md
  - domain-model.md
  - api-contract.openapi.md
  - database-migration-plan.md
  - database-migration-plan.md
  - ui-screen-map.md
  - ui-screen-map.md
  - security-and-audit-rules.md
  - security-and-audit-rules.md
  - test-plan.md
  - test-plan.md
  - traceability.md
  required_context_for_subagents:
  - BUSINESS_REQUIREMENT.md
  - PROJECT_BRIEF.md
  - PROJECT_BRIEF.md
  - SOURCE_OF_TRUTH.md
  - PROJECT_STATE.md
  - target module package
  - related product, domain, architecture, contract and QA artifacts
  handoff_rule: Subagents receive repository paths and module scope, not chat history.
readiness_gate:
  status: ready
  required_truths:
  - High-level business requirement exists and was supplied by the requester.
  - Project brief refines the business requirement.
  - Source of truth lists all authoritative artifacts.
  - Project state has no blocking definition gaps.
  - MVP modules are ordered incrementally.
  - First module package is complete.
  - Agent-agnostic validation passes.
  - Open-source-first technology selection and security quality gate expectations
    are defined.
  - Technology evolution review and technical-debt backlog process are defined.
```
