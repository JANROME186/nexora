---
id: HOP-OBS-BCM-RES-002
format: markdown_structured_payload
type: observability-model
name: PDF Report Generation Observability Model
version: 0.1.0
status: modeled
---

# Pdf Report Generation Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-RES-002
  type: observability-model
  name: PDF Report Generation Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-002
  depends_on_capability: BCM-PLT-006
logs:
- event: report_generated
  level: info
  fields:
  - reportId
  - resultId
  - reportVersion
  - generationTrigger
  correlation_id: resultId
- event: report_generation_failed
  level: error
  fields:
  - resultId
  - reasonCode
  correlation_id: resultId
- event: report_integrity_hash_mismatch
  level: critical
  fields:
  - reportId
  - resultId
  correlation_id: resultId
metrics:
- name: report_generated_total
  type: counter
  labels:
  - tenantId
  - laboratoryId
  - generationTrigger
- name: report_generation_failed_total
  type: counter
  labels:
  - tenantId
  - laboratoryId
  - reasonCode
- name: report_generation_duration_ms
  type: histogram
  labels:
  - tenantId
  - laboratoryId
- name: report_integrity_mismatch_total
  type: counter
  labels:
  - tenantId
  - laboratoryId
traces:
- span: GenerateResultReport
  child_spans:
  - RenderPdf
  - ComputeContentHash
  - StoreDocumentViaPlt008
- span: GetResultReport
  child_spans:
  - VerifyContentHash
audit_events:
- ReportGenerated
- ReportSuperseded
alerts:
- name: ReportGenerationFailureRateHigh
  condition: report_generation_failed_total rate exceeds threshold
  severity: critical
- name: ReportIntegrityMismatchDetected
  condition: report_integrity_mismatch_total rate exceeds zero
  severity: critical
```
