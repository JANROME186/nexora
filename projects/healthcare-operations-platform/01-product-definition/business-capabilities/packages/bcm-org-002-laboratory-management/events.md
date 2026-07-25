---
id: HOP-EVT-BCM-ORG-002
format: markdown_structured_payload
type: events
name: Laboratory Management Events
version: 1.0.0
---

# Laboratory Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-ORG-002
  type: events
  name: Laboratory Management Events
  version: 1.0.0
events:
- id: EVT-LAB-001
  name: LaboratoryRegisteredEvent
  type: domain_event
  publisher: organization-management
  summary: Emitted when a new laboratory profile is created under a tenant.
  payload_schema:
    laboratory_id: UUID
    tenant_id: UUID
    code: String
    name: String
    registered_at: Instant
- id: EVT-LAB-002
  name: LaboratorySanitaryLicenseUpdatedEvent
  type: domain_event
  publisher: organization-management
  summary: Emitted when sanitary license credentials or documents are updated.
  payload_schema:
    laboratory_id: UUID
    tenant_id: UUID
    license_number: String
    expiration_date: LocalDate
    updated_at: Instant
- id: EVT-LAB-003
  name: LaboratoryStatusChangedEvent
  type: domain_event
  publisher: organization-management
  summary: Emitted when a laboratory transitions status (ACTIVE, SUSPENDED, ARCHIVED).
  payload_schema:
    laboratory_id: UUID
    tenant_id: UUID
    previous_status: String
    new_status: String
    reason: String
    changed_at: Instant
```
