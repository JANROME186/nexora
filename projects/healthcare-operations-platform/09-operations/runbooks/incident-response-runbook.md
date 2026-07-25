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
`environment-matrix.md`'s approval chains). Treat evidence preservation as equally urgent to
mitigation when data integrity or a breach is implicated.

## Known gaps and forward pointers

- No alerting backend pages an on-call operator automatically; detection today is manual. Forward
  pointer: `COM-MOD-012-BE-001` plus a future alerting-infrastructure backlog item.
- No incident-tracking system integration exists; this runbook's evidence trail is the record of
  truth until one is adopted. Forward pointer: `BCM-PLT-009` workflow automation.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUNBOOK-INCIDENT-001
  type: operational-runbook
  name: Incident Response Runbook
  version: 1.0.0
  status: active
  backlog_item: COM-MOD-012-OPS-002
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  human_readable: incident-response-runbook.md
  machine_readable: incident-response-runbook.md
  owner: Nexora Platform Operations
  date: 2026-07-22
purpose: 'Give the on-call operator a single decision tree from first detection of
  a HOP operational incident through triage, mitigation and handoff to specialized
  runbooks (rollback, restore, tenant-impact triage, evidence collection, post-incident
  review). This is the hub runbook for BCM-PLT-009''s operational incident workflow.

  '
applicable_environments:
  local: executable_for_drills_and_local_debugging
  dev: applicable
  qa: applicable
  staging: applicable
  prod: applicable_highest_severity_default
capability_traceability:
  BCM-PLT-009:
    responsibility: Owns the incident workflow this runbook documents (states, approvals,
      handoffs).
  BCM-PLT-006:
    responsibility: Primary detection source (health checks, telemetry) that triggers
      most incidents.
  BCM-PLT-007:
    responsibility: Every incident action (mitigation, rollback, restore, access grant)
      must be traceable via audit events.
  BCM-ORG-001:
    responsibility: Tenant scope of the incident must be established early (see tenant-impact-triage-runbook.md).
  BCM-PLT-001:
    responsibility: Emergency break-glass access, if used, is an IAM-governed action
      requiring post-hoc audit review.
severity_classification:
- level: P1_CRITICAL
  definition: Full outage, data integrity risk, or a security/tenant-isolation breach
    affecting any tenant.
  response_target: immediate_all_hands
- level: P2_HIGH
  definition: Partial outage or degraded functionality affecting one or more tenants,
    no data integrity risk confirmed.
  response_target: within_1_hour
- level: P3_WARNING
  definition: Isolated or non-customer-facing degradation, SLO at risk but not yet
    breached.
  response_target: within_1_business_day
iam_and_audit:
  required_permissions:
  - platform:incident:manage
  minimum_role: platform_operations_on_call_escalating_to_release_manager_for_P1
  audit_expectation: 'Every mitigation, access grant, rollback trigger or restore
    trigger taken during the incident must be captured in the incident evidence bundle
    (see evidence-collection-runbook.md) and, once BCM-PLT-009 automation exists,
    as native audit events.

    '
prerequisites:
- id: PRE-001
  name: observability-runbook.md and health-readiness-liveness-runbook.md have
    been run to confirm the signal is real
  required_for: incident_declaration
- id: PRE-002
  name: Access to run backup/restore/rollback runbooks if mitigation requires them
  required_for: mitigation
procedure:
- id: INC-STEP-001
  name: Detect and confirm
  detail: 'Run observability-runbook.md and health-readiness-liveness-runbook.md.
    Do not declare an incident on a single unconfirmed alert; confirm with at least
    one independent signal (health probe plus log/metric, or two consecutive probe
    failures).

    '
- id: INC-STEP-002
  name: Classify severity
  detail: Assign P1/P2/P3 per severity_classification above.
- id: INC-STEP-003
  name: Establish tenant impact scope
  reference: tenant-impact-triage-runbook.md
  detail: Determine whether the incident is single-tenant, multi-tenant or platform-wide
    before choosing mitigation.
- id: INC-STEP-004
  name: Contain
  detail: 'Apply the narrowest effective mitigation first: a tenant-specific feature
    flag disable (BCM-PLT-002) is preferred over a full rollback; a full rollback
    is preferred over a database restore, per the escalation order below.

    '
- id: INC-STEP-005
  name: Choose and execute mitigation
  escalation_order:
  - tenant_specific_feature_flag_disable
  - application_rollback_to_previous_image_digest
  - database_restore_from_verified_backup
  references:
  - rollback-incident-handoff-runbook.md
  - restore-runbook.md
- id: INC-STEP-006
  name: Verify mitigation
  reference: health-readiness-liveness-runbook.md
  detail: Re-run health/readiness checks and a relevant smoke_validation entry from
    local-solution-runbook.md (or its environment equivalent) after mitigation.
- id: INC-STEP-007
  name: Collect evidence continuously
  reference: evidence-collection-runbook.md
  detail: Evidence collection runs in parallel with steps 1-6, not only at the end.
- id: INC-STEP-008
  name: Declare incident resolved and schedule post-incident review
  reference: post-incident-review-runbook.md
success_criteria:
- Incident detected, classified, contained with the narrowest effective mitigation,
  and verified resolved within the response_target for its severity.
- Full evidence trail exists from detection through resolution.
failure_criteria:
- Mitigation is applied without first establishing tenant-impact scope.
- A rollback or restore is executed without following rollback-incident-handoff-runbook.md
  or restore-runbook.md.
- No post-incident review is scheduled for a P1 or P2 incident.
evidence_expected:
- Timeline of detection, classification, mitigation steps taken, verification results
  and resolution time, per evidence-collection-runbook.md.
responsible_role: platform_operations_on_call
next_action_if_failed: 'If mitigation does not resolve the incident within the response_target,
  escalate severity one level and broaden the response (add release manager, then
  product owner, per environment-matrix.md approval_required chains for staging/prod).
  If the incident implicates potential data integrity or breach, treat evidence preservation
  as equally urgent to mitigation per evidence-collection-runbook.md.

  '
related_runbooks:
- observability-runbook.md
- health-readiness-liveness-runbook.md
- tenant-impact-triage-runbook.md
- rollback-incident-handoff-runbook.md
- restore-runbook.md
- evidence-collection-runbook.md
- post-incident-review-runbook.md
known_gaps_and_forward_pointers:
- gap: No alerting backend pages an on-call operator automatically; incident detection
    today is manual, driven by running observability-runbook.md.
  forward_pointer: COM-MOD-012-BE-001 closed without an alerting backend; tracked
    as TD-OBS-001 (distributed trace export, provisioned Grafana/Prometheus/Loki stack
    and SLO/SLA alerting backend), owned by a future dedicated observability-infrastructure
    backlog item.
- gap: No incident-tracking system integration exists; this runbook's evidence trail
    is the record of truth until one is adopted.
  forward_pointer: BCM-PLT-009 workflow automation.
closure:
  backlog_item: COM-MOD-012-OPS-002
  status: active
```
