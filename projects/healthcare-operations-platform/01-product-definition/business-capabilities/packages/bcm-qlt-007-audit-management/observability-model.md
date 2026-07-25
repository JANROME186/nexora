---
id: HOP-OBS-BCM-QLT-007
format: markdown_structured_payload
type: observability-model
name: Audit Management Observability Model
version: 0.1.0
status: modeled
---

# Audit Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-QLT-007
  type: observability-model
  name: Audit Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-007
metrics:
- name: audits_scheduled_total
  type: counter
  description: Total number of audits scheduled.
  labels:
  - tenant_id
  - category
- name: audit_findings_total
  type: counter
  description: Total number of audit findings logged.
  labels:
  - tenant_id
  - severity
logs:
- event: AuditClosed
  level: INFO
  mdc_fields:
  - tenantId
  - userId
  - traceId
  - auditId
  - auditCode
  pattern: 'Audit closed: auditCode={auditCode}'
tracing:
  propagation_headers:
  - X-Correlation-ID
  - X-Tenant-ID
```
