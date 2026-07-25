---
id: HOP-PROC-BCM-RES-006
format: markdown_structured_payload
type: processes
name: Critical Results Processes
version: 0.1.0
status: modeled
---

# Critical Results Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-RES-006
  type: processes
  name: Critical Results Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-006
actors:
- id: medical-validator
  actor_ref: ACT-009
  name: Medical Validator
  source: ACM-001
- id: technical-validator
  actor_ref: ACT-008
  name: Technical Validator
  source: ACM-001
processes:
- id: PRC-CRR-006-01
  name: Create critical result escalation
  actor: system
  trigger: ResultFlaggedCritical is published by BCM-LAB-008.
  commands:
  - CreateCriticalResultEscalation
  preconditions:
  - ResultFlaggedCritical event received.
  steps:
  - Copy criticalReason from the event.
  - Assign an initial handler and acknowledgementDeadline per tenant policy.
  - Create CriticalResultEscalation in open status at tier 1.
  - Publish CriticalResultEscalationCreated.
  outcome: CriticalResultEscalationCreated
  rules:
  - RN-001
  - RN-005
  - RN-006
- id: PRC-CRR-006-02
  name: Acknowledge critical result
  actor: medical-validator
  trigger: The assigned handler reviews and acts on the critical value.
  commands:
  - AcknowledgeCriticalResult
  preconditions:
  - Escalation is in open or escalated status.
  - Actor holds escalation.manage.
  steps:
  - Record acknowledgedBy and acknowledgedAt.
  - Transition escalation to closed.
  - Publish CriticalResultAcknowledged.
  outcome: CriticalResultAcknowledged
  rules:
  - RN-003
  - RN-005
  - RN-006
- id: PRC-CRR-006-03
  name: Escalate unacknowledged critical result
  actor: system
  trigger: acknowledgementDeadline elapses without acknowledgement.
  commands:
  - EscalateCriticalResult
  preconditions:
  - Escalation is still in open status past its deadline.
  steps:
  - Increment escalationTier.
  - Reassign to a broader/more senior handler per tenant policy.
  - Set a new acknowledgementDeadline.
  - Transition escalation to escalated.
  - Trigger a new notification request through BCM-RES-007.
  outcome: CriticalResultEscalated
  rules:
  - RN-002
  - RN-006
commands:
- name: CreateCriticalResultEscalation
  generatable: false
  custom_reason: Mandatory, unconditional creation on every critical flag.
- name: AcknowledgeCriticalResult
  generatable: false
  custom_reason: Terminal-state guard requiring both acknowledgement fields.
- name: EscalateCriticalResult
  generatable: false
  custom_reason: Deadline-driven tier progression and cross-capability re-notification
    trigger.
```
