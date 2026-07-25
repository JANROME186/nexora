---
id: HOP-PERM-BCM-PLT-003
format: markdown_structured_payload
type: permissions
name: Notification Management Permissions
version: 0.1.0
status: modeled
---

# Notification Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-PLT-003
  type: permissions
  name: Notification Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-003
  depends_on_capability: BCM-PLT-001
scopes:
- code: notification.submit
  description: Submit a notification request (system/service-to-service scope).
- code: notification.read
  description: Read notification request status (system/service-to-service scope).
roles:
- role: system
  grants:
  - notification.submit
  - notification.read
- role: tenant-administrator
  grants:
  - notification.read
access_policies:
- id: POL-NOT-003-01
  statement: Notification commands are scoped to the requesting capability's tenant.
  enforcement: row_level_tenant_filter
- id: POL-NOT-003-02
  statement: All physical dispatch is delegated to NotificationProviderPort; direct
    provider access is forbidden.
  enforcement: adapter_boundary_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: NotificationQueued
    fields:
    - notificationRequestId
    - requestingCapability
    - recipientType
    - channel
  - event: NotificationDispatched
    fields:
    - notificationRequestId
    - channel
    - providerReference
  - event: NotificationDeliveryFailed
    fields:
    - notificationRequestId
    - channel
```
