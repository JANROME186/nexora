---
id: HOP-TRC-BCM-AI-001
format: markdown_structured_payload
type: traceability
name: Assistant Orchestration Traceability Matrix
version: 1.0.0
status: module_closed
---

# Assistant Orchestration Traceability Matrix

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRC-BCM-AI-001
  type: traceability
  status: module_closed
capability_id: BCM-AI-001
roadmap_group: COM-MOD-015
mappings:
  - requirement: AI Overlay
    rules:
    - AI-ORCH-001
    - AI-ORCH-002
    - AI-ORCH-003
    processes:
    - PROC-AI-001
    - PROC-AI-002
    events:
    - AssistantSessionStartedEvent
    - AssistantSuggestionPreparedEvent
    - AssistantSessionClosedEvent
    api_endpoints:
    - /api/ai/assistant/sessions
    - /api/ai/assistant/sessions/{sessionId}/messages
    - /api/ai/assistant/sessions/{sessionId}/close
    permissions:
    - ai.assistant:use
    - ai.assistant:review
    - ai.assistant:admin
definition:
  backlog_item: COM-MOD-015-DEF
  status: modeled
  qa_evidence: ../../../../08-qa/qa/ai-overlay/COM-MOD-015-DEF-validation.md
  security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-015-DEF/security-quality-evidence.md
closeout:
  backlog_item: COM-MOD-015-CLOSEOUT
  status: closed
  qa_evidence: ../../../../08-qa/qa/ai-overlay/COM-MOD-015-CLOSEOUT-validation.md
  security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-015-CLOSEOUT/security-quality-evidence.md
  notes: Formally closed COM-MOD-015 AI Overlay. Marked BCM-AI-001 module_closed in capability-package.md and capability-package-index.md.
backlog_items:
  definition: COM-MOD-015-DEF
  definition_status: closed
  compilation: COM-MOD-015-BE-001
  compilation_status: closed
  custom_rules: COM-MOD-015-BE-002
  custom_rules_status: closed
  ui: COM-MOD-015-FE-001
  ui_status: closed
  validation: COM-MOD-015-QA-001
  validation_status: closed
  closeout: COM-MOD-015-CLOSEOUT
  closeout_status: closed
```
```
