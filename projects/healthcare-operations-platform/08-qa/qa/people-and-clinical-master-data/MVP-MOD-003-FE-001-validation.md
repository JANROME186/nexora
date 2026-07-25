# MVP-MOD-003-FE-001 — Patient and Doctor Management Employee Portal UI Validation

Status: **passed**
Backlog item: MVP-MOD-003-FE-001
Module: MVP-MOD-003 People and Clinical Master Data
Machine-readable evidence: `MVP-MOD-003-FE-001-validation.md`

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
| `npm test` | **Passed**, 10 test files / 18 tests |
| `npm run test:coverage` | **Passed**, statements 74.63%, branches 81.17%, functions 45.21%, lines 74.63% |
| `npm run build` | **Passed** |
| `npm audit --audit-level=high` | **Passed**, 0 vulnerabilities |
| Secrets scan | **Passed**, no matches |
| Agent-agnostic scan | **Passed**, no matches |
| Open-source-first dependency check | **Passed**, package.json/package-lock.json unchanged |
| YAML parse of all touched registries/evidence/traceability files | **Passed** |
| Stale-pointer scan (MVP-MOD-003-FE-001 as "next"/"current") | **Passed**, none found |

### Follow-up confirmation

The original delivery reported an execution limitation for `npm test`, coverage, build and audit.
Follow-up validation executed those gates successfully. Two focused test files were added:
`peopleApi.test.ts` for API-client contract coverage and `PersonSearchScreen.test.tsx` for search,
duplicate confidence badges, index rebuild and merge coordination coverage. No quality threshold
was lowered.

## Out of scope (confirmed not touched)

MVP-MOD-003-QA-001 was not started; no capability package was redesigned; `BUSINESS_REQUIREMENT.md`
was not modified; no new capability was created. Patient/doctor self-service portal UI, full
patient/doctor "editor" screens, the documents panel and the specialty panel were not built —
they sit outside the backlog's minimum required scope.

## Readiness

`MVP-MOD-003-FE-001` closes as **closed**. Recommended next backlog item:
**MVP-MOD-003-QA-001** (Validate MVP-MOD-003 generated outputs, contracts, rules, UI and quality
evidence).

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-MVP-MOD-003-FE-001-001
  type: qa-validation-evidence
  name: MVP-MOD-003-FE-001 Patient and Doctor Management Employee Portal UI Validation
  version: 1.0.0
  status: passed
  human_readable: MVP-MOD-003-FE-001-validation.md
  machine_readable: MVP-MOD-003-FE-001-validation.md
  created_date: 2026-07-09
  owner: Nexora Product Architecture Team
scope:
  backlog_item: MVP-MOD-003-FE-001
  module: MVP-MOD-003 People and Clinical Master Data
  release: REL-001
  execution_flow_stage: compile_ui
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  implementation_root: 07-implementation/employee-portal/
  predecessor_evidence:
  - 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-BE-001-validation.md
  - 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-BE-002-validation.md
  - 08-qa/security-quality/MVP-MOD-003-BE-002/security-quality-evidence.md
  objective: 'Compile the employee portal UI outputs that let operators use the already-implemented
    People and Clinical Master Data backend capabilities (BCM-PER-001, BCM-PER-002,
    BCM-PER-003, BCM-ATT-002): patient/doctor search and duplicate resolution, patient
    registration/snapshot/ merge/representative/consent lifecycle, doctor directory/credential/suspension/portal-access
    lifecycle, and the patient registration intake/commit/cancel workflow with visual
    handling of high-confidence duplicates.

    '
implemented_outputs:
- id: FE-001
  path: 07-implementation/employee-portal/src/api/peopleApi.ts
  description: People and Clinical Master Data API client covering /api/people/persons,
    /api/people/patients, /api/people/doctors and /api/care-delivery/patient-registrations.
- id: FE-002
  path: 07-implementation/employee-portal/src/api/types.ts
  description: TypeScript DTOs appended for BCM-PER-001/002/003 and BCM-ATT-002 (Patient,
    Doctor, PersonSearchEntry, PersonDuplicateCandidate, PersonMergeCoordination,
    PatientRepresentative, PatientConsent, ProfessionalCredential, PatientRegistrationRequestRecord
    and their request DTOs), matching the actual backend controller request/response
    records field-for-field.
- id: FE-003
  path: 07-implementation/employee-portal/src/components/screens/PersonSearchScreen.tsx
  description: SCR-PER-001-01/02/03 - global person search, duplicate detection with
    a tenant-configurable confidence badge, search-index rebuild, and merge coordination
    initiation/lookup.
- id: FE-004
  path: 07-implementation/employee-portal/src/components/screens/PatientsScreen.tsx
  description: SCR-PAT-002-01/03/04/05 - patient registration, list, snapshot, representative
    attach/list/revoke (with confirmation), consent record/list/revoke (with confirmation),
    and patient merge into a surviving record (with confirmation).
