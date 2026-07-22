# HOP Post-Incident Review Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

Close the loop after any P1/P2 incident: produce a blameless review, confirm root cause, and route
durable follow-ups into `technical-debt-index.yaml` or the commercial backlog rather than letting
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
5. **Convert every durable follow-up into a tracked item** in `technical-debt-index.yaml` or the
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
