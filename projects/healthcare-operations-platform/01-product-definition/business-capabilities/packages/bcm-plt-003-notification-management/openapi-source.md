---
id: HOP-API-SRC-BCM-PLT-003
format: markdown_structured_payload
type: openapi-source
name: Notification Management API Source Model
version: 0.1.0
status: modeled
---

# Notification Management Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-PLT-003
  type: openapi-source
  name: Notification Management API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-003
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md. This is a system-to-system
    internal API.

    '
api:
  base_path: /api/platform/notifications
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - notification.submit
    - notification.read
resources:
- name: NotificationRequest
  operations:
  - id: submitNotificationRequest
    method: POST
    path: /
    scopes:
    - notification.submit
    generatable: true
  - id: getNotificationRequest
    method: GET
    path: /{notificationRequestId}
    scopes:
    - notification.read
    generatable: true
schemas_source:
- NotificationRequest
- NotificationDeliveryAttempt
- RecipientNotificationPreference
error_model:
  standard: rfc7807
  domain_errors:
  - code: NOTIFICATION_RECIPIENT_OPTED_OUT
    maps_to_rule: RN-001
  - code: NOTIFICATION_PROVIDER_PORT_BYPASS_ATTEMPTED
    maps_to_rule: RN-002
  - code: NOTIFICATION_CONTENT_DECISION_REJECTED
    maps_to_rule: RN-003
  - code: NOTIFICATION_RETRY_POLICY_EXHAUSTED
    maps_to_rule: RN-004
  - code: NOTIFICATION_SCOPE_MISMATCH
    maps_to_rule: RN-006
```
