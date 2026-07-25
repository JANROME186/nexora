---
id: HOP-BUS-MOD-BCM-ORG-002
format: markdown_structured_payload
type: business-model
name: Laboratory Management Business Model
version: 1.0.0
---

# Laboratory Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BUS-MOD-BCM-ORG-002
  type: business-model
  name: Laboratory Management Business Model
  version: 1.0.0
aggregate:
  name: Laboratory
  id: AGG-002
  bounded_context: organization-management
  root_entity: LaboratoryRoot
entities:
- name: LaboratoryRoot
  type: root_entity
  fields:
    laboratory_id: UUID
    tenant_id: UUID
    code: String (unique per tenant)
    name: String
    tax_id: String
    sanitary_license_number: String
    status: Enum [PENDING_CONFIGURATION, ACTIVE, SUSPENDED, ARCHIVED]
    created_at: Instant
    updated_at: Instant
- name: LaboratorySanitaryLicense
  type: value_object
  fields:
    license_number: String
    issuing_authority: String
    issue_date: LocalDate
    expiration_date: LocalDate
    status: Enum [VALID, EXPIRING_SOON, EXPIRED, REVOKED]
    license_document_id: UUID (ref StoredDocument AGG-023)
- name: ClinicalDirectorAssignment
  type: value_object
  fields:
    doctor_id: UUID (ref Doctor AGG-005)
    professional_license_number: String
    specialty: String
    assigned_at: Instant
- name: LaboratoryOperatingHours
  type: value_object
  fields:
    timezone: String
    weekday_schedule: String
    weekend_schedule: String
    holiday_policy: String
invariants:
- Laboratory code must be unique within the owning tenant.
- An active laboratory must have a valid sanitary license and an assigned clinical
  director.
- Suspended laboratories block new order placement across all child branches.
```
