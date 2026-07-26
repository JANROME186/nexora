---
id: HOP-EVT-BCM-AI-008
format: markdown_structured_payload
type: event-model
name: AI Audit and Evaluation Events
version: 1.0.0
status: modeled
---

# AI Audit and Evaluation Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-AI-008
  type: event-model
  status: modeled
capability_id: BCM-AI-008
events:
  - name: AiEvaluationRunStartedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: AiEvaluationFindingRecordedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
  - name: AiAuditEvidenceExportedEvent
    publication: internal_domain_event
    payload_policy: no_raw_secret_or_unredacted_prompt_token
event_controls:
  audit_trail_required: true
  replay_contains_no_provider_secret: true
```
