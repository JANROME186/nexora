---
id: HOP-API-BCM-AI-001
format: markdown_structured_payload
type: openapi-source
name: Assistant Orchestration Openapi Source
version: 1.0.0
status: modeled
---

# Assistant Orchestration OpenAPI Source

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-BCM-AI-001
  type: openapi-source
  status: modeled
capability_id: BCM-AI-001
base_path: /api/ai
operations:
  - path: /api/ai/assistant/sessions
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/assistant/sessions/{sessionId}/messages
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/assistant/sessions/{sessionId}/close
    method: post_or_get_by_operation
    auth: required
    generated: true
contract_policy:
  provider_specific_schema: prohibited
  ai_output_label_required: true
  citation_array_required_for_generated_text: true
```
