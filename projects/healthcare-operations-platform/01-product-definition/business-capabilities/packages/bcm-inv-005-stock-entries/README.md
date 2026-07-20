# BCM-INV-005 Stock Entries Capability Package

Human-readable companion for the Stock Entries capability package. The YAML
models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-INV-005
- Domain: DOM-08 Inventory
- Bounded context: `inventory-procurement`
- Primary aggregate: `InventoryItem` (AGG-013, owned by BCM-INV-001); `stockSummary` increase delegated to this capability
- Roadmap group: COM-MOD-010 Inventory and Internal Quality
- Priority: High

## Purpose

Records goods receipt (from an approved purchase order or manual entry),
increasing `StockLot.remainingQuantity` and
`InventoryItem.stockSummary.onHandQuantity` through the narrowly-scoped
`ApplyStockReceipt` command.

## Package contents

Same 14-artifact structure as every COM-MOD-010 package.

## Key rules modeled

- Positive quantity only (RN-001).
- Purchase-order-linked receipts validate against an approved order line (RN-002).
- Only `ApplyStockReceipt` increases stock (RN-003).
- Discontinued items reject new receipts (RN-004).

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are
declared as generated outputs in `generation-plan.yaml`. Custom
implementation covers the delegated multi-field mutation and cross-capability
validation.
