# Business Capability Packages

This folder contains HOP Business Capability Packages.

The Business Capability Map is the master index:

`../bcm-001/business-capability-map.md`

Each capability package is created when its capability becomes active for modeling, compilation or implementation.

Capability packages are the primary unit of HOP development. Modules may group capabilities for roadmap planning, but the capability package is the autonomous, versionable product unit.

Each package must follow:

`../../../../../nexora-framework/02-standards/standards/capability-package-standard.md`

The first capability packages to create belong to `MVP-MOD-002 Diagnostic Catalog`:

- `bcm-svc-001-diagnostic-service-catalog/`
- `bcm-svc-002-test-catalog/`
- `bcm-svc-003-panel-catalog/`
- `bcm-svc-004-analyte-catalog/`
- `bcm-svc-005-patient-preparation-management/`
- `bcm-svc-006-reference-range-management/`
- `bcm-svc-007-sample-catalog/`
- `bcm-svc-009-price-list-management/`

`MVP-MOD-003 People and Clinical Master Data` capability packages:

- `bcm-per-001-person-management/`
- `bcm-per-002-patient-management/`
- `bcm-per-003-doctor-management/`
- `bcm-att-002-patient-registration/`

`MVP-MOD-004 Front Desk and Care Delivery` capability packages:

- `bcm-att-001-appointment-scheduling/`
- `bcm-att-003-reception-management/`
- `bcm-att-004-admission-management/`
- `bcm-att-006-quotation-management/`
- `bcm-lab-001-diagnostic-order-management/`

The ingestion and migration capability package to create for `MVP-MOD-008` is:

- `bcm-plt-010-open-data-ingestion-and-migration/`

The marketplace capability package to create for `COM-MOD-017` is:

- `bcm-plt-011-product-marketplace-and-entitlements/`
