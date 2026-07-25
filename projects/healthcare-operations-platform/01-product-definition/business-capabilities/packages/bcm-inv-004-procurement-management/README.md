# BCM-INV-004 Procurement Management Capability Package

Human-readable companion for the Procurement Management capability package.
The YAML models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-INV-004
- Domain: DOM-08 Inventory
- Bounded context: `inventory-procurement`
- Primary aggregate: `PurchaseOrder` (new entity owned by this capability)
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: High

## Purpose

Manages purchase intent toward suppliers (draft, submit, approve, cancel,
receive) without ever mutating `InventoryItem` or `Supplier` directly. Goods
receipt is delegated entirely to BCM-INV-005 Stock Entries.

## Supplier ownership note

AGG-014 Supplier's bounded context is `inventory-procurement`, but its
formal owning capability is BCM-PER-006 Supplier Management (not part of
COM-MOD-010 and not yet modeled). This package intentionally does not claim
Supplier ownership — it holds only a captured `SupplierSnapshot`, mirroring
the `PatientSnapshot` pattern used by `DiagnosticOrder`.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- Purchase order lines must reference an existing, active InventoryItem (RN-001).
- No direct InventoryItem/Supplier mutation; receipt always delegates (RN-002).
- Terminal states reject further transitions (RN-003).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.md`. Custom
implementation covers cross-capability line validation and receipt
delegation.
