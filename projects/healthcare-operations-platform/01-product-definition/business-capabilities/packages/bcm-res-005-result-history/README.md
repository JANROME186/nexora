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
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | PatientResultHistoryView read projection |
| `business-rules.yaml` | Numbered rules RN-001..RN-006 |
| `processes.yaml` | Project history entry, view history, update after amendment |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Patient/doctor portal chronological/trend view |
| `mobile-model.yaml` | Mobile scope (result_view_required — shared with BCM-RES-004) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Key rules modeled

- History entries are projected only from authorized deliveries (RN-001, aligned to BRM-001-R014).
- This capability never mutates LaboratoryResult or Patient (RN-002).
- Trend computation never leaks a cross-patient comparison (RN-003).
- Amended results update history only after re-authorization (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers the authorized-only projection rebuild, the read-only
architecture boundary, authorization-scoped trend computation and the
amendment-gated update workflow.
