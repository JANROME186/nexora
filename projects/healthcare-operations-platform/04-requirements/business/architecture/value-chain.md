---
id: BA-VC-001
name: Nexora Enterprise Value Chain
version: 0.1.0
status: Draft
owner: Business Architecture
---

# Nexora Enterprise Value Chain

Nexora supports the complete operational chain of a diagnostic healthcare organization.

```mermaid
flowchart LR
    A[Market Awareness] --> B[Lead Capture]
    B --> C[Quotation]
    C --> D[Appointment]
    D --> E[Patient Reception]
    E --> F[Cashier and Payment]
    F --> G[Order Creation]
    G --> H[Sample Collection]
    H --> I[Laboratory Processing]
    G --> J[Imaging Processing]
    I --> K[Result Validation]
    J --> K
    K --> L[Result Delivery]
    L --> M[Medical Follow-up]
    M --> N[Loyalty and Retention]
    G --> O[Billing]
    H --> P[Inventory Consumption]
    I --> P
    J --> P
    P --> Q[Procurement]
    K --> R[Quality Management]
    O --> S[Administrative Reporting]
    R --> T[Executive Analytics]
```

## Primary Value Streams

| ID | Value Stream | Purpose |
|---|---|---|
| VS-001 | Patient Acquisition | Convert digital and offline interest into scheduled services. |
| VS-002 | Patient Attention | Receive the patient, confirm identity, collect payment and create the diagnostic order. |
| VS-003 | Diagnostic Execution | Perform laboratory and imaging services with traceability and quality control. |
| VS-004 | Result Management | Validate, sign, publish and deliver clinical results. |
| VS-005 | Administrative Management | Manage billing, inventory, purchases, cash control and reporting. |
| VS-006 | Quality and Compliance | Ensure traceability, auditability, compliance and operational quality. |
| VS-007 | Intelligence and Optimization | Use analytics and AI to improve operations, service quality and decision-making. |

## Design Rule

Every functional module must be linked to at least one value stream and one business capability.
