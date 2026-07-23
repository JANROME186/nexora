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
- [capability-package.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/capability-package.yaml)
- [business-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/business-model.yaml)
- [business-rules.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/business-rules.yaml)
- [processes.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/processes.yaml)
- [events.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/events.yaml)
- [openapi-source.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/openapi-source.yaml)
- [permissions.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/permissions.yaml)
- [ui-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/ui-model.yaml)
- [mobile-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/mobile-model.yaml)
- [test-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/test-model.yaml)
- [observability-model.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/observability-model.yaml)
- [generation-plan.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/generation-plan.yaml)
- [traceability.yaml](file:///C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora/projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/bcm-qlt-002-external-quality-controls/traceability.yaml)
