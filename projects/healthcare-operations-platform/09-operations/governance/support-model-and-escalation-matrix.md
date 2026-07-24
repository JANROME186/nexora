# HOP Support Model (L1/L2/L3) and Escalation Matrix

## Overview

This document specifies the operational support model, escalation matrix, and shift handover procedures for the **Healthcare Operations Platform (HOP)**. It establishes clear boundaries and SLAs for Level 1 (L1), Level 2 (L2), and Level 3 (L3) support personnel to guarantee high availability, rapid response, and transparent governance for commercial healthcare customers.

## Support Architecture (L1/L2/L3)

```
[ Customer / User ]
        │
        ▼
[ L1 Helpdesk & Triage ] ──(SOP / Known Fix)──► Resolution
        │
        │ (Unresolved / Technical Triage)
        ▼
[ L2 Operational Support ] ──(Config / Runbook)──► Resolution
        │
        │ (Code Defect / Core Patch)
        ▼
[ L3 Product Engineering ] ──(Hotfix / Release)──► Resolution & PIR
```

### Level 1 (L1): Customer Helpdesk & Intake
- **Scope**: First point of contact for customer operational personnel, clinic staff, and portal users.
- **Responsibilities**:
  - Receive and log customer tickets via open-source service desk interfaces.
  - Classify incident severity (P1 through P4) based on predefined criteria.
  - Perform basic user assistance, password resets, browser/device compatibility checks.
  - Apply standard operating procedures (SOPs) from the customer onboarding guides.
  - Escalate to L2 within **15 minutes** if unresolved by SOPs.
- **Coverage**: 24/7 for P1/P2 incidents; standard business hours (08:00–18:00 local) for P3/P4.

### Level 2 (L2): HOP Operational Support Engineering
- **Scope**: Advanced technical support, log analysis, configuration management, and tenant operational triage.
- **Responsibilities**:
  - Inspect Spring MDC trace logs, Actuator metrics (`/actuator/prometheus`), and PostgreSQL health probes.
  - Execute operational runbooks (`tenant-impact-triage-runbook`, `health-readiness-liveness-runbook`).
  - Manage tenant feature flags and platform configurations via BCM-PLT-002 endpoints.
  - Troubleshoot data migration and open data ingestion jobs (BCM-PLT-010).
  - Escalate code defects or core infrastructure failures to L3 within **30 minutes**.
- **Coverage**: 24/7 on-call rotation for P1; extended hours (08:00–20:00 local) for P2–P4.

### Level 3 (L3): Core Product Engineering & Architecture
- **Scope**: Code-level investigation, bug fixes, hotfix generation, and architecture updates.
- **Responsibilities**:
  - Deep-dive code analysis across Spring Modulith backend, React employee portal, public website, and mobile components.
  - Author, test, and release emergency hotfixes following `rollback-and-hotfix-governance`.
  - Perform database schema fix deployments and migration rollbacks.
  - Conduct Blameless Post-Incident Reviews (PIR) for P1 and P2 incidents.
- **Coverage**: 24/7 emergency escalation for P1/P2; business hours for P3/P4 technical debt and fixes.

---

## Escalation Matrix

| Priority | Definition | L1 Target | L2 Target | L3 Target | Management Notice | Exec Briefing |
|---|---|---|---|---|---|---|
| **P1 - Critical** | Complete system outage, data loss risk, or result delivery blocked platform-wide. | 15 mins | 15 mins | 30 mins | 15 mins | Hourly |
| **P2 - High** | Major capability degraded (e.g., sample intake or billing requests failing), no workaround. | 30 mins | 60 mins | 120 mins | 60 mins | Every 4 hrs |
| **P3 - Medium** | Minor workflow issue with an available operational workaround. | 120 mins | 240 mins | 480 mins | 240 mins | Daily |
| **P4 - Low** | Cosmetic bug, documentation update, or non-blocking feature request. | 480 mins | 1440 mins | Next Release | N/A | Weekly |

---

## Shift Handover Protocol

At each operational shift transition:
1. **Status Summary**: Outgoing team summarizes active P1/P2 tickets and ongoing triage status.
2. **Workaround Log**: Share all temporary operational compensating controls currently in effect.
3. **Release Schedule**: Verify any upcoming deployment windows or maintenance freezes in the next 24 hours.
4. **Audit Evidence**: Record handover notes in the shift log artifact (`09-operations/runbooks/rollback-incident-handoff-runbook.yaml`).

---

## Traceability & Standards

- **Capabilities**: [BCM-ORG-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/capability-package.yaml), [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.yaml), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.yaml), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.yaml)
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes (built on standard open observability, logs, and process frameworks)
