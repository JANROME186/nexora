# BCM-ATT-004 Admission Management Capability Package

Human-readable companion for the Admission Management capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-ATT-004
- Domain: DOM-04 Care Delivery
- Bounded context: `orders-samples`
- Primary aggregate reference: `DiagnosticOrder` (AGG-007, owned by BCM-LAB-001)
- Process reference: HRP-001-P03 Patient Registration and Order Intake
- Roadmap group: MVP-MOD-004 Front Desk and Care Delivery
- Priority: High

## Purpose

Completes front-desk order intake: gathers catalog selection, referring
doctor, clinical notes and consent/sample-requirement acknowledgement for a
reception visit with confirmed identity, verifies completeness, and commits
the diagnostic order by invoking BCM-LAB-001's CreateDiagnosticOrder,
PriceDiagnosticOrder and AcceptDiagnosticOrder commands. It is the
completeness gate between the front desk and clinical order acceptance and
never persists order state itself.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | AdmissionRequest process entity and catalog-selection value object |
| `business-rules.md` | Numbered rules RN-001..RN-007 |
| `processes.md` | Start, mark ready, commit, reject |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal intake wizard, list, detail |
| `mobile-model.md` | Mobile scope (not_required, front-desk only) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers the reception precondition check, published-catalog
completeness validation, tenant-configurable consent/sample-acknowledgement
gating, the cross-capability commit delegation to BCM-LAB-001 and the
admission intake wizard UI.
