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
