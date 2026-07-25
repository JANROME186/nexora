# Framework Feedback and Continuous Improvement Standard

**Artifact ID:** `NXF-FWK-FEEDBACK-001`
**Status:** Approved
**Machine-readable source:** `framework-feedback-continuous-improvement-standard.md`
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
08-qa/framework-feedback/framework-feedback-index.md
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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-FWK-FEEDBACK-001
  type: framework-standard
  name: Framework Feedback and Continuous Improvement Standard
  version: 1.0.0
  status: approved
  human_readable: framework-feedback-continuous-improvement-standard.md
  machine_readable: framework-feedback-continuous-improvement-standard.md
  owner: Nexora Engineering
purpose: 'Define how agents capture practical execution feedback from project analysis,
  backlog implementation, validation and closeout so Nexora can continuously improve
  the framework through a governed company-owned backlog.

  '
principles:
- Framework feedback is evidence, not an automatic framework change.
- Product backlog execution must not be blocked by non-critical framework improvement
  ideas.
- Agents may propose framework improvements, but Nexora owns prioritization and implementation.
- Feedback must be grounded in concrete execution experience, not generic preference.
- Feedback must remain agent agnostic, vendor agnostic and project portable.
- YAML is the machine-readable source; Markdown is the human-readable companion when
  useful.
feedback_capture_policy:
  required_when:
  - new_project_analysis_completes
  - framework_compliance_validation_completes
  - code_changing_backlog_item_completes
  - module_closeout_completes
  - release_readiness_completes
  - agent_detects_framework_ambiguity_or_repeated_friction
  optional_when:
  - exploratory_analysis_without_repository_changes
  - user_requests_framework_review
  not_required_when:
  - purely_informational_question
  - no_project_or_framework_artifact_was_loaded
  project_feedback_root: 08-qa/framework-feedback/
  project_feedback_index: 08-qa/framework-feedback/framework-feedback-index.md
  project_feedback_item_pattern: 08-qa/framework-feedback/<feedback-id>.yaml
  feedback_id_pattern: FWF-<project-or-module>-<sequence>
central_framework_backlog:
  root_folder: nexora-framework/07-governance/framework-improvement-backlog/
  index_file: nexora-framework/07-governance/framework-improvement-backlog/framework-improvement-backlog.md
  human_readable_index: nexora-framework/07-governance/framework-improvement-backlog/README.md
  item_path_pattern: nexora-framework/07-governance/framework-improvement-backlog/items/<item-id>.yaml
  item_id_pattern: FWI-<area>-<sequence>
  owner: Nexora Engineering
  agents_may_create_items: true
  agents_may_implement_items: false_unless_explicitly_assigned_by_nexora
  company_triage_required: true
feedback_item_must_include:
- id
- source_project
- source_backlog_item_or_phase
- detection_date
- feedback_type
- affected_framework_area
- observed_friction_or_gap
- evidence
- proposed_improvement
- expected_benefit
- impacted_project_types
- priority_recommendation
- impact_assessment
- urgency
- estimated_effort
- risk_if_not_addressed
- implementation_owner
- status
feedback_types:
- ambiguity
- missing_template
- missing_prompt
- missing_standard
- contradictory_guidance
- repetitive_agent_work
- validation_gap
- quality_gate_gap
- source_of_truth_gap
- generated_artifact_gap
- onboarding_or_handoff_gap
- automation_opportunity
- governance_gap
priority_model:
  priority_levels:
  - P0_blocking
  - P1_high
  - P2_medium
  - P3_low
  scoring_dimensions:
    impact_on_project_delivery:
      weight: 35
      values:
        low: 1
        medium: 2
        high: 3
        blocking: 4
    recurrence_probability:
      weight: 25
      values:
        one_off: 1
        occasional: 2
        frequent: 3
        systemic: 4
    framework_reusability:
      weight: 20
      values:
        project_specific: 1
        project_family: 2
        many_projects: 3
        all_projects: 4
    implementation_effort_inverse:
      weight: 10
      values:
        large: 1
        medium: 2
        small: 3
        trivial: 4
    risk_reduction:
      weight: 10
      values:
        low: 1
        medium: 2
        high: 3
        critical: 4
  guidance: 'Agents recommend priority. Nexora may change priority during company
    triage based on strategy, budget, product roadmap and cross-project impact.

    '
central_backlog_item_statuses:
- proposed
- accepted
- prioritized
- scheduled
- in_progress
- implemented
- rejected_with_reason
- superseded
- deferred
agent_rules:
- Agents must capture framework feedback when execution reveals ambiguity, repeated
  manual effort, missing templates, missing prompts, missing validations or contradictory
  guidance.
- Agents must not modify framework standards or prompts solely because they created
  a feedback item.
- Agents must not block a product backlog item for non-critical framework feedback.
- Agents must promote feedback to a blocking gap only when the framework issue prevents
  safe completion, causes contradictory instructions, or would create material delivery
  risk.
- Agents must link feedback to concrete evidence such as files touched, validation
  results, closeout notes, missing artifact names or repeated manual steps.
- Agents must update the project feedback index when project-local feedback is created.
- Agents may add proposed items to the central framework improvement backlog, but
  implementation requires explicit Nexora/company assignment.
- Agents must keep feedback agent agnostic and avoid references to a specific AI product,
  vendor or runtime unless the issue is explicitly about an approved external integration.
human_triage_rules:
- Nexora reviews central backlog items periodically.
- Nexora may merge duplicate feedback from multiple projects into a single framework
  improvement item.
- Nexora assigns implementation priority based on cross-project impact, delivery risk,
  quality improvement and strategic roadmap alignment.
- Accepted framework improvements must reference the feedback items that motivated
  them.
- Implemented framework improvements must include validation evidence and update source-of-truth
  registries.
evidence_requirements:
  project_feedback_index_must_include:
  - feedback_id
  - source_backlog_item_or_phase
  - feedback_type
  - affected_framework_area
  - priority_recommendation
  - central_backlog_item
  - status
  central_backlog_index_must_include:
  - item_id
  - title
  - source_feedback_ids
  - affected_framework_area
  - priority
  - expected_impact
  - status
  - owner
  - target_decision
non_goals:
- This standard does not create an automatic self-modifying framework.
- This standard does not allow product backlog agents to implement framework changes
  without assignment.
- This standard does not replace project technical debt; project technical debt remains
  under 08-qa/technical-debt/.
- This standard does not require feedback when an execution had no framework learning.
```
