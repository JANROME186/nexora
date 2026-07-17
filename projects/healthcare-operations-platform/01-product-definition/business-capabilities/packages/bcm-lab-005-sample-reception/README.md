# BCM-LAB-005 Sample Reception Capability Package

Human-readable companion for the Sample Reception capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-LAB-005
- Domain: DOM-05 Clinical Operations
- Bounded context: `orders-samples`
- Primary aggregate: `Sample` (AGG-008, owned by BCM-LAB-002; this capability holds delegated authority over `receptionRecord`, rejection-at-reception and disposal)
- Process reference: HRP-001-P05 Sample Collection and Processing
- Roadmap group: MVP-MOD-006 Laboratory Workflow
- Priority: High

## Purpose

Receives a labeled sample at the laboratory, runs a structured
`ReceptionConditionCheck` (label present, container intact, no visible
hemolysis, within transport window, sufficient volume) and either accepts it
or rejects it with a structured reason. Also manages evidence-preserving
disposal once a sample reaches a terminal state. Never creates, collects or
labels samples — those remain BCM-LAB-002 and BCM-LAB-003.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | ReceptionWorklistEntry process record, condition-check and disposal value objects |
| `business-rules.yaml` | Numbered rules RN-001..RN-007 |
| `processes.yaml` | Receive, reject-at-reception and dispose sample |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal reception worklist, condition checklist and disposal panel |
| `mobile-model.yaml` | Mobile scope (not_required) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA, technical debt |

## Clinical rules modeled

- An unlabeled sample is rejected, not received (RN-001).
- Hemolysis, container damage, insufficient volume or transport-window violation triggers rejection with a structured reason (RN-002, aligned to BRM-001-R010).
- A rejected sample is terminal and can only be disposed, never processed (RN-003).
- Disposal preserves all prior clinical evidence; only disposal metadata is appended (RN-004).

## Technical debt note

`traceability.yaml` documents that this package's real `Sample.status`
values (received, rejected) unblock `TD-BE-010`'s code-level remediation
in `MVP-MOD-006-BE-002`.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers the condition-check evaluation, terminal-state guards,
evidence-preserving disposal and the delegated aggregate-boundary rule.
