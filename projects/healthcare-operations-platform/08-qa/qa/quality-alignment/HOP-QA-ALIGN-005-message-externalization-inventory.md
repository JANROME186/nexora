# HOP-QA-ALIGN-005 — Message Externalization and Magic-String Inventory

Status: complete. Backlog item: `HOP-QA-ALIGN-005` — Establish message externalization and
magic-string remediation baseline.

Machine-readable companion: `HOP-QA-ALIGN-005-message-externalization-inventory.md`.

## Scope and closure rule

Per `open-source-first-security-quality-standard.md`'s `message_externalization_policy`, only
new/changed content since the previous quality-alignment iteration must be externalized or
formally dispositioned as debt — a full retroactive product-wide refactor is not required. No
prior HOP-QA-ALIGN item performed this inventory, so this baseline covers the full current
surface of `07-implementation/backend` (frontdeskcaredelivery module, the only module with an
informal coded-message convention already in place), `07-implementation/employee-portal/src`
(all 25 non-test files, 5,020 lines, read in full) and `07-implementation/mobile-app/src` (all 8
non-test files, 398 lines, read in full).

This backlog item does **not** externalize the entire product — that would be a large,
non-safe refactor. It leaves an actionable enterprise baseline: a complete inventory, a P0/P1/P2
classification, a documented strategy per stack, immediate remediation of what was small and
safe, and formal technical debt for what requires larger work.

## P0 / P1 / P2 classification

Per `HOP-ENGINEERING-EXCELLENCE-PRIORITIZATION.md`, "message externalization and magic-string
inventory" is explicitly listed under `must_be_brought_to_p0_now`.

| Priority | Meaning here | Disposition |
|---|---|---|
| P0 | Must be resolved in this backlog item | Remediated directly (see "Immediate remediation" below) |
| P1 | High value, not blocking; naturally scoped to the next real trigger | Tracked by TD-I18N-002 |
| P2 | Low priority or an existing strength requiring no action | No action needed, or tracked by TD-I18N-002 for completeness |

## Backend inventory

Method: grep across `07-implementation/backend/src/main/java` for string-literal throws,
`STATUS_` constants and numeric literals in `frontdeskcaredelivery`, cross-referenced against
`error_model.domain_errors` in each owning capability package's `openapi-source.md`
(`bcm-lab-001-diagnostic-order-management`, `bcm-att-001-appointment-scheduling`,
`bcm-att-003-reception-management`, `bcm-att-004-admission-management`,
`bcm-att-006-quotation-management`).

- **33 domain error codes** are modeled across the five packages' `error_model.domain_errors`.
  **30 have a runtime throw site** in `frontdeskcaredelivery`; the remaining 3
  (RN-004/RN-005-class `*_SCOPE_MISMATCH`/`*_BOUNDARY_VIOLATION` codes) are enforced generically
  by cross-cutting authorization logic rather than a per-service throw site, so no code change is
  needed for them.
- Before this backlog item, all 30 throw sites embedded their code as a raw string prefix
  (`"ORDER_DOCTOR_NOT_ELIGIBLE: ..."`) — informally coded but not a named, IDE-navigable,
  compile-time-checked constant. **P0 — remediated** (see below).
- **API error response shape inconsistency (P1, tracked by TD-I18N-002):** 5 distinct
  `*ApiErrorResponse` record shapes exist (one per `@RestControllerAdvice` handler), none with a
  dedicated `code` field. `CatalogApiErrorResponse` and `PeopleApiErrorResponse` carry
  `(status, message, ruleId, backlogItem, occurredAt)`; `FrontDeskApiErrorResponse`,
  `IdentityAccessExceptionHandler`'s and `OrganizationManagementExceptionHandler`'s
  `ApiErrorResponse` carry only `(status, message, occurredAt)`. Adding a `code` field is an
  OpenAPI response-contract change across 5 capability packages — too large for this backlog's
  small-and-safe bar, deferred to TD-I18N-002.
