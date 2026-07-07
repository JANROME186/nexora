# HOP MVP Agent Framework

## Role

Use this guide when an agent is asked to implement or plan part of the Healthcare Operations Platform MVP.

The agent may be any AI or deterministic automation. This document defines repository-driven behavior, not platform-specific behavior.

## Required Context Loading

Load these files before planning:

1. `NEXORA_CODEX_BOOTSTRAP.md`
2. `PROJECT_STATE.yaml`
3. `SOURCE_OF_TRUTH.yaml`
4. `05-delivery/mvp/healthcare-operations-platform-mvp-framework.yaml`
5. `02-platform-definition/business-capabilities/bcm-001/business-capability-map.yaml`
6. `02-platform-definition/business-capabilities/bcm-002/capability-dependency-map.yaml`
7. `02-platform-definition/domain-foundation/context-map/context-map.yaml`
8. `02-platform-definition/domain-foundation/shared-kernel/shared-kernel.yaml`
9. `02-platform-definition/domain-foundation/aggregates/aggregate-catalog.yaml`

## Planning Protocol

For each requested module or capability:

1. Identify capability ids.
2. Confirm their MVP phase and dependency profile.
3. Identify the owning bounded context.
4. Identify aggregate roots and forbidden mutators.
5. Identify required APIs, events, UI surfaces, security rules and tests.
6. Produce a small implementation backlog.
7. Stop if the request requires changing architecture without an ADR.

## Output Contract

Every implementation plan must include:

- Module or capability id.
- Source artifacts consulted.
- Bounded context ownership.
- Aggregate ownership.
- API contracts to create or update.
- UI or mobile surfaces to create or update.
- Domain events to publish or consume.
- Security, audit and privacy rules.
- Tests.
- Traceability updates.

## Guardrails

Agents must not:

- Invent capability ids.
- Depend on chat history.
- Introduce cloud-specific, AI-provider-specific or tool-specific requirements into source artifacts.
- Mutate aggregates outside the owning bounded context.
- Bypass anti-corruption layers for migration, HL7, ASTM, FHIR, DICOM or PACS integration.
- Treat AI suggestions as clinical decisions.

## Preferred Work Order

1. MVP-MOD-001 Platform Foundation.
2. MVP-MOD-002 Diagnostic Catalog.
3. MVP-MOD-003 People and Clinical Master Data.
4. MVP-MOD-004 Front Desk and Care Delivery.
5. MVP-MOD-005 Cashier and Billing Request.
6. MVP-MOD-006 Laboratory Workflow.
7. MVP-MOD-007 Results and Digital Delivery.
8. MVP-MOD-008 MVP Integration and Migration Readiness.

## Completion Signal

An agent may mark a module plan ready only when every capability in the module satisfies the Definition of Ready in:

`05-delivery/mvp/healthcare-operations-platform-mvp-framework.yaml`
