---
id: HOP-PERM-BCM-LAB-008
format: markdown_structured_payload
type: permissions
name: Technical Validation Permissions
version: 0.1.0
status: modeled
---

# Technical Validation Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-LAB-008
  type: permissions
  name: Technical Validation Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-008
  depends_on_capability: BCM-PLT-001
scopes:
- code: result.manage
  description: Perform technical validation and flag a result critical.
- code: result.read
  description: Read technical validation history for audit or downstream workflows.
roles:
- role: technical-validator
  grants:
  - result.manage
  - result.read
- role: medical-validator
  grants:
  - result.read
- role: laboratory-technician
  grants:
  - result.read
- role: tenant-administrator
  grants:
  - result.read
access_policies:
- id: POL-TVL-008-01
  statement: Technical validation commands are scoped to the actor's tenant and laboratory.
  enforcement: row_level_tenant_laboratory_filter
- id: POL-TVL-008-02
  statement: This capability may write only LaboratoryResult.technicalValidation and
    LaboratoryResult.criticalFlag; no other LaboratoryResult field may be written
    from this capability.
  enforcement: cross_capability_delegation_policy
- id: POL-TVL-008-03
  statement: When tenant policy requires segregation of duties, the validating actor
    must differ from the actor who captured the result.
  enforcement: segregation_of_duties_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: ResultTechnicallyValidated
    fields:
    - resultId
    - actorId
  - event: ResultFlaggedCritical
    fields:
    - resultId
    - actorId
    - criticalReason
```