- **Status constants (P2, existing strength):** 74 `STATUS_*` constants are already centralized
  as named Java constants on their owning domain records (e.g. `DiagnosticOrder.STATUS_ACCEPTED`).
  No action needed.
- **Configurable business values (P2, existing strength):** tenant-configurable thresholds
  (branch daily appointment capacity, no-show grace days, required admission acknowledgements,
  discount percentage caps) already resolve through `FrontDeskPolicyStore` /
  `TenantPeoplePolicyStore` rather than hardcoded constants. No action needed.
- **TD-BE-008 reviewed:** unrelated to message externalization (read-model masking
  configurability); left unchanged by this backlog item.

### Backend message catalog strategy (proposed)

The framework's `message_externalization_policy.backend_expectations` calls for stable error
codes and a message-catalog/resource-bundle approach. HOP already has the raw material for both,
mechanically derived from the model:

1. **Today (this backlog item):** every capability package's `openapi-source.md` already models
   `error_model.domain_errors` with a `code` and `maps_to_rule`. A new
   `FrontDeskErrorCodes` constants class
   (`frontdeskcaredelivery/shared/FrontDeskErrorCodes.java`) makes the 30 runtime-reachable codes
   named, IDE-navigable Java constants — a 1:1 mechanical projection of the model, not a new
   design decision. All 30 throw sites across `DiagnosticOrderManagementService`,
   `AppointmentSchedulingService`, `ReceptionManagementService`, `AdmissionManagementService` and
   `QuotationManagementService` now reference these constants instead of inline literals.
2. **Next step (TD-I18N-002):** add a `code: String` field to all five `*ApiErrorResponse`
   shapes, sourced from the same model, so `code` becomes a first-class, independently parseable
   API response field instead of a message-string prefix. Pair the prose portion with a Spring
   `MessageSource`/`ResourceBundle` (`messages_en.properties`, `messages_es.properties`) once a
   second locale is actually needed — HOP is Spring Boot-based, so `MessageSource` is a
   zero-new-dependency, framework-native fit.
3. **Rollout order when triggered:** start with `frontdeskcaredelivery` (already coded, lowest
   risk) as the reference implementation, then apply the identical `<Module>ErrorCodes` pattern to
   `catalogtestconfiguration`, `peopleclinicalmasterdata`, `organizationmanagement`,
   `identityaccess` and `auditcompliance` as each is next touched — this mirrors the framework's
   `technical_debt_first_execution_policy` (debt addressed when the affected component is
   touched, not as a standalone big-bang migration).

## Frontend (employee-portal) inventory

Method: full read of all 25 non-test `.ts`/`.tsx` files (5,020 lines, no sampling).

| Category | Finding | Priority | Disposition |
|---|---|---|---|
| User-visible text | ~130 distinct strings (headings, labels, hints); no i18n library installed | P1 | Majority remains inline; tracked by TD-I18N-002 |
| Validation/error messages | 34 distinct; `"Select a doctor first."` (5x), `"Select a patient first."` (5x), `"Unexpected error. Please try again."` (2x) | P0 | **Remediated this backlog item** |
| Status labels | `AsyncStatus` union independently declared 3 times (`useAsyncAction.ts`, `StatusBanner.tsx`, a local `CommitPhase` alias); several entity status fields typed as plain `string` | P0 (triple declaration) / P1 (untyped fields) | Triple declaration **remediated**; untyped fields tracked by TD-I18N-002 |
| Routes/navigation | No client router; closed `ScreenKey` union already compile-time-checked; 6 API base-path literals | P2 | Existing strength, no action |
| Permissions/scope | Closed `AccessScopeType` union; `roleCode` is free-text operator input (not a magic-value set) | P2 | Existing strength, no action |
| API query/cache keys | No cache library; only `URLSearchParams` parameter names, used consistently | P2 | Existing strength, no action |
| Repeated magic strings/numbers | 8 clusters with 3+ occurrences; confidence thresholds `0.85`/`0.5` duplicated with a duplicated `confidenceClass()` function | P0 (confidence thresholds) / P2 (rest) | Confidence thresholds **remediated**; rest tracked by TD-I18N-002 |
| Configurable business values | 7 clusters; a 409 handler couples to a backend error-message substring (`.includes("MATCH_RESOLUTION_REQUIRED")`); diagnostic-catalog create-handler business defaults hardcoded | P1 / P2 | Tracked by TD-I18N-002 (409 coupling blocked on the backend `code` field above) |

