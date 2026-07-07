# CAP-001 Patient Management - Domain Events

## Event catalog

| Event ID | Event name | Producer | Consumers |
|---|---|---|---|
| EVT-001 | PatientRegistered | Patient Management | Orders, Notifications, Audit, Analytics |
| EVT-002 | PatientUpdated | Patient Management | Audit, Search, Analytics |
| EVT-003 | PatientDuplicateDetected | Patient Management | Reception UI, Audit |
| EVT-004 | PatientConsentRecorded | Patient Management | Notifications, Portal, Audit |
| EVT-005 | PatientGuardianAdded | Patient Management | Orders, Portal, Audit |
| EVT-006 | PatientDeactivated | Patient Management | Orders, Portal, Audit |
| EVT-007 | PatientReactivated | Patient Management | Orders, Portal, Audit |
| EVT-008 | PatientBlocked | Patient Management | Orders, Security, Audit |

## Event example

```json
{
  "eventId": "EVT-001",
  "eventName": "PatientRegistered",
  "eventVersion": "1.0.0",
  "occurredAt": "2026-07-07T00:00:00Z",
  "tenantId": "tenant-001",
  "aggregateId": "patient-001",
  "payload": {
    "patientId": "patient-001",
    "status": "ACTIVE",
    "registeredBy": "user-001"
  }
}
```
