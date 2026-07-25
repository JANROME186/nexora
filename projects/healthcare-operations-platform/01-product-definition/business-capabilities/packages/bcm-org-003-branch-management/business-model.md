---
id: HOP-BUS-MOD-BCM-ORG-003
format: markdown_structured_payload
type: business-model
name: Branch Management Business Model
version: 1.0.0
---

# Branch Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BUS-MOD-BCM-ORG-003
  type: business-model
  name: Branch Management Business Model
  version: 1.0.0
aggregate:
  name: Branch
  id: AGG-003
  bounded_context: organization-management
  root_entity: BranchRoot
entities:
- name: BranchRoot
  type: root_entity
  fields:
    branch_id: UUID
    laboratory_id: UUID (ref Laboratory AGG-002)
    tenant_id: UUID
    code: String (unique per laboratory)
    name: String
    status: Enum [CONFIGURATION, OPERATIONAL, MAINTENANCE, SUSPENDED, CLOSED]
    created_at: Instant
    updated_at: Instant
- name: BranchAddress
  type: value_object
  fields:
    street_address: String
    neighborhood: String
    city: String
    state: String
    postal_code: String
    country_code: String
    latitude: Double
    longitude: Double
- name: BranchCapacityConfig
  type: value_object
  fields:
    reception_booths_count: Integer
    sampling_cubicles_count: Integer
    max_hourly_appointments: Integer
    waiting_room_capacity: Integer
- name: BranchSchedule
  type: value_object
  fields:
    opening_time: String (HH:mm)
    closing_time: String (HH:mm)
    sampling_cutoff_time: String (HH:mm)
    operating_days: List<String>
invariants:
- Branch code must be unique within its parent laboratory.
- An operational branch must have a complete address, valid schedule, and at least
  1 sampling cubicle.
- A branch cannot be OPERATIONAL if its parent laboratory is SUSPENDED or ARCHIVED.
```
