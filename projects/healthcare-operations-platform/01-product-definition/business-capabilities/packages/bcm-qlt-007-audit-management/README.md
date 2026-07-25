# Audit Management (BCM-QLT-007)

## Overview
The **Audit Management** capability package models internal, regulatory, and supplier audit planning, execution, non-conformity finding logging, report publishing, and integration with CAPA (BCM-QLT-006) and Document Retention (BCM-PLT-008).

## Key Functions
- **Audit Scheduling & Scope Definition**: Plan internal quality audits, accredited external inspections (ISO 15189, COFEPRIS, CLIA), or vendor audits with assigned Lead Auditors.
- **Finding & Non-Conformity Logging**: Record findings during audit execution, classified by severity (critical, major, minor, opportunity_for_improvement).
- **Automated CAPA Linkage**: Automatically initiate CAPA investigations (BCM-QLT-006) for critical and major non-conformities.
- **Audit Report Publishing & Evidence Archival**: Compile final audit reports, attach supporting evidence, and store with regulatory retention tags via Document Management (BCM-PLT-008).

## Bounded Context & Aggregates
- **Bounded Context**: `external-quality-compliance`
- **Primary Aggregate**: `AuditSchedule` (AGG-022)
- **Roadmap Group**: `COM-MOD-013` (Advanced Quality and Compliance)

## Artifact Index
- [capability-package.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/capability-package.md)
- [business-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/business-model.md)
- [business-rules.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/business-rules.md)
- [processes.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/processes.md)
- [events.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/events.md)
- [openapi-source.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/openapi-source.md)
- [permissions.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/permissions.md)
- [ui-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/ui-model.md)
- [mobile-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/mobile-model.md)
- [test-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/test-model.md)
- [observability-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/observability-model.md)
- [generation-plan.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/generation-plan.md)
- [traceability.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/traceability.md)
