# HOP Rollback and Hotfix Governance Specification

## Overview

This document specifies the operational governance policies for **Emergency Rollbacks** and **Fast-Track Hotfixes** across all deployed surfaces of the **Healthcare Operations Platform (HOP)**. It ensures that critical runtime failures can be immediately contained through automated or manual rollbacks, and urgent defects can be safely patched without compromising quality or compliance.

---

## Rollback Governance & Triggers

```
[ Automated Health Probe / Alert Trigger ]
                     │
                     ▼
[ Revert Container Tag / Blue-Green Traffic Switch ]
                     │
                     ▼
[ Execute Database Down Script (if applicable) ]
                     │
                     ▼
[ Verify Health Readiness (200 OK) & Notify Support ]
```

### Automatic Rollback Triggers
An automated rollback is triggered immediately without waiting for human intervention if:
1. **Readiness Probe Failure**: `/actuator/health/readiness` fails or returns 503 for **3 consecutive minutes** following container deployment.
2. **HTTP 5xx Error Rate Spike**: Uncaught 5xx error rate exceeds **1.0%** of total requests within the first 15 minutes post-deployment.
3. **Database Migration Error**: Liquibase/Flyway schema migration aborts or encounters an unhandled constraint violation.

### Manual Rollback Triggers
The Incident Commander (IC) orders a manual rollback if:
- A P1 outage occurs and no operational workaround is available within **30 minutes**.
- Multi-tenant data isolation or RBAC permission boundaries exhibit security anomalies post-release.

---

## Fast-Track Hotfix Workflow

When a production defect requires an immediate software patch:

```
[ Main / Release Tag ] ──► [ hotfix/vX.Y.Z-issue ] ──► [ Targeted Unit/Regress Test ]
                                                                   │
[ Merge Back to Main ] ◄── [ Tag & Deploy Patch ] ◄── [ eCAB Signoff (15m) ] ◄┘
```

1. **Branch Creation**: Create a dedicated hotfix branch directly from the running production tag:
   `git checkout -b hotfix/v1.2.1-TD-QA-007-multipart-fix v1.2.0`
2. **Minimal Scoped Code Fix**: Modify only the code necessary to resolve the root cause. Avoid bundling unrelated refactoring.
3. **Regression Test Addition**: Add at least one explicit automated unit or integration test reproducing the defect and verifying the fix.
4. **Targeted Verification**: Run the relevant stack quality command (e.g., `mvn test -Dtest=...` or `npm run test`).
5. **eCAB Fast-Track Approval**: Secure 15-minute approval from the Emergency CAB (Incident Commander + Tech Lead + Ops Lead).
6. **Tag & Publish**: Tag the patch version (`v1.2.1`), compile container image, and promote to container registry.
7. **Post-Deployment Merge**: Merge hotfix branch back to main development branch to prevent regression in future releases.

---

## Database Migration & Rollback Strategy

1. **Additive Schema Rule**: Schema changes must be additive (e.g., `ADD COLUMN IF NOT EXISTS`, new tables). Destructive DDL (`DROP TABLE`, `DROP COLUMN`) is prohibited in routine releases and requires a 2-phase deprecation window.
2. **Rollback Scripts**: Every Liquibase / Flyway migration must include a verified `down` script.
3. **Data Preservation Protocol**: Prior to executing a database rollback script, export any data created during the failed release window into an isolated staging table to ensure zero customer data loss.

---

## Traceability & Standards

- **Capabilities**: [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.yaml), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.yaml), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.yaml)
- **Runbook Integration**: Integrated with [rollback-incident-handoff-runbook.yaml](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/09-operations/runbooks/rollback-incident-handoff-runbook.yaml).
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes
