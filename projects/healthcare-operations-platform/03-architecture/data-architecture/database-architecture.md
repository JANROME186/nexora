# HOP Database Architecture

Machine-readable source: `database-architecture.yaml`. Produced by `HOP-ENT-FOUND-001`.

## Engine and portability

PostgreSQL via `org.postgresql:postgresql` 42.7.13, standard SQL DDL (no proprietary extensions
beyond `jsonb`), application-generated `varchar(36)` UUID primary keys. Schema definitions live as
versioned `schema.sql` files under `07-implementation/backend/src/main/resources/db/`, independent
of the local Docker Compose profile used only to run PostgreSQL locally — the database product
deliverable is the schema files plus this documentation, not the container.

## Schema organization

One PostgreSQL schema per bounded context, one `schema.sql` file per module folder:

| Postgres schema(s) | File | Owning module |
|---|---|---|
| `organization`, `identity`, `audit` | `db/platform-foundation/schema.sql` | platformfoundation |
| `catalog` | `db/catalog-test-configuration/schema.sql` | catalogtestconfiguration |
| `care_delivery` | `db/front-desk-care-delivery/schema.sql` | frontdeskcaredelivery |
| `orders_samples`, `laboratory_results` | `db/laboratory-workflow/schema.sql` | laboratoryworkflow |
| `people` | `db/people-and-clinical-master-data/schema.sql` | peopleclinicalmasterdata |
| `cash_sales` | `db/cash-sales/schema.sql` | cashsales |

`resultsanddigitaldelivery`, `documentmanagement` and `notificationmanagement` (MVP-MOD-007) have
no dedicated `schema.sql` yet — in-memory adapters only. Disclosed explicitly as **TD-DB-001**.

## Conventions

- **Primary keys**: application-generated `varchar(36)` UUIDs everywhere.
- **Audit**: `created_at`/`updated_at` per mutating table, plus a cross-cutting append-only
  `audit.audit_events` table.
- **Tenant scoping**: `tenant_id` on every business table (direct or transitive), enforced by
  application-level `WHERE` clauses, not native row-level security (**TD-DB-004**).
- **Referential integrity**: explicit foreign keys where the referenced aggregate is the owner;
  immutable point-in-time snapshots are used instead of live FKs where the domain requires it (see
  `normalization-report.md`).

## Country / locale / currency reference data (new this iteration)

Three small reference tables added to `db/platform-foundation/schema.sql`:
`organization.countries`, `organization.locales`, `organization.currencies`, each with parallel
`name_es_mx`/`name_en_us` columns, seeded with MX/US, es-MX/en-US and MXN/USD (see
`seed-data-catalog.md`). This is the recommended pattern for HOP's larger translatable catalogs
too — the diagnostic test/panel/analyte catalog (`catalog` schema) has no translation columns yet,
disclosed as **TD-DB-002** with a concrete recommended migration rather than left silent. No
backend read API was added for the new tables yet since no screen consumes them (**TD-DB-003**).

## Migration strategy

Idempotent, additive `CREATE SCHEMA/TABLE IF NOT EXISTS` files applied at local-profile startup;
no formal down-migrations exist yet. Documented as an accepted interim baseline; Flyway/Liquibase
adoption remains evaluated under `persistence-and-contract-generation-review.md` and tracked by
the broader `TD-STACK-001` stack-modernization roadmap.

## Technical debt registered

TD-DB-001 (no persistence for 3 MVP-MOD-007 modules), TD-DB-002 (catalog not translatable),
TD-DB-003 (no reference-data read API), TD-DB-004 (no native RLS) — see YAML for full detail.
