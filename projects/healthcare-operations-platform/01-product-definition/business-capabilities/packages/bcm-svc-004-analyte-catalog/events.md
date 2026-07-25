---
id: HOP-EVT-BCM-SVC-004
format: markdown_structured_payload
type: events
name: Analyte Catalog Events
version: 0.1.0
status: modeled
---

# Analyte Catalog Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-SVC-004
  type: events
  name: Analyte Catalog Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-004
  aggregate: TestDefinition
domain_events:
- name: AnalyteCreated
  description: An analyte draft was created.
  payload:
  - analyteId
  - tenantId
  - laboratoryId
  - code
  - resultDataType
  - version
  audit: true
- name: AnalyteDefinitionPublished
  description: An analyte version was published and frozen.
  payload:
  - analyteId
  - version
  - publishedAt
  - publishedBy
  audit: true
- name: AnalyteDefinitionRevised
  description: A published analyte was revised into a new draft version with dependent
    review flags.
  payload:
  - analyteId
  - previousVersion
  - newVersion
  - dataTypeChanged
  audit: true
- name: AnalyteDeprecated
  description: An analyte was deprecated.
  payload:
  - analyteId
  - version
  - deprecatedAt
  audit: true
integration_events:
  published:
  - name: AnalyteDefinitionPublished
    description: Published language consumed by result capture, ranges and results.
    consumers:
    - laboratory-results
    - BCM-SVC-002
    - BCM-SVC-006
    - BCM-RES-001
  consumed: []
published_language:
- AnalyteDefinition
- AnalyteResultConstraint
```
