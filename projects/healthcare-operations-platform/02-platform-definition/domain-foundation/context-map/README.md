# E03.02 — Context Map

The Context Map defines how Nexora bounded contexts collaborate without duplicating ownership or leaking internal models.

## Relationship Types

- Shared Kernel
- Customer/Supplier
- Published Language
- Anti-Corruption Layer
- Conformist
- Event Subscriber

## Core Rule

A bounded context may consume another context's information only through a declared relationship.

Direct aggregate mutation across contexts is forbidden.
