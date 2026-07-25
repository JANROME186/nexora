# HOP Post-Incident Review Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Close the loop after any P1/P2 incident: produce a blameless review, confirm root cause, and route
durable follow-ups into `technical-debt-index.md` or the commercial backlog rather than letting
fixes live only in the incident evidence bundle.

## Applicable environment

Applicable across `dev`/`qa`/`staging`/`prod` (mandatory for P1/P2 in `prod`); usable on `local` for
drills.

## Capability traceability

- `BCM-PLT-009` — owns the incident workflow's closure state, formally completed here.
- `BCM-PLT-007` — the review reconciles against the audit trail as the authoritative timeline, not
  operator memory.
- `BCM-PLT-006` — assesses whether observability gaps delayed detection, feeding back into
  `observability-runbook.md`.

## IAM and audit

Requires `platform:incident:review`, held by the release manager or platform operations lead. The
review document itself joins the incident evidence bundle; any corrective action taken as a result
must be audited under its own capability.

## Prerequisites

- The incident is resolved and verified per `incident-response-runbook.md`.
- The evidence bundle from `evidence-collection-runbook.md` is complete.

## Procedure

1. **Reconstruct the timeline** from the evidence bundle and `GET /api/audit/events`, not operator
   recollection.
2. **Identify root cause** using a blameless method (e.g. five whys) — process/tooling/system gaps,
   not individual blame.
3. **Assess detection and response effectiveness**: did observability/health checks catch it before
   customer impact? Was tenant-impact triage run promptly? Was the mitigation the narrowest
   effective one?
4. **Determine tenant and compliance impact**: confirm the final tenant-impact-triage classification
   and whether CLINICAL/FINANCIAL audit-category data was implicated.
5. **Convert every durable follow-up into a tracked item** in `technical-debt-index.md` or the
   commercial backlog — an unregistered "we'll remember to fix this" is a review failure.
6. **Publish the review and close the incident**, storing it in the same evidence bundle path.

## Success criteria

Blameless root-cause analysis reconciled against audit events; detection/response assessed against
the runbooks actually used; every durable follow-up registered with an owner.

## Failure criteria

Review skipped for P1/P2; root cause assigns individual blame instead of identifying gaps;
follow-ups identified but never registered as tracked items.

## Evidence expected

Published review (timeline, root cause, detection/response assessment, tenant/compliance impact,
follow-ups with owners), stored in the incident's evidence bundle.

## Responsible role

Release manager or platform operations lead.

## If this fails

If a review cannot complete within a reasonable window (target: 5 business days for P1, 10 for P2),
escalate to the product owner and schedule it explicitly rather than letting it lapse.

## Known gaps and forward pointers

- No dedicated post-incident-review template or tracking system exists beyond this runbook's
  procedure; reviews are plain files in the evidence bundle. Forward pointer: `BCM-PLT-008`
  Document Management implementation.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUNBOOK-POSTMORTEM-001
  type: operational-runbook
  name: Post-Incident Review Runbook
  version: 1.0.0
  status: active
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: post-incident-review-runbook.md
  machine_readable: post-incident-review-runbook.md
  owner: Nexora Platform Operations
  date: 2026-07-22
purpose: 'Close the loop after any P1/P2 incident (and, at the operator''s discretion,
  any P3): produce a blameless review, confirm root cause, and route durable follow-ups
  into technical-debt-index.md or the commercial backlog rather than letting fixes
  live only in the incident evidence bundle.

  '
applicable_environments:
  local: applicable_for_drills
  dev: applicable
  qa: applicable
  staging: applicable
  prod: applicable_mandatory_for_P1_and_P2
capability_traceability:
  BCM-PLT-009:
    responsibility: Owns the incident workflow's closure state, which this runbook
      formally completes.
  BCM-PLT-007:
    responsibility: The review must reconcile against the audit trail as the authoritative
      timeline source, not just operator memory.
  BCM-PLT-006:
    responsibility: Review should assess whether observability gaps delayed detection
      or diagnosis, feeding back into observability-runbook.md known_gaps.
