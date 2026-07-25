# External Quality Controls (BCM-QLT-002)

## Overview
The **External Quality Controls** capability package provides structured management of participation in External Quality Assessment (EQA) and Proficiency Testing (PT) schemes from recognized providers (e.g., CAP, RIQAS, PEEC, UK NEQAS).

## Key Functions
- **Survey Cycle Management**: Register program subscriptions, survey rounds, target sample codes, and submission deadlines.
- **Result Recording & Submission**: Capture laboratory measurement results for external survey samples prior to submission deadlines.
- **Evaluation & Peer Group Scoring**: Record provider evaluation results, peer group mean/SD, z-score, and Standard Deviation Index (SDDI).
- **Out-of-Range Gating & CAPA Trigger**: Automatically classify evaluations (acceptable, warning, unacceptable) and trigger CAPA investigations (BCM-QLT-006) when z-score thresholds (|z| > 2.0) are violated.
- **Document & Certificate Archival**: Link immutable provider PDF evaluation reports and accreditation certificates via Document Management (BCM-PLT-008).

## Bounded Context & Aggregates
- **Bounded Context**: `external-quality-compliance`
- **Primary Aggregate**: `ExternalQualityEvaluation` (AGG-020)
- **Roadmap Group**: `COM-MOD-013` (Advanced Quality and Compliance)

## Artifact Index
- [capability-package.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/capability-package.md)
- [business-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/business-model.md)
- [business-rules.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/business-rules.md)
- [processes.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/processes.md)
- [events.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/events.md)
- [openapi-source.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/openapi-source.md)
- [permissions.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/permissions.md)
- [ui-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/ui-model.md)
- [mobile-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/mobile-model.md)
- [test-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/test-model.md)
- [observability-model.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/observability-model.md)
- [generation-plan.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/generation-plan.md)
- [traceability.md](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/traceability.md)
