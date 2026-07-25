---
id: HOP-PROC-BCM-RES-007
format: markdown_structured_payload
type: processes
name: Result Notifications Processes
version: 0.1.0
status: modeled
---

# Result Notifications Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-RES-007
  type: processes
  name: Result Notifications Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-007
actors:
- id: notification-service
  actor_ref: ACT-017
  name: Notification Service
  source: ACM-001
processes:
- id: PRC-RNT-007-01
  name: Compose result-delivered notification
  actor: notification-service
  trigger: ResultDeliveryAuthorized is published for a recipient.
  commands:
  - ComposeResultNotification
  preconditions:
  - ResultDeliveryAuthorized event received.
  steps:
  - Select the result_delivered template.
  - Create ResultNotificationRequest in pending_submission status.
  - Submit to BCM-PLT-003 via SubmitNotificationRequest.
  outcome: ResultNotificationComposed
  rules:
  - RN-001
  - RN-003
  - RN-005
  - RN-006
- id: PRC-RNT-007-02
  name: Compose critical-result notification
  actor: notification-service
  trigger: ResultFlaggedCritical is published.
  commands:
  - ComposeResultNotification
  preconditions:
  - ResultFlaggedCritical event received.
  steps:
  - Select the result_critical template with elevated priority.
  - Create ResultNotificationRequest in pending_submission status.
  - Submit to BCM-PLT-003 via SubmitNotificationRequest with priority critical.
  outcome: ResultNotificationComposed
  rules:
  - RN-002
  - RN-003
  - RN-005
  - RN-006
- id: PRC-RNT-007-03
  name: Track dispatch status
  actor: notification-service
  trigger: BCM-PLT-003 publishes NotificationDispatched or NotificationDeliveryFailed
    for this request's dispatchReference.
  commands:
  - none (status mirror only)
  preconditions:
  - dispatchReference matches an existing ResultNotificationRequest.
  steps:
  - Update dispatchStatus to reflect the BCM-PLT-003 outcome.
  outcome: ResultNotificationStatusUpdated
  rules:
  - RN-006
commands:
- name: ComposeResultNotification
  generatable: false
  custom_reason: Trigger-specific template selection and mandatory-creation rule for
    critical results.
```
