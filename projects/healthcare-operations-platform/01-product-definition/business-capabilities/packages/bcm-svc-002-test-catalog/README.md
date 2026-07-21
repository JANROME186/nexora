# BCM-SVC-002 Test Catalog Capability Package

Human-readable companion for the Test Catalog capability package. The YAML models in
this folder are the authoritative source of truth.

## Capability

- ID: BCM-SVC-002
- Domain: DOM-03 Diagnostic Services
- Bounded context: `catalog-test-configuration`
- Primary aggregate: `TestDefinition` (AGG-006)
- Roadmap group: MVP-MOD-002 Diagnostic Catalog
- Priority: Critical

## Purpose

Defines individual diagnostic tests including method, measurement unit, result type,
turnaround time and references to analytes, samples and preparations. The test is the
atomic orderable clinical definition consumed by orders, processing and results.

## Package contents

Standard 14-artifact capability package per the Business Capability Package Standard:
`capability-package.yaml`, `business-model.yaml`, `business-rules.yaml`,
`processes.yaml`, `events.yaml`, `openapi-source.yaml`, `permissions.yaml`,
`ui-model.yaml`, `mobile-model.yaml`, `test-model.yaml`, `observability-model.yaml`,
`generation-plan.yaml`, `traceability.yaml`, `README.md`.

## COM-MOD-011 reuse

The published-catalog read surface (`getPublishedTestSnapshot` plus a new `listPublishedTests`
projection) is reused, unauthenticated and rate-limited, by the COM-MOD-011 Public Website and
Digital Growth module. No new capability package, aggregate or schema was created for this
reuse; see `traceability.yaml`'s `cross_module_reuse` entry.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are declared
as generated outputs in `generation-plan.yaml`. Custom rules (publication validation,
immutable versioning, analyte publication checks, snapshot projection) are implemented
manually in later backlog items.
