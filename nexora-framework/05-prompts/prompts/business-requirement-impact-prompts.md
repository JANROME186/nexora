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
nexora-framework/02-standards/standards/business-requirement-versioning-standard.md
```

If a project index exists, the agent must use:

```text
projects/<project-slug>/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-PROMPTS-BR-IMPACT-001
  type: prompt-playbook
  name: Business Requirement Version and Impact Prompts
  version: 1.0.0
  status: approved
  human_readable: business-requirement-impact-prompts.md
  machine_readable: business-requirement-impact-prompts.md
  owner: Nexora Product Architecture
purpose: Provide agent-agnostic prompts for resolving the latest business requirement
  version and estimating impact, effort, timeline and cost after requirement changes.
prompts:
  resolve_latest_business_requirement:
    id: PROMPT-BR-RESOLVE-001
    name: Resolve latest business requirement version
    prompt: 'Resolve the latest BUSINESS_REQUIREMENT version for the target project.


      Target project:

      projects/<project-slug>/


      Load:

      - nexora-framework/02-standards/standards/business-requirement-versioning-standard.md

      - projects/<project-slug>/SOURCE_OF_TRUTH.md if present

      - projects/<project-slug>/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
      if present

      - projects/<project-slug>/BUSINESS_REQUIREMENT.md

      - projects/<project-slug>/BUSINESS_REQUIREMENT.md if present


      Rules:

      - Use BUSINESS_REQUIREMENT_INDEX.md current.version and current.file when
      the index exists.

      - If the index is missing, use projects/<project-slug>/BUSINESS_REQUIREMENT.md
      as current source.

      - If the latest version is ambiguous, stop and ask the requester to confirm
      the active version.

      - Validate whether BUSINESS_REQUIREMENT.md reflects the current Markdown source.

      - Do not invent requirements.


      Output:

      - Current business requirement version.

      - Current business requirement file.

      - Previous comparable version when known.

      - Whether YAML regeneration is required.

      - Whether impact assessment is required.

      '
  estimate_business_requirement_change_impact:
    id: PROMPT-BR-IMPACT-001
    name: Estimate business requirement change impact
    prompt: 'Analyze the impact of the latest BUSINESS_REQUIREMENT change for the
      target project.


      Target project:

      projects/<project-slug>/


      Load:

      - nexora-framework/02-standards/standards/business-requirement-versioning-standard.md

      - projects/<project-slug>/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md

      - current BUSINESS_REQUIREMENT.md

      - previous comparable BUSINESS_REQUIREMENT.md when available

      - projects/<project-slug>/BUSINESS_REQUIREMENT.md

      - projects/<project-slug>/SOURCE_OF_TRUTH.md

      - projects/<project-slug>/PROJECT_STATE.md

      - current capability map, dependency map, requirements catalog, contracts, delivery
      backlog and implementation state when available.


      Required work:

      - Compare current requirement against previous comparable version.

      - Classify detected changes.

      - Identify impacted business capabilities.

      - Identify impacted requirements, domain components, architecture components,
      contracts, UI/mobile surfaces, data migration, marketplace model, tests, QA
      and operations.

      - Estimate effort in person-days and t-shirt size.

      - Estimate timeline in calendar days with confidence level.

      - Estimate cost only if a rate card exists; otherwise mark cost as requires_rate_card.

      - Document assumptions, risks, decisions and recommendation.


      Required outputs:

      - projects/<project-slug>/00-intake/business-requirements/impact-assessments/<version>/business-requirement-impact-assessment.yaml

      - projects/<project-slug>/00-intake/business-requirements/impact-assessments/<version>/business-requirement-impact-assessment.md


      Before finishing:

      - Validate YAML parsing.

      - Update SOURCE_OF_TRUTH.md with the new impact assessment files.

      - Update PROJECT_STATE.md with the analyzed business requirement version and
      impact status.

      - Do not modify implementation code unless explicitly instructed after impact
      acceptance.

      '
minimal_user_prompts:
  resolve_latest: Resolve the latest BUSINESS_REQUIREMENT version for projects/<project-slug>/
    and report whether derived YAML or impact assessment is required.
  estimate_impact: Analyze the impact of the latest BUSINESS_REQUIREMENT change for
    projects/<project-slug>/ and estimate impacted components, effort, time and cost.
```
