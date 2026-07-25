---
id: HOP-MKT-COMPAT-BCM-PLT-011
format: markdown_structured_payload
type: package-compatibility-model
name: Marketplace Compatibility Model
version: 1.0.0
status: modeled
---

# Marketplace Compatibility Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MKT-COMPAT-BCM-PLT-011
  type: package-compatibility-model
  name: Marketplace Compatibility Model
  version: 1.0.0
  status: modeled
compatibility_dimensions:
- platform_version
- api_contract_version
- database_schema_version
- dependency_capability_versions
- tenant_region
- language_support
- currency_support
- regulatory_profile
- feature_flags
decisions:
  compatible:
    effect: allow_installation
  compatible_with_warning:
    effect: allow_with_operator_acknowledgement
  incompatible:
    effect: block_installation
  unknown:
    effect: block_until_reviewed
required_dependency_capabilities:
- BCM-PLT-001
- BCM-PLT-002
- BCM-PLT-005
- BCM-PLT-006
- BCM-PLT-007
- BCM-PLT-009
```
