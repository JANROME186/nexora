---
id: HOP-TRC-BCM-AI-005
format: markdown_structured_payload
type: traceability
name: Retrieval Knowledge Grounding Traceability Matrix
version: 1.0.0
status: modeled
---

# Retrieval Knowledge Grounding Traceability Matrix

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TRC-BCM-AI-005
  type: traceability
  status: modeled
capability_id: BCM-AI-005
roadmap_group: COM-MOD-015
mappings:
  - requirement: AI Overlay
    rules:
    - AI-RAG-001
    - AI-RAG-002
    - AI-RAG-003
    processes:
    - PROC-AI-009
    - PROC-AI-010
    events:
    - GroundingPackageCreatedEvent
    - GroundingSourceLinkedEvent
    - GroundingPackageRetiredEvent
    api_endpoints:
    - /api/ai/grounding/packages
    - /api/ai/grounding/packages/{packageId}/sources
    - /api/ai/grounding/packages/{packageId}/retire
    permissions:
    - ai.grounding:manage
    - ai.grounding:use
    - ai.grounding:audit
definition:
  backlog_item: COM-MOD-015-DEF
  status: modeled
  qa_evidence: ../../../../08-qa/qa/ai-overlay/COM-MOD-015-DEF-validation.md
  security_quality_evidence: ../../../../08-qa/security-quality/COM-MOD-015-DEF/security-quality-evidence.md
backlog_items:
  definition: COM-MOD-015-DEF
  definition_status: closed
  compilation: COM-MOD-015-BE-001
  compilation_status: closed
  custom_rules: COM-MOD-015-BE-002
  custom_rules_status: closed
  ui: COM-MOD-015-FE-001
  ui_status: planned
  validation: COM-MOD-015-QA-001
  validation_status: closed
```
