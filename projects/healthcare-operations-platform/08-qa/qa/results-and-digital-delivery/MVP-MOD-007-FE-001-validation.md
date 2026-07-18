# QA Validation Evidence: MVP-MOD-007-FE-001

**Status:** PASSED
**Date:** 2026-07-17

Compiled the employee-portal UI outputs for Results and Digital Delivery: Result Search and
Worklist / Result Detail (BCM-RES-001), Result Report History and Regenerate (BCM-RES-002),
Critical Result Escalation Worklist / Acknowledge / Escalate / Close (BCM-RES-006), and Result
Notification History (BCM-RES-007).

## Pre-Existing Work Found and Corrected

This backlog item started with an uncommitted, partially built set of four screens
(`ResultSearchScreen.tsx`, `ResultReportsScreen.tsx`, `CriticalEscalationsScreen.tsx`,
`ResultNotificationsScreen.tsx`), a `resultsDeliveryApi.ts` client, matching `types.ts` additions,
and test files, from a prior session. `npm run format:check` was the only failing gate reported at
handoff.

Validating the API client against the real backend controllers (not just the tests, which mocked
the HTTP layer) found it targeted endpoints that did not exist as REST adapters:

- **BCM-RES-001**: no list-by-status search endpoint existed, and `getResultById` omitted the
  backend's required `tenantId` parameter.
- **BCM-RES-002** and **BCM-RES-007**: no web adapter existed at all for report history/regenerate
  or notification history — only bare domain classes or an application service, despite
  `MVP-MOD-007-BE-001` ("Compile result report and document generation outputs") having been marked
  closed.

This was escalated to the requester, who selected building the missing backend end to end rather
than descoping or stubbing the corresponding screens.

## Backend Gap Closure (07-implementation/backend)

- **BCM-RES-001**: added `LaboratoryResultsService.listByStatus` and a `GET
  /api/clinical-operations/laboratory-results?tenantId=&status=` endpoint (the repository already
  supported `findByStatus` in both the in-memory and JDBC implementations; only the service method
  and controller endpoint were missing).
- **BCM-RES-002**: built the previously-missing `GeneratedResultReportRepository` (port + in-memory
  adapter), `ResultReportService` (list + regenerate, enforcing the RN-001 release-state
  precondition, versioning and superseding prior active reports), and `ResultReportController`.
  This is the first production caller of `DocumentManagementService`, which previously had none.
  Added a `GENERATION_FAILED` status and an `integrityChecksum` field to `GeneratedResultReport`
  additively, without changing the existing `GENERATED`/`SUPERSEDED` values or the constructor
  already covered by `ResultsDomainTest`.
- **BCM-RES-007**: added `findByResultId` to the notification repository, a
  `listNotificationsForResult` service method, and a new `ResultNotificationController`. Added
  `channel`/`dispatchedAt`/`deliveredAt`/`failureReason` fields to `ResultNotificationRequest`
  additively, preserving the existing constructors and getters used by `ResultsDomainTest` and
  `ResultNotificationServiceTest`.
- Fixed a **Spring Modulith module-boundary violation** this wiring exposed: `documentmanagement`'s
  `application`/`domain` packages had no `NamedInterface`, so `resultsanddigitaldelivery` could not
  legitimately depend on `DocumentManagementService`/`StoredDocument`/`RetentionPolicy` even though
  the module-level `allowedDependencies` already listed `documentmanagement`. Added
  `document-service`/`document-domain` named interfaces; `PlatformFoundationModulithTest` passes.

Backend suite: **156 tests, 0 failures, 0 errors, 9 skipped** (local-database-only tests). JaCoCo
line coverage **76.99%**, up from the 76.93% floor set by `MVP-MOD-007-BE-002` (no regression).

## Frontend Contract Correction

`resultsDeliveryApi.ts` now sends `tenantId` (and `regenerateReport` an `actorId`) on every call
that requires it, matching every other controller in this codebase. `LaboratoryResult`'s wire shape
(owned by BCM-LAB-006/MVP-MOD-006) predates this backlog and is shared with already-closed screens
(`ResultReleaseScreen`, `TechnicalValidationScreen`, `MedicalValidationScreen`) that carry the same
shape mismatch against their own FE type — reshaping that shared type is out of scope (historical
debt, registered as **TD-FE-007**, not introduced by this backlog item). Instead,
`resultsDeliveryApi.ts` normalizes the real backend response into the existing `LaboratoryResult` FE
shape at the API-client boundary, so `ResultSearchScreen.tsx` renders correctly against the real
backend without touching the shared type or the other, already-closed screens that consume it.

