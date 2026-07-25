---
id: HOP-BM-BCM-PER-003
format: markdown_structured_payload
type: business-model
name: Doctor Management Business Model
version: 0.1.0
status: modeled
---

# Doctor Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-PER-003
  type: business-model
  name: Doctor Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-003
  bounded_context: medical-staff
  primary_aggregate: Doctor
entities:
- id: ENT-DOC-001
  name: Doctor
  is_aggregate_root: true
  aggregate_ref: AGG-005
  description: Master data record for a physician who refers, orders or reviews diagnostic
    services.
  fields:
  - name: doctorId
    type: DoctorId
    shared_kernel_ref: VO-ID-006
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    shared_kernel_ref: VO-ID-001
    required: true
  - name: laboratoryId
    type: LaboratoryId
    shared_kernel_ref: VO-ID-002
    required: true
  - name: doctorCode
    type: string
    required: true
    unique_scope:
    - tenantId
  - name: name
    type: PersonName
    shared_kernel_ref: VO-002
    required: true
  - name: primaryDocument
    type: PersonDocument
    required: true
  - name: contacts
    type: list[PersonContact]
    required: false
  - name: address
    type: Address
    shared_kernel_ref: VO-005
    required: false
  - name: doctorType
    type: enum
    values:
    - referring_external
    - internal_medical_validator
    - both
    required: true
  - name: status
    type: enum
    values:
    - active
    - suspended
    - retired
    required: true
  - name: portalAccessBaseline
    type: DoctorPortalAccessBaseline
    required: false
  - name: audit
    type: AuditMetadata
    shared_kernel_ref: VO-007
    required: true
- id: ENT-DOC-002
  name: ProfessionalCredential
  is_aggregate_root: false
  owned_by_aggregate: Doctor
  description: Professional licensing credential attached to a doctor.
  fields:
  - name: credentialId
    type: uuid
    required: true
    identifier: true
  - name: doctorId
    type: DoctorId
    required: true
  - name: credentialType
    type: enum
    values:
    - medical_license
    - specialty_certification
    - board_certification
    - institutional_registration
    - other
    required: true
  - name: credentialNumber
    type: string
    required: true
  - name: issuingAuthority
    type: string
    required: true
  - name: issuingCountry
    type: ISO-3166-1
    required: false
  - name: issuedAt
    type: date
    required: true
  - name: expiresAt
    type: date
    required: false
  - name: verificationStatus
    type: enum
    values:
    - pending
    - verified
    - expired
    - revoked
    required: true
  - name: verifiedAt
    type: datetime
    required: false
- id: ENT-DOC-003
  name: SpecialtyAssignment
  is_aggregate_root: false
  owned_by_aggregate: Doctor
  description: Association between a doctor and a medical specialty.
  fields:
  - name: assignmentId
    type: uuid
    required: true
    identifier: true
  - name: doctorId
    type: DoctorId
    required: true
  - name: specialtyCode
    type: string
    required: true
  - name: primary
    type: boolean
    required: false
- id: ENT-DOC-004
  name: DoctorPortalAccessBaseline
  is_aggregate_root: false
  owned_by_aggregate: Doctor
  description: Baseline description of doctor portal access parameters.
  fields:
  - name: portalEmail
    type: EmailAddress
    shared_kernel_ref: VO-003
    required: false
  - name: portalStatus
    type: enum
    values:
    - not_provisioned
    - ready_for_provisioning
    - provisioned
    - disabled
    required: true
  - name: provisioningReadyAt
    type: datetime
    required: false
read_models:
- id: RM-DOC-001
  name: DoctorSnapshot
  description: Immutable minimal doctor projection consumed by downstream contexts.
  fields:
  - doctorId
  - tenantId
  - laboratoryId
  - doctorCode
  - fullName
  - primaryDocumentType
  - primaryDocumentNumberMasked
  - doctorType
  - status
  - version
  consumers:
  - orders-samples
  - laboratory-results
  - imaging-operations
  - doctor-portal
invariants:
- id: INV-DOC-001
  statement: A doctor must have a unique doctorCode within the tenant.
- id: INV-DOC-002
  statement: Only the medical-staff context may mutate Doctor aggregate state.
- id: INV-DOC-003
  statement: A doctor with no verified medical license credential cannot hold status
    active.
- id: INV-DOC-004
  statement: A suspended doctor cannot be referenced as a new referring doctor.
- id: INV-DOC-005
  statement: A doctor with an expired primary credential must be automatically flagged
    for re-verification.
external_references:
- shared_kernel:
  - VO-ID-006 DoctorId
  - VO-ID-001 TenantId
  - VO-002 PersonName
  - VO-003 EmailAddress
  - VO-005 Address
  - VO-007 AuditMetadata
- context_map:
  - REL-CTX-001 organization-management shared identifiers
```
