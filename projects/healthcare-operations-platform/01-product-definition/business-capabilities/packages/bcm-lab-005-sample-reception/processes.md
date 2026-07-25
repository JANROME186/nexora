---
id: HOP-PROC-BCM-LAB-005
format: markdown_structured_payload
type: processes
name: Sample Reception Processes
version: 0.1.0
status: modeled
---

# Sample Reception Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-LAB-005
  type: processes
  name: Sample Reception Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-005
actors:
- id: laboratory-technician
  actor_ref: ACT-007
  name: Laboratory Technician
  source: ACM-001
processes:
- id: PRC-RCP-005-01
  name: Receive sample at laboratory
  actor: laboratory-technician
  trigger: A labeled sample has physically arrived at the laboratory for intake.
  commands:
  - ReceiveSampleAtLaboratory
  preconditions:
  - Sample.labelInfo is confirmed.
  - Actor holds sample.receive.
  steps:
  - Perform ReceptionConditionCheck.
  - When all checks pass, record SampleReceptionRecord and transition Sample to received.
  - Append a ChainOfCustodyEvent (received).
  - Publish SampleReceived.
  outcome: SampleReceived
  rules:
  - RN-001
  - RN-002
  - RN-006
  - RN-007
- id: PRC-RCP-005-02
  name: Reject sample at reception
  actor: laboratory-technician
  trigger: ReceptionConditionCheck fails one or more criteria.
  commands:
  - RejectSampleAtReception
  preconditions:
  - Sample is not already received, rejected or disposed.
  steps:
  - Capture structured SampleRejectionReason with rejectionStage=at_reception.
  - Transition Sample to rejected.
  - Append a ChainOfCustodyEvent (rejected).
  - Publish SampleRejected.
  outcome: SampleRejected
  rules:
  - RN-002
  - RN-003
  - RN-005
  - RN-007
- id: PRC-RCP-005-03
  name: Dispose sample
  actor: laboratory-technician
  trigger: A sample has completed processing or was rejected and retention requirements
    are satisfied.
  commands:
  - DisposeSample
  preconditions:
  - Sample is in a terminal state (rejected or processing complete).
  steps:
  - Capture DisposalRecord.
  - Transition Sample to disposed.
  - Append a final ChainOfCustodyEvent (disposed).
  - Publish SampleDisposed.
  outcome: SampleDisposed
  rules:
  - RN-003
  - RN-004
  - RN-006
commands:
- name: ReceiveSampleAtLaboratory
  generatable: false
  custom_reason: Multi-criterion condition check and delegated Sample.receptionRecord
    mutation.
- name: RejectSampleAtReception
  generatable: false
  custom_reason: Structured reason-code validation and terminal-state transition.
- name: DisposeSample
  generatable: false
  custom_reason: Terminal-state precondition guard and evidence-preserving disposal
    record.
```
