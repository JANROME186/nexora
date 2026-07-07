# 16 Test Specification

## Unit tests

- Tax calculation rules.
- Fiscal profile validation.
- Folio reservation uniqueness.
- Invoice state transitions.
- Cancellation eligibility.

## Contract tests

- Billing OpenAPI request/response schemas.
- Error model standardization.
- Idempotent invoice issue calls.

## Integration tests

- Sale paid → invoice issued.
- Provider failure → retry without duplicate.
- Invoice issued → documents stored.
- Cancel request → provider response.

## Security tests

- Unauthorized users cannot issue/cancel invoices.
- Cross-tenant invoice access is blocked.
- Fiscal documents require permission and audit.
