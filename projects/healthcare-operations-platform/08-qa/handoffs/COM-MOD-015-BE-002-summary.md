---
artifact:
  id: COM-MOD-015-BE-002-HANDOFF
  type: backlog-handoff
  status: completed
  backlog_item: COM-MOD-015-BE-002
---

# COM-MOD-015-BE-002 Handoff

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
backlog_item: COM-MOD-015-BE-002
status: completed
module: COM-MOD-015 AI Overlay
completed_scope:
- Added AiOverlayCapability (BCM-AI-002 OCR Document Intake, BCM-AI-003 Result and Case Summaries,
  BCM-AI-004 Semantic Search, BCM-AI-005 Retrieval Knowledge Grounding) resolving each capability
  from the existing assistant request's purpose field.
- Added AiOverlayCapabilityRuleEngine enforcing each capability's business-rules.md guardrails as
  concrete checks - source context type must belong to the capability's declared, tenant-scoped
  scope; generated output must carry at least one source citation; output must remain
  human-review-required.
- Wired the rule engine into AiAssistantService.requestAssistantDraft without duplicating a second
  REST surface next to the existing /api/ai/assistant/sessions endpoint.
- Added AI_SOURCE_CONTEXT_NOT_ALLOWED and AI_CITATIONS_REQUIRED error codes with en_US/es_MX/default
  i18n catalog entries.
- 12 new tests (unit for the capability enum and rule engine, plus 3 API-level tests); 517 total
  backend tests, 0 failures/errors.
debt_action:
  reduced:
  - TD-BE-017 (status field synced to materially_reduced; was left at open by COM-MOD-015-BE-001
    despite the item's own remediation.strategy/progress note already recording the reduction)
  added:
  - TD-BE-021 (dedicated per-capability REST paths modeled by traceability.md not compiled;
    deliberate scope decision, non-blocking, low risk)
  notes: technical-debt-index.md's backend_java_maven coverage baseline (84.65%) had never been
    synced since COM-MOD-017-CLOSEOUT, through the entire COM-MOD-014 Imaging Operations backend
    expansion and COM-MOD-015-DEF/BE-001; this item synced it to the current, twice-reproduced
    clean-rebuild figure of 70.14%.
validation_refs:
  qa_evidence: 08-qa/qa/ai-overlay/COM-MOD-015-BE-002-validation.md
  security_quality_evidence: 08-qa/security-quality/COM-MOD-015-BE-002/security-quality-evidence.md
next_backlog_item:
  id: COM-MOD-015-FE-001
  name: Compile assistant and review UI outputs
handoff_notes:
- Do not compile a second, per-capability REST surface (e.g. /api/ai/ocr/jobs) without a real
  caller needing the distinct shape; the generic assistant endpoint plus AiOverlayCapabilityRuleEngine
  already serve BCM-AI-002..005's functional need (see TD-BE-021).
- backend_java_maven coverage floor is now 70.14%, not the stale 84.65% figure that was still in
  HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md and technical-debt-index.md before this item; do not
  reintroduce the old number without re-deriving it from a clean rebuild.
- Preserve mandatory human review and audit evidence for every AI output; the rule engine's own
  review-status check is a regression safety net on top of AiAssistantService's existing invariant,
  not a replacement for it.
```
