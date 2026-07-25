---
id: HOP-OBS-BCM-RES-004
format: markdown_structured_payload
type: observability-model
name: Digital Delivery Observability Model
version: 0.1.0
status: modeled
---

# Digital Delivery Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-RES-004
  type: observability-model
  name: Digital Delivery Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-004
  depends_on_capability: BCM-PLT-006
logs:
- event: result_delivery_authorized
  level: info
  fields:
  - deliveryTicketId
  - resultId
  - recipientType
  - deliveryChannel
  correlation_id: resultId
- event: result_viewed
  level: info
  fields:
  - deliveryTicketId
  - resultId
  - recipientId
  correlation_id: resultId
- event: result_delivery_withheld
  level: warn
  fields:
  - deliveryTicketId
  - resultId
  correlation_id: resultId
- event: result_delivery_authorization_denied
  level: warn
  fields:
  - resultId
  - recipientType
  - reasonCode
  correlation_id: resultId
metrics:
- name: result_delivery_authorized_total
  type: counter
  labels:
  - tenantId
  - recipientType
  - deliveryChannel
- name: result_viewed_total
  type: counter
  labels:
  - tenantId
  - recipientType
  - deliveryChannel
- name: result_delivery_authorization_denied_total
  type: counter
  labels:
  - tenantId
  - recipientType
  - reasonCode
- name: result_delivery_to_view_latency_ms
  type: histogram
  labels:
  - tenantId
  - deliveryChannel
traces:
- span: AuthorizeResultDelivery
  child_spans:
  - RunDeliveryAuthorizationCheck
  - TriggerNotificationHook
- span: GetDeliveredResult
  child_spans:
  - ReVerifyAuthorization
  - RecordResultViewed
audit_events:
- ResultDeliveryAuthorized
- ResultViewed
- ResultDeliveryWithheld
alerts:
- name: HighDeliveryAuthorizationDenialRate
  condition: result_delivery_authorization_denied_total rate exceeds threshold
  severity: warning
- name: DeliveryToViewLatencyHigh
  condition: result_delivery_to_view_latency_ms p95 exceeds threshold
  severity: warning
```
