---
id: HOP-PERM-BCM-PER-003
format: markdown_structured_payload
type: permissions
name: Doctor Management Permissions
version: 0.1.0
status: modeled
---

# Doctor Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-PER-003
  type: permissions
  name: Doctor Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-003
  depends_on_capability: BCM-PLT-001
scopes:
- code: doctor.read
  description: Read doctor master data and snapshots.
- code: doctor.write
  description: Create, update, suspend, retire doctors and manage specialties and
    portal readiness.
- code: doctor.credential.verify
  description: Verify or revoke professional credentials.
- code: doctor.portal.prepare
  description: Prepare doctor portal access baseline without provisioning identity.
- code: doctor.self.read
  description: Doctor self-read of own DoctorSnapshot (future portal scope).
roles:
- role: catalog-administrator
  grants:
  - doctor.read
  - doctor.write
- role: tenant-administrator
  grants:
  - doctor.read
  - doctor.write
  - doctor.credential.verify
  - doctor.portal.prepare
- role: medical-validator
  grants:
  - doctor.read
- role: receptionist
  grants:
  - doctor.read
- role: referring-doctor
  grants:
  - doctor.self.read
  note: Future portal role, not activated in MVP-MOD-003.
access_policies:
- id: POL-DOC-003-01
  statement: Doctor write operations are tenant scoped.
  enforcement: row_level_tenant_filter
- id: POL-DOC-003-02
  statement: DoctorSnapshot is the only projection exposed to non-owning contexts.
  enforcement: published_language_only
- id: POL-DOC-003-03
  statement: Credential verification and revocation require tenant-administrator or
    an authorized verifier role.
  enforcement: role_policy
- id: POL-DOC-003-04
  statement: Doctor portal readiness cannot grant portal access; provisioning is a
    separate module.
  enforcement: module_boundary_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: DoctorRegistered
    fields:
    - doctorId
    - actorId
    - tenantId
  - event: DoctorUpdated
    fields:
    - doctorId
    - changedFields
    - actorId
  - event: DoctorCredentialAttached
    fields:
    - doctorId
    - credentialId
    - credentialType
    - actorId
  - event: DoctorCredentialVerified
    fields:
    - doctorId
    - credentialId
    - actorId
  - event: DoctorCredentialRevoked
    fields:
    - doctorId
    - credentialId
    - actorId
  - event: DoctorSuspended
    fields:
    - doctorId
    - actorId
    - reasonCode
  - event: DoctorRetired
    fields:
    - doctorId
    - actorId
  - event: DoctorPortalAccessPrepared
    fields:
    - doctorId
    - portalStatus
    - actorId
```
