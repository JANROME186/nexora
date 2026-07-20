# BCM-INV-003 Lot Management Capability Package

Human-readable companion for the Lot Management capability package. The YAML
models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-INV-003
- Domain: DOM-08 Inventory
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owned by BCM-INV-001); `StockLot` owned by this capability
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: High

## Purpose

Owns `StockLot` — physical batch metadata (lot number, expiration, storage
condition, quarantine/expired/disposed status) — for every InventoryItem.
Quantity movement stays exclusively delegated to BCM-INV-005/006/007/008/009.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- remainingQuantity never exceeds receivedQuantity or goes negative (RN-001).
- Expired lots transition automatically via a scheduled sweep (RN-002).
- Lot metadata vs. quantity fields stay under separate delegated ownership (RN-003).
- Disposed lots are terminal (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers the scheduled expiration sweep and the metadata/
quantity delegation boundary.
