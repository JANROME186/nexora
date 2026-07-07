# 07 Domain Model

## Bounded Context

**Identity & Access Context**

Responsible for workforce identity, user accounts, authentication policies, role assignments, permissions, branch-scoped access and authorization decisions.

## Aggregates

### Employee Aggregate

Root: `Employee`

Owns:

- Employee profile.
- Position assignment.
- Organizational unit assignment.
- Employee status.
- Branch assignments.

### User Account Aggregate

Root: `UserAccount`

Owns:

- Login identity.
- Account status.
- Authentication policy reference.
- Invitation state.
- Session policy.

### Role Aggregate

Root: `Role`

Owns:

- Role metadata.
- Role permissions.
- Scope constraints.

### Access Policy Aggregate

Root: `AccessPolicy`

Owns:

- Permission expressions.
- Attribute-based constraints.
- Risk level.
- Approval requirements.

## Value Objects

- `PermissionCode`
- `BranchScope`
- `TenantScope`
- `RoleCode`
- `EmployeeNumber`
- `LoginIdentifier`
- `AccessDecision`
- `PermissionRiskLevel`

## Domain Services

- `AuthorizationPolicyService`
- `PermissionEvaluationService`
- `UserProvisioningService`
- `AccessReviewService`
