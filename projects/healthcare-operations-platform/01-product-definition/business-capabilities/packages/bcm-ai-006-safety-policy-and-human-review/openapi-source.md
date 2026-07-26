---
id: HOP-API-BCM-AI-006
format: markdown_structured_payload
type: openapi-source
name: Safety Policy and Human Review Openapi Source
version: 1.0.0
status: modeled
---

# Safety Policy and Human Review OpenAPI Source

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-BCM-AI-006
  type: openapi-source
  status: modeled
capability_id: BCM-AI-006
base_path: /api/ai
operations:
  - path: /api/ai/safety/policies
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/safety/decisions
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/safety/reviews/{reviewId}
    method: post_or_get_by_operation
    auth: required
    generated: true
contract_policy:
  provider_specific_schema: prohibited
  ai_output_label_required: true
  citation_array_required_for_generated_text: true
```
