# 03 Decision Tables

## DT-ORG-001 Branch Activation Eligibility

| Has address | Has schedule | Has active services | Tenant active | Decision |
|---|---|---|---|---|
| Yes | Yes | Yes | Yes | Activate branch |
| No | Any | Any | Yes | Reject: missing address |
| Yes | No | Any | Yes | Reject: missing schedule |
| Yes | Yes | No | Yes | Reject: no active services |
| Any | Any | Any | No | Reject: tenant inactive |

## DT-ORG-002 Branch Service Availability

| Branch active | Service enabled | Inside schedule | Resource available | Decision |
|---|---|---|---|---|
| Yes | Yes | Yes | Yes | Available |
| No | Any | Any | Any | Not available: branch inactive |
| Yes | No | Any | Any | Not available: service disabled |
| Yes | Yes | No | Any | Not available: outside schedule |
| Yes | Yes | Yes | No | Not available: no resource |

## DT-ORG-003 Branch Deactivation

| Has pending orders | Is primary branch | Alternative primary exists | Decision |
|---|---|---|---|
| No | No | Any | Allow deactivation |
| Yes | Any | Any | Reject: pending orders |
| No | Yes | Yes | Allow with primary reassignment |
| No | Yes | No | Reject: no alternative primary |
