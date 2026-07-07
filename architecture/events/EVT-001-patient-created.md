---
id: EVT-001
name: PatientCreated
status: draft
version: 0.13.0
---

# EVT-001 PatientCreated

Emitted after successful patient registration.

## Payload

```yaml
patient_id: uuid
tenant_id: uuid
laboratory_id: uuid
branch_id: uuid
created_by: uuid
occurred_at: datetime
```

## Consumers

- Orders.
- Audit.
- Notifications.
- Analytics.
