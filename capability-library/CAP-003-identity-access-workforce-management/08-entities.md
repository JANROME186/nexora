# 08 Entities

| ID | Entity | Type | Description |
|---|---|---|---|
| ENT-IAM-001 | Employee | Aggregate Root | Worker profile associated with a tenant. |
| ENT-IAM-002 | UserAccount | Aggregate Root | Authentication account used to access Nexora. |
| ENT-IAM-003 | Role | Aggregate Root | Collection of permissions and scope constraints. |
| ENT-IAM-004 | Permission | Entity | Atomic operation allowed by the system. |
| ENT-IAM-005 | UserRoleAssignment | Entity | Assignment of a role to a user. |
| ENT-IAM-006 | EmployeeBranchAssignment | Entity | Branches where an employee/user may operate. |
| ENT-IAM-007 | AccessPolicy | Aggregate Root | ABAC policy for sensitive operations. |
| ENT-IAM-008 | AuthenticationPolicy | Entity | Password, MFA and session policy rules. |
| ENT-IAM-009 | UserInvitation | Entity | Invitation token and acceptance state. |
| ENT-IAM-010 | LoginAttempt | Entity | Security event for authentication attempts. |
| ENT-IAM-011 | Session | Entity | Active user session metadata. |
| ENT-IAM-012 | AccessReview | Aggregate Root | Periodic review of user access. |
| ENT-IAM-013 | AccessReviewItem | Entity | Individual access confirmation/revocation item. |
| ENT-IAM-014 | EmergencyAccessGrant | Entity | Time-bound elevated access grant. |
| ENT-IAM-015 | PermissionAuditRecord | Entity | Audit trail for role and permission changes. |
| ENT-IAM-016 | PositionAccessProfile | Entity | Default access profile by organization position. |
| ENT-IAM-017 | UserPreference | Entity | Language, theme and accessibility preferences. |
| ENT-IAM-018 | IdentityProviderLink | Entity | External IdP association for future SSO. |