iam_and_audit:
  required_permissions:
  - platform:incident:review
  minimum_role: release_manager_or_platform_operations_lead
  audit_expectation: The review document itself becomes part of the incident evidence
    bundle; no separate audit event is required for the review meeting itself, but
    any corrective action taken as a result must be individually audited under its
    own capability.
prerequisites:
- id: PRE-001
  name: The incident is resolved and verified per incident-response-runbook.md
  required_for: starting_review
- id: PRE-002
  name: The evidence bundle from evidence-collection-runbook.md is complete
  required_for: accurate_root_cause_analysis
procedure:
- id: PIR-STEP-001
  name: Reconstruct the timeline from the evidence bundle and audit events
  reference: evidence-collection-runbook.md
  command: Invoke-RestMethod "http://localhost:8080/api/audit/events" -Headers @{
    "X-Tenant-Id" = "<tenant-id>" }
  expected_result: Timeline in the review matches audit-event timestamps, not just
    operator recollection.
- id: PIR-STEP-002
  name: Identify root cause using a blameless "five whys" or equivalent method
  detail: Focus on process, tooling and system gaps, not individual blame.
- id: PIR-STEP-003
  name: Assess detection and response effectiveness
  detail: 'Did observability-runbook.md / health-readiness-liveness-runbook.md
    catch the issue before customer impact? Was tenant-impact-triage-runbook.md
    run promptly? Was the chosen mitigation the narrowest effective one per incident-response-runbook.md
    escalation_order?

    '
- id: PIR-STEP-004
  name: Determine tenant and compliance impact
  detail: Confirm final tenant-impact-triage-runbook.md classification and whether
    any BCM-PLT-007 CLINICAL or FINANCIAL category data was implicated, requiring
    compliance notification review.
- id: PIR-STEP-005
  name: Convert every durable follow-up into a tracked item
  detail: 'A follow-up that is not registered in 08-qa/technical-debt/technical-debt-index.md
    (for quality/architecture gaps) or the commercial backlog (for missing capability)
    will not survive past this incident. Ad hoc "we''ll remember to fix this" outcomes
    are a review failure.

    '
- id: PIR-STEP-006
  name: Publish the review and close the incident
  working_directory: repository_root
  detail: Store the review under the same evidence bundle path as the incident, then
    mark the incident closed in incident-response-runbook.md's records.
success_criteria:
- A blameless root-cause analysis exists, reconciled against audit events.
- Detection and response effectiveness assessed against the runbooks actually used.
- Every durable follow-up is registered in technical-debt-index.md or the commercial
  backlog with an owner.
failure_criteria:
- Review is skipped for a P1 or P2 incident.
- Root cause analysis assigns blame to an individual instead of identifying process/system
  gaps.
- Follow-ups are identified but never registered as tracked technical debt or backlog
  items.
evidence_expected:
- Published review document (timeline, root cause, detection/response assessment,
  tenant/compliance impact, follow-up items with owners), stored in the incident's
  evidence bundle.
responsible_role: release_manager_or_platform_operations_lead
next_action_if_failed: 'If a review cannot be completed within a reasonable window
  after resolution (target: within 5 business days for P1, 10 for P2), escalate to
  the product owner and schedule it explicitly rather than letting it lapse silently.

  '
related_runbooks:
- incident-response-runbook.md
- evidence-collection-runbook.md
- tenant-impact-triage-runbook.md
known_gaps_and_forward_pointers:
- gap: No dedicated post-incident-review template repository or tracking system exists
    beyond this runbook's procedure; reviews are stored as plain files in the evidence
    bundle.
  forward_pointer: BCM-PLT-008 Document Management implementation.
closure:
  backlog_item: COM-MOD-012-OPS-002
  status: active
```
