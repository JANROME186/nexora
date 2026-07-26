---
id: HOP-API-BCM-AI-008
format: markdown_structured_payload
type: openapi-source
name: AI Audit and Evaluation Openapi Source
version: 1.0.0
status: modeled
---

# AI Audit and Evaluation OpenAPI Source

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-BCM-AI-008
  type: openapi-source
  status: modeled
capability_id: BCM-AI-008
base_path: /api/ai
operations:
  - path: /api/ai/evaluations
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/evaluations/{runId}/findings
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/audit/evidence/export
    method: post_or_get_by_operation
    auth: required
    generated: true
contract_policy:
  provider_specific_schema: prohibited
  ai_output_label_required: true
  citation_array_required_for_generated_text: true
```
