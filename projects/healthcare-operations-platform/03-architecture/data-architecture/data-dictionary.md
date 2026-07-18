# HOP Data Dictionary

Machine-readable source: `data-dictionary.yaml`. Produced by `HOP-ENT-FOUND-001`.

Business-meaning-level dictionary for all 48 tables across HOP's 6 `schema.sql` files (45
pre-existing + 3 new reference tables). **Column-level physical detail is authoritative in the
referenced `schema.sql` file** — this dictionary documents business purpose and relationships,
which the DDL alone does not express.

## organization / identity / audit (`db/platform-foundation/schema.sql`)

`tenants` → `laboratories` → `branches` is the tenant-scoping backbone for the whole database.
`branches` gained a `version` column this iteration (TD-BE-009 fix). `user_accounts` /
`role_assignments` back the IAM model (`role_assignments.created_by` now records the real actor,
not a hardcoded `"system"`). `audit_events` is the append-only cross-cutting audit log. New this
iteration: `countries`, `locales`, `currencies` reference tables.

## catalog (`db/catalog-test-configuration/schema.sql`)

18 tables modeling the diagnostic-service/test/panel/analyte catalog, sample requirements,
preparation instructions, reference ranges and price lists. **Not yet translatable** — see
`database-architecture.md`'s TD-DB-002.

## care_delivery (`db/front-desk-care-delivery/schema.sql`)

9 tables for diagnostic orders (with immutable patient/doctor/branch/price snapshots),
appointments, reception visits, admissions and quotations.

## orders_samples / laboratory_results (`db/laboratory-workflow/schema.sql`)

`samples` + `chain_of_custody` (specimen lifecycle); `results` + `processing_incidents` (result
lifecycle through technical/medical validation to release — read-only downstream of MVP-MOD-006).

## people (`db/people-and-clinical-master-data/schema.sql`)

10 tables for the authoritative patient and doctor master records, representatives, consents,
documents, emergency contacts, doctor credentials/specialties, merge coordination and portal
registration intake.

## cash_sales (`db/cash-sales/schema.sql`)

6 tables for cashier sessions, sales, sale lines, payment allocations and fiscal billing requests
(via the provider-agnostic `FiscalAdapterPort`) with tax lines.

Full per-table purpose and relationships: see the YAML companion.
