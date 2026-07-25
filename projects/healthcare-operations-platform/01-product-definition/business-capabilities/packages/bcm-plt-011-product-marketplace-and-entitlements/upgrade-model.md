---
id: HOP-MKT-UPGRADE-BCM-PLT-011
format: markdown_structured_payload
type: upgrade-model
name: Marketplace Upgrade and Retirement Model
version: 1.0.0
status: modeled
---

# Marketplace Upgrade And Retirement Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MKT-UPGRADE-BCM-PLT-011
  type: upgrade-model
  name: Marketplace Upgrade and Retirement Model
  version: 1.0.0
  status: modeled
upgrade_lifecycle:
  states:
  - upgrade_available
  - upgrade_requested
  - compatibility_pending
  - upgrade_scheduled
  - upgrading
  - upgraded
  - rollback_requested
  - rolled_back
  - failed
  required_controls:
  - compatibility_check
  - migration_plan
  - rollback_plan
  - customer_notification
  - audit_event
  - telemetry_checkpoint
retirement_lifecycle:
  states:
  - deprecation_announced
  - migration_window_open
  - retired_for_new_sales
  - retired_for_all_tenants
  rules:
  - Existing tenants must receive migration or replacement guidance before forced
    retirement.
  - Retirement cannot orphan an entitlement without explicit commercial disposition.
```
