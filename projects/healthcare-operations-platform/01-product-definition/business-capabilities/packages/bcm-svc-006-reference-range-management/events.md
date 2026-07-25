---
id: HOP-EVT-BCM-SVC-006
format: markdown_structured_payload
type: events
name: Reference Range Management Events
version: 0.1.0
status: modeled
---

# Reference Range Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-SVC-006
  type: events
  name: Reference Range Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-006
  aggregate: TestDefinition
domain_events:
- name: ReferenceRangeCreated
  description: A reference range draft was created.
  payload:
  - rangeId
  - analyteRefId
  - tenantId
  - laboratoryId
  - version
  - effectiveFrom
  audit: true
- name: ReferenceRangePublished
  description: A reference range version was published and frozen.
  payload:
  - rangeId
  - analyteRefId
  - version
  - effectiveFrom
  - publishedBy
  audit: true
- name: ReferenceRangeRevised
  description: A published range was revised into a new effective-dated version.
  payload:
  - rangeId
  - analyteRefId
  - previousVersion
  - newVersion
  - effectiveFrom
  audit: true
- name: ReferenceRangeDeprecated
  description: A reference range was deprecated.
  payload:
  - rangeId
  - version
  - deprecatedAt
  audit: true
integration_events:
  published:
  - name: ReferenceRangeUpdated
    description: Published language consumed by validation and critical result detection.
    consumers:
    - laboratory-results
    - BCM-LAB-008
    - BCM-LAB-009
    - BCM-RES-006
  consumed:
  - name: AnalyteDefinitionPublished
    source: BCM-SVC-004
  - name: AnalyteDefinitionRevised
    source: BCM-SVC-004
    description: Triggers range review when analyte data type changes.
published_language:
- ReferenceRange
```
