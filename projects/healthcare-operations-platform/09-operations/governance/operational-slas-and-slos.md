# HOP Operational SLAs and SLOs Governance Specification

## Overview

This specification establishes the **Service Level Agreements (SLAs)** and **Service Level Objectives (SLOs)** for the Healthcare Operations Platform (HOP). It defines contractually binding performance metrics, internal service health targets, error budget policies, and scheduled maintenance windows.

---

## Service Level Agreements (SLAs)

SLAs define contractually guaranteed response and resolution times for commercial tenant deployments.

| Priority | First Response Target | Workaround / Resolution Target | Status Update Frequency | Remediations Eligible |
|---|---|---|---|---|
| **P1 - Critical** | **15 minutes** | **2 hours** | Every 30 mins | Yes (Service Credits) |
| **P2 - High** | **60 minutes** | **8 hours** | Every 2 hrs | Yes (Service Credits) |
| **P3 - Medium** | **4 hours** | **48 hours** | Every 12 hrs | No |
| **P4 - Low** | **24 hours** | Next Release Cycle | Every 48 hrs | No |

---

## Service Level Objectives (SLOs)

SLOs are internal engineering targets used to measure system reliability, speed, and data safety.

### 1. System Availability
- **Target**: **99.9%** availability measured over a rolling 30-day window.
- **Probe Endpoint**: `GET /actuator/health/readiness`
- **Max Monthly Downtime Budget**: **43.2 minutes**

### 2. API & Surface Latency
- **Backend REST API**:
  - **p95 Latency**: $\le 200\text{ ms}$
  - **p99 Latency**: $\le 500\text{ ms}$
- **Employee Portal Web Page Load**: $\le 1.5\text{ seconds}$ (p95)
- **Public Website Load**: $\le 1.0\text{ second}$ (p95)

### 3. Error Rate
- **Uncaught 5xx Server Error Rate**: $< 0.01\%$ of total HTTP requests.
- **Metric Source**: Prometheus `http_server_requests_seconds_count` and status code distributions.

### 4. Operational Recovery & Data Safety
- **Mean Time to Detect (MTTD)**: $\le 15\text{ minutes}$
- **Mean Time to Repair (MTTR)**: $\le 2\text{ hours}$ for P1
- **Recovery Point Objective (RPO)**: $\le 1\text{ hour}$ (Max allowed data delta in catastrophic failure)
- **Recovery Time Objective (RTO)**: $\le 4\text{ hours}$ (Max allowed time to restore database and services)
- **Backup Schedule**: Every 6 hours via automated PostgreSQL dump (`pg_dump`)
- **Restore Rehearsal**: Monthly automated or manual restore rehearsal validation.

---

## Error Budget Policy

HOP enforces an automated governance model based on monthly error budget consumption:

1. **50% Error Budget Consumed**:
   - Issue warning notification to L2/L3 engineering teams.
   - Review technical debt index (`08-qa/technical-debt/`) for stability items.
2. **75% Error Budget Consumed**:
   - **Feature Freeze**: Pause new feature deployments.
   - Redirect engineering resources to bug fixes, performance tuning, and resilience enhancements.
3. **100% Error Budget Consumed**:
   - **Full Deployment Freeze**: Block all non-emergency releases.
   - Only P1 hotfixes approved by the Emergency Change Advisory Board (eCAB) are permitted.
   - Conduct mandatory architectural review before resuming normal release cadence.

---

## Scheduled Maintenance Windows

- **Standard Maintenance Window**: Sundays, 02:00–06:00 UTC.
  - Notification lead time: Minimum **7 calendar days** advance notice to customer admins.
- **Urgent Maintenance Window**:
  - Used for emergency security patches or infrastructure containment.
  - Notification lead time: Minimum **24 hours** advance notice, subject to eCAB signoff.

---

## Traceability & Compliance

- **Capabilities**: [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.md), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.md), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.md)
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GOV-SLA-001
  type: operational-governance-specification
  name: HOP Operational SLAs and SLOs Governance Specification
  version: 1.0.0
  status: approved
  human_readable: operational-slas-and-slos.md
  machine_readable: operational-slas-and-slos.md
  backlog_item: COM-MOD-016-OPS-001
  created_date: 2026-07-24
  owner: HOP Platform & Service Level Architecture Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
service_level_agreements:
  sla_definitions:
  - priority: P1_critical
    response_time_minutes: 15
    resolution_time_hours: 2
    update_frequency_minutes: 30
    availability_impact: high
    financial_remediation_eligible: true
  - priority: P2_high
    response_time_minutes: 60
    resolution_time_hours: 8
    update_frequency_hours: 2
    availability_impact: medium
    financial_remediation_eligible: true
  - priority: P3_medium
    response_time_minutes: 240
    resolution_time_hours: 48
    update_frequency_hours: 12
    availability_impact: low
    financial_remediation_eligible: false
  - priority: P4_low
    response_time_minutes: 1440
    resolution_time_hours: next_release_cycle
    update_frequency_hours: 48
    availability_impact: none
    financial_remediation_eligible: false
service_level_objectives:
  availability:
    target_percentage: 99.9
    measurement_window: 30_days_rolling
    endpoint: GET /actuator/health/readiness
    allowed_downtime_per_month_minutes: 43.2
  latency:
    backend_api_p95_ms: 200
    backend_api_p99_ms: 500
    employee_portal_page_load_p95_seconds: 1.5
    public_website_page_load_p95_seconds: 1.0
    measurement_metric: http_server_requests_seconds
  error_rate:
    max_5xx_error_percentage: 0.01
    measurement_metric: http_server_requests_5xx_total / http_server_requests_total
  operational_resilience:
    mttd_minutes: 15
    mttr_hours: 2
    rpo_hours: 1
    rto_hours: 4
    backup_frequency_hours: 6
    restore_rehearsal_frequency: monthly
error_budget_policy:
  monthly_budget_minutes: 43.2
  thresholds:
  - consumption_percentage: 50
    action: Inform L2 support and review active technical debt
  - consumption_percentage: 75
    action: Freeze non-critical feature deployments; prioritize stability patches
  - consumption_percentage: 100
    action: Full deployment freeze except P1 emergency hotfixes; mandatory CAB RCA
      review
maintenance_windows:
  standard_window:
    day: Sunday
    time_utc: 02:00-06:00
    notice_period_days: 7
  urgent_window:
    notice_period_hours: 24
    approval_required: Emergency CAB (eCAB)
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
