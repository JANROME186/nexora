---
id: HOP-BM-BCM-SVC-004
format: markdown_structured_payload
type: business-model
name: Analyte Catalog Business Model
version: 0.1.0
status: modeled
---

# Analyte Catalog Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-SVC-004
  type: business-model
  name: Analyte Catalog Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-004
  bounded_context: catalog-test-configuration
  primary_aggregate: TestDefinition
entities:
- id: ENT-ANL-001
  name: AnalyteDefinition
  is_aggregate_root: false
  owned_by_aggregate: TestDefinition
  description: An atomic measurable component of a diagnostic test.
  fields:
  - name: analyteId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: laboratoryId
    type: LaboratoryId
    required: true
  - name: code
    type: string
    required: true
    unique_scope:
    - tenantId
    - laboratoryId
  - name: name
    type: LocalizedText
    required: true
  - name: loincCode
    type: string
    required: false
  - name: resultDataType
    type: enum
    values:
    - numeric
    - qualitative
    - semi_quantitative
    - text
    - coded
    required: true
  - name: measurementUnit
    type: string
    required: false
  - name: decimalPrecision
    type: integer
    required: false
  - name: status
    type: enum
    values:
    - draft
    - published
    - deprecated
    - retired
    required: true
  - name: version
    type: integer
    required: true
  - name: audit
    type: AuditMetadata
    required: true
- id: ENT-ANL-002
  name: AnalyteResultConstraint
  is_aggregate_root: false
  owned_by_aggregate: TestDefinition
  description: Value constraints applied to captured analyte results.
  fields:
  - name: constraintId
    type: uuid
    required: true
    identifier: true
  - name: analyteId
    type: uuid
    required: true
  - name: minValue
    type: decimal
    required: false
  - name: maxValue
    type: decimal
    required: false
  - name: allowedCodedValues
    type: list
    required: false
- id: ENT-ANL-003
  name: AnalyteCodedValue
  is_aggregate_root: false
  owned_by_aggregate: TestDefinition
  description: An enumerated coded result value for qualitative or coded analytes.
  fields:
  - name: codedValueId
    type: uuid
    required: true
    identifier: true
  - name: analyteId
    type: uuid
    required: true
  - name: code
    type: string
    required: true
  - name: display
    type: LocalizedText
    required: true
value_objects:
- name: LocalizedText
  fields:
  - name: en
    type: string
    required: true
  - name: es
    type: string
    required: true
relationships:
- from: AnalyteDefinition
  to: AnalyteResultConstraint
  type: one_to_one
- from: AnalyteDefinition
  to: AnalyteCodedValue
  type: one_to_many
- from: AnalyteDefinition
  to: LaboratoryResult
  type: reference_by_id
  note: Result values in laboratory-results (AGG-009) reference published analyte
    definitions.
invariants:
- id: INV-SVC-004-01
  statement: A numeric analyte must declare a measurement unit and decimal precision.
- id: INV-SVC-004-02
  statement: A coded analyte must declare at least one coded value.
- id: INV-SVC-004-03
  statement: Analyte code must be unique within a laboratory.
external_references:
- identifier: TenantId
  source: shared-kernel
- identifier: LaboratoryId
  source: shared-kernel
- aggregate: TestDefinition
  context: catalog-test-configuration
  access: owning_context
- aggregate: LaboratoryResult
  context: laboratory-results
  access: published_language_only
```
