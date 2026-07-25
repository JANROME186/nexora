# CAPA Management (BCM-QLT-006)

## Overview
The **CAPA Management** (Corrective Action and Preventive Action) capability package models systematic quality investigations, root cause analyses (RCA), action planning, approval, and effectiveness verification.

## Key Functions
- **Quality Incident & Trigger Ingestion**: Captures triggers automatically from EQA failures (|z| > 2.0), internal QC out-of-control runs, audit findings, critical result escalations, or manual entries.
- **Root Cause Analysis (RCA)**: Supports structured 5-Whys and Ishikawa (Fishbone) analysis documentation.
- **Action Plan Formulation**: Defines corrective and preventive action items with assignees, due dates, and completion criteria.
- **Independent Approval & Verification**: Requires quality approver sign-off for action plans and mandatory post-implementation effectiveness evaluation before final closure.
- **Audit & Evidence Traceability**: Links supporting evidence documents via BCM-PLT-008 and emits append-only audit events via BCM-PLT-007.

## Bounded Context & Aggregates
- **Bounded Context**: `external-quality-compliance`
- **Primary Aggregate**: `CapaInvestigation` (AGG-021)
- **Roadmap Group**: `COM-MOD-013` (Advanced Quality and Compliance)

## Artifact Index
- [capability-package.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/capability-package.md)
- [business-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/business-model.md)
- [business-rules.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/business-rules.md)
- [processes.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/processes.md)
- [events.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/events.md)
- [openapi-source.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/openapi-source.md)
- [permissions.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/permissions.md)
- [ui-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/ui-model.md)
- [mobile-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/mobile-model.md)
- [test-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/test-model.md)
- [observability-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/observability-model.md)
- [generation-plan.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/generation-plan.md)
- [traceability.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-006-capa-management/traceability.md)
