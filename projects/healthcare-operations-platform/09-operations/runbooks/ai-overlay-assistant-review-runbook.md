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
- For OCR Document Intake (purpose ocr_document_intake), Result and Case Summaries (result_case_summary),
  Semantic Search (semantic_search) and Retrieval Knowledge Grounding (retrieval_grounding), confirm the
  request is rejected with AI_SOURCE_CONTEXT_NOT_ALLOWED when sourceContextType falls outside that
  capability's declared scope, and the draft is rejected with AI_CITATIONS_REQUIRED if it carries no
  citations.
- Record reviewer decision and reason through /api/ai/assistant/sessions/{sessionId}/review.
- Use /api/ai/assistant/sessions/audit-records for tenant audit trace review.
controls:
- AI outputs are advisory only until reviewed.
- Model provider is replaceable through AiDraftGeneratorPort.
- Local deterministic adapter requires no API key or token-billed service.
- Every persisted interaction includes policy version, source context, citations, confidence band and audit metadata.
- OCR Document Intake, Result and Case Summaries, Semantic Search and Retrieval Knowledge Grounding
  (BCM-AI-002..005) each carry their own AiOverlayCapabilityRuleEngine guardrails on top of the controls
  above - allowed source context types and mandatory source citations.
known_limitations:
- Dedicated per-capability REST paths (e.g. /api/ai/ocr/jobs, /api/ai/summaries) modeled by each
  capability's traceability.md are not compiled; the generic /api/ai/assistant/sessions endpoint above,
  scoped by the purpose field, serves the same functional need (TD-BE-021, open, non-blocking).
- Generic workflow-engine automation remains open in TD-BE-017 (materially reduced).
```
