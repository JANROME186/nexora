# BCM-ATT-001 Appointment Scheduling Capability Package

Human-readable companion for the Appointment Scheduling capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-ATT-001
- Domain: DOM-04 Care Delivery
- Bounded context: `orders-samples`
- Primary aggregate reference: `DiagnosticOrder` (AGG-007, owned by BCM-LAB-001)
- Process reference: HRP-001-P03 Patient Registration and Order Intake
- Roadmap group: MVP-MOD-004 Front Desk and Care Delivery
- Priority: High

## Purpose

Manages appointment slot requests, confirmation, check-in, cancellation and
no-show tracking for a branch. Validates branch operational status and
overlap before confirming a slot, and surfaces preparation instructions and
published catalog availability for the selected service. It does not own
the DiagnosticOrder aggregate; on check-in it hands off the appointment as
an intake reference so Reception Management (BCM-ATT-003) and Admission
Management (BCM-ATT-004) can create the order through BCM-LAB-001.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | AppointmentSlot process entity and catalog/preparation value objects |
| `business-rules.yaml` | Numbered rules RN-001..RN-008 |
| `processes.yaml` | Request, confirm, check in, cancel and no-show |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal scheduler, calendar and detail |
| `mobile-model.yaml` | Mobile scope (check_in_later, deferred to COM-MOD-009) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## COM-MOD-011 reuse

The existing `requestAppointment` operation is reused, anonymously and rate-limited, by the
COM-MOD-011 Public Website and Digital Growth module (RN-008): a public request creates a
requested-state slot from a ProspectiveContact only, never a confirmed booking. No new
capability package, aggregate or schema was created; see `traceability.yaml`'s
`cross_module_reuse` entry. That entry also records COM-MOD-011-DEF's correction of stale
COM-MOD-009/MVP-MOD-004 status pointers found in this package during modeling.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers branch operational-status validation, appointment
overlap detection, published-catalog validation, order-creation handoff,
no-show grace-period policy and the scheduler wizard UI.
