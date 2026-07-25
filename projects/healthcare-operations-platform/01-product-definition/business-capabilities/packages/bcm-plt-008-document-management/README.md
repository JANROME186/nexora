# Document Management (BCM-PLT-008)

## Overview
The **Document Management** capability package provides generic binary document storage, content integrity verification (SHA-256), retention policy tracking, legal hold locking, and compliance evidence bundling across Healthcare Operations Platform bounded contexts.

## Extension Note (COM-MOD-013)
Extended under **COM-MOD-013 Advanced Quality and Compliance** to support accredited regulatory document retention rules (5-year / 10-year minimum schedules for clinical reports, EQA certificates, CAPA evidence, and audit reports), legal hold locking (`legalHold`), compliance tagging, and compliance evidence package bundling (`ComplianceEvidencePackage`).

## Key Functions
- **Provider-Agnostic Storage**: Stores document bytes behind `DocumentStoragePort` with local filesystem or object-storage adapters.
- **SHA-256 Integrity Check**: Computes content hash on upload and verifies hash integrity on retrieval.
- **Retention & Legal Hold**: Enforces retention schedules and prevents deletion/disposal when `legalHold=true`.
- **Compliance Evidence Bundling**: Bundles related documents for EQA, CAPA, and audit inspections into downloadable manifests.

## Bounded Context & Aggregates
- **Bounded Context**: `document-management`
- **Primary Aggregate**: `StoredDocument` (AGG-023)
- **Roadmap Group**: `COM-MOD-013`

## Artifact Index
- [capability-package.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/capability-package.md)
- [business-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/business-model.md)
- [business-rules.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/business-rules.md)
- [processes.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/processes.md)
- [events.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/events.md)
- [openapi-source.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/openapi-source.md)
- [permissions.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/permissions.md)
- [ui-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/ui-model.md)
- [mobile-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/mobile-model.md)
- [test-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/test-model.md)
- [observability-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/observability-model.md)
- [generation-plan.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/generation-plan.md)
- [traceability.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-plt-008-document-management/traceability.md)
