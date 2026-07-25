---
id: HOP-BM-BCM-PER-001
format: markdown_structured_payload
type: business-model
name: Person Management Business Model
version: 0.1.0
status: modeled
---

# Person Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-PER-001
  type: business-model
  name: Person Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-001
  bounded_context: patient-management
  secondary_bounded_context: medical-staff
  primary_aggregate_reference: shared_person_concepts
model_kind: shared_master_data_concepts
value_objects:
- id: VO-PER-001
  name: PersonNaturalKey
  description: Normalized natural-key attributes used for duplicate detection.
  fields:
  - name: normalizedFamilyName
    type: string
    required: true
  - name: normalizedGivenName
    type: string
    required: true
  - name: birthDate
    type: date
    required: false
  - name: sexAtBirth
    type: enum
    values:
    - female
    - male
    - unknown
    - not_disclosed
    required: false
  - name: nationalIdentifierHash
    type: string
    required: false
  rules:
  - Fields must be normalized to uppercase without diacritics before hashing.
  - National identifier hash must be one-way.
- id: VO-PER-002
  name: PersonName
  description: Structured personal name aligned with shared kernel PersonName.
  shared_kernel_ref: VO-002
  fields:
  - name: givenName
    type: string
    required: true
  - name: middleName
    type: string
    required: false
  - name: familyName
    type: string
    required: true
  - name: secondFamilyName
    type: string
    required: false
  - name: preferredName
    type: string
    required: false
- id: VO-PER-003
  name: PersonDocument
  description: Government or institutional identification document.
  fields:
  - name: documentType
    type: enum
    values:
    - national_id
    - passport
    - drivers_license
    - tax_id
    - professional_license
    - other
    required: true
  - name: documentNumber
    type: string
    required: true
  - name: issuingCountry
    type: ISO-3166-1
    required: false
  - name: issuedAt
    type: date
    required: false
  - name: expiresAt
    type: date
    required: false
  rules:
  - Document type and number must be unique within a tenant when marked primary.
  - Expired primary documents must be flagged but not deleted.
- id: VO-PER-004
  name: PersonContact
  description: Reachable contact channel for a person.
  fields:
  - name: channelType
    type: enum
    values:
    - email
    - phone_mobile
    - phone_landline
    - whatsapp
    - other
    required: true
  - name: value
    type: string
    required: true
  - name: preferred
    type: boolean
    required: false
  - name: consentToContact
    type: boolean
    required: true
  - name: locale
    type: Locale
    shared_kernel_ref: VO-008
    required: false
- id: VO-PER-005
  name: PersonAddress
  description: Residential or professional address.
  shared_kernel_ref: VO-005
read_models:
- id: RM-PER-001
  name: PersonSearchIndex
  description: Denormalized search index projecting Patient and Doctor natural keys
    and documents into a single searchable view.
  source_aggregates:
  - Patient (AGG-001)
  - Doctor (AGG-005)
  fields:
  - normalizedFamilyName
  - normalizedGivenName
  - birthDate
  - primaryDocumentType
  - primaryDocumentNumberHash
  - tenantId
  - laboratoryId
  - personKind
  - sourceAggregateId
  projection_source_events:
  - PatientRegistered
  - PatientUpdated
  - PatientMerged
  - DoctorRegistered
  - DoctorCredentialVerified
  - DoctorSuspended
  freshness_target_seconds: 30
domain_services:
- id: DS-PER-001
  name: PersonDuplicateDetector
  description: Returns candidate person matches for a proposed natural key.
  inputs:
  - PersonNaturalKey
  - tenantId
  - personKind
  outputs:
  - list of candidate matches with confidence score
  depends_on:
  - RM-PER-001
  used_by:
  - BCM-PER-002 (patient registration)
  - BCM-PER-003 (doctor registration)
  - BCM-ATT-002 (patient registration process)
  - BCM-PLT-010 (open data ingestion)
invariants:
- id: INV-PER-001-01
  statement: Person natural keys must be normalized before comparison.
- id: INV-PER-001-02
  statement: A primary document number must be unique within a tenant.
- id: INV-PER-001-03
  statement: Person read model may only be updated by projection of published domain
    events.
external_references:
- shared_kernel:
  - VO-002 PersonName
  - VO-003 EmailAddress
  - VO-004 PhoneNumber
  - VO-005 Address
  - VO-007 AuditMetadata
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
- context_map:
  - REL-CTX-002 orders-samples uses PatientSnapshot
```
