---
id: HOP-EVT-BCM-ORG-001
format: markdown_structured_payload
type: events
name: Tenant Management Domain Events
version: 1.0.0
---

# Tenant Management Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-ORG-001
  type: events
  name: Tenant Management Domain Events
  version: 1.0.0
events:
- name: TenantProvisionedEvent
  type: domain_event
  publisher: organization-management
  payload:
    tenant_id: UUID
    code: String
    legal_name: String
    tier: String
    timestamp: Instant
- name: TenantStatusChangedEvent
  type: domain_event
  publisher: organization-management
  payload:
    tenant_id: UUID
    previous_status: String
    new_status: String
    reason: String
    timestamp: Instant
- name: TenantQuotaExceededEvent
  type: domain_event
  publisher: organization-management
  payload:
    tenant_id: UUID
    metric_type: String
    current_value: Integer
    max_quota: Integer
    timestamp: Instant
```
