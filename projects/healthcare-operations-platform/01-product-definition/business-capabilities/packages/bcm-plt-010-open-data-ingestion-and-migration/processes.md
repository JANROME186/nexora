---
id: HOP-PROC-BCM-PLT-010
format: markdown_structured_payload
type: processes
name: Open Data Ingestion and Migration Processes
version: 0.1.0
status: modeled
---

# Open Data Ingestion And Migration Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-PLT-010
  type: processes
  name: Open Data Ingestion and Migration Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-010
actors:
- id: platform-administrator
  actor_ref: ACT-001
  name: Platform Super Administrator
  source: ACM-001
- id: tenant-administrator
  actor_ref: ACT-002
  name: Tenant Administrator
  source: ACM-001
  note: 'Performs the human implementation-specialist/data-migration-lead role described
    in 04-requirements/capabilities/bcm-plt-010-open-data-ingestion-and-migration/requirements.md;
    ACM-001 does not yet define dedicated Implementation Specialist / Data Migration
    Lead actor ids, so the closest existing internal_staff actors (ACT-001, ACT-002)
    are used.

    '
processes:
- id: PRC-MIG-010-01
  name: Receive migration package
  actor: tenant-administrator
  trigger: A customer or implementation specialist uploads a manifest-declared migration
    bundle.
  commands:
  - ReceiveImportPackage
  preconditions:
  - Actor holds migration.job.create scope.
  steps:
  - Verify manifest completeness and checksum against the Open Data Ingestion Standard.
  - Create ImportBatch under a new or existing MigrationJob.
  - Optionally store the raw bundle via ImportPackageStoragePort (BCM-PLT-008).
  - Publish ImportPackageReceived.
  outcome: ImportPackageReceived
  rules:
  - RN-001
  - RN-006
- id: PRC-MIG-010-02
  name: Map and dry-run validate
  actor: tenant-administrator
  trigger: An ImportBatch has been received and needs field mapping and validation.
  commands:
  - MapImportBatch
  - RunDryRunValidation
  preconditions:
  - ImportBatch exists in package_received status.
  steps:
  - Apply MappingTemplate to translate source fields to canonical entities.
  - Run structural, required-field, data-type, referential-integrity, duplicate, business-rule
    and privacy validation categories without mutating any domain.
  - Produce ImportValidationReport.
  - Publish ImportDryRunValidated.
  outcome: ImportDryRunValidated
  rules:
  - RN-002
  - RN-006
- id: PRC-MIG-010-03
  name: Approve and execute import
  actor: tenant-administrator
  trigger: The customer administrator has reviewed and approved the dry-run validation
    report.
  commands:
  - ApproveImport
  - ExecuteImport
  preconditions:
  - ImportValidationReport.passed is true.
  - Customer approval is recorded.
  steps:
  - Invoke only existing domain commands (never direct persistence) to commit accepted
    records.
  - Record each invoked command and its checkpoint in ImportExecution.
  - Produce a post-import ReconciliationReport.
  - Publish ImportReconciled.
  outcome: ImportReconciled
  rules:
  - RN-002
  - RN-003
  - RN-005
- id: PRC-MIG-010-04
  name: Retry migration job
  actor: platform-administrator
  trigger: An ImportExecution failed partway through.
  commands:
  - RetryImportExecution
  preconditions:
  - ImportExecution exists in failed status with a recorded checkpoint.
  steps:
  - Resume from the last recorded checkpoint.
  - Re-invoke only domain commands not yet committed.
  - Update ReconciliationReport.
  outcome: MigrationExecuted
  rules:
  - RN-004
  - RN-005
commands:
- name: ReceiveImportPackage
  generatable: false
  custom_reason: Manifest and checksum verification per the Open Data Ingestion Standard.
- name: MapImportBatch
  generatable: false
  custom_reason: Anti-corruption field-mapping logic.
- name: RunDryRunValidation
  generatable: false
  custom_reason: Multi-category validation without mutation.
- name: ApproveImport
  generatable: true
- name: ExecuteImport
  generatable: false
  custom_reason: Delegated exclusively to existing domain commands with checkpointed
    execution tracking.
- name: RetryImportExecution
  generatable: false
  custom_reason: Checkpoint-based idempotent resume.
```
