---
id: HOP-OBS-BCM-ORG-003
format: markdown_structured_payload
type: observability-model
name: Branch Management Observability Model
version: 1.0.0
---

# Branch Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-ORG-003
  type: observability-model
  name: Branch Management Observability Model
  version: 1.0.0
metrics:
- name: hop_branches_active_total
  type: gauge
  description: Total number of active operational branches per tenant.
- name: hop_branch_capacity_utilisation_percent
  type: gauge
  description: Current hourly appointment capacity utilization percentage per branch.
log_events:
- event_type: BRANCH_CREATED
  level: INFO
  mdc_keys:
  - tenant_id
  - laboratory_id
  - branch_id
  - user_id
- event_type: BRANCH_STATUS_CHANGED
  level: WARN
  mdc_keys:
  - tenant_id
  - laboratory_id
  - branch_id
  - user_id
  - previous_status
  - new_status
```
