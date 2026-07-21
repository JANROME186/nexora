# BCM-ATT-006 Quotation Management Capability Package

Human-readable companion for the Quotation Management capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-ATT-006
- Domain: DOM-04 Care Delivery
- Bounded context: `cash-sales` (secondary `catalog-test-configuration`)
- Primary aggregate: `QuotationRequest` (standalone process aggregate; the
  `Sale` aggregate, AGG-010, is deferred to future MVP-MOD-005 and is not a
  dependency of this package)
- Process reference: HRP-001-P03 Patient Registration and Order Intake
- Roadmap group: MVP-MOD-004 Front Desk and Care Delivery
- Priority: High

## Purpose

Produces pre-order price estimates for prospective or existing patients from
published catalog items (BCM-SVC-001/002/003) and price lists (BCM-SVC-009),
applying tenant commercial discount rules within policy limits. A quotation
is a standalone, versionable record that never mutates catalog, price list
or order state. Accepted quotations convert into a diagnostic order through
BCM-LAB-001 CreateDiagnosticOrder; a future Sale conversion path can be
added once MVP-MOD-005 models the Sale aggregate.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | QuotationRequest, quotation lines and pricing/discount value objects |
| `business-rules.yaml` | Numbered rules RN-001..RN-009 |
| `processes.yaml` | Draft, issue, accept, convert, cancel/expire |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal builder, list, detail |
| `mobile-model.yaml` | Mobile scope (not_required, deferred to COM-MOD-011) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## COM-MOD-011 reuse

The existing `startQuotation` operation is reused, anonymously and rate-limited, by the
COM-MOD-011 Public Website and Digital Growth module (RN-009): a public request creates a
draft-state quotation from a ProspectiveContact only, never an issued or accepted one. No new
capability package, aggregate or schema was created; see `traceability.yaml`'s
`cross_module_reuse` entry, which also records COM-MOD-011-DEF's correction of stale
MVP-MOD-004 status pointers found in this package during modeling.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers published-catalog validation, price-list resolution
and pricing snapshot capture, tenant discount policy enforcement, validity-
window enforcement, the cross-capability conversion to BCM-LAB-001 and the
quotation builder UI.
