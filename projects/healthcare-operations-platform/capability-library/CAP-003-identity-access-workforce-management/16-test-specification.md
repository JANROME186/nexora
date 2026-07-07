# 16 Test Specification

## Unit Tests

- PermissionEvaluationService.
- BranchAccessPolicy.
- RoleAssignmentPolicy.
- UserLoginEligibilityPolicy.
- EmergencyAccessPolicy.
- HighRiskPermissionApprovalPolicy.

## Contract Tests

- Validate IAM API against OpenAPI.
- Validate error schema.
- Validate authorization headers and tenant context.
- Validate pagination on search endpoints.

## Integration Tests

- Create employee → invite user → accept invitation → assign branch → assign role → evaluate permission.
- Attempt to access unauthorized branch.
- Suspend user and verify sessions are invalidated.
- Assign high-risk permission requiring approval.

## Security Tests

- Privilege escalation attempt.
- Cross-tenant access attempt.
- Branch scope bypass attempt.
- Expired invitation token.
- Suspended user login.
