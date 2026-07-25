---
id: TD-BE-013
format: markdown_structured_payload
type: technical-debt-item
name: XLSX row-level parsing not implemented for open data ingestion and migration
version: 1.0.0
status: open
---

# Xlsx Row Level Parsing Not Implemented For Open Data Ingestion And Migration

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-013
  type: technical-debt-item
  name: XLSX row-level parsing not implemented for open data ingestion and migration
  version: 1.0.0
  status: open
  created_date: 2026-07-18
source:
  discovered_during_backlog_item: MVP-MOD-008-BE-001
  module: MVP-MOD-008-Integration-and-Migration-Readiness
  evidence: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-BE-001-validation.md
classification:
  category: incomplete_feature_scope
  affected_area: open_data_ingestion_and_migration_file_parsing
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/datamigrationportability/migrationmanagement/application/ImportFileParser.java
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: 'The HOP Open Data Ingestion Contract accepts csv, xlsx, json,
    ndjson and zip_bundle. BE-001 implements real row-level parsing for csv (Apache
    Commons CSV), json/ndjson (Jackson) and zip_bundle extraction (java.util.zip),
    and accepts xlsx as a declared format in the manifest, but ImportFileParser.countRows()
    returns ROWS_NOT_COUNTED for xlsx rather than parsing rows, and runDryRunValidation()
    surfaces that as a non-blocking rowLevelWarning rather than a hard failure. No
    migration currently in flight requires xlsx; CSV/JSON/NDJSON/ZIP cover the near-term
    provider export formats seen so far.

    '
current_state:
  issue: 'Apache POI (poi + poi-ooxml) was deliberately not added in MVP-MOD-008-BE-001:
    it pulls in a significantly larger transitive dependency footprint (xmlbeans,
    commons-compress, and others) than commons-csv, and adding it for a format with
    no immediate provider demand was judged disproportionate to this iteration''s
    scope. xlsx bundles can still be received and stored (the manifest/checksum verification
    and DocumentStoragePort archival apply uniformly to every declared format); only
    the row-count/structural-validation step is skipped for xlsx files.

    '
target_state:
  preferred_open_source_tooling:
  - Apache POI (poi-ooxml) for streaming XLSX row iteration (SXSSF/XSSF event API
    to avoid loading whole workbooks into memory for large provider exports).
  expected_integration_points:
  - ImportFileParser.countRows(String, byte[]) — add an xlsx branch.
  - MigrationManagementService.runDryRunValidation — remove the xlsx ROWS_NOT_COUNTED
    warning once real counting exists.
remediation:
  strategy: implement_when_a_real_provider_export_requires_xlsx
  owner: platform_team
  estimated_effort: small
  estimated_cost_impact: low
  target_backlog: MVP-MOD-008-BE-002_or_later_when_needed
  dependencies_or_prerequisites: []
  incremental_remediation_triggers:
  - A real migration source system's export bundle is only available in .xlsx.
  acceptance_criteria:
  - ImportFileParser.countRows("xlsx", ...) returns a real row count instead of ROWS_NOT_COUNTED.
  - runDryRunValidation no longer emits the "row-level count not evaluated" warning
    for xlsx files.
  owner_or_responsible_role: platform_team
```
