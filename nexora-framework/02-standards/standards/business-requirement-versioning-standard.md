# Nexora Business Requirement Versioning and Impact Standard

**Artifact ID:** `NXF-BRV-STD-001`  
**Status:** Approved  
**Version:** `1.0.0`

## Purpose

This standard defines how agents must identify the latest requester-supplied `BUSINESS_REQUIREMENT` version, detect changes, and produce impact, effort, timeline and cost estimates before analysis, planning or implementation continues.

## Current Version Rule

Agents must always use the latest business requirement version.

Resolution order:

1. If `projects/<project-slug>/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml` exists, use `current.version` and `current.file`.
2. If no index exists, use `projects/<project-slug>/BUSINESS_REQUIREMENT.md`.
3. If only archived versions exist, stop unless the latest version can be determined unambiguously.

If the latest version is ambiguous, the agent must stop and request confirmation.

## Versioning Model

The project root `BUSINESS_REQUIREMENT.md` is the current human-readable business requirement.

`BUSINESS_REQUIREMENT.yaml` is a structured index derived from the current Markdown source.

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
- Regenerate or validate `BUSINESS_REQUIREMENT.yaml` when `BUSINESS_REQUIREMENT.md` changes.
- Generate impact assessment before modifying derived artifacts when the requirement changed.
- Update `PROJECT_STATE.yaml` with the analyzed business requirement version.
- Update `SOURCE_OF_TRUTH.yaml` when new impact artifacts are created.

Agents must not:

- Ignore `BUSINESS_REQUIREMENT_INDEX.yaml` when it exists.
- Continue with stale `BUSINESS_REQUIREMENT.yaml`.
- Infer changes from chat instead of the latest business requirement source.
- Estimate fixed cost without a rate card or explicit commercial rule.
- Absorb new scope into an active module without impact assessment.
