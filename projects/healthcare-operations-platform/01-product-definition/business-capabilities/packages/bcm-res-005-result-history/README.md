# BCM-RES-005 Result History Capability Package

Human-readable companion for the Result History capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-RES-005
- Domain: DOM-07 Results
- Bounded context: `laboratory-results`
- Primary aggregate: `PatientResultHistoryView` (new read-model entity owned by this capability; `LaboratoryResult` AGG-009 and `Patient` AGG-001 are read-only)
- Process reference: HRP-001-P07 Result Report and Digital Delivery
- Roadmap group: MVP-MOD-007 Results and Digital Delivery
- Priority: High

## Purpose

Provides patients and referring doctors a chronological, trend-aware view
across multiple released results over time (e.g. tracking an analyte across
visits), distinct from BCM-RES-004's single-result delivery view. Never
mutates `LaboratoryResult` or `Patient`. Also the designated upstream read
source for future AI-assisted trend analysis (BCM-AI-005/006), which may
only read, never write, this history.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | PatientResultHistoryView read projection |
| `business-rules.md` | Numbered rules RN-001..RN-006 |
| `processes.md` | Project history entry, view history, update after amendment |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Patient/doctor portal chronological/trend view |
| `mobile-model.md` | Mobile scope (result_view_required — shared with BCM-RES-004) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Key rules modeled

- History entries are projected only from authorized deliveries (RN-001, aligned to BRM-001-R014).
- This capability never mutates LaboratoryResult or Patient (RN-002).
- Trend computation never leaks a cross-patient comparison (RN-003).
- Amended results update history only after re-authorization (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers the authorized-only projection rebuild, the read-only
architecture boundary, authorization-scoped trend computation and the
amendment-gated update workflow.
