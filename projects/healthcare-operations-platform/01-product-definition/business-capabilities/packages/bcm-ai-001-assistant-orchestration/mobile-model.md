---
id: HOP-MOB-BCM-AI-001
format: markdown_structured_payload
type: mobile-model
name: Assistant Orchestration Mobile Model
version: 1.0.0
status: modeled
---

# Assistant Orchestration Mobile Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-MOB-BCM-AI-001
  type: mobile-model
  status: modeled
capability_id: BCM-AI-001
mobile_scope: optional_read_only_assistant_context
offline_behavior:
  ai_generation_offline: prohibited
  cached_ai_output_display: allowed_only_when_already_reviewed
privacy_controls:
  local_prompt_storage: prohibited
  audit_reference_display_only: true
```
