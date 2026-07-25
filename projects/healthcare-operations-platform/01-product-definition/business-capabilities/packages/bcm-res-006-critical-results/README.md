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
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | CriticalResultEscalation aggregate |
| `business-rules.md` | Numbered rules RN-001..RN-006 |
| `processes.md` | Create, acknowledge and escalate an escalation |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal escalation worklist and acknowledgement panel |
| `mobile-model.md` | Mobile scope (not_required) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## Key rules modeled

- Every critical flag produces an escalation, with no exception (RN-001, aligned to BRM-001-R013).
- Unacknowledged escalations auto-advance tier and re-trigger notification on deadline (RN-002).
- Closing requires both acknowledgedBy and acknowledgedAt (RN-003).
- This capability never mutates LaboratoryResult, including the critical flag itself (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers the mandatory-creation rule, deadline-driven tier
progression, the acknowledgement terminal-state guard and the read-only
architecture boundary.
