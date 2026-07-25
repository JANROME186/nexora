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
- [capability-package.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/capability-package.md)
- [business-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/business-model.md)
- [business-rules.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/business-rules.md)
- [processes.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/processes.md)
- [events.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/events.md)
- [openapi-source.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/openapi-source.md)
- [permissions.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/permissions.md)
- [ui-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/ui-model.md)
- [mobile-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/mobile-model.md)
- [test-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/test-model.md)
- [observability-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/observability-model.md)
- [generation-plan.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/generation-plan.md)
- [traceability.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-007-audit-trail/traceability.md)
