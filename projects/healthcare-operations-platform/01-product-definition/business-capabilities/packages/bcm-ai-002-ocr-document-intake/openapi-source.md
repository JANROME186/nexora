---
id: HOP-API-BCM-AI-002
format: markdown_structured_payload
type: openapi-source
name: OCR Document Intake Openapi Source
version: 1.0.0
status: modeled
---

# OCR Document Intake OpenAPI Source

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-API-BCM-AI-002
  type: openapi-source
  status: modeled
capability_id: BCM-AI-002
base_path: /api/ai
operations:
  - path: /api/ai/ocr/jobs
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/ocr/jobs/{jobId}
    method: post_or_get_by_operation
    auth: required
    generated: true
  - path: /api/ai/ocr/jobs/{jobId}/corrections
    method: post_or_get_by_operation
    auth: required
    generated: true
contract_policy:
  provider_specific_schema: prohibited
  ai_output_label_required: true
  citation_array_required_for_generated_text: true
```
