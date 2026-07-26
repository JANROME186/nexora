---
id: HOP-BM-BCM-AI-004
format: markdown_structured_payload
type: business-model
name: Semantic Search Business Model
version: 1.0.0
status: modeled
---

# Semantic Search Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-AI-004
  type: business-model
  status: modeled
capability_id: BCM-AI-004
roadmap_group: COM-MOD-015
bounded_context: ai-search
primary_aggregate:
  name: SemanticSearchQuery (AGG-042)
  ownership: owned_by_capability
business_objects:
  - id: BCM_AI_004_REQUEST
    description: User-scoped request with tenant, actor, purpose, source context and requested AI action.
  - id: BCM_AI_004_OUTPUT
    description: Draft AI output with citations, confidence, safety decision and review status.
  - id: BCM_AI_004_AUDIT_RECORD
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
