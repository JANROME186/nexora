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
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | ResultSearchIndexEntry read projection and access-audit entry |
| `business-rules.md` | Numbered rules RN-001..RN-005 |
| `processes.md` | Projection rebuild, search/worklist query |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal search/worklist and detail screens |
| `mobile-model.md` | Mobile scope (not_required; internal-only surface) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Key rules modeled

- No command is ever issued against LaboratoryResult, Sample, Patient or Doctor (RN-001).
- The read projection updates only from consumed domain events, never direct writes (RN-002).
- Internal visibility is role- and laboratory-scoped (RN-003).
- Every result access is audited (RN-004, aligned to BRM-001-R018).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers the event-sourced projection rebuild, row-level scope
filtering, mandatory access-audit append and the read-only architecture
boundary.
