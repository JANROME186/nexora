# BCM-RES-001 Result Management Capability Package

Human-readable companion for the Result Management capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-RES-001
- Domain: DOM-07 Results
- Bounded context: `laboratory-results`
- Primary aggregate: `LaboratoryResult` (AGG-009, owned by BCM-LAB-006; this capability is **read-only**)
- Process reference: HRP-001-P06 Result Validation and Release
- Roadmap group: MVP-MOD-007 Results and Digital Delivery
- Priority: Critical

## Purpose

Provides the internal, role-scoped search and worklist facade over
`LaboratoryResult` for laboratory and clinical staff. Owns no part of the
aggregate — instead it maintains its own `ResultSearchIndexEntry` read
projection, rebuilt from BCM-LAB-006/008/009/010's domain events, and
issues no command against `LaboratoryResult`, `Sample`, `Patient` or
`Doctor`. It is the upstream read source that PDF generation, digital
delivery and critical-result handling build upon.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | ResultSearchIndexEntry read projection and access-audit entry |
| `business-rules.yaml` | Numbered rules RN-001..RN-005 |
| `processes.yaml` | Projection rebuild, search/worklist query |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal search/worklist and detail screens |
| `mobile-model.yaml` | Mobile scope (not_required; internal-only surface) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Key rules modeled

- No command is ever issued against LaboratoryResult, Sample, Patient or Doctor (RN-001).
- The read projection updates only from consumed domain events, never direct writes (RN-002).
- Internal visibility is role- and laboratory-scoped (RN-003).
- Every result access is audited (RN-004, aligned to BRM-001-R018).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers the event-sourced projection rebuild, row-level scope
filtering, mandatory access-audit append and the read-only architecture
boundary.
