# HOP Seed Data Catalog

Machine-readable source: `seed-data-catalog.yaml`. Produced by `HOP-ENT-FOUND-001`.

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
