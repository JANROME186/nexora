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

- **Capabilities**: [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.md), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.md), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.md)
- **Runbook Integration**: Integrated with [rollback-incident-handoff-runbook.md](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/09-operations/runbooks/rollback-incident-handoff-runbook.md).
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GOV-RLB-001
  type: operational-governance-specification
  name: HOP Rollback and Hotfix Governance Specification
  version: 1.0.0
  status: approved
  human_readable: rollback-and-hotfix-governance.md
  machine_readable: rollback-and-hotfix-governance.md
  backlog_item: COM-MOD-016-OPS-001
  created_date: 2026-07-24
  owner: HOP Platform Engineering & Operations Governance Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
rollback_triggers:
  automatic_triggers:
  - trigger: readiness_probe_failure
    condition: /actuator/health/readiness returns 503 or fails for 3 consecutive minutes
      post-deployment.
    action: Immediate automated blue/green container traffic reversion to previous
      release image tag.
  - trigger: error_rate_spike
    condition: HTTP 5xx error rate exceeds 1% within first 15 minutes of deployment.
    action: Automated container traffic shift back to stable release tag.
  - trigger: data_integrity_alert
    condition: Unhandled database migration error or cross-tenant query failure detected
      during launch.
    action: Instant deployment halt, traffic reversion, and database rollback execution.
  manual_triggers:
  - trigger: P1_uncapped_outage
    condition: Unresolved P1 issue with no viable operational workaround within 30
      minutes of deployment.
    authority: Incident Commander signoff.
hotfix_governance:
  branch_naming_convention: hotfix/v<X.Y.Z>-<issue-id>-<short-description>
  fast_track_workflow:
  - Step 1: Branch from current production release tag.
  - Step 2: Implement minimal reproducible code fix.
  - Step 3: Add dedicated regression test covering root cause.
  - Step 4: Execute targeted Maven/npm quality suite for affected stack.
  - Step 5: Fast-track eCAB review (15-minute SLA).
  - Step 6: Tag patch release (e.g., v1.2.1), promote to production container registry.
  - Step 7: Verify health probes and error rate baseline.
  - Step 8: Post-deployment code merge back to main development branch.
database_rollback_policy:
  schema_migration_rules:
  - Destructive DDL (DROP TABLE, DROP COLUMN) is forbidden in normal releases; must
    follow a 2-phase deprecation cycle.
  - Every database migration must include a verified rollback script (Liquibase/Flyway
    undo or manual down script).
  - In the event of a rollback, verify data written during the failed release window
    is preserved or safely isolated before executing schema down scripts.
post_rollback_triage:
  mandatory_actions:
  - Notify L1/L2 support and updated status page within 15 minutes.
  - Convene Blameless PIR within 24 hours (referencing post-incident-review-runbook.md).
  - Update Known Error Database (KEDB) and technical debt index with lessons learned.
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
