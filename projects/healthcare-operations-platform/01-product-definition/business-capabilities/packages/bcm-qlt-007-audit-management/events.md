---
id: HOP-EVT-BCM-QLT-007
format: markdown_structured_payload
type: events
name: Audit Management Domain Events
version: 0.1.0
status: modeled
---

# Audit Management Domain Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-QLT-007
  type: events
  name: Audit Management Domain Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-007
events:
- id: EVT-AUD-001
  name: AuditScheduled
  description: Emitted when an audit is scheduled.
  payload:
  - name: auditId
    type: uuid
  - name: tenantId
    type: string
  - name: auditCode
    type: string
  - name: category
    type: string
  - name: plannedStartDate
    type: date
- id: EVT-AUD-002
  name: AuditFindingRecorded
  description: Emitted when an audit finding is logged.
  payload:
  - name: auditId
    type: uuid
  - name: findingId
    type: uuid
  - name: tenantId
    type: string
  - name: severity
    type: string
  - name: capaInvestigationId
    type: uuid
- id: EVT-AUD-003
  name: AuditClosed
  description: Emitted when an audit is closed.
  payload:
  - name: auditId
    type: uuid
  - name: tenantId
    type: string
  - name: auditCode
    type: string
  - name: closedAt
    type: datetime
```
