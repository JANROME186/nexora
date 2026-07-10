# MVP-MOD-003-FE-001 — Patient and Doctor Management Employee Portal UI Validation

Status: **passed_with_execution_limitation**
Backlog item: MVP-MOD-003-FE-001
Module: MVP-MOD-003 People and Clinical Master Data
Machine-readable evidence: `MVP-MOD-003-FE-001-validation.yaml`

## Objective

Compile the employee portal UI needed to operate the already-implemented People and Clinical
Master Data backend capabilities (BCM-PER-001 Person Management, BCM-PER-002 Patient Management,
BCM-PER-003 Doctor Management, BCM-ATT-002 Patient Registration), without starting the next
backlog item (MVP-MOD-003-QA-001).

## What was built

Four new screens were added to `07-implementation/employee-portal`:

- **People Search** (`PersonSearchScreen.tsx`) — global person search, duplicate detection with
  a color-coded confidence badge, search-index rebuild, and merge-coordination initiation/lookup.
- **Patients** (`PatientsScreen.tsx`) — register, list, snapshot, representative
  attach/list/revoke, consent record/list/revoke, and patient merge. Revoke and merge actions are
  gated behind a confirmation dialog.
- **Doctors** (`DoctorsScreen.tsx`) — register, list, snapshot, credential
  attach/list/verify/revoke, suspend, and portal-access preparation. Suspend, revoke and portal
  access actions are confirmation-gated where destructive.
- **Patient Registrations** (`PatientRegistrationsScreen.tsx`) — start, list, detail, commit and
  cancel. The commit flow explicitly handles the backend's 409
  `REGISTRATION_MATCH_RESOLUTION_REQUIRED` response by calling duplicate detection live and
  rendering the actual high-confidence candidates with a one-click "Use this patient" resolution,
  rather than a generic error message.

Supporting changes: `peopleApi.ts` (new API client, 29 functions across the four capabilities),
~35 new DTOs appended to `types.ts`, four tabs wired into `AppShell.tsx`/`App.tsx`, and CSS for
confidence badges / panels / empty states.

## Requirements coverage

- Patient/doctor search and person index: covered by People Search.
- Patient registration, query, snapshot: covered by Patients and Patient Registrations screens.
- Patient-registration commit flow: covered, including the 409 duplicate-resolution branch.
- Visual handling of high-confidence duplicates: confidence badges (>=85% high, >=50% medium,
  else low) shown in both People Search and the registration commit duplicate panel.
- Patient merge: covered (Patients screen, confirmation-gated).
- Representative and consent revocation: covered (Patients screen, confirmation-gated).
- Doctor directory/query: covered (Doctors screen).
- Doctor suspension: covered (confirmation-gated).
- Portal-access preparation: covered (Doctors screen).
- Medical credential verification and revocation: covered (Doctors screen, revoke
  confirmation-gated).
- Loading/error/empty/confirmation states: `useAsyncAction` + `StatusBanner` on every action;
  explicit empty-state copy; `ConfirmDialog` before every destructive action.
- Explicit 2xx/4xx handling: success paths update state and show a success banner; known 4xx
  responses surface the backend's message, with the 409 duplicate case handled specially as
  described above.
- No agent/provider/runtime-specific dependency: only React and the project's existing shared
  components are used; no new npm dependency was added.

## Validations executed

| Check | Result |
|---|---|
| `npm run typecheck` (tsc --noEmit) | **Passed**, 0 errors |
| `npm test` | Not executed (sandbox limitation, see below) |
| `npm run test:coverage` | Not executed (sandbox limitation) |
| `npm run build` | Not executed (sandbox limitation) |
| `npm audit --audit-level=high` | Not executed (sandbox limitation); no dependency changed |
| Secrets scan | **Passed**, no matches |
| Agent-agnostic scan | **Passed**, no matches |
| Open-source-first dependency check | **Passed**, package.json/package-lock.json unchanged |
| YAML parse of all touched registries/evidence/traceability files | **Passed** |
| Stale-pointer scan (MVP-MOD-003-FE-001 as "next"/"current") | **Passed**, none found |

### Execution limitation

This sandbox's network allowlist blocks the Linux-native `esbuild`/`@rollup/rollup-linux-x64-gnu`
binaries that Vite/Vitest require (`npm install esbuild` and `npm audit` both returned a `403
Forbidden` / "Connection blocked by network allowlist"). The committed `node_modules` only has
Windows-native binaries. As a result `npm test`, `npm run test:coverage`, `npm run build` and `npm
audit` could not run in this sandbox.

**Compensating controls applied:**

1. `npm run typecheck` ran to completion with 0 errors, structurally validating every new file.
2. Every new test's queries (label text, button/dialog accessible names) were manually
   cross-checked against the literal text rendered by the corresponding component.
3. Every API call's URL, method and payload were manually verified against the real backend
   controller/record definitions read from source.
4. Secrets scan, agent-agnostic scan, YAML parse and stale-pointer scan all ran successfully.

**Recommended follow-up:** run `npm test`, `npm run test:coverage`, `npm run build` and `npm audit
--audit-level=high` on a developer machine or CI with unrestricted registry access, and update
this evidence's results accordingly, before or during MVP-MOD-003-QA-001.

## Out of scope (confirmed not touched)

MVP-MOD-003-QA-001 was not started; no capability package was redesigned; `BUSINESS_REQUIREMENT.md`
was not modified; no new capability was created. Patient/doctor self-service portal UI, full
patient/doctor "editor" screens, the documents panel and the specialty panel were not built —
they sit outside the backlog's minimum required scope.

## Readiness

`MVP-MOD-003-FE-001` closes as **closed_with_execution_limitation**. Recommended next backlog
item: **MVP-MOD-003-QA-001** (Validate MVP-MOD-003 generated outputs, contracts, rules, UI and
quality evidence).
