# Release Notes — v0.43.0

## Summary

This release orders the Healthcare Operations Platform project root so agents can work incrementally using only numbered folders.

## Changed

- HOP root now contains only ordered project folders:
  - `00-intake`
  - `01-product-definition`
  - `02-domain-definition`
  - `03-architecture`
  - `04-requirements`
  - `05-contracts`
  - `06-delivery`
  - `07-implementation`
  - `08-qa`
  - `09-operations`
  - `10-generated`
  - `99-legacy`
- Product, domain, architecture, requirements, contracts, delivery, QA, operations and generated assets were moved under their matching numbered stages.
- Active source-of-truth paths were updated to the new ordered structure.

## Added

- `projects/healthcare-operations-platform/ORDERED_DEVELOPMENT_GUIDE.md`

## Result

Any agent can now load the project in numeric order and start development from:

`projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/`
