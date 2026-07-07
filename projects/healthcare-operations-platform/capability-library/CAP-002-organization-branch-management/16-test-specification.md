# 16 Test Specification

## Unit Tests

- BranchActivationPolicy.
- BranchServiceAvailabilityPolicy.
- TenantIsolationPolicy.
- BranchCodeUniqueness.
- PrimaryBranchDeactivationPolicy.

## Contract Tests

- Validate all Organization API endpoints against OpenAPI.
- Validate error format, pagination and tenant isolation headers.

## Integration Tests

- Create tenant → create organization → create branch → add address → add schedule → enable service → activate branch.
- Attempt to deactivate branch with pending orders.
- License plan change disables unsupported service.

## E2E Tests

- Admin creates first branch through onboarding wizard.
- Supervisor checks branch availability.
- Branch is hidden from order creation after deactivation.

## Security Tests

- User cannot access branch from another tenant.
- User cannot activate branch without permission.
- Audit is recorded for configuration changes.
