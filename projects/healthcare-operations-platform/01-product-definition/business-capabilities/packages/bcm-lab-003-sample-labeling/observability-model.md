---
id: HOP-OBS-BCM-LAB-003
format: markdown_structured_payload
type: observability-model
name: Sample Labeling Observability Model
version: 0.1.0
status: modeled
---

# Sample Labeling Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-LAB-003
  type: observability-model
  name: Sample Labeling Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-003
  depends_on_capability: BCM-PLT-006
logs:
- event: specimen_label_printed
  level: info
  fields:
  - printJobId
  - sampleId
  - actorId
  - printAttempts
  correlation_id: sampleId
- event: specimen_label_assigned
  level: info
  fields:
  - sampleId
  - printJobId
  - actorId
  correlation_id: sampleId
- event: specimen_label_mismatch_detected
  level: warn
  fields:
  - printJobId
  - sampleId
  - mismatchDetail
  correlation_id: sampleId
metrics:
- name: specimen_label_printed_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: specimen_label_reprint_total
  type: counter
  labels:
  - tenantId
  - branchId
  - reasonCode
- name: specimen_label_mismatch_total
  type: counter
  labels:
  - tenantId
  - branchId
traces:
- span: PrintSpecimenLabel
  child_spans:
  - SelectLabelTemplate
  - GenerateBarcode
- span: ConfirmSpecimenLabel
  child_spans:
  - RunLabelMismatchCheck
  - InvokeAssignSpecimenLabel
audit_events:
- SpecimenLabelPrinted
- SpecimenLabelAssigned
- SpecimenLabelReprinted
alerts:
- name: HighLabelMismatchRate
  condition: specimen_label_mismatch_total rate exceeds threshold
  severity: warning
```
