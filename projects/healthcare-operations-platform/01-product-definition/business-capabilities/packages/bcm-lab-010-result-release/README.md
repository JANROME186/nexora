# BCM-LAB-010 Result Release Capability Package

Human-readable companion for the Result Release capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-LAB-010
- Domain: DOM-05 Clinical Operations
- Bounded context: `laboratory-results`
- Primary aggregate: `LaboratoryResult` (AGG-009, owned by BCM-LAB-006; this capability holds delegated authority over `releaseRecord` and `amendments`)
- Process reference: HRP-001-P06 Result Validation and Release
- Roadmap group: MVP-MOD-006 Laboratory Workflow
- Priority: Critical

## Purpose

Releases a medically validated result, making it eligible for downstream
report generation and delivery in MVP-MOD-007, and manages post-release
corrections through an explicit, licensed-authority-gated amendment
workflow. A released result's value is immutable except through
`ResultAmendment` — clinical evidence is never edited in place or deleted.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | ResultReleaseWorklistEntry process record and eligibility/amendment value objects |
| `business-rules.md` | Numbered rules RN-001..RN-007 |
| `processes.md` | Release result, amend result |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal release worklist, eligibility panel and amendment form |
| `mobile-model.md` | Mobile scope (not_required; deferred to MVP-MOD-007) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Clinical rules modeled

- Release requires prior medical validation (RN-001).
- A result linked to a rejected sample can never be released (RN-002, aligned to BRM-001-R010).
- Released values are immutable; corrections are new amendment events, never in-place edits (RN-003).
- Amendments require licensed authority and a structured reason (RN-004).
- This capability writes only `releaseRecord` and `amendments`, never the result value or validation fields (RN-005).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers the release-eligibility check, the immutable-value
guard, licensed-authority-gated amendments and the aggregate boundary rule.
