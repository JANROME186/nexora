---
id: HOP-PERM-BCM-LAB-006
format: markdown_structured_payload
type: permissions
name: Laboratory Processing Permissions
version: 0.1.0
status: modeled
---

# Laboratory Processing Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-LAB-006
  type: permissions
  name: Laboratory Processing Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-006
  depends_on_capability: BCM-PLT-001
scopes:
- code: result.capture
  description: Capture a result value, record a processing incident and submit a result
    for validation.
- code: result.manage
  description: Perform LaboratoryResult state transitions delegated to sibling capabilities
    (technical validation, medical validation, release).
- code: result.read
  description: Read result history for audit, review or downstream workflows.
roles:
- role: laboratory-technician
  grants:
  - result.capture
  - result.read
- role: technical-validator
  grants:
  - result.manage
  - result.read
- role: medical-validator
  grants:
  - result.manage
  - result.read
- role: branch-administrator
  grants:
  - result.capture
  - result.read
- role: tenant-administrator
  grants:
  - result.read
access_policies:
- id: POL-LPR-006-01
  statement: Result commands are scoped to the actor's tenant, laboratory and branch.
  enforcement: row_level_tenant_laboratory_branch_filter
- id: POL-LPR-006-02
  statement: Only BCM-LAB-006, BCM-LAB-008, BCM-LAB-009 and BCM-LAB-010 commands may
    mutate LaboratoryResult state; cross-capability access is command-only, never
    direct persistence.
  enforcement: cross_capability_delegation_policy
- id: POL-LPR-006-03
  statement: AI-assisted summarization may read LaboratoryResult fields but must never
    call a validation, release or amendment command.
  enforcement: ai_governance_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: ResultCaptured
    fields:
    - resultId
    - orderId
    - sampleId
    - actorId
    - analyteId
  - event: ProcessingIncidentRecorded
    fields:
    - resultId
    - incidentType
    - actorId
  - event: ResultSubmittedForValidation
    fields:
    - resultId
    - actorId
```
