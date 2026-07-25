# QA Specification — CAP-006 Orders, Appointments & Sample Collection

## Contract Tests

- Validate `POST /orders` against `orders.openapi.md`.
- Validate `POST /appointments` against `appointments.openapi.md`.
- Validate `POST /samples/{sampleId}/collect` against `samples.openapi.md`.
- Validate error schema consistency.

## Domain Tests

- Order cannot be confirmed with inactive tests.
- Order requiring payment cannot progress without clearance or exception.
- Sample rejection requires reason.
- Reprinting labels creates audit event.
- Branch-level permissions are enforced.

## E2E Tests

1. Walk-in order creation → payment clearance → label generation → sample collection.
2. Scheduled appointment → check-in → order confirmation → sample collection.
3. Sample rejection → recollection request → new collection.
4. Order cancellation prevents further sample and result operations.
