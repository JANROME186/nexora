---
id: TD-DB-003
format: markdown_structured_payload
type: technical-debt-item
name: No backend read API exists yet for the new country/locale/currency reference
  tables
version: 1.0.0
status: open
---

# No Backend Read Api Exists Yet For The New Country/Locale/Currency Reference Tables

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-DB-003
  type: technical-debt-item
  name: No backend read API exists yet for the new country/locale/currency reference
    tables
  version: 1.0.0
  status: open
  created_date: 2026-07-17
source:
  discovered_during_backlog_item: HOP-ENT-FOUND-001
  module: HOP-ENTERPRISE-FOUNDATION-ALIGNMENT
  evidence: 03-architecture/data-architecture/database-architecture.md
classification:
  category: database_product_baseline_gap
  affected_area: reference_data_api
  affected_components:
  - 07-implementation/backend/src/main/resources/db/platform-foundation/schema.sql
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: No current screen or client needs to read these tables yet.
current_state:
  issue: organization.countries/locales/currencies exist as real seeded reference
    tables with no repository/service/controller layer reading them.
  compensating_control:
  - Data is static and small (2 rows each); absence of an API does not risk data loss.
target_state:
  preferred_open_source_tooling: []
  expected_integration_points:
  - New organizationmanagement/domain/ReferenceDataRepository port
  - New adapter/out/jdbc/JdbcReferenceDataRepository + adapter/in/web/ReferenceDataController
remediation:
  strategy: implement_when_a_screen_or_client_first_needs_country_locale_currency_options
  owner: backend_platform_team
  estimated_effort: small
  estimated_cost_impact: low
  target_backlog: whenever_a_consumer_first_needs_it
  dependencies_or_prerequisites: []
  incremental_remediation_triggers:
  - A registration or settings screen needs a country/locale/currency picker.
  acceptance_criteria:
  - GET endpoints exist returning countries/locales/currencies, covered by unit and
    MockMvc tests.
  owner_or_responsible_role: backend_platform_team
```
