---
artifact:
  id: HOP-SQ-COM-MOD-015-QA-001
  type: security-quality-evidence
  status: validated
  backlog_item: COM-MOD-015-QA-001
---

# COM-MOD-015-QA-001 Security and Quality Evidence

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-COM-MOD-015-QA-001
  type: security-quality-evidence
  name: COM-MOD-015-QA-001 Security and Quality Evidence
  version: 1.0.0
  status: validated
  backlog_item: COM-MOD-015-QA-001
  captured_on: 2026-07-26
scope:
  component: backend + employee-portal
  module: AI Overlay
  capability: BCM-AI-006 Safety Policy and Human Review
open_source_first:
  new_dependencies_added: false
  production_dependencies_changed: false
  dev_dependencies_changed: false
security_controls:
  provider_neutrality:
    api_surface: /api/ai/assistant/sessions
    provider_specific_schema_added: false
    token_billed_api_key_dependency_added: false
    vendor_lock_in_scan: AiOverlayVendorNeutralityTest asserts backend/pom.xml
      and AiAssistantService carry no proprietary AI SDK marker (OpenAI, Azure
      AI, Google Vertex AI, AWS Bedrock, Anthropic, Cohere).
  safety_policy:
    autonomous_clinical_validation_enabled: false
    policy_blocked_error_code: AI_POLICY_BLOCKED
    policy_version: AI-SAFE-001
  human_control:
    review_required_status: human_review_required
    review_decisions:
    - accepted
    - rejected
    citation_required_before_review: true
    review_reason_required: true
    review_reason_error_code: AI_REVIEW_REASON_REQUIRED (corrected from a
      generic fallback; see TD-BE-022)
    review_decision_immutable: true
    review_already_recorded_error_code: AI_REVIEW_ALREADY_RECORDED (new;
      HTTP 409, see TD-BE-022)
  explainability_and_audit:
    audit_records_endpoint: /api/ai/assistant/sessions/audit-records
    visible_fields:
    - citations
    - confidenceBand
    - safetyDecision
    - reviewStatus
    - modelProviderRef
    - modelNameRef
    - policyVersion
    audit_events_recorded:
    - AiAssistantDraftGenerated
    - AiAssistantDraftReviewed
    audit_event_metadata_verified_by_test: AiAssistantServiceTest#draftGenerationAndReviewAreBothRecordedAsExplainableAuditEvents
  tenant_actor_context:
    required_headers:
    - X-Tenant-Id
    - X-User-Id
  i18n:
    new_error_message_key: ai.error.review_already_recorded
    locales_updated:
    - default (messages.properties)
    - en-US (messages_en_US.properties)
    - es-MX (messages_es_MX.properties)
evidence_commands:
  backend_focused_tests:
    command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Djava.io.tmpdir=target/tmp -Dhop.document-storage.local.path=target/test-documents -Dtest=com.nexora.hop.platformfoundation.aioverlay.** test
    result: 24 tests passed, 2 skipped (local-db-tests profile not enabled), 0 failures
  backend_quality_verify:
    command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Djava.io.tmpdir=target/tmp -Dhop.document-storage.local.path=target/test-documents -Pquality clean verify
    result: passed
    tests: 522
    failures: 0
    errors: 0
    skipped: 31
  backend_coverage:
    tool: JaCoCo
    line_coverage_percent: 70.16
    previous_floor_percent: 70.14
    regression: false
  frontend_typecheck:
    command: npm run typecheck
    result: passed
  frontend_lint:
    command: npm run lint
    result: passed_with_warnings
    errors: 0
    warnings: 62
    security_plugin: eslint-plugin-security enabled in eslint.config.js
  frontend_test_coverage:
    command: npm run test:coverage
    result: 256 tests passed, 69 test files
    line_coverage_percent: 91.00
    previous_floor_percent: 91.00
    regression: false
  frontend_build:
    command: npm run build
    result: passed
  frontend_duplication:
    command: npm run duplication
    result: passed
  frontend_format:
    command: npm run format:check
    result: passed
  frontend_license:
    command: npm run license:check
    result: passed (MIT 5, UNLICENSED 1 project package)
  frontend_dependency_audit_offline:
    command: npm audit --audit-level=low --offline
    result: found 0 vulnerabilities
  frontend_production_dependency_audit_offline:
    command: npm audit --omit=dev --audit-level=low --offline
    result: found 0 vulnerabilities
  frontend_trivy_filesystem:
    command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL --skip-dirs node_modules --skip-dirs dist --skip-dirs coverage .
    version: 0.72.0
    vulnerability_db_updated_at: 2026-07-26T13:31:30Z
    result: passed
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
technical_debt:
  materially_reduced:
  - TD-BE-022
  residual_non_blocking:
  - TD-BE-021
  - TD-FE-010
  - TD-FE-012
closure:
  status: closed
  blocking_findings: 0
  next_backlog_item: COM-MOD-015-CLOSEOUT
```
