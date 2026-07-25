# BCM-LAB-002 Sample Collection Capability Package

Human-readable companion for the Sample Collection capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-LAB-002
- Domain: DOM-05 Clinical Operations
- Bounded context: `orders-samples`
- Primary aggregate: `Sample` (AGG-008, owned by this capability)
- Process reference: HRP-001-P05 Sample Collection and Processing
- Roadmap group: MVP-MOD-006 Laboratory Workflow
- Priority: Critical

## Purpose

Owns the Sample aggregate: creation from an accepted DiagnosticOrder line,
collection data capture (collector, method, site, container, timestamp),
immutable patient-identity and sample-requirement snapshots, and the first
chain-of-custody event. Sample Labeling (BCM-LAB-003) and Sample Reception
(BCM-LAB-005) are sibling capabilities in the same bounded context with
delegated authority over specific named fields (`labelInfo`;
`receptionRecord`, rejection-at-reception, disposal), so the aggregate
always has exactly one authorized mutator per field. This mirrors the
DiagnosticOrder / BCM-LAB-001 ownership pattern from MVP-MOD-004.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | Sample aggregate, collection value objects and chain-of-custody |
| `business-rules.md` | Numbered rules RN-001..RN-009 |
| `processes.md` | Generate worklist, collect sample, reject at collection |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal worklist, collection form and sample detail |
| `mobile-model.md` | Mobile scope (sample_collection_later; intended future flows modeled) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA, technical debt |

## Clinical rules modeled

- A sample must be associated with a valid, accepted order (RN-001).
- A sample must be traceably identified before reception/processing (RN-003).
- Every state transition appends an audit chain-of-custody event (RN-004).
- A rejected sample records a structured reason and never advances (RN-005).
- Only BCM-LAB-002/003/005 may mutate Sample; no other capability or bounded context may (RN-006).
- Clinical evidence is never deleted; corrections are new custody events (RN-009).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers snapshot capture, order-line acceptance validation,
chain-of-custody enforcement, structured rejection handling and the
aggregate boundary rule shared with BCM-LAB-003 and BCM-LAB-005.

## Technical debt note

`traceability.md` documents that this package's SampleStatus model
satisfies the modeling precondition recorded in `TD-BE-010`, unblocking that
debt item's code-level remediation in `MVP-MOD-006-BE-002`.
