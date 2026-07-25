# BCM-INV-001 Product Catalog Capability Package

Human-readable companion for the Product Catalog capability package. The YAML
models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-INV-001
- Domain: DOM-08 Inventory
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owner)
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: High

## Purpose

Master catalog of inventory items (consumables, reagents, supplies and
equipment). Creates and owns the `InventoryItem` aggregate — the single
shared aggregate for the whole Inventory and Internal Quality module — and
delegates named-field mutation authority to 9 sibling capabilities within the
same bounded context, mirroring the AGG-008 Sample / AGG-009 LaboratoryResult
delegated-ownership pattern from MVP-MOD-006/MVP-MOD-007.

## Package contents

| Artifact | Purpose |
| --- | --- |
| `capability-package.md` | Package identity, scope, dependencies, surfaces |
| `business-model.md` | InventoryItem aggregate, StockSummary/ReagentProfile/EquipmentProfile placeholders |
| `business-rules.md` | Numbered rules RN-001..RN-005 |
| `processes.md` | Register, update, discontinue |
| `events.md` | Domain and integration events |
| `openapi-source.md` | API source model for contract generation |
| `permissions.md` | Scopes, roles, policies, audit obligations |
| `ui-model.md` | Employee-portal catalog screen |
| `mobile-model.md` | Mobile scope (not_required) |
| `test-model.md` | Test cases mapped to rules |
| `observability-model.md` | Logs, metrics, traces, alerts |
| `generation-plan.md` | Generated outputs vs custom implementation |
| `traceability.md` | Links to BCM, domain, rules, APIs, tests, QA |

## Delegated ownership map

`InventoryItem` (AGG-013) is owned by this capability. Field-scoped delegated
mutation authority is granted to:

- `reagentProfile` -> BCM-INV-002 Reagent Management
- stock lots -> BCM-INV-003 Lot Management (own entity referencing inventoryItemId)
- `stockSummary` (increase) -> BCM-INV-005 Stock Entries
- `stockSummary` (decrease, exit) -> BCM-INV-006 Stock Exits
- `stockSummary` (decrease, consumption) -> BCM-INV-007 Consumption Tracking
- `stockSummary` (correction) -> BCM-INV-008 Inventory Adjustments
- `stockSummary` (decrease, disposal) -> BCM-INV-009 Waste Management
- calibration records -> BCM-QLT-003 Calibration Management
- `equipmentProfile` -> BCM-QLT-004 Equipment Management
- maintenance records -> BCM-QLT-005 Maintenance Management

AGG-014 Supplier is **not** owned by this module; it belongs to the
not-yet-modeled BCM-PER-006 Supplier Management. BCM-INV-004 Procurement
Management references supplier identity only as a captured, read-only
snapshot, mirroring the PatientSnapshot pattern used by DiagnosticOrder.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers itemType/classification consistency, the field-level
delegation boundary and the discontinuation gate.
