# BCM-RES-006 Critical Results Capability Package

Human-readable companion for the Critical Results capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-RES-006
- Domain: DOM-07 Results
- Bounded context: `laboratory-results`
- Primary aggregate: `CriticalResultEscalation` (new entity owned by this capability; `LaboratoryResult` AGG-009 is read-only)
- Process reference: HRP-001-P06 Result Validation and Release
- Roadmap group: MVP-MOD-007 Results and Digital Delivery
- Priority: Critical

## Purpose

Tracks the traceable escalation and acknowledgement lifecycle required for
every critical result flagged by BCM-LAB-008. This is the "traceable
notification or escalation record" required by BRM-001-R013 — distinct from
the notification message itself (BCM-RES-007) and from the technical act of
flagging (BCM-LAB-008). An unacknowledged escalation automatically advances
through tiers on a deadline. Never mutates `LaboratoryResult`.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | CriticalResultEscalation aggregate |
| `business-rules.yaml` | Numbered rules RN-001..RN-006 |
| `processes.yaml` | Create, acknowledge and escalate an escalation |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal escalation worklist and acknowledgement panel |
| `mobile-model.yaml` | Mobile scope (not_required) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Key rules modeled

- Every critical flag produces an escalation, with no exception (RN-001, aligned to BRM-001-R013).
- Unacknowledged escalations auto-advance tier and re-trigger notification on deadline (RN-002).
- Closing requires both acknowledgedBy and acknowledgedAt (RN-003).
- This capability never mutates LaboratoryResult, including the critical flag itself (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers the mandatory-creation rule, deadline-driven tier
progression, the acknowledgement terminal-state guard and the read-only
architecture boundary.
