# Decision Tables

## Purchase Approval Decision

| Condition | Outcome |
|---|---|
| Amount <= branch approval limit and supplier active | Auto approve |
| Amount > branch approval limit and <= organization approval limit | Requires supervisor approval |
| Amount > organization approval limit | Requires finance approval |
| Supplier inactive | Reject |
| Item restricted | Requires compliance approval |

## Lot Consumption Decision

| Condition | Outcome |
|---|---|
| Lot expired | Block consumption |
| Lot near expiration and non-critical | Warn user |
| Lot recalled | Block consumption |
| Lot available and valid | Allow consumption |
| Lot reserved for another branch | Require transfer approval |

## Reorder Alert Decision

| Condition | Outcome |
|---|---|
| Current stock <= minimum stock | Create reorder alert |
| Current stock <= safety stock and supplier lead time high | Create urgent reorder alert |
| Current stock > minimum stock | No alert |
| Item inactive | No alert |
