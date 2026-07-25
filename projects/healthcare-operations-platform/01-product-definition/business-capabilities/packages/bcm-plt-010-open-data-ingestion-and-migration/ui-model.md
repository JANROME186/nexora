---
id: HOP-UI-BCM-PLT-010
format: markdown_structured_payload
type: ui-model
name: Open Data Ingestion and Migration UI Model
version: 0.1.0
status: modeled
---

# Open Data Ingestion And Migration Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-PLT-010
  type: ui-model
  name: Open Data Ingestion and Migration UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-010
  target_surface: employee_portal
surfaces:
  employee_portal:
    status: admin_required
    generatable: partial
  patient_portal:
    status: not_required
    generatable: not_applicable
  doctor_portal:
    status: not_required
    generatable: not_applicable
screens:
- id: SCR-MIG-010-01
  name: Migration Jobs
  route: /admin/migration/jobs
  purpose: Create migration jobs, upload manifest-declared packages and track job
    status.
  components:
  - DataTable
  - PackageUploadForm
  - StatusBadge
  generatable: partial
  custom_reason: Package upload triggers custom manifest/checksum verification (RN-001).
- id: SCR-MIG-010-02
  name: Dry-Run Validation Review
  route: /admin/migration/jobs/{migrationJobId}/dry-run
  purpose: Review row-level errors, warnings and reconciliation totals before approving
    an import.
  components:
  - ValidationReportTable
  - ApproveAction
  generatable: partial
  custom_reason: Approval gate requires the dry run to have passed (RN-002).
- id: SCR-MIG-010-03
  name: Reconciliation Report
  route: /admin/migration/jobs/{migrationJobId}/reconciliation
  purpose: Review imported, rejected, skipped and warning counts by entity and file,
    and trigger a bounded retry.
  components:
  - ReconciliationTable
  - RetryAction
  generatable: partial
  custom_reason: Retry action invokes the custom RetryImportExecution command (RN-004).
states:
- created
- package_received
- mapped
- dry_run_validated
- approved
- executing
- reconciled
- failed
localization:
  languages:
  - en
  - es
  default: es
  message_key_namespace: migration.*
  note: 'New user-facing strings for these screens must be registered under the migration.*
    message-key namespace in the backend MessageSource and frontend locale catalogs
    established by HOP-ENT-FOUND-001/HOP-QA-ALIGN-005, not hardcoded.

    '
rationale: 'BCM-PLT-010 is an administrative onboarding/migration capability; only
  employee-portal administrators upload packages, review dry-run validation and approve
  or retry imports. No patient or doctor-facing screen is modeled here.

  '
```
