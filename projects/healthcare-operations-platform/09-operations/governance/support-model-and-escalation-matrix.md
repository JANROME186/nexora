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
4. **Audit Evidence**: Record handover notes in the shift log artifact (`09-operations/runbooks/rollback-incident-handoff-runbook.md`).

---

## Traceability & Standards

- **Capabilities**: [BCM-ORG-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/capability-package.md), [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.md), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.md), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.md)
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes (built on standard open observability, logs, and process frameworks)

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GOV-SUP-001
  type: operational-governance-specification
  name: HOP Support Model L1/L2/L3 and Escalation Matrix Specification
  version: 1.0.0
  status: approved
  human_readable: support-model-and-escalation-matrix.md
  machine_readable: support-model-and-escalation-matrix.md
  backlog_item: COM-MOD-016-OPS-001
  created_date: 2026-07-24
  owner: HOP Operations & Customer Success Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
support_model:
  tiers:
  - tier: L1
    name: Customer Helpdesk & Intake
    responsibilities:
    - First point of contact for customer inquiries, incidents, and requests
    - Incident logging, initial classification (P1-P4), and basic troubleshooting
    - User account lockouts, basic credential resets, and navigation assistance
    - Verification of customer environment and browser/mobile prerequisites
    - Escalation to L2 when issues cannot be resolved using standard operating procedures
      (SOPs)
    shift_coverage: 24/7 for P1/P2; Business Hours (08:00-18:00 local time) for P3/P4
    max_initial_triage_time_minutes: 15
    tools: Open source ticketing system, customer communication portal, knowledge
      base
  - tier: L2
    name: HOP Operational Support Engineering
    responsibilities:
    - Technical triage and log analysis (Spring MDC traces, browser console, PostgreSQL
      logs)
    - Application configuration updates via Platform Configuration endpoints
    - Tenant status verification and isolation triage (using tenant-impact-triage-runbook)
    - Data ingestion and migration job troubleshooting (BCM-PLT-010 checkpoint analysis)
    - Database query inspection and non-mutating operational diagnostics
    - Escalation to L3 for underlying product defects or core code modifications
    shift_coverage: 24/7 on-call rotation for P1; 08:00-20:00 local time for P2-P4
    max_triage_escalation_time_minutes: 30
    tools: Actuator health/prometheus endpoints, PostgreSQL read replicas, open observability
      tools
  - tier: L3
    name: HOP Core Product Engineering & Architecture
    responsibilities:
    - Deep code-level investigation, debugging, and root cause analysis (RCA)
    - Hotfix development, patch compilation, and emergency release validation
    - Core database schema fixes and migration rollback execution
    - Security defect remediation and vulnerability patching
    - Third-party adapter (fiscal, PACS, notification) boundary remediation
    shift_coverage: On-call escalation for P1/P2 incidents; Business Hours for P3/P4
      problem resolution
    target_engagement_time_minutes: 15 (for P1 escalation)
    tools: Source code repository, dev toolchain, local compose stack, Git, security
      scanners
escalation_matrix:
  priority_rules:
  - priority: P1_critical
    definition: Complete system outage, tenant-wide unavailability, data corruption
      risk, or diagnostic result delivery blocked across major branches.
    l1_response_time_minutes: 15
    l2_engagement_time_minutes: 15
    l3_engagement_time_minutes: 30
    management_notification_minutes: 15
    executive_briefing_frequency_hours: 1
    escalation_channel: P1-Emergency-Bridge-Channel
  - priority: P2_high
    definition: Major capability degraded (e.g., sample reception or payment billing
      request failing for subset of users), no immediate workaround available.
    l1_response_time_minutes: 30
    l2_engagement_time_minutes: 60
    l3_engagement_time_minutes: 120
    management_notification_minutes: 60
    executive_briefing_frequency_hours: 4
    escalation_channel: Support-High-Priority-Queue
  - priority: P3_medium
    definition: Minor functionality issue, non-critical workflow degraded with an
      available operational workaround.
    l1_response_time_minutes: 120
    l2_engagement_time_minutes: 240
    l3_engagement_time_minutes: 480
    management_notification_minutes: 240
    executive_briefing_frequency_hours: 24
    escalation_channel: Support-Standard-Queue
  - priority: P4_low
    definition: Cosmetic defect, documentation typo, feature enhancement request,
      or non-blocking UI improvement.
    l1_response_time_minutes: 480
    l2_engagement_time_minutes: 1440
    l3_engagement_time_minutes: next_release_cycle
    management_notification_minutes: not_required
    executive_briefing_frequency_hours: weekly_report
    escalation_channel: Product-Backlog-Queue
handover_policy:
  shift_handover:
    frequency: Daily at shift transitions
    mandatory_content:
    - Active P1/P2 incidents and current status
    - Pending customer escalations
    - Scheduled maintenance or release activities in next 24 hours
    - Known active workarounds in place
    evidence_location: 09-operations/runbooks/rollback-incident-handoff-runbook.md
traceability:
  capabilities:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-ORG-003
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-007
  standards_compliance:
    agent_agnostic: true
    open_source_first: true
    no_proprietary_agent_dependencies: true
```
