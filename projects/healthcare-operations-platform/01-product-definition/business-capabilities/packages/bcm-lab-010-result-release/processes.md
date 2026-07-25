---
id: HOP-PROC-BCM-LAB-010
format: markdown_structured_payload
type: processes
name: Result Release Processes
version: 0.1.0
status: modeled
---

# Result Release Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-LAB-010
  type: processes
  name: Result Release Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-010
actors:
- id: medical-validator
  actor_ref: ACT-009
  name: Medical Validator
  source: ACM-001
processes:
- id: PRC-RLS-010-01
  name: Release result
  actor: medical-validator
  trigger: A result has been medically validated and is eligible for release.
  commands:
  - ReleaseResult
  preconditions:
  - Result is in medically_validated status.
  - Linked Sample is not rejected.
  - Actor holds result.manage as medical-validator.
  steps:
  - Run ReleaseEligibilityCheck.
  - Record ResultReleaseRecord and transition result to released.
  - Publish ResultReleased.
  outcome: ResultReleased
  rules:
  - RN-001
  - RN-002
  - RN-006
  - RN-007
- id: PRC-RLS-010-02
  name: Amend result
  actor: medical-validator
  trigger: A post-release correction is required (transcription error, updated interpretation,
    corrected value).
  commands:
  - AmendResult
  preconditions:
  - Result is in released status.
  - Actor holds result.manage as medical-validator with a verified credential.
  steps:
  - Capture AmendmentRequest with reason and corrected value.
  - Append ResultAmendment; original resultValue remains unchanged and visible in
    history.
  - Transition result to amended.
  - Publish ResultAmended.
  outcome: ResultAmended
  rules:
  - RN-003
  - RN-004
  - RN-005
  - RN-007
commands:
- name: ReleaseResult
  generatable: false
  custom_reason: Multi-precondition eligibility check spanning medical validation
    and sample status.
- name: AmendResult
  generatable: false
  custom_reason: Licensed-authority verification and append-only amendment recording.
```
