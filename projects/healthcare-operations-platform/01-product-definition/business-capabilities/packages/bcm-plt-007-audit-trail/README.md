# Audit Trail (BCM-PLT-007)

## Overview
The **Audit Trail** capability package governs append-only security audit event recording, tamper-evident cryptographic hash chaining, compliance data access logging, structured query, and compliance export capabilities for regulatory oversight.

## Extension Note (COM-MOD-013)
Extended under **COM-MOD-013 Advanced Quality and Compliance** to ingest domain events from External Quality Controls (BCM-QLT-002), CAPA Management (BCM-QLT-006), and Audit Management (BCM-QLT-007) with `complianceCorrelationId` and `qualityInvestigationId` tags, and expose structured search (`searchAuditEvents`) and export (`exportAuditEvents`) endpoints for accredited regulatory inspections.

## Key Functions
- **Append-Only Logging**: Write security, access, clinical, financial, quality, and administrative events with tamper-evident SHA-256 hash chaining.
- **Compliance Search & Filter**: Query audit trail entries by correlation ID, date range, category, or quality investigation reference.
- **Regulatory Evidence Export**: Generate authorized CSV/JSON audit trail export packages for external auditors.

## Bounded Context & Aggregates
- **Bounded Context**: `audit-compliance`
- **Primary Aggregate**: `AuditEvent` (AGG-018)
- **Roadmap Group**: `COM-MOD-013`

## Artifact Index
- [capability-package.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.yaml)
- [business-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/business-model.yaml)
- [business-rules.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/business-rules.yaml)
- [processes.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/processes.yaml)
- [events.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/events.yaml)
- [openapi-source.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/openapi-source.yaml)
- [permissions.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/permissions.yaml)
- [ui-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/ui-model.yaml)
- [mobile-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/mobile-model.yaml)
- [test-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/test-model.yaml)
- [observability-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/observability-model.yaml)
- [generation-plan.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/generation-plan.yaml)
- [traceability.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/traceability.yaml)
