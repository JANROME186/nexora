---
id: TD-BE-022
format: markdown_structured_payload
type: technical-debt-item
name: AI Overlay human-review-reason error code was dead code and a reviewed session
  could be silently re-reviewed
version: 1.0.0
status: materially_reduced
---

# AI Overlay Human-Review-Reason Error Code Was Dead Code and a Reviewed Session Could Be Silently Re-Reviewed

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-022
  type: technical-debt-item
  name: AI Overlay human-review-reason error code was dead code and a reviewed
    session could be silently re-reviewed
  version: 1.0.0
  status: materially_reduced
  created_date: 2026-07-26
  updated_date: 2026-07-26
source:
  discovered_during_backlog_item: COM-MOD-015-QA-001
  module: COM-MOD-015 AI Overlay
  evidence: 08-qa/qa/ai-overlay/COM-MOD-015-QA-001-validation.md
classification:
  category: human_control_and_explainability_gap
  affected_area: ai_overlay_assistant_review_flow
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/aioverlay/assistant/application/AiAssistantService.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/aioverlay/shared/AiOverlayErrorCode.java
  risk_level: medium
  urgency: medium
  blocking: false
  reason_non_blocking: Both issues were fixed within this same backlog item before
    closure; nothing remains open in product code. Recorded as debt so the gap and
    its fix are traceable, matching this project's pattern of registering a defect
    discovered mid-item alongside its remediation rather than leaving it implicit.
current_state:
  issue_1: AiOverlayErrorCode.AI_REVIEW_REASON_REQUIRED (and its en_US/es_MX/default
    i18n catalog entries) existed but was never thrown; AiAssistantService.reviewDraft
    used the generic AI_COMMAND_INVALID code for a blank human-review reason instead,
    so BCM-AI-006's human_override_requires_reason guardrail was not distinctly
    identifiable in the audit/error explainability surface.
  issue_2: AiAssistantService.reviewDraft had no guard against reviewing a session
    whose lifecycleStatus was already STATUS_ARCHIVED, so a second reviewDraft call
    against an already-decided session would silently overwrite the recorded
    reviewStatus/reviewerId/reviewReason, undermining the human-control guarantee
    that a human review decision is an immutable audit record.
target_state:
  fix: Added AiOverlayErrorCode.AI_REVIEW_ALREADY_RECORDED (with matching i18n
    entries) and wired it into AiOverlayExceptionHandler (409 Conflict, same as
    AI_POLICY_BLOCKED). AiAssistantService.reviewDraft now throws
    AI_REVIEW_REASON_REQUIRED for a blank reason and AI_REVIEW_ALREADY_RECORDED
    when the session's lifecycleStatus is already archived, before persisting any
    change.
  quality_goal: Every guardrail statement in a capability's business-rules.md must
    map to a distinguishable, tested error code rather than a generic fallback,
    and a recorded human-review decision must be immutable.
  acceptance_criteria:
  - AiAssistantServiceTest asserts AI_REVIEW_REASON_REQUIRED for a blank reason and
    AI_REVIEW_ALREADY_RECORDED for a second review attempt.
  - AiOverlayApiTest exercises the already-reviewed conflict at the HTTP layer
    (409, code AI_REVIEW_ALREADY_RECORDED).
remediation:
  strategy: fixed_in_place_during_discovery
  owner: backend_team
  estimated_effort: small
  estimated_cost_impact: low
  target_backlog: COM-MOD-015-QA-001
progress_log:
- backlog_item: COM-MOD-015-QA-001
  date: 2026-07-26
  action: Added AI_REVIEW_ALREADY_RECORDED error code, i18n entries, exception-handler
    mapping, the immutability guard in AiAssistantService.reviewDraft, and wired
    the pre-existing but unused AI_REVIEW_REASON_REQUIRED code into the blank-reason
    path. Added AiAssistantServiceTest.reviewedDecisionCannotBeChangedAfterItIsRecorded,
    AiAssistantServiceTest.reviewerMustRecordDispositionReason (updated to assert
    the specific error code) and AiOverlayApiTest.rejectedReviewDecisionIsRecordedAndAuditableAndCannotBeChangedAgain.
  result: mvn test/quality verify green (522 tests, 0 failures/errors, 70.16 percent
    line coverage); status moved from (undiscovered) directly to materially_reduced
    since the fix and its tests landed in the same backlog item.
```
