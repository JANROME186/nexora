---
id: TD-DB-002
format: markdown_structured_payload
type: technical-debt-item
name: Diagnostic catalog business tables are not yet translatable (single name column,
  no es-MX/en-US variants)
version: 2.0.0
status: closed
---

# Diagnostic Catalog Business Tables Are Not Yet Translatable (Single Name Column, No Es Mx/En Us Variants)

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-DB-002
  type: technical-debt-item
  name: Diagnostic catalog business tables are not yet translatable (single name column,
    no es-MX/en-US variants)
  version: 2.0.0
  status: closed
  created_date: 2026-07-17
  updated_date: 2026-07-26
source:
  discovered_during_backlog_item: HOP-ENT-FOUND-001
  module: HOP-ENTERPRISE-FOUNDATION-ALIGNMENT
  evidence: 03-architecture/data-architecture/database-architecture.md
classification:
  category: database_product_baseline_gap
  affected_area: catalog_test_configuration_localization
  affected_components:
  - 07-implementation/backend/src/main/resources/db/catalog-test-configuration/schema.sql
  risk_level: medium
  urgency: low
  blocking: false
  reason_non_blocking: 'MVP-MOD-002 is already closed; no current screen or business
    requirement demands a second catalog language today. Altering an already-closed
    module''s schema without a dedicated backlog item and migration plan was judged
    out of scope for this platform-level iteration.

    '
current_state:
  issue: 'catalog.diagnostic_services, catalog.test_definitions, catalog.panel_definitions
    and catalog.analyte_definitions each have a single name/equivalent column with
    no locale variant, unlike the new organization.countries/locales/currencies tables
    added by HOP-ENT-FOUND-001.

    '
  compensating_control:
  - The employee-portal (internal, Spanish-speaking staff assumed today) is the only
    current consumer of catalog names; no external/patient-facing surface renders
    them yet.
target_state:
  preferred_open_source_tooling: []
  expected_integration_points:
  - db/catalog-test-configuration/schema.sql (add name_es_mx/name_en_us parallel columns,
    matching the pattern established by organization.countries/locales/currencies)
remediation:
  strategy: gradual_when_catalog_test_configuration_is_next_touched_by_a_code_changing_backlog_item
  owner: backend_platform_team
  estimated_effort: medium
  estimated_cost_impact: low
  target_backlog: next_catalog_test_configuration_backlog_item
  dependencies_or_prerequisites: []
  incremental_remediation_triggers:
  - A patient/doctor portal or public-facing surface needs to render catalog names.
  - A second operating locale is committed to for a real customer.
  acceptance_criteria:
  - catalog.diagnostic_services/test_definitions/panel_definitions/analyte_definitions
    expose es-MX and en-US name variants.
  owner_or_responsible_role: backend_platform_team
disposition_history:
- backlog_item: HOP-HARD-DATA-001
  date: 2026-07-26
  disposition: closed
  reason: 'Re-audited the current schema and code against this item''s own acceptance
    criteria rather than assuming the original description was still accurate. Every one of
    the four named tables (catalog.diagnostic_services, test_definitions, panel_definitions,
    analyte_definitions) has carried parallel name_en/name_es columns since the original
    MVP-MOD-002-BE-001 compile (git history shows a single commit introducing them, predating
    this debt item''s own 2026-07-17 creation date under HOP-ENT-FOUND-001); the description''s
    "single name column" premise no longer (and likely never, for these four tables
    specifically) held. The bilingual support is end-to-end, not just schema-level: a shared
    LocalizedText(en, es) domain value object
    (catalogtestconfiguration/shared/LocalizedText.java) is threaded through every one of the
    four aggregates'' Create/Update commands, application services and web controllers, and
    seed data confirms real es-MX/en-US translations (e.g. "Fasting glucose"/"Glucosa en
    ayuno"), not placeholder duplicates. The acceptance criterion ("expose es-MX and en-US name
    variants") is therefore objectively already met; no schema migration or code change was
    required to close it. Residual, explicitly non-blocking naming-convention inconsistency:
    these columns are named name_en/name_es (generic) rather than the
    name_es_mx/name_en_us convention organization.countries/locales/currencies use; both encode
    the same two locales (es-MX, en-US) so this is a cosmetic naming difference, not a
    functional gap, and is not tracked as further debt.'
```
