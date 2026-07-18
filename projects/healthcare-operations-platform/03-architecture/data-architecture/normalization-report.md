# HOP Third Normal Form Review

Machine-readable source: `normalization-report.yaml`. Produced by `HOP-ENT-FOUND-001`.

## Overall finding

HOP's 48 tables are in 3NF except for a small set of **deliberate, domain-driven
denormalizations**, each required by an explicit business rule for point-in-time immutability, not
an oversight.

## Documented denormalizations

| Location | What's denormalized | Business reason |
|---|---|---|
| `care_delivery.diagnostic_orders`/`diagnostic_order_lines` | Patient/doctor/branch identity + resolved price, snapshotted at order time | An order must never silently change when the source record is later edited (clinical/audit correctness) |
| `cash_sales.sales`/`sale_lines` | Price/description captured at sale time | A financial transaction must not retroactively change with catalog edits |
| `laboratory_results.results` | Analyte/reference-range definitions captured at result time | A released result must stay interpretable as originally reported |
| `organization.countries`/`locales`/`currencies` (new) | `name_es_mx`/`name_en_us` as parallel columns instead of a translation table | Small (2-row), static reference catalogs — a join buys nothing at this scale; revisit if extended to larger translatable catalogs (TD-DB-002) |

## No other issues found

No unintentional repeating groups, delimited-string multi-valued columns, or non-key transitive
dependencies were found elsewhere in the schema. `audit_events.metadata_json` (`jsonb`) is an
intentional exception for a genuinely variable-shape audit payload, not a violation.

## Closure gate compliance

Third normal form review is documented; every denormalization found has a stated business reason.
