---
id: TD-BE-021
format: markdown_structured_payload
type: technical-debt-item
name: BCM-AI-002..005 per-capability REST paths modeled by traceability.md/openapi-source.md
  but not compiled as dedicated endpoints
version: 1.0.0
status: open
---

# Bcm Ai 002..005 Per-Capability Rest Paths Modeled but Not Compiled as Dedicated Endpoints

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-021
  type: technical-debt-item
  name: BCM-AI-002..005 per-capability REST paths modeled by traceability.md/openapi-source.md
    but not compiled as dedicated endpoints
  version: 1.0.0
  status: open
  created_date: 2026-07-26
source:
  discovered_during_backlog_item: COM-MOD-015-BE-002
  module: COM-MOD-015 AI Overlay
  evidence: 08-qa/qa/ai-overlay/COM-MOD-015-BE-002-validation.md
classification:
  category: backend_missing_capability
  affected_area: ai_overlay_per_capability_api_surface
  affected_components: []
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: The four capabilities' functional need (advisory, attributable,
    tenant-scoped, reviewable AI output) is already served end-to-end through the
    generic /api/ai/assistant/sessions endpoint compiled by COM-MOD-015-BE-001, now
    gated per capability by COM-MOD-015-BE-002's custom rule engine (source-context
    scoping and citation requirements keyed off the purpose field). No client or
    runbook depends on the aspirational dedicated paths yet.
current_state:
  issue: bcm-ai-002-ocr-document-intake/bcm-ai-003-result-and-case-summaries/bcm-ai-004-semantic-search/bcm-ai-005-retrieval-knowledge-grounding
    traceability.md files each model dedicated REST paths (e.g. /api/ai/ocr/jobs,
    /api/ai/summaries, /api/ai/search/query, /api/ai/grounding/packages) and dedicated
    permission codes (ai.ocr:*, ai.summary:*, ai.search:*, ai.grounding:*). COM-MOD-015-BE-002
    compiled the custom guardrail rules these packages require but deliberately did
    not compile a second, parallel REST surface duplicating the already-working generic
    assistant endpoint, since doing so before a client (employee-portal AI screens
    or an external caller) actually needs the distinct shape would be exactly the
    "CRUD surface ahead of a real orchestration need" anti-pattern this project's
    own debt policy discourages (see TD-BE-017's target_state.quality_goal).
target_state:
  preferred_remediation: When COM-MOD-015-FE-001 (or a future dedicated backlog item)
    needs capability-specific request/response shapes or independent rate/permission
    scoping per capability, compile the dedicated controllers/permission codes at
    that point, backed by the same AiAssistantService/AiOverlayCapabilityRuleEngine
    orchestration.
  quality_goal: Do not duplicate a working generic endpoint with capability-specific
    routes until a real caller needs the distinct shape.
  acceptance_criteria:
  - Either the dedicated per-capability endpoints are compiled and traceability.md's
    api_endpoints entries are verified reachable, or traceability.md is corrected
    to point at the generic endpoint if the dedicated paths are retired as a design
    decision.
remediation:
  strategy: gradual_when_a_future_ui_or_external_caller_needs_capability_specific_request_response_shapes
  owner: backend_team
  estimated_effort: medium
  estimated_cost_impact: low
  target_backlog: COM-MOD-015-FE-001_or_a_future_dedicated_backlog_item
  dependencies_or_prerequisites:
  - A decided first caller of the capability-specific shape (employee-portal AI
    screens are the leading candidate).
progress_log:
- backlog_item: HOP-HARD-BE-001
  date: 2026-07-26
  action: Reviewed during backend hardening debt burn-down. No employee-portal AI
    screen or external caller has been compiled yet that needs the dedicated
    per-capability request/response shapes, so the generic assistant endpoint
    remains the sole compiled surface and the dedicated paths were intentionally
    not added, per this item's own quality_goal against duplicating a working
    endpoint ahead of real need.
  result: status remains open by design; risk remains low/non-blocking. Next owner
    is backend_team; next trigger is unchanged (COM-MOD-015-FE-001 or a future
    dedicated backlog item once a first caller of the capability-specific shape is
    decided).
```