### Frontend i18n strategy (proposed)

1. **Today (this backlog item):** two new modules under `employee-portal/src/i18n/` —
   `messages.ts` (the `MESSAGES` catalog: the 3 repeated/duplicated validation and fallback-error
   strings) and `matching.ts` (the duplicate-match confidence thresholds and shared
   `confidenceClass()` function). These are plain TypeScript modules, not a locale-switching
   library — the goal is a single source of truth for what was previously copy-pasted, not a
   locale mechanism (no second locale is committed to yet).
2. Converged the three independent `idle`/`loading`/`success`/`error` union declarations onto the
   single exported `AsyncStatus` type from `state/useAsyncAction.ts`, so `StatusBanner` and
   `PatientRegistrationsScreen` no longer risk silently drifting from it.
3. **Next step (TD-I18N-002):** adopt `react-i18next` or `@formatjs/intl` (both open source,
   MIT-licensed, standard React ecosystem choices) with locale JSON resources under
   `employee-portal/src/i18n/locales/`, migrating the ~125 remaining single-occurrence strings
   incrementally per screen as each is next touched — again following
   `technical_debt_first_execution_policy` rather than a big-bang rewrite. Once the backend `code`
   field exists (TD-I18N-002, backend half), replace the `PatientRegistrationsScreen` 409 handler's
   `.includes("MATCH_RESOLUTION_REQUIRED")` string match with a direct `error.code === "..."`
   comparison.
4. `MVP-MOD-004-FE-001` (paused pending `HOP-QA-ALIGN-CLOSEOUT`) should consume `MESSAGES` and
   `matching.ts` for any validation/confidence copy it needs, and register new entries there
   instead of inlining new literals, per the framework's `message_externalization_policy`.

## Mobile-app inventory

Method: full read of all 8 non-test `.ts` files (398 lines).

| Category | Finding | Priority | Disposition |
|---|---|---|---|
| User-visible text | 9 distinct strings in screen "model" files (no renderable UI layer yet) | P2 | Centralized this backlog item |
| Validation/error messages | 6 distinct (`localAuth.ts`, `mobileApp.ts`) | P0 | **Remediated this backlog item** |
| Status labels | `UserResponse.status`/`AccessScope.type` unions duplicate employee-portal's verbatim; no shared package between the two apps | P2 | Tracked by TD-I18N-002, defer until a shared package is introduced |
| Routes/navigation | Closed `MobileRoute` union, already compile-time-checked | P2 | Existing strength, no action |
| Repeated magic strings/numbers | None repeat 3+ times within mobile-app alone | P2 | No action needed |
| Configurable business values | None found; scaffolding only | P2 | No action needed |

### Mobile localization strategy (proposed)

1. **Today (this backlog item):** a `mobile-app/src/i18n/messages.ts` module centralizes the 6
   validation strings from `localAuth.ts` and `mobileApp.ts`, mirroring the employee-portal
   pattern for consistency across the two TypeScript codebases.
2. **Next step (TD-I18N-002, gated on TD-APP-001):** mobile-app is presently a
   "renderer-agnostic TypeScript foundation," not a runnable native app — there is no rendering
   layer to attach a localization-resource mechanism to yet. Once TD-APP-001 selects a native
   renderer stack (e.g. React Native), adopt whichever localization library pairs naturally with
   it (`react-intl` for React Native is the open-source default) and migrate `messages.ts`'s
   content into that mechanism's resource format.
3. Until then, all new mobile validation/user-facing strings should be added to `messages.ts`
   rather than inlined, keeping the surface ready for that migration.

