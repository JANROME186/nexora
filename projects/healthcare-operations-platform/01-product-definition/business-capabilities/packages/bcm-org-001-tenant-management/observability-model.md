---
id: HOP-OBS-BCM-ORG-001
format: markdown_structured_payload
type: observability-model
name: Tenant Management Observability Model
version: 1.0.0
---

# Tenant Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-ORG-001
  type: observability-model
  name: Tenant Management Observability Model
  version: 1.0.0
metrics:
- name: hop_tenant_active_total
  type: gauge
  description: Total number of active tenants.
- name: hop_tenant_quota_usage_ratio
  type: gauge
  labels:
  - tenant_id
  - resource_type
  description: Current resource usage divided by tenant quota limit.
logs:
- event: TENANT_PROVISIONED
  level: INFO
  attributes:
  - tenant_id
  - tier
  - isolation_strategy
- event: TENANT_STATUS_CHANGED
  level: WARN
  attributes:
  - tenant_id
  - previous_status
  - new_status
  - reason
```
