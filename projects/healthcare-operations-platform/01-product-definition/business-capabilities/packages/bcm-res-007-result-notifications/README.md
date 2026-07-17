# BCM-RES-007 Result Notifications Capability Package

Human-readable companion for the Result Notifications capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-RES-007
- Domain: DOM-07 Results
- Bounded context: `notifications`
- Primary aggregate: `ResultNotificationRequest` (new entity owned by this capability; `LaboratoryResult` AGG-009 is read-only)
- Process reference: HRP-001-P07 Result Report and Digital Delivery
- Roadmap group: MVP-MOD-007 Results and Digital Delivery
- Priority: High

## Purpose

Owns the business decision of when and what to notify a patient or doctor
about a result — delivery available, critical result, or amendment —
composing the notification content and submitting it to BCM-PLT-003 for
provider-agnostic dispatch. This capability decides the "what and why";
BCM-PLT-003 decides the "how". Never mutates `LaboratoryResult`, `Patient`
or `Doctor`.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | ResultNotificationRequest aggregate |
| `business-rules.yaml` | Numbered rules RN-001..RN-006 |
| `processes.yaml` | Compose delivered/critical notification, track dispatch status |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal notification history |
| `mobile-model.yaml` | Mobile scope (not_required; deferred to BCM-RES-004) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Key rules modeled

- Delivered notifications never precede delivery authorization (RN-001).
- Every critical result produces a notification request, with no exception (RN-002, aligned to BRM-001-R013).
- All physical dispatch is delegated to BCM-PLT-003 (RN-003).
- This capability never mutates LaboratoryResult, Patient or Doctor (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers trigger-specific composition rules, the mandatory
critical-notification rule, the content/dispatch boundary and the read-only
architecture boundary.