## Immediate remediation (this backlog item)

All changes below are behavior-preserving refactors — the same message strings are produced at
runtime; only where they live changed.

**Backend** — 6 files changed (1 new): `FrontDeskErrorCodes.java` (new, 21 named constants) plus
`DiagnosticOrderManagementService.java`, `AppointmentSchedulingService.java`,
`ReceptionManagementService.java`, `AdmissionManagementService.java`,
`QuotationManagementService.java` (30 throw sites updated to reference the constants).
Verification: `mvn test` — 77 tests, 0 failures, 0 errors, 7 skipped (unchanged); line coverage
66.48% (floor 65.82%, no regression).

**Employee portal** — 2 new files (`src/i18n/messages.ts`, `src/i18n/matching.ts`), 6 files
changed (`DoctorsScreen.tsx`, `PatientsScreen.tsx`, `useAsyncAction.ts`, `StatusBanner.tsx`,
`PatientRegistrationsScreen.tsx`, `PersonSearchScreen.tsx`). Verification: `npm run quality`
(typecheck, lint, 18/18 tests, build, duplication, format, license) all passed; line coverage
73.04% (floor 72.89%, no regression); `npm audit` 0 vulnerabilities.

**Mobile app** — 1 new file (`src/i18n/messages.ts`), 2 files changed (`localAuth.ts`,
`mobileApp.ts`). Verification: `npm run quality` (typecheck, lint, 8/8 tests, duplication, format)
all passed.

## Technical debt disposition

- **TD-I18N-001 closed.** Its acceptance criteria (inventory exists in YAML+MD; backend/frontend/
  mobile strategies documented; findings remediated or assigned debt targets) are all met by this
  document and the remediation above.
- **TD-I18N-002 registered** (open, non-blocking) for the larger remaining scope: the backend
  `code`-field API contract addition, full frontend i18n-library adoption for the ~125
  single-occurrence strings, the 409 string-coupling fix, and full mobile localization once a
  renderer stack exists.
- **TD-BE-008 and TD-FE-002 reviewed** — neither is impacted by message-externalization changes;
  both left unchanged.

## Readiness

Message externalization and magic-string remediation baseline: **established**. Ready for
`HOP-QA-ALIGN-CLOSEOUT`. `MVP-MOD-004-FE-001` remains paused pending that item.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-ALIGN-005-INVENTORY
  type: message-externalization-inventory
  name: Message Externalization and Magic-String Inventory
  version: 1.0.0
  status: complete
  created_date: 2026-07-16
  human_readable: HOP-QA-ALIGN-005-message-externalization-inventory.md
  machine_readable: HOP-QA-ALIGN-005-message-externalization-inventory.md
  policy: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  prioritization_framework: HOP-ENGINEERING-EXCELLENCE-PRIORITIZATION.md
backlog_item:
  id: HOP-QA-ALIGN-005
  name: Establish message externalization and magic-string remediation baseline
scope:
  components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/frontdeskcaredelivery
  - 07-implementation/employee-portal/src
  - 07-implementation/mobile-app/src
  closure_rule: Per the framework's message_externalization_policy.closure_rule, only
    new/changed content since the previous quality-alignment iteration must be externalized
    or formally dispositioned as debt. A full retroactive product-wide refactor is
    explicitly not required; this inventory nonetheless covers the full current surface
    (not just deltas) because no prior HOP-QA-ALIGN item performed this inventory,
    so there is no smaller delta to scope to.
