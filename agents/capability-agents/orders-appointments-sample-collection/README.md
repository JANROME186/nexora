# Orders, Appointments & Sample Collection Agent

## Purpose

Generate, validate or update artifacts for CAP-006 without bypassing OpenAPI, business rules or state machines.

## Required Context

1. `business/capabilities/orders-appointments-sample-collection/capability.yaml`
2. `business/capabilities/orders-appointments-sample-collection/README.md`
3. `contracts/openapi/orders/orders.openapi.yaml`
4. `contracts/openapi/appointments/appointments.openapi.yaml`
5. `contracts/openapi/samples/samples.openapi.yaml`
6. CAP-001 through CAP-005 metadata.
7. IAM permission model.

## Rules

- Do not generate backend endpoints that are not present in OpenAPI.
- Do not allow sample operations without branch-level authorization.
- Do not put payment rules directly in UI; call the application/domain policy.
- Do not use AI as a required dependency for order confirmation.
- Always emit domain events for state transitions.
