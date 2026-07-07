# State Machines

## Sale State

```mermaid
stateDiagram-v2
  [*] --> Draft
  Draft --> PendingPayment
  PendingPayment --> PartiallyPaid
  PendingPayment --> Paid
  PartiallyPaid --> Paid
  Paid --> Cancelled
  Paid --> Refunded
  Paid --> Adjusted
  Cancelled --> [*]
  Refunded --> [*]
```

## Cash Drawer Session State

```mermaid
stateDiagram-v2
  [*] --> Opened
  Opened --> Suspended
  Suspended --> Opened
  Opened --> PendingClose
  PendingClose --> Closed
  PendingClose --> Reopened
  Reopened --> PendingClose
  Closed --> Approved
  Approved --> [*]
```
