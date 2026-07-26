---
id: HOP-TRC-BCM-AI-003
format: markdown_structured_payload
type: traceability
name: Result and Case Summaries Traceability Matrix
version: 1.0.0
status: modeled
---

# Result and Case Summaries Traceability Matrix

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRC-BCM-AI-003
  type: traceability
  status: modeled
capability_id: BCM-AI-003
roadmap_group: COM-MOD-015
mappings:
  - requirement: AI Overlay
    rules:
    - AI-SUM-001
    - AI-SUM-002
    - AI-SUM-003
    processes:
    - PROC-AI-005
    - PROC-AI-006
    events:
    - SummaryRequestedEvent
    - SummaryDraftGeneratedEvent
    - SummaryAcceptedWithEditsEvent
    api_endpoints:
    - /api/ai/summaries
    - /api/ai/summaries/{summaryId}
    - /api/ai/summaries/{summaryId}/review
    permissions:
    - ai.summary:request
    - ai.summary:approve
    - ai.summary:audit
definition:
  backlog_item: COM-MOD-015-DEF
  status: modeled
  qa_evidence: ../../../../08-qa/qa/ai-overlay/COM-MOD-015-DEF-validation.md
  security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-015-DEF/security-quality-evidence.md
backlog_items:
  definition: COM-MOD-015-DEF
  definition_status: closed
  compilation: COM-MOD-015-BE-001
  compilation_status: planned
  custom_rules: COM-MOD-015-BE-002
  custom_rules_status: planned
  ui: COM-MOD-015-FE-001
  ui_status: planned
  validation: COM-MOD-015-QA-001
  validation_status: planned
```
