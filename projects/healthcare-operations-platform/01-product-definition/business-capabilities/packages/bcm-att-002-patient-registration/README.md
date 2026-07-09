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
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | Registration request entity, draft VOs and orchestration outcome |
| `business-rules.yaml` | Numbered rules RN-001..RN-008 |
| `processes.yaml` | Start, commit and cancel registration |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal wizard, list and detail |
| `mobile-model.yaml` | Mobile scope (check_in_later, deferred to COM-MOD-009) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom implementation
covers orchestration of duplicate-detection consultation, cross-capability
commit delegation, atomic representative attach, tenant-configurable consent
requirements, age-of-majority default and the registration wizard UI.
