# BCM-RES-004 Digital Delivery Capability Package

Human-readable companion for the Digital Delivery capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-RES-004
- Domain: DOM-07 Results
- Bounded context: `laboratory-results`
- Primary aggregate: `ResultDeliveryTicket` (new entity owned by this capability; `LaboratoryResult` AGG-009 is read-only)
- Process reference: HRP-001-P07 Result Report and Digital Delivery
- Roadmap group: MVP-MOD-007 Results and Digital Delivery
- Priority: High

## Purpose

Delivers a released result and its report to authorized patient, doctor and
mobile channels. Enforces that only released results are ever delivered
externally, that patients (and representatives) see only their own or
represented results, and that doctors see only results linked to their
referral or treatment relationship. Never mutates `LaboratoryResult`,
`Patient` or `Doctor`; notification dispatch is delegated to BCM-RES-007 and
BCM-PLT-003, keeping delivery decoupled from any specific provider.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | ResultDeliveryTicket aggregate and DeliveryAuthorizationCheck value object |
| `business-rules.yaml` | Numbered rules RN-001..RN-008 |
| `processes.yaml` | Authorize delivery, view result, withhold on amendment |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Patient/doctor portal released-results list and detail |
| `mobile-model.yaml` | Mobile scope (result_view_required — this module's owner of that requirement) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Key rules modeled

- Only released results are ever delivered externally (RN-001, aligned to BRM-001-R012).
- Patients see only their own results (RN-002); representatives require active, verified authorization (RN-003, aligned to BRM-001-R015); doctors see only referral-linked results (RN-004) — together aligned to BRM-001-R014.
- Amendments withhold existing deliveries until re-authorization (RN-005).
- This capability never mutates LaboratoryResult, Patient or Doctor (RN-006).
- Every view is recorded (RN-007).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers the multi-recipient authorization check, the
amendment withhold/reauthorize workflow, view-state recording and the
read-only architecture boundary.
