# Decision Tables

## Discount Authorization

| Condition | Result |
|---|---|
| Discount <= user threshold | Apply discount |
| Discount > user threshold and supervisor approves | Apply discount |
| Discount > user threshold and no approval | Reject discount |
| Study has restricted discount policy | Require supervisor approval |

## Cancellation Decision

| Order State | Has Payment | Result |
|---|---:|---|
| Draft | No | Allow cancellation |
| Paid | Yes | Require authorized cancellation and refund evaluation |
| Sample Collected | Yes | Require supervisor + clinical policy validation |
| Result Released | Yes | Block cancellation; allow financial adjustment only |

## Cash Closing Decision

| Difference | Result |
|---:|---|
| 0 | Approve closing |
| Within tolerance | Require comment and supervisor review |
| Above tolerance | Block approval until investigation |
