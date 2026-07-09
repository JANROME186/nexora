# BCM-SVC-009 Price List Management Capability Package

Human-readable companion for the Price List Management capability package. The YAML
models in this folder are the authoritative source of truth.

## Capability

- ID: BCM-SVC-009
- Domain: DOM-03 Diagnostic Services
- Bounded context: `catalog-test-configuration` (published language consumed by `cash-sales`)
- Primary aggregate: `TestDefinition` (AGG-006); references `Sale` (AGG-010)
- Roadmap group: MVP-MOD-002 Diagnostic Catalog
- Priority: High

## Purpose

Defines version-aware price lists that assign prices to services, tests and panels with
currency, effective dating and optional agreement segmentation. Published price snapshots
are consumed by quotations, cashier operations and billing requests.

## Package contents

Standard 14-artifact capability package per the Business Capability Package Standard.

## MDPE note

This capability is financially sensitive: publication validation, effective-dated
versioning, overlap detection and effective price resolution are custom rules. CRUD,
DTOs, controllers, repositories, SDKs, Swagger and repetitive tests remain generated
outputs declared in `generation-plan.yaml`.
