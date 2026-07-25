# Integration Patterns

**Artifact ID:** IIA-PAT-001
**Version:** 0.20.0

## Primary Patterns

### 1. Adapter Pattern

Used to isolate Nexora from external protocols and vendor-specific APIs.

### 2. Canonical Message Pattern

Used to normalize external payloads into stable internal structures.

### 3. Outbox Pattern

Used to reliably publish domain events to external systems.

### 4. Inbox Pattern

Used to safely process external messages with idempotency.

### 5. Dead Letter Queue

Used to isolate messages that cannot be processed.

### 6. Retry with Backoff

Used for transient failures.

### 7. Reconciliation Process

Used when external and internal systems may become inconsistent.

### 8. Anti-Corruption Layer

Used between Nexora domains and external systems.

### 9. Contract First API

Used for all external and internal service APIs.

### 10. Event-Driven Integration

Used when processes cross bounded contexts or external systems.
