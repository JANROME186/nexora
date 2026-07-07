# 03 Decision Tables

## DT-IAM-001 User Login Eligibility

| User active | Employee active | Tenant active | Password valid | MFA required | MFA passed | Decision |
|---|---|---|---|---|---|---|
| Yes | Yes | Yes | Yes | No | N/A | Allow login |
| Yes | Yes | Yes | Yes | Yes | Yes | Allow login |
| Yes | Yes | Yes | Yes | Yes | No | Require MFA |
| No | Any | Any | Any | Any | Any | Deny: user inactive |
| Any | No | Any | Any | Any | Any | Deny: employee inactive |
| Any | Any | No | Any | Any | Any | Deny: tenant inactive |
| Any | Any | Any | No | Any | Any | Deny: invalid credentials |

## DT-IAM-002 Branch Access Decision

| Has all-branch access | Explicit branch assignment | Branch active | Tenant active | Decision |
|---|---|---|---|---|
| Yes | Any | Yes | Yes | Allow |
| No | Yes | Yes | Yes | Allow |
| No | No | Yes | Yes | Deny: branch not assigned |
| Any | Any | No | Yes | Deny: branch inactive |
| Any | Any | Any | No | Deny: tenant inactive |

## DT-IAM-003 Permission Assignment

| Admin has permission | Target permission higher risk | Requires approval | Approval granted | Decision |
|---|---|---|---|---|
| Yes | No | No | N/A | Assign |
| Yes | Yes | Yes | Yes | Assign |
| Yes | Yes | Yes | No | Pending approval |
| No | Any | Any | Any | Reject |
