---
id: HOP-PERM-BCM-SVC-006
format: markdown_structured_payload
type: permissions
name: Reference Range Management Permissions
version: 0.1.0
status: modeled
---

# Reference Range Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-SVC-006
  type: permissions
  name: Reference Range Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-006
  depends_on_capability: BCM-PLT-001
scopes:
- code: catalog.range.read
  description: Read reference ranges and effective snapshots.
- code: catalog.range.write
  description: Create and update reference range drafts.
- code: catalog.range.publish
  description: Publish and deprecate reference ranges.
roles:
- role: laboratory-manager
  grants:
  - catalog.range.read
  - catalog.range.write
- role: medical-director
  grants:
  - catalog.range.read
  - catalog.range.write
  - catalog.range.publish
- role: lab-technician
  grants:
  - catalog.range.read
access_policies:
- id: POL-SVC-006-01
  statement: Reference range write and publish operations are tenant and laboratory
    scoped.
  enforcement: row_level_tenant_laboratory_filter
- id: POL-SVC-006-02
  statement: Range publication is restricted to medical authority roles.
  enforcement: role_based_publish_restriction
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: ReferenceRangeCreated
    fields:
    - rangeId
    - analyteRefId
    - actorId
    - tenantId
  - event: ReferenceRangePublished
    fields:
    - rangeId
    - version
    - effectiveFrom
    - actorId
  - event: ReferenceRangeRevised
    fields:
    - rangeId
    - previousVersion
    - newVersion
    - actorId
```
