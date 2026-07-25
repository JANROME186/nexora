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

- `capability-package.md`
- `business-model.md`
- `business-rules.md`
- `processes.md`
- `events.md`
- `openapi-source.md`
- `permissions.md`
- `ui-model.md`
- `mobile-model.md`
- `test-model.md`
- `observability-model.md`
- `generation-plan.md`
- `traceability.md`
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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-CAP-PKG-STD-001
  type: framework-standard
  name: Business Capability Package Standard
  version: 1.0.0
  status: approved
  human_readable: capability-package-standard.md
  machine_readable: capability-package-standard.md
  owner: Nexora Engineering
purpose: Define the complete package required to model, compile, implement, validate
  and version one Business Capability.
capability_package:
  definition: A versioned, autonomous package containing all editable models and generation
    contracts needed to deliver a business capability end to end.
  primary_unit_of_development: true
  may_be_grouped_by_module: true
  must_be_independently_versionable: true
  must_be_agent_agnostic: true
folder_convention:
  project_relative_root: 01-product-definition/business-capabilities/packages/
  folder_name_pattern: '{capability_id-lowercase}-{capability-slug}/'
  example: bcm-per-002-patient-management/
required_artifacts:
- file: capability-package.md
  classification: editable_model
  purpose: Package identity, ownership, capability scope, roadmap and dependencies.
- file: business-model.md
  classification: editable_model
  purpose: Business entities, relationships and invariants.
- file: business-rules.md
  classification: editable_model
  purpose: Numbered business rules and enforcement expectations.
- file: processes.md
  classification: editable_model
  purpose: Business processes, actors, commands and outcomes.
- file: events.md
  classification: editable_model
  purpose: Domain and integration events emitted or consumed by the capability.
- file: openapi-source.md
  classification: editable_model
  purpose: Source API contract model used to generate OpenAPI artifacts and adapters.
- file: permissions.md
  classification: editable_model
  purpose: Roles, scopes, access policies and audit obligations.
- file: ui-model.md
  classification: editable_model
  purpose: Web components, screens, states and generated component expectations.
- file: mobile-model.md
  classification: editable_model
  purpose: Mobile components, flows, states and offline expectations.
- file: test-model.md
  classification: editable_model
  purpose: Acceptance, contract, regression and generated test cases.
- file: observability-model.md
  classification: editable_model
  purpose: Logs, metrics, traces, audit events and telemetry expectations.
- file: generation-plan.md
  classification: editable_model
  purpose: Generated outputs, custom implementation points and compiler strategy.
- file: traceability.md
  classification: editable_model
  purpose: Links BCM, rules, processes, APIs, UI, tests, generated outputs and QA
    evidence.
- file: README.md
  classification: human_readable_companion
  purpose: Human explanation of the package.
generated_outputs_expected:
  backend:
  - CRUD scaffolding when applicable
  - DTOs
  - Controllers
  - Repositories
  - Domain skeletons
  - API adapters
  frontend:
  - React components
  - Routes
  - Forms
  - Client SDK usage
  mobile:
  - Flutter components
  - Mobile flows
  - Client SDK usage
  contracts:
  - OpenAPI rendered contract
  - SDKs
  - Swagger documentation
  tests:
  - Repetitive unit tests
  - Contract tests
  - Acceptance test skeletons
  operations:
  - Observability assets
  - Dashboard definitions
  - Alert definitions
  - Runbook skeletons
custom_implementation_expected:
- Complex business rules not expressible by generator templates.
- External adapters.
- Security-sensitive policies.
- Performance-sensitive queries.
- Legacy migration mappings requiring human decision.
minimum_business_rule_format:
  id_pattern: RN-###
  required_fields:
  - id
  - statement
  - applies_to
  - enforcement_point
  - severity
  - audit_required
  - test_refs
definition_of_ready:
- Capability exists in BCM-001.
- Capability has dependency profile in BCM-002.
- Package folder exists under the capability packages root.
- Required editable model artifacts exist.
- Generation plan identifies generated outputs and custom implementation points.
- Traceability links the capability to Healthcare Operating Model, BCM, rules, processes
  and tests.
definition_of_done:
- Package version is updated.
- All editable models are valid YAML.
- Generated outputs are produced or explicitly deferred.
- Custom implementation points are implemented or explicitly deferred.
- QA evidence exists for model validation, generated output validation and custom
  rule validation.
- Source registries classify package artifacts correctly.
```
