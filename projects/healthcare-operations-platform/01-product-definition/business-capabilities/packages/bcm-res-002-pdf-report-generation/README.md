# BCM-RES-002 PDF Report Generation Capability Package

Human-readable companion for the PDF Report Generation capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-RES-002
- Domain: DOM-07 Results
- Bounded context: `laboratory-results`
- Primary aggregate: `GeneratedResultReport` (new entity owned by this capability; `LaboratoryResult` AGG-009 is read-only)
- Process reference: HRP-001-P07 Result Report and Digital Delivery
- Roadmap group: MVP-MOD-007 Results and Digital Delivery
- Priority: High

## Purpose

Generates the PDF report artifact for a released result and persists it
through Document Management (BCM-PLT-008). Owns its own
`GeneratedResultReport` record — identifier, version, content hash,
generation provenance — and never mutates `LaboratoryResult`. An amendment
always produces a new report version; the prior one is marked superseded,
never edited or deleted.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | GeneratedResultReport aggregate and DocumentReference value object |
| `business-rules.md` | Numbered rules RN-001..RN-007 |
| `processes.md` | Generate, regenerate and retrieve/verify report |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal report history and regeneration panel |
| `mobile-model.md` | Mobile scope (not_required; deferred to BCM-RES-004) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Key rules modeled

- Reports are generated only from released results (RN-001, aligned to BRM-001-R012).
- Every report has an identifier, version and content hash (RN-002).
- Amendments regenerate a new version; the prior report is superseded, never mutated (RN-003).
- Content hash is re-verified at serve time; a mismatch blocks the serve (RN-004).
- This capability never mutates LaboratoryResult, Sample, Patient or Doctor (RN-005).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers PDF rendering, hash computation and versioning,
amendment-triggered regeneration, integrity re-verification, and the
read-only architecture boundary.
