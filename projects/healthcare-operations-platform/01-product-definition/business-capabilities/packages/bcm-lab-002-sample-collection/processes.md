---
id: HOP-PROC-BCM-LAB-002
format: markdown_structured_payload
type: processes
name: Sample Collection Processes
version: 0.1.0
status: modeled
---

# Sample Collection Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-LAB-002
  type: processes
  name: Sample Collection Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-002
actors:
- id: sample-collector
  actor_ref: ACT-006
  name: Sample Collector
  source: ACM-001
- id: laboratory-technician
  actor_ref: ACT-007
  name: Laboratory Technician
  source: ACM-001
  note: Consumes the collection worklist as part of the shared sample lifecycle view;
    does not execute collection commands.
processes:
- id: PRC-COL-002-01
  name: Generate collection worklist
  actor: sample-collector
  trigger: An order has been accepted with sample requirements pending collection.
  commands:
  - none (query only)
  preconditions:
  - Actor holds sample.collect.
  steps:
  - List accepted order lines with a published SampleRequirement and no existing Sample.
  - Sort by branch, priority and appointment time when available.
  outcome: CollectionWorklistGenerated
  rules: []
- id: PRC-COL-002-02
  name: Collect sample
  actor: sample-collector
  trigger: The collector is physically obtaining the specimen from the patient.
  commands:
  - CollectSample
  preconditions:
  - Order line is accepted and has a published SampleRequirement.
  - Actor holds sample.collect.
  steps:
  - Capture PatientIdentitySnapshot and SampleRequirementSnapshot.
  - Capture SampleCollectionData (collector, method, site, container, timestamp, patient
    condition).
  - Create the Sample aggregate in collected status.
  - Append the first ChainOfCustodyEvent (collected).
  - Publish SampleCollected.
  outcome: SampleCollected
  rules:
  - RN-001
  - RN-002
  - RN-004
  - RN-007
  - RN-008
- id: PRC-COL-002-03
  name: Reject sample at collection
  actor: sample-collector
  trigger: The specimen cannot be validly obtained (patient refusal, contamination
    during draw, wrong container on hand).
  commands:
  - RejectSampleAtCollection
  preconditions:
  - No prior collection recorded for this order line, or collection just occurred
    and must be immediately voided.
  steps:
  - Capture structured SampleRejectionReason with rejectionStage=at_collection.
  - Transition Sample (if already created) to rejected status, or record the rejection
    without creating a Sample.
  - Append a ChainOfCustodyEvent (rejected).
  - Publish SampleRejected.
  outcome: SampleRejected
  rules:
  - RN-004
  - RN-005
  - RN-006
  - RN-009
commands:
- name: CollectSample
  generatable: false
  custom_reason: Multi-source snapshot capture and chain-of-custody initiation.
- name: RejectSampleAtCollection
  generatable: false
  custom_reason: Structured reason-code validation and terminal-state guard.
```
