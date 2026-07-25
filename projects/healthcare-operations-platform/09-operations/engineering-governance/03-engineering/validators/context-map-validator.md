# Context Map Validator

## Purpose

Validate that bounded context relationships are explicit, allowed and traceable.

## Validation Rules

### CTX-VAL-001
Every cross-context dependency must exist in `context-map.md`.

### CTX-VAL-002
Every relationship must declare:
- id
- source
- target
- relationship
- notes

### CTX-VAL-003
Customer/Supplier relationships must identify the supplier.

### CTX-VAL-004
Anti-corruption layer relationships must define a published language or canonical model.

### CTX-VAL-005
No context may directly mutate another context aggregate.

### CTX-VAL-006
Migration and integration contexts must enter core domains through ACL and validation.

### CTX-VAL-007
AI Platform must never bypass clinical approval workflows.

### CTX-VAL-008
Shared Kernel changes require architecture review.
