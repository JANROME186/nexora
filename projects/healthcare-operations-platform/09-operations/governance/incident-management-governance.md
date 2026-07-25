# HOP Incident Management Governance Specification

## Purpose

This document establishes the end-to-end governance framework for detecting, containing, remediating, and reviewing operational incidents within the **Healthcare Operations Platform (HOP)**. It ensures structured response, clear role accountability, strict evidence collection, and blameless post-incident improvement.

---

## Incident Lifecycle Phases

```
[ 1. Detection & Triage ] ──► [ 2. Containment ] ──► [ 3. Remediation ]
                                                             │
[ 5. Post-Incident Review ] ◄── [ 4. Resolution & Check ] ◄──┘
```

### Phase 1: Detection & Triage (Target: $\le 15\text{ mins}$)
- Incident detected via Prometheus/Actuator metrics alert, automated container check, or customer support ticket.
- L1 Helpdesk assigns preliminary priority (P1–P4).
- For P1/P2 incidents, an **Incident Commander (IC)** is immediately designated and the war room bridge opened.

### Phase 2: Containment & Triage (Target: $\le 30\text{ mins}$)
- Isolate the scope of failure (single tenant vs. platform-wide, specific branch vs. global).
- Apply immediate operational compensating controls using runbook `tenant-impact-triage-runbook`.
- Enable feature flag toggles or circuit breakers via BCM-PLT-002 if a specific sub-module is failing.

### Phase 3: Remediation & Recovery (Target: $\le 90\text{ mins}$)
- Implement hotfix patch or trigger automated blue/green container rollback using runbook `rollback-incident-handoff-runbook`.
- Verify database migration compatibility before applying schema patches.
- Validate readiness probes (`/actuator/health/readiness`) return `UP (200 OK)`.

### Phase 4: Resolution & Verification (Target: $\le 15\text{ mins}$)
- Monitor HTTP 5xx error rates and p95 latency for 15 consecutive minutes to confirm stability.
- Confirm patient report delivery, order intake, and payment billing request workflows operate without errors.
- Close the active incident ticket and notify affected customers.

### Phase 5: Post-Incident Review (PIR) (Target: $\le 3\text{ business days}$)
- Conduct a Blameless Post-Incident Review meeting.
- Complete the PIR runbook (`post-incident-review-runbook.md`).
- Register actionable technical debt items under `08-qa/technical-debt/` for root cause remediation.

---

## Role & Responsibility Matrix

| Role | Primary Responsibility | Key Actions |
|---|---|---|
| **Incident Commander (IC)** | Incident Lead & Bridge Authority | Assigns tasks, authorizes rollbacks/hotfixes, directs war room calls, approves customer comms. |
| **Technical Lead** | Technical Triage & Code Fix | Analyzes MDC trace logs, inspects database locks, compiles hotfix patches, runs unit/integration tests. |
| **Communications Lead** | Stakeholder & Customer Updates | Maintains status page updates, issues executive briefings, coordinates customer success outreach. |
| **L2 Support Lead** | Log & Metrics Triage | Extracts Actuator/Prometheus metrics, runs tenant isolation scripts, assists customer workarounds. |

---

## Mandatory Evidence Collection

During any P1 or P2 incident, the Technical Lead must preserve an immutable evidence package containing:
1. Actuator Health Probe output (`GET /actuator/health`).
2. MDC Log Excerpt featuring `traceId`, `tenantId`, and `userId` correlation fields.
3. PostgreSQL activity snapshot (`pg_stat_activity` query log during incident).
4. Running Git commit hash and Docker image manifest.
5. Evidence Collection Runbook log (`09-operations/runbooks/evidence-collection-runbook.md`).

---

## Severity Promotion & Demotion Policy

- **Promote to P1**: If a P2/P3 incident begins impacting core clinical results release or tenant isolation boundaries, the IC must immediately reclassify the ticket to P1 and activate the Executive Escalation chain.
- **Demote to P2**: If an effective operational workaround is established for a P1 incident (e.g., routing traffic to secondary branch infrastructure) that removes immediate risk to patient safety or billing, the IC may demote the incident to P2 while the permanent fix compiles.

---

## Traceability & Standards

- **Capabilities**: [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.md), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.md), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.md)
- **Runbook Integration**: Integrated with `09-operations/runbooks/incident-response-runbook.md`, `tenant-impact-triage-runbook.md`, `evidence-collection-runbook.md`, and `post-incident-review-runbook.md`.
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GOV-INC-001
  type: operational-governance-specification
  name: HOP Incident Management Governance Specification
  version: 1.0.0
  status: approved
  human_readable: incident-management-governance.md
  machine_readable: incident-management-governance.md
  backlog_item: COM-MOD-016-OPS-001
  created_date: 2026-07-24
  owner: HOP Operations Governance & Security Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
incident_lifecycle:
  phases:
  - phase: 1_Detection_and_Triage
    description: Automated health check failure, alert trigger, or user ticket received.
      Severity classified (P1-P4).
    max_duration_minutes: 15
    output: Incident ticket created, Incident Commander assigned for P1/P2.
  - phase: 2_Containment_and_Triage
    description: Isolate failure impact, apply tenant containment, activate fallback/circuit
      breaker, preserve diagnostic evidence.
    max_duration_minutes: 30
    runbook_reference: 09-operations/runbooks/tenant-impact-triage-runbook.md
  - phase: 3_Remediation_and_Recovery
    description: Deploy hotfix, execute configuration adjustment, or perform rollback.
      Validate readiness probe restoration.
    max_duration_minutes: 90
    runbook_reference: 09-operations/runbooks/rollback-incident-handoff-runbook.md
  - phase: 4_Resolution_Verification
    description: Confirm full service restoration across affected tenants, verify
      backend/frontend error rate return to baseline.
    max_duration_minutes: 15
    output: Ticket marked Resolved; evidence bundle assembled.
  - phase: 5_Post_Incident_Review
    description: Conduct blameless PIR, document root cause, log actionable technical
      debt items, publish customer summary.
    max_duration_days: 3
    runbook_reference: 09-operations/runbooks/post-incident-review-runbook.md
key_roles:
  incident_commander:
    responsibility: Overall incident authority, war room leadership, triage direction,
      and escalation management.
  technical_lead:
    responsibility: Technical diagnosis, code/db investigation, patch creation, and
      technical remediation.
  communications_lead:
    responsibility: Status page updates, customer notifications, executive briefings,
      and internal alignment.
evidence_collection_mandate:
  required_artifacts:
  - Actuator metrics snapshot (/actuator/prometheus)
  - Health probe status history (/actuator/health)
  - MDC trace log snippet (traceId, tenantId, userId correlation)
  - Database query log or locking trace
  - Git commit SHA and deployment version running at failure time
  - Customer impact matrix (affected tenants, branches, order IDs)
  evidence_runbook: 09-operations/runbooks/evidence-collection-runbook.md
severity_reclassification_rules:
  upgrade:
  - Trigger: P2/P3 incident affecting multiple tenants or spreading across core clinical
      workflows.
  - Action: Immediately upgrade to P1; notify Incident Commander and activate Emergency
      Bridge.
  downgrade:
  - Trigger: Workaround applied that restores clinical/financial workflow while permanent
      fix is prepared.
  - Action: Demote P1 to P2; update status page and maintain active monitoring queue.
traceability:
  capabilities:
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-007
  standards_compliance:
    agent_agnostic: true
    open_source_first: true
    no_proprietary_agent_dependencies: true
```
