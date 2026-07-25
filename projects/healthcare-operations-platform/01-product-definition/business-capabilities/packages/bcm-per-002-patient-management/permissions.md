---
id: HOP-PERM-BCM-PER-002
format: markdown_structured_payload
type: permissions
name: Patient Management Permissions
version: 0.1.0
status: modeled
---

# Patient Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-PER-002
  type: permissions
  name: Patient Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-002
  depends_on_capability: BCM-PLT-001
scopes:
- code: patient.read
  description: Read patient master data and snapshots.
- code: patient.write
  description: Create, update, deactivate patients and manage consents, documents
    and representatives.
- code: patient.merge
  description: Merge patient records within patient-management.
- code: patient.self.read
  description: Patient self-read of own PatientSnapshot (future portal scope).
roles:
- role: receptionist
  grants:
  - patient.read
  - patient.write
- role: laboratory-manager
  grants:
  - patient.read
- role: tenant-administrator
  grants:
  - patient.read
  - patient.write
  - patient.merge
- role: support-analyst
  grants:
  - patient.read
  - patient.merge
- role: patient
  grants:
  - patient.self.read
  note: Future portal role, not activated in MVP-MOD-003.
- role: patient-representative
  grants:
  - patient.self.read
  note: Future portal role, honored only through PatientRepresentative authorization
    range.
access_policies:
- id: POL-PAT-002-01
  statement: Patient write operations are tenant scoped.
  enforcement: row_level_tenant_filter
- id: POL-PAT-002-02
  statement: Patient snapshot is the only view exposed to non-owning contexts.
  enforcement: published_language_only
- id: POL-PAT-002-03
  statement: Merge operations require support-analyst or tenant-administrator scope
    and produce audit trace.
  enforcement: role_and_audit_policy
- id: POL-PAT-002-04
  statement: Representative access to patient data requires an active authorization
    range.
  enforcement: time_bound_authorization_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: PatientRegistered
    fields:
    - patientId
    - actorId
    - tenantId
  - event: PatientUpdated
    fields:
    - patientId
    - changedFields
    - actorId
  - event: PatientDeactivated
    fields:
    - patientId
    - actorId
  - event: PatientMerged
    fields:
    - sourcePatientId
    - survivingPatientId
    - actorId
  - event: PatientConsentRecorded
    fields:
    - patientId
    - consentId
    - consentType
    - grantedBy
  - event: PatientConsentRevoked
    fields:
    - patientId
    - consentId
    - actorId
  - event: PatientRepresentativeAttached
    fields:
    - patientId
    - representativeId
    - actorId
  - event: PatientRepresentativeRevoked
    fields:
    - patientId
    - representativeId
    - actorId
```
