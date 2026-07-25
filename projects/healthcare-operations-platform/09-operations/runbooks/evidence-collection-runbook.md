# HOP Incident and Operations Evidence Collection Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Defines what evidence every other runbook in this folder must produce, where it is stored, and how
it is indexed, so observability checks, backups, restores, incidents and rollbacks all leave a
consistent, auditable trail. Referenced, not duplicated, by every other runbook here.

## Applicable environment

Executable today on `local`; applicable unchanged across `dev`/`qa`/`staging`/`prod`, with the
highest retention requirement in `prod`.

## Capability traceability

- `BCM-PLT-007` — audit events remain the primary compliance-grade evidence source; these bundles
  are the operational supplement, not a replacement.
- `BCM-PLT-008` — owns the eventual formal storage/retention of operational documents; until
  implemented, evidence lives under `08-qa/` by repository convention.
- `BCM-PLT-006` — source of telemetry-derived evidence referenced by the observability runbooks.

## IAM and audit

Requires `platform:evidence:write`. Evidence collection itself needs no separate audit event, but
the actions it documents (backup, restore, rollback, mitigation) do, per their own runbooks.

## Evidence bundle structure

Root: `08-qa/qa/platform-hardening-and-saas-operations/incidents/<incident-or-activity-id>/`

- `timeline.md` or `.yaml` — chronological log of detection, classification, actions and resolution.
- `command_outputs/` — raw command output captured from each executed runbook step.
- `queries_and_results/` — SQL/audit-event/telemetry query text and result excerpts.
- `approvals/` — recorded approvals for live restores, rollbacks or emergency access.
- `checksums/` — SHA-256 hashes for any backup/restore artifact referenced.

Retention: matches `BCM-PLT-007`'s `AuditRetentionPolicy` default of 7 years for
compliance-relevant incidents; routine operational checks retained at least 1 year or until
superseded.

## Prerequisites

- The activity being documented has an id (incident id, backup timestamp, release id).
- Write access to `08-qa/qa/platform-hardening-and-saas-operations/`.

## Procedure

1. `New-Item -ItemType Directory -Force
   projects/healthcare-operations-platform/08-qa/qa/platform-hardening-and-saas-operations/incidents/<activity-id>`
   — create the bundle folder.
2. Capture each executed runbook step's command and output into `command_outputs/` (required for
   real activities, not routine local drills).
3. Record a chronological timeline: UTC timestamp, actor, action, outcome.
4. Attach checksums for any backup/restore artifact referenced.
5. Attach approval records for any live restore, rollback or emergency access.

## Success criteria

Every P1/P2 incident, every live restore, and every production rollback has a complete evidence
bundle, with checksums and approvals present where required.

## Failure criteria

An incident is closed without a corresponding evidence bundle; a live restore or rollback proceeds
without a captured approval record.

## Evidence expected

This runbook is the definition of evidence expected by all others; its own evidence is the
existence and completeness of the bundles it defines.

## Responsible role

Platform operations on-call.

## If this fails

If evidence cannot be captured in real time during a P1 incident, reconstruct the timeline and
command history immediately after resolution, before starting the post-incident review, and note
the reconstruction gap explicitly rather than presenting it as contemporaneous.

## Known gaps and forward pointers

- No automated evidence-capture tooling exists; bundles are assembled manually. Forward pointer:
  `BCM-PLT-008` Document Management implementation and `BCM-PLT-009` workflow automation.
- No formal document-management retention/export system exists yet. Forward pointer:
  `COM-MOD-012-BE-001` and subsequent `BCM-PLT-008` implementation.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUNBOOK-EVIDENCE-001
  type: operational-runbook
  name: Incident and Operations Evidence Collection Runbook
  version: 1.0.0
  status: active
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: evidence-collection-runbook.md
  machine_readable: evidence-collection-runbook.md
  owner: Nexora Platform Operations
  date: 2026-07-22
purpose: 'Define what evidence every other runbook in this folder must produce, where
  it is stored, and how it is indexed, so that observability checks, backups, restores,
  incidents and rollbacks all leave a consistent, auditable trail. This runbook is
  referenced, not duplicated, by every other runbook in 09-operations/runbooks/.

  '
applicable_environments:
  local: executable_today
  dev: applicable
  qa: applicable
  staging: applicable
  prod: applicable_highest_retention_requirement
