---
id: HOP-TEST-BCM-AI-003
format: markdown_structured_payload
type: test-model
name: Result and Case Summaries Test Model
version: 1.0.0
status: modeled
---

# Result and Case Summaries Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-AI-003
  type: test-model
  status: modeled
capability_id: BCM-AI-003
tests:
  - id: TEST-AI-003-001
    type: model_traceability
    expectation: Result and Case Summaries preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-003-002
    type: model_traceability
    expectation: Result and Case Summaries preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-003-003
    type: model_traceability
    expectation: Result and Case Summaries preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-003-004
    type: model_traceability
    expectation: Result and Case Summaries preserves advisory output, human control, audit evidence and provider neutrality.
qa_focus:
  safety: required
  explainability: required
  human_control: required
  vendor_lock_in_scan: required
```
