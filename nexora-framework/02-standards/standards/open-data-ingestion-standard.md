# Open Data Ingestion Standard

Artifact ID: `NXF-ODI-STD-001`
Version: `1.0.0`
Status: `approved`

## Purpose

This standard defines how Nexora products handle open, provider-deliverable data ingestion and migration.

Migration must not depend on proprietary vendor cooperation beyond exporting ordinary business data. Every product that requires migration must define a project-specific ingestion contract that implements this standard.

## Accepted Formats

Every project-specific contract must support simple formats that an incumbent provider can reasonably export:

- CSV.
- XLSX.
- JSON.

Recommended formats for large or complex migrations:

- NDJSON.
- ZIP bundle with `manifest.yaml`.

Optional formats may be added when a domain requires them, such as XML, Parquet or fixed-width files.

## Manifest

Every migration package must include a manifest when multiple files, checksums, documents or large exports are involved.

Required manifest fields:

- `source_system_name`
- `source_system_version`
- `exporting_organization`
- `export_datetime`
- `export_timezone`
- `exported_by`
- `contact_email`
- `files`
- `entity_counts`
- `checksum_algorithm`
- `checksums`
- `declared_formats`
- `declared_encoding`

## Required Process

The standard ingestion process is:

1. Receive package.
2. Verify manifest.
3. Verify checksums.
4. Profile data.
5. Map to the canonical model.
6. Execute dry-run validation.
7. Reconcile before mutation.
8. Obtain business approval.
9. Execute import through domain commands.
10. Reconcile after mutation.
11. Audit and archive evidence.

## Provider Request Policy

Minimum request template:

```text
Export the in-scope data in CSV, XLSX, JSON or NDJSON with a manifest, stable identifiers, row counts and checksums.
```

Unacceptable excuses:

- Data can only be exported through screenshots.
- Data requires proprietary viewers without structured export.
- Field meanings cannot be documented.
- Row counts or checksums cannot be provided.
- Core business identifiers cannot be included.
- Export requires unsupported manual retyping.

Acceptable constraints:

- Some historical fields may be unavailable if documented in the manifest.
- Proprietary codes may be delivered when accompanied by a code dictionary.
- Large exports may be split into multiple files when declared in the manifest.
- Sensitive fields may be masked when not required for the migration objective.

## Project Contract

Each project-specific contract must define:

- Purpose.
- Capability or domain scope.
- Accepted formats.
- Required bundle files.
- Manifest fields.
- Minimum entity groups.
- Ingestion process.
- Validation categories.
- Provider request policy.
- Required outputs.

The project-specific contract must reference `NXF-ODI-STD-001`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-ODI-STD-001
  type: framework-standard
  name: Open Data Ingestion Standard
  version: 1.0.0
  status: approved
  human_readable: open-data-ingestion-standard.md
  machine_readable: open-data-ingestion-standard.md
  owner: Nexora Engineering
purpose: Define a reusable Nexora standard for open, provider-deliverable data ingestion
  and migration across products.
scope:
  applies_to:
  - legacy_platform_migration
  - customer_onboarding
  - data_portability
  - import_export_contracts
  - migration_dry_runs
  reusable_by_projects: true
  project_specific_contract_required: true
principles:
- Migration must not depend on proprietary vendor cooperation beyond exporting ordinary
  business data.
- Every Nexora product that requires migration must define a project-specific ingestion
  contract that implements this standard.
- Accepted migration inputs must use simple, documented and broadly available formats.
- Imported data must be validated, normalized and reconciled before domain mutation.
- Imports must pass through anti-corruption mappings and domain commands.
- Failed validation must block mutation and produce actionable correction reports.
- Every import must be auditable, retryable and reversible where practical.
- The customer must be able to request the export from the incumbent provider without
  relying on proprietary tooling.
accepted_formats:
  required_minimum:
  - csv
  - xlsx
  - json
  recommended_for_large_data:
  - ndjson
  - zip_bundle
  optional_when_domain_requires:
  - xml
  - parquet
  - fixed_width
  format_rules:
    csv:
      encoding: UTF-8
      delimiter_policy: Comma preferred; semicolon accepted when declared in manifest.
      header_required: true
    xlsx:
      sheet_names_must_be_declared: true
      formulas_must_be_resolved_to_values: true
    json:
      schema_or_field_dictionary_required: true
    ndjson:
      one_entity_per_line: true
      streaming_validation_supported: true
    zip_bundle:
      manifest_required: true
required_manifest:
  filename: manifest.yaml
  required_fields:
  - source_system_name
  - source_system_version
  - exporting_organization
  - export_datetime
  - export_timezone
  - exported_by
  - contact_email
  - files
  - entity_counts
  - checksum_algorithm
  - checksums
  - declared_formats
  - declared_encoding
  recommended_fields:
  - field_dictionary
  - code_dictionaries
  - known_data_gaps
  - extraction_filters
  - privacy_constraints
  - source_timezone_policy
required_ingestion_process:
- step: receive_package
  output: received_package_record
- step: verify_manifest
  output: manifest_validation_report
- step: verify_checksums
  output: checksum_validation_report
- step: profile_data
  output: data_profile_report
- step: map_to_canonical_model
  output: field_mapping_report
- step: dry_run_validation
  output: dry_run_validation_report
- step: reconcile_before_mutation
  output: pre_import_reconciliation_report
- step: obtain_business_approval
  output: customer_approval_record
- step: execute_import_through_domain_commands
  output: import_execution_report
- step: reconcile_after_mutation
  output: post_import_reconciliation_report
- step: audit_and_archive
  output: import_audit_record
validation_categories:
- structural_validation
- required_field_validation
- data_type_validation
- referential_integrity_validation
- duplicate_detection
- business_rule_validation
- privacy_validation
- domain_consistency_validation
- reconciliation_validation
provider_request_policy:
  minimum_request_template: Export the in-scope data in CSV, XLSX, JSON or NDJSON
    with a manifest, stable identifiers, row counts and checksums.
  unacceptable_excuses:
  - Data can only be exported through screenshots.
  - Data requires proprietary viewers without structured export.
  - Field meanings cannot be documented.
  - Row counts or checksums cannot be provided.
  - Core business identifiers cannot be included.
  - Export requires unsupported manual retyping.
  acceptable_constraints:
  - Some historical fields may be unavailable if documented in the manifest.
  - Proprietary codes may be delivered when accompanied by a code dictionary.
  - Large exports may be split into multiple files when declared in the manifest.
  - Sensitive fields may be masked when not required for the migration objective.
project_contract_requirements:
  required_sections:
  - purpose
  - capability_or_domain_scope
  - accepted_formats
  - required_bundle_files
  - manifest_required_fields
  - minimum_entity_groups
  - ingestion_process
  - validation_categories
  - provider_request_policy
  - required_outputs
  must_reference_framework_standard: NXF-ODI-STD-001
definition_of_done:
- Project-specific ingestion contract exists in YAML and Markdown.
- Contract identifies accepted formats and required manifest fields.
- Contract defines validation, dry-run, reconciliation and audit outputs.
- Contract identifies anti-corruption mapping and domain command boundaries.
- Contract is registered in the project SOURCE_OF_TRUTH.md.
- Contract is agent agnostic and provider agnostic.
```
