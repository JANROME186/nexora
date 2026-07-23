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
- [capability-package.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/capability-package.yaml)
- [business-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/business-model.yaml)
- [business-rules.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/business-rules.yaml)
- [processes.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/processes.yaml)
- [events.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/events.yaml)
- [openapi-source.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/openapi-source.yaml)
- [permissions.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/permissions.yaml)
- [ui-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/ui-model.yaml)
- [mobile-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/mobile-model.yaml)
- [test-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/test-model.yaml)
- [observability-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/observability-model.yaml)
- [generation-plan.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/generation-plan.yaml)
- [traceability.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-007-audit-management/traceability.yaml)
