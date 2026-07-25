---
id: HOP-EVT-BCM-SVC-007
format: markdown_structured_payload
type: events
name: Sample Catalog Events
version: 0.1.0
status: modeled
---

# Sample Catalog Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-SVC-007
  type: events
  name: Sample Catalog Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-007
  aggregate: TestDefinition
domain_events:
- name: SampleTypeCreated
  description: A sample type draft was created.
  payload:
  - sampleTypeId
  - tenantId
  - laboratoryId
  - code
  - matrix
  - version
  audit: true
- name: SampleRequirementCreated
  description: A sample requirement draft was created.
  payload:
  - requirementId
  - sampleTypeRefId
  - version
  audit: true
- name: SampleRequirementPublished
  description: A sample requirement version was published and frozen.
  payload:
  - requirementId
  - sampleTypeRefId
  - version
  - publishedAt
  - publishedBy
  audit: true
- name: SampleRequirementDeprecated
  description: A sample requirement was deprecated.
  payload:
  - requirementId
  - version
  - deprecatedAt
  audit: true
integration_events:
  published:
  - name: SampleRequirementPublished
    description: Published language consumed by collection, labeling and reception.
    consumers:
    - orders-samples
    - BCM-LAB-002
    - BCM-LAB-003
    - BCM-LAB-005
    - BCM-SVC-002
  consumed:
  - name: TestDefinitionPublished
    source: BCM-SVC-002
    description: Correlates requirements to published tests.
published_language:
- SampleRequirement
```
