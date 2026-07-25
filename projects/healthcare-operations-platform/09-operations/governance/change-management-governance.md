# HOP Change Management Governance Specification

## Overview

Change Management governs all modifications to the **Healthcare Operations Platform (HOP)** production software, database schemas, environment configurations, and integration adapter boundaries. The objective is to minimize service disruptions, ensure security compliance, and maintain full traceability for all operational changes.

---

## Change Classification & Workflows

```
[ Request for Change (RFC) ]
             │
    ┌────────┴────────┬─────────────────┐
    ▼                 ▼                 ▼
[ Standard ]      [ Normal ]       [ Emergency ]
(Pre-Approved)   (CAB Review)     (eCAB - 15m)
    │                 │                 │
    └────────┬────────┴─────────────────┘
             ▼
[ Deployment & Audit Log ]
```

### 1. Standard Changes
- **Criteria**: Low-risk, routine operational tasks with proven, repeatable runbooks and no user downtime.
- **Examples**: Toggling tenant feature flags via BCM-PLT-002, updating localization text resources, adding minor user roles.
- **Approval**: Peer review and L2 Lead signoff.
- **Lead Time**: Minimum 4 hours advance logging.

### 2. Normal Changes
- **Criteria**: Scheduled software releases, additive database DDL migrations, new feature introductions, or integration adapter updates.
- **Examples**: Deploying a minor release tag (`v1.2.0`), applying Spring schema migrations, updating REST controllers.
- **Approval**: Formal **Change Advisory Board (CAB)** approval.
- **Lead Time**: Minimum 3 business days advance RFC submission.

### 3. Emergency Changes
- **Criteria**: Urgent hotfixes required to restore a P1 outage or mitigate a High/Critical security vulnerability.
- **Examples**: Deploying a emergency patch for an unhandled 500 error, applying zero-day dependency patches.
- **Approval**: **Emergency CAB (eCAB)** consisting of Incident Commander, Technical Lead, and Operations Lead.
- **Lead Time**: 15 minutes fast-track review, followed by full CAB retroactive audit within 24 hours.

---

## Change Advisory Board (CAB) Structure

- **Members**:
  - Product Architecture Lead
  - Operations & Infrastructure Lead
  - Quality Assurance & Security Lead
  - Customer Success Lead
- **Meeting Cadence**: Weekly on Thursdays at 14:00 UTC.
- **RFC Evaluation Criteria**:
  1. Automated test results (unit, integration, contract tests pass).
  2. Security quality evidence (`08-qa/security-quality/` clean).
  3. Reversible database migration plan or backward-compatible schema design.
  4. Operational acceptance checklist (`09-operations/governance/operational-acceptance-criteria.md`).
  5. Backout/Rollback plan documented and tested.

---

## Risk Assessment Matrix

| Risk Level | Criteria | Required Evidence | Approval Scope |
|---|---|---|---|
| **Low** | Isolated service update, zero schema change, fully reversible. | Test report, git diff check | L2 Lead / Peer |
| **Medium** | Multi-service release, additive DDL migration, external adapter update. | QA evidence, security scan, rollback script | Full CAB |
| **High** | Core domain schema change, auth/IAM permission catalog edit, breaking API. | Complete regression suite, DAST pass, staging rehearse | Full CAB + Exec |
| **Critical** | Tenant isolation boundary change, major framework upgrade, emergency P1 hotfix. | eCAB signoff, live backup snapshot, instant rollback plan | eCAB / Exec |

---

## Change Freeze Policies

1. **Fiscal Year-End Freeze**: December 20 to January 5 annually. Only emergency P1 hotfixes are permitted.
2. **Customer Onboarding Freeze**: Configurable 7-day window surrounding a major customer cutover or hypercare launch.

---

## Traceability & Audit Standards

- **Capabilities**: [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.md), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.md), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.md)
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GOV-CHG-001
  type: operational-governance-specification
  name: HOP Change Management Governance Specification
  version: 1.0.0
  status: approved
  human_readable: change-management-governance.md
  machine_readable: change-management-governance.md
  backlog_item: COM-MOD-016-OPS-001
  created_date: 2026-07-24
  owner: HOP Change Advisory Board & Operations Governance Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
change_categories:
- category: Standard_Change
  definition: Pre-approved low-risk operational changes with established runbooks
    and zero downtime impact.
  examples: Feature flag activation, standard configuration update, user role adjustments.
  approval_authority: Peer Review / L2 Lead
  lead_time_hours: 4
  cab_review_required: false
- category: Normal_Change
  definition: Planned releases, schema additions, integration updates, or non-emergency
    bug fixes.
  examples: Scheduled minor version release, API endpoint addition, database migration
    deployment.
  approval_authority: Change Advisory Board (CAB)
  lead_time_days: 3
  cab_review_required: true
- category: Emergency_Change
  definition: Urgent fixes required to restore P1 outage or mitigate critical security
    vulnerability.
  examples: Security patch application, hotfix deployment, database rollback execution.
  approval_authority: Emergency CAB (eCAB - Incident Commander + Tech Lead + Ops Lead)
  lead_time_minutes: 15
  cab_review_required: true (retroactive full CAB review within 24 hours)
change_advisory_board:
  composition:
  - Product Architecture Lead
  - Operations & Infrastructure Lead
  - Quality Assurance & Security Lead
  - Customer Success Lead
  meeting_schedule: Weekly on Thursdays at 14:00 UTC (eCAB convened on demand 24/7)
  decision_rules:
    unanimous_for_normal: Required for minor/major releases
    quorum_for_emergency: 2 of 3 eCAB members required
risk_assessment_matrix:
  risk_levels:
  - level: Low
    criteria: Independent service update, backward compatible API, zero schema changes,
      reversible.
  - level: Medium
    criteria: Multi-service update, additive DDL migration, external adapter modification.
  - level: High
    criteria: Core persistence schema modification, auth/IAM permission catalog updates,
      breaking API change.
  - level: Critical
    criteria: Cross-tenant data boundary update, major framework version migration,
      platform-wide rollback.
change_freeze_windows:
  rules:
  - window: Fiscal_Year_End_Freeze
    dates: December 20 to January 5 annually
    allowed_changes: Emergency P1 hotfixes only
  - window: Customer_Major_Go_Live_Freeze
    dates: Configurable per tenant (7 days surrounding customer cutover)
    allowed_changes: Standard configuration changes and pre-approved cutover scripts
      only
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
