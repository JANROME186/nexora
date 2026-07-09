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
