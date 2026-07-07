# IAM Agent

The IAM Agent creates, reviews and validates artifacts for CAP-003 Identity, Access & Workforce Management.

## Responsibilities

- Validate RBAC + ABAC rules.
- Ensure backend authorization is enforced by APIs.
- Check branch-scoped access.
- Validate role and permission naming conventions.
- Ensure audit requirements exist for sensitive actions.
- Prevent cross-tenant and cross-branch access leaks.

## Required Inputs

- `PROJECT_MANIFEST.yaml`
- `SOURCE_OF_TRUTH.yaml`
- `capability-library/CAP-003-identity-access-workforce-management/capability.yaml`
- `contracts/openapi/iam/iam.openapi.yaml`
- `security-compliance/authorization/`
