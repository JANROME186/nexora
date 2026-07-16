# HOP-QA-ALIGN-005 — Validation Evidence

Backlog item: `HOP-QA-ALIGN-005` — Establish message externalization and magic-string remediation
baseline. Result: **completed**.

Machine-readable companion: `HOP-QA-ALIGN-005-validation.yaml`.

## Required outputs delivered

| Output | Location |
|---|---|
| `message_externalization_inventory_yaml` | `HOP-QA-ALIGN-005-message-externalization-inventory.yaml` |
| `message_externalization_inventory_md` | `HOP-QA-ALIGN-005-message-externalization-inventory.md` |
| Updated/new technical debt items | `TD-I18N-001` (closed), `TD-I18N-002` (new) |
| Proposed backend message catalog strategy | Inventory MD, "Backend message catalog strategy" |
| Proposed frontend i18n strategy | Inventory MD, "Frontend i18n strategy" |
| Proposed mobile localization strategy | Inventory MD, "Mobile localization strategy" |

## Quality gates executed

### Backend

- `mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -q compile` — passed.
- `mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml test` — **77 tests, 0 failures, 0
  errors, 7 skipped** — identical counts to the pre-existing baseline, confirming the
  `FrontDeskErrorCodes` refactor across 30 throw sites in 5 service classes is behavior-preserving.
- `mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Pquality test jacoco:report` —
  line coverage **66.48%** (floor 65.82%, no regression).

### Employee portal

- `npm run quality` (typecheck, lint, test:coverage, build, duplication, format:check,
  license:check) — all passed. 18/18 tests. Line coverage **73.04%** (floor 72.89%, no
  regression). 9 pre-existing lint warnings, 0 errors, none introduced by this backlog item.
- `npm audit --audit-level=low` — 0 vulnerabilities.

### Mobile app

- `npm run quality` (typecheck, lint, test, duplication, format:check) — all passed. 8/8 tests.
- Coverage measurement remains blocked by the pre-existing `TD-APP-002` toolchain gap, unrelated
  to message externalization; not attempted, consistent with the HOP-QA-ALIGN-004 disposition.

## Behavior-preservation evidence

Every change in this backlog item was a pure extraction of already-duplicated or
already-informally-coded literal values into named constants or shared functions — no exception
message text, UI copy, or control flow changed. This is directly evidenced by every touched test
suite passing with identical pass/fail/skip counts to the pre-existing baseline (backend 77/0/7,
frontend 18/0, mobile 8/0).

## Final validations

- **VAL-001 YAML parse** — passed, full project and framework tree.
- **VAL-002 Agent-agnostic scan** — passed, 0 forbidden files/folders, 0 content matches.
- **VAL-003 Stale pointer scan** — passed. `HOP-QA-ALIGN-004` no longer appears as an active/
  current/next backlog pointer anywhere in the repository; `PROJECT_STATE.yaml`,
  `SOURCE_OF_TRUTH.yaml`, `HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml` and the runbook now all
  point to `HOP-QA-ALIGN-CLOSEOUT`.
- **VAL-004 No prohibited execution-limitation statuses** — passed, 0 matches for
  `passed_with_execution_limitation`, `closed_with_execution_limitation` or a mandatory gate
  `not_executed`.
- **VAL-005 `git diff --check`** — passed.

## Out of scope, confirmed and dispositioned

- Backend `code` field on the 5 `*ApiErrorResponse` shapes — `TD-I18N-002` (OpenAPI contract
  change, too large for this backlog's small-and-safe bar).
- Full frontend i18n-library adoption for the ~125 remaining single-occurrence UI strings —
  `TD-I18N-002`.
- Full mobile localization — `TD-I18N-002`, gated on `TD-APP-001`'s renderer-stack selection.
- Mobile coverage baseline — pre-existing `TD-APP-002`, unrelated to this backlog item.
- `TD-BE-008` and `TD-FE-002` — reviewed, neither impacted by message-externalization changes.

## Readiness

- `HOP-QA-ALIGN-005` status: **closed**.
- Ready for next backlog item: **`HOP-QA-ALIGN-CLOSEOUT`**.
- `MVP-MOD-004-FE-001` remains paused pending `HOP-QA-ALIGN-CLOSEOUT`.