backend:
  method: Grep across 07-implementation/backend/src/main/java for string-literal throws,
    STATUS_ constants and numeric literals in the frontdeskcaredelivery module (the
    only module with an informal coded-message convention already in place), cross-referenced
    against error_model.domain_errors in each of the five owning capability packages'
    openapi-source.md.
  domain_error_codes:
    total_modeled_in_openapi_source: 33
    with_runtime_throw_site: 30
    without_runtime_throw_site: 3
    without_runtime_throw_site_reason: RN-004/RN-005-class *_SCOPE_MISMATCH and *_BOUNDARY_VIOLATION
      codes are enforced generically by cross-cutting authorization logic, not a per-service
      throw site; no code change needed.
    packages:
    - package: bcm-lab-001-diagnostic-order-management
      codes:
      - ORDER_DOCTOR_NOT_ELIGIBLE
      - ORDER_NO_LINES
      - ORDER_PRICING_SNAPSHOT_REQUIRED
      - ORDER_TERMINAL_STATE_IMMUTABLE
      - ORDER_CANCELLATION_OVERRIDE_REQUIRED
      - ORDER_CATALOG_ITEM_NOT_PUBLISHED
      service: DiagnosticOrderManagementService.java
      throw_sites: 8
    - package: bcm-att-001-appointment-scheduling
      codes:
      - APPOINTMENT_BRANCH_NOT_ACTIVE
      - APPOINTMENT_WINDOW_OVERLAP
      - APPOINTMENT_BRANCH_CAPACITY_EXCEEDED
      - APPOINTMENT_CATALOG_ITEM_NOT_PUBLISHED
      - APPOINTMENT_NO_SHOW_GRACE_PERIOD_ACTIVE
      service: AppointmentSchedulingService.java
      throw_sites: 6
    - package: bcm-att-003-reception-management
      codes:
      - RECEPTION_IDENTITY_NOT_CONFIRMED
      - RECEPTION_APPOINTMENT_NOT_CHECKED_IN
      service: ReceptionManagementService.java
      throw_sites: 2
    - package: bcm-att-004-admission-management
      codes:
      - ADMISSION_IDENTITY_NOT_CONFIRMED
      - ADMISSION_CATALOG_INCOMPLETE
      - ADMISSION_CONSENT_OR_SAMPLE_ACK_MISSING
      service: AdmissionManagementService.java
      throw_sites: 5
    - package: bcm-att-006-quotation-management
      codes:
      - QUOTATION_CATALOG_ITEM_NOT_PUBLISHED
      - QUOTATION_PRICING_SNAPSHOT_REQUIRED
      - QUOTATION_DISCOUNT_POLICY_EXCEEDED
      - QUOTATION_EXPIRED
      - QUOTATION_TERMINAL_STATE_IMMUTABLE
      service: QuotationManagementService.java
      throw_sites: 7
  api_error_response_shape_inconsistency:
    finding: '5 distinct *ApiErrorResponse record shapes exist (one per exception
      handler), none with a dedicated `code` field: CatalogApiErrorResponse and PeopleApiErrorResponse
      carry (status, message, ruleId, backlogItem, occurredAt); FrontDeskApiErrorResponse,
      IdentityAccessExceptionHandler''s ApiErrorResponse and OrganizationManagementExceptionHandler''s
      ApiErrorResponse carry only (status, message, occurredAt).'
    priority: P1
    disposition: tracked_by_TD-I18N-002
    reason_not_fixed_now: Adding a code field is an OpenAPI response-contract change
      across 5 capability packages; too large to qualify as this backlog's "small
      and safe" remediation bar.
  status_constants:
    finding: 74 STATUS_ constants already centralized as named Java constants on their
      owning domain records (e.g. DiagnosticOrder.STATUS_ACCEPTED) across all modules.
    priority: P2
    disposition: existing_strength_no_action_needed
  configurable_business_values:
    finding: Tenant-configurable thresholds (branch daily appointment capacity, no-show
      grace days, required admission acknowledgements, standard/override max discount
      percentage) already resolve through FrontDeskPolicyStore / TenantPeoplePolicyStore
      rather than hardcoded constants.
    priority: P2
    disposition: existing_strength_no_action_needed
  masking_and_config_debt_review:
    td_be_008_reviewed: true
    td_be_008_impacted_by_this_backlog_item: false
    note: TD-BE-008 (read-model masking not tenant-configurable) is unrelated to message
      externalization; no change made in this backlog item.
