# CAP-001 Patient Management - Decision Tables

## DT-001 Minor patient requirement

| Condition: Age known | Condition: Age < legal age | Action: Require guardian | Action: Allow registration |
|---|---|---|---|
| Yes | Yes | Yes | Only if guardian exists |
| Yes | No | No | Yes |
| No | N/A | Depends on policy | With warning |

## DT-002 Digital delivery eligibility

| Has email | Has phone | Has digital consent | Delivery allowed | Preferred fallback |
|---|---|---|---|---|
| Yes | Any | Yes | Yes | Email |
| No | Yes | Yes | Yes | SMS/WhatsApp |
| Yes | Yes | No | No | Printed delivery |
| No | No | Any | No | Printed delivery |

## DT-003 Duplicate patient handling

| Match score | Same identifier | Same birth date | System action |
|---|---|---|---|
| >= 95 | Yes | Any | Block creation and show existing patient |
| >= 85 | No | Yes | Warn and require confirmation |
| >= 70 | No | No | Show possible matches |
| < 70 | No | No | Allow creation |

## DT-004 Patient status and order creation

| Patient status | User permission | Action |
|---|---|---|
| Active | `orders:create` | Allow |
| Inactive | `patients:reactivate` + `orders:create` | Require reactivation |
| Blocked | `patients:override-block` + `orders:create` | Require supervisor override |
| Merged | N/A | Redirect to master patient |
