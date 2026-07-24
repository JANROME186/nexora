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
- **Technical Debt Pointer**: Link to the tracked item in `08-qa/technical-debt/technical-debt-index.yaml`.

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

- **Capabilities**: [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.yaml), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.yaml), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.yaml)
- **Technical Debt Index**: Linked to [technical-debt-index.yaml](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/08-qa/technical-debt/technical-debt-index.yaml).
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes
