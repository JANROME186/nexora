# Aggregate Validator

## Purpose

Validate aggregate ownership, invariants and cross-context restrictions.

## Validation Rules

### AGG-VAL-001
Every aggregate must have one owning bounded context.

### AGG-VAL-002
Every aggregate must define an identifier.

### AGG-VAL-003
Every aggregate must define primary domain events.

### AGG-VAL-004
No aggregate may be owned by more than one bounded context.

### AGG-VAL-005
Foreign context mutations are forbidden.

### AGG-VAL-006
Read models cannot be aggregate owners.

### AGG-VAL-007
Migration jobs must not write directly to core aggregate storage.

### AGG-VAL-008
AI capabilities must not mutate clinical aggregates directly.
