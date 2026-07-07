# 09 Domain Events

| ID | Event | Producer | Consumers |
|---|---|---|---|
| EVT-IAM-001 | EmployeeCreated | IAM Context | Organization, Audit, Analytics |
| EVT-IAM-002 | EmployeeActivated | IAM Context | Audit, Notification |
| EVT-IAM-003 | EmployeeDeactivated | IAM Context | Orders, Caja, Inventory, Audit |
| EVT-IAM-004 | UserInvited | IAM Context | Notification |
| EVT-IAM-005 | UserActivated | IAM Context | Audit, Analytics |
| EVT-IAM-006 | UserSuspended | IAM Context | Audit, Security |
| EVT-IAM-007 | UserReactivated | IAM Context | Audit |
| EVT-IAM-008 | UserDeactivated | IAM Context | Audit, Security |
| EVT-IAM-009 | RoleCreated | IAM Context | Audit |
| EVT-IAM-010 | RoleUpdated | IAM Context | Audit, Security |
| EVT-IAM-011 | RoleAssignedToUser | IAM Context | Audit, Security |
| EVT-IAM-012 | PermissionGranted | IAM Context | Audit, Security |
| EVT-IAM-013 | PermissionRevoked | IAM Context | Audit, Security |
| EVT-IAM-014 | BranchAccessAssigned | IAM Context | Audit, Orders, Caja |
| EVT-IAM-015 | BranchAccessRemoved | IAM Context | Audit, Orders, Caja |
| EVT-IAM-016 | AuthenticationFailed | IAM Context | Security Monitoring |
| EVT-IAM-017 | LoginSucceeded | IAM Context | Audit, Analytics |
| EVT-IAM-018 | AccessReviewCompleted | IAM Context | Compliance, Audit |
| EVT-IAM-019 | EmergencyAccessGranted | IAM Context | Security, Audit |
| EVT-IAM-020 | EmergencyAccessExpired | IAM Context | Security, Audit |
