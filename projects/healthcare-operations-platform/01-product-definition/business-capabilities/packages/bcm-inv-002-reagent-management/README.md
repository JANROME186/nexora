# BCM-INV-002 Reagent Management Capability Package

Human-readable companion for the Reagent Management capability package. The
YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-INV-002
- Domain: DOM-08 Inventory
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owned by BCM-INV-001; `reagentProfile` delegated)
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: Critical

## Purpose

Classifies reagent/consumable InventoryItem records with their diagnostic
test linkage and consumption ratio, feeding BCM-INV-007 Consumption
Tracking's automatic stock decrement. Holds delegated, field-scoped
mutation authority over `InventoryItem.reagentProfile` only.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package (see
`capability-package.yaml` `required_artifacts`).

## Key rules modeled

- Only reagent/consumable itemType InventoryItems are eligible (RN-001).
- `consumptionUnitRatio` must be positive (RN-002).
- Only this capability writes `reagentProfile` (RN-003).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers itemType eligibility and the delegated field-mutation
boundary.
