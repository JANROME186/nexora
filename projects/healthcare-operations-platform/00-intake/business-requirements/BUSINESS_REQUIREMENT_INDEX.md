# HOP Business Requirement Version Index

**Artifact ID:** `HOP-BR-INDEX-001`
**Status:** Approved

## Purpose

This index declares which `BUSINESS_REQUIREMENT` version is current for Healthcare Operations Platform.

Agents must use this index before analysis, validation, planning or development.

## Current Version

| Field | Value |
| --- | --- |
| Version | `v0.68.0` |
| Date | `2026-07-08` |
| Current file | `../../BUSINESS_REQUIREMENT.md` |
| Structured YAML | `../../BUSINESS_REQUIREMENT.md` |
| Status | Active |

## Previous Version

| Field | Value |
| --- | --- |
| Version | `v0.67.0` |
| Status | Superseded |
| Archive | Not archived |

## Agent Rule

Agents must:

- Read `BUSINESS_REQUIREMENT_INDEX.md`.
- Use `current.file` as the active business requirement.
- Validate `current.structured_index` is derived from the active Markdown source.
- Generate an impact assessment when the current version changes after the last analyzed version.

Impact assessments must be placed under:

```text
00-intake/business-requirements/impact-assessments/<version>/
```

If no rate card exists, effort and time can be estimated, but cost must be marked as requiring a rate card.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-INDEX-001
  type: business-requirement-version-index
  name: Healthcare Operations Platform Business Requirement Version Index
  version: 1.0.0
  status: approved
  human_readable: BUSINESS_REQUIREMENT_INDEX.md
  machine_readable: BUSINESS_REQUIREMENT_INDEX.md
purpose: Track the current and previous requester-supplied business requirement versions
  for HOP.
standard:
  framework_standard: ../../../../nexora-framework/02-standards/standards/business-requirement-versioning-standard.md
current:
  version: v0.68.0
  date: 2026-07-08
  file: ../../BUSINESS_REQUIREMENT.md
  structured_index: ../../BUSINESS_REQUIREMENT.md
  status: active
  description: Business-facing HOP requirement redesigned as future-project intake
    template.
previous:
  version: v0.67.0
  date: 2026-07-08
  file: not_archived
  structured_index: not_archived
  status: superseded
  description: Marketplace framework standard added before the business requirement
    template redesign.
versioning_policy:
  root_business_requirement_is_current_pointer: true
  archive_new_major_business_versions: true
  archive_path_pattern: versions/<version>/
  impact_assessment_path_pattern: impact-assessments/<version>/
  agent_must_use_current_pointer: true
  agent_must_not_use_archived_version_when_current_exists: true
change_detection:
  last_analyzed_version: v0.68.0
  impact_assessment_required: false
  latest_impact_assessment: null
  next_change_behavior: Generate impact assessment before modifying derived artifacts
    or implementation.
estimation_policy:
  estimate_components: true
  estimate_effort: true
  estimate_timeline: true
  estimate_cost: true
  cost_requires_rate_card: true
  default_currency: null
  rate_card_source: not_defined
history:
- version: v0.68.0
  date: 2026-07-08
  status: active
  summary: HOP business requirement redesigned as a reusable intake template and synchronized
    with current product state.
- version: v0.67.0
  date: 2026-07-08
  status: superseded
  summary: Product marketplace standard and HOP marketplace capability were added.
```
