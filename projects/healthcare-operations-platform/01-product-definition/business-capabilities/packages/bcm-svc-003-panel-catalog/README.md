# BCM-SVC-003 Panel Catalog Capability Package

Human-readable companion for the Panel Catalog capability package. The YAML models in
this folder are the authoritative source of truth.

## Capability

- ID: BCM-SVC-003
- Domain: DOM-03 Diagnostic Services
- Bounded context: `catalog-test-configuration`
- Primary aggregate: `TestDefinition` (AGG-006)
- Roadmap group: MVP-MOD-002 Diagnostic Catalog
- Priority: High

## Purpose

Defines panels that group multiple tests into a single orderable clinical set with
shared preparation and sample handling, lifecycle and publication controls consumed by
orders and quotations.

## Package contents

Standard 14-artifact capability package per the Business Capability Package Standard.

## MDPE note

CRUD, DTOs, controllers, repositories, SDKs, Swagger and repetitive tests are generated
outputs declared in `generation-plan.yaml`. Custom rules (member publication validation,
immutable versioning, published snapshot projection) are implemented in later backlog
items.
