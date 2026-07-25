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
scan, and `capability-package-index.md` consistency. **All passed; zero blocking gaps.**

## Stale pointers found and corrected

While modeling BCM-ATT-001, BCM-ATT-006 and BCM-PLT-005, three pre-existing stale registry
pointers unrelated to this backlog item's own scope were found and corrected:

- All three packages' `traceability.md` had `ui_status`/`validation_status`/`closeout_status`
  stuck at `pending` even though their owning modules (MVP-MOD-004, MVP-MOD-008) had long since
  closed those exact backlog items.
- BCM-ATT-001 and BCM-PLT-005's `capability-package.md` still pointed `next_backlog_item` at
  an already-closed COM-MOD-009 item.

Each correction is documented inline in the affected file with an explicit
`*_correction_note`, and each package's `roadmap_group`/`module`/`next_backlog_item` now points
at COM-MOD-011, the current consuming module.

A fourth, unrelated defect was also found: the project `SOURCE_OF_TRUTH.md` rules narrative
contained an unescaped colon inside a long plain-scalar sentence ("closed the module: all 13
..."), which a strict YAML parser (PyYAML) reads as an implicit mapping-key separator — the file
was not actually machine-parseable by a strict parser despite reading as plain prose. Fixed by
replacing the colon with a plain dash separator; a full sweep of all 1,110 project YAML files
plus the root/project `PROJECT_STATE.md`/`SOURCE_OF_TRUTH.md` then passed with 0 failures.

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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-COM-MOD-011-DEF-001
  type: qa-validation-evidence
  name: COM-MOD-011-DEF Public Website and Digital Growth Capability Package Validation
  version: 1.0.0
  status: passed
  human_readable: COM-MOD-011-DEF-validation.md
  machine_readable: COM-MOD-011-DEF-validation.md
  created_date: 2026-07-20
  owner: Nexora Product Architecture Team
scope:
  backlog_item: COM-MOD-011-DEF
  module: COM-MOD-011 Public Website and Digital Growth
  release: REL-002
  execution_flow_stage: model
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  code_implemented: false
preflight:
  purpose: Before modeling, validated PROJECT_STATE.md (root and project), SOURCE_OF_TRUTH.md
    and HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md, confirmed the active backlog
    item was COM-MOD-011-DEF, and swept the repository for live (non-historical) pointers
    to COM-MOD-010-CLOSEOUT or COM-MOD-010-QA-001.
  corrections_applied: []
  stale_pointer_sweep_before_modeling:
    method: Repository-wide grep for COM-MOD-010-CLOSEOUT and COM-MOD-010-QA-001 across
      all YAML files.
    result: passed
    detail: All 31 matches were historical evidence references (closed backlog_items
      entries, qa_evidence pointers to closed-item evidence files, technical-debt
      coverage-baseline source_evidence) correctly marked closed/module_closed. No
      live active/current/next pointer targeted COM-MOD-010-CLOSEOUT or COM-MOD-010-QA-001;
      all such fields already named COM-MOD-011/COM-MOD-011-DEF.
  technical_debt_review:
    method: Reviewed 08-qa/technical-debt/technical-debt-index.md in full before
      modeling.
    finding: TD-BE-015 (rate-limit enforcement scoped to partner-API-key-bearing requests
      only) is explicitly tracked with remediation_strategy gradual_before_first_public_api_consumer_onboarding
      and target_backlog COM-MOD-011_or_earlier. BCM-SVC-001/002/003/005 and BCM-ATT-001/006
      are the first real public API consumers, making this the exact trigger. Materially
      reduced (see debt_first_review below) rather than left untouched.
  additional_stale_pointers_found_during_modeling:
  - package: bcm-att-001-appointment-scheduling
    finding: capability-package.md's artifact.roadmap_group/roadmap.module were
      stale at COM-MOD-009 (already closed) with roadmap.package_status stale at "modeled"
      though capability-package-index.md records this capability as compiled under
      the now module_closed MVP-MOD-004 roadmap group. traceability.md's backlog_items
      ui_status/ validation_status/closeout_status were stale at "pending" though
      MVP-MOD-004-FE-001, MVP-MOD-004-QA-001 and MVP-MOD-004-CLOSEOUT are all closed.
    correction: Updated roadmap_group/module/next_backlog_item to COM-MOD-011/COM-MOD-011-BE-001,
      package_status to compiled, and the three stale traceability status fields to
      closed, each with an explicit correction_note.
  - package: bcm-att-006-quotation-management
    finding: capability-package.md's roadmap.package_status was stale at "modeled"
      though this capability is recorded as compiled under the now module_closed MVP-MOD-004
      roadmap group. traceability.md's backlog_items ui_status/validation_status/closeout_status
      were stale at "pending" for the same reason as BCM-ATT-001.
    correction: Updated roadmap_group/module/next_backlog_item to COM-MOD-011/COM-MOD-011-BE-001,
      package_status to compiled, and the three stale traceability status fields to
      closed, each with an explicit correction_note.
  - artifact: SOURCE_OF_TRUTH.md (project)
    finding: 'A pre-existing latent YAML-validity defect was found while editing the
      rules narrative near line 497: "COM-MOD-010-CLOSEOUT closed the module: all
      13 COM-MOD-010 capability packages..." used an unescaped colon-space inside
      an unquoted block-sequence plain scalar. A strict YAML 1.1 parser (PyYAML) reads
      the first colon-space in such a scalar as an implicit mapping-key separator,
      and a later occurrence elsewhere in the same long sentence then raised "mapping
      values are not allowed here" -- the file was not actually machine-parseable
      by a strict parser, though it read as plain prose to a human.'
    correction: 'Replaced the colon with an em-dash-style "--" separator ("closed
      the module -- all 13 ..."). Verified via a full PyYAML sweep of all 1,110 YAML
      files under projects/healthcare-operations-platform/ plus the root and project
      PROJECT_STATE.md/ SOURCE_OF_TRUTH.md: 0 failures after the fix.'
  - artifact: 08-qa/security-quality/security-quality-index.md
    finding: COM-MOD-010-CLOSEOUT's security-quality-evidence.md existed on disk
      but was never registered in the index.
    correction: Backfilled the missing COM-MOD-010-CLOSEOUT entry alongside the new
      COM-MOD-011-DEF entry.
  - package: bcm-plt-005-api-management
    finding: capability-package.md's roadmap.next_backlog_item was stale at COM-MOD-009-BE-001
      (already closed) and roadmap.package_status used the roadmap-group-level value
      "module_closed" rather than a valid capability package_status. traceability.md's
      backlog_items ui_status/validation_status/closeout_status were stale at "pending"
      though MVP-MOD-008-FE-001, MVP-MOD-008-QA-001 and MVP-MOD-008-CLOSEOUT are all
      closed.
    correction: Updated roadmap_group/module/next_backlog_item to COM-MOD-011/COM-MOD-011-BE-001,
      package_status to compiled, and the three stale traceability status fields to
      closed, each with an explicit correction_note.
capabilities_reused:
- capability_id: BCM-SVC-001
  package_folder: 01-product-definition/business-capabilities/packages/bcm-svc-001-diagnostic-service-catalog/
  owning_roadmap_group: MVP-MOD-002
  surface_added: public_website
  new_operations:
  - listPublishedServices
  reused_operations:
  - getPublishedServiceSnapshot
  new_aggregate_or_schema: none
- capability_id: BCM-SVC-002
  package_folder: 01-product-definition/business-capabilities/packages/bcm-svc-002-test-catalog/
  owning_roadmap_group: MVP-MOD-002
  surface_added: public_website
  new_operations:
  - listPublishedTests
  reused_operations:
  - getPublishedTestSnapshot
  new_aggregate_or_schema: none
- capability_id: BCM-SVC-003
  package_folder: 01-product-definition/business-capabilities/packages/bcm-svc-003-panel-catalog/
  owning_roadmap_group: MVP-MOD-002
  surface_added: public_website
  new_operations:
  - listPublishedPanels
  reused_operations:
  - getPublishedPanelSnapshot
  new_aggregate_or_schema: none
- capability_id: BCM-SVC-005
  package_folder: 01-product-definition/business-capabilities/packages/bcm-svc-005-patient-preparation-management/
  owning_roadmap_group: MVP-MOD-002
  surface_added: public_website
  new_operations:
  - getPublishedPreparationSnapshot
  reused_operations: []
  new_aggregate_or_schema: none
  note: New operation fills a pre-existing gap (PublishedPreparationSnapshot schema
    existed with no getter), mirroring BCM-SVC-001/002/003.
- capability_id: BCM-ATT-001
  package_folder: 01-product-definition/business-capabilities/packages/bcm-att-001-appointment-scheduling/
  owning_roadmap_group: MVP-MOD-004
  surface_added: public_website
  new_operations: []
  reused_operations:
  - requestAppointment
  new_business_rule: RN-008
  new_aggregate_or_schema: none
  note: Reuses BCM-ATT-006's ProspectiveContact schema; no duplicate schema defined.
- capability_id: BCM-ATT-006
  package_folder: 01-product-definition/business-capabilities/packages/bcm-att-006-quotation-management/
  owning_roadmap_group: MVP-MOD-004
  surface_added: public_website
  new_operations: []
  reused_operations:
  - startQuotation
  new_business_rule: RN-009
  new_aggregate_or_schema: none
- capability_id: BCM-PLT-005
  package_folder: 01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/
  owning_roadmap_group: MVP-MOD-008
  surface_added: public_website (governance_only, no public screen or endpoint of
    its own)
  new_operations: []
  reused_operations: []
  new_business_rule: RN-007
  new_aggregate_or_schema: none
  note: Extended the existing RateLimitPolicy entity with a consumerIdentificationMethod
    field (partner_api_key | ip_address | session_token); no new aggregate.
validations:
- id: VAL-001
  name: No duplicate capability, aggregate or schema created
  method: Confirm all 7 COM-MOD-011 capabilities already existed as validated/compiled/modeled
    packages before this backlog item, and that every new operation added reuses an
    existing aggregate/schema rather than introducing a new one.
  result: passed
  detail: Zero new capability-package folders were created. BCM-SVC-001/002/003/005
    reuse AGG-006 TestDefinition and their existing Published*Snapshot schemas. BCM-ATT-001/006
    reuse the existing requestAppointment/startQuotation operations and BCM-ATT-006's
    ProspectiveContact schema (not duplicated into BCM-ATT-001). BCM-PLT-005 reuses
    the existing RateLimitPolicy entity, adding one field rather than a new entity.
- id: VAL-002
  name: YAML syntax validation
  method: Parse all created and modified YAML files and fail on syntax errors.
  result: passed
  detail: 35 capability-package-model files modified across 7 packages (capability-package.md,
    openapi-source.md, ui-model.md, permissions.md, traceability.md on all
    7; plus business-rules.md/test-model.md on BCM-ATT-001, BCM-ATT-006 and BCM-PLT-005;
    plus business-model.md on BCM-PLT-005), capability-package-index.md, technical-debt-index.md,
    TD-BE-015's own file, root and project PROJECT_STATE.md/ SOURCE_OF_TRUTH.md,
    and both commercial backlog/execution-prompt files all parsed without errors.
- id: VAL-003
  name: Capability map and dependency map traceability unchanged
  method: Confirm no edit was made to bcm-001/business-capability-map.md or bcm-002/capability-dependency-map.md,
    since all 7 capabilities and their dependency profiles already existed there.
  result: passed
  detail: No changes made to either file; all 7 capabilities' capability_map/dependency_map
    trace blocks were already correct and unchanged.
- id: VAL-004
  name: Public surface security modeling
  method: Confirm every new public_surface/public operation declares anonymous_rate_limited
    security, a public_read or request.public scope distinct from the internal scope,
    and a governed_by pointer to BCM-PLT-005.
  result: passed
  detail: 'All 6 consuming capabilities'' public_surface blocks declare security:
    anonymous_rate_limited and governed_by: BCM-PLT-005 ApiSurfaceRegistration/RateLimitPolicy
    (classification=public). New permissions.md scopes (catalog.*.public_read, appointment.request.public,
    quotation.request.public) are distinct from existing internal scopes and granted
    only to a new anonymous public-website-visitor role.'
- id: VAL-005
  name: Write operations remain staff-gated
  method: Confirm the two write-capable public operations (requestAppointment, startQuotation)
    create only requested/draft-state records and cannot confirm, issue, accept, convert
    or read others' records under the public scope.
  result: passed
  detail: RN-008 (BCM-ATT-001) and RN-009 (BCM-ATT-006) explicitly restrict the public
    scope to creating a requested/draft-state record from a ProspectiveContact; POL-APT-001-03
    and POL-QUO-006-04 explicitly state the public scope can never confirm/issue/accept/convert/read
    others' records.
- id: VAL-006
  name: MDPE manual authoring compliance
  method: Confirm no CRUD, DTO, controller, repository, SDK, Swagger or repetitive
    test was authored as implementation.
  result: passed
  detail: Only editable models (capability-package.md, business-model/-rules, openapi-source,
    ui-model, permissions, test-model, traceability, README) were modified; no implementation
    code was written.
- id: VAL-007
  name: Business rule format compliance
  method: Confirm every new rule (RN-007 BCM-PLT-005, RN-008 BCM-ATT-001, RN-009 BCM-ATT-006)
    follows RN-### format with all required fields and a corresponding test_refs entry.
  result: passed
  detail: All 3 new rules include id, statement, applies_to, enforcement_point, severity,
    audit_required, test_refs, and a matching new test-model.md case (TST-APIM-005-07,
    TST-APT-001-08, TST-QUO-006-09) cross-referenced in traceability.md rules_to_tests.
- id: VAL-008
  name: Registered path existence
  method: Confirm all referenced package folders and index entries resolve to existing
    files.
  result: passed
- id: VAL-009
  name: Agent-agnostic scan
  method: Scan created/modified artifacts for named-agent, assistant, model-vendor
    or platform-runtime requirements.
  result: passed
  detail: No named-agent or vendor-runtime dependency found in any modified artifact.
- id: VAL-010
  name: capability-package-index.md consistency
  method: Confirm the new COM-MOD-011 active_capability_package_groups entry correctly
    marks each capability's package_status as reused (not modeled/compiled fresh)
    and points owning_roadmap_group at its real original module.
  result: passed
  detail: All 7 entries use package_status reused_public_surface_added or reused_governance_extended
    and declare owning_roadmap_group MVP-MOD-002, MVP-MOD-004 or MVP-MOD-008 matching
    their pre-existing completed_capability_package_groups entries exactly.
blocking_gaps: []
non_blocking_observations:
- id: OBS-001
  description: COM-MOD-011-WEB-001 (Compile public website service discovery and conversion
    flows) will be the first HOP delivery unit to actually render public-facing pages;
    no public-website frontend project exists yet under 07-implementation/. This capability-package
    model layer only defines the read/request API contract those pages will consume.
  disposition: tracked_as_documented_package_boundary
  reference: n/a (scope boundary for COM-MOD-011-BE-001/WEB-001, not a modeling defect)
- id: OBS-002
  description: TD-BE-015's acceptance_criteria (public/internal requests actually
    throttled) is not yet met; RN-007 and consumerIdentificationMethod are modeling-stage
    decisions only.
  disposition: tracked_as_documented_package_boundary
  reference: 08-qa/technical-debt/TD-BE-015-rate-limit-enforcement-scoped-to-partner-keys.md
    (status materially_reduced, not closed)
