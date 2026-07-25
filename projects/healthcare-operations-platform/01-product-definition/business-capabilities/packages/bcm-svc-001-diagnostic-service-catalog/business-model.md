---
id: HOP-BM-BCM-SVC-001
format: markdown_structured_payload
type: business-model
name: Diagnostic Service Catalog Business Model
version: 0.1.0
status: modeled
---

# Diagnostic Service Catalog Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-SVC-001
  type: business-model
  name: Diagnostic Service Catalog Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-001
  bounded_context: catalog-test-configuration
  primary_aggregate: TestDefinition
entities:
- id: ENT-SVC-001
  name: DiagnosticService
  is_aggregate_root: false
  owned_by_aggregate: TestDefinition
  description: A sellable and orderable catalog entry composed of tests and panels.
  fields:
  - name: serviceId
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
  - name: category
    type: DiagnosticServiceCategory
    required: true
  - name: serviceType
    type: enum
    values:
    - test
    - panel
    - profile
    - mixed
    required: true
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
- id: ENT-SVC-002
  name: DiagnosticServiceCategory
  is_aggregate_root: false
  description: Hierarchical classification for organizing the service catalog.
  fields:
  - name: categoryId
    type: uuid
    required: true
    identifier: true
  - name: name
    type: LocalizedText
    required: true
  - name: parentCategoryId
    type: uuid
    required: false
- id: ENT-SVC-003
  name: ServiceComponentLink
  is_aggregate_root: false
  description: Reference from a service to its composing tests or panels.
  fields:
  - name: linkId
    type: uuid
    required: true
    identifier: true
  - name: serviceId
    type: uuid
    required: true
  - name: componentType
    type: enum
    values:
    - test
    - panel
    required: true
  - name: componentRefId
    type: uuid
    required: true
  - name: displayOrder
    type: integer
    required: false
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
- from: DiagnosticService
  to: DiagnosticServiceCategory
  type: many_to_one
- from: DiagnosticService
  to: ServiceComponentLink
  type: one_to_many
- from: ServiceComponentLink
  to: TestDefinition
  type: reference_by_id
  note: Component references point to test or panel definitions owned by BCM-SVC-002
    and BCM-SVC-003.
invariants:
- id: INV-SVC-001-01
  statement: A published service must reference at least one component.
- id: INV-SVC-001-02
  statement: Service code must be unique within a laboratory.
- id: INV-SVC-001-03
  statement: Only published component references may compose a published service.
external_references:
- aggregate: TestDefinition
  context: catalog-test-configuration
  access: owning_context
- identifier: TenantId
  source: shared-kernel
- identifier: LaboratoryId
  source: shared-kernel
```