## Debt-First Action

This backlog item's own new code triggered fresh ESLint `max-lines-per-function`/
`cognitive-complexity` warnings (tracked collectively by **TD-FE-003**) on `ResultSearchScreen.tsx`,
`CriticalEscalationsScreen.tsx` and `resultsDeliveryApi.ts`. Rather than add to that historical
total, all three were refactored to 0 warnings:

- `ResultSearchScreen.tsx` → extracted `ResultDetailPanel` / `ResultLifecycleTimeline`.
- `CriticalEscalationsScreen.tsx` → extracted `EscalationDetailPanel` / `EscalationsTable`.
- `resultsDeliveryApi.ts` → split the `LaboratoryResult` mapper into
  `toAnalyteSnapshots`/`toReferenceRangeSnapshots`/`toResultValues`/`toIncidents`/`toAmendments`.

`TD-FE-003` itself (pre-existing screens) remains open and out of scope.

## Capability Coverage

| Capability | Screens | Endpoints |
|---|---|---|
| BCM-RES-001 Result Management | Result Search and Worklist, Result Detail | `GET .../laboratory-results?tenantId=&status=`, `GET .../laboratory-results/{resultId}?tenantId=` |
| BCM-RES-002 PDF Report Generation | Report History, Regenerate | `GET .../reports?tenantId=`, `POST .../reports/regenerate?tenantId=&actorId=` |
| BCM-RES-006 Critical Results | Escalation Worklist, Acknowledge/Escalate/Close | `GET .../critical-escalations/open?tenantId=`, `POST .../{id}/acknowledge`, `/escalate`, `/close` |
| BCM-RES-007 Result Notifications | Notification History | `GET .../notifications?tenantId=` |
| BCM-RES-004/005 Digital Delivery / Result History | none required | `ui-model.yaml` scopes both to patient/doctor portal only |

## Gates Executed

- `npm run typecheck` — passed
- `npm run lint` — passed, 0 errors, 24 warnings, all on pre-existing/untouched screens (TD-FE-003)
- `npm run test:coverage` — passed, 29 files / 79 tests, line coverage **83.98%** (floor 82.69%,
  handoff figure 83.85%, no regression)
- `npm run build` — passed
- `npm run duplication` — passed, 0 jscpd findings
- `npm run format:check` — failing at handoff, fixed with `prettier --write`, re-run passed
- `npm run license:check` — passed, 5 MIT + 1 UNLICENSED, unchanged
- `npm audit --audit-level=low` — passed, 0 vulnerabilities
- `npm run quality` — passed (chains all of the above)
- `mvn -o test` (backend) — passed, 156 tests, 0 failures, coverage 76.99% (floor 76.93%)
- `trivy fs --scanners vuln,secret,misconfig ...` — passed, 0 vulnerabilities/secrets/misconfigurations
- `git diff --check` — passed, 0 whitespace errors

## Agent-Agnostic and i18n Validation

Grepped every touched file for `claude|anthropic|copilot|cursor|chatgpt|openai|gemini|codex|
windsurf|aider` (case-insensitive): 0 forbidden matches (the only hit was `cursor: pointer` CSS,
confirmed a false positive). The non-ASCII `⚠` introduced in `ResultSearchScreen.tsx` was removed in
favor of the existing `catalog-status--critical` class plus text/aria-label. The em-dash (`—`)
placeholder was kept as an already-established repo convention. Cross-screen guard/outcome messages
were centralized in `messages.ts`; single-occurrence screen copy stays inline per the repo's
documented policy.

## Technical Debt

- **TD-FE-007 (new)**: `LaboratoryResult` wire-shape mismatch between its FE type and the real
  BCM-LAB-006 backend record, pre-existing and shared with already-closed MVP-MOD-006 screens.
  Worked around locally in `resultsDeliveryApi.ts`; recommend a dedicated cleanup item.
- TD-FE-003 and TD-I18N-002 reviewed and confirmed out of scope (pre-existing, unrelated screens).

## Readiness

`MVP-MOD-007-FE-001` is closed. The next backlog item, confirmed in
`HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml`, is `MVP-MOD-007-PORTAL-001` (Compile patient and doctor
released result views).
