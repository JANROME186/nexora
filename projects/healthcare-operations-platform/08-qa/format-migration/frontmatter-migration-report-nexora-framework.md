---
id: NXF-FMT-MIGRATION-REPORT
type: frontmatter-migration-report
status: completed
scope: nexora-framework
apply: false
use_ollama: false
archive_source: false
update_references: false
model: qwen2.5-coder:0.5b
limit: null
candidates: 205
planned: 198
written: 0
collisions: 0
errors: 0
reference_issues: 0
---

# Frontmatter Migration Report

## Summary

| Scope | Candidates | Planned | Written | Collisions | Errors | Reference Issues |
| --- | --- | --- | --- | --- | --- | --- |
| nexora-framework | 205 | 198 | 0 | 0 | 0 | 0 |

## Strategy Breakdown

| Strategy | Count |
| --- | --- |
| deterministic | 33 |
| ollama | 165 |
| skip | 7 |

## Artifact Type Breakdown

| Kind | Count |
| --- | --- |
| backlog | 4 |
| markdown | 162 |
| narrative_or_large_yaml | 10 |
| qa_evidence | 5 |
| runbook | 3 |
| structured_yaml | 21 |

## Samples

| Source | Target | Strategy |
| --- | --- | --- |
| nexora-framework/00-start-here/docs/README.md | nexora-framework/00-start-here/docs/README.md | ollama |
| nexora-framework/00-start-here/docs/vision/NEXORA_FINAL_VISION.md | nexora-framework/00-start-here/docs/vision/NEXORA_FINAL_VISION.md | ollama |
| nexora-framework/00-start-here/docs/vision/NEXORA_FINAL_VISION.md | nexora-framework/00-start-here/docs/vision/NEXORA_FINAL_VISION.md | deterministic |
| nexora-framework/00-start-here/docs/vision/NEXORA_STRATEGIC_HANDOFF.md | nexora-framework/00-start-here/docs/vision/NEXORA_STRATEGIC_HANDOFF.md | ollama |
| nexora-framework/00-start-here/docs/vision/NEXORA_STRATEGIC_HANDOFF.md | nexora-framework/00-start-here/docs/vision/NEXORA_STRATEGIC_HANDOFF.md | deterministic |
| nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md | nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md | ollama |
| nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md | nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md | deterministic |
| nexora-framework/00-start-here/README.md | nexora-framework/00-start-here/README.md | ollama |
| nexora-framework/01-enterprise/company/company-profile.md | nexora-framework/01-enterprise/company/company-profile.md | deterministic |
| nexora-framework/01-enterprise/company/README.md | nexora-framework/01-enterprise/company/README.md | ollama |
| nexora-framework/01-enterprise/README.md | nexora-framework/01-enterprise/README.md | ollama |
| nexora-framework/02-standards/README.md | nexora-framework/02-standards/README.md | ollama |
| nexora-framework/02-standards/standards/agent-agnostic-standard.md | nexora-framework/02-standards/standards/agent-agnostic-standard.md | ollama |
| nexora-framework/02-standards/standards/agent-agnostic-standard.md | nexora-framework/02-standards/standards/agent-agnostic-standard.md | deterministic |
| nexora-framework/02-standards/standards/business-requirement-versioning-standard.md | nexora-framework/02-standards/standards/business-requirement-versioning-standard.md | ollama |

## Context Policy

This report is intentionally compact. Detailed per-file inventories are not emitted by default because they increase downstream model context without improving backlog execution.
