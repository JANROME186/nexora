---
id: HOP-PERM-BCM-PLT-011
format: markdown_structured_payload
type: permissions
name: Product Marketplace and Entitlements Permissions
version: 1.0.0
status: modeled
backlog_item: COM-MOD-017-DEF
---

# Product Marketplace And Entitlements Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-PLT-011
  type: permissions
  name: Product Marketplace and Entitlements Permissions
  version: 1.0.1
  status: modeled
  backlog_item: COM-MOD-017-DEF
implementation_note: 'COM-MOD-017-QA-001 traceability check: the compiled backend/frontend
  (PermissionCode.java, RolePermissionCatalog.java, EndpointPermissionRegistry.java,
  employee-portal/src/state/permissions.ts) enforce this capability with the platform-wide
  coarse screen-level codes SCREEN_MARKETPLACE_PACKAGES, SCREEN_MARKETPLACE_OFFERS,
  SCREEN_MARKETPLACE_ENTITLEMENTS and SCREEN_MARKETPLACE_INSTALLATIONS -- one per
  employee-portal screen -- consistent with every other HOP capability package, not
  the 15 fine-grained marketplace.<resource>:<action> codes below. Those remain this
  package''s target-state action-level model under TD-IAM-002 (permission-granularity
  gap, materially_reduced, platform-wide), not yet implemented; MARKETPLACE_OPERATOR
  and TENANT_ADMIN are correctly wired to the 4 screen-level codes in RolePermissionCatalog.java
  and mirrored exactly in permissions.ts.'
permission_namespace: marketplace
permissions:
- marketplace.package:submit
- marketplace.package:certify
- marketplace.package:publish
- marketplace.package:retire
- marketplace.offer:publish
- marketplace.offer:accept
- marketplace.entitlement:grant
- marketplace.entitlement:revoke
- marketplace.installation:install
- marketplace.installation:activate
- marketplace.installation:upgrade
- marketplace.installation:rollback
- marketplace.installation:suspend
- marketplace.installation:uninstall
- marketplace.billing:event_publish
roles:
  MARKETPLACE_OPERATOR:
    permissions:
    - marketplace.package:certify
    - marketplace.package:publish
    - marketplace.package:retire
    - marketplace.offer:publish
  TENANT_ADMIN:
    permissions:
    - marketplace.offer:accept
    - marketplace.installation:install
    - marketplace.installation:activate
    - marketplace.installation:upgrade
    - marketplace.installation:rollback
    - marketplace.installation:suspend
    - marketplace.installation:uninstall
audit_required: true
```
