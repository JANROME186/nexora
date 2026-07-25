# COM-MOD-009-APP-001 Security Quality Evidence

Status: **passed**

This evidence covers the patient mobile workflow delivered under `07-implementation/mobile-app/`.

## Executed Gates

| Gate | Result |
| --- | --- |
| TypeScript typecheck | Passed |
| ESLint with security and sonarjs rules | Passed |
| Vitest coverage | 40 tests, 0 failures |
| Line coverage | 99.21%, above the 98.87% previous floor |
| Duplicate-code check (`jscpd`) | Passed |
| Prettier format check | Passed |
| Dependency vulnerability scan | `npm audit --audit-level=low`: 0 vulnerabilities |
| Secrets scan | Passed |
| Agent-agnostic scan | Passed |

DAST and container/IaC scans are not applicable for this item because no runnable mobile surface, container asset or infrastructure asset changed.

## Security Notes

Patient mobile navigation is derived from session role permissions. The API facade keeps the provider-neutral fetch boundary and propagates authorization/session headers. The workflow exposes a forbidden state when the session has no patient-channel permissions.

`TD-I18N-002` and `TD-IAM-002` were materially reduced. No new technical debt was registered.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: COM-MOD-009-APP-001-SECURITY-QUALITY
  type: security-quality-evidence
  backlog_item: COM-MOD-009-APP-001
  module: COM-MOD-009 Patient and Doctor Portals
  name: Patient mobile workflow security and quality evidence
  status: passed
  created_date: 2026-07-19
  owner: Nexora Delivery Team
scope:
  changed_stack: mobile_app_typescript
  changed_surface:
  - 07-implementation/mobile-app/src/auth/
  - 07-implementation/mobile-app/src/navigation/
  - 07-implementation/mobile-app/src/api/
  - 07-implementation/mobile-app/src/screens/
  - 07-implementation/mobile-app/src/i18n/
  - 07-implementation/mobile-app/src/test/
checks:
  tests:
    status: passed
    command: npm run quality
    test_files: 12
    tests: 40
    failures: 0
    errors: 0
    skipped: 0
  typecheck:
    status: passed
    command: npm run typecheck
  static_analysis:
    status: passed
    tools:
    - eslint
    - eslint-plugin-security
    - eslint-plugin-sonarjs
    - typescript
  duplicate_code:
    status: passed
    tool: jscpd
  formatting:
    status: passed
    tool: prettier
  dependency_vulnerabilities_all_severities:
    status: passed
    command: npm audit --audit-level=low
    vulnerabilities: 0
  coverage:
    status: passed
    current_line_coverage_percent: 99.21
    previous_line_coverage_floor_percent: 98.87
    final_target_percent: 80
    regression: false
  secrets_scan:
    status: passed
    method: repository_regex_scan_for_secret_like_tokens_on_changed_source_and_evidence
  agent_agnostic_scan:
    status: passed
    method: repository_regex_scan_on_changed_source_and_evidence
  dast:
    status: not_applicable_no_runnable_mobile_surface
  container_or_iac_scan_when_assets_change:
    status: not_applicable_no_container_or_iac_changed
security_controls:
  iam:
    status: passed
    detail: Patient workflow routes are exposed from role-derived permissions; forbidden
      state is explicit.
  session_context:
    status: passed
    detail: API facade propagates bearer token and tenant/user session headers through
      existing mobile session context.
  privacy:
    status: passed
    detail: The mobile model loads only allowed patient sections and delegates self-access
      enforcement to backend portal contracts.
  localization:
    status: passed
    detail: New and changed visible strings are in es-MX/en-US locale catalogs.
technical_debt:
  materially_reduced:
  - TD-I18N-002
  - TD-IAM-002
  newly_registered: []
  blocking: []
decision:
  backlog_item_status: closed
  ready_for_next_backlog_item: COM-MOD-010-BE-001
  next_backlog_item_name: Compile product, reagent, lot and stock outputs
```