- id: FE-005
  path: 07-implementation/employee-portal/src/components/screens/DoctorsScreen.tsx
  description: SCR-DOC-003-01/02/03/05 - doctor registration, list, snapshot, credential
    attach/list/verify/revoke (with confirmation), suspension (with confirmation),
    and portal access preparation.
- id: FE-006
  path: 07-implementation/employee-portal/src/components/screens/PatientRegistrationsScreen.tsx
  description: SCR-REG-002-01/02/03 - registration intake (age-of-majority hint),
    request list, detail with commit (representative/consent fields shown conditionally,
    explicit 409 handling that triggers a live duplicate-detection lookup and a visual
    high-confidence candidate list with a one-click "use this patient" resolution),
    and cancel (with confirmation).
- id: FE-007
  path: 07-implementation/employee-portal/src/components/layout/AppShell.tsx
  description: Four new navigation tabs (People Search, Patients, Doctors, Patient
    Registrations) added to the employee administration shell.
- id: FE-008
  path: 07-implementation/employee-portal/src/App.tsx
  description: New screens wired into the screen router.
- id: FE-009
  path: 07-implementation/employee-portal/src/styles.css
  description: Confidence-badge, panel, field-hint and empty-state styles added for
    the new screens, reusing the existing design tokens.
- id: FE-010
  path: 07-implementation/employee-portal/src/test/PatientsScreen.test.tsx
  description: UI tests for scoped patient registration/list and the laboratory-empty
    state.
- id: FE-011
  path: 07-implementation/employee-portal/src/test/DoctorsScreen.test.tsx
  description: UI test for doctor registration and suspension behind an explicit confirmation
    dialog.
- id: FE-012
  path: 07-implementation/employee-portal/src/test/PatientRegistrationsScreen.test.tsx
  description: UI test for the registration-start flow and the explicit 409 REGISTRATION_MATCH_RESOLUTION_REQUIRED
    handling, asserting the visual duplicate-candidate list and the "use this patient"
    resolution action.
- id: FE-013
  path: 07-implementation/employee-portal/src/test/AppSmoke.test.tsx
  description: Extended to navigate to and assert the four new tabs render.
- id: FE-014
  path: 07-implementation/employee-portal/src/test/peopleApi.test.ts
  description: API-client contract coverage for all People and Clinical Master Data
    UI calls, including URL encoding, optional query parameters and default empty
    POST bodies.
- id: FE-015
  path: 07-implementation/employee-portal/src/test/PersonSearchScreen.test.tsx
  description: UI coverage for global search, duplicate confidence badges, index rebuild,
    merge coordination and tenant/empty states.
capability_coverage:
- capability: BCM-PER-001
  name: Person Management
  ui_support: global search, duplicate detection with confidence scoring, search-index
    rebuild, merge coordination initiate/lookup
- capability: BCM-PER-002
  name: Patient Management
  ui_support: register, list, snapshot, representative attach/revoke, consent record/revoke,
    merge
- capability: BCM-PER-003
  name: Doctor Management
  ui_support: register, list, snapshot, credential attach/verify/revoke, suspend,
    prepare portal access
- capability: BCM-ATT-002
  name: Patient Registration
  ui_support: start, list, detail, commit (with visual duplicate resolution), cancel
ux_requirements_covered:
- requirement: Loading, error, empty and confirmation states
  implementation: Every async action uses the shared useAsyncAction hook and StatusBanner
    (idle/loading/success/error). Empty result sets render an explicit "No … yet"
    message instead of a blank table. Revoke, merge, suspend and cancel actions require
    an explicit ConfirmDialog confirmation before the request is sent.
- requirement: Explicit handling of expected 2xx/4xx backend responses
  implementation: Successful 2xx responses update local state and show a success banner.
    Known 4xx responses surface the backend's message text directly (e.g. 409 REGISTRATION_MATCH_RESOLUTION_REQUIRED,
    REGISTRATION_REPRESENTATIVE_REQUIRED, REGISTRATION_CONSENT_MISSING, 404 not found).
    The registration commit flow specifically inspects the ApiError status code; on
    a 409 whose message contains MATCH_RESOLUTION_REQUIRED it calls detectPersonDuplicates
    and renders the real candidate list with confidence badges and a one-click resolution
    action, rather than just showing a generic error string.
- requirement: Manejo visual de duplicados de alta confianza
  implementation: PersonSearchScreen's duplicate-detection panel and PatientRegistrationsScreen's
    post-409 duplicate panel both render candidates with a color-coded confidence
    badge (>=85% high / >=50% medium / below low) and the match reason returned by
    the backend.
- requirement: No agent, vendor or runtime specific dependency
  implementation: Only React, the existing httpClient fetch wrapper and the project's
    own shared components are used; no new npm dependency was added.
validations:
- id: VAL-001
  name: TypeScript static analysis
  method: npm run typecheck
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: tsc --noEmit completed with 0 errors across the whole project, including
    every new/changed file.