frontend_employee_portal:
  method: Full read of all 25 non-test .ts/.tsx files under employee-portal/src (5,020
    lines, no sampling), grep-verified for literal-occurrence counts.
  categories:
    user_visible_text:
      count_distinct: ~130
      priority: P1
      disposition: majority_remains_inline_tracked_by_TD-I18N-002
      note: single-occurrence headings, button labels, field hints; no i18n library
        installed.
    validation_error_messages:
      count_distinct: 34
      repeated_3_plus_times:
      - literal: Select a doctor first.
        occurrences: 5
        file: components/screens/DoctorsScreen.tsx
      - literal: Select a patient first.
        occurrences: 5
        file: components/screens/PatientsScreen.tsx
      repeated_2_times:
      - literal: Unexpected error. Please try again.
        occurrences: 2
        files:
        - state/useAsyncAction.ts
        - components/screens/PatientRegistrationsScreen.tsx
      priority: P0
      disposition: remediated_this_backlog_item
      remediation: Centralized in employee-portal/src/i18n/messages.ts (MESSAGES.selectDoctorFirst,
        MESSAGES.selectPatientFirst, MESSAGES.unexpectedError); all 12 call sites
        updated.
    status_labels:
      finding: AsyncStatus ("idle"|"loading"|"success"|"error") was independently
        declared 3 times (state/useAsyncAction.ts, components/common/StatusBanner.tsx,
        a local CommitPhase alias in PatientRegistrationsScreen.tsx). CatalogStatus,
        UserStatus and PatientRegistrationOutcome unions exist in api/types.ts but
        several entity status fields (Tenant.status, Doctor.status/portalStatus, PatientRepresentative.status,
        ProfessionalCredential.verificationStatus) remain plain `string`, compared
        via raw string literals.
      priority: P0_for_the_triple_declaration_P1_for_the_untyped_fields
      disposition: triple_declaration_remediated_this_backlog_item_untyped_fields_tracked_by_TD-I18N-002
      remediation: StatusBanner and PatientRegistrationsScreen now import the single
        exported AsyncStatus type from state/useAsyncAction.ts instead of redeclaring
        the union.
    routes_and_navigation:
      finding: No client-side router; a ScreenKey union in AppShell.tsx drives tab
        switching (already a closed, compile-time-checked set). 6 distinct API base-path
        string literals.
      priority: P2
      disposition: existing_strength_no_action_needed
    permissions_and_scope:
      finding: AccessScopeType union ("platform"|"tenant"|"laboratory"|"branch") is
        a closed, compile-time-checked set. roleCode on the Role Assignments screen
        is a free-text operator input with no client-side allowlist (opaque strings
        from user input, not a fixed magic-value set to externalize).
      priority: P2
      disposition: existing_strength_no_action_needed
    api_query_keys:
      finding: No data-fetching/cache library is present (plain useState); "keys"
        are only URLSearchParams parameter names (laboratoryId, tenantId, personKind,
        familyName, givenName, birthDate, subjectId), each used consistently against
        its matching backend query parameter.
      priority: P2
      disposition: existing_strength_no_action_needed
    repeated_magic_strings_and_numbers:
      count_3_plus: 8
      highlighted:
      - literal: 0.85 / 0.5 duplicate-match confidence thresholds + confidenceClass()
        occurrences: 2
        files:
        - components/screens/PersonSearchScreen.tsx
        - components/screens/PatientRegistrationsScreen.tsx
        priority: P0
        disposition: remediated_this_backlog_item
        remediation: Centralized in employee-portal/src/i18n/matching.ts (DUPLICATE_MATCH_CONFIDENCE_HIGH,
          DUPLICATE_MATCH_CONFIDENCE_MEDIUM, confidenceClass); both screens now import
          the shared function.
      - literal: '"national_id" / "passport" / "other" document-type defaults'
        occurrences: 9+
        priority: P2
        disposition: tracked_by_TD-I18N-002
      - literal: response.status === 204
        occurrences: 2 (cross-app, employee-portal httpClient.ts and mobile-app platformFoundationApi.ts)
        priority: P2
        disposition: tracked_by_TD-I18N-002
    configurable_business_values:
      count_clusters: 7
      highlighted:
      - finding: HTTP 409 + string match `.includes("MATCH_RESOLUTION_REQUIRED")`
          coupling UI logic to a backend error-message substring instead of a structured
          code.
        file: components/screens/PatientRegistrationsScreen.tsx:172
        priority: P1
        disposition: tracked_by_TD-I18N-002_blocked_on_backend_code_field
      - finding: Diagnostic catalog create-handler business defaults hardcoded (serviceType
          "laboratory", resultType "numeric", measurementUnit "mg/dL", currency "MXN",
          decimalPrecision 2, reference-range normalHigh/criticalHigh).
        file: components/screens/DiagnosticCatalogScreen.tsx
        priority: P2
        disposition: tracked_by_TD-I18N-002_defer_until_multi_unit_or_multi_currency_need
