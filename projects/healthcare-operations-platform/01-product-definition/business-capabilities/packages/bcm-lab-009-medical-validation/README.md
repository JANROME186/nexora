# BCM-LAB-009 Medical Validation Capability Package

Human-readable companion for the Medical Validation capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-LAB-009
- Domain: DOM-05 Clinical Operations
- Bounded context: `laboratory-results`
- Primary aggregate: `LaboratoryResult` (AGG-009, owned by BCM-LAB-006; this capability holds delegated authority over `medicalValidation` only)
- Process reference: HRP-001-P06 Result Validation and Release
- Roadmap group: MVP-MOD-006 Laboratory Workflow
- Priority: Critical

## Purpose

Performs the clinical/medical review of a technically validated result and
confirms it eligible for release. Restricted to a licensed clinical
authority with a verified credential; never performed by AI, per
BRM-001-R017 and context-map rule FORBID-CTX-002. Never captures results,
performs technical validation or releases results.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | MedicalValidationWorklistEntry process record and clinical-note/licensed-authority value objects |
| `business-rules.yaml` | Numbered rules RN-001..RN-006 |
| `processes.yaml` | Perform medical validation |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal validation worklist and review panel |
| `mobile-model.yaml` | Mobile scope (not_required; deferred to COM-MOD-009 if ever built) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Clinical rules modeled

- Medical validation requires prior technical validation (RN-001).
- Only an actor with a verified, active medical credential may validate (RN-002).
- AI can never perform medical validation, under any tenant configuration (RN-003, aligned to BRM-001-R017).
- This capability writes only `medicalValidation`, never any other LaboratoryResult field (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers the technical-validation precondition, licensed-
authority verification, the hard AI-exclusion boundary and the aggregate
boundary rule.
