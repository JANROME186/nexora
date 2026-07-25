# BCM-INV-007 Consumption Tracking Capability Package

Human-readable companion for the Consumption Tracking capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-INV-007
- Domain: DOM-08 Inventory
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owned by BCM-INV-001); `stockSummary` decrease delegated to this capability
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: Critical

## Purpose

Automatically decrements stock when a linked diagnostic test is performed,
using BCM-INV-002's consumption ratio. Reads `AGG-009 LaboratoryResult`
(owned by `laboratory-results`) read-only for traceability; never mutates
it. Feeds BCM-QLT-001 Internal Quality Controls with the control-material
lot consumed.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- Quantity derives from the cross-capability consumption ratio (RN-001).
- LaboratoryResult is read-only, never mutated (RN-002).
- Only `ApplyConsumption` decreases stock for consumption purposes (RN-003).
- Expired/disposed lots are ineligible (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers ratio resolution, the read-only cross-context
reference boundary and the delegated multi-field mutation.
