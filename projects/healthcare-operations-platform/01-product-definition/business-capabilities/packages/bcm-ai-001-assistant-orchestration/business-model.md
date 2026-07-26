---
id: HOP-BM-BCM-AI-001
format: markdown_structured_payload
type: business-model
name: Assistant Orchestration Business Model
version: 1.0.0
status: modeled
---

# Assistant Orchestration Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-AI-001
  type: business-model
  status: modeled
capability_id: BCM-AI-001
roadmap_group: COM-MOD-015
bounded_context: ai-overlay
primary_aggregate:
  name: AssistantSession (AGG-039)
  ownership: owned_by_capability
business_objects:
  - id: BCM_AI_001_REQUEST
    description: User-scoped request with tenant, actor, purpose, source context and requested AI action.
  - id: BCM_AI_001_OUTPUT
    description: Draft AI output with citations, confidence, safety decision and review status.
  - id: BCM_AI_001_AUDIT_RECORD
    description: Immutable evidence linking inputs, model metadata, policy decisions and human disposition.
lifecycle:
  states:
  - draft_requested
  - policy_checked
  - generated
  - human_review_required
  - accepted
  - rejected
  - archived
decision_boundaries:
  human_accountable: true
  autonomous_clinical_validation_allowed: false
  provider_specific_runtime_required: false
```
