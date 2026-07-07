# State Machines

## Inventory Item State

```mermaid
stateDiagram-v2
    [*] --> Draft
    Draft --> Active
    Active --> Suspended
    Suspended --> Active
    Active --> Discontinued
    Suspended --> Discontinued
    Discontinued --> Archived
```

## Purchase Order State

```mermaid
stateDiagram-v2
    [*] --> Draft
    Draft --> PendingApproval
    PendingApproval --> Approved
    PendingApproval --> Rejected
    Approved --> SentToSupplier
    SentToSupplier --> PartiallyReceived
    SentToSupplier --> Received
    PartiallyReceived --> Received
    Approved --> Cancelled
    SentToSupplier --> Cancelled
    Received --> Closed
```

## Lot State

```mermaid
stateDiagram-v2
    [*] --> Received
    Received --> Available
    Available --> Reserved
    Reserved --> Available
    Available --> Quarantined
    Quarantined --> Available
    Quarantined --> Disposed
    Available --> Expired
    Expired --> Disposed
```
