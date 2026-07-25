---
id: HOP-PROC-BCM-PER-003
format: markdown_structured_payload
type: processes
name: Doctor Management Processes
version: 0.1.0
status: modeled
---

# Doctor Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-PER-003
  type: processes
  name: Doctor Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-003
actors:
- id: catalog-administrator
  actor_ref: ACT-010
  name: Catalog Administrator
  source: ACM-001
- id: tenant-administrator
  actor_ref: ACT-002
  name: Tenant Administrator
  source: ACM-001
- id: medical-validator
  actor_ref: ACT-009
  name: Medical Validator
  source: ACM-001
processes:
- id: PRC-DOC-003-01
  name: Register doctor master data
  actor: catalog-administrator
  trigger: A doctor must be available for order referral or medical review.
  commands:
  - RegisterDoctor
  preconditions:
  - Actor holds doctor.write.
  - Duplicate detection has been consulted.
  steps:
  - Collect natural key and consult PersonDuplicateDetector.
  - Confirm no acceptable duplicate exists or resolve match with actor decision.
  - Persist Doctor aggregate as active or pending_credential status.
  - Publish DoctorRegistered event.
  outcome: DoctorRegistered
  rules:
  - RN-001
  - RN-002
  - RN-009
- id: PRC-DOC-003-02
  name: Register and verify professional credential
  actor: tenant-administrator
  trigger: A credential must be attached and verified for a doctor.
  commands:
  - AttachProfessionalCredential
  - VerifyProfessionalCredential
  - RevokeProfessionalCredential
  steps:
  - Attach credential with pending status.
  - Verify credential authenticity and mark verified.
  - Optionally mark credential revoked or expired.
  - Publish DoctorCredentialAttached and DoctorCredentialVerified events.
  outcome: DoctorCredentialVerified
  rules:
  - RN-004
  - RN-005
- id: PRC-DOC-003-03
  name: Update doctor master data
  actor: catalog-administrator
  trigger: Correction or addition to doctor identity, contacts or specialty.
  commands:
  - UpdateDoctor
  - AssignSpecialty
  - UnassignSpecialty
  steps:
  - Load current Doctor state.
  - Apply delta and validate invariants.
  - Publish DoctorUpdated with change delta.
  outcome: DoctorUpdated
  rules:
  - RN-008
- id: PRC-DOC-003-04
  name: Suspend and retire doctor
  actor: tenant-administrator
  trigger: A doctor should no longer be selectable as referring doctor.
  commands:
  - SuspendDoctor
  - RetireDoctor
  steps:
  - Update status to suspended or retired.
  - Refresh DoctorSnapshot eligibility filter.
  - Publish DoctorSuspended or DoctorRetired.
  outcome: DoctorSuspended
  rules:
  - RN-006
- id: PRC-DOC-003-05
  name: Prepare doctor portal access baseline
  actor: tenant-administrator
  trigger: A doctor is ready to be provisioned into the doctor portal in a later module.
  commands:
  - PreparePortalAccess
  steps:
  - Capture portal email and readiness state.
  - Publish DoctorPortalAccessPrepared.
  - Defer identity provisioning to COM-MOD-009.
  outcome: DoctorPortalAccessPrepared
  rules:
  - RN-007
commands:
- name: RegisterDoctor
  generatable: false
  custom_reason: Duplicate detection integration is a custom rule.
- name: UpdateDoctor
  generatable: true
- name: AttachProfessionalCredential
  generatable: true
- name: VerifyProfessionalCredential
  generatable: false
  custom_reason: Verification transitions and doctor activation cascade are custom
    rules.
- name: RevokeProfessionalCredential
  generatable: false
  custom_reason: Revocation cascade impacts doctor eligibility.
- name: AssignSpecialty
  generatable: true
- name: UnassignSpecialty
  generatable: true
- name: SuspendDoctor
  generatable: false
  custom_reason: Suspension refreshes downstream eligibility projections.
- name: RetireDoctor
  generatable: true
- name: PreparePortalAccess
  generatable: false
  custom_reason: Portal readiness must not short-circuit identity provisioning.
```
