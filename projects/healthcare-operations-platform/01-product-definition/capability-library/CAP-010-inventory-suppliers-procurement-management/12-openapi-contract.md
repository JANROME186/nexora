# OpenAPI Contract Scope

The Inventory API is contract-first and must be governed by `05-contracts/contracts/openapi/inventory/inventory.openapi.yaml`.

## Initial Resources

- `/suppliers`
- `/inventory-items`
- `/warehouses`
- `/stock-balances`
- `/stock-movements`
- `/purchase-requests`
- `/purchase-orders`
- `/goods-receipts`
- `/lots`
- `/reorder-alerts`
- `/inventory-audits`

## Contract Rules

- All endpoints must include organization context.
- Branch/warehouse scope must be explicit where applicable.
- Stock movement creation must be idempotent.
- Immutable movement records cannot be updated through PATCH.
- State changes must use explicit action endpoints.
