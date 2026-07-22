# HOP Incident Response Runbook

Backlog item: `COM-MOD-012-OPS-002`

## Purpose

The hub runbook: a single decision tree from first detection of a HOP operational incident through
triage, mitigation and handoff to the specialized runbooks (rollback, restore, tenant-impact
triage, evidence collection, post-incident review). Documents `BCM-PLT-009`'s operational incident
workflow.

## Applicable environment

Applicable to `dev`/`qa`/`staging`/`prod`; also usable on `local` for drills.

## Capability traceability

- `BCM-PLT-009` — owns the incident workflow (states, approvals, handoffs).
- `BCM-PLT-006` — primary detection source (health checks, telemetry).
- `BCM-PLT-007` — every incident action must be traceable via audit events.
- `BCM-ORG-001` — tenant scope must be established early.
- `BCM-PLT-001` — emergency break-glass access, if used, requires post-hoc audit review.

## Severity classification

| Level | Definition | Response target |
|---|---|---|
| P1 Critical | Full outage, data-integrity risk, or a security/tenant-isolation breach | Immediate, all hands |
| P2 High | Partial outage or degraded functionality for one or more tenants, no confirmed data-integrity risk | Within 1 hour |
| P3 Warning | Isolated/non-customer-facing degradation, SLO at risk but not breached | Within 1 business day |

## IAM and audit

Requires `platform:incident:manage`, held by platform operations on-call, escalating to the release
manager for P1. Every mitigation, access grant, rollback or restore trigger must be captured in the
incident evidence bundle.

## Prerequisites

- `observability-runbook.md` and `health-readiness-liveness-runbook.md` have confirmed the signal
  is real.
- Access to run backup/restore/rollback runbooks if mitigation requires them.

## Procedure

1. **Detect and confirm.** Run observability and health checks; do not declare an incident on a
   single unconfirmed alert.
2. **Classify severity** per the table above.
3. **Establish tenant impact scope** via `tenant-impact-triage-runbook.md` before choosing
   mitigation.
4. **Contain** with the narrowest effective action first.
5. **Choose and execute mitigation**, in escalation order:
   1. Tenant-specific feature-flag disable (`BCM-PLT-002`).
   2. Application rollback to the previous image digest (`rollback-incident-handoff-runbook.md`).
   3. Database restore from a verified backup (`restore-runbook.md`).
6. **Verify mitigation** by re-running health/readiness checks and a relevant smoke-validation
   entry.
7. **Collect evidence continuously**, in parallel with the steps above, per
   `evidence-collection-runbook.md`.
8. **Declare resolved and schedule a post-incident review** via `post-incident-review-runbook.md`.

## Success criteria

Detected, classified, contained with the narrowest effective mitigation, and verified resolved
within the severity's response target, with a full evidence trail.

## Failure criteria

Mitigation applied before establishing tenant-impact scope; a rollback or restore executed outside
its dedicated runbook; no post-incident review scheduled for a P1/P2 incident.

## Evidence expected

Timeline of detection, classification, mitigation steps, verification results and resolution time.

## Responsible role

Platform operations on-call.

## If this fails

If mitigation does not resolve the incident within its response target, escalate severity one level
and broaden the response (release manager, then product owner, mirroring
`environment-matrix.yaml`'s approval chains). Treat evidence preservation as equally urgent to
mitigation when data integrity or a breach is implicated.

## Known gaps and forward pointers

- No alerting backend pages an on-call operator automatically; detection today is manual. Forward
  pointer: `COM-MOD-012-BE-001` plus a future alerting-infrastructure backlog item.
- No incident-tracking system integration exists; this runbook's evidence trail is the record of
  truth until one is adopted. Forward pointer: `BCM-PLT-009` workflow automation.
