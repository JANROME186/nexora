# BCM-PER-001 Person Management Capability Package

Human-readable companion for the Person Management capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-PER-001
- Domain: DOM-02 People
- Bounded context: `patient-management` (primary) with secondary `medical-staff`
- Primary aggregate reference: shared person concepts (no owning aggregate)
- Roadmap group: MVP-MOD-003 People and Clinical Master Data
- Priority: High

## Purpose

Defines the shared Person master-data concepts, natural-key normalization,
duplicate detection service and cross-context person search read model used by
Patient (BCM-PER-002), Doctor (BCM-PER-003) and Patient Registration
(BCM-ATT-002). Person Management does not own an aggregate; it publishes a
projection that keeps Patient and Doctor searchable through the same
tenant-scoped read model without violating bounded-context ownership rules.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | Shared value objects, read model, domain services, invariants |
| `business-rules.yaml` | Numbered rules RN-001..RN-007 |
| `processes.yaml` | Detection, index rebuild and cross-context merge coordination |
| `events.yaml` | Domain and integration events with published language |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal screens and states |
| `mobile-model.yaml` | Mobile scope (not_required) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom implementation
covers cross-context uniqueness enforcement, confidence scoring, projection
idempotence, national identifier hashing and merge coordination workflow.
