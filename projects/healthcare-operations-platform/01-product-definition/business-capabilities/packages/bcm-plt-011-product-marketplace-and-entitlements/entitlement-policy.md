---
id: HOP-MKT-ENTITLEMENT-BCM-PLT-011
format: markdown_structured_payload
type: entitlement-policy-model
name: Marketplace Entitlement Policy
version: 1.0.0
status: modeled
---

# Marketplace Entitlement Policy

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MKT-ENTITLEMENT-BCM-PLT-011
  type: entitlement-policy-model
  name: Marketplace Entitlement Policy
  version: 1.0.0
  status: modeled
policy:
  default_decision: deny
  entitlement_scope:
  - tenant
  - laboratory
  - branch
  - role
  - permission
  - capability
  - feature
  evaluation_order:
  - tenant_status
  - package_status
  - license_status
  - compatibility_status
  - iam_permission
  - feature_flag
  - clinical_safety_control
  - usage_limit
  decision_outputs:
  - allowed
  - denied_missing_entitlement
  - denied_missing_permission
  - denied_incompatible_version
  - denied_suspended_package
  - denied_usage_limit
runtime_guards:
  every_marketplace_feature_requires_permission: true
  purchase_does_not_bypass_iam: true
  entitlement_changes_are_audited: true
  entitlement_cache_requires_revocation_invalidation: true
```
