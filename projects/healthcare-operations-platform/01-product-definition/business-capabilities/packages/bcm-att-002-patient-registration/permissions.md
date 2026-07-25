---
id: HOP-PERM-BCM-ATT-002
format: markdown_structured_payload
type: permissions
name: Patient Registration Permissions
version: 0.1.0
status: modeled
---

# Patient Registration Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-ATT-002
  type: permissions
  name: Patient Registration Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-002
  depends_on_capability: BCM-PLT-001
scopes:
- code: patient.register
  description: Start, commit or cancel a patient registration request.
- code: patient.register.read
  description: Read patient registration request history for audit and review.
roles:
- role: receptionist
  grants:
  - patient.register
  - patient.register.read
- role: tenant-administrator
  grants:
  - patient.register
  - patient.register.read
- role: laboratory-manager
  grants:
  - patient.register.read
- role: support-analyst
  grants:
  - patient.register.read
access_policies:
- id: POL-REG-002-01
  statement: Registration commands are scoped to the actor's tenant, laboratory and
    branch.
  enforcement: row_level_tenant_laboratory_branch_filter
- id: POL-REG-002-02
  statement: Registration must delegate patient state mutation to BCM-PER-002 aggregate
    commands.
  enforcement: cross_capability_delegation_policy
- id: POL-REG-002-03
  statement: Registration must not disclose duplicate-detection candidates outside
    the actor's tenant scope.
  enforcement: cross_context_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: PatientRegistrationStarted
    fields:
    - registrationRequestId
    - actorId
    - branchId
    - intakeChannel
  - event: PatientRegistrationCommitted
    fields:
    - registrationRequestId
    - outcomePatientId
    - actorId
    - branchId
  - event: PatientRegistrationCancelled
    fields:
    - registrationRequestId
    - actorId
    - reasonCode
```
