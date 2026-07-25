---
id: HOP-TST-BCM-ORG-001
format: markdown_structured_payload
type: test-model
name: Tenant Management Test Model
version: 1.0.0
---

# Tenant Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TST-BCM-ORG-001
  type: test-model
  name: Tenant Management Test Model
  version: 1.0.0
test_cases:
- id: TEST-TEN-001
  name: Verify Tenant Provisioning Initializes Schema Isolation
  type: integration
  target_rule: RN-TEN-001
  expected_result: Schema/RLS context initialized and default admin assigned.
- id: TEST-TEN-002
  name: Verify Suspended Tenant Blocks Requests
  type: security
  target_rule: RN-TEN-002
  expected_result: 403 Forbidden with TENANT_SUSPENDED error code.
- id: TEST-TEN-003
  name: Verify Resource Quota Exceeded Enforcement
  type: unit
  target_rule: RN-TEN-003
  expected_result: QuotaExceededException thrown when user count exceeds quota.
- id: TEST-TEN-004
  name: Verify PostgreSQL Native RLS Policy Enforcement
  type: database_security
  target_rule: RN-TEN-004
  expected_result: Queries without app.current_tenant set fail or return zero rows.
```
