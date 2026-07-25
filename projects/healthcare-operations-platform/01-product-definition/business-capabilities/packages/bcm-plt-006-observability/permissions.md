---
id: HOP-PERM-BCM-PLT-006
format: markdown_structured_payload
type: permissions
name: Observability Permissions
version: 1.0.0
---

# Observability Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-PLT-006
  type: permissions
  name: Observability Permissions
  version: 1.0.0
roles:
- SYSTEM_ADMIN
- PLATFORM_OPERATIONS
permissions:
- code: metrics:read
  description: Access Prometheus metrics and Grafana dashboards.
  assigned_roles:
  - SYSTEM_ADMIN
  - PLATFORM_OPERATIONS
- code: health:read
  description: Access detailed health probe breakdown.
  assigned_roles:
  - SYSTEM_ADMIN
  - PLATFORM_OPERATIONS
```
