---
id: UIM-001
format: markdown_structured_payload
type: universal-import-model
version: 0.34.0
status: draft
---

# Uim 001

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: UIM-001
type: universal-import-model
owner: Data Platform Team
status: draft
version: 0.34.0
purpose: Modelo común de entrada para migrar datos de cualquier origen hacia Nexora
  sin depender de formatos propietarios.
pipeline:
- source-detection
- structure-validation
- mapping
- normalization
- business-validation
- preview
- approval
- import
- reconciliation
- audit-report
supported_sources:
- csv
- xlsx
- tsv
- json
- ndjson
- xml
- yaml
- postgresql-dump
- mysql-dump
- mariadb-dump
- sqlserver-script
- sqlite
- hl7
- fhir
- astm
- dicom
- dicomdir
- pdf
- jpg
- png
- tiff
- zip
- tar.gz
- rest-api
- graphql
- sftp
- odbc
- jdbc
canonical_objects:
- Laboratory
- Branch
- Employee
- UserAccount
- Patient
- Physician
- TestDefinition
- DiagnosticOrder
- Sample
- Result
- Report
- Invoice
- Supplier
- InventoryItem
- StockLot
- ImagingStudy
- Document
constraints:
- Imports must be idempotent.
- Production import requires approval.
- Every import produces validation and reconciliation reports.
- Source identifiers must be preserved for audit.
```
