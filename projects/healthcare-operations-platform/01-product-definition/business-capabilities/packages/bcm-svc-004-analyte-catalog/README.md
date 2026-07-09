# BCM-SVC-004 Analyte Catalog Capability Package

Human-readable companion for the Analyte Catalog capability package. The YAML models in
this folder are the authoritative source of truth.

## Capability

- ID: BCM-SVC-004
- Domain: DOM-03 Diagnostic Services
- Bounded context: `catalog-test-configuration` (published language consumed by `laboratory-results`)
- Primary aggregate: `TestDefinition` (AGG-006); references `LaboratoryResult` (AGG-009)
- Roadmap group: MVP-MOD-002 Diagnostic Catalog
- Priority: Critical

## Purpose

Defines analytes, the atomic measurable components of a test, including result data type,
measurement unit, decimal precision, coding and result value constraints. Analytes are
the published language consumed by result capture, validation and reference range
evaluation.

## Package contents

Standard 14-artifact capability package per the Business Capability Package Standard.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are generated
outputs declared in `generation-plan.yaml`. Custom rules (immutable versioning, data type
change ripple review, published snapshot projection) are implemented in later backlog
items.
