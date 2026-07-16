# Cashier Operations Capability Package

Capability: `BCM-ATT-005 Cashier Operations`

Roadmap group: `MVP-MOD-005 Cashier and Billing Request`

Bounded context: `cash-sales`

Primary aggregates: `AGG-010 Sale`, `AGG-011 CashRegister`

This package defines branch cashier execution: cash sessions, sales, payment allocations, sale
cancellation/refund boundaries and financial audit evidence. It delegates fiscal invoice issuance
to `BCM-ATT-008 Billing Request Management` and does not mutate clinical or patient aggregates.

The editable model artifacts in this folder are the source for generated backend, frontend,
contract, test and observability outputs.
