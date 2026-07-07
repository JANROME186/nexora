# 06 Event Storming

## Commands

- CreateEmployee
- UpdateEmployeeProfile
- ActivateEmployee
- DeactivateEmployee
- InviteUser
- AcceptInvitation
- SuspendUser
- ReactivateUser
- DeactivateUser
- CreateRole
- UpdateRole
- AssignRoleToUser
- RemoveRoleFromUser
- GrantPermission
- RevokePermission
- AssignBranchAccess
- RemoveBranchAccess
- RequestAccessReview
- CompleteAccessReview

## Domain Events

- EmployeeCreated
- EmployeeActivated
- EmployeeDeactivated
- UserInvited
- UserActivated
- UserSuspended
- UserReactivated
- UserDeactivated
- RoleCreated
- RoleUpdated
- RoleAssignedToUser
- RoleRemovedFromUser
- PermissionGranted
- PermissionRevoked
- BranchAccessAssigned
- BranchAccessRemoved
- AccessReviewCompleted
- AuthenticationFailed
- LoginSucceeded

## Hotspots

- Avoid role explosion by combining RBAC with ABAC policies.
- Permission evaluation must be centralized and testable.
- Branch-scoped access must be enforced in every data query.
- Emergency access must not become a permanent bypass.
