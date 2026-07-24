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
- Complete the PIR runbook (`post-incident-review-runbook.yaml`).
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
5. Evidence Collection Runbook log (`09-operations/runbooks/evidence-collection-runbook.yaml`).

---

## Severity Promotion & Demotion Policy

- **Promote to P1**: If a P2/P3 incident begins impacting core clinical results release or tenant isolation boundaries, the IC must immediately reclassify the ticket to P1 and activate the Executive Escalation chain.
- **Demote to P2**: If an effective operational workaround is established for a P1 incident (e.g., routing traffic to secondary branch infrastructure) that removes immediate risk to patient safety or billing, the IC may demote the incident to P2 while the permanent fix compiles.

---

## Traceability & Standards

- **Capabilities**: [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.yaml), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.yaml), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.yaml)
- **Runbook Integration**: Integrated with `09-operations/runbooks/incident-response-runbook.yaml`, `tenant-impact-triage-runbook.yaml`, `evidence-collection-runbook.yaml`, and `post-incident-review-runbook.yaml`.
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes
