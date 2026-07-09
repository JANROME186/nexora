# HOP Open Data Ingestion Contract

Artifact ID: `HOP-ODI-001`
Version: `1.0.0`
Status: `approved`

## Purpose

This contract defines how HOP ingests information from another laboratory platform during migration.

The goal is simple: any incumbent provider should be able to deliver the required information without proprietary excuses. HOP accepts ordinary, business-friendly formats and validates everything before mutation.

## Accepted Formats

HOP accepts:

- CSV, UTF-8.
- Excel workbook.
- JSON.
- NDJSON for large datasets.
- ZIP bundles containing data files, documents and a manifest.

## Required Manifest

Every migration package must include `manifest.yaml` with:

- Source system name and version.
- Exporting organization.
- Export date and timezone.
- Export contact.
- File list.
- Entity counts.
- Checksum algorithm.
- Checksums.
- Encoding.
- Delimiter when CSV is used.

## Typical Data Groups

The migration package may include:

- Organization: laboratories, branches, departments, employees.
- People: persons, patients, doctors, companies, agreements.
- Diagnostic catalog: services, tests, panels, analytes, samples, containers, reference ranges, preparations, price lists.
- Operations: appointments, orders, order items, samples, results, result values, result reports.
- Revenue: sales, payments, billing requests, invoices when available.
- Documents: result PDFs, attachments and document indexes.

## Ingestion Process

1. Receive package.
2. Verify manifest.
3. Profile data.
4. Map to HOP canonical model through anti-corruption mappings.
5. Execute dry-run validation.
6. Obtain customer approval.
7. Execute import through domain commands only.
8. Reconcile imported, rejected, skipped and warning records.
9. Audit and archive import evidence.

## Provider Request Policy

Minimum request:

`Export the in-scope data in CSV, XLSX, JSON or NDJSON with a manifest and stable identifiers.`

Unacceptable excuses:

- Data can only be exported through screenshots.
- Data requires proprietary viewers without structured export.
- Field meanings cannot be documented.
- Row counts or checksums cannot be provided.
- Patient, catalog, order or result identifiers cannot be included.

Acceptable constraints:

- Some historical fields may be unavailable if documented in the manifest.
- Proprietary codes may be delivered when accompanied by a code dictionary.
- Large exports may be split into multiple files when declared in the manifest.

## Required Outputs

Each import must produce:

- Migration profile report.
- Field mapping report.
- Dry-run validation report.
- Reconciliation report.
- Import audit record.
- Rejected rows file.
- Warning rows file.
- Customer approval record.
