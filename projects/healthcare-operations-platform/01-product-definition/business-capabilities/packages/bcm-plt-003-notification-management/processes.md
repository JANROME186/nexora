---
id: HOP-PROC-BCM-PLT-003
format: markdown_structured_payload
type: processes
name: Notification Management Processes
version: 0.1.0
status: modeled
---

# Notification Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-PLT-003
  type: processes
  name: Notification Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-003
actors:
- id: notification-service
  actor_ref: ACT-017
  name: Notification Service
  source: ACM-001
processes:
- id: PRC-NOT-003-01
  name: Submit notification request
  actor: notification-service
  trigger: A requesting capability (e.g. BCM-RES-007) needs to notify a recipient.
  commands:
  - SubmitNotificationRequest
  preconditions:
  - Requester supplies recipientId, recipientType, channel, templateReference and
    templateParameters.
  steps:
  - Create NotificationRequest in queued status.
  - Publish NotificationQueued.
  outcome: NotificationQueued
  rules:
  - RN-003
  - RN-006
- id: PRC-NOT-003-02
  name: Dispatch notification
  actor: notification-service
  trigger: A queued NotificationRequest is ready for dispatch.
  commands:
  - DispatchNotification
  preconditions:
  - Recipient preference check passes, or priority is critical with override allowed.
  steps:
  - Check RecipientNotificationPreference for the channel.
  - Invoke NotificationProviderPort.dispatch.
  - Record NotificationDeliveryAttempt with outcome.
  - Transition to dispatched, delivered or retry-pending based on outcome.
  outcome: NotificationDispatched
  rules:
  - RN-001
  - RN-002
  - RN-004
  - RN-007
- id: PRC-NOT-003-03
  name: Exhaust retries and finalize
  actor: notification-service
  trigger: A dispatch attempt fails and the retry policy's maximum attempts is reached.
  commands:
  - FinalizeFailedNotification
  preconditions:
  - deliveryAttempts count has reached the configured maximum.
  steps:
  - Transition NotificationRequest to failed.
  - Publish NotificationDeliveryFailed.
  outcome: NotificationDeliveryFailed
  rules:
  - RN-004
  - RN-007
commands:
- name: SubmitNotificationRequest
  generatable: true
- name: DispatchNotification
  generatable: false
  custom_reason: Preference check, provider-port dispatch and retry-sequencing logic.
- name: FinalizeFailedNotification
  generatable: false
  custom_reason: Terminal-state determination after exhausting the retry policy.
```
