---
id: HOP-PRC-BCM-PLT-008
format: markdown_structured_payload
type: processes
name: Document Management Processes
version: 1.2.0
status: modeled
---

# Document Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PRC-BCM-PLT-008
  type: processes
  name: Document Management Processes
  version: 1.2.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-008
processes:
- id: PRC-DOC-001
  name: Upload & Store Document
  description: Store binary document, calculate SHA-256 hash, and attach retention
    policy.
  steps:
  - step_number: 1
    name: Ingest File Bytes
    actor: System / User
    action: Pass file bytes, contentType, ownerCapability, and ownerReferenceId to
      DocumentStoragePort.
  - step_number: 2
    name: Compute Hash & Persist Metadata
    actor: System
    action: Compute SHA-256 hash, write to storage adapter, and save StoredDocument
      record.
- id: PRC-DOC-002
  name: Manage Retention Schedule & Legal Hold
  description: Apply retention schedule or legal hold to stored document.
  steps:
  - step_number: 1
    name: Set Legal Hold Status
    actor: Quality Manager / Legal Counsel
    action: Set legalHold flag to true or false. Emit LegalHoldApplied event.
  - step_number: 2
    name: Update Retention Date
    actor: Compliance Officer
    action: Set retainUntil date in accordance with regulatory schedule.
- id: PRC-DOC-003
  name: Bundle Compliance Evidence Package
  description: Group related audit, EQA, and CAPA document artifacts into a zip manifest
    for regulatory inspection.
  steps:
  - step_number: 1
    name: Select Document Manifest
    actor: Compliance Officer
    action: Select list of documentIds associated with an audit or investigation cycle.
  - step_number: 2
    name: Generate Bundle & Export
    actor: System
    action: Verify SHA-256 hash for each document, create bundle manifest, and emit
      ComplianceEvidencePackageCreated event.
```
