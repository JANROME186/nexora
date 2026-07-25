# BCM-LAB-008 Technical Validation Capability Package

Human-readable companion for the Technical Validation capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-LAB-008
- Domain: DOM-05 Clinical Operations
- Bounded context: `laboratory-results`
- Primary aggregate: `LaboratoryResult` (AGG-009, owned by BCM-LAB-006; this capability holds delegated authority over `technicalValidation` and `criticalFlag`)
- Process reference: HRP-001-P06 Result Validation and Release
- Roadmap group: MVP-MOD-006 Laboratory Workflow
- Priority: Critical

## Purpose

Performs the analytical/technical review of a submitted result (delta check,
reference-range plausibility, unresolved-incident clearance) and either
technically validates it or flags it critical. A critical flag always
triggers a notification/escalation hook toward Notification Management
(BCM-PLT-003), satisfying the "critical result must notify" requirement.
Never captures results, performs medical validation or releases results.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | TechnicalValidationWorklistEntry process record and acceptance/critical-threshold check value objects |
| `business-rules.md` | Numbered rules RN-001..RN-007 |
| `processes.md` | Perform technical validation, flag critical result |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal validation worklist and review panel |
| `mobile-model.md` | Mobile scope (not_required) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Clinical rules modeled

- Validation is blocked by unresolved reliability-affecting incidents (RN-001).
- Segregation of duties: the validator must differ from the capturer when tenant policy requires it (RN-002).
- Critical-threshold values are mandatorily flagged (RN-003) and always trigger a notification/escalation hook (RN-004, aligned to BRM-001-R013).
- This capability writes only `technicalValidation` and `criticalFlag`, never medical validation, release or amendment fields (RN-005).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers the acceptance-check evaluation, segregation-of-duties
enforcement, critical-threshold comparison, the notification hook and the
aggregate boundary rule.
