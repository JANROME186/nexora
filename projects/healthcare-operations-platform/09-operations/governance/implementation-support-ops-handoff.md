# HOP Implementation to Support & Operations Handoff Specification

## Overview

This specification details the formal transition framework between **Professional Services / Implementation Teams** and **Business-As-Usual (BAU) Operations & Support Teams** for the **Healthcare Operations Platform (HOP)**. It governs the transition from customer setup and initial data ingestion to long-term operational maintenance and 24/7 support.

---

## Handoff Lifecycle Phases

```
[ Phase 1: Implementation ]
(Tenant Provisioning, Catalog & Data Ingestion)
            │
            ▼
[ Phase 2: Hypercare (30 Days) ]
(Joint Delivery & Support Operations)
            │
            ▼
[ Phase 3: BAU Operations Handoff ]
(Full Transition to L1/L2/L3 Support)
```

### Phase 1: Implementation & Customer Setup
- Led by Professional Services / Delivery Engineers.
- Complete tenant provisioning (`BCM-ORG-001`), laboratory/branch setup (`BCM-ORG-002`, `BCM-ORG-003`), user account mapping (`BCM-PLT-001`), diagnostic catalog publishing (`MVP-MOD-002`), and historical open data ingestion (`BCM-PLT-010`).
- Deliver initial customer configuration report and data ingestion reconciliation summary.

### Phase 2: Hypercare Protocol (30 Calendar Days)
- Joint operational ownership between Delivery Lead and L2 Support Engineering.
- Conduct daily 15-minute operational check-ins with customer laboratory managers.
- Expedited incident triage (P1/P2 tickets routed directly to joint Hypercare bridge).
- Exit Criteria:
  1. Zero P1 or P2 open incidents for **10 consecutive business days**.
  2. End-to-end operational validation passed: sample intake $\rightarrow$ processing $\rightarrow$ medical validation $\rightarrow$ result delivery $\rightarrow$ cashier billing request.
  3. Formal Hypercare exit signoff signed by Customer Project Sponsor.

### Phase 3: BAU Operations Handoff
- Full transfer of tenant management to standard L1/L2/L3 support structure.
- Customer support tickets managed exclusively through standard channels (`support-model-and-escalation-matrix.md`).
- Tenant incorporated into routine maintenance, backup, restore rehearsals, and automated observability sweeps.

---

## Mandatory Handoff Checklist

### Technical & Configuration Signoff
- [ ] Tenant provisioned with verified legal name, tax ID, tier, and isolation strategy ([BCM-ORG-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/capability-package.md)).
- [ ] Laboratory and branch records established ([BCM-ORG-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-002-laboratory-management/capability-package.md), [BCM-ORG-003](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-003-branch-management/capability-package.md)).
- [ ] Role-based access control (27 permissions) verified per user account ([BCM-PLT-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-001-identity-and-access-management/capability-package.md)).
- [ ] Diagnostic services, tests, analytes, reference ranges, and price lists published.

### Data Ingestion & Integration Signoff
- [ ] Migration import dry-run and reconciliation report executed without errors ([BCM-PLT-010](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-010-open-data-ingestion-and-migration/capability-package.md)).
- [ ] Partner API keys, integration endpoints, and rate-limit policies configured ([BCM-PLT-004](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-004-integration-management/capability-package.md), [BCM-PLT-005](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/capability-package.md)).
- [ ] Backup schedule active and health readiness probes verified.

### Enablement & Governance Signoff
- [ ] Customer staff trained on employee portal, patient/doctor portal access, and cashier screens.
- [ ] Customer emergency contact matrix logged in L1 Helpdesk system.
- [ ] Formal Handoff Protocol Document signed by Delivery Lead, Support Manager, and Customer Ops Sponsor.

---

## Traceability & Compliance

- **Capabilities**: [BCM-ORG-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/capability-package.md), [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.md), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.md), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.md), [BCM-PLT-008](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/capability-package.md)
- **Onboarding Integration**: Integrated with [initial-training-human-validation-and-acceptance-guide.md](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/09-operations/onboarding/initial-training-human-validation-and-acceptance-guide.md).
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GOV-HND-001
  type: operational-governance-specification
  name: HOP Implementation to Support & Operations Handoff Specification
  version: 1.0.0
  status: approved
  human_readable: implementation-support-ops-handoff.md
  machine_readable: implementation-support-ops-handoff.md
  backlog_item: COM-MOD-016-OPS-001
  created_date: 2026-07-24
  owner: HOP Professional Services & Customer Operations Team
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  module: COM-MOD-016
  release: REL-003
handoff_phases:
- phase: 1_Implementation_Phase
  owner: Professional Services / Delivery Team
  activities: Tenant setup, organization/branch/lab configuration, initial data ingestion
    (BCM-PLT-010), custom pricing/catalog setup.
  deliverables: Initial Tenant Onboarding Report, Data Ingestion Summary.
- phase: 2_Hypercare_Phase
  duration_days: 30
  joint_owners: Professional Services Lead & L2 Support Lead
  activities: Daily operational check-ins, priority support routing, user training
    reinforcement, performance monitoring.
  exit_criteria: Zero open P1/P2 incidents for 10 consecutive days, customer signoff
    on core operational workflows.
- phase: 3_BAU_Operations_Handoff
  owner: L2 Support Engineering & BAU Customer Success
  activities: Full transition to standard L1/L2/L3 support model, ticket queue transfer,
    routine maintenance inclusion.
  signoff_required: Formal Handoff Protocol Signoff Document.
handoff_checklist:
  technical_readiness:
  - Tenant provisioned with correct legal name, tax ID, tier, and isolation strategy
    (BCM-ORG-001).
  - Initial organization, laboratory, and branch records created and linked (BCM-ORG-002,
    BCM-ORG-003).
  - User accounts, role assignments, and initial permission matrices configured (BCM-PLT-001).
  - Master diagnostic catalog, test definitions, reference ranges, and price lists
    published (MVP-MOD-002).
  operational_readiness:
  - Open data ingestion migration package imported, dry-run executed, and reconciliation
    report signed off (BCM-PLT-010).
  - Integration endpoints and partner API keys configured with rate-limiting policies
    (BCM-PLT-004, BCM-PLT-005).
  - Backup policies active and database restore rehearsal verified.
  - Actuator health probes and Prometheus metrics monitoring confirmed active.
  support_and_customer_readiness:
  - Customer administrator and staff key roles trained on employee portal screens.
  - Customer support contacts and escalation hierarchy configured in L1 ticketing
    system.
  - Standard Operating Procedures (SOPs) handed over to customer clinic managers.
  - Operational acceptance criteria (OAC) validated and signed off by Customer Project
    Sponsor.
traceability:
  capabilities:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-ORG-003
  - BCM-PLT-002
  - BCM-PLT-006
  - BCM-PLT-007
  - BCM-PLT-008
  standards_compliance:
    agent_agnostic: true
    open_source_first: true
    no_proprietary_agent_dependencies: true
```
