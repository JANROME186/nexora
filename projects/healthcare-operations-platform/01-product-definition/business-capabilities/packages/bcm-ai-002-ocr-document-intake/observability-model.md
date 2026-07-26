---
id: HOP-OBS-BCM-AI-002
format: markdown_structured_payload
type: observability-model
name: OCR Document Intake Observability Model
version: 1.0.0
status: modeled
---

# OCR Document Intake Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-AI-002
  type: observability-model
  status: modeled
capability_id: BCM-AI-002
metrics:
  - ai_request_count
  - ai_policy_block_count
  - ai_human_review_latency_seconds
  - ai_provider_error_count
logs:
  prompt_text_logging: prohibited
  correlation_id_required: true
traces:
  provider_adapter_span: required
  safety_policy_span: required
```
