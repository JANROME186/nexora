# BCM-ATT-003 Reception Management Capability Package

Human-readable companion for the Reception Management capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-ATT-003
- Domain: DOM-04 Care Delivery
- Bounded context: `orders-samples` (secondary `patient-management`)
- Primary aggregate reference: `DiagnosticOrder` (AGG-007, owned by BCM-LAB-001)
- Process reference: HRP-001-P03 Patient Registration and Order Intake
- Roadmap group: MVP-MOD-004 Front Desk and Care Delivery
- Priority: Critical

## Purpose

Coordinates the front-desk queue: confirms patient identity for walk-in or
scheduled arrivals through a read-only check against BCM-PER-002, links a
checked-in appointment when one exists (BCM-ATT-001), prioritizes the
reception queue and hands the visit to Admission Management (BCM-ATT-004)
once identity is confirmed. It does not own the DiagnosticOrder aggregate
and performs no direct order or patient mutation.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | ReceptionVisit process entity and queue value object |
| `business-rules.md` | Numbered rules RN-001..RN-007 |
| `processes.md` | Start, confirm identity, advance, reprioritize, abandon |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal queue worklist, identity confirmation, detail |
| `mobile-model.md` | Mobile scope (not_required, front-desk only) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers read-only identity confirmation, appointment linkage
validation, tenant-configurable queue prioritization, the admission handoff
boundary and the queue worklist UI.
