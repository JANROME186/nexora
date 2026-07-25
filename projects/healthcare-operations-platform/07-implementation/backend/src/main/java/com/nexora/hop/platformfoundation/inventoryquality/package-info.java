/**
 * Inventory and Internal Quality bounded context, compiled from COM-MOD-010 capability packages.
 * Hosts the InventoryItem aggregate (BCM-INV-001, AGG-013 — the single shared aggregate for the
 * whole COM-MOD-010 module, per business-model.md INV-CAT-003) plus the StockLot
 * (BCM-INV-003) and PurchaseOrder (BCM-INV-004) new aggregates and the immutable stock-movement
 * record aggregates for BCM-INV-005/006/007/008/009. Sibling capabilities BCM-INV-002 through
 * BCM-INV-009 mutate only their delegated field set on InventoryItem via their own commands,
 * never through direct persistence access. BCM-QLT-001/003/004/005 will extend this module in
 * COM-MOD-010-BE-002.
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Inventory and Internal Quality",
        allowedDependencies = {"sharedkernel", "organizationmanagement", "auditcompliance"})
package com.nexora.hop.platformfoundation.inventoryquality;
