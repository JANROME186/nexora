# Billing Request Management Capability Package

Capability: `BCM-ATT-008 Billing Request Management`

Roadmap group: `MVP-MOD-005 Cashier and Billing Request`

Bounded context: `billing-tax` with secondary dependency on `cash-sales`

Primary aggregate: `AGG-012 Invoice`, modeled here as provider-agnostic `InvoiceRequest`

This package defines fiscal billing request lifecycle from a sale, including immutable fiscal
profile snapshots, tax line calculation, provider-agnostic adapter submission and request status
tracking. Country-specific fiscal connectors are adapters and are not part of the core model.
