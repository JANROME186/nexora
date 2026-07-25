---
id: HOP-MKT-SEC-BCM-PLT-011
format: markdown_structured_payload
type: marketplace-security-review-model
name: Marketplace Security Review Model
version: 1.0.0
status: modeled
---

# Marketplace Security Review Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MKT-SEC-BCM-PLT-011
  type: marketplace-security-review-model
  name: Marketplace Security Review Model
  version: 1.0.0
  status: modeled
security_review:
  required_before_publish: true
  checks:
  - open_source_license_review
  - sbom_available
  - dependency_vulnerability_scan_all_severities
  - sast_static_analysis
  - secret_scan
  - permission_mapping_review
  - data_classification_review
  - tenant_isolation_review
  - audit_event_review
  - rollback_review
  prohibited_results:
  - unresolved_vulnerability
  - missing_permission_mapping
  - entitlement_bypass
  - proprietary_runtime_lock_in_without_adr
  - hardcoded_secret
approval:
  minimum_roles:
  - marketplace_security_reviewer
  - product_owner
  - platform_architect
```
