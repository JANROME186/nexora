# BCM-QLT-001 Internal Quality Controls Capability Package

Human-readable companion for the Internal Quality Controls capability
package. The YAML models in this folder are the authoritative source of
truth.

## Capability

- ID: BCM-QLT-001
- Domain: DOM-09 Quality
- Bounded context: `inventory-procurement`
- Primary aggregate: `QualityControlRun` (new entity owned by this capability)
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: Critical

## Purpose

Records internal QC runs against control-material reagent lots, evaluates
acceptance/rejection with Westgard-style rules, and links the outcome,
read-only, to the patient results it validates (`AGG-009 LaboratoryResult`,
owned by `laboratory-results`). Never mutates `InventoryItem`, `StockLot`
or `LaboratoryResult`.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- Read-only reference boundary to InventoryItem/StockLot/LaboratoryResult (RN-001).
- Westgard-style multi-rule evaluation (RN-002).
- Out-of-control results require an authorized, audited override (RN-003).
- Control material eligibility validation (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers the Westgard rule engine, the override authorization
path and the cross-capability control-material validation.
