---
id: HOP-PERM-BCM-PER-001
format: markdown_structured_payload
type: permissions
name: Person Management Permissions
version: 0.1.0
status: modeled
---

# Person Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-PER-001
  type: permissions
  name: Person Management Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-001
  depends_on_capability: BCM-PLT-001
scopes:
- code: person.read
  description: Read person search index and duplicate detection results.
- code: person.merge.read
  description: Read merge coordination requests.
- code: person.merge.write
  description: Initiate cross-context person merge coordination.
- code: person.index.rebuild
  description: Rebuild the person search index during maintenance.
roles:
- role: receptionist
  grants:
  - person.read
- role: catalog-administrator
  grants:
  - person.read
- role: tenant-administrator
  grants:
  - person.read
  - person.merge.read
  - person.merge.write
  - person.index.rebuild
- role: support-analyst
  grants:
  - person.read
  - person.merge.read
  - person.merge.write
- role: laboratory-manager
  grants:
  - person.read
  - person.merge.read
access_policies:
- id: POL-PER-001-01
  statement: Person search is tenant scoped and does not disclose data from other
    tenants.
  enforcement: row_level_tenant_filter
- id: POL-PER-001-02
  statement: National identifier values must never be returned in clear text.
  enforcement: field_masking
- id: POL-PER-001-03
  statement: Merge coordination requires authorization from the owning bounded contexts.
  enforcement: cross_context_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: PersonDuplicateDetectionRequested
    fields:
    - detectionId
    - actorId
    - tenantId
    - candidateCount
  - event: PersonSearchIndexRebuilt
    fields:
    - actorId
    - tenantId
    - fromEventOffset
    - toEventOffset
  - event: PersonMergeCoordinationCompleted
    fields:
    - coordinationId
    - actorId
    - tenantId
    - sourceRecordId
    - targetRecordId
```
