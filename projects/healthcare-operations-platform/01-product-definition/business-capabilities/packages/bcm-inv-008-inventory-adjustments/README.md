# BCM-INV-008 Inventory Adjustments Capability Package

Human-readable companion for the Inventory Adjustments capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-INV-008
- Domain: DOM-08 Inventory
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owned by BCM-INV-001); `stockSummary` correction delegated to this capability
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: High

## Purpose

Records manual stock corrections (physical count discrepancies, data-entry
corrections) with a mandatory reason code and dual-actor approval, through
the narrowly-scoped `ApplyAdjustment` command.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- Non-zero delta that never drives stock negative (RN-001).
- Separation of duties between requester and approver (RN-002).
- Mandatory reason code (RN-003).
- Only `ApplyAdjustment` corrects stock (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers the real-time negative-quantity guard and delegated
multi-field mutation.
