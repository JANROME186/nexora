---
id: HOP-TEST-BCM-AI-004
format: markdown_structured_payload
type: test-model
name: Semantic Search Test Model
version: 1.0.0
status: modeled
---

# Semantic Search Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-AI-004
  type: test-model
  status: modeled
capability_id: BCM-AI-004
tests:
  - id: TEST-AI-004-001
    type: model_traceability
    expectation: Semantic Search preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-004-002
    type: model_traceability
    expectation: Semantic Search preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-004-003
    type: model_traceability
    expectation: Semantic Search preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-004-004
    type: model_traceability
    expectation: Semantic Search preserves advisory output, human control, audit evidence and provider neutrality.
qa_focus:
  safety: required
  explainability: required
  human_control: required
  vendor_lock_in_scan: required
```
