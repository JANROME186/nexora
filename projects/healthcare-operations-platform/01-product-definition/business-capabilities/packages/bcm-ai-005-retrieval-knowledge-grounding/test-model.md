---
id: HOP-TEST-BCM-AI-005
format: markdown_structured_payload
type: test-model
name: Retrieval Knowledge Grounding Test Model
version: 1.0.0
status: modeled
---

# Retrieval Knowledge Grounding Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-AI-005
  type: test-model
  status: modeled
capability_id: BCM-AI-005
tests:
  - id: TEST-AI-005-001
    type: model_traceability
    expectation: Retrieval Knowledge Grounding preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-005-002
    type: model_traceability
    expectation: Retrieval Knowledge Grounding preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-005-003
    type: model_traceability
    expectation: Retrieval Knowledge Grounding preserves advisory output, human control, audit evidence and provider neutrality.
  - id: TEST-AI-005-004
    type: model_traceability
    expectation: Retrieval Knowledge Grounding preserves advisory output, human control, audit evidence and provider neutrality.
qa_focus:
  safety: required
  explainability: required
  human_control: required
  vendor_lock_in_scan: required
```
