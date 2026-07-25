# HOP Problem Management & RCA Governance Specification

## Overview

Problem Management in the **Healthcare Operations Platform (HOP)** aims to prevent recurring incidents, identify root causes, maintain a **Known Error Database (KEDB)**, and integrate permanent resolutions into the development backlog via the **Technical Debt Index**.

While Incident Management focuses on restoring service as quickly as possible, Problem Management focuses on finding the underlying cause to prevent recurrence.

---

## Problem Management Workflow

```
[ Incident / Recurrence ] ──► [ Problem Identification ] ──► [ Root Cause Analysis (5 Whys) ]
                                                                      │
[ Debt Index / Backlog ] ◄── [ Permanent Solution ] ◄── [ KEDB Workaround Recorded ] ◄┘
```

### 1. Problem Identification Triggers
A formal Problem Investigation is opened automatically under any of the following conditions:
- **P1 Incident Resolution**: Every P1 incident must produce a corresponding Problem Investigation within 24 hours.
- **Recurring Incidents**: Occurrence of 3 or more P2 or P3 incidents sharing the same failure pattern within 30 days.
- **Unresolved Vulnerabilities**: Any High/Critical security finding from static analysis or vulnerability scanning that cannot be patched immediately.

### 2. Root Cause Analysis (RCA) Methodology
HOP mandates the **5 Whys Methodology** for Root Cause Analysis:
1. *Why did the service degrade?* (e.g., Database connection pool exhausted)
2. *Why was the connection pool exhausted?* (e.g., Long-running unindexed query held connections)
3. *Why was the unindexed query executed?* (e.g., New search parameter was added without database index)
4. *Why was the query missing an index?* (e.g., Index creation was deferred during migration script generation)
5. *Why was index creation deferred?* (e.g., Migration verification gate did not validate query execution plan)

### 3. Known Error Database (KEDB)
When a root cause is identified but a permanent code fix is deferred to a future backlog item, L2 Support must record a **Known Error Record**:
- **Title & Description**: Detailed description of symptom and underlying mechanism.
- **Workaround**: Prescribed operational steps for L1/L2 support to bypass the issue safely.
- **Technical Debt Pointer**: Link to the tracked item in `08-qa/technical-debt/technical-debt-index.md`.

---

## Technical Debt Integration

Problem resolutions must be converted into formal technical debt records:
1. Open or update an item under `08-qa/technical-debt/<TD-ID>-<short-name>.yaml`.
2. Define explicit `risk_level`, `affected_area`, `remediation_strategy`, and `target_backlog`.
3. Enforce the **Technical Debt Burn-down Policy**:
   - Technical debt intensity increases as release readiness approaches.
   - All open technical debt items attributable to core operations must be closed before commercial GA signoff.

---

## Traceability & Standards

- **Capabilities**: [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.md), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.md), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.md)
- **Technical Debt Index**: Linked to [technical-debt-index.md](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/08-qa/technical-debt/technical-debt-index.md).
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GOV-PRB-001
  type: operational-governance-specification
  name: HOP Problem Management & RCA Governance Specification
  version: 1.0.0
  status: approved
  human_readable: problem-management-governance.md
  machine_readable: problem-management-governance.md
  backlog_item: COM-MOD-016-OPS-001
  created_date: 2026-07-24
  owner: HOP Platform Quality & Architecture Governance Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
problem_triggers:
- trigger: P1_incident_closure
  rule: Every closed P1 incident automatically generates a mandatory Problem investigation
    record.
- trigger: recurring_p2_p3_incidents
  rule: 3 or more incidents with identical error codes or component root causes within
    a 30-day window generate a Problem record.
- trigger: major_security_vulnerability
  rule: Critical/High security vulnerability identified during open-source scans generates
    a Problem record if immediate hotfix is deferred.
investigation_methodology:
  framework: Five_Whys_and_Fishbone_Analysis
  steps:
  - Step 1: Problem Definition & Impact Statement
  - Step 2: Chronological Timeline Reconstruction
  - Step 3: Root Cause Traversal (5 Whys)
  - Step 4: Technical Debt & Architectural Gap Identification
  - Step 5: Preventive Action Item Definition & Backlog Entry
known_error_database:
  kedb_policy:
    purpose: Maintain searchable index of temporary workarounds and known non-blocking
      product limitations for L1/L2 support.
    location: 09-operations/governance/problem-management-governance.md
    review_frequency: monthly
  registered_known_errors:
  - id: KE-OBS-001
    title: Distributed trace export requires external OpenTelemetry collector stack
    workaround: Rely on Spring MDC traceId log correlation and Prometheus HTTP metrics
    tracked_debt_item: TD-OBS-001
    target_module: Infrastructure Expansion
  - id: KE-IAM-004
    title: Quality & Document controllers assign synthetic tenant ID
    workaround: Use request-header filtering and application-level tenant context
      verification
    tracked_debt_item: TD-IAM-004
    target_module: Future Spring Modulith Shared Context Refactoring
technical_debt_integration:
  policy:
  - Problem resolution actions must be logged as actionable items in 08-qa/technical-debt/technical-debt-index.md.
  - Each debt item must contain risk level, owner, target backlog, and acceptance
    criteria.
  - Technical debt burn-down rules enforce resolution before final General Availability
    (GA) closure.
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
