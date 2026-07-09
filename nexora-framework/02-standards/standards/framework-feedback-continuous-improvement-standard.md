# Framework Feedback and Continuous Improvement Standard

**Artifact ID:** `NXF-FWK-FEEDBACK-001`  
**Status:** Approved  
**Machine-readable source:** `framework-feedback-continuous-improvement-standard.yaml`  
**Version:** `1.0.0`

## Purpose

Nexora improves its framework through evidence gathered while agents analyze projects, implement
backlog items, validate modules and close releases.

Agents may propose improvements, but they do not automatically implement them. Framework evolution
is governed by Nexora through a central backlog.

## Feedback Capture

Agents must capture framework feedback when execution reveals:

- Ambiguous or contradictory guidance.
- Missing templates, prompts or standards.
- Repetitive manual work that could be standardized.
- Validation or quality gate gaps.
- Source-of-truth or handoff gaps.
- Automation opportunities.
- Governance gaps.

Project-local feedback lives under:

```text
08-qa/framework-feedback/
```

The project index is:

```text
08-qa/framework-feedback/framework-feedback-index.yaml
```

## Central Backlog

The company-owned framework backlog lives under:

```text
nexora-framework/07-governance/framework-improvement-backlog/
```

Agents may create proposed items there, but implementation requires explicit Nexora assignment.

## Priority

Agents recommend priority using delivery impact, recurrence probability, framework reusability,
implementation effort and risk reduction. Nexora may change priority during triage.

Priority levels:

- `P0_blocking`
- `P1_high`
- `P2_medium`
- `P3_low`

## Rules

- Do not block product backlog delivery for non-critical framework feedback.
- Promote feedback to blocking only when the framework issue prevents safe completion or creates material delivery risk.
- Link every feedback item to concrete evidence.
- Keep feedback agent agnostic and vendor agnostic.
- Do not modify framework standards or prompts just because a feedback item was created.

Framework feedback is a learning loop, not a shortcut around governance.
