---
id: HOP-API-SRC-BCM-PER-003
format: markdown_structured_payload
type: openapi-source
name: Doctor Management API Source Model
version: 0.1.0
status: modeled
---

# Doctor Management Api Source Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-SRC-BCM-PER-003
  type: openapi-source
  name: Doctor Management API Source Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-003
  note: 'Source contract model. The rendered OpenAPI document, controllers, DTOs and
    SDKs are generated outputs declared in generation-plan.md.

    '
api:
  base_path: /api/people/doctors
  surface_classification: internal
  future_surfaces:
  - classification: partner
    status: account_linking_later
    note: Doctor portal read of own DoctorSnapshot planned for COM-MOD-009.
  security:
    scheme: bearer_jwt
    required_scopes_default:
    - doctor.read
resources:
- name: Doctor
  operations:
  - id: listDoctors
    method: GET
    path: /
    scopes:
    - doctor.read
    generatable: true
  - id: getDoctor
    method: GET
    path: /{doctorId}
    scopes:
    - doctor.read
    generatable: true
  - id: getDoctorSnapshot
    method: GET
    path: /{doctorId}/snapshot
    scopes:
    - doctor.read
    generatable: false
    custom_reason: Serves downstream consumers with eligibility filter applied.
  - id: registerDoctor
    method: POST
    path: /
    scopes:
    - doctor.write
    generatable: false
    custom_reason: Duplicate detection integration is a custom rule.
  - id: updateDoctor
    method: PUT
    path: /{doctorId}
    scopes:
    - doctor.write
    generatable: true
  - id: suspendDoctor
    method: POST
    path: /{doctorId}/suspend
    scopes:
    - doctor.write
    generatable: false
    custom_reason: Cascade to downstream eligibility projection.
  - id: retireDoctor
    method: POST
    path: /{doctorId}/retire
    scopes:
    - doctor.write
    generatable: true
  - id: preparePortalAccess
    method: POST
    path: /{doctorId}/portal-access/prepare
    scopes:
    - doctor.portal.prepare
    generatable: false
    custom_reason: Portal preparation must not short-circuit identity provisioning.
- name: ProfessionalCredential
  operations:
  - id: listDoctorCredentials
    method: GET
    path: /{doctorId}/credentials
    scopes:
    - doctor.read
    generatable: true
  - id: attachDoctorCredential
    method: POST
    path: /{doctorId}/credentials
    scopes:
    - doctor.write
    generatable: true
  - id: verifyDoctorCredential
    method: POST
    path: /{doctorId}/credentials/{credentialId}/verify
    scopes:
    - doctor.credential.verify
    generatable: false
    custom_reason: Verification triggers activation cascade.
  - id: revokeDoctorCredential
    method: POST
    path: /{doctorId}/credentials/{credentialId}/revoke
    scopes:
    - doctor.credential.verify
    generatable: false
    custom_reason: Revocation cascade impacts doctor eligibility.
- name: SpecialtyAssignment
  operations:
  - id: listSpecialtyAssignments
    method: GET
    path: /{doctorId}/specialties
    scopes:
    - doctor.read
    generatable: true
  - id: assignSpecialty
    method: POST
    path: /{doctorId}/specialties
    scopes:
    - doctor.write
    generatable: true
  - id: unassignSpecialty
    method: DELETE
    path: /{doctorId}/specialties/{assignmentId}
    scopes:
    - doctor.write
    generatable: true
schemas_source:
- Doctor
- ProfessionalCredential
- SpecialtyAssignment
- DoctorPortalAccessBaseline
- DoctorSnapshot
- DoctorEligibilityStatus
error_model:
  standard: rfc7807
  domain_errors:
  - code: DOCTOR_CODE_CONFLICT
    maps_to_rule: RN-001
  - code: DOCTOR_DUPLICATE_DETECTION_REQUIRED
    maps_to_rule: RN-002
  - code: DOCTOR_ACTIVATION_UNAUTHORIZED
    maps_to_rule: RN-004
  - code: DOCTOR_NOT_ELIGIBLE
    maps_to_rule: RN-006
  - code: DOCTOR_PORTAL_PROVISIONING_REQUIRED
    maps_to_rule: RN-007
```
