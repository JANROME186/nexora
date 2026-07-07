# Test Specification

## Contract Tests

- Validate Inventory API against OpenAPI.
- Validate error and pagination schemas.
- Validate idempotency for movement creation.

## Domain Tests

- Expired lots cannot be consumed.
- Purchase approval decision table is enforced.
- Stock movements are immutable.
- Reorder alerts are generated at threshold.
- Adjustments require permissions and reasons.

## Integration Tests

- Receive goods updates stock balance.
- Consume stock creates movement and updates balance.
- Inventory audit records variance and approval flow.

## Security Tests

- Branch users cannot access stock from unauthorized branches.
- Supplier suspension requires permission.
- Restricted item adjustment requires elevated permission.
