---
id: HOP-API-BCM-AI-005
format: markdown_structured_payload
type: openapi-source
name: Retrieval Knowledge Grounding Openapi Source
version: 1.0.0
status: modeled
---

# Retrieval Knowledge Grounding OpenAPI Source

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-BCM-AI-005
  type: openapi-source
  status: modeled
capability_id: BCM-AI-005
base_path: /api/ai
operations:
  - path: /api/ai/grounding/packages
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/grounding/packages/{packageId}/sources
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/grounding/packages/{packageId}/retire
    method: post_or_get_by_operation
    auth: required
    generated: true
contract_policy:
  provider_specific_schema: prohibited
  ai_output_label_required: true
  citation_array_required_for_generated_text: true
```