capability_traceability:
  BCM-PLT-007:
    responsibility: Audit events are the primary compliance-grade evidence source
      (SECURITY, CLINICAL, FINANCIAL, OPERATIONAL categories); this runbook's evidence
      bundles are the operational supplement, not a replacement.
  BCM-PLT-008:
    responsibility: Owns the eventual formal storage/retention of operational documents
      and generated runbooks; until implemented, evidence is stored under 08-qa/ per
      repository convention.
  BCM-PLT-006:
    responsibility: Source of telemetry-derived evidence (health checks, logs) referenced
      by observability-runbook.md and metrics-logs-traces-validation-runbook.md.
iam_and_audit:
  required_permissions:
  - platform:evidence:write
  minimum_role: platform_operations_on_call
  audit_expectation: 'Evidence collection itself does not require a separate audit
    event, but the underlying actions it documents (backup, restore, rollback, incident
    mitigation) do, per each runbook''s own iam_and_audit section.

    '
evidence_bundle_structure:
  root_path_pattern: 08-qa/qa/platform-hardening-and-saas-operations/incidents/<incident-or-activity-id>/
  required_contents:
  - timeline.md_or_yaml: Chronological log of detection, classification, actions taken
      and resolution.
  - command_outputs/: Raw command output captured from each runbook step executed.
  - queries_and_results/: Any SQL, audit-event or telemetry query text and its result
      excerpt.
  - approvals/: Recorded approvals for live restores, rollbacks or emergency access.
  - checksums/: SHA-256 or equivalent hashes for any backup/restore artifact referenced.
  retention_policy:
    minimum_retention: matches_BCM-PLT-007_AuditRetentionPolicy_default_of_7_years_for_incidents_with_compliance_relevance
    routine_operational_checks: retained_at_least_1_year_or_until_superseded_by_the_next_equivalent_check
prerequisites:
- id: PRE-001
  name: The activity being documented has an id (incident id, backup timestamp, deployment
    release id)
  required_for: bundle_naming
- id: PRE-002
  name: Write access to 08-qa/qa/platform-hardening-and-saas-operations/
  required_for: evidence_storage
procedure:
- id: EVID-STEP-001
  name: Create the evidence bundle folder for the activity
  working_directory: repository_root
  command: New-Item -ItemType Directory -Force projects/healthcare-operations-platform/08-qa/qa/platform-hardening-and-saas-operations/incidents/<activity-id>
  expected_result: Folder created.
- id: EVID-STEP-002
  name: Capture each executed runbook step's command and output into command_outputs/
  detail: Every STEP-level command in observability, health, telemetry, backup, restore,
    incident, rollback and tenant-triage runbooks must have its output captured here
    when executed for a real activity (not required for routine local drills).
- id: EVID-STEP-003
  name: Record a chronological timeline
  detail: One line per action with UTC timestamp, actor, action, and outcome.
- id: EVID-STEP-004
  name: Attach checksums for any backup/restore artifact referenced
  reference: backup-runbook.md, restore-runbook.md
- id: EVID-STEP-005
  name: Attach approval records for any live restore, rollback or emergency access
  reference: restore-runbook.md, rollback-incident-handoff-runbook.md
success_criteria:
- Every P1/P2 incident, every live restore, and every production rollback has a complete
  evidence bundle per evidence_bundle_structure.
- Checksums and approvals are present where the source runbook requires them.
failure_criteria:
- An incident is closed (post-incident-review-runbook.md) without a corresponding
  evidence bundle.
- A live restore or rollback proceeds without an approval record captured.
evidence_expected:
- This runbook is itself the definition of evidence expected by all other runbooks;
  its own evidence is the existence and completeness of the bundles it defines.
responsible_role: platform_operations_on_call
next_action_if_failed: 'If evidence cannot be captured in real time during a P1 incident
  (operator is fully occupied with mitigation), reconstruct the timeline and command
  history immediately after resolution, before starting post-incident-review-runbook.md,
  and note the reconstruction gap explicitly rather than presenting it as contemporaneous.

  '
related_runbooks:
- observability-runbook.md
- health-readiness-liveness-runbook.md
- metrics-logs-traces-validation-runbook.md
- backup-runbook.md
- restore-runbook.md
- incident-response-runbook.md
- rollback-incident-handoff-runbook.md
- tenant-impact-triage-runbook.md
- post-incident-review-runbook.md
known_gaps_and_forward_pointers:
- gap: No automated evidence-capture tooling exists; bundles are assembled manually
    by the operator.
  forward_pointer: BCM-PLT-008 Document Management implementation and BCM-PLT-009
    workflow automation.
- gap: No formal document-management retention/export system exists yet; evidence
    lives as repository files under 08-qa/.
  forward_pointer: COM-MOD-012-BE-001 and subsequent BCM-PLT-008 implementation.
closure:
  backlog_item: COM-MOD-012-OPS-002
  status: active
```
