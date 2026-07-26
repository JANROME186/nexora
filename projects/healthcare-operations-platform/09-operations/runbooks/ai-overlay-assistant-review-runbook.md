---
artifact:
  id: HOP-RUNBOOK-AI-OVERLAY-ASSISTANT-REVIEW
  type: operational-runbook
  status: active
---

# AI Overlay Assistant Review Runbook

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
runbook: ai-overlay-assistant-review
module: COM-MOD-015 AI Overlay
backend_surface: /api/ai/assistant/sessions
policy_version: AI-SAFE-001
operator_flow:
- Create a draft with tenant, actor, purpose, source context and prompt.
- Confirm response has safetyDecision allowed_with_human_review and reviewStatus human_review_required.
- Reject any workflow that attempts autonomous diagnosis, autonomous validation or skipping human review.
- Record reviewer decision and reason through /api/ai/assistant/sessions/{sessionId}/review.
- Use /api/ai/assistant/sessions/audit-records for tenant audit trace review.
controls:
- AI outputs are advisory only until reviewed.
- Model provider is replaceable through AiDraftGeneratorPort.
- Local deterministic adapter requires no API key or token-billed service.
- Every persisted interaction includes policy version, source context, citations, confidence band and audit metadata.
known_limitations:
- OCR, summary, semantic search and retrieval custom rules are scheduled for COM-MOD-015-BE-002.
- Generic workflow-engine automation remains open in TD-BE-017.
```
