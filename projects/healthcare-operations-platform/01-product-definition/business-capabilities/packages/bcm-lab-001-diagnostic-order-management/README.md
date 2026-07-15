# BCM-LAB-001 Diagnostic Order Management Capability Package

Human-readable companion for the Diagnostic Order Management capability
package. The YAML models in this folder are the authoritative source of
truth.

## Capability

- ID: BCM-LAB-001
- Domain: DOM-05 Clinical Operations
- Bounded context: `orders-samples`
- Primary aggregate: `DiagnosticOrder` (AGG-007, owned by this capability)
- Process reference: HRP-001-P03 Patient Registration and Order Intake
- Roadmap group: MVP-MOD-004 Front Desk and Care Delivery
- Priority: Critical

## Purpose

Owns the DiagnosticOrder aggregate end to end: creation, pricing,
acceptance, cancellation and completion. Every order captures immutable
snapshots of patient (BCM-PER-002), doctor (BCM-PER-003), branch
(BCM-ORG-003), catalog (BCM-SVC-001/002/003) and price list (BCM-SVC-009)
state at order time, so downstream reception, admission, cashier, sample
collection and result modules never depend on live master-data mutation.
Appointment Scheduling (BCM-ATT-001), Reception Management (BCM-ATT-003),
Admission Management (BCM-ATT-004) and Quotation Management (BCM-ATT-006)
orchestrate around this capability and delegate order mutation to its
commands, mirroring the BCM-ATT-002 / BCM-PER-002 orchestration pattern
already used in MVP-MOD-003.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | DiagnosticOrder aggregate, order lines and five immutable snapshot value objects |
| `business-rules.yaml` | Numbered rules RN-001..RN-009 |
| `processes.yaml` | Create, price, accept, cancel and complete order |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal creation wizard, list, detail and actions |
| `mobile-model.yaml` | Mobile scope (not_required, deferred to COM-MOD-009) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers multi-source snapshot capture, published-catalog
validation, price-list resolution, aggregate boundary enforcement, terminal-
state immutability and the order creation wizard UI.
