---
id: HOP-MKT-MANIFEST-BCM-PLT-011
format: markdown_structured_payload
type: package-manifest
name: Product Marketplace Package Manifest
version: 1.0.0
status: modeled
---

# Product Marketplace Package Manifest

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MKT-MANIFEST-BCM-PLT-011
  type: package-manifest
  name: Product Marketplace Package Manifest
  version: 1.0.0
  status: modeled
manifest:
  package_code: product-marketplace-and-entitlements
  package_versioning: semantic_versioning
  compatibility_contract: compatibility.md
  required_artifacts:
  - capability-package.md
  - marketplace-package.md
  - commercial-offer.md
  - license-plan.md
  - entitlement-policy.md
  - installation-model.md
  - upgrade-model.md
  - security-review.md
  - support-model.md
  - telemetry-model.md
  generated_outputs:
    backend:
    - package catalog APIs
    - offer APIs
    - entitlement APIs
    - installation APIs
    - compatibility evaluation APIs
    - billing event adapter ports
    web:
    - public marketplace listing model
    - marketplace package detail model
    employee_portal:
    - package administration model
    - offer administration model
    - tenant entitlement administration model
    - installation operations model
    portals_and_mobile:
    - entitlement-aware feature availability model
installability:
  supports_trial: true
  supports_plan_upgrade: true
  supports_plan_downgrade: true
  supports_suspend_resume: true
  supports_uninstall: true
  uninstall_policy: soft_disable_preserve_audit
```