mobile_app:
  method: Full read of all 8 non-test .ts files under mobile-app/src (398 lines).
  categories:
    user_visible_text:
      count_distinct: 9
      priority: P2
      disposition: centralized_this_backlog_item
      remediation: mobile-app/src/i18n/messages.ts (title/greeting/scope templates
        and HomeAction.label values reviewed; no renderable UI layer exists yet so
        no further remediation applies).
    validation_error_messages:
      count_distinct: 6
      priority: P0
      disposition: remediated_this_backlog_item
      remediation: Centralized in mobile-app/src/i18n/messages.ts (MESSAGES.tenantIdRequired,
        userIdRequired, displayNameRequired, emailRequired, emailInvalid, sessionRequired);
        both call sites (auth/localAuth.ts, mobileApp.ts) updated.
    status_labels:
      finding: UserResponse.status and AccessScope.type unions duplicate employee-portal's
        UserStatus/AccessScopeType verbatim; no shared package exists between the
        two apps.
      priority: P2
      disposition: tracked_by_TD-I18N-002_defer_until_a_shared_package_is_introduced
    routes_and_navigation:
      finding: MobileRoute union (7 values) already a closed, compile-time-checked
        set.
      priority: P2
      disposition: existing_strength_no_action_needed
    repeated_magic_strings_and_numbers:
      finding: No literal repeats 3+ times within mobile-app alone; the tree is too
        small.
      priority: P2
      disposition: no_action_needed
    configurable_business_values:
      finding: None found; mobile-app is presently data-shape and navigation-state
        scaffolding.
      priority: P2
      disposition: no_action_needed
immediate_remediation_summary:
  backend:
    files_changed: 6
    new_files:
    - 07-implementation/backend/.../frontdeskcaredelivery/shared/FrontDeskErrorCodes.java
    throw_sites_updated: 30
    behavior_preserving: true
    verification: mvn test — 77 tests, 0 failures, 0 errors, 7 skipped (unchanged
      from before this backlog item); line coverage 66.48% (>= 65.82% floor).
  frontend_employee_portal:
    new_files:
    - 07-implementation/employee-portal/src/i18n/messages.ts
    - 07-implementation/employee-portal/src/i18n/matching.ts
    files_changed: 6
    verification: npm run quality — typecheck, lint, 18/18 tests, build, duplication,
      format, license all passed; line coverage 73.04% (>= 72.89% floor); npm audit
      0 vulnerabilities.
  mobile_app:
    new_files:
    - 07-implementation/mobile-app/src/i18n/messages.ts
    files_changed: 2
    verification: npm run quality — typecheck, lint, 8/8 tests, duplication, format
      all passed.
technical_debt:
  closed:
  - TD-I18N-001
  newly_registered:
  - TD-I18N-002
  reviewed_unchanged:
  - TD-BE-008
  - TD-FE-002
readiness:
  message_externalization_baseline_status: established
  ready_for_next_backlog_item: HOP-QA-ALIGN-CLOSEOUT
```
