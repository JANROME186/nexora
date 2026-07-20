# BCM-QLT-005 Maintenance Management Capability Package

Human-readable companion for the Maintenance Management capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-QLT-005
- Domain: DOM-09 Quality
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owned by BCM-INV-001); `maintenanceRecord` delegated to this capability
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: High

## Purpose

Records preventive and corrective maintenance events for equipment-classified
`InventoryItem` records. Publishes `MaintenanceScheduled`/
`MaintenanceCompleted` for BCM-QLT-004 Equipment Management to consume
instead of writing `equipmentProfile` directly.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- Only equipment-type items are eligible (RN-001).
- Start/completion publish events rather than direct cross-field writes (RN-002).
- Only this capability appends to `maintenanceRecord` (RN-003).
- Completion must follow the start time (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers itemType eligibility, event publication and the
delegated append-only mutation boundary.
