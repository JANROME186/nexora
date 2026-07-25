# HOP-QA-ALIGN-005 — Security & Quality Evidence

Backlog item: `HOP-QA-ALIGN-005` — Establish message externalization and magic-string remediation
baseline. Status: **passed**.

## Open-source-first

No new third-party dependency was introduced in any stack. All remediation is internal source
reorganization: a new Java constants class (`FrontDeskErrorCodes`) and two new TypeScript i18n
modules per frontend/mobile app, using the existing toolchain only.

## Checks

| Check | Result |
|---|---|
| Backend compile | passed |
| Backend tests (77) | passed |
| Backend coverage (66.48% >= 65.82% floor) | passed, no regression |
| Frontend typecheck / lint / build | passed |
| Frontend tests (18) | passed |
| Frontend coverage (73.04% >= 72.89% floor) | passed, no regression |
| Frontend duplication / format / license / npm audit | passed |
| Mobile typecheck / lint / duplication / format | passed |
| Mobile tests (8) | passed |
| DAST / container-IaC scan | not applicable to this backlog item (no runtime surface, dependency, or infrastructure change) |

## Message-externalization policy alignment

All eight `applies_to` categories from the framework's `message_externalization_policy` were
reviewed: user-visible text, validation messages, error titles/descriptions, domain error codes,
audit/notification templates, status labels, configurable business thresholds, and repeated magic
strings/numbers.

- **Backend expectations**: stable domain error codes now exist as named Java constants
  (`FrontDeskErrorCodes`), a mechanical projection of each capability package's
  `error_model.domain_errors`. A dedicated API `code` field and a resource-bundle message catalog
  are the documented next step (`TD-I18N-002`).
- **Frontend web expectations**: repeated/duplicated validation strings and confidence thresholds
  are centralized. Full locale-dictionary adoption for single-occurrence UI copy is the documented
  next step (`TD-I18N-002`).
- **Mobile app expectations**: repeated validation strings are centralized. Full
  localization-resource adoption is deferred until a renderable mobile UI layer exists
  (`TD-APP-001`), tracked by `TD-I18N-002`.
- **Closure rule applied**: only content actually reviewed in this baseline was externalized or
  formally dispositioned as debt; no full retroactive product-wide refactor was performed or
  required.

## Findings

No new vulnerabilities or application defects were found or fixed in this backlog item (none were
in scope — no dependency, DAST, or runtime-surface change occurred).

## Residual findings — accepted risk

| ID | Finding | Risk | Owner | Target |
|---|---|---|---|---|
| TD-I18N-002 | Backend API error responses lack a stable `code` field; ~125 single-occurrence frontend strings and full mobile localization remain unmigrated | Medium | Platform & frontend teams | Next i18n or release-readiness backlog item |

## Technical debt

- **Closed**: `TD-I18N-001`.
- **Newly registered**: `TD-I18N-002`.
- **Reviewed, unchanged**: `TD-BE-008`, `TD-FE-002` (neither impacted by message-externalization
  changes).

## Readiness

Security/quality status: **passed**. Ready for next backlog item: **`HOP-QA-ALIGN-CLOSEOUT`**.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-ALIGN-005-SECURITY-QUALITY
  type: security-quality-evidence
  backlog_item: HOP-QA-ALIGN-005
  status: passed
  created_date: 2026-07-16
  standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
open_source_first:
  status: passed
  proprietary_runtime_dependency_detected: false
  new_dependencies_introduced: false
  note: This backlog item introduced no new third-party dependency in any stack. All
    remediation is internal source reorganization (new Java constants class; new TypeScript
    i18n modules) using the existing toolchain.
checks:
  backend_compile: passed
  backend_tests: passed
  backend_coverage_no_regression: passed
  frontend_typecheck: passed
  frontend_lint: passed
  frontend_tests: passed
  frontend_coverage_no_regression: passed
  frontend_build: passed
  frontend_duplication_scan: passed
  frontend_format_check: passed
  frontend_license_check: passed
  frontend_dependency_vulnerability_scan: passed
  mobile_typecheck: passed
  mobile_lint: passed
  mobile_tests: passed
  mobile_duplication_scan: passed
  mobile_format_check: passed
  dast_for_runnable_web_or_api_surfaces: not_applicable_for_backlog
  container_or_iac_scan_when_assets_change: not_applicable_for_backlog
  message_externalization_inventory_and_strategy: passed
results:
  backend_tests_run: 77
  backend_tests_failed: 0
  backend_line_coverage_percent: 66.48
  frontend_tests_run: 18
  frontend_tests_failed: 0
  frontend_line_coverage_percent: 73.04
  frontend_npm_audit_low_or_higher_vulnerabilities: 0
  mobile_tests_run: 8
  mobile_tests_failed: 0
message_externalization_policy_alignment:
  applies_to_categories_reviewed:
  - user_visible_text
  - validation_messages
  - error_titles_and_descriptions
  - domain_error_codes
  - audit_or_notification_templates
  - status_labels
  - configurable_business_thresholds
  - repeated_magic_strings_or_numbers
  backend_expectations_status: Stable domain error codes now exist as named Java constants
    (FrontDeskErrorCodes, 21 constants covering all 30 runtime-reachable throw sites),
    a 1:1 mechanical projection of each capability package's openapi-source.md error_model.domain_errors.
    A dedicated API `code` response field and a resource-bundle-backed message catalog
    are the documented next step, tracked by TD-I18N-002.
  frontend_web_expectations_status: Repeated/duplicated validation strings and the
    duplicate-match confidence thresholds are now centralized in employee-portal/src/i18n/.
    Full locale-dictionary adoption for single-occurrence UI copy is the documented
    next step, tracked by TD-I18N-002.
  mobile_app_expectations_status: Repeated validation strings are centralized in mobile-app/src/i18n/messages.ts.
    Full localization-resource adoption is deferred until a renderable mobile UI layer
    exists (TD-APP-001), tracked by TD-I18N-002.
  closure_rule_applied: Only content actually reviewed in this baseline (the full
    current backend/frontend/mobile surface, since no smaller delta existed) was externalized
    or formally dispositioned as debt; no full retroactive product-wide refactor was
    performed or required.
application_defects_found_and_fixed: []
vulnerabilities_found_and_fixed: []
residual_findings_accepted_risk:
- id: TD-I18N-002
  finding: Backend API error responses do not yet expose a stable `code` field; ~125
    single-occurrence frontend UI strings and full mobile localization remain unmigrated
    to a locale-resource mechanism.
  risk_level: medium
  owner: platform_and_frontend_teams
  target_backlog: next_i18n_or_release_readiness_backlog_item_not_yet_scheduled
  expiration: review_at_next_HOP_QA_ALIGN_or_release_readiness_backlog_item
technical_debt:
  closed:
  - TD-I18N-001
  newly_registered:
  - TD-I18N-002
  reviewed_unchanged:
  - TD-BE-008
  - TD-FE-002
  blocking: []
exceptions: []
readiness:
  security_quality_status: passed
  ready_for_next_backlog_item: HOP-QA-ALIGN-CLOSEOUT
  next_required_focus:
  - HOP Enterprise Quality Alignment closeout (HOP-QA-ALIGN-CLOSEOUT).
  - Resume MVP-MOD-004-FE-001 once HOP-QA-ALIGN-CLOSEOUT passes.
  - Address TD-I18N-002 when a second locale or a structured-error-code API consumer
    is needed.
```
