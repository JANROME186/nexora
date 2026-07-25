# Generated Context Map

> Generated from `02-domain-definition/domain-foundation/context-map/context-map.md`.

This generated artifact summarizes the official context relationships. Do not edit manually.

## High-Level Flow

```mermaid
flowchart LR
    IAM[Identity & Access]
    ORG[Organization Management]
    PAT[Patient Management]
    CAT[Catalog & Test Configuration]
    ORD[Orders & Samples]
    RES[Laboratory Results]
    CASH[Cash & Sales]
    BILL[Billing & Tax]
    INV[Inventory & Procurement]
    IMG[Imaging Operations]
    AI[AI Platform]
    INT[Integration & Interoperability]
    MIG[Data Migration & Portability]

    IAM <-- Shared Kernel --> ORG
    ORD --> PAT
    ORD --> CAT
    RES --> ORD
    CASH --> ORD
    BILL --> CASH
    INV --> CAT
    IMG --> ORD
    AI --> PAT
    AI --> RES
    INT --> ORD
    INT --> RES
    MIG --> PAT
    MIG --> ORD
    MIG --> RES
```
