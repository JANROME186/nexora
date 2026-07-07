---
id: BPMN-001
name: Patient Intake Process
version: 0.1.0
status: Draft
owner: Business Architecture
related_journey: JRN-001
related_capabilities:
  - CAP-001
  - CAP-007
  - CAP-010
---

# Patient Intake Process

This process starts when a patient arrives at a branch or begins digital check-in and ends when the diagnostic order is ready for sample collection or imaging.

```mermaid
flowchart TD
    A[Start: Patient arrives or checks in] --> B{Existing patient?}
    B -- Yes --> C[Search and confirm patient identity]
    B -- No --> D[Register patient]
    C --> E[Update contact and demographic data]
    D --> E
    E --> F{Minor or legally dependent?}
    F -- Yes --> G[Register responsible tutor/guardian]
    F -- No --> H[Continue]
    G --> H
    H --> I[Capture or confirm consent]
    I --> J[Select tests, panels or imaging studies]
    J --> K[Validate preparation requirements]
    K --> L{Payment required now?}
    L -- Yes --> M[Send to cashier]
    L -- No --> N[Create order]
    M --> O[Register payment]
    O --> N
    N --> P[Generate order number and labels]
    P --> Q[Assign sample/imaging queue]
    Q --> R[End: Ready for collection or imaging]
```

## Roles

- Patient.
- Receptionist.
- Cashier.
- Sample Collection Technician.
- Branch Supervisor.

## Key Rules

- Patient identity must be confirmed before creating an order.
- A minor patient must have a responsible tutor/guardian registered.
- Consent must be captured when required by study type, regulation or laboratory policy.
- Preparation requirements must be visible before payment and before sample collection.
- Labels must not be generated without an order number.
- Cancelled orders must preserve audit trail and cancellation reason.
