---
id: HOP-PERM-BCM-LAB-002
format: markdown_structured_payload
type: permissions
name: Sample Collection Permissions
version: 0.1.0
status: modeled
---

# Sample Collection Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-LAB-002
  type: permissions
  name: Sample Collection Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-002
  depends_on_capability: BCM-PLT-001
scopes:
- code: sample.collect
  description: Generate the collection worklist, collect a sample and reject a sample
    at collection.
- code: sample.manage
  description: Perform Sample state transitions delegated to sibling capabilities
    (labeling, reception).
- code: sample.read
  description: Read sample history for audit, review or downstream workflows.
roles:
- role: sample-collector
  grants:
  - sample.collect
  - sample.read
- role: laboratory-technician
  grants:
  - sample.manage
  - sample.read
- role: branch-administrator
  grants:
  - sample.collect
  - sample.manage
  - sample.read
- role: tenant-administrator
  grants:
  - sample.read
- role: medical-validator
  grants:
  - sample.read
access_policies:
- id: POL-COL-002-01
  statement: Sample commands are scoped to the actor's tenant, laboratory and branch.
  enforcement: row_level_tenant_laboratory_branch_filter
- id: POL-COL-002-02
  statement: Only BCM-LAB-002, BCM-LAB-003 and BCM-LAB-005 commands may mutate Sample
    state; cross-capability access is command-only, never direct persistence.
  enforcement: cross_capability_delegation_policy
- id: POL-COL-002-03
  statement: PatientIdentitySnapshot fields exposed through the API must apply the
    same masking policy as the source patient read model.
  enforcement: cross_context_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: SampleCollected
    fields:
    - sampleId
    - orderId
    - actorId
    - branchId
    - collectionMethod
  - event: SampleRejected
    fields:
    - sampleId
    - orderId
    - rejectionStage
    - reasonCode
    - actorId
```
