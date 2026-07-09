# Business Capability Package Standard

Artifact ID: `NXF-CAP-PKG-STD-001`
Version: `1.0.0`
Status: `approved`

## Purpose

A Business Capability Package is the primary unit of development in Nexora.

It is a versioned, autonomous package containing all editable models and generation contracts needed to deliver one business capability end to end.

## Folder Convention

Capability packages live under each project at:

```text
01-product-definition/business-capabilities/packages/
```

Example:

```text
01-product-definition/business-capabilities/packages/bcm-per-002-patient-management/
```

## Required Artifacts

Each capability package must include:

- `capability-package.yaml`
- `business-model.yaml`
- `business-rules.yaml`
- `processes.yaml`
- `events.yaml`
- `openapi-source.yaml`
- `permissions.yaml`
- `ui-model.yaml`
- `mobile-model.yaml`
- `test-model.yaml`
- `observability-model.yaml`
- `generation-plan.yaml`
- `traceability.yaml`
- `README.md`

## Generated Outputs

The package model is used to generate or derive:

- Backend CRUD scaffolding, DTOs, controllers, repositories, domain skeletons and API adapters.
- React components, routes, forms and client usage.
- Flutter components and mobile flows.
- OpenAPI rendered contracts, SDKs and Swagger documentation.
- Repetitive unit, contract and acceptance test skeletons.
- Observability assets, dashboard definitions, alerts and runbook skeletons.

## Custom Implementation

Only high-value custom logic should be implemented manually:

- Complex business rules not expressible by generator templates.
- External adapters.
- Security-sensitive policies.
- Performance-sensitive queries.
- Legacy migration mappings requiring human decision.

## Business Rule Format

Business rules use IDs like `RN-001`.

Each rule must define:

- `id`
- `statement`
- `applies_to`
- `enforcement_point`
- `severity`
- `audit_required`
- `test_refs`

## Readiness

A capability package is ready for implementation when:

- The capability exists in `BCM-001`.
- The dependency profile exists in `BCM-002`.
- All required editable model artifacts exist.
- The generation plan identifies generated outputs and custom implementation points.
- Traceability links the package to operating model, BCM, rules, processes and tests.
