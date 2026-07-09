# Business Capability Packages

This folder contains HOP Business Capability Packages.

The Business Capability Map is the master index:

`../bcm-001/business-capability-map.yaml`

Each capability package is created when its capability becomes active for modeling, compilation or implementation.

Capability packages are the primary unit of HOP development. Modules may group capabilities for roadmap planning, but the capability package is the autonomous, versionable product unit.

Each package must follow:

`../../../../../nexora-framework/02-standards/standards/capability-package-standard.yaml`

The first capability packages to create belong to `MVP-MOD-002 Diagnostic Catalog`:

- `bcm-svc-001-diagnostic-service-catalog/`
- `bcm-svc-002-test-catalog/`
- `bcm-svc-003-panel-catalog/`
- `bcm-svc-004-analyte-catalog/`
- `bcm-svc-005-patient-preparation-management/`
- `bcm-svc-006-reference-range-management/`
- `bcm-svc-007-sample-catalog/`
- `bcm-svc-009-price-list-management/`

The ingestion and migration capability package to create for `MVP-MOD-008` is:

- `bcm-plt-010-open-data-ingestion-and-migration/`
