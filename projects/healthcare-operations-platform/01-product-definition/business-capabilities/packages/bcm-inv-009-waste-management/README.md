# BCM-INV-009 Waste Management Capability Package

Human-readable companion for the Waste Management capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-INV-009
- Domain: DOM-08 Inventory
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owned by BCM-INV-001); `stockSummary` disposal decrease delegated to this capability
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: High

## Purpose

Records disposal of expired, damaged, contaminated or recalled stock through
the narrowly-scoped `ApplyWasteDisposal` command, transitioning the
referenced `StockLot` to `disposed` when fully exhausted.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- Quantity cannot exceed the lot's remaining quantity (RN-001).
- A waste reason code is mandatory (RN-002).
- Only `ApplyWasteDisposal` decreases stock for disposal purposes (RN-003).
- Exhausted lots transition to disposed transactionally (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers the real-time quantity guard and the delegated
multi-field mutation with conditional status transition.
