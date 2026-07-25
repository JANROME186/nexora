---
id: HOP-UI-BCM-ORG-001
format: markdown_structured_payload
type: ui-model
name: Tenant Management UI Model
version: 1.0.0
---

# Tenant Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-ORG-001
  type: ui-model
  name: Tenant Management UI Model
  version: 1.0.0
screens:
- id: SCR-TEN-001
  name: Tenant Administration Console
  surface: employee_portal / operations_console
  route: /admin/tenants
  components:
  - TenantListTable
  - ProvisionTenantModal
  - TenantQuotaCard
  - TenantStatusBadge
```
