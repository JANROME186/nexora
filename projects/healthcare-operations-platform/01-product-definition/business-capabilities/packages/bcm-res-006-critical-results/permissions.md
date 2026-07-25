---
id: HOP-PERM-BCM-RES-006
format: markdown_structured_payload
type: permissions
name: Critical Results Permissions
version: 0.1.0
status: modeled
---

# Critical Results Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-RES-006
  type: permissions
  name: Critical Results Permissions
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-006
  depends_on_capability: BCM-PLT-001
scopes:
- code: escalation.manage
  description: Acknowledge a critical-result escalation.
- code: escalation.read
  description: View open and historical critical-result escalations.
roles:
- role: medical-validator
  grants:
  - escalation.manage
  - escalation.read
- role: technical-validator
  grants:
  - escalation.read
- role: tenant-administrator
  grants:
  - escalation.read
access_policies:
- id: POL-CRR-006-01
  statement: Escalation commands are scoped to the actor's tenant and laboratory.
  enforcement: row_level_tenant_laboratory_filter
- id: POL-CRR-006-02
  statement: This capability never mutates LaboratoryResult, including the criticalFlag
    field.
  enforcement: read_only_boundary_policy
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: CriticalResultEscalationCreated
    fields:
    - escalationId
    - resultId
    - assignedHandlerId
  - event: CriticalResultAcknowledged
    fields:
    - escalationId
    - resultId
    - acknowledgedBy
  - event: CriticalResultEscalated
    fields:
    - escalationId
    - resultId
    - escalationTier
```
