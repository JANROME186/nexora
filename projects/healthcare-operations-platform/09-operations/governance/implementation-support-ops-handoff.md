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
- [ ] Tenant provisioned with verified legal name, tax ID, tier, and isolation strategy ([BCM-ORG-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/capability-package.yaml)).
- [ ] Laboratory and branch records established ([BCM-ORG-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-002-laboratory-management/capability-package.yaml), [BCM-ORG-003](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-003-branch-management/capability-package.yaml)).
- [ ] Role-based access control (27 permissions) verified per user account ([BCM-PLT-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-001-identity-and-access-management/capability-package.yaml)).
- [ ] Diagnostic services, tests, analytes, reference ranges, and price lists published.

### Data Ingestion & Integration Signoff
- [ ] Migration import dry-run and reconciliation report executed without errors ([BCM-PLT-010](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-010-open-data-ingestion-and-migration/capability-package.yaml)).
- [ ] Partner API keys, integration endpoints, and rate-limit policies configured ([BCM-PLT-004](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-004-integration-management/capability-package.yaml), [BCM-PLT-005](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/capability-package.yaml)).
- [ ] Backup schedule active and health readiness probes verified.

### Enablement & Governance Signoff
- [ ] Customer staff trained on employee portal, patient/doctor portal access, and cashier screens.
- [ ] Customer emergency contact matrix logged in L1 Helpdesk system.
- [ ] Formal Handoff Protocol Document signed by Delivery Lead, Support Manager, and Customer Ops Sponsor.

---

## Traceability & Compliance

- **Capabilities**: [BCM-ORG-001](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-org-001-tenant-management/capability-package.yaml), [BCM-PLT-002](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-002-platform-configuration/capability-package.yaml), [BCM-PLT-006](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-006-observability/capability-package.yaml), [BCM-PLT-007](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.yaml), [BCM-PLT-008](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/capability-package.yaml)
- **Onboarding Integration**: Integrated with [initial-training-human-validation-and-acceptance-guide.yaml](file:///c:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/09-operations/onboarding/initial-training-human-validation-and-acceptance-guide.yaml).
- **Agent-Agnostic**: Yes
- **Open-Source-First**: Yes
