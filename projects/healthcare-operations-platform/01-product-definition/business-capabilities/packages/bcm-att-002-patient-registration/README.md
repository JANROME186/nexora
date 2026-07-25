# BCM-ATT-002 Patient Registration Capability Package

Human-readable companion for the Patient Registration capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-ATT-002
- Domain: DOM-04 Care Delivery
- Bounded context: `patient-management`
- Primary aggregate reference: `Patient` (AGG-001, owned by BCM-PER-002)
- Process reference: HRP-001-P03 Patient Registration and Order Intake
- Roadmap group: MVP-MOD-003 People and Clinical Master Data
- Priority: Critical

## Purpose

Coordinates the operational patient registration flow at the front desk or
through a portal handoff. Consults Person Management (BCM-PER-001) duplicate
detection, delegates state mutation to Patient Management (BCM-PER-002)
aggregate commands and handles representative attachment and initial consent
capture during commit. It does not own the Patient aggregate; it orchestrates
its creation or reuse.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | Registration request entity, draft VOs and orchestration outcome |
| `business-rules.md` | Numbered rules RN-001..RN-008 |
| `processes.md` | Start, commit and cancel registration |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal wizard, list and detail |
| `mobile-model.md` | Mobile scope (check_in_later, deferred to COM-MOD-009) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom implementation
covers orchestration of duplicate-detection consultation, cross-capability
commit delegation, atomic representative attach, tenant-configurable consent
requirements, age-of-majority default and the registration wizard UI.
