---
artifact:
  id: HOP-SQ-COM-MOD-015-FE-001
  type: security-quality-evidence
  status: validated
  backlog_item: COM-MOD-015-FE-001
---

# COM-MOD-015-FE-001 Security and Quality Evidence

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-COM-MOD-015-FE-001
  type: security-quality-evidence
  name: COM-MOD-015-FE-001 Security and Quality Evidence
  version: 1.0.0
  status: validated
  backlog_item: COM-MOD-015-FE-001
  captured_on: 2026-07-26
scope:
  component: employee-portal
  module: AI Overlay
  frontend_surface: AiAssistantReviewScreen
open_source_first:
  new_dependencies_added: false
  production_dependencies_changed: false
  dev_dependencies_changed: false
  license_check: passed
security_controls:
  provider_neutrality:
    api_surface: /api/ai/assistant/sessions
    provider_specific_schema_added: false
    token_billed_api_key_dependency_added: false
  authorization:
    screen_permission: SCREEN_AI_ASSISTANT
    screen_key: ai-assistant-review
    role_mapping: RESULTS_COORDINATOR receives SCREEN_AI_ASSISTANT; ADMIN receives
      all permissions through PERMISSION_CODES.
    dynamic_navigation: AppShell filters the tab through SCREEN_TO_PERMISSION.
  tenant_actor_context:
    headers_added:
    - X-Tenant-Id
    - X-User-Id
    rationale: AI and imaging controllers read these standard headers directly while
      existing HOP authorization headers remain preserved.
  human_control:
    review_required_status: human_review_required
    review_decisions:
    - accepted
    - rejected
    citation_required_before_review: true
    autonomous_clinical_validation_enabled: false
  auditability:
    audit_records_endpoint: /api/ai/assistant/sessions/audit-records
    visible_fields:
    - citations
    - confidenceBand
    - safetyDecision
    - reviewStatus
    - modelProviderRef
    - modelNameRef
    - policyVersion
  xss_posture:
    dangerously_set_inner_html_used: false
    rendering: React JSX text-node escaping for prompt, draft output, citations and
      audit table values.
  i18n:
    user_visible_text_externalized: true
    locales:
    - es-MX
    - en-US
evidence_commands:
  typecheck:
    command: npm run typecheck
    result: passed
  lint_sast:
    command: npm run lint
    result: passed_with_warnings
    errors: 0
    warnings: 62
    security_plugin: eslint-plugin-security enabled in eslint.config.js
  test_coverage:
    command: npm run test:coverage
    result: 256 tests passed, 69 test files
    line_coverage_percent: 91.00
  build:
    command: npm run build
    result: passed
  duplication:
    command: npm run duplication
    result: passed
  format:
    command: npm run format:check
    result: passed
  dependency_audit_online:
    command: npm run audit:all
    result: blocked_by_registry_endpoint
    note: npm audit endpoint returned an error in the restricted environment; no
      dependency files were modified.
  dependency_audit_offline:
    command: npm audit --audit-level=low --offline
    result: found 0 vulnerabilities
  production_dependency_audit_offline:
    command: npm audit --omit=dev --audit-level=low --offline
    result: found 0 vulnerabilities
  trivy_filesystem:
    command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL --skip-dirs node_modules --skip-dirs dist --skip-dirs coverage .
    version: 0.72.0
    vulnerability_db_updated_at: 2026-07-26T13:31:30Z
    result: passed
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
  license:
    command: npm run license:check
    result: passed (MIT 5, UNLICENSED 1 project package)
  i18n_key_parity:
    command: npm run typecheck
    result: passed; en-US catalog conforms to es-MX MessageCatalog shape.
technical_debt:
  materially_reduced:
  - TD-UX-001
  residual_non_blocking:
  - TD-FE-010
  - TD-FE-012
closure:
  status: closed
  blocking_findings: 0
  next_backlog_item: COM-MOD-015-QA-001
```
