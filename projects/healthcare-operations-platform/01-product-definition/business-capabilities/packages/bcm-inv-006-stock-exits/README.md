# BCM-INV-006 Stock Exits Capability Package

Human-readable companion for the Stock Exits capability package. The YAML
models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-INV-006
- Domain: DOM-08 Inventory
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owned by BCM-INV-001); `stockSummary` decrease delegated to this capability
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: High

## Purpose

Records non-consumption stock exits (branch transfer, internal transfer,
return to supplier) through the narrowly-scoped `ApplyStockExit` command.
Distinct from BCM-INV-007 (consumption) and BCM-INV-009 (waste).

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- Quantity cannot exceed the lot's remaining quantity (RN-001).
- Transfer exits require a destination branch (RN-002).
- Only `ApplyStockExit` decreases stock for exit purposes (RN-003).
- Expired/disposed lots are ineligible; route through Waste Management (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers the real-time quantity/eligibility guard and delegated
multi-field mutation.
