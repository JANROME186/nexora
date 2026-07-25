# BCM-SVC-001 Diagnostic Service Catalog Capability Package

Human-readable companion for the Diagnostic Service Catalog capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-SVC-001
- Domain: DOM-03 Diagnostic Services
- Bounded context: `catalog-test-configuration`
- Primary aggregate: `TestDefinition` (AGG-006)
- Roadmap group: MVP-MOD-002 Diagnostic Catalog
- Priority: Critical

## Purpose

Defines the sellable and orderable diagnostic service offering that groups tests and
panels into catalog entries with lifecycle, versioning and publication controls
consumed by orders, quotations and portals.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | Entities, relationships and invariants |
| `business-rules.md` | Numbered rules RN-001..RN-006 |
| `processes.md` | Processes, actors and commands |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee portal screens and states |
| `mobile-model.md` | Mobile scope (not_required for this module) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## COM-MOD-011 reuse

The published-catalog read surface (`getPublishedServiceSnapshot` plus a new
`listPublishedServices` projection) is reused, unauthenticated and rate-limited, by the
COM-MOD-011 Public Website and Digital Growth module. No new capability package, aggregate
or schema was created for this reuse; see `traceability.md`'s `cross_module_reuse` entry.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are declared
as generated outputs in `generation-plan.md`. Only custom rules (publication
validation, immutable versioning, published snapshot projection, order eligibility) are
implemented manually in later backlog items.
