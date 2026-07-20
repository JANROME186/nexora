# BCM-QLT-004 Equipment Management Capability Package

Human-readable companion for the Equipment Management capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-QLT-004
- Domain: DOM-09 Quality
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owned by BCM-INV-001); `equipmentProfile` delegated to this capability
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: Critical

## Purpose

Manages equipment-classified `InventoryItem` records: asset profile and
availability status. Reacts to `CalibrationFailed` (BCM-QLT-003) and
`MaintenanceScheduled`/`MaintenanceCompleted` (BCM-QLT-005) events instead of
either sibling capability writing `equipmentProfile` directly.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- Only equipment-type items are eligible (RN-001).
- Retired is a terminal state (RN-002).
- Only this capability writes `equipmentProfile` (RN-003).
- Calibration failure drives an automatic availability transition (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers itemType eligibility, the delegated field-mutation
boundary and the event-driven availability transitions.
