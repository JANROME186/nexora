---
id: HOP-API-BCM-AI-003
format: markdown_structured_payload
type: openapi-source
name: Result and Case Summaries Openapi Source
version: 1.0.0
status: modeled
---

# Result and Case Summaries OpenAPI Source

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-BCM-AI-003
  type: openapi-source
  status: modeled
capability_id: BCM-AI-003
base_path: /api/ai
operations:
  - path: /api/ai/summaries
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/summaries/{summaryId}
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/summaries/{summaryId}/review
    method: post_or_get_by_operation
    auth: required
    generated: true
contract_policy:
  provider_specific_schema: prohibited
  ai_output_label_required: true
  citation_array_required_for_generated_text: true
```