debt_first_review:
  applicable: true
  rationale: 'This is a definition-only backlog item with no code changes, so no code-changing
    debt item could be remediated with actual code. TD-BE-015 was genuinely reasonably
    addressable at the modeling stage: its own remediation_strategy already named
    gradual_before_first_public_api_consumer_onboarding with target_backlog COM-MOD-011_or_earlier,
    and this backlog item registers exactly that first public consumer set. RN-007
    and RateLimitPolicy.consumerIdentificationMethod are a genuine forward modeling-stage
    decision, honestly scoped as such (status materially_reduced, not closed; runtime
    enforcement remains a COM-MOD-011-BE-001 task).'
  debt_items_materially_reduced:
  - TD-BE-015
  debt_items_not_applicable_to_this_backlog_item:
  - reason: All other open/materially_reduced entries in technical-debt-index.md
      concern code, coverage or tooling areas unrelated to the 7 reused capabilities'
      public-surface modeling.
readiness:
  com_mod_011_def_status: closed
  ready_for_next_backlog_item: COM-MOD-011-BE-001
  next_backlog_item_name: Compile public catalog, location and request outputs
  hop_commercially_complete: false
  hop_ga_ready: false
  rationale: 'All 7 COM-MOD-011 capabilities are confirmed reused from already-modeled/compiled
    capability packages with no duplicate model, aggregate or schema created. Each
    package''s product_surfaces, openapi-source.md, ui-model.md, permissions.md
    and traceability.md were extended with a public_website surface realizing pre-existing
    future_surfaces/deferred_to placeholders left by MVP-MOD-002, MVP-MOD-004 and
    MVP-MOD-008. TD-BE-015 was materially reduced through a concrete, honestly-scoped
    modeling-stage decision. Three unrelated stale roadmap/status pointers (BCM-ATT-001,
    BCM-ATT-006, BCM-PLT-005) found during modeling were corrected. No code was implemented
    in this backlog item; backend coverage remains 83.73%, employee-portal 88.24%,
    mobile 99.21%, patient-portal 94.11% and doctor-portal 96.28%, all unchanged and
    not regressed. HOP is not commercially complete or GA-ready; COM-MOD-011-BE-001
    through COM-MOD-011-CLOSEOUT and all subsequent REL-002/003/004 modules remain
    planned.

    '
```
