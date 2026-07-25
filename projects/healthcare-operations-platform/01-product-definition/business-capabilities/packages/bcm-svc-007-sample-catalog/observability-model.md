---
id: HOP-OBS-BCM-SVC-007
format: markdown_structured_payload
type: observability-model
name: Sample Catalog Observability Model
version: 0.1.0
status: modeled
---

# Sample Catalog Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-SVC-007
  type: observability-model
  name: Sample Catalog Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-007
  depends_on_capability: BCM-PLT-006
logs:
- event: sample_type_created
  level: info
  fields:
  - sampleTypeId
  - tenantId
  - actorId
- event: sample_requirement_published
  level: info
  fields:
  - requirementId
  - version
  - actorId
- event: sample_requirement_publish_rejected
  level: warn
  fields:
  - requirementId
  - reasonCode
metrics:
- name: catalog_sample_types_total
  type: gauge
  labels:
  - tenantId
  - status
  - matrix
- name: catalog_sample_requirements_total
  type: gauge
  labels:
  - tenantId
  - status
- name: catalog_sample_publish_failures_total
  type: counter
  labels:
  - tenantId
  - reasonCode
traces:
- span: CreateSampleRequirement
- span: PublishSampleRequirement
  child_spans:
  - ValidateSampleTypePublication
  - ValidateHandling
  - FreezeRequirementSnapshot
audit_events:
- SampleTypeCreated
- SampleRequirementCreated
- SampleRequirementPublished
- SampleRequirementDeprecated
alerts:
- name: HighSampleRequirementPublishFailureRate
  condition: catalog_sample_publish_failures_total rate exceeds threshold
  severity: warning
```