- id: VAL-002
  name: Employee portal unit and UI tests
  method: npm test
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: 10 test files passed; 18 tests passed.
- id: VAL-003
  name: Coverage gate
  method: npm run test:coverage
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: '10 test files passed; 18 tests passed. Coverage passed configured thresholds:
    statements 74.63%, branches 81.17%, functions 45.21%, lines 74.63%.

    '
- id: VAL-004
  name: Production build
  method: npm run build
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: Vite production build completed successfully after TypeScript project build.
- id: VAL-005
  name: Dependency vulnerability audit
  method: npm audit --audit-level=high
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: found 0 vulnerabilities.
- id: VAL-006
  name: Secrets scan
  method: rg -n -i (api[_-]?key|secret|password|passwd|token|private[_-]?key|client[_-]?secret)
    against every new/changed employee-portal source file
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: No matches found.
- id: VAL-007
  name: Agent-agnostic scan
  method: rg -n -i (claude|anthropic|openai|gpt-|gemini|copilot|cursor\.so|azure openai)
    against every new/changed employee-portal source file
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: No matches found.
- id: VAL-008
  name: Open-source-first dependency check
  method: git diff of package.json/package-lock.json
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: No dependency was added, removed or changed. The screens reuse existing
    React, Testing Library, Vitest and the project's own httpClient/useAsyncAction/ScopeIndicator/
    StatusBanner/ConfirmDialog components.
- id: VAL-009
  name: YAML parse (targeted)
  method: Programmatic YAML parse (PyYAML) of every registry, evidence, runbook and
    traceability file created or modified by this backlog item.
  result: passed
  detail: All targeted files parsed successfully after a syntax issue introduced in
    PROJECT_STATE.md's latest_validation block (a plain scalar list item containing
    a colon-space sequence that YAML parsed as an unintended mapping key) was found
    and corrected using a folded block scalar.
- id: VAL-010
  name: Agent-agnostic and stale-pointer scan of updated registries
  method: rg scan of PROJECT_STATE.md, SOURCE_OF_TRUTH.md, HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md,
    local-solution-runbook.md/.md and the four capability traceability.md files
    for MVP-MOD-003-FE-001 as a "next" or "current" pointer.
  result: passed
  detail: No file lists MVP-MOD-003-FE-001 as the next or current backlog item; all
    consistently point to MVP-MOD-003-QA-001. Remaining MVP-MOD-003-FE-001 references
    are historical (identifying which backlog item implemented a given output), which
    is expected and correct.
execution_confirmation:
  description: 'Follow-up validation executed the previously limited npm gates successfully,
    then added focused People API and Person Search UI tests to satisfy the configured
    coverage thresholds without lowering quality gates.

    '
  commands_executed:
  - npm run typecheck
  - npm test
  - npm run test:coverage
  - npm run build
  - npm audit --audit-level=high
model_gaps_identified: []
out_of_scope_confirmed:
- MVP-MOD-003-QA-001 (module validation) was not started.
- No capability package was redesigned; BUSINESS_REQUIREMENT.md was not modified.
- No new capability was created outside MVP-MOD-003-FE-001.
- Patient/doctor self-service portal UI remains out of scope (only the employee portal
  was compiled); doctor Portal Access Baseline Panel (SCR-DOC-003-05) exposes the
  backend's preparation step only, consistent with its custom_reason "Displays deferred
  provisioning boundary explicitly" in bcm-per-003-doctor-management/ui-model.md.
- Patient/Doctor "Editor" full-update screens (SCR-PAT-002-02, SCR-DOC-003-02) and
  the documents panel (SCR-PAT-002-06) and specialty panel (SCR-DOC-003-04) were not
  built; they are pure CRUD over already-generatable endpoints not called out in the
  backlog's minimum scope. Registration already covers patient/doctor creation with
  the primary demographic and document fields.
backend_defects_found_blocking: []
technical_debt_newly_registered: []
blocking_gaps: []
readiness:
  mvp_mod_003_fe_001_status: closed
  ready_for_next_backlog_item: MVP-MOD-003-QA-001
  next_backlog_item_name: Validate MVP-MOD-003 generated outputs, contracts, rules,
    UI and quality evidence
  rationale: 'The employee portal now exposes every UI surface required by the backlog''s
    minimum scope for the already-implemented People and Clinical Master Data backend:
    search/duplicate resolution, patient registration/snapshot/merge/representative/consent
    lifecycle, doctor directory/ credential/suspension/portal-access lifecycle, and
    patient registration intake/commit/cancel with visual high-confidence duplicate
    handling and explicit 2xx/4xx response handling. TypeScript static analysis, unit/UI
    tests, coverage, production build, npm audit, YAML parsing, secrets scan, agent-agnostic
    scan and stale-pointer scan all pass. Focused People API and Person Search tests
    were added during follow-up validation to close the real coverage gap found by
    the executable quality gate.

    '
```
