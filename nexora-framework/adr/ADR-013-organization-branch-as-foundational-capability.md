# ADR-013 Organization & Branch as Foundational Capability

## Status
Accepted

## Context
Nexora is multi-tenant and multi-branch. Patients, orders, caja, inventory, users and reports require a stable organizational model.

## Decision
Organization & Branch Management is defined as `CAP-002` and becomes an upstream foundational capability.

## Consequences
- All operational transactions must reference tenant and branch context when applicable.
- Tenant isolation is mandatory.
- Branch status controls operational availability.
- Country packs and licensing may modify allowed branch services without changing domain logic.
