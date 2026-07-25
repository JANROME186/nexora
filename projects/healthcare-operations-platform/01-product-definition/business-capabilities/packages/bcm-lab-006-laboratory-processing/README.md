# BCM-LAB-006 Laboratory Processing Capability Package

Human-readable companion for the Laboratory Processing capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-LAB-006
- Domain: DOM-05 Clinical Operations
- Bounded context: `laboratory-results`
- Primary aggregate: `LaboratoryResult` (AGG-009, owned by this capability)
- Process reference: HRP-001-P05 Sample Collection and Processing
- Roadmap group: MVP-MOD-006 Laboratory Workflow
- Priority: Critical

## Purpose

Owns the LaboratoryResult aggregate: creation from a received sample and a
published analyte, result-value capture (manual or normalized device
message), processing-incident recording and submission for validation.
Technical Validation (BCM-LAB-008), Medical Validation (BCM-LAB-009) and
Result Release (BCM-LAB-010) are sibling capabilities in the same bounded
context with delegated authority over specific named validation/release
fields, mirroring the Sample / BCM-LAB-002 ownership pattern used earlier in
this module.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | LaboratoryResult aggregate and ten value objects covering capture through release/amendment placeholders |
| `business-rules.md` | Numbered rules RN-001..RN-008 |
| `processes.md` | Capture result, record incident, submit for validation |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal processing worklist, capture form and result detail |
| `mobile-model.md` | Mobile scope (not_required) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Clinical rules modeled

- A result can be captured only against a received sample (RN-001, aligned to BRM-001-R010).
- Analyte, unit, reference range and method are captured as immutable snapshots (RN-002, RN-003).
- Device messages are consumed only after normalization by BCM-PLT-004; raw protocol parsing is out of scope here (RN-004, aligned to BRM-001-R016).
- AI may read results but never validates, releases or amends them (RN-006, aligned to BRM-001-R017).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers snapshot capture, plausibility checking, the
device-message anti-corruption boundary, incident-reliability judgment and
the aggregate boundary rule shared with BCM-LAB-008, BCM-LAB-009 and
BCM-LAB-010.
