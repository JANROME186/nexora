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
| `capability-package.yaml` | Package identity, scope, dependencies, surfaces |
| `business-model.yaml` | Entities, relationships and invariants |
| `business-rules.yaml` | Numbered rules RN-001..RN-006 |
| `processes.yaml` | Processes, actors and commands |
| `events.yaml` | Domain and integration events |
| `openapi-source.yaml` | API source model for contract generation |
| `permissions.yaml` | Scopes, roles, policies, audit obligations |
| `ui-model.yaml` | Employee portal screens and states |
| `mobile-model.yaml` | Mobile scope (not_required for this module) |
| `test-model.yaml` | Test cases mapped to rules |
| `observability-model.yaml` | Logs, metrics, traces, alerts |
| `generation-plan.yaml` | Generated outputs vs custom implementation |
| `traceability.yaml` | Links to BCM, domain, rules, APIs, UI, tests, QA |

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are declared
as generated outputs in `generation-plan.yaml`. Only custom rules (publication
validation, immutable versioning, published snapshot projection, order eligibility) are
implemented manually in later backlog items.
