---
id: HOP-EVT-BCM-PLT-003
format: markdown_structured_payload
type: events
name: Notification Management Events
version: 0.1.0
status: modeled
---

# Notification Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-PLT-003
  type: events
  name: Notification Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-003
domain_events:
- name: NotificationQueued
  description: A notification request was accepted and queued for dispatch.
  payload:
  - notificationRequestId
  - requestingCapability
  - recipientType
  - channel
  audit: true
- name: NotificationDispatched
  description: A notification was successfully dispatched to the provider.
  payload:
  - notificationRequestId
  - channel
  - attemptNumber
  - providerReference
  audit: true
- name: NotificationDeliveryFailed
  description: A notification exhausted its retry policy without successful dispatch.
  payload:
  - notificationRequestId
  - channel
  - attemptNumber
  audit: true
integration_events:
  published:
  - name: NotificationDispatched
    description: Signals the requesting capability that dispatch succeeded.
    consumers:
    - laboratory-results
  - name: NotificationDeliveryFailed
    description: Signals the requesting capability and audit-compliance of a failed
      delivery.
    consumers:
    - laboratory-results
    - audit-compliance
  consumed:
  - name: ResultNotificationRequested
    source: BCM-RES-007
published_language:
- NotificationQueued
- NotificationDispatched
- NotificationDeliveryFailed
```
