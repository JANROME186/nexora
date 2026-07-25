---
id: HOP-OBS-BCM-PLT-004
format: markdown_structured_payload
type: observability-model
name: Integration Management Observability Model
version: 0.1.0
status: modeled
---

# Integration Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-PLT-004
  type: observability-model
  name: Integration Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-004
  depends_on_capability: BCM-PLT-006
logs:
- event: integration_endpoint_registered
  level: info
  fields:
  - endpointId
  - protocol
  - direction
  correlation_id: endpointId
- event: external_message_received
  level: info
  fields:
  - endpointId
  - externalMessageId
  correlation_id: externalMessageId
- event: message_normalization_failed
  level: warn
  fields:
  - endpointId
  - externalMessageId
  - canonicalErrorCode
  correlation_id: externalMessageId
- event: message_dead_lettered
  level: critical
  fields:
  - endpointId
  - externalMessageId
  correlation_id: externalMessageId
metrics:
- name: integration_messages_received_total
  type: counter
  labels:
  - tenantId
  - endpointId
  - protocol
- name: integration_messages_normalized_total
  type: counter
  labels:
  - tenantId
  - endpointId
- name: integration_messages_normalization_failed_total
  type: counter
  labels:
  - tenantId
  - endpointId
  - canonicalErrorCode
- name: integration_message_retry_total
  type: counter
  labels:
  - tenantId
  - endpointId
- name: integration_message_processing_duration_ms
  type: histogram
  labels:
  - tenantId
  - protocol
traces:
- span: ReceiveMessage
  child_spans:
  - InvokeIntegrationAdapterPortReceive
  - NormalizeMessage
- span: RetryMessage
  child_spans:
  - InvokeIntegrationAdapterPortAcknowledge
audit_events:
- IntegrationEndpointRegistered
- MessageNormalizationFailed
- MessageDeadLettered
alerts:
- name: IntegrationNormalizationFailureRateHigh
  condition: integration_messages_normalization_failed_total rate exceeds threshold
  severity: high
- name: IntegrationMessageDeadLettered
  condition: integration_message_dead_lettered occurs
  severity: critical
```
