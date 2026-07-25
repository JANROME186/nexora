---
id: HOP-EVT-BCM-RES-006
format: markdown_structured_payload
type: events
name: Critical Results Events
version: 0.1.0
status: modeled
---

# Critical Results Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-RES-006
  type: events
  name: Critical Results Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-006
domain_events:
- name: CriticalResultEscalationCreated
  description: A traceable escalation record was created for a critical result.
  payload:
  - escalationId
  - resultId
  - assignedHandlerId
  - acknowledgementDeadline
  audit: true
- name: CriticalResultAcknowledged
  description: An assigned handler acknowledged and closed the critical-result escalation.
  payload:
  - escalationId
  - resultId
  - acknowledgedBy
  - acknowledgedAt
  audit: true
- name: CriticalResultEscalated
  description: An unacknowledged escalation advanced to the next tier.
  payload:
  - escalationId
  - resultId
  - escalationTier
  audit: true
integration_events:
  published:
  - name: CriticalResultEscalationCreated
    description: Signals result notifications that a critical-result notification
      is required.
    consumers:
    - notifications
  - name: CriticalResultEscalated
    description: Signals result notifications that a re-notification to an escalated
      audience is required.
    consumers:
    - notifications
  consumed:
  - name: ResultFlaggedCritical
    source: BCM-LAB-008
published_language:
- CriticalResultEscalationCreated
- CriticalResultAcknowledged
- CriticalResultEscalated
```
