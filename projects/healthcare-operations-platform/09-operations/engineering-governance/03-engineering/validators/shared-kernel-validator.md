# Shared Kernel Validator

## Purpose

Prevent uncontrolled Shared Kernel growth and ensure shared concepts remain stable.

## Validation Rules

### SHK-VAL-001
Every Shared Kernel item must have an ID, name, owner, version and status.

### SHK-VAL-002
A value object must define fields and validation rules.

### SHK-VAL-003
A concept may be shared only if used by at least three bounded contexts or approved by architecture exception.

### SHK-VAL-004
Business behavior must not be introduced into the Shared Kernel.

### SHK-VAL-005
Persistence implementation details are forbidden.

### SHK-VAL-006
Breaking changes require a major version and migration policy.

### SHK-VAL-007
Country-specific behavior must be delegated to Country Packs.
