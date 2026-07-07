# ADR-018: Define Laboratory Results & Report Management as a Core Capability

Status: Accepted

## Context
Result capture, validation, reporting and delivery are central clinical workflows. They require strong auditability, state control, versioning and security.

## Decision
Define CAP-007 as a core business capability with explicit rules, decision tables, state machines, domain events and OpenAPI contract.

## Consequences
- Results become a first-class domain capability.
- Report release is governed by state transitions and authorization.
- Critical values and amendments require auditable workflows.
