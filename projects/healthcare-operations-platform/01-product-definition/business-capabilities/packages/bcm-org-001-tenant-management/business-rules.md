---
id: HOP-BUS-RLS-BCM-ORG-001
format: markdown_structured_payload
type: business-rules
name: Tenant Management Business Rules
version: 1.0.0
---

# Tenant Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BUS-RLS-BCM-ORG-001
  type: business-rules
  name: Tenant Management Business Rules
  version: 1.0.0
rules:
- id: RN-TEN-001
  statement: Tenant creation must initialize schema isolation and set default security
    policies.
  applies_to: TenantRoot
  enforcement_point: CreateTenantCommand
  severity: CRITICAL
  audit_required: true
  test_refs:
  - TEST-TEN-001
- id: RN-TEN-002
  statement: Suspended tenants must immediately block token validation and non-admin
    operational requests.
  applies_to: TenantRoot
  enforcement_point: TenantStatusInterceptor
  severity: CRITICAL
  audit_required: true
  test_refs:
  - TEST-TEN-002
- id: RN-TEN-003
  statement: Resource consumption exceeding tenant quotas must throw QuotaExceededException
    with explicit metric header.
  applies_to: TenantQuota
  enforcement_point: QuotaEnforcementAspect
  severity: HIGH
  audit_required: true
  test_refs:
  - TEST-TEN-003
- id: RN-TEN-004
  statement: Tenant isolation policy must enforce PostgreSQL native Row-Level Security
    session context on every JDBC connection (addressing TD-DB-004).
  applies_to: TenantIsolationPolicy
  enforcement_point: TenantAwareDataSource
  severity: CRITICAL
  audit_required: true
  test_refs:
  - TEST-TEN-004
```
