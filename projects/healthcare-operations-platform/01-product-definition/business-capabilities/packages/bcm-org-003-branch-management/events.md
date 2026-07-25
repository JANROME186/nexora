---
id: HOP-EVT-BCM-ORG-003
format: markdown_structured_payload
type: events
name: Branch Management Events
version: 1.0.0
---

# Branch Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-ORG-003
  type: events
  name: Branch Management Events
  version: 1.0.0
events:
- id: EVT-BRN-001
  name: BranchCreatedEvent
  type: domain_event
  publisher: organization-management
  summary: Emitted when a new branch location is created under a laboratory.
  payload_schema:
    branch_id: UUID
    laboratory_id: UUID
    tenant_id: UUID
    code: String
    name: String
    created_at: Instant
- id: EVT-BRN-002
  name: BranchConfiguredEvent
  type: domain_event
  publisher: organization-management
  summary: Emitted when branch operating hours, capacity, and address configuration
    are saved.
  payload_schema:
    branch_id: UUID
    laboratory_id: UUID
    tenant_id: UUID
    configured_at: Instant
- id: EVT-BRN-003
  name: BranchStatusChangedEvent
  type: domain_event
  publisher: organization-management
  summary: Emitted when a branch transitions operational status.
  payload_schema:
    branch_id: UUID
    laboratory_id: UUID
    tenant_id: UUID
    previous_status: String
    new_status: String
    reason: String
    changed_at: Instant
```
