---
id: PB-002
name: Create Business Capability
version: 0.1.0
status: Draft
owner: Nexora Engineering Framework
---

# Playbook: Create Business Capability

## Purpose

Create a new business capability and connect it to the Product Knowledge Graph.

## Steps

1. Read `CONSTITUTION.md`.
2. Read `PROJECT_MANIFEST.md`.
3. Read `business/architecture/value-chain.md`.
4. Identify the value stream where the capability participates.
5. Create or update `business/capabilities/business-capability-model.md`.
6. Create the capability file under `business/capabilities/<capability-name>.md`.
7. Define objectives, roles, processes, rules, events and metrics.
8. Create traceability entries under `business/traceability/`.
9. Update `KNOWLEDGE_INDEX.md`.
10. Update `PROJECT_STATE.md`.
11. Add changelog entry.

## Quality Gates

- Capability has a stable ID.
- Capability has at least one process or journey.
- Capability has business rules.
- Capability has traceability to downstream artifacts or explicit TODOs.
- No API or database design is generated before the capability is approved.
