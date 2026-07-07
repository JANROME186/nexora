# ADR-019: Cash, Sales & Cash Closing as a Dedicated Capability

## Status

Accepted

## Context

Financial operations require strict authorization, auditability, branch scoping and reconciliation. Combining cash logic directly inside orders or billing would create unclear ownership and weak financial controls.

## Decision

Nexora will implement Cash, Sales & Cash Closing Management as CAP-008, a dedicated business capability responsible for sales, payments, cancellations, refunds, cash drawer sessions and cash closing. Fiscal invoice issuance remains outside this capability and belongs to Billing & Tax Compliance.

## Consequences

- Cash operations can evolve independently from clinical operations.
- Orders can depend on payment status without owning payment logic.
- Billing can consume payment completion events without duplicating cash behavior.
- Audit and authorization rules remain centralized.
