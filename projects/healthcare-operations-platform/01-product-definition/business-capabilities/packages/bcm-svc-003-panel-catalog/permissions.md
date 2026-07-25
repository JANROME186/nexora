---
id: HOP-PERM-BCM-SVC-003
format: markdown_structured_payload
type: permissions
name: Panel Catalog Permissions
version: 0.2.0
status: modeled
---

# Panel Catalog Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-SVC-003
  type: permissions
  name: Panel Catalog Permissions
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-003
  depends_on_capability: BCM-PLT-001
scopes:
- code: catalog.panel.read
  description: Read panel definitions and published snapshots.
- code: catalog.panel.write
  description: Create and update panel drafts.
- code: catalog.panel.publish
  description: Publish and deprecate panels.
- code: catalog.panel.public_read
  description: Anonymous, rate-limited read of published panel snapshots only, consumed
    by the COM-MOD-011 public website. Never grants visibility into drafts.
roles:
- role: catalog-administrator
  grants:
  - catalog.panel.read
  - catalog.panel.write
- role: laboratory-manager
  grants:
  - catalog.panel.read
  - catalog.panel.write
  - catalog.panel.publish
- role: reception-agent
  grants:
  - catalog.panel.read
- role: public-website-visitor
  grants:
  - catalog.panel.public_read
  authentication: anonymous
  governed_by: BCM-PLT-005 RateLimitPolicy (classification=public)
access_policies:
- id: POL-SVC-003-01
  statement: Catalog write and publish operations are tenant and laboratory scoped.
  enforcement: row_level_tenant_laboratory_filter
- id: POL-SVC-003-02
  statement: Unpublished drafts are visible only to authors and managers.
  enforcement: status_based_filter
- id: POL-SVC-003-03
  statement: Public, anonymous catalog reads return only status=published snapshots
    and are rate-limited per BCM-PLT-005's public classification tier.
  enforcement: status_based_filter_plus_public_rate_limit
audit_obligations:
  audit_sink: BCM-PLT-007
  events:
  - event: PanelDefinitionCreated
    fields:
    - panelId
    - actorId
    - tenantId
  - event: PanelDefinitionPublished
    fields:
    - panelId
    - version
    - actorId
  - event: PanelDefinitionDeprecated
    fields:
    - panelId
    - actorId
```
