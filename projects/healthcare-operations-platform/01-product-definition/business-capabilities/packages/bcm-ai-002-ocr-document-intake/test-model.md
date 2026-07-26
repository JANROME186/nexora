---
id: HOP-TEST-BCM-AI-002
format: markdown_structured_payload
type: test-model
name: OCR Document Intake Test Model
version: 1.0.0
status: modeled
---

# OCR Document Intake Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-AI-002
  type: test-model
  status: modeled
capability_id: BCM-AI-002
tests:
  - id: TEST-AI-002-001
    type: model_traceability
    expectation: OCR Document Intake preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-002-002
    type: model_traceability
    expectation: OCR Document Intake preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-002-003
    type: model_traceability
    expectation: OCR Document Intake preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-002-004
    type: model_traceability
    expectation: OCR Document Intake preserves advisory output, human control, audit evidence and provider neutrality.
qa_focus:
  safety: required
  explainability: required
  human_control: required
  vendor_lock_in_scan: required
```
