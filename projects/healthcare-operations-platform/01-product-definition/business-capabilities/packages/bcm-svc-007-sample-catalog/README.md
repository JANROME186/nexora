# BCM-SVC-007 Sample Catalog Capability Package

Human-readable companion for the Sample Catalog capability package. The YAML models in
this folder are the authoritative source of truth.

## Capability

- ID: BCM-SVC-007
- Domain: DOM-03 Diagnostic Services
- Bounded context: `catalog-test-configuration` (published language consumed by `orders-samples`)
- Primary aggregate: `TestDefinition` (AGG-006); references `Sample` (AGG-008)
- Roadmap group: MVP-MOD-002 Diagnostic Catalog
- Priority: High

## Purpose

Defines sample types and sample requirements including sample kind, container, minimum
volume, handling and temperature conditions used to determine what biological samples a
test requires and how they must be collected and handled.

## Package contents

Standard 14-artifact capability package per the Business Capability Package Standard.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are generated
outputs declared in `generation-plan.md`. Custom rules (sample type publication
validation, immutable versioning, handling completeness, snapshot projection) are
implemented in later backlog items.
