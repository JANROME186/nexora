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

## Initial diagnostic services/tests/studies seed (deliberately not seeded)

The standard's requirement here is conditional ("seed data for initial diagnostic tests and
studies... when business allows"). Publishing a specific, business-real test/panel list without
commercial sign-off on an actual customer's test menu would be fabricated catalog content
presented as authoritative — assessed as a real risk, not a shortcut worth taking. MVP-MOD-002's
catalog module is already closed and its lifecycle already validated end to end with
application-generated test data during QA. **Recommended next step**: seed a real menu, using the
same idempotent SQL pattern established here, once a pilot customer's test menu is confirmed by
the business.

## Seed execution mechanism

Idempotent SQL `INSERT ... ON CONFLICT DO NOTHING`, co-located with each table's `CREATE TABLE` in
`schema.sql`, applied on every local-profile `DataSource` startup — not only during automated
tests.
