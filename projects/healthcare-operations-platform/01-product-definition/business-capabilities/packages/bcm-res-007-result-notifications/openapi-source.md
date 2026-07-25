---
id: HOP-API-SRC-BCM-RES-007
format: markdown_structured_payload
type: openapi-source
name: Result Notifications API Source Model
version: 0.1.0
status: modeled
---

# Result Notifications Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-RES-007
  type: openapi-source
  name: Result Notifications API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-007
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md. This is primarily
    an event-driven, internal capability.

    '
api:
  base_path: /api/results/notifications
  surface_classification: internal
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - notification.compose
    - notification.read
resources:
- name: ResultNotificationRequest
  operations:
  - id: listResultNotifications
    method: GET
    path: /
    scopes:
    - notification.read
    generatable: true
  - id: getResultNotification
    method: GET
    path: /{resultNotificationRequestId}
    scopes:
    - notification.read
    generatable: true
schemas_source:
- ResultNotificationRequest
error_model:
  standard: rfc7807
  domain_errors:
  - code: NOTIFICATION_DELIVERY_NOT_YET_AUTHORIZED
    maps_to_rule: RN-001
  - code: NOTIFICATION_CRITICAL_TRACE_REQUIRED
    maps_to_rule: RN-002
  - code: NOTIFICATION_DISPATCH_BOUNDARY_VIOLATION
    maps_to_rule: RN-003
  - code: NOTIFICATION_BOUNDARY_VIOLATION
    maps_to_rule: RN-004
  - code: NOTIFICATION_SCOPE_MISMATCH
    maps_to_rule: RN-005
```
