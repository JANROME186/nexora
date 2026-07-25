---
id: HOP-PERM-BCM-LAB-010
format: markdown_structured_payload
type: permissions
name: Result Release Permissions
version: 0.1.0
status: modeled
---

# Result Release Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-LAB-010
  type: permissions
  name: Result Release Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-010
  depends_on_capability: BCM-PLT-001
scopes:
- code: result.manage
  description: Release a medically validated result and request an amendment.
- code: result.read
  description: Read release and amendment history for audit or downstream workflows.
roles:
- role: medical-validator
  grants:
  - result.manage
  - result.read
- role: technical-validator
  grants:
  - result.read
- role: tenant-administrator
  grants:
  - result.read
access_policies:
- id: POL-RLS-010-01
  statement: Release and amendment commands are scoped to the actor's tenant and laboratory.
  enforcement: row_level_tenant_laboratory_filter
- id: POL-RLS-010-02
  statement: This capability may write only LaboratoryResult.releaseRecord and LaboratoryResult.amendments;
    no other LaboratoryResult field may be written from this capability.
  enforcement: cross_capability_delegation_policy
- id: POL-RLS-010-03
  statement: No automated, AI-assisted or service-account actor may call ReleaseResult
    or AmendResult under any tenant configuration.
  enforcement: ai_governance_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: ResultReleased
    fields:
    - resultId
    - orderId
    - actorId
  - event: ResultAmended
    fields:
    - resultId
    - actorId
    - amendmentReason
```
