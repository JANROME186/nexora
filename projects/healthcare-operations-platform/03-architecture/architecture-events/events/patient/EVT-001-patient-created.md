# EVT-001 Patient Created

Emitted when a patient is successfully registered in a tenant.

Consumers may include order management, notifications, analytics, audit and patient portal provisioning.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: EVT-001
type: event
name: Patient Created
status: draft
version: 0.15.0
owner: Architecture
domain: DOM-001
trigger: Patient registration completed successfully.
payload:
- name: patientId
  type: uuid
- name: tenantId
  type: uuid
- name: occurredAt
  type: datetime
consumers:
- audit
- analytics
- order-management
- notification
relations:
- type: emittedBy
  target: DOM-001
- type: causedBy
  target: US-001
```
