---
id: HOP-PROC-BCM-PER-001
format: markdown_structured_payload
type: processes
name: Person Management Processes
version: 0.1.0
status: modeled
---

# Person Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-PER-001
  type: processes
  name: Person Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-001
actors:
- id: receptionist
  actor_ref: ACT-004
  name: Receptionist
  source: ACM-001
- id: catalog-administrator
  actor_ref: ACT-010
  name: Catalog Administrator
  source: ACM-001
- id: tenant-administrator
  actor_ref: ACT-002
  name: Tenant Administrator
  source: ACM-001
- id: support-analyst
  actor_ref: ACT-018
  name: Support Analyst
  source: ACM-001
processes:
- id: PRC-PER-001-01
  name: Detect person duplicates
  actor: receptionist
  trigger: Registration or search command needs to know if a person already exists.
  commands:
  - DetectPersonDuplicates
  preconditions:
  - Actor holds person.read.
  steps:
  - Normalize provided natural key.
  - Query PersonSearchIndex within tenant scope.
  - Return ranked candidates with confidence score.
  - Record duplicate detection audit event.
  outcome: PersonDuplicateDetectionRequested
  rules:
  - RN-001
  - RN-003
  - RN-005
  - RN-006
- id: PRC-PER-001-02
  name: Rebuild person search index
  actor: tenant-administrator
  trigger: Operational or migration task requires index rebuild.
  commands:
  - RebuildPersonSearchIndex
  preconditions:
  - Actor holds person.index.rebuild.
  - Rebuild is authorized within maintenance window.
  steps:
  - Snapshot source aggregate state ranges.
  - Replay projection events in order.
  - Verify idempotent state.
  outcome: PersonSearchIndexRebuilt
  rules:
  - RN-004
- id: PRC-PER-001-03
  name: Coordinate person merge across contexts
  actor: support-analyst
  trigger: An operator has confirmed that two person records refer to the same real
    person.
  commands:
  - InitiatePersonMergeCoordination
  preconditions:
  - Both source records are within the same tenant.
  - Merge is authorized by the owning context of each record.
  steps:
  - Verify owning context of each record.
  - Delegate merge command to owning context aggregate (PatientMerged or DoctorMerged,
    only through the owning bounded context).
  - Wait for owning-context confirmation events.
  - Reconcile PersonSearchIndex projection.
  outcome: PersonMergeCoordinationCompleted
  rules:
  - RN-004
  - RN-005
commands:
- name: DetectPersonDuplicates
  generatable: false
  custom_reason: Confidence scoring and audit trace are custom rules.
- name: RebuildPersonSearchIndex
  generatable: false
  custom_reason: Operational projection replay with ordering guarantees.
- name: InitiatePersonMergeCoordination
  generatable: false
  custom_reason: Cross-context coordination pattern; owning-context mutation is delegated.
```
