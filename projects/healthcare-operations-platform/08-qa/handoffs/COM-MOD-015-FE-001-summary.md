---
artifact:
  id: COM-MOD-015-FE-001-HANDOFF
  type: backlog-handoff
  status: completed
  backlog_item: COM-MOD-015-FE-001
---

# COM-MOD-015-FE-001 Handoff

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
backlog_item: COM-MOD-015-FE-001
status: completed
module: COM-MOD-015 AI Overlay
completed_scope:
- Added the employee-portal AI assistant review screen wired to the existing generic
  /api/ai/assistant/sessions backend surface.
- Added provider-neutral aiOverlayApi functions for requesting drafts, loading sessions,
  submitting human review decisions and loading tenant audit records.
- "Exposed the four compiled backend capability purposes in the UI: ocr_document_intake,
  result_case_summary, semantic_search and retrieval_grounding."
- Preserved the BE-002 decision not to compile dedicated per-capability REST paths;
  source context type selection feeds the generic assistant endpoint and backend
  AiOverlayCapabilityRuleEngine.
- Added citation visibility, AI-generated labeling, model/policy metadata, audit
  table selection and review blocking when citations are absent.
- Added SCREEN_AI_ASSISTANT navigation and permission mapping; RESULTS_COORDINATOR
  receives the screen and ADMIN continues to derive all permissions automatically.
- Added X-Tenant-Id and X-User-Id aliases to the shared frontend session headers so
  AI/imaging controllers that read standard headers directly receive tenant/actor context.
- Added es-MX/en-US aiOverlay catalog entries and AppShell tab labels.
debt_action:
  materially_reduced:
  - TD-UX-001
  notes: Synced stale shared-component-library debt because DataTable, StatusBanner,
    ScopeIndicator and ConfirmDialog already exist; the new AI screen adopts the
    shared DataTable/StatusBanner/ScopeIndicator pattern.
validation_refs:
  qa_evidence: 08-qa/qa/ai-overlay/COM-MOD-015-FE-001-validation.md
  security_quality_evidence: 08-qa/security-quality/COM-MOD-015-FE-001/security-quality-evidence.md
validation_summary:
  typecheck: passed
  focused_tests: 18 passed
  full_coverage: 256 tests passed across 69 files; line coverage 91.00 percent
  build: passed
  quality: passed
  trivy: 0 vulnerabilities/secrets/misconfigurations
  npm_audit_offline: 0 vulnerabilities
  npm_audit_online: registry endpoint unavailable in restricted environment
next_backlog_item:
  id: COM-MOD-015-QA-001
  name: Safety, explainability and human-control evidence
handoff_notes:
- Do not add /api/ai/ocr, /api/ai/summaries, /api/ai/search or /api/ai/retrieval
  endpoints unless a future caller needs a distinct contract; TD-BE-021 remains
  the explicit non-blocking tracker.
- "Preserve the UI's human-review gate: drafts with no citations must not enable
  accepted/rejected review submission."
- Online npm audit was blocked by registry endpoint access; offline npm audit and
  Trivy both reported 0 vulnerabilities from the local cache/database.
```
