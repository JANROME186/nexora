---
id: HOP-BM-BCM-QLT-001
format: markdown_structured_payload
type: business-model
name: Internal Quality Controls Business Model
version: 0.1.0
status: modeled
---

# Internal Quality Controls Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-QLT-001
  type: business-model
  name: Internal Quality Controls Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-001
  bounded_context: inventory-procurement
  primary_aggregate: QualityControlRun
  model_kind: aggregate_owner
entities:
- id: ENT-IQC-001
  name: QualityControlRun
  is_aggregate_root: true
  description: 'New entity owned by this capability. Not a duplicate of AGG-013 InventoryItem
    or AGG-009 LaboratoryResult; both are referenced by id only and never mutated.

    '
  fields:
  - name: qcRunId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: laboratoryId
    type: LaboratoryId
    required: true
  - name: branchId
    type: BranchId
    required: true
  - name: testDefinitionId
    type: uuid
    required: true
    description: Read-only reference to a published TestDefinition (BCM-SVC-002/BCM-SVC-004).
  - name: controlMaterialStockLotId
    type: uuid
    required: true
    description: Read-only reference to the control-material StockLot (BCM-INV-003).
  - name: measuredValue
    type: decimal
    required: true
  - name: expectedRange
    type: ExpectedRange
    required: true
  - name: ruleEvaluation
    type: enum
    values:
    - in_control
    - warning
    - out_of_control
    required: true
  - name: acceptanceDecision
    type: enum
    values:
    - accepted
    - rejected
    - repeat_required
    required: true
  - name: linkedLaboratoryResultIds
    type: list[uuid]
    required: false
    description: Read-only references to the batch of patient results run alongside
      this QC; never mutated.
  - name: performedBy
    type: UserId
    required: true
  - name: performedAt
    type: datetime
    required: true
  - name: evidenceReference
    type: string
    required: false
    description: Optional reference to a stored document (BCM-PLT-008 Document Management,
      not part of COM-MOD-010).
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-IQC-001
  name: ExpectedRange
  description: Acceptable value range for the control material, captured from the
    manufacturer insert at run time.
  fields:
  - name: minValue
    type: decimal
    required: true
  - name: maxValue
    type: decimal
    required: true
  - name: capturedAt
    type: datetime
    required: true
invariants:
- id: INV-IQC-001
  statement: This capability never mutates InventoryItem, StockLot or AGG-009 LaboratoryResult;
    all such references are read-only.
- id: INV-IQC-002
  statement: measuredValue and expectedRange must be present before ruleEvaluation
    can be computed.
- id: INV-IQC-003
  statement: An out_of_control ruleEvaluation must never resolve to acceptanceDecision
    accepted without an explicit, audited override.
- id: INV-IQC-004
  statement: controlMaterialStockLotId must reference a StockLot whose classification
    is calibrator_control_material and whose status is active at run time.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-003 BranchId
  - VO-ID-004 UserId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-INV-001 / BCM-INV-003 InventoryItem and StockLot (read-only control-material
    lot reference)
  - BCM-INV-007 Consumption Tracking (source of the consumed control-material lot
    event)
  - BCM-SVC-002 Test Catalog / BCM-SVC-004 Analyte Catalog (read-only TestDefinition
    reference)
  - BCM-LAB-008 Technical Validation (downstream consumer of out_of_control status;
    enforcement of any release block remains BCM-LAB-008's own responsibility, outside
    COM-MOD-010)
- aggregate_catalog:
  - AGG-009 LaboratoryResult (read-only reference only; this capability is not among
    laboratory-results' bounded-context mutators)
```
