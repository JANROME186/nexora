# BCM-LAB-003 Sample Labeling Capability Package

Human-readable companion for the Sample Labeling capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-LAB-003
- Domain: DOM-05 Clinical Operations
- Bounded context: `orders-samples`
- Primary aggregate: `Sample` (AGG-008, owned by BCM-LAB-002; this capability holds delegated authority over `labelInfo` only)
- Process reference: HRP-001-P05 Sample Collection and Processing
- Roadmap group: MVP-MOD-006 Laboratory Workflow
- Priority: High

## Purpose

Prints and confirms the specimen barcode label for a collected sample. Models
its own `LabelPrintJob` process record (print queue, template selection,
reprint tracking) and, once a label mismatch check passes, is the sole
capability authorized to invoke `AssignSpecimenLabel` on the Sample
aggregate owned by BCM-LAB-002 — mirroring the `ReceptionVisit` /
`DiagnosticOrder` satellite pattern already used by BCM-ATT-003 in
MVP-MOD-004.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | LabelPrintJob process record and mismatch-check value object |
| `business-rules.md` | Numbered rules RN-001..RN-006 |
| `processes.md` | Print, confirm and reprint specimen label |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal label action panel |
| `mobile-model.md` | Mobile scope (not_required; deferred to BCM-LAB-002) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers the label-print precondition check, mismatch
detection, the delegated aggregate-boundary rule and relabeling override
capture.
