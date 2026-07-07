# State Machines

## Result State Machine

```mermaid
stateDiagram-v2
  [*] --> Pending
  Pending --> InProgress
  InProgress --> Draft
  Draft --> TechnicalValidated
  TechnicalValidated --> ClinicalValidationRequired
  ClinicalValidationRequired --> ClinicalValidated
  TechnicalValidated --> ReadyForRelease
  ClinicalValidated --> ReadyForRelease
  ReadyForRelease --> Released
  Released --> Amended
  Amended --> ClinicalValidationRequired
  Draft --> Rejected
  InProgress --> Rejected
```

## Report State Machine

```mermaid
stateDiagram-v2
  [*] --> NotGenerated
  NotGenerated --> Generated
  Generated --> Signed
  Signed --> Published
  Published --> Delivered
  Published --> Revoked
  Delivered --> Revoked
```
