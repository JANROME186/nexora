# COM-MOD-011-DEF Validation — Public Website and Digital Growth Capability Package Models

**Status:** passed
**Backlog item:** COM-MOD-011-DEF
**Module:** COM-MOD-011 Public Website and Digital Growth
**Code implemented:** No — this is a definition-only backlog item.

## Scope

Unlike every prior `-DEF` backlog item, COM-MOD-011 introduces **zero new capabilities**. All
7 capabilities in its scope already exist as validated/compiled/modeled capability packages
owned by earlier modules:

| Capability | Owning module | Reused/added |
| --- | --- | --- |
| BCM-SVC-001 Diagnostic Service Catalog | MVP-MOD-002 | `getPublishedServiceSnapshot` reused; `listPublishedServices` added |
| BCM-SVC-002 Test Catalog | MVP-MOD-002 | `getPublishedTestSnapshot` reused; `listPublishedTests` added |
| BCM-SVC-003 Panel Catalog | MVP-MOD-002 | `getPublishedPanelSnapshot` reused; `listPublishedPanels` added |
| BCM-SVC-005 Patient Preparation Management | MVP-MOD-002 | `getPublishedPreparationSnapshot` added (fills a pre-existing schema/operation gap) |
| BCM-ATT-001 Appointment Scheduling | MVP-MOD-004 | `requestAppointment` reused; new RN-008 |
| BCM-ATT-006 Quotation Management | MVP-MOD-004 | `startQuotation` reused; new RN-009 |
| BCM-PLT-005 API Management | MVP-MOD-008 | governance only; new RN-007 |

The task's own execution-prompt guardrail was: *"COM-MOD-011 capabilities are reused from
already-modeled/compiled capability packages; confirm no duplicate model or aggregate is
created before adding any new public-website-specific artifacts."* All 6 consuming capability
packages already carried forward-looking `future_surfaces`/`deferred_to COM-MOD-011` or
`request_only_later` placeholders left by MVP-MOD-002, MVP-MOD-004 and MVP-MOD-008 — this
backlog item realizes those placeholders rather than inventing new ones.

## Key modeling decisions

- **No new capability package, aggregate or schema.** Every new public-facing operation reuses
  an existing published-snapshot schema (catalog capabilities) or an existing
  request/creation operation restricted to a requested/draft state (BCM-ATT-001/006), and
  BCM-ATT-001 reuses BCM-ATT-006's `ProspectiveContact` schema rather than duplicating it.
- **Read surfaces** (BCM-SVC-001/002/003/005) add an anonymous, rate-limited
  `public_surface` classification and a `catalog.*.public_read` scope, distinct from the
  existing internal `catalog.*.read` scope, returning only `status=published` snapshots.
- **Write surfaces** (BCM-ATT-001, BCM-ATT-006) stay staff-gated: the new
  `appointment.request.public`/`quotation.request.public` scopes can only create a
  requested/draft-state record from a `ProspectiveContact`; confirming, issuing, accepting or
  converting still requires the existing internal `.manage` scope.
- **BCM-PLT-005** governs all of the above via `ApiSurfaceRegistration` classification and a
  new `RateLimitPolicy.consumerIdentificationMethod` field (partner API key, IP address or
  session token) — the modeling-stage fix for TD-BE-015.
- Public website pages themselves are explicitly out of scope for these capability packages
  (`generatable: not_applicable`, deferred to COM-MOD-011-WEB-001); each package only models
  the API contract those pages will consume.

## Validations

10 validations executed (VAL-001 through VAL-010), covering: no duplicate capability/
aggregate/schema, YAML syntax across all 35 modified capability-package-model files plus
registries, unchanged BCM-001/BCM-002 traceability, public-surface security modeling
(anonymous, rate-limited, distinct scope, governed by BCM-PLT-005), write-operation
staff-gating, MDPE manual-authoring compliance, business-rule format compliance for the 3 new
rules (RN-007/008/009) with matching test cases, registered path existence, agent-agnostic
scan, and `capability-package-index.yaml` consistency. **All passed; zero blocking gaps.**

## Stale pointers found and corrected

While modeling BCM-ATT-001, BCM-ATT-006 and BCM-PLT-005, three pre-existing stale registry
pointers unrelated to this backlog item's own scope were found and corrected:

- All three packages' `traceability.yaml` had `ui_status`/`validation_status`/`closeout_status`
  stuck at `pending` even though their owning modules (MVP-MOD-004, MVP-MOD-008) had long since
  closed those exact backlog items.
- BCM-ATT-001 and BCM-PLT-005's `capability-package.yaml` still pointed `next_backlog_item` at
  an already-closed COM-MOD-009 item.

Each correction is documented inline in the affected file with an explicit
`*_correction_note`, and each package's `roadmap_group`/`module`/`next_backlog_item` now points
at COM-MOD-011, the current consuming module.

A fourth, unrelated defect was also found: the project `SOURCE_OF_TRUTH.yaml` rules narrative
contained an unescaped colon inside a long plain-scalar sentence ("closed the module: all 13
..."), which a strict YAML parser (PyYAML) reads as an implicit mapping-key separator — the file
was not actually machine-parseable by a strict parser despite reading as plain prose. Fixed by
replacing the colon with a plain dash separator; a full sweep of all 1,110 project YAML files
plus the root/project `PROJECT_STATE.yaml`/`SOURCE_OF_TRUTH.yaml` then passed with 0 failures.

## Debt-first review

TD-BE-015 (rate-limit enforcement scoped to partner-API-key-bearing requests only) was
**materially reduced** (not closed — no code was written). Its own remediation strategy
already named `gradual_before_first_public_api_consumer_onboarding` with target backlog
`COM-MOD-011_or_earlier` — this backlog item registers exactly that first public-consumer set.
BCM-PLT-005 gained RN-007 and a new `RateLimitPolicy.consumerIdentificationMethod` field
modeling how anonymous public traffic is identified for rate-limiting; the runtime counter/
window mechanism remains a COM-MOD-011-BE-001 implementation task.

## Readiness

- COM-MOD-011-DEF: **closed**
- Next backlog item: **COM-MOD-011-BE-001** (Compile public catalog, location and request
  outputs)
- HOP commercially complete / GA-ready: **No** — unchanged
- Coverage baselines unchanged and not regressed (no source code touched): backend 83.73%,
  employee portal 88.24%, mobile 99.21%, patient portal 94.11%, doctor portal 96.28%.
