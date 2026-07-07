# 11 User Stories

## Epic CAP-003-E01 Employee Management

### US-IAM-001 Create employee profile

As a laboratory administrator, I want to register an employee profile so that the person can later be assigned a user account, position and branch access.

Acceptance criteria:

- Employee belongs to the current tenant.
- Required personal and work fields are validated.
- Employee starts in Draft or Active according to policy.
- Audit record is created.
- `EmployeeCreated` event is published.

### US-IAM-002 Assign employee to branch

As a laboratory administrator, I want to assign an employee to one or more branches so that the employee can operate only in authorized locations.

Acceptance criteria:

- Branch must belong to the same tenant.
- Assignment can include default branch.
- Duplicate assignment is rejected.
- `BranchAccessAssigned` event is published.

## Epic CAP-003-E02 User Provisioning

### US-IAM-010 Invite user

As a laboratory administrator, I want to invite an employee to create a user account so that they can access Nexora securely.

Acceptance criteria:

- Employee exists and is active.
- Invitation expiration is defined.
- Invitation notification is generated.
- User account starts in Invited status.
- `UserInvited` event is published.

### US-IAM-011 Suspend user

As a laboratory administrator, I want to suspend a user so that their access is immediately blocked without deleting audit history.

Acceptance criteria:

- Active sessions are invalidated.
- User cannot authenticate while suspended.
- Reason is required.
- `UserSuspended` event is published.

## Epic CAP-003-E03 Roles and Permissions

### US-IAM-020 Create role

As a tenant administrator, I want to create roles with permissions so that access can be managed consistently for groups of users.

Acceptance criteria:

- Role code is unique within tenant.
- Permissions must exist in the permission catalog.
- High-risk permissions require policy validation.
- `RoleCreated` event is published.

### US-IAM-021 Assign role to user

As a tenant administrator, I want to assign a role to a user so that the user obtains the required permissions.

Acceptance criteria:

- Administrator must have permission to assign the target role.
- Assignment is tenant-scoped.
- Effective permissions can be queried after assignment.
- `RoleAssignedToUser` event is published.

## Epic CAP-003-E04 Authorization Enforcement

### US-IAM-030 Evaluate effective permissions

As a backend service, I want to evaluate a user's effective permissions so that every API can enforce authorization consistently.

Acceptance criteria:

- Evaluation includes tenant, branch, role, permission and ABAC attributes.
- Denied decisions include a safe reason code.
- Sensitive denial details are not exposed to the end user.
- Decision is traceable for audit when required.

### US-IAM-031 Review user access

As a compliance administrator, I want to review user access periodically so that excessive or outdated permissions can be removed.

Acceptance criteria:

- Review can be filtered by tenant, branch, role and permission.
- Each item can be confirmed or revoked.
- Completion evidence is stored.
- `AccessReviewCompleted` event is published.
