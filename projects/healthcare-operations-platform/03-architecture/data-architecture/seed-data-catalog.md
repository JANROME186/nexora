# HOP Seed Data Catalog

Machine-readable source: `seed-data-catalog.md`. Produced by `HOP-ENT-FOUND-001`.

## Country / locale / currency seed (implemented)

Idempotent seed rows added to `db/platform-foundation/schema.sql` alongside the new reference
tables:

- **Countries**: MX (México / Mexico), US (Estados Unidos / United States)
- **Locales**: es-MX (default), en-US (fallback)
- **Currencies**: MXN, USD (2 minor-unit digits each)

This is real product/reference data loaded on every local-profile startup, not a test-only
fixture. No backend read API consumes it yet (TD-DB-003).

## Initial diagnostic services/tests/studies seed (implemented baseline)

Corrective closure added a minimal bilingual diagnostic catalog seed to
`db/catalog-test-configuration/schema.sql` using the same idempotent SQL pattern:

- **Analytes**: GLU/Glucosa/Glucose, HGB/Hemoglobina/Hemoglobin, WBC/Leucocitos/Leukocytes
- **Sample types**: SERUM/Suero/Serum, WHOLE_BLOOD/Sangre total/Whole blood
- **Tests**: GLU_FASTING/Glucosa en ayuno/Fasting glucose, CBC/Biometría hematica/Complete blood count
- **Diagnostic services**: SVC_GLU_FASTING and SVC_CBC

This seed is suitable for local MVP/commercial review and is not a final customer-specific test
menu. Before customer production rollout, the business must provide the signed-off commercial
catalog to extend or replace this baseline.

## Seed execution mechanism

Idempotent SQL `INSERT ... ON CONFLICT DO NOTHING`, co-located with each table's `CREATE TABLE` in
`schema.sql`, applied on every local-profile `DataSource` startup — not only during automated
tests.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SEED-CATALOG-001
  type: seed-data-catalog
  name: HOP Seed Data Catalog
  version: 1.0.0
  status: approved
  human_readable: seed-data-catalog.md
  machine_readable: seed-data-catalog.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-17
  source_backlog_item: HOP-ENT-FOUND-001
purpose: 'Catalog HOP''s seed/reference data as a real product deliverable (not test-only
  fixtures), per ../../../../nexora-framework/02-standards/standards/enterprise-product-foundation-standard.md
  (database_product_baseline minimum requirements: country/locale/currency catalogs
  and seed data for initial diagnostic services/tests/studies "when business allows").

  '
country_locale_currency_seed:
  status: implemented_this_iteration
  location: db/platform-foundation/schema.sql (idempotent INSERT ... ON CONFLICT DO
    NOTHING, applied alongside the CREATE TABLE statements for organization.countries/locales/currencies)
  data:
    countries:
    - country_code: MX
      name_es_mx: México
      name_en_us: Mexico
      status: active
    - country_code: US
      name_es_mx: Estados Unidos
      name_en_us: United States
      status: active
    locales:
    - locale_code: es-MX
      name_es_mx: Español (México)
      name_en_us: Spanish (Mexico)
      is_default: true
    - locale_code: en-US
      name_es_mx: Inglés (Estados Unidos)
      name_en_us: English (United States)
      is_default: false
    currencies:
    - currency_code: MXN
      name_es_mx: Peso mexicano
      name_en_us: Mexican Peso
      minor_unit_digits: 2
    - currency_code: USD
      name_es_mx: Dólar estadounidense
      name_en_us: US Dollar
      minor_unit_digits: 2
  application_scope: This is real product/reference data (loaded on every local-profile
    startup via schema.sql), not a test-only fixture. It is not yet consumed by any
    backend read API or UI screen (see database-architecture.md's TD-DB-003).
initial_diagnostic_services_tests_and_studies_seed:
  status: implemented_by_corrective_closure
  location: db/catalog-test-configuration/schema.sql (idempotent INSERT ... ON CONFLICT
    DO NOTHING)
  scope: 'Minimal commercial-review seed for the local MVP environment, not a final
    customer-specific catalog. The seed gives reviewers an initial bilingual diagnostic
    offer without fabricating a full enterprise test menu.

    '
  data:
    analytes:
    - code: GLU
      name_es: Glucosa
      name_en: Glucose
      loinc_code: 2345-7
    - code: HGB
      name_es: Hemoglobina
      name_en: Hemoglobin
      loinc_code: 718-7
    - code: WBC
      name_es: Leucocitos
      name_en: Leukocytes
      loinc_code: 6690-2
    sample_types:
    - code: SERUM
      name_es: Suero
      name_en: Serum
    - code: WHOLE_BLOOD
      name_es: Sangre total
      name_en: Whole blood
    tests:
    - code: GLU_FASTING
      name_es: Glucosa en ayuno
      name_en: Fasting glucose
    - code: CBC
      name_es: Biometria hematica
      name_en: Complete blood count
    diagnostic_services:
    - code: SVC_GLU_FASTING
      name_es: Glucosa en ayuno
      name_en: Fasting glucose
    - code: SVC_CBC
      name_es: Biometria hematica
      name_en: Complete blood count
  remaining_business_action: 'Replace or extend this minimal seed with the first pilot
    customer''s signed-off commercial test menu before any customer production rollout.

    '
seed_execution_mechanism:
  current: 'Idempotent SQL INSERT statements co-located in the same schema.sql file
    as their table''s CREATE TABLE statement, applied automatically whenever the "local"
    Spring profile initializes its DataSource (see ../technology-architecture/persistence-and-contract-generation-review.md
    and the 09-operations/runbooks/local-solution-runbook.md startup sequence).

    '
  not_test_only: 'This mechanism runs for any local-profile startup (developer machine,
    CI, or a future on-premise/team-profile deployment reusing the same schema.sql),
    not only for the automated test suite — satisfying the standard''s "does not basta
    con scripts de prueba" (not just test scripts) requirement.

    '
closure_gate_compliance: 'Country/locale/currency and a minimal diagnostic-service/test/analyte/sample
  seed are implemented as real, idempotent product data (not test-only). The diagnostic
  seed is intentionally minimal and suitable for local MVP review; customer-specific
  commercial catalog expansion remains a business-owned input for deployment readiness.

  '
```
