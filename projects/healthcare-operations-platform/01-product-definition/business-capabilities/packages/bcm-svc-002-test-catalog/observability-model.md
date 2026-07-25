---
id: HOP-OBS-BCM-SVC-002
format: markdown_structured_payload
type: observability-model
name: Test Catalog Observability Model
version: 0.1.0
status: modeled
---

# Test Catalog Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-SVC-002
  type: observability-model
  name: Test Catalog Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-002
  depends_on_capability: BCM-PLT-006
logs:
- event: test_created
  level: info
  fields:
  - testDefinitionId
  - tenantId
  - actorId
- event: test_published
  level: info
  fields:
  - testDefinitionId
  - version
  - actorId
- event: test_publish_rejected
  level: warn
  fields:
  - testDefinitionId
  - reasonCode
metrics:
- name: catalog_tests_total
  type: gauge
  labels:
  - tenantId
  - status
- name: catalog_test_publish_failures_total
  type: counter
  labels:
  - tenantId
  - reasonCode
- name: catalog_test_write_latency_ms
  type: histogram
traces:
- span: CreateTestDefinition
- span: PublishTestDefinition
  child_spans:
  - ValidateLinkedComponents
  - FreezeTestSnapshot
audit_events:
- TestDefinitionCreated
- TestDefinitionPublished
- TestDefinitionDeprecated
alerts:
- name: HighTestPublishFailureRate
  condition: catalog_test_publish_failures_total rate exceeds threshold
  severity: warning
```
