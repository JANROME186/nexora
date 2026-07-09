# Business Requirement Version and Impact Prompts

**Artifact ID:** `NXF-PROMPTS-BR-IMPACT-001`  
**Status:** Approved  
**Version:** `1.0.0`

## Purpose

Use these prompts when a requester versions or modifies `BUSINESS_REQUIREMENT.md`.

The agent must always resolve the latest business requirement version before analysis, validation, planning or development.

## Prompt: Resolve Latest Version

```text
Resolve the latest BUSINESS_REQUIREMENT version for projects/<project-slug>/ and report whether derived YAML or impact assessment is required.
```

The agent must load the framework standard:

```text
nexora-framework/02-standards/standards/business-requirement-versioning-standard.yaml
```

If a project index exists, the agent must use:

```text
projects/<project-slug>/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml
```

## Prompt: Estimate Impact

```text
Analyze the impact of the latest BUSINESS_REQUIREMENT change for projects/<project-slug>/ and estimate impacted components, effort, time and cost.
```

The agent must generate:

```text
projects/<project-slug>/00-intake/business-requirements/impact-assessments/<version>/business-requirement-impact-assessment.yaml
projects/<project-slug>/00-intake/business-requirements/impact-assessments/<version>/business-requirement-impact-assessment.md
```

## Required Impact Analysis

The impact assessment must identify:

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
- Effort estimate.
- Timeline estimate.
- Cost estimate.
- Assumptions.
- Risks.
- Required decisions.
- Recommendation.

If no rate card exists, cost must be marked as `requires_rate_card`.
