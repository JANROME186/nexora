---
artifact:
  id: HOP-BACKLOG-MODULE-COM-MOD-015
  type: backlog-module-record
  status: active
  optimization: atomic_context
---

# COM-MOD-015 Module Backlog

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: COM-MOD-015
name: AI Overlay
release: REL-004
priority: 140
status: active
source: mvp_framework_future_module
objective: Add assistant, OCR, summary, semantic search and retrieval capabilities with strict clinical guardrails.
depends_on:
- MVP-MOD-008
- COM-MOD-012
- COM-MOD-013
capabilities:
- BCM-AI-001
- BCM-AI-002
- BCM-AI-003
- BCM-AI-004
- BCM-AI-005
- BCM-AI-006
- BCM-AI-007
- BCM-AI-008
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: optional
  doctor_portal: optional
  mobile_app: optional
  model_provider: replaceable
backlog_items:
- id: COM-MOD-015-DEF
  name: Capability package models
  status: closed
- id: COM-MOD-015-BE-001
  name: Compile AI orchestration, policy and audit outputs
  status: next
- id: COM-MOD-015-BE-002
  name: Implement OCR, summary, search and retrieval custom rules
  status: planned
- id: COM-MOD-015-FE-001
  name: Compile assistant and review UI outputs
  status: planned
- id: COM-MOD-015-QA-001
  name: Safety, explainability and human-control evidence
  status: planned
- id: COM-MOD-015-CLOSEOUT
  name: Module closeout and registry update
  status: planned
acceptance_summary:
- AI capabilities assist administrative and clinical workflows without autonomous clinical validation.
- All AI outputs are attributable, reviewable and auditable.
- Model providers and runtimes remain replaceable.
```
