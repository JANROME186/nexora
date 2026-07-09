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
| Structured YAML | `../../BUSINESS_REQUIREMENT.yaml` |
| Status | Active |

## Previous Version

| Field | Value |
| --- | --- |
| Version | `v0.67.0` |
| Status | Superseded |
| Archive | Not archived |

## Agent Rule

Agents must:

- Read `BUSINESS_REQUIREMENT_INDEX.yaml`.
- Use `current.file` as the active business requirement.
- Validate `current.structured_index` is derived from the active Markdown source.
- Generate an impact assessment when the current version changes after the last analyzed version.

Impact assessments must be placed under:

```text
00-intake/business-requirements/impact-assessments/<version>/
```

If no rate card exists, effort and time can be estimated, but cost must be marked as requiring a rate card.
