---
id: HOP-PERM-BCM-SVC-007
format: markdown_structured_payload
type: permissions
name: Sample Catalog Permissions
version: 0.1.0
status: modeled
---

# Sample Catalog Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-SVC-007
  type: permissions
  name: Sample Catalog Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-007
  depends_on_capability: BCM-PLT-001
scopes:
- code: catalog.sample.read
  description: Read sample types, requirements and published snapshots.
- code: catalog.sample.write
  description: Create and update sample catalog drafts.
- code: catalog.sample.publish
  description: Publish and deprecate sample requirements.
roles:
- role: catalog-administrator
  grants:
  - catalog.sample.read
  - catalog.sample.write
- role: laboratory-manager
  grants:
  - catalog.sample.read
  - catalog.sample.write
  - catalog.sample.publish
- role: lab-technician
  grants:
  - catalog.sample.read
access_policies:
- id: POL-SVC-007-01
  statement: Sample catalog write and publish operations are tenant and laboratory
    scoped.
  enforcement: row_level_tenant_laboratory_filter
- id: POL-SVC-007-02
  statement: Unpublished drafts are visible only to authors and managers.
  enforcement: status_based_filter
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: SampleTypeCreated
    fields:
    - sampleTypeId
    - actorId
    - tenantId
  - event: SampleRequirementPublished
    fields:
    - requirementId
    - version
    - actorId
  - event: SampleRequirementDeprecated
    fields:
    - requirementId
    - actorId
```
