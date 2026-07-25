---
id: NXF-FMT-MIGRATION-REPORT
type: frontmatter-migration-report
status: completed
scope: projects/healthcare-operations-platform
apply: false
use_ollama: false
archive_source: false
update_references: false
model: qwen2.5-coder:0.5b
limit: null
candidates: 2187
planned: 2151
written: 0
collisions: 0
errors: 0
reference_issues: 0
---

# Frontmatter Migration Report

## Summary

| Scope | Candidates | Planned | Written | Collisions | Errors | Reference Issues |
| --- | --- | --- | --- | --- | --- | --- |
| projects/healthcare-operations-platform | 2187 | 2151 | 0 | 0 | 0 | 0 |

## Strategy Breakdown

| Strategy | Count |
| --- | --- |
| deterministic | 1236 |
| ollama | 915 |
| skip | 36 |

## Artifact Type Breakdown

| Kind | Count |
| --- | --- |
| backlog | 6 |
| markdown | 816 |
| narrative_or_large_yaml | 135 |
| qa_evidence | 287 |
| runbook | 10 |
| structured_yaml | 933 |

## Samples

| Source | Target | Strategy |
| --- | --- | --- |
| projects/healthcare-operations-platform/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md | projects/healthcare-operations-platform/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md | ollama |
| projects/healthcare-operations-platform/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml | projects/healthcare-operations-platform/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md | deterministic |
| projects/healthcare-operations-platform/00-intake/business-requirements/README.md | projects/healthcare-operations-platform/00-intake/business-requirements/README.md | ollama |
| projects/healthcare-operations-platform/00-intake/README.md | projects/healthcare-operations-platform/00-intake/README.md | ollama |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/bcm-001/business-capability-map.md | projects/healthcare-operations-platform/01-product-definition/business-capabilities/bcm-001/business-capability-map.md | ollama |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/bcm-001/business-capability-map.yaml | projects/healthcare-operations-platform/01-product-definition/business-capabilities/bcm-001/business-capability-map.md | ollama |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md | projects/healthcare-operations-platform/01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md | ollama |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/bcm-002/capability-dependency-map.yaml | projects/healthcare-operations-platform/01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md | ollama |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/business-model.yaml | projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/business-model.md | deterministic |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/business-rules.yaml | projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/business-rules.md | deterministic |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/capability-package.yaml | projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/capability-package.md | deterministic |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/events.yaml | projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/events.md | deterministic |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/generation-plan.yaml | projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/generation-plan.md | deterministic |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/mobile-model.yaml | projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/mobile-model.md | deterministic |
| projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/observability-model.yaml | projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/observability-model.md | deterministic |

## Context Policy

This report is intentionally compact. Detailed per-file inventories are not emitted by default because they increase downstream model context without improving backlog execution.
