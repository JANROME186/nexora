---
id: HOP-BM-BCM-PLT-008
format: markdown_structured_payload
type: business-model
name: Document Management Business Model
version: 1.2.0
status: modeled
---

# Document Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-PLT-008
  type: business-model
  name: Document Management Business Model
  version: 1.2.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-008
  bounded_context: document-management
  primary_aggregate: StoredDocument
  model_kind: aggregate_owner
entities:
- id: ENT-DOC-001
  name: StoredDocument
  is_aggregate_root: true
  description: 'Generic, domain-agnostic document record. Extended under COM-MOD-013
    with compliance category and evidence tags for regulatory document management.

    '
  fields:
  - name: documentId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: laboratoryId
    type: LaboratoryId
    required: true
  - name: ownerCapability
    type: string
    required: true
    description: Identifies the capability that created the document (e.g., BCM-QLT-002,
      BCM-QLT-006, BCM-QLT-007).
  - name: ownerReferenceId
    type: string
    required: true
    description: The owning capability's entity ID (opaque to this capability).
  - name: documentVersion
    type: integer
    required: true
  - name: contentType
    type: string
    required: true
  - name: contentHash
    type: string
    required: true
  - name: sizeBytes
    type: integer
    required: true
  - name: storageReference
    type: StorageReference
    required: true
  - name: retentionPolicy
    type: RetentionPolicy
    required: true
  - name: complianceCategory
    type: enum
    values:
    - clinical_report
    - eqa_certificate
    - capa_evidence
    - audit_report
    - sop_manual
    - general_operational
    required: false
  - name: complianceTags
    type: list[string]
    required: false
  - name: status
    type: enum
    values:
    - stored
    - superseded
    - pending_disposal
    - disposed
    - legal_hold
    required: true
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-DOC-001
  name: StorageReference
  description: Opaque, provider-agnostic pointer to the physical bytes.
  fields:
  - name: storageProvider
    type: enum
    values:
    - local_filesystem
    - object_storage_compatible
    required: true
  - name: storageKey
    type: string
    required: true
  - name: storedAt
    type: datetime
    required: true
- id: VO-DOC-002
  name: RetentionPolicy
  description: Document retention schedule and legal hold metadata.
  fields:
  - name: retentionSchedule
    type: enum
    values:
    - standard_5_year
    - extended_10_year
    - permanent
    - custom
    required: true
  - name: retainUntil
    type: date
    required: false
  - name: legalHold
    type: boolean
    required: true
    default: false
- id: VO-DOC-003
  name: ComplianceEvidencePackage
  description: Value object representing an immutable zip/bundle manifest of compliance
    evidence documents.
  fields:
  - name: packageId
    type: uuid
    required: true
  - name: title
    type: string
    required: true
  - name: documentIds
    type: list[uuid]
    required: true
  - name: generatedAt
    type: datetime
    required: true
ports:
- id: PORT-DOC-001
  name: DocumentStoragePort
  description: Provider-agnostic storage boundary.
  operations:
  - putDocument(bytes, contentType) -> StorageReference
  - getDocument(StorageReference) -> bytes
  - deleteDocument(StorageReference) -> void
  default_adapter: local_filesystem_deterministic_adapter
invariants:
- id: INV-DOC-001
  statement: A StoredDocument's contentHash must match bytes returned by DocumentStoragePort.getDocument
    at every read.
- id: INV-DOC-003
  statement: A document under legalHold cannot transition to pending_disposal or disposed
    regardless of retainUntil date.
- id: INV-DOC-005
  statement: Clinical reports and regulatory evidence documents must have retentionSchedule
    of at least standard_5_year.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-QLT-002 External Quality Controls (EQA certificates)
  - BCM-QLT-006 CAPA Management (CAPA completion evidence)
  - BCM-QLT-007 Audit Management (Audit summary reports)
```
