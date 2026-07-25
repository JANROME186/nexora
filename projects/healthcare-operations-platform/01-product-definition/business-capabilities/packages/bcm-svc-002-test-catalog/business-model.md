---
id: HOP-BM-BCM-SVC-002
format: markdown_structured_payload
type: business-model
name: Test Catalog Business Model
version: 0.1.0
status: modeled
---

# Test Catalog Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-SVC-002
  type: business-model
  name: Test Catalog Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-002
  bounded_context: catalog-test-configuration
  primary_aggregate: TestDefinition
entities:
- id: ENT-TST-001
  name: TestDefinition
  is_aggregate_root: true
  aggregate: TestDefinition
  description: The atomic orderable clinical test definition.
  fields:
  - name: testDefinitionId
    type: TestDefinitionId
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
  - name: methodology
    type: string
    required: false
  - name: measurementUnit
    type: string
    required: false
  - name: resultType
    type: enum
    values:
    - numeric
    - qualitative
    - semi_quantitative
    - text
    - structured
    required: true
  - name: turnaroundTimeHours
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
- id: ENT-TST-002
  name: TestAnalyteLink
  is_aggregate_root: false
  owned_by_aggregate: TestDefinition
  description: References analyte definitions that compose the test.
  fields:
  - name: linkId
    type: uuid
    required: true
    identifier: true
  - name: analyteRefId
    type: uuid
    required: true
  - name: displayOrder
    type: integer
    required: false
- id: ENT-TST-003
  name: TestSampleRequirementLink
  is_aggregate_root: false
  owned_by_aggregate: TestDefinition
  description: References sample requirements needed to run the test.
  fields:
  - name: linkId
    type: uuid
    required: true
    identifier: true
  - name: sampleRequirementRefId
    type: uuid
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
- from: TestDefinition
  to: TestAnalyteLink
  type: one_to_many
- from: TestDefinition
  to: TestSampleRequirementLink
  type: one_to_many
- from: TestAnalyteLink
  to: AnalyteDefinition
  type: reference_by_id
  note: Analyte master owned by BCM-SVC-004.
invariants:
- id: INV-SVC-002-01
  statement: A numeric test must declare a measurement unit.
- id: INV-SVC-002-02
  statement: A published test must reference at least one sample requirement.
- id: INV-SVC-002-03
  statement: Test code must be unique within a laboratory.
external_references:
- identifier: TenantId
  source: shared-kernel
- identifier: LaboratoryId
  source: shared-kernel
- aggregate: TestDefinition
  context: catalog-test-configuration
  access: owning_context
```
