# BCM-QLT-003 Calibration Management Capability Package

Human-readable companion for the Calibration Management capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-QLT-003
- Domain: DOM-09 Quality
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owned by BCM-INV-001); `calibrationRecord` delegated to this capability
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: High

## Purpose

Records calibration events for equipment-classified `InventoryItem` records.
On a failed calibration, publishes `CalibrationFailed` for BCM-QLT-004
Equipment Management to consume instead of writing `equipmentProfile`
directly.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- Only equipment-type items are eligible (RN-001).
- Failures publish an event rather than a direct cross-field write (RN-002).
- Only this capability appends to `calibrationRecord` (RN-003).
- `nextDueDate` must follow `performedAt` (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers itemType eligibility, conditional event publication and
the delegated append-only mutation boundary.
