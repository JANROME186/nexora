---
id: HOP-EVT-BCM-PLT-009
format: markdown_structured_payload
type: events
name: Workflow Engine Domain Events
version: 1.0.0
---

# Workflow Engine Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-PLT-009
  type: events
  name: Workflow Engine Domain Events
  version: 1.0.0
events:
- name: WorkflowStartedEvent
  type: domain_event
  publisher: platform-operations
  payload:
    execution_id: UUID
    workflow_code: String
    tenant_id: UUID
    timestamp: Instant
- name: WorkflowCompletedEvent
  type: domain_event
  publisher: platform-operations
  payload:
    execution_id: UUID
    workflow_code: String
    status: String
    duration_ms: Long
    timestamp: Instant
- name: RollbackTriggeredEvent
  type: domain_event
  publisher: platform-operations
  payload:
    execution_id: UUID
    workflow_code: String
    failed_step: String
    reason: String
    timestamp: Instant
```
