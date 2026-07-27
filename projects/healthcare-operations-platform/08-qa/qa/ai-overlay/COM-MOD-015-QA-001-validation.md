---
artifact:
  id: HOP-QA-COM-MOD-015-QA-001
  type: qa-validation-evidence
  status: validated
  backlog_item: COM-MOD-015-QA-001
---

# COM-MOD-015-QA-001 Validation Evidence

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-COM-MOD-015-QA-001
  type: qa-validation-evidence
  name: COM-MOD-015-QA-001 Safety, Explainability and Human-Control Evidence
  version: 1.0.0
  status: validated
  backlog_item: COM-MOD-015-QA-001
  roadmap_group: COM-MOD-015
  created_date: 2026-07-26
scope:
  capability: BCM-AI-006 Safety Policy and Human Review
  module: AI Overlay
  qa_focus:
  - safety
  - explainability
  - human_control
  - vendor_lock_in_scan
guardrail_evidence:
  no_autonomous_clinical_validation:
    control: AiAssistantService.enforcePolicy rejects prompts requesting autonomous
      clinical validation or skipping human review with AI_POLICY_BLOCKED (409).
    tests:
    - AiAssistantServiceTest#policyBlocksAutonomousClinicalValidationRequests
    - AiOverlayApiTest#prohibitedAutonomousClinicalRequestIsRejected
  source_citations_required:
    control: AiOverlayCapabilityRuleEngine.validateDraft rejects any OCR/summary/search/retrieval
      draft without at least one citation with AI_CITATIONS_REQUIRED (400); the
      employee-portal review screen also blocks review submission client-side
      when citations are absent (COM-MOD-015-FE-001).
    tests:
    - AiOverlayCapabilityRuleEngineTest#requiresAtLeastOneCitationOnGeneratedOutput
    - AiOverlayApiTest#ocrDocumentIntakeAcceptsAnAllowedSourceContextType
    - AiOverlayApiTest#semanticSearchAndRetrievalGroundingAcceptTheirDeclaredSourceContextTypes
  human_override_requires_reason:
    control: AiAssistantService.reviewDraft requires a non-blank reviewReason,
      now raising the previously-unused AI_REVIEW_REASON_REQUIRED code (was
      falling back to the generic AI_COMMAND_INVALID; corrected as TD-BE-022).
    tests:
    - AiAssistantServiceTest#reviewerMustRecordDispositionReason
  ai_output_must_not_bypass_iam_or_audit:
    control: requestAssistantDraft/reviewDraft require tenantId/actorId and record
      an AuditRecorder system event on every draft generation and review decision,
      carrying policyVersion, reviewStatus/decision and reviewerId; the
      /api/ai/assistant/sessions/audit-records endpoint exposes citations,
      confidenceBand, safetyDecision, reviewStatus, modelProviderRef, modelNameRef
      and policyVersion for explainability.
    tests:
    - AiAssistantServiceTest#draftGenerationAndReviewAreBothRecordedAsExplainableAuditEvents
    - AiOverlayApiTest#assistantDraftReviewAndAuditRoundTrip
    - AiOverlayApiTest#rejectedReviewDecisionIsRecordedAndAuditableAndCannotBeChangedAgain
  human_control_decision_immutability:
    control: 'New guard: a reviewed session (lifecycleStatus already archived)
      cannot be reviewed a second time; AI_REVIEW_ALREADY_RECORDED (409) protects
      the recorded human decision from being silently overwritten (TD-BE-022).'
    tests:
    - AiAssistantServiceTest#reviewedDecisionCannotBeChangedAfterItIsRecorded
    - AiOverlayApiTest#rejectedReviewDecisionIsRecordedAndAuditableAndCannotBeChangedAgain
  vendor_lock_in_scan:
    control: AiDraftGeneratorPort is the sole, replaceable integration seam for
      model providers; no token-billed proprietary AI SDK dependency exists in
      backend/pom.xml or in AiAssistantService.
    tests:
    - AiOverlayVendorNeutralityTest#buildDeclaresNoProprietaryModelProviderSdkDependency
    - AiOverlayVendorNeutralityTest#assistantServiceOnlyDependsOnTheReplaceableDraftGeneratorPort
technical_debt_action:
  materially_reduced:
  - TD-BE-022
  details: Discovered during this validation review that AI_REVIEW_REASON_REQUIRED
    was dead code (the review-reason guardrail fell back to the generic
    AI_COMMAND_INVALID) and that a reviewed session had no immutability guard.
    Both are fixed in this item; see 08-qa/technical-debt/TD-BE-022-ai-overlay-review-reason-error-code-and-review-immutability-gap.md.
  residual_non_blocking:
  - TD-BE-021
quality_gates:
  backend:
    maven_test:
      command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Djava.io.tmpdir=target/tmp -Dhop.document-storage.local.path=target/test-documents -Dtest=com.nexora.hop.platformfoundation.aioverlay.** test
      result: passed
      tests: 24
      failures: 0
      errors: 0
      skipped: 2
    maven_quality_verify:
      command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Djava.io.tmpdir=target/tmp -Dhop.document-storage.local.path=target/test-documents -Pquality clean verify
      result: passed
      tests: 522
      failures: 0
      errors: 0
      skipped: 31
    coverage:
      tool: JaCoCo
      line_coverage_percent: 70.16
      previous_floor_percent: 70.14
      coverage_regression: false
  frontend:
    typecheck:
      command: npm run typecheck
      result: passed
    lint_sast:
      command: npm run lint
      result: passed_with_warnings
      errors: 0
      warnings: 62
    test_coverage:
      command: npm run test:coverage
      result: 256 tests passed across 69 test files
      line_coverage_percent: 91.00
      previous_floor_percent: 91.00
      coverage_regression: false
    build:
      command: npm run build
      result: passed
    duplication:
      command: npm run duplication
      result: passed
    format:
      command: npm run format:check
      result: passed
    license:
      command: npm run license:check
      result: passed (MIT 5, UNLICENSED 1 project package)
    dependency_audit_offline:
      command: npm audit --audit-level=low --offline
      result: passed (0 vulnerabilities)
    production_dependency_audit_offline:
      command: npm audit --omit=dev --audit-level=low --offline
      result: passed (0 vulnerabilities)
    trivy_filesystem:
      command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL --skip-dirs node_modules --skip-dirs dist --skip-dirs coverage .
      version: 0.72.0
      result: passed (0 vulnerabilities, 0 secrets, 0 misconfigurations)
  git_diff_check: clean
acceptance:
- All four BCM-AI-006 guardrails (no autonomous clinical validation, source
  citations required, human override requires reason, AI output does not bypass
  IAM or audit) have a distinct, tested error code and audit trail, not a generic
  fallback.
- A recorded human-review decision is now immutable; a second review attempt on
  an already-reviewed session is rejected with AI_REVIEW_ALREADY_RECORDED.
- Draft generation and review are both explainable: every AiAssistantService
  action records an AuditRecorder event carrying policyVersion and the
  decision/reviewStatus, retrievable through the tenant audit-records endpoint.
- No proprietary, token-billed AI provider SDK dependency exists in the backend
  build or in AiAssistantService; AiDraftGeneratorPort remains the only,
  replaceable integration seam.
- No regression to backend (70.16 percent, floor 70.14 percent) or frontend
  (91.00 percent) coverage; no new lint errors; 0 vulnerabilities/secrets across
  offline npm audit and Trivy.
closure:
  status: closed
  blocking_findings: 0
  next_backlog_item: COM-MOD-015-CLOSEOUT
```
