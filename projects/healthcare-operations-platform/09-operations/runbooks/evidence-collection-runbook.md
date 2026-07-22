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
