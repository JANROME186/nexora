# Nexora Business Requirement Versioning and Impact Standard

**Artifact ID:** `NXF-BRV-STD-001`
**Status:** Approved
**Version:** `1.0.0`

## Purpose

This standard defines how agents must identify the latest requester-supplied `BUSINESS_REQUIREMENT` version, detect changes, and produce impact, effort, timeline and cost estimates before analysis, planning or implementation continues.

## Current Version Rule

Agents must always use the latest business requirement version.

Resolution order:

1. If `projects/<project-slug>/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md` exists, use `current.version` and `current.file`.
2. If no index exists, use `projects/<project-slug>/BUSINESS_REQUIREMENT.md`.
3. If only archived versions exist, stop unless the latest version can be determined unambiguously.

If the latest version is ambiguous, the agent must stop and request confirmation.

## Versioning Model

The project root `BUSINESS_REQUIREMENT.md` is the current human-readable business requirement.

`BUSINESS_REQUIREMENT.md` is a structured index derived from the current Markdown source.

Archived versions may live under:

```text
projects/<project-slug>/00-intake/business-requirements/versions/<version>/
```

Impact assessments must live under:

```text
projects/<project-slug>/00-intake/business-requirements/impact-assessments/<version>/
```

## Change Detection

When a business requirement changes, the agent must compare the current version against the previous comparable version declared in the index.

Changes must be classified as one or more of:

- New scope.
- Changed scope.
- Removed scope.
- Clarified requirement.
- Business rule change.
- Actor change.
- Data or privacy change.
- Integration change.
- Marketplace or commercial change.
- Migration change.
- Non-functional change.
- Out-of-scope change.

Severity must be marked as low, medium, high or critical.

## Required Impact Outputs

When a change is detected, create:

- `business-requirement-impact-assessment.yaml`
- `business-requirement-impact-assessment.md`

The assessment must include:

- Source versions.
- Detected changes.
- Impacted business capabilities.
- Impacted requirements.
- Impacted domain components.
- Impacted architecture components.
- Impacted contracts.
- Impacted UI and mobile surfaces.
- Impacted data migration.
- Impacted marketplace or commercial model.
- Impacted tests and QA.
- Impacted operations.
- Implementation options.
- Effort estimate.
- Timeline estimate.
- Cost estimate.
- Assumptions.
- Risks.
- Required decisions.
- Recommendation.

## Estimation Rule

Effort must be estimated in person-days, person-weeks or t-shirt size.

Timeline must include calendar days, critical path items, dependencies, parallelizable work and confidence.

Cost must use a rate card when one exists. If no rate card exists, the agent must estimate effort and timeline, then mark cost as `requires_rate_card`.

## Agent Rules

Agents must:

- Resolve the latest business requirement before analysis, validation, planning or development.
- Regenerate or validate `BUSINESS_REQUIREMENT.md` when `BUSINESS_REQUIREMENT.md` changes.
- Generate impact assessment before modifying derived artifacts when the requirement changed.
- Update `PROJECT_STATE.md` with the analyzed business requirement version.
- Update `SOURCE_OF_TRUTH.md` when new impact artifacts are created.

Agents must not:

- Ignore `BUSINESS_REQUIREMENT_INDEX.md` when it exists.
- Continue with stale `BUSINESS_REQUIREMENT.md`.
- Infer changes from chat instead of the latest business requirement source.
- Estimate fixed cost without a rate card or explicit commercial rule.
- Absorb new scope into an active module without impact assessment.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-BRV-STD-001
  type: framework-standard
  name: Nexora Business Requirement Versioning and Impact Standard
  version: 1.0.0
  status: approved
  human_readable: business-requirement-versioning-standard.md
  machine_readable: business-requirement-versioning-standard.md
  owner: Nexora Product Architecture
purpose: 'Define how agents must identify the latest requester-supplied BUSINESS_REQUIREMENT
  version, detect business requirement changes, and produce impact, effort, timeline
  and cost estimates before analysis, planning or implementation continues.

  '
scope:
  applies_to:
  - projects/<project-slug>/BUSINESS_REQUIREMENT.md
  - projects/<project-slug>/BUSINESS_REQUIREMENT.md
  - projects/<project-slug>/00-intake/business-requirements/
  - project analysis
  - MVP definition
  - commercial backlog planning
  - implementation change intake
  excludes:
  - Agent-generated replacement of BUSINESS_REQUIREMENT.md
  - Silent assumption of business changes from chat history
  - Implementation before impact assessment when the business requirement changed
latest_version_resolution:
  priority_order:
  - source: projects/<project-slug>/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
    rule: Use current.version and current.file when the index exists.
  - source: projects/<project-slug>/BUSINESS_REQUIREMENT.md
    rule: Use as current requirement when no index exists.
  - source: projects/<project-slug>/00-intake/business-requirements/versions/
    rule: Use only as fallback if the index is missing and a single latest version
      can be determined unambiguously.
  ambiguity_behavior: stop_and_request_current_business_requirement_confirmation
  missing_behavior: stop_and_request_business_requirement
versioning_model:
  current_pointer_file: projects/<project-slug>/BUSINESS_REQUIREMENT.md
  structured_current_file: projects/<project-slug>/BUSINESS_REQUIREMENT.md
  index_file: projects/<project-slug>/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
  version_folder_pattern: projects/<project-slug>/00-intake/business-requirements/versions/<version>/
  impact_folder_pattern: projects/<project-slug>/00-intake/business-requirements/impact-assessments/<version>/
  version_id_formats:
  - semantic: v<major>.<minor>.<patch>
  - date: YYYY-MM-DD
  - combined: v<major>.<minor>.<patch>-YYYY-MM-DD
  rules:
  - BUSINESS_REQUIREMENT.md at project root is the current human-readable business
    requirement.
  - BUSINESS_REQUIREMENT.md is a structured index derived from the current Markdown
    requirement.
  - Archived versions may live under the version folder pattern.
  - The business requirement index declares the current version and previous comparable
    version.
  - Agents must never choose an older archived version when the index declares a current
    version.
  - Agents must update derived YAML only after reading the current Markdown source.
  - Agents must not invent missing business deltas.
change_detection:
  compare:
  - current BUSINESS_REQUIREMENT.md
  - previous version declared in BUSINESS_REQUIREMENT_INDEX.md when present
  - current BUSINESS_REQUIREMENT.md and previous structured YAML when present
  classification:
  - new_scope
  - changed_scope
  - removed_scope
  - clarified_requirement
  - business_rule_change
  - actor_change
  - data_or_privacy_change
  - integration_change
  - marketplace_or_commercial_change
  - migration_change
  - non_functional_change
  - out_of_scope_change
  severity:
  - low
  - medium
  - high
  - critical
impact_assessment_required_outputs:
- business-requirement-impact-assessment.yaml
- business-requirement-impact-assessment.md
impact_assessment_required_sections:
- artifact
- source_versions
- executive_summary
- detected_changes
- impacted_business_capabilities
- impacted_requirements
- impacted_domain_components
- impacted_architecture_components
- impacted_contracts
- impacted_ui_mobile_surfaces
- impacted_data_migration
- impacted_marketplace_or_commercial_model
- impacted_tests_and_qa
- impacted_operations
- implementation_options
- effort_estimate
- timeline_estimate
- cost_estimate
- assumptions
- risks
- required_decisions
- recommendation
estimation_model:
  effort_units:
  - person_days
  - person_weeks
  - t_shirt_size
  t_shirt_scale:
    XS:
      person_days_range:
      - 0.5
      - 2
    S:
      person_days_range:
      - 2
      - 5
    M:
      person_days_range:
      - 5
      - 15
    L:
      person_days_range:
      - 15
      - 40
    XL:
      person_days_range:
      - 40
      - 90
  timeline_fields:
  - calendar_days_range
  - critical_path_items
  - dependencies
  - parallelizable_work
  - confidence
  cost_fields:
  - rate_card_source
  - currency
  - estimated_low
  - estimated_high
  - formula
  - exclusions
  cost_rule: If no rate card exists, estimate effort and time, then mark cost as requires_rate_card.
agent_required_behavior:
- Always resolve the current business requirement version before analysis, validation,
  planning or development.
- If the current business requirement version changed since the last analyzed version,
  generate an impact assessment before modifying derived artifacts.
- Estimate affected components, effort, timeline and cost or mark cost as requiring
  a rate card.
- Record assumptions and confidence level.
- Update PROJECT_STATE.md with the analyzed business requirement version and any
  blocking decisions.
- Update SOURCE_OF_TRUTH.md when new impact assessment artifacts are created.
- Do not implement changed scope until the impact assessment is accepted or the project
  governance explicitly allows proceeding.
agent_prohibited_behavior:
- Do not infer requirement changes from chat when a newer BUSINESS_REQUIREMENT.md
  is available.
- Do not ignore BUSINESS_REQUIREMENT_INDEX.md when it exists.
- Do not proceed with stale BUSINESS_REQUIREMENT.md if the Markdown source changed.
- Do not estimate cost as a fixed price without a rate card or explicit commercial
  rule.
- Do not silently absorb new business scope into an active module without impact assessment.
readiness_gate:
  ready_to_continue_when:
  - latest_business_requirement_version_resolved: true
  - business_requirement_yaml_current: true
  - change_impact_required: false
  ready_after_change_when:
  - latest_business_requirement_version_resolved: true
  - business_requirement_yaml_current: true
  - business_requirement_impact_assessment_exists: true
  - required_business_decisions_resolved: true
```
